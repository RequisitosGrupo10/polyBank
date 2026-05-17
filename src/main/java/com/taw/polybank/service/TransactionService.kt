package com.taw.polybank.service

import com.taw.polybank.controller.atm.TransactionFilterLucia
import com.taw.polybank.dao.BadgeRepository
import com.taw.polybank.dao.BankAccountRepository
import com.taw.polybank.dao.BeneficiaryRepository
import com.taw.polybank.dao.ClientRepository
import com.taw.polybank.dao.CurrencyExchangeRepository
import com.taw.polybank.dao.PaymentRepository
import com.taw.polybank.dao.TransactionRepository
import com.taw.polybank.dto.BadgeDTO
import com.taw.polybank.dto.BankAccountDTO
import com.taw.polybank.dto.ClientDTO
import com.taw.polybank.dto.TransactionDTO
import com.taw.polybank.entity.BadgeEntity
import com.taw.polybank.entity.BankAccountEntity
import com.taw.polybank.entity.BenficiaryEntity
import com.taw.polybank.entity.ClientEntity
import com.taw.polybank.entity.CurrencyExchangeEntity
import com.taw.polybank.entity.PaymentEntity
import com.taw.polybank.entity.TransactionEntity
import com.taw.polybank.ui.transaction.TransactionFilterJose
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.stream.Collectors

/**
 * @author Illya Rozumovskyy 40%
 * @author Lucía Gutiérrez Molina 15%
 * @author José Manuel Sánchez Rico 45%
 */
@Service
class TransactionService {
    @Autowired
    private val transactionRepository: TransactionRepository? = null

    @Autowired
    private val bankAccountRepository: BankAccountRepository? = null

    @Autowired
    private val clientRepository: ClientRepository? = null

    @Autowired
    private val beneficiaryRepository: BeneficiaryRepository? = null

    @Autowired
    private val badgeRepository: BadgeRepository? = null

    @Autowired
    private val currencyExchangeRepository: CurrencyExchangeRepository? = null

    @Autowired
    private val paymentRepository: PaymentRepository? = null

    fun findByBankAccountByBankAccountId(bankAccount: BankAccountDTO): MutableList<TransactionDTO?> {
        val bankAccountEntity =
            bankAccountRepository!!.findByIban(bankAccount.iban).orElse(null)
        val transactionEntities =
            transactionRepository!!.findByBankAccountByBankAccountId(bankAccountEntity)
        return entityListToDTO(transactionEntities)
    }

    fun entityListToDTO(transactionEntityList: MutableList<TransactionEntity>): MutableList<TransactionDTO?> {
        val transactionDTOList: MutableList<TransactionDTO?> = ArrayList<TransactionDTO?>()
        for (transactionEntity in transactionEntityList) {
            transactionDTOList.add(transactionEntity.toDTO())
        }
        return transactionDTOList
    }

    fun filter(
        bankAccount: BankAccountDTO,
        filter: TransactionFilterLucia,
    ): MutableList<TransactionDTO?> {
        val begin = Timestamp.valueOf(filter.timestampBegin!!.toLocalDate().atTime(0, 0))
        val end = Timestamp.valueOf(filter.timestampEnd!!.toLocalDate().atTime(23, 59))

        val clients = clientRepository!!.findByNameOrSurname(filter.transactionOwner)
        val beneficiary =
            beneficiaryRepository!!.findByIban(filter.beneficiaryIban).orElse(null)

        val bankAccountEntity =
            bankAccountRepository!!.findByIban(bankAccount.iban).orElse(null)
        val transactions: MutableList<TransactionEntity>

        if (filter.transactionOwner!!.isBlank() || filter.beneficiaryIban!!.isBlank()) {
            if (filter.transactionOwner!!.isBlank()) {
                if (filter.beneficiaryIban!!.isBlank()) {
                    transactions =
                        transactionRepository!!.filterByBankAccount_TimestampRange_Amount(
                            bankAccountEntity,
                            begin,
                            end,
                            filter.amount!!,
                        )
                } else {
                    transactions =
                        transactionRepository!!.filterByBankAccount_TimestampRange_beneficiaryIBAN_Amount(
                            bankAccountEntity,
                            begin,
                            end,
                            beneficiary,
                            filter.amount!!,
                        )
                }
            } else {
                transactions =
                    transactionRepository!!.filterByBankAccount_TimestampRange_TransactionOwner_Amount(
                        bankAccountEntity,
                        begin,
                        end,
                        clients,
                        filter.amount!!,
                    )
            }
        } else {
            transactions =
                transactionRepository!!
                    .filterByBankAccount_TimestampRange_TransactionOwner_beneficiaryIBAN_Amount(
                        bankAccountEntity,
                        begin,
                        end,
                        clients,
                        beneficiary,
                        filter.amount!!,
                    )
        }
        return entityListToDTO(transactions)
    }

