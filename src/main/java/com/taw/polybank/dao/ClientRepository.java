package com.taw.polybank.dao;

import com.taw.polybank.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Integer> {

    @Query("select c from ClientEntity c where c.dni = :dni")
    ClientEntity findByDNI(@Param("dni") String dni);
}
