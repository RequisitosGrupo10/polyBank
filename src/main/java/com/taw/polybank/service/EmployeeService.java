package com.taw.polybank.service;

import com.taw.polybank.dao.EmployeeRepository;
import com.taw.polybank.dao.RequestRepository;
import com.taw.polybank.dto.EmployeeDTO;
import com.taw.polybank.entity.ClientEntity;
import com.taw.polybank.entity.EmployeeEntity;
import com.taw.polybank.entity.RequestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RequestRepository requestRepository;

    public List<EmployeeDTO> findAllClients(){
        List <EmployeeEntity> employeeEntityList= employeeRepository.findAll();
        List<EmployeeDTO> employeeDTOList = new ArrayList<>();
        for (EmployeeEntity employee: employeeEntityList) {
            employeeDTOList.add(employee.toDTO());
        }
        return employeeDTOList;
    }

    public void solveRequest(Integer id, byte approved) {
        Optional<RequestEntity> requestEntityOptional =requestRepository.findById(id);
        if (requestEntityOptional.isEmpty())
            return;
        RequestEntity requestEntity = requestEntityOptional.get();
        requestEntity.setApproved(approved);
        requestEntity.setSolved((byte) 1);
        requestRepository.save(requestEntity);
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
        employeeEntityList.forEach((final EmployeeEntity employeeEntity) -> employeeList.add(employeeEntity.toDTO()));
        return employeeList;
    }
}
