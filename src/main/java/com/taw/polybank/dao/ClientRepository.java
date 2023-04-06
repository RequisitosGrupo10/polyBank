package com.taw.polybank.dao;

import com.taw.polybank.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Integer> {

    @Query("select c from ClientEntity c where c.dni = :dni")
    ClientEntity findByDNI(@Param("dni") String dni);

    @Query("select ce from ClientEntity ce join ce.authorizedAccountsById aa join aa.bankAccountByBankAccountId bank join bank.companiesById com where com.id = :id union select ce from ClientEntity ce join ce.bankAccountsById bank join bank.companiesById com where com.id = :id")
    List<ClientEntity> findAllRepresentativesOfGivenCompany(@Param("id") Integer id);
}
