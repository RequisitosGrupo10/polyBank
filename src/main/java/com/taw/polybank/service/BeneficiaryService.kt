package com.taw.polybank.service

import com.taw.polybank.dao.BankAccountRepository
import com.taw.polybank.dao.BeneficiaryRepository
import com.taw.polybank.dao.ClientRepository
import com.taw.polybank.dto.BenficiaryDTO
import com.taw.polybank.dto.ClientDTO
import com.taw.polybank.entity.BenficiaryEntity
import org.springframework.stereotype.Service

/**
 * @author Illya Rozumovskyy 75%
 * @author Lucía Gutiérrez Molina 25%
 */
@Service
class BeneficiaryService(
  private val beneficiaryRepository: BeneficiaryRepository,
  private val bankAccountRepository: BankAccountRepository,
  private val clientRepository: ClientRepository,
) {
  fun findBenficiaryByNameAndIban(beneficiaryName: String?, iban: String?): BenficiaryDTO? {
    val beneficiary =
      beneficiaryRepository.findBenficiaryEntityByNameAndIban(beneficiaryName, iban)
    return if (beneficiary == null) null else beneficiary.toDTO()
  }

  fun toEntity(benficiaryDTO: BenficiaryDTO): BenficiaryEntity {
    val benficiary =
      beneficiaryRepository.findById(benficiaryDTO.getId()).orElse(BenficiaryEntity())
    benficiary.setId(benficiaryDTO.getId())
    benficiary.setName(benficiaryDTO.getName())
    benficiary.setBadge(benficiaryDTO.getBadge())
    benficiary.setIban(benficiaryDTO.getIban())
    benficiary.setSwift(benficiaryDTO.getSwift())
    return benficiary
  }

  fun save(beneficiaryDTO: BenficiaryDTO) {
    val benficiary = this.toEntity(beneficiaryDTO)
    beneficiaryRepository.save<BenficiaryEntity>(benficiary)
    beneficiaryDTO.setId(benficiary.getId())
  }

  fun guardarBeneficiarios(client: ClientDTO) {
    // Selecciono las cuentas del cliente
    val clientEntity = clientRepository.findByDNI(client.getDni())
    val bankAccountEntityList =
      bankAccountRepository.findByClientByClientId(clientEntity)
    for (bankAccountEntity in bankAccountEntityList) {
      // Por cada cuenta, si encuentro un beneficiario entonces le actualizo el nombre
      val benficiaryEntity =
        beneficiaryRepository.findByIban(bankAccountEntity.getIban()).orElse(null)
      if (benficiaryEntity != null) {
        benficiaryEntity.setName(client.getName())
        beneficiaryRepository.save<BenficiaryEntity>(benficiaryEntity)
      }
    }
  }
}