    fun makeTransaction(
        amount: Double,
        ibanReceptor: String,
        nameReceptor: String?,
        finalBadgeDTO: BadgeDTO,
        emisorBadgeDTO: BadgeDTO,
        bankAccountEmisorDTO: BankAccountDTO,
        clientDTO: ClientDTO,
    ) {
        val finalBadge = badgeRepository!!.findById(finalBadgeDTO.getId()).orElse(null)

        val client = clientRepository!!.findByDNI(clientDTO.getDni())

        val beneficiary =
            getOrMakeBeneficiaryEntity(ibanReceptor, nameReceptor, finalBadge!!)

        // Make (if neccessary) a currency exchange
        var currencyExchange: CurrencyExchangeEntity? = null
        var finalAmount = amount
        val emisorBadge = badgeRepository.findById(emisorBadgeDTO.getId()).orElse(null)
        if (emisorBadge!!.getId() != finalBadge.getId()) {
            // There exists a currency exchange
            currencyExchange = CurrencyExchangeEntity()
            currencyExchange.setBadgeByFinalBadgeId(finalBadge)
            currencyExchange.setBadgeByInitialBadgeId(emisorBadge)
            currencyExchange.setInitialAmount(amount)
            finalAmount = amount * emisorBadge.getValue() / finalBadge.getValue()
            currencyExchange.setFinalAmount(finalAmount)

            currencyExchangeRepository!!.save<CurrencyExchangeEntity>(currencyExchange)
        }

        // Update the bank account(s)
        val bankAccountEmisor =
            bankAccountRepository!!.findByIban(bankAccountEmisorDTO.iban).orElse(null)
        updateBankAccounts(
            amount,
            ibanReceptor,
            finalBadge,
            finalAmount,
            emisorBadge,
            bankAccountEmisor!!,
        )

        // Make the payment
        val payment = makePayment(amount, beneficiary, currencyExchange)

        // Make the transaction
        makeTransaction(payment, client, bankAccountEmisor)
    }

    private fun makeTransaction(
        payment: PaymentEntity?,
        client: ClientEntity?,
        bankAccount: BankAccountEntity?,
    ) {
        val transaction = TransactionEntity()
        transaction.setBankAccountByBankAccountId(bankAccount)
        transaction.setClientByClientId(client)
        transaction.setPaymentByPaymentId(payment)
        transaction.setTimestamp(Timestamp.valueOf(LocalDateTime.now()))
        transactionRepository!!.save<TransactionEntity>(transaction)
    }

    private fun makePayment(
        amount: Double,
        beneficiary: BenficiaryEntity?,
        currencyExchange: CurrencyExchangeEntity?,
    ): PaymentEntity {
        val payment = PaymentEntity()
        payment.setAmount(amount)
        if (currencyExchange != null) {
            payment.setCurrencyExchangeByCurrencyExchangeId(currencyExchange)
        }
        payment.setBenficiaryByBenficiaryId(beneficiary)
        paymentRepository!!.save<PaymentEntity>(payment)
        return payment
    }

    private fun updateBankAccounts(
        amount: Double,
        ibanReceptor: String,
        finalBadge: BadgeEntity,
        finalAmount: Double,
        emisorBadge: BadgeEntity,
        bankAccountEmisor: BankAccountEntity,
    ) {
        if (ibanReceptor != bankAccountEmisor.getIban()) {
            // Es una transferencia, se actualiza el dinero de ambas partes
            val bankAccountReceptor =
                bankAccountRepository!!.findByIban(ibanReceptor).orElse(null)
            if (bankAccountReceptor != null) {
                bankAccountReceptor.setBalance(bankAccountReceptor.getBalance() + finalAmount)
                bankAccountRepository.save<BankAccountEntity>(bankAccountReceptor)
            }
            bankAccountEmisor.setBalance(bankAccountEmisor.getBalance() - amount)
        } else {
            // Está sacando dinero (ya sea de la misma o distinta moneda), por lo que hay que sacar el
            // dinero final de la cuenta bancaria.
            bankAccountEmisor.setBalance(
                bankAccountEmisor.getBalance() -
                    (amount * finalBadge.getValue() / emisorBadge.getValue()),
            )
        }
        bankAccountRepository!!.save<BankAccountEntity>(bankAccountEmisor)
    }

