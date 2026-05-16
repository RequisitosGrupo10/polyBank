package com.taw.polybank.dao;

import com.taw.polybank.entity.EmployeeEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author Illya Rozumovskyy 25%
 * @author Lucía Gutiérrez Molina 25%
 * @author Javier Jordán Luque 25%
 * @author José Manuel Sánchez Rico 25%
 */
@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Integer> {

  @Query("select e from EmployeeEntity e where e.type = 'manager'")
  List<EmployeeEntity> findAllManagers();

  @Query("select employee from EmployeeEntity employee where employee.dni like :dni")
  Optional<EmployeeEntity> findByDNI(@Param("dni") String dni);

  @Query(
      "select employee from EmployeeEntity employee where employee.type = 'manager' order by (select count(r) from RequestEntity r where r.employeeByEmployeeId = employee) asc")
  List<EmployeeEntity> findEmployeeWithMinimmumRequests();

  @Query(
      "select employee from EmployeeEntity employee order by (select count(c) from ChatEntity c where c.employeeByAssistantId = employee) asc")
  List<EmployeeEntity> findEmployeeWithMinimumChats();
}
