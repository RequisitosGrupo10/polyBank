package com.taw.polybank.service;

import com.taw.polybank.dao.RequestRepository;
import com.taw.polybank.dto.BankAccountDTO;
import com.taw.polybank.dto.ClientDTO;
import com.taw.polybank.dto.EmployeeDTO;
import com.taw.polybank.dto.RequestDTO;
import com.taw.polybank.entity.RequestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class RequestService {

    @Autowired
    protected RequestRepository requestRepository;
    @Autowired
    protected ClientService clientService;
    @Autowired
    protected BankAccountService bankAccountService;
    @Autowired
    protected EmployeeService employeeService;

    public RequestEntity toEntity(RequestDTO request) {
        RequestEntity requestEntity = requestRepository.findById(request.getId()).orElse(null);
        if(requestEntity == null){
            requestEntity = new RequestEntity();
        }

        requestEntity.setId(request.getId());
        requestEntity.setSolved(request.isSolved());
        requestEntity.setTimestamp(request.getTimestamp());
        requestEntity.setType(request.getType());
        requestEntity.setDescription(request.getDescription());
        requestEntity.setApproved(request.isApproved());
        requestEntity.setClientByClientId(clientService.toEntidy(request.getClientByClientId()));
        requestEntity.setBankAccountByBankAccountId(bankAccountService.toEntity(request.getBankAccountByBankAccountId()));
        requestEntity.setEmployeeByEmployeeId(employeeService.toEntity(request.getEmployeeByEmployeeId()));
        return requestEntity;
    }
}
