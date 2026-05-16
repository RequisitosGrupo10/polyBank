package com.taw.polybank.dao;

import com.taw.polybank.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Lucía Gutiérrez Molina
 */
@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Integer> {}
