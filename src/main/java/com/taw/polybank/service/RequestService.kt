package com.taw.polybank.service

import com.taw.polybank.dao.BankAccountRepository
import com.taw.polybank.dao.ClientRepository
import com.taw.polybank.dao.EmployeeRepository
import com.taw.polybank.dao.RequestRepository
import com.taw.polybank.dto.BankAccountDTO
import com.taw.polybank.dto.ClientDTO
import com.taw.polybank.dto.RequestDTO
import com.taw.polybank.entity.EmployeeEntity
import com.taw.polybank.entity.RequestEntity
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.stream.Collectors

/**
 * @author Illya Rozumovskyy 50%
 * @author Lucía Gutiérrez Molina 50%
 */
@Service
@RequiredArgsConstructor
class RequestService(
    private val requestRepository: RequestRepository,
    private val bankAccountRepository: BankAccountRepository,
    private val employeeRepository: EmployeeRepository,
    private val clientRepository: ClientRepository,
) {
    fun findByBankAccountByBankAccountIdAndAndSolved(
        bankAccount: BankAccountDTO,
        b: Boolean,
    ): MutableList<RequestDTO?> {
        val bankAccountEntity =
            bankAccountRepository.findByIban(bankAccount.iban).orElse(null)
        val requestEntityList =
            requestRepository.findByBankAccountByBankAccountIdAndSolved(bankAccountEntity, b)
        return entityListToDTO(requestEntityList)
    }

    fun entityListToDTO(requestEntityList: MutableList<RequestEntity>): MutableList<RequestDTO?> {
        val requestDTOList: MutableList<RequestDTO?> = ArrayList<RequestDTO?>()
        for (requestEntity in requestEntityList) {
            requestDTOList.add(requestEntity.toDTO())
        }
        return requestDTOList
    }

    fun createNewRequest(
        client: ClientDTO,
        bankAccount: BankAccountDTO,
        activation: RequestEntity.RequestType?,
        description: String?,
    ) {
        val employees =
            employeeRepository.findEmployeeWithMinimmumRequests(EmployeeEntity.EmployeeType.MANAGER)
        val clientEntity = clientRepository.findByDNI(client.dni)
        val bankAccountEntity =
            bankAccountRepository.findByIban(bankAccount.iban).orElse(null)

        val request = RequestEntity()
        request.setClientByClientId(clientEntity)
        request.setBankAccountByBankAccountId(bankAccountEntity)
        request.setEmployeeByEmployeeId(employees.get(0))
        request.setSolved(false)
        request.setTimestamp(Timestamp.valueOf(LocalDateTime.now()))
        request.setType(activation)
        request.setDescription(if (description == null) "" else description)

        requestRepository.save<RequestEntity>(request)
    }

    fun toEntity(request: RequestDTO): RequestEntity {
        val requestEntity =
            requestRepository.findById(request.getId()).orElse(RequestEntity())
        requestEntity.setId(request.getId())
        requestEntity.setSolved(request.isSolved())
        requestEntity.setTimestamp(request.getTimestamp())
        requestEntity.setType(request.getType())
        requestEntity.setDescription(request.getDescription())
        requestEntity.setApproved(request.isApproved())
        return requestEntity
    }

    fun save(
        requestDTO: RequestDTO,
        clientService: ClientService,
        bankAccountService: BankAccountService,
        employeeService: EmployeeService,
        badgeService: BadgeService,
    ) {
        val request = this.toEntity(requestDTO)

        request.setClientByClientId(clientService.toEntidy(requestDTO.getClientByClientId()))
        request.setBankAccountByBankAccountId(
            bankAccountService.toEntity(
                requestDTO.getBankAccountByBankAccountId(),
                clientService,
                badgeService,
            ),
        )
        request.setEmployeeByEmployeeId(employeeService.toEntity(requestDTO.getEmployeeByEmployeeId()))

        requestRepository.save<RequestEntity>(request)
        requestDTO.setId(request.getId())
    }

    fun findUnsolvedUnblockRequestByUserId(
        clientId: Int,
        bankAccountId: Int,
    ): MutableList<RequestDTO?> {
        val requestEntities =
            requestRepository.findUnsolvedUnblockRequestByUserId(
                clientId,
                bankAccountId,
                RequestEntity.RequestType.ACTIVATION,
            )
        val requestDTOS =
            requestEntities.stream().map { req: RequestEntity? -> req!!.toDTO() }.collect(Collectors.toList())
        return requestDTOS
    }
}
