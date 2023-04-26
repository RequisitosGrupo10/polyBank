package com.taw.polybank.service;

import com.taw.polybank.dao.CompanyRepository;
import com.taw.polybank.dto.ClientDTO;
import com.taw.polybank.dto.CompanyDTO;
import com.taw.polybank.entity.CompanyEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Optional<CompanyEntity> companyEntityOptional = companyRepository.findById(id);
        Optional<CompanyDTO> companyDTOOptional;
        if (companyEntityOptional.isPresent())
            companyDTOOptional = Optional.of(companyEntityOptional.get().toDTO());
        else
            companyDTOOptional = Optional.of(null);
        return companyDTOOptional;
    }

    public List<CompanyDTO> findCompanyRepresentedByClient(int id) {
        return companyRepository.findCompanyRepresentedByClient(id).stream().map(company -> company.toDTO()).collect(Collectors.toList());
    }
}
