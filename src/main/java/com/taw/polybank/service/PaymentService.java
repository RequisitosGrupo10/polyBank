package com.taw.polybank.service;

import com.taw.polybank.dao.PaymentRepository;
import com.taw.polybank.dto.CurrencyExchangeDTO;
import com.taw.polybank.dto.PaymentDTO;
import com.taw.polybank.entity.CurrencyExchangeEntity;
import com.taw.polybank.entity.PaymentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    @Autowired
    protected PaymentRepository paymentRepository;

    public void save(PaymentDTO paymentDTO, BeneficiaryService beneficiaryService, CurrencyExchangeService currencyExchangeService, BadgeService badgeService) {
        PaymentEntity payment = this.toEntity(paymentDTO, beneficiaryService, currencyExchangeService, badgeService);
        paymentRepository.save(payment);
        paymentDTO.setId(payment.getId());
    }

    public PaymentEntity toEntity(PaymentDTO paymentDTO, BeneficiaryService beneficiaryService, CurrencyExchangeService currencyExchangeService, BadgeService badgeService) {
        PaymentEntity payment = paymentRepository.findById(paymentDTO.getId()).orElse(new PaymentEntity());
        payment.setId(paymentDTO.getId());
        payment.setAmount(paymentDTO.getAmount());
        payment.setBenficiaryByBenficiaryId(beneficiaryService.toEntity(paymentDTO.getBenficiaryByBenficiaryId()));
        CurrencyExchangeEntity currencyExchangeEntity = paymentDTO.getCurrencyExchangeByCurrencyExchangeId() == null ? null : currencyExchangeService.toEntity(paymentDTO.getCurrencyExchangeByCurrencyExchangeId(), badgeService);
        payment.setCurrencyExchangeByCurrencyExchangeId(currencyExchangeEntity);
        return payment;
    }
}
