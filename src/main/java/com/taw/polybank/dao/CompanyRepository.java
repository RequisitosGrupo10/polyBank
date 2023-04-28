package com.taw.polybank.dao;

import com.taw.polybank.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Integer> {

    @Query("select co from CompanyEntity co join co.bankAccountByBankAccountId bc join bc.authorizedAccountsById auth join auth.clientByClientId cli where cli.id = :id")
    CompanyEntity findCompanyRepresentedByClientUsingAuthAcc(@Param("id") int id);

    @Query("select c from CompanyEntity c where c.name like concat('%',:name ,'%')")
    List<CompanyEntity> findByName(String name);
    
    @Query("select c from CompanyEntity c where c.bankAccountByBankAccountId.clientByClientId.id = :id " +
            "UNION " +
            "select co from CompanyEntity co join co.bankAccountByBankAccountId bc join bc.authorizedAccountsById auth join auth.clientByClientId cli where cli.id = :id")
    List<CompanyEntity> findCompanyRepresentedByClient(@Param("id") int id);

    CompanyEntity findCompanyEntityByName(String name);
}
