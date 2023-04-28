package com.taw.polybank.dao;


import com.taw.polybank.entity.AuthorizedAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorizedAccountRepository extends JpaRepository<AuthorizedAccountEntity, Integer> {

    @Query("select c.authorizedAccountsById from BankAccountEntity c where c.id = :bankAccountId")
    List<AuthorizedAccountEntity> findAuthorizedAccountEntitiesOfGivenBankAccount(@Param("bankAccountId") Integer id);

    @Query("select auth " +
            "from AuthorizedAccountEntity auth " +
            "join auth.clientByClientId client " +
            "join auth.bankAccountByBankAccountId bank " +
            "join bank.companiesById comp " +
            "where client.id = :clientId and comp.id = :companyId")
    AuthorizedAccountEntity findAuthAccOfGivenClientAndCompany(@Param("clientId") Integer clientId, @Param("companyId") Integer companyId);
}