    private fun getOrMakeBeneficiaryEntity(
        ibanReceptor: String?,
        nameReceptor: String?,
        finalBadge: BadgeEntity,
    ): BenficiaryEntity {
        // Find or make the Beneficiary
        var beneficiary = beneficiaryRepository!!.findByIban(ibanReceptor).orElse(null)
        if (beneficiary == null) {
            beneficiary = BenficiaryEntity()
            beneficiary.setName(nameReceptor)
            beneficiary.setIban(ibanReceptor)
            beneficiary.setBadge(finalBadge.getName())
            beneficiary.setSwift("")

            beneficiaryRepository.save<BenficiaryEntity>(beneficiary)
        }
        return beneficiary
    }

    fun save(
        transactionDTO: TransactionDTO,
        clientService: ClientService,
        bankAccountService: BankAccountService,
        currencyExchangeService: CurrencyExchangeService,
        paymentService: PaymentService,
        badgeService: BadgeService,
        beneficiaryService: BeneficiaryService,
    ) {
        val transaction =
            this.toEntity(
                transactionDTO,
                clientService,
                bankAccountService,
                currencyExchangeService,
                paymentService,
                badgeService,
                beneficiaryService,
            )
        transactionRepository!!.save<TransactionEntity>(transaction)
        transactionDTO.setId(transaction.getId())
    }

    private fun toEntity(
        transactionDTO: TransactionDTO,
        clientService: ClientService,
        bankAccountService: BankAccountService,
        currencyExchangeService: CurrencyExchangeService,
        paymentService: PaymentService,
        badgeService: BadgeService,
        beneficiaryService: BeneficiaryService,
    ): TransactionEntity {
        val transaction =
            transactionRepository!!.findById(transactionDTO.getId()).orElse(TransactionEntity())
        transaction.setId(transactionDTO.getId())
        transaction.setTimestamp(transactionDTO.getTimestamp())
        transaction.setClientByClientId(clientService.toEntidy(transactionDTO.getClientByClientId()))
        transaction.setBankAccountByBankAccountId(
            bankAccountService.toEntity(
                transactionDTO.getBankAccountByBankAccountId(),
                clientService,
                badgeService,
            ),
        )
        val currencyExchangeEntity =
            if (transactionDTO.getCurrencyExchangeByCurrencyExchangeId() == null) {
                null
            } else {
                currencyExchangeService.toEntity(
                    transactionDTO.getCurrencyExchangeByCurrencyExchangeId(),
                    badgeService,
                )
            }
        transaction.setCurrencyExchangeByCurrencyExchangeId(currencyExchangeEntity)
        transaction.setPaymentByPaymentId(
            paymentService.toEntity(
                transactionDTO.getPaymentByPaymentId(),
                beneficiaryService,
                currencyExchangeService,
                badgeService,
            ),
        )
        return transaction
    }

    fun findTransactionsByBankAccountByBankAccountIdId(bankId: Int): MutableList<TransactionDTO?> =
        transactionRepository!!
            .findTransactionEntitiesByBankAccountByBankAccountIdId(bankId)
            .stream()
            .map<TransactionDTO?> { transaction: TransactionEntity? -> transaction!!.toDTO() }
            .collect(Collectors.toList())

    fun findAllTransactionsByBankAccountAndDatesAndSendAmountInRange(
        bankId: Int,
        dateAfter: Timestamp?,
        dateBefore: Timestamp?,
        minAmount: Double,
        maxAmount: Double,
    ): MutableList<TransactionDTO?> =
        transactionRepository!!
            .findAllTransactionsByBankAccountAndDatesAndSendAmountInRange(
                bankId,
                dateAfter,
                dateBefore,
                minAmount,
                maxAmount,
            ).stream()
            .map<TransactionDTO?> { transaction: TransactionEntity? -> transaction!!.toDTO() }
            .collect(Collectors.toList())

