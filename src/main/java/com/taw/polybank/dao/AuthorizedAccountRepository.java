package com.taw.polybank.dao;


import com.taw.polybank.entity.AuthorizedAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorizedAccountRepository extends JpaRepository<AuthorizedAccountEntity, Integer> {
}
