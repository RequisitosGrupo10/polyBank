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
}
