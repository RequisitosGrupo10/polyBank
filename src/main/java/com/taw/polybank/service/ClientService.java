package com.taw.polybank.service;

import com.taw.polybank.dao.AuthorizedAccountRepository;
import com.taw.polybank.dao.ClientRepository;
import com.taw.polybank.dto.*;
import com.taw.polybank.entity.*;
import com.taw.polybank.ui.client.ClientFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    protected AuthorizedAccountRepository authorizedAccountRepository;

    public List<ClientDTO> findAll(){
        List <ClientEntity> clientEntityList = clientRepository.findAll();
        List<ClientDTO> clientDTOList = getClientDTOS(clientEntityList);
        return clientDTOList;
    }

    private static List<ClientDTO> getClientDTOS(List<ClientEntity> clientEntityList) {
        List<ClientDTO> clientDTOList = new ArrayList<>();
        for (ClientEntity client : clientEntityList) {
            clientDTOList.add(client.toDTO());
        }
        return clientDTOList;
    }

    public ClientDTO autenticar(String user, String password) {
        ClientEntity clientEntity = clientRepository.autenticar(user, password);
        return clientEntity == null ? null : clientEntity.toDTO();
    }

    public void guardarCliente(ClientDTO client, String password) {
        ClientEntity clientEntity = clientRepository.findByDNI(client.getDni());
        clientEntity.setName(client.getName());
        clientEntity.setSurname(client.getSurname());
        if(!password.isBlank()){
            clientEntity.setPassword(password);
        }
        clientRepository.save(clientEntity);
    }

    public List<ClientDTO> findByNameOrSurname(String transactionOwner) {
        List<ClientEntity> clientEntityList = clientRepository.findByNameOrSurname(transactionOwner);
        return entityListToDTOList(clientEntityList);
    }

    public List<ClientDTO> entityListToDTOList (List<ClientEntity> clientEntityList) {
        List<ClientDTO> clientDTOList = new ArrayList<>();
        for (ClientEntity clientEntity : clientEntityList) {
            clientDTOList.add(clientEntity.toDTO());
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

    public void updateUserPassword(int userId, String password) {
        ClientEntity client = clientRepository.findById(userId).orElse(null);
        client.setPassword(password);
        clientRepository.save(client);
    }

    public ClientEntity toEntidy(ClientDTO client) {
        ClientEntity clientEntity = clientRepository.findById(client.getId()).orElse(new ClientEntity());
        clientEntity.setId(client.getId());
        clientEntity.setDni(client.getDni());
        clientEntity.setName(client.getName());
        clientEntity.setSurname(client.getSurname());
        clientEntity.setCreationDate(client.getCreationDate());
        return clientEntity;
    }

    public int getClientId(ClientDTO client) {
        ClientEntity clientEntity = clientRepository.findByDNI(client.getDni());
        return clientEntity.getId();
    }

    public void save(ClientDTO clientDTO) {
        ClientEntity client = this.toEntidy(clientDTO);
        clientRepository.save(client);
        clientDTO.setId(client.getId());
    }

    public void save(ClientDTO clientDTO, String[] saltAndPass) {
        ClientEntity client = this.toEntidy(clientDTO);
        client.setSalt(saltAndPass[0]);
        client.setPassword(saltAndPass[1]);
        clientRepository.save(client);
        clientDTO.setId(client.getId());
    }

    public List<ClientDTO> findAllRepresentativesOfGivenCompany(int companyId) {
        return clientRepository.findAllRepresentativesOfGivenCompany(companyId)
                .stream()
                .map(client -> client.toDTO())
                .collect(Collectors.toList());
    }

    public List<ClientDTO> findAllRepresentativesOfACompanyThatWasRegisteredBetweenDates(
            int companyId, Timestamp registeredBefore, Timestamp registeredAfter) {
        return clientRepository
                .findAllRepresentativesOfACompanyThatWasRegisteredBetweenDates(
                        companyId, registeredBefore, registeredAfter)
                .stream()
                .map(client -> client.toDTO())
                .collect(Collectors.toList());
    }

    public List<ClientDTO> findAllRepresentativesOfACompanyThatHasANameOrSurnameAndWasRegisteredBetweenDates(
            int companyId, String nameOrSurname, Timestamp registeredBefore, Timestamp registeredAfter) {
        return clientRepository
                .findAllRepresentativesOfACompanyThatHasANameOrSurnameAndWasRegisteredBetweenDates(
                        companyId, nameOrSurname, registeredBefore, registeredAfter)
                .stream()
                .map(client -> client.toDTO())
                .collect(Collectors.toList());
    }

    public boolean isBlocked(ClientDTO client, CompanyDTO company, AuthorizedAccountService authorizedAccountService) {
        List<AuthorizedAccountDTO> listOfAuthAccounts = authorizedAccountService.findAuthorizedAccountEntitiesOfGivenBankAccount(company.getBankAccountByBankAccountId().getId());
        boolean result = listOfAuthAccounts.stream()
                .filter(authAcc -> authAcc.getClientByClientId().equals(client))
                .findFirst()
                .map(authAccount -> authAccount.isBlocked())
                .orElse(false);
        return result;
    }


    public int getNumberAuthorizedAccounts(int clientId) {
        return clientRepository.getNumberAuthorizedAccounts(clientId);
    }

    public String getLastMessage(ClientDTO clientDTO) {
        ClientEntity clientEntity = clientRepository.findById(clientDTO.getId()).orElse(null);
        return clientEntity.getMessagesById().stream().reduce((a, b) -> b).map(MessageEntity::getContent).orElse("");
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
