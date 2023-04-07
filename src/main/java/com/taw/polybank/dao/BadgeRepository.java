package com.taw.polybank.dao;

import com.taw.polybank.entity.BadgeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BadgeRepository extends JpaRepository<BadgeEntity, Integer> {

    BadgeEntity findBadgeEntityByName(String name);
}
