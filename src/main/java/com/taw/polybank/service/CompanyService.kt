package com.taw.polybank.service

import com.taw.polybank.dao.CompanyRepository
import com.taw.polybank.dto.CompanyDTO
import com.taw.polybank.entity.CompanyEntity
import com.taw.polybank.ui.company.CompanyFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import java.util.stream.Collectors

/**
 * @author Illya Rozumovskyy 80%
 * @author José Manuel Sánchez Rico 20%
 */
@Service
class CompanyService(private val companyRepository: CompanyRepository) {

  fun findAll(): MutableList<CompanyDTO?> {
    val companyEntities = companyRepository.findAll()
    val companyDTOS: MutableList<CompanyDTO?> = getCompanyDTOS(companyEntities)
    return companyDTOS
  }

  fun findById(id: Int): Optional<CompanyDTO> {
    val companyEntityOptional = companyRepository.findById(id)
    val companyDTOOptional: Optional<CompanyDTO>
    if (companyEntityOptional.isPresent()) {
      companyDTOOptional =
        Optional.of(companyEntityOptional.get().toDTO())
    } else {
      companyDTOOptional = Optional.empty<CompanyDTO>()
    }
    return companyDTOOptional
  }

  fun findCompanyRepresentedByClient(id: Int): MutableList<CompanyDTO?> = companyRepository.findCompanyRepresentedByClient(id).stream()
    .map { company: CompanyEntity? -> company!!.toDTO() }
    .collect(Collectors.toList())

  fun toEntity(company: CompanyDTO): CompanyEntity {
    val companyEntity =
      companyRepository.findById(company.getId()).orElse(CompanyEntity())
    companyEntity.setId(company.getId())
    companyEntity.setName(company.getName())
    return companyEntity
  }

  fun save(
    companyDTO: CompanyDTO,
    bankAccountService: BankAccountService,
    clientService: ClientService,
    badgeService: BadgeService,
  ) {
    val company = this.toEntity(companyDTO)

    val bankAccount =
      bankAccountService.toEntity(
        companyDTO.getBankAccountByBankAccountId(),
        clientService,
        badgeService,
      )
    company.setBankAccountByBankAccountId(bankAccount)

    companyRepository.save<CompanyEntity>(company)
    companyDTO.setId(company.getId())
  }

  fun save(companyDTO: CompanyDTO) {
    val company = companyRepository.findById(companyDTO.getId()).orElse(null)
    company.setName(companyDTO.getName())
    companyRepository.save(company)
  }

  fun findCompanyByName(beneficiaryName: String?): CompanyDTO? {
    val company = companyRepository.findCompanyEntityByName(beneficiaryName)
    return if (company == null) null else company.toDTO()
  }

  fun findByFilter(filter: CompanyFilter?): MutableList<CompanyDTO?>? {
    if (filter == null) return this.findAll()
    if (filter.getName() == null || filter.getName().isBlank()) {
      return this.findAll()
    }
    if (filter.getName() != null && !filter.getName().isBlank()) {
      return getCompanyDTOS(companyRepository.findByName(filter.getName()))
    }
    return mutableListOf()
  }

  companion object {
    private fun getCompanyDTOS(companyEntities: MutableList<CompanyEntity>): MutableList<CompanyDTO?> {
      val companyDTOS: MutableList<CompanyDTO?> = ArrayList<CompanyDTO?>()
      for (companyEntity in companyEntities) {
        companyDTOS.add(companyEntity.toDTO())
      }
      return companyDTOS
    }
  }
}
