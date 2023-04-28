package com.taw.polybank.service;

import com.taw.polybank.dao.BeneficiaryRepository;
import com.taw.polybank.dto.BenficiaryDTO;
import com.taw.polybank.entity.BenficiaryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BeneficiaryService {

    @Autowired
    protected BeneficiaryRepository beneficiaryRepository;

    public BenficiaryDTO findBenficiaryByNameAndIban(String beneficiaryName, String iban) {
        BenficiaryEntity beneficiary = beneficiaryRepository.findBenficiaryEntityByNameAndIban(beneficiaryName, iban);
        return beneficiary == null ? null : beneficiary.toDTO();
    }

    public BenficiaryEntity toEntity(BenficiaryDTO benficiaryDTO) {
        BenficiaryEntity benficiary = beneficiaryRepository.findById(benficiaryDTO.getId()).orElse(new BenficiaryEntity());
        benficiary.setId(benficiaryDTO.getId());
        benficiary.setName(benficiaryDTO.getName());
        benficiary.setBadge(benficiaryDTO.getBadge());
        benficiary.setIban(benficiaryDTO.getIban());
        benficiary.setSwift(benficiaryDTO.getSwift());
        return benficiary;
    }

    public void save(BenficiaryDTO beneficiaryDTO) {
        BenficiaryEntity benficiary = this.toEntity(beneficiaryDTO);
        beneficiaryRepository.save(benficiary);
        beneficiaryDTO.setId(benficiary.getId());
    }
}
