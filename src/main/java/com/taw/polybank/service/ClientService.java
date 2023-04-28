package com.taw.polybank.service;

import com.taw.polybank.dao.AuthorizedAccountRepository;
import com.taw.polybank.dao.ClientRepository;
import com.taw.polybank.dto.*;
import com.taw.polybank.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
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

    public void save(ClientDTO clientDTO,
                     BankAccountDTO bankAccountDTO, BankAccountService bankAccountService,
                     RequestDTO requestDTO, RequestService requestService,
                     BadgeService badgeService, String[] saltAndPass) {

        ClientEntity client = this.toEntidy(clientDTO);
        BankAccountEntity bankAccount = bankAccountService.toEntity(bankAccountDTO, this, badgeService);
        RequestEntity request = requestService.toEntity(requestDTO);

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
        clientDTO.setId(client.getId());
    }

    public void addAuthorizedAccount(ClientDTO client, AuthorizedAccountDTO authorizedAccount) {
        ClientEntity clientEntity = clientRepository.findById(client.getId()).orElse(null);
        AuthorizedAccountEntity authorizedAccountEntity = authorizedAccountRepository.findById(authorizedAccount.getAuthorizedAccountId()).orElse(null);

        if(clientEntity.getAuthorizedAccountsById() == null){
            clientEntity.setAuthorizedAccountsById(List.of(authorizedAccountEntity));
        }else{
            clientEntity.getAuthorizedAccountsById().add(authorizedAccountEntity);
        }
        clientRepository.save(clientEntity);
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

    public boolean isBlocked(ClientDTO client, CompanyDTO company, AuthorizedAccountService authorizedAccountService){
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
}
