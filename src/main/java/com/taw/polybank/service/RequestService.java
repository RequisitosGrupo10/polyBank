package com.taw.polybank.service;

import com.taw.polybank.dao.RequestRepository;
import com.taw.polybank.dto.RequestDTO;
import com.taw.polybank.entity.RequestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestService {

    @Autowired
    protected RequestRepository requestRepository;


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
