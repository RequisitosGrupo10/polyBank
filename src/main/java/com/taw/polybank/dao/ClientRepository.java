package com.taw.polybank.dao;

import com.taw.polybank.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Integer> {
    @Query("select c from ClientEntity c where c.dni = :user and c.password = :password")
    ClientEntity autenticar(@Param("user") String user, @Param("password") String password);

    @Query("select c from ClientEntity c where c.name like concat('%',:name ,'%') or c.surname like concat('%',:name ,'%')")
    List<ClientEntity> findByNameOrSurname(String name);


    @Query("select c from ClientEntity c where c.dni = :dni")
    ClientEntity findByDNI(@Param("dni") String dni);

    @Query("select ce from ClientEntity ce join ce.authorizedAccountsById aa join aa.bankAccountByBankAccountId bank " +
            "join bank.companiesById com where com.id = :id union select ce from ClientEntity ce " +
            "join ce.bankAccountsById bank join bank.companiesById com where com.id = :id")
    List<ClientEntity> findAllRepresentativesOfGivenCompany(@Param("id") Integer id);

    @Query("SELECT ce FROM ClientEntity ce " +
            "JOIN ce.authorizedAccountsById aa " +
            "JOIN aa.bankAccountByBankAccountId bank " +
            "JOIN bank.companiesById com " +
            "WHERE com.id = :companyId " +
            "AND (lower(ce.name) LIKE lower(concat('%', :nameOrSurname, '%')) OR lower(ce.surname) LIKE lower(concat('%', :nameOrSurname, '%'))) " +
            "AND ce.creationDate <= :registeredBefore " +
            "AND ce.creationDate >= :registeredAfter " +
            "UNION " +
            "SELECT ce FROM ClientEntity ce " +
            "JOIN ce.bankAccountsById bank " +
            "JOIN bank.companiesById com " +
            "WHERE com.id = :companyId " +
            "AND (lower(ce.name) LIKE lower(concat('%', :nameOrSurname, '%')) OR lower(ce.surname) LIKE lower(concat('%', :nameOrSurname, '%'))) " +
            "AND ce.creationDate <= :registeredBefore " +
            "AND ce.creationDate >= :registeredAfter")
    List<ClientEntity> findAllRepresentativesOfACompanyThatHasANameOrSurnameAndWasRegisteredBetweenDates(@Param("companyId") Integer id,
                                                                                                         @Param("nameOrSurname") String nameOrSurname,
                                                                                                         @Param("registeredBefore") Timestamp registeredBefore,
                                                                                                         @Param("registeredAfter") Timestamp registeredAfter);

    @Query("SELECT ce FROM ClientEntity ce " +
            "JOIN ce.authorizedAccountsById aa " +
            "JOIN aa.bankAccountByBankAccountId bank " +
            "JOIN bank.companiesById com " +
            "WHERE com.id = :companyId " +
            "AND ce.creationDate <= :registeredBefore " +
            "AND ce.creationDate >= :registeredAfter " +
            "UNION " +
            "SELECT ce FROM ClientEntity ce " +
            "JOIN ce.bankAccountsById bank " +
            "JOIN bank.companiesById com " +
            "WHERE com.id = :companyId " +
            "AND ce.creationDate <= :registeredBefore " +
            "AND ce.creationDate >= :registeredAfter")
    List<ClientEntity> findAllRepresentativesOfACompanyThatWasRegisteredBetweenDates(@Param("companyId") Integer id,
                                                                                     @Param("registeredBefore") Timestamp registeredBefore,
                                                                                     @Param("registeredAfter") Timestamp registeredAfter);
    ClientEntity findClientEntityByBankAccountsByIdAndName(Integer id, String name);


}
