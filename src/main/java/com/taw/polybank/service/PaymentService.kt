package com.taw.polybank.service

import com.taw.polybank.dao.PaymentRepository
import com.taw.polybank.dto.PaymentDTO
import com.taw.polybank.entity.PaymentEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @author Illya Rozumovskyy
 */
@Service
class PaymentService(
  private val paymentRepository: PaymentRepository,
) {
  fun save(
    paymentDTO: PaymentDTO,
    beneficiaryService: BeneficiaryService,
    currencyExchangeService: CurrencyExchangeService,
    badgeService: BadgeService,
  ) {
    val payment =
      this.toEntity(paymentDTO, beneficiaryService, currencyExchangeService, badgeService)
    paymentRepository.save<PaymentEntity>(payment)
    paymentDTO.setId(payment.getId())
  }

  fun toEntity(
    paymentDTO: PaymentDTO,
    beneficiaryService: BeneficiaryService,
    currencyExchangeService: CurrencyExchangeService,
    badgeService: BadgeService,
  ): PaymentEntity {
    val payment =
      paymentRepository.findById(paymentDTO.getId()).orElse(PaymentEntity())
    payment.setId(paymentDTO.getId())
    payment.setAmount(paymentDTO.getAmount())
    payment.setBenficiaryByBenficiaryId(
      beneficiaryService.toEntity(paymentDTO.getBenficiaryByBenficiaryId()),
    )
    val currencyExchangeEntity =
      if (paymentDTO.getCurrencyExchangeByCurrencyExchangeId() == null) {
        null
      } else {
        currencyExchangeService.toEntity(
          paymentDTO.getCurrencyExchangeByCurrencyExchangeId(),
          badgeService,
        )
      }
    payment.setCurrencyExchangeByCurrencyExchangeId(currencyExchangeEntity)
    return payment
  }
}
