package com.taw.polybank.dao;

import com.taw.polybank.entity.CurrencyExchangeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Illya Rozumovskyy
 */
@Repository
public interface CurrencyExchangeRepository
    extends JpaRepository<CurrencyExchangeEntity, Integer> {}
