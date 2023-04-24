package com.taw.polybank.service;

import com.taw.polybank.dao.CompanyRepository;
import com.taw.polybank.dto.CompanyDTO;
import com.taw.polybank.entity.CompanyEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    public List<CompanyDTO> findAll() {
        List<CompanyEntity> companyEntities = companyRepository.findAll();
        List<com.taw.polybank.dto.CompanyDTO> companyDTOS = new ArrayList<>();
        for (CompanyEntity companyEntity: companyEntities) {
            companyDTOS.add(companyEntity.toDTO());
        }
        return companyDTOS;
    }

    public Optional<CompanyDTO> findById(Integer id) {
        Optional<CompanyEntity> companyEntity = companyRepository.findById(id);
        companyRepository.findById(id);
        return null;
    }
}
