package com.taw.polybank.service;

import com.taw.polybank.dao.BankAccountRepository;
import com.taw.polybank.dao.ClientRepository;
import com.taw.polybank.dao.EmployeeRepository;
import com.taw.polybank.dao.RequestRepository;
import com.taw.polybank.dto.*;
import com.taw.polybank.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.taw.polybank.dao.RequestRepository;
import com.taw.polybank.dto.RequestDTO;
import com.taw.polybank.entity.RequestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Illya Rozumovskyy 50%
 * @author Lucía Gutiérrez Molina 50%
 */
@Service
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ClientRepository clientRepository;


    public List<RequestDTO> findByBankAccountByBankAccountIdAndAndSolved(BankAccountDTO bankAccount, boolean b) {
        BankAccountEntity bankAccountEntity = bankAccountRepository.findByIban(bankAccount.getIban()).orElse(null);
        List<RequestEntity> requestEntityList = requestRepository.findByBankAccountByBankAccountIdAndSolved(bankAccountEntity, b);
        return entityListToDTO(requestEntityList);
    }

    public List<RequestDTO> entityListToDTO (List<RequestEntity> requestEntityList){
        List<RequestDTO> requestDTOList = new ArrayList<>();
        for(RequestEntity requestEntity : requestEntityList){
            requestDTOList.add(requestEntity.toDTO());
        }
        return requestDTOList;
    }

    public void createNewRequest(ClientDTO client, BankAccountDTO bankAccount, String activation, String description) {

        List<EmployeeEntity> employees = employeeRepository.findEmployeeWithMinimmumRequests();
        ClientEntity clientEntity = clientRepository.findByDNI(client.getDni());
        BankAccountEntity bankAccountEntity = bankAccountRepository.findByIban(bankAccount.getIban()).orElse(null);

        RequestEntity request = new RequestEntity();
        request.setClientByClientId(clientEntity);
        request.setBankAccountByBankAccountId(bankAccountEntity);
        request.setEmployeeByEmployeeId(employees.get(0));
        request.setSolved(false);
        request.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        request.setType(activation);
        request.setDescription(description == null ? "" : description);

        requestRepository.save(request);
    }

    public RequestEntity toEntity(RequestDTO request) {
        RequestEntity requestEntity = requestRepository.findById(request.getId()).orElse(new RequestEntity());
        requestEntity.setId(request.getId());
        requestEntity.setSolved(request.isSolved());
        requestEntity.setTimestamp(request.getTimestamp());
        requestEntity.setType(request.getType());
        requestEntity.setDescription(request.getDescription());
        requestEntity.setApproved(request.isApproved());
        return requestEntity;
    }

    public void save(RequestDTO requestDTO, ClientService clientService,
                     BankAccountService bankAccountService, EmployeeService employeeService,
                     BadgeService badgeService) {
        RequestEntity request = this.toEntity(requestDTO);

        request.setClientByClientId(clientService.toEntidy(requestDTO.getClientByClientId()));
        request.setBankAccountByBankAccountId(bankAccountService.toEntity(requestDTO.getBankAccountByBankAccountId(), clientService, badgeService));
        request.setEmployeeByEmployeeId(employeeService.toEntity(requestDTO.getEmployeeByEmployeeId()));

        requestRepository.save(request);
        requestDTO.setId(request.getId());
    }

    public List<RequestDTO> findUnsolvedUnblockRequestByUserId(int clientId, int bankAccountId) {
        List<RequestEntity> requestEntities = requestRepository.findUnsolvedUnblockRequestByUserId(clientId, bankAccountId);
        List<RequestDTO> requestDTOS = requestEntities.stream().map(req -> req.toDTO()).collect(Collectors.toList());
        return requestDTOS;
    }
}