    fun findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenSenderDni(
        bankId: Int,
        dateAfter: Timestamp?,
        dateBefore: Timestamp?,
        minAmount: Double,
        maxAmount: Double,
        senderId: String?,
    ): MutableList<TransactionDTO?> =
        transactionRepository!!
            .findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenSenderDni(
                bankId,
                dateAfter,
                dateBefore,
                minAmount,
                maxAmount,
                senderId,
            ).stream()
            .map<TransactionDTO?> { transaction: TransactionEntity? -> transaction!!.toDTO() }
            .collect(Collectors.toList())

    fun findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenRecipientName(
        bankId: Int,
        dateAfter: Timestamp?,
        dateBefore: Timestamp?,
        minAmount: Double,
        maxAmount: Double,
        recipientName: String?,
    ): MutableList<TransactionDTO?> =
        transactionRepository!!
            .findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenRecipientName(
                bankId,
                dateAfter,
                dateBefore,
                minAmount,
                maxAmount,
                recipientName,
            ).stream()
            .map<TransactionDTO?> { transaction: TransactionEntity? -> transaction!!.toDTO() }
            .collect(Collectors.toList())

    fun findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenSenderDniAndRecipientName(
        bankId: Int,
        dateAfter: Timestamp?,
        dateBefore: Timestamp?,
        minAmount: Double,
        maxAmount: Double,
        senderId: String?,
        recipientName: String?,
    ): MutableList<TransactionDTO?> =
        transactionRepository!!
            .findAllTransactionsByBankAccountAndDatesAndSendAmountInRangeWithGivenSenderDniAndRecipientName(
                bankId,
                dateAfter,
                dateBefore,
                minAmount,
                maxAmount,
                senderId,
                recipientName,
            ).stream()
            .map<TransactionDTO?> { transaction: TransactionEntity? -> transaction!!.toDTO() }
            .collect(Collectors.toList())

    fun findByClientIdAndFilter(
        id: Int,
        filter: TransactionFilterJose,
    ): MutableList<TransactionDTO?> {
        if (filter.getType() == null || filter.getType().isBlank()) {
            return findByClientIdAndMinimumAmount(
                id,
                filter.getAmount(),
            )
        }
        if (filter.getType() == "Payment") {
            return getPaymentByClientIdDTOS(id, filter)
        } else if (filter.getType() == "CurrencyExchange") {
            return getCurrencyExchangeByClientIdDTOS(id, filter)
        } else {
            return getAnyTransactionByClientIdDTOS(id, filter)
        }
    }

    fun findByBankIdAndFilter(
        id: Int,
        filter: TransactionFilterJose,
    ): MutableList<TransactionDTO?> {
        if (filter.getType() == null ||
            filter
                .getType()
                .isBlank()
        ) {
            return getDtoList(transactionRepository!!.findByBankIdAndMinimumAmount(id, filter.getAmount()))
        }
        if (filter.getType() == "Payment") {
            return getPaymentByBankIdDTOS(id, filter)
        } else if (filter.getType() == "CurrencyExchange") {
            return getCurrencyExchangeByBankIdDTOS(id, filter)
        } else {
            return getAnyTransactionByClientIdDTOS(id, filter)
        }
    }

    private fun getAnyTransactionByClientIdDTOS(
        id: Int,
        filter: TransactionFilterJose,
    ): MutableList<TransactionDTO?> {
        if (filter.getSorting() == null || filter.getSorting().isBlank()) {
            return findByClientId(id)
        } else if (filter.getSorting() == "timestamp_asc") {
            return findByClientIdSortByTimestampAsc(id)
        } else if (filter.getSorting() == "timestamp_desc") {
            return findByClientIdSortByTimestampDesc(id)
        } else {
            return findByClientId(id)
        }
    }

    private fun getCurrencyExchangeByClientIdDTOS(
        id: Int?,
        filter: TransactionFilterJose,
    ): MutableList<TransactionDTO?> {
        if (filter.getSorting() == null || filter.getSorting().isBlank()) {
            return findCurrencyExchangesByClientIdAndMinimumAmount(id, filter.getAmount())
        } else if (filter.getSorting() == "timestamp_asc") {
            return findCurrencyExchangesByClientIdAndMinimumAmountSortByTimestampAsc(
                id,
                filter.getAmount(),
            )
        } else if (filter.getSorting() == "timestamp_desc") {
            return findCurrencyExchangesByClientIdAndMinimumAmountSortByTimestampDesc(
                id,
                filter.getAmount(),
            )
        } else {
            return findCurrencyExchangesByClientIdAndMinimumAmount(id, filter.getAmount())
        }
    }

