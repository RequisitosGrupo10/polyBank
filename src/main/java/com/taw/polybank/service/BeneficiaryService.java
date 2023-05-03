package com.taw.polybank.service;

import com.taw.polybank.dao.BankAccountRepository;
import com.taw.polybank.dao.BeneficiaryRepository;
import com.taw.polybank.dao.ClientRepository;
import com.taw.polybank.dto.BenficiaryDTO;
import com.taw.polybank.dto.ClientDTO;
import com.taw.polybank.entity.BankAccountEntity;
import com.taw.polybank.entity.BenficiaryEntity;
import com.taw.polybank.entity.ClientEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BeneficiaryService {

    @Autowired
    protected BeneficiaryRepository beneficiaryRepository;

    @Autowired
    protected BankAccountRepository bankAccountRepository;

    @Autowired
    protected ClientRepository clientRepository;

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

    public void guardarBeneficiarios(ClientDTO client) {
        //Selecciono las cuentas del cliente
        ClientEntity clientEntity = clientRepository.findByDNI(client.getDni());
        List<BankAccountEntity> bankAccountEntityList = bankAccountRepository.findByClientByClientId(clientEntity);
        for(BankAccountEntity bankAccountEntity : bankAccountEntityList){
            //Por cada cuenta, si encuentro un beneficiario entonces le actualizo el nombre
            BenficiaryEntity benficiaryEntity = beneficiaryRepository.findByIban(bankAccountEntity.getIban()).orElse(null);
            if(benficiaryEntity != null){
                benficiaryEntity.setName(client.getName());
                beneficiaryRepository.save(benficiaryEntity);
            }
        }

    }
}
