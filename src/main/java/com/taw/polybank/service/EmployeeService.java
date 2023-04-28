package com.taw.polybank.service;

import com.taw.polybank.dao.BankAccountRepository;
import com.taw.polybank.dao.EmployeeRepository;
import com.taw.polybank.dao.RequestRepository;
import com.taw.polybank.dto.EmployeeDTO;
import com.taw.polybank.dto.RequestDTO;
import com.taw.polybank.entity.BankAccountEntity;
import com.taw.polybank.entity.EmployeeEntity;
import com.taw.polybank.entity.RequestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private BankAccountRepository bankAccountRepository;


    public List<EmployeeDTO> findAllClients(){
        List <EmployeeEntity> employeeEntityList= employeeRepository.findAll();
        List<EmployeeDTO> employeeDTOList = new ArrayList<>();
        for (EmployeeEntity employee: employeeEntityList) {
            employeeDTOList.add(employee.toDTO());
        }
        return employeeDTOList;
    }

    public void solveRequest(Integer id, boolean approved) {
        Optional<RequestEntity> requestEntityOptional =requestRepository.findById(id);
        if (requestEntityOptional.isEmpty())
            return;
        RequestEntity requestEntity = requestEntityOptional.get();
        requestEntity.setApproved(approved);
        requestEntity.setSolved(true);
        BankAccountEntity bankAccountEntity = requestEntity.getBankAccountByBankAccountId();
        if (approved)
            bankAccountEntity.setActive(true);
        requestRepository.save(requestEntity);
        bankAccountRepository.save(bankAccountEntity);
    }

    public List<RequestDTO> findRequestsForEmployee(EmployeeEntity employee) {
        if (employee != null && employee.getType().toString().equals("manager")) {
            return getDtoList(requestRepository.findBySolvedAndAndEmployeeByEmployeeId(false, employee));
        }
        return new ArrayList<>();
    }

    public List<RequestDTO> getDtoList(List<RequestEntity> requestEntityList){
        List<RequestDTO> requestDTOS = new ArrayList<>();
        for (RequestEntity requestEntity : requestEntityList)
            requestDTOS.add(requestEntity.toDTO());
        return requestDTOS;
    }
    
    public EmployeeDTO findById(Integer employeeId) {
        EmployeeEntity employeeEntity = this.employeeRepository.findById(employeeId).orElse(null);
        if (employeeEntity != null) {
            return employeeEntity.toDTO();
        }
        return null;
    }

    public List<EmployeeDTO> findEmployeeWithMinimumChats() {
        return this.listToDTO(employeeRepository.findEmployeeWithMinimumChats());
    }

    protected List<EmployeeDTO> listToDTO(List<EmployeeEntity> employeeEntityList) {
        ArrayList employeeList = new ArrayList<EmployeeDTO>();
        for (EmployeeEntity employeeEntity: employeeEntityList) {
            employeeList.add(employeeEntity.toDTO());
        }
        return employeeList;
    }

    public EmployeeDTO findManager() {
        EmployeeEntity manager = employeeRepository.findAllManagers().stream()
                .min(Comparator.comparingInt(mgr -> mgr.getRequestsById().size()))
                .orElse(null);
        return manager == null ? null : manager.toDTO();
    }

    public EmployeeEntity toEntity(EmployeeDTO employee) {
        EmployeeEntity employeeEntity = employeeRepository.findById(employee.getId()).orElse(null);
        if(employeeEntity == null){
            employeeEntity = new EmployeeEntity();
        }
        employeeEntity.setId(employee.getId());
        employeeEntity.setDni(employee.getDni());
        employeeEntity.setName(employee.getName());
        employeeEntity.setType(employee.getType());
        return employeeEntity;
    }
}