    private fun getPaymentByClientIdDTOS(
        id: Int,
        filter: TransactionFilterJose,
    ): MutableList<TransactionDTO?> {
        if (filter.getSorting() == null || filter.getSorting().isBlank()) {
            return findPaymentsByClientIdAndMinimumAmount(id, filter.getAmount())
        } else if (filter.getSorting() == "timestamp_asc") {
            return findPaymentsByClientIdAndMinimumAmountSortByTimestampAsc(id, filter.getAmount())
        } else if (filter.getSorting() == "timestamp_desc") {
            return findPaymentsByClientIdAndMinimumAmountSortByTimestampDesc(id, filter.getAmount())
        } else {
            return findPaymentsByClientIdAndMinimumAmount(id, filter.getAmount())
        }
    }

    private fun getAnyTransactionByBankIdDTOS(
        id: Int,
        filter: TransactionFilterJose,
    ): MutableList<TransactionDTO?> {
        if (filter.getSorting() == null || filter.getSorting().isBlank()) {
            return findByBankId(id)
        } else if (filter.getSorting() == "timestamp_asc") {
            return findByBankIdSortByTimestampAsc(id)
        } else if (filter.getSorting() == "timestamp_desc") {
            return findByBankIdSortByTimestampDesc(id)
        } else {
            return findByBankId(id)
        }
    }

    private fun findByBankIdSortByTimestampDesc(id: Int?): MutableList<TransactionDTO?> =
        getDtoList(transactionRepository!!.findByBankIdSortByTimestampDesc(id))

    private fun findByBankIdSortByTimestampAsc(id: Int?): MutableList<TransactionDTO?> =
        getDtoList(transactionRepository!!.findByBankIdSortByTimestampAsc(id))

    private fun getCurrencyExchangeByBankIdDTOS(
        id: Int?,
        filter: TransactionFilterJose,
    ): MutableList<TransactionDTO?> {
        if (filter.getSorting() == null || filter.getSorting().isBlank()) {
            return findCurrencyExchangesByBankIdAndMinimumAmount(id, filter.getAmount())
        } else if (filter.getSorting() == "timestamp_asc") {
            return findCurrencyExchangesByBankIdAndMinimumAmountSortByTimestampAsc(
                id,
                filter.getAmount(),
            )
        } else if (filter.getSorting() == "timestamp_desc") {
            return findCurrencyExchangesByBankIdAndMinimumAmountSortByTimestampDesc(
                id,
                filter.getAmount(),
            )
        } else {
            return findCurrencyExchangesByBankIdAndMinimumAmount(id, filter.getAmount())
        }
    }

    private fun findCurrencyExchangesByBankIdAndMinimumAmountSortByTimestampAsc(
        id: Int?,
        amount: Double,
    ): MutableList<TransactionDTO?> =
        getDtoList(
            transactionRepository!!.findCurrencyExchangesByBankIdAndMinimumAmountSortByTimestampAsc(
                id,
                amount,
            ),
        )

    private fun findCurrencyExchangesByBankIdAndMinimumAmountSortByTimestampDesc(
        id: Int?,
        amount: Double,
    ): MutableList<TransactionDTO?> =
        getDtoList(
            transactionRepository!!.findCurrencyExchangesByBankIdAndMinimumAmountSortByTimestampDesc(
                id,
                amount,
            ),
        )

    private fun findCurrencyExchangesByBankIdAndMinimumAmount(
        id: Int?,
        amount: Double,
    ): MutableList<TransactionDTO?> =
        getDtoList(
            transactionRepository!!.findCurrencyExchangesByBankIdAndMinimumAmount(id, amount),
        )

    private fun getPaymentByBankIdDTOS(
        id: Int?,
        filter: TransactionFilterJose,
    ): MutableList<TransactionDTO?> {
        if (filter.getSorting() == null || filter.getSorting().isBlank()) {
            return findPaymentsByBankIdAndMinimumAmount(id, filter.getAmount())
        } else if (filter.getSorting() == "timestamp_asc") {
            return findPaymentsByBankIdAndMinimumAmountSortByTimestampAsc(id, filter.getAmount())
        } else if (filter.getSorting() == "timestamp_desc") {
            return findPaymentsByBankIdAndMinimumAmountSortByTimestampDesc(id, filter.getAmount())
        } else {
            return findPaymentsByBankIdAndMinimumAmount(id, filter.getAmount())
        }
    }

