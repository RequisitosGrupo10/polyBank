package com.taw.polybank.service;

import com.taw.polybank.dao.ClientRepository;
import com.taw.polybank.dto.ClientDTO;
import com.taw.polybank.entity.BankAccountEntity;
import com.taw.polybank.entity.ClientEntity;
import com.taw.polybank.entity.RequestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public Optional<ClientDTO> findById(Integer id) {
        Optional<ClientEntity> clientEntityOptional = clientRepository.findById(id);
        Optional<ClientDTO> clientDTOOptional;
        if (clientEntityOptional.isPresent())
            clientDTOOptional = Optional.of(clientEntityOptional.get().toDTO());
        else
            clientDTOOptional = Optional.ofNullable(null);
        return clientDTOOptional;
    }

    public ClientDTO findByDNI(String dni) {
        ClientEntity client = clientRepository.findByDNI(dni);
        return client == null ? null : client.toDTO();
    }

    public String getSalt(int id) {
        return clientRepository.findClientSaltByClientId(id);
    }

    public String getPassword(int id) {
        return clientRepository.findClientPasswordByClientId(id);
    }

    public void saveUserSaltAndPassword(int userId, String salt, String password) {
        ClientEntity client = clientRepository.findById(userId).orElse(null);
        client.setPassword(password);
        client.setSalt(salt);
        clientRepository.save(client);
    }

    public void updateUserPassword(int userId, String password) {
        ClientEntity client = clientRepository.findById(userId).orElse(null);
        client.setPassword(password);
        clientRepository.save(client);
    }

    public ClientEntity toEntidy(ClientDTO client){
        ClientEntity clientEntity = clientRepository.findById(client.getId()).orElse(null);
        if(clientEntity == null){
            clientEntity = new ClientEntity();
        }
        clientEntity.setId(client.getId());
        clientEntity.setDni(client.getDni());
        clientEntity.setName(client.getName());
        clientEntity.setSurname(client.getSurname());
        clientEntity.setCreationDate(client.getCreationDate());
        return clientEntity;
    }

    public void save(ClientEntity client, BankAccountEntity bankAccount, RequestEntity request, String[] saltAndPass) {
        client.setSalt(saltAndPass[0]);
        client.setPassword(saltAndPass[1]);

        if(client.getBankAccountsById() == null){
            client.setBankAccountsById(List.of(bankAccount));
        }else {
            client.getBankAccountsById().add(bankAccount);
        }

        if(client.getRequestsById() == null){
            client.setRequestsById(List.of(request));
        }else {
            client.getRequestsById().add(request);
        }

        clientRepository.save(client);

    }

    public int getClientId(ClientDTO client) {
        ClientEntity clientEntity = clientRepository.findByDNI(client.getDni());
        return clientEntity.getId();
    }
}
