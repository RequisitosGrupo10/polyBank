package com.taw.polybank.service

import com.taw.polybank.dao.ClientRepository
import com.taw.polybank.dto.AuthorizedAccountDTO
import com.taw.polybank.dto.ClientDTO
import com.taw.polybank.dto.CompanyDTO
import com.taw.polybank.entity.ClientEntity
import com.taw.polybank.entity.MessageEntity
import com.taw.polybank.ui.client.ClientFilter
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors

/**
 * @author Illya Rozumovskyy 45%
 * @author Lucía Gutiérrez Molina 10%
 * @author José Manuel Sánchez Rico 45%
 */
@Service
class ClientService(
  private val clientRepository: ClientRepository,
) {

  fun findAll(): MutableList<ClientDTO?> {
    val clientEntityList = clientRepository.findAll()
    val clientDTOList: MutableList<ClientDTO?> = getClientDTOS(clientEntityList)
    return clientDTOList
  }

  fun autenticar(user: String?, password: String?): ClientDTO? {
    val clientEntity = clientRepository.autenticar(user, password)
    return if (clientEntity == null) null else clientEntity.toDTO()
  }

  fun guardarCliente(client: ClientDTO, password: String) {
    val clientEntity = clientRepository.findByDNI(client.getDni())
    clientEntity.setName(client.getName())
    clientEntity.setSurname(client.getSurname())
    if (!password.isBlank()) {
      clientEntity.setPassword(password)
    }
    clientRepository.save(clientEntity)
  }

  fun findById(id: Int): Optional<ClientDTO> {
    val clientEntityOptional = clientRepository.findById(id)
    val clientDTOOptional: Optional<ClientDTO>
    if (clientEntityOptional.isPresent()) {
      clientDTOOptional =
        Optional.of(clientEntityOptional.get().toDTO())
    } else {
      clientDTOOptional = Optional.empty<ClientDTO>()
    }
    return clientDTOOptional
  }

  fun findByDNI(dni: String?): ClientDTO? {
    val client = clientRepository.findByDNI(dni)
    return if (client == null) null else client.toDTO()
  }

  fun getSalt(id: Int): String = clientRepository.findClientSaltByClientId(id)

  fun getPassword(id: Int): String? = clientRepository.findClientPasswordByClientId(id)

  fun updateUserPassword(userId: Int, password: String?) {
    val client = clientRepository.findById(userId).orElse(null)
    client.setPassword(password)
    clientRepository.save(client)
  }

  fun toEntidy(client: ClientDTO): ClientEntity {
    val clientEntity =
      clientRepository.findById(client.getId()).orElse(ClientEntity())
    clientEntity.setId(client.getId())
    clientEntity.setDni(client.getDni())
    clientEntity.setName(client.getName())
    clientEntity.setSurname(client.getSurname())
    clientEntity.setCreationDate(client.getCreationDate())
    return clientEntity
  }

  fun save(clientDTO: ClientDTO) {
    val client = this.toEntidy(clientDTO)
    clientRepository.save<ClientEntity>(client)
    clientDTO.setId(client.getId())
  }

  fun save(clientDTO: ClientDTO, saltAndPass: Array<String?>) {
    val client = this.toEntidy(clientDTO)
    client.setSalt(saltAndPass[0])
    client.setPassword(saltAndPass[1])
    clientRepository.save<ClientEntity>(client)
    clientDTO.setId(client.getId())
  }

  fun findAllRepresentativesOfGivenCompany(companyId: Int): MutableList<ClientDTO?> = clientRepository.findAllRepresentativesOfGivenCompany(companyId).stream()
    .map<ClientDTO?> { client: ClientEntity? -> client!!.toDTO() }
    .collect(Collectors.toList())

  fun findAllRepresentativesOfACompanyThatWasRegisteredBetweenDates(
    companyId: Int,
    registeredBefore: Timestamp?,
    registeredAfter: Timestamp?,
  ): MutableList<ClientDTO?> = clientRepository
    .findAllRepresentativesOfACompanyThatWasRegisteredBetweenDates(
      companyId,
      registeredBefore,
      registeredAfter,
    )
    .stream()
    .map { client: ClientEntity? -> client!!.toDTO() }
    .collect(Collectors.toList())

  fun findAllRepresentativesOfACompanyThatHasANameOrSurnameAndWasRegisteredBetweenDates(
    companyId: Int,
    nameOrSurname: String?,
    registeredBefore: Timestamp?,
    registeredAfter: Timestamp?,
  ): MutableList<ClientDTO?> = clientRepository
    .findAllRepresentativesOfACompanyThatHasANameOrSurnameAndWasRegisteredBetweenDates(
      companyId,
      nameOrSurname,
      registeredBefore,
      registeredAfter,
    )
    .stream()
    .map { client: ClientEntity? -> client!!.toDTO() }
    .collect(Collectors.toList())

  fun isBlocked(
    client: ClientDTO?,
    company: CompanyDTO,
    authorizedAccountService: AuthorizedAccountService,
  ): Boolean {
    val listOfAuthAccounts =
      authorizedAccountService.findAuthorizedAccountEntitiesOfGivenBankAccount(
        company.getBankAccountByBankAccountId().id,
      )
    val result =
      listOfAuthAccounts.stream()
        .filter { authAcc: AuthorizedAccountDTO? -> authAcc!!.getClientByClientId() == client }
        .findFirst()
        .map<Boolean?>(Function { authAccount: AuthorizedAccountDTO? -> authAccount!!.isBlocked() })
        .orElse(false)
    return result
  }

  fun getNumberAuthorizedAccounts(clientId: Int): Int = clientRepository.getNumberAuthorizedAccounts(clientId)

  fun getLastMessage(clientDTO: ClientDTO): String {
    val clientEntity = clientRepository.findById(clientDTO.getId()).orElse(null)
    return clientEntity!!.getMessagesById().stream()
      .reduce { a: MessageEntity?, b: MessageEntity? -> b }
      .map(Function { obj: MessageEntity? -> obj!!.getContent() })
      .orElse("")
  }

  fun findByFilter(filter: ClientFilter?): MutableList<ClientDTO?> {
    if (filter == null) return this.findAll()
    if ((filter.getDNI() == null || filter.getDNI().isBlank()) &&
      (filter.getName() == null || filter.getName().isBlank())
    ) {
      return this.findAll()
    }
    if (filter.getDNI() != null && !filter.getDNI().isBlank() && filter.getName() != null && !filter.getName()
        .isBlank()
    ) {
      return getClientDTOS(
        clientRepository.findClientsByDNIAndName(filter.getDNI(), filter.getName()),
      )
    }
    if (filter.getDNI() != null && !filter.getDNI().isBlank()) {
      return getClientDTOS(
        clientRepository.findClientsByDNI(
          filter.getDNI(),
        ),
      )
    }
    if (filter.getName() != null && !filter.getName()
        .isBlank()
    ) {
      return getClientDTOS(clientRepository.findClientsByNameOrSurname(filter.getName()))
    }
    return this.findAll()
  }

  companion object {
    private fun getClientDTOS(clientEntityList: MutableList<ClientEntity>): MutableList<ClientDTO?> {
      val clientDTOList: MutableList<ClientDTO?> = ArrayList<ClientDTO?>()
      for (client in clientEntityList) {
        clientDTOList.add(client.toDTO())
      }
      return clientDTOList
    }
  }
}
