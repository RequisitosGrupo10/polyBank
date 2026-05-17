package com.taw.polybank.dao;

import com.taw.polybank.entity.BankAccountEntity;
import com.taw.polybank.entity.EmployeeEntity;
import com.taw.polybank.entity.RequestEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author Illya Rozumovskyy 33%
 * @author José Manuel Sánchez Rico 66%
 */
@Repository
public interface RequestRepository extends JpaRepository<RequestEntity, Integer> {

  List<RequestEntity> findBySolvedAndAndEmployeeByEmployeeId(
      boolean solved, EmployeeEntity employee);

  List<RequestEntity> findByBankAccountByBankAccountIdAndSolved(
      BankAccountEntity bankAccount, boolean b);

  @Query(
      "select r from RequestEntity r where r.bankAccountByBankAccountId.id = :bankId and r.clientByClientId.id = :userId and r.solved = false and r.type = :type")
  List<RequestEntity> findUnsolvedUnblockRequestByUserId(
      @Param("userId") int userId,
      @Param("bankId") int bankId,
      @Param("type") RequestEntity.RequestType type);
}