    private fun findPaymentsByBankIdAndMinimumAmountSortByTimestampAsc(
        id: Int?,
        amount: Double,
    ): MutableList<TransactionDTO?> =
        getDtoList(
            transactionRepository!!.findPaymentsByBankIdAndMinimumAmountSortByTimestampAsc(id, amount),
        )

    private fun findPaymentsByBankIdAndMinimumAmountSortByTimestampDesc(
        id: Int?,
        amount: Double,
    ): MutableList<TransactionDTO?> =
        getDtoList(
            transactionRepository!!.findPaymentsByBankIdAndMinimumAmountSortByTimestampDesc(id, amount),
        )

    private fun findPaymentsByBankIdAndMinimumAmount(
        id: Int?,
        amount: Double,
    ): MutableList<TransactionDTO?> = getDtoList(transactionRepository!!.findPaymentsByBankIdAndMinimumAmount(id, amount))

    private fun findByClientIdSortByTimestampDesc(id: Int?): MutableList<TransactionDTO?> =
        getDtoList(transactionRepository!!.findByClientIdSortByTimestampDesc(id))

    private fun findByClientIdSortByTimestampAsc(id: Int?): MutableList<TransactionDTO?> =
        getDtoList(transactionRepository!!.findByClientIdSortByTimestampAsc(id))

    private fun findCurrencyExchangesByClientIdAndMinimumAmountSortByTimestampDesc(
        id: Int?,
        amount: Double,
    ): MutableList<TransactionDTO?> =
        getDtoList(
            transactionRepository!!.findCurrencyExchangesByClientIdAndMinimumAmountSortByTimestampDesc(
                id,
                amount,
            ),
        )

    private fun findCurrencyExchangesByClientIdAndMinimumAmountSortByTimestampAsc(
        id: Int?,
        amount: Double,
    ): MutableList<TransactionDTO?> =
        getDtoList(
            transactionRepository!!.findCurrencyExchangesByClientIdAndMinimumAmountSortByTimestampAsc(
                id,
                amount,
            ),
        )

    private fun findPaymentsByClientIdAndMinimumAmountSortByTimestampAsc(
        id: Int?,
        amount: Double,
    ): MutableList<TransactionDTO?> =
        getDtoList(
            transactionRepository!!.findPaymentsByClientIdAndMinimumAmountSortByTimestampAsc(id, amount),
        )

    fun findByClientId(id: Int): MutableList<TransactionDTO?> = getDtoList(transactionRepository!!.findByClientId(id))

    private fun findCurrencyExchangesByClientIdAndMinimumAmount(
        id: Int?,
        amount: Double,
    ): MutableList<TransactionDTO?> =
        getDtoList(
            transactionRepository!!.findCurrencyExchangesByClientIdAndMinimumAmount(id, amount),
        )

    private fun findPaymentsByClientIdAndMinimumAmount(
        id: Int?,
        amount: Double,
    ): MutableList<TransactionDTO?> = getDtoList(transactionRepository!!.findPaymentsByClientIdAndMinimumAmount(id, amount))

    fun findByClientIdAndMinimumAmount(
        id: Int?,
        amount: Double,
    ): MutableList<TransactionDTO?> = getDtoList(transactionRepository!!.findByClientIdAndMinimumAmount(id, amount))

    private fun getDtoList(transactionEntityList: MutableList<TransactionEntity>): MutableList<TransactionDTO?> {
        val transactionDTOS: MutableList<TransactionDTO?> = ArrayList<TransactionDTO?>()
        for (transactionEntity in transactionEntityList) {
            transactionDTOS.add(transactionEntity.toDTO())
        }
        return transactionDTOS
    }

    private fun findPaymentsByClientIdAndMinimumAmountSortByTimestampDesc(
        id: Int,
        amount: Double,
    ): MutableList<TransactionDTO?> =
        getDtoList(
            transactionRepository!!.findPaymentsByClientIdAndMinimumAmountSortByTimestampDesc(
                id,
                amount,
            ),
        )

    fun findByBankId(id: Int): MutableList<TransactionDTO?> = getDtoList(transactionRepository!!.findByBankAccountId(id))

    fun updateBankAccount(bankAccountEmisor: BankAccountDTO): BankAccountDTO? {
        val bankAccountEntity =
            bankAccountRepository!!.findByIban(bankAccountEmisor.iban).orElse(null)
        return if (bankAccountEntity == null) null else bankAccountEntity.toDTO()
    }
}
