package com.taw.polybank.dao;

import com.taw.polybank.entity.BadgeEntity;
import com.taw.polybank.entity.BankAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BadgeRepository extends JpaRepository<BadgeEntity, Integer> {

    public BadgeEntity findByBankAccountsById(BankAccountEntity bankAccount);

    BadgeEntity findBadgeEntityByName(String name);
}
