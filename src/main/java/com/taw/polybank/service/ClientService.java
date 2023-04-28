package com.taw.polybank.service;

import com.taw.polybank.dao.ClientRepository;
import com.taw.polybank.dto.BankAccountDTO;
import com.taw.polybank.dto.ClientDTO;
import com.taw.polybank.dto.TransactionDTO;
import com.taw.polybank.entity.ClientEntity;
import com.taw.polybank.ui.client.ClientFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;

    public List<ClientDTO> findAll(){
        List <ClientEntity> clientEntityList = clientRepository.findAll();
        List<ClientDTO> clientDTOList = getClientDTOS(clientEntityList);
        return clientDTOList;
    }

    private static List<ClientDTO> getClientDTOS(List<ClientEntity> clientEntityList) {
        List<ClientDTO> clientDTOList = new ArrayList<>();
        for (ClientEntity client: clientEntityList) {
            clientDTOList.add(client.toDTO());
        }
        return clientDTOList;
    }

    public Optional<ClientDTO> findById(Integer id) {
        Optional<ClientEntity> clientEntityOptional = clientRepository.findById(id);
        Optional<ClientDTO> clientDTOOptional;
        if (clientEntityOptional.isPresent())
            clientDTOOptional = Optional.of(clientEntityOptional.get().toDTO());
        else
            clientDTOOptional = Optional.of(null);
        return clientDTOOptional;
    }

    public List<ClientDTO> findByFilter(ClientFilter filter) {
        if (filter == null)
            return this.findAll();
        if ((filter.getDNI() == null || filter.getDNI().isBlank())
                && (filter.getName() == null || filter.getName().isBlank()))
            return this.findAll();
        if (filter.getDNI() != null && !filter.getDNI().isBlank()) {
            List<ClientDTO> clientDTOS = new ArrayList<>();
            clientDTOS.add(clientRepository.findByDNI(filter.getDNI()).toDTO());
            return clientDTOS;
        }
        if (filter.getName() != null && !filter.getName().isBlank()){
            return getClientDTOS(clientRepository.findByNameOrSurname(filter.getName()));
        }
        return Collections.emptyList();
    }
}
