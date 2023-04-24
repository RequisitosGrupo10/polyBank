package com.taw.polybank.dao;

import com.taw.polybank.entity.CurrencyExchangeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurrencyExchangeRepository extends JpaRepository<CurrencyExchangeEntity, Integer> {

    List<CurrencyExchangeEntity> findCurrencyExchangeEntitiesByBadgeByInitialBadgeIdId(Integer id);

    List<CurrencyExchangeEntity> findCurrencyExchangeEntitiesByBadgeByFinalBadgeIdId(Integer id);
}
