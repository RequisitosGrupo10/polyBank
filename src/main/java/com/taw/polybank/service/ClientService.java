package com.taw.polybank.service;

import com.taw.polybank.dao.ClientRepository;
import com.taw.polybank.dto.ClientDTO;
import com.taw.polybank.entity.ClientEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;

    public List<ClientDTO> findAll(){
        List <ClientEntity> clientEntityList= clientRepository.findAll();
        List<ClientDTO> clientDTOList = new ArrayList<>();
        for (ClientEntity client: clientEntityList) {
            clientDTOList.add(client.toDTO());
        }
        return clientDTOList;
    }
    
    public ClientDTO findById(Integer clientId) {
        ClientEntity clientEntity = this.clientRepository.findById(clientId).orElse(null);

        if (clientEntity != null) {
            return clientEntity.toDTO();
        }

        return null;
    }
}
