package com.taw.polybank.service;

import com.taw.polybank.dao.CompanyRepository;
import com.taw.polybank.dto.ClientDTO;
import com.taw.polybank.dto.CompanyDTO;
import com.taw.polybank.entity.CompanyEntity;
import com.taw.polybank.ui.company.CompanyFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    public List<CompanyDTO> findAll() {
        List<CompanyEntity> companyEntities = companyRepository.findAll();
        List<CompanyDTO> companyDTOS = getCompanyDTOS(companyEntities);
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

    public List<CompanyDTO> findByFilter(CompanyFilter filter) {
        if (filter == null)
            return this.findAll();
        if (filter.getName() == null || filter.getName().isBlank()) {
            return this.findAll();
        }
        if (filter.getName() != null && !filter.getName().isBlank()){
            return getCompanyDTOS(companyRepository.findByName(filter.getName()));
        }
        return Collections.emptyList();
    }

    private static List<CompanyDTO> getCompanyDTOS(List<CompanyEntity> companyEntities) {
        List<CompanyDTO> companyDTOS = new ArrayList<>();
        for (CompanyEntity companyEntity: companyEntities) {
            companyDTOS.add(companyEntity.toDTO());
        }
        return companyDTOS;
    }
}
