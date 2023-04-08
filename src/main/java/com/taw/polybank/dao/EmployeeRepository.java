package com.taw.polybank.dao;

import com.taw.polybank.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Integer> {

    @Query("select e from EmployeeEntity e where e.type = 'manager'")
    public List<EmployeeEntity> findAllManagers();
    @Query("select employee from EmployeeEntity employee where employee.dni like :dni")
    Optional<EmployeeEntity> findByDNI(@Param("dni") String dni);
}
