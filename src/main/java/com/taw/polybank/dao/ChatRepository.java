package com.taw.polybank.dao;

import com.taw.polybank.entity.ChatEntity;
import com.taw.polybank.entity.ClientEntity;
import com.taw.polybank.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, Integer> {
    @Query("select c from ChatEntity c where c.clientByClientId = :client")
    List<ChatEntity> findByClient(@Param("client") ClientEntity client);

    @Query("select c from ChatEntity c where c.employeeByAssistantId = :employee")
    List<ChatEntity> findByEmployee(@Param("employee") EmployeeEntity employee);

    @Query("select c from ChatEntity c where c.employeeByAssistantId = :employee and c.clientByClientId.dni like %:clientDni%")
    List<ChatEntity> findByEmployeeAndClientDni(@Param("employee") EmployeeEntity employee, @Param("clientDni") String clientDni);

    @Query("select c from ChatEntity c where c.employeeByAssistantId = :employee and c.clientByClientId.name like %:clientName% and c.clientByClientId.dni like %:clientDni%")
    List<ChatEntity> findByEmployeeAndClientDniAndClientName(@Param("employee") EmployeeEntity employee, @Param("clientDni") String clientDni, @Param("clientName") String clientName);

    @Query("select c, MAX(m.timestamp) AS latestMessageTimestamp from ChatEntity c INNER JOIN c.messagesById m where c.employeeByAssistantId = :employee and c.clientByClientId.dni like %:clientDni% group by c order by latestMessageTimestamp desc")
    List<ChatEntity> findByEmployeeAndClientDniAndRecent(@Param("employee") EmployeeEntity employee, @Param("clientDni") String clientDni);

    @Query("select c, MAX(m.timestamp) AS latestMessageTimestamp from ChatEntity c INNER JOIN c.messagesById m where c.employeeByAssistantId = :employee and c.clientByClientId.name like %:clientName% and c.clientByClientId.dni like %:clientDni% group by c order by latestMessageTimestamp desc")
    List<ChatEntity> findByEmployeeAndClientDniAndClientNameAndRecent(@Param("employee") EmployeeEntity employee, @Param("clientDni") String clientDni, @Param("clientName") String clientName);

    @Query("select c from ChatEntity c where c.employeeByAssistantId = :employee and c.clientByClientId.name like %:clientName%")
    List<ChatEntity> findByEmployeeAndClientName(@Param("employee") EmployeeEntity employee, @Param("clientName") String clientName);

    @Query("select c, MAX(m.timestamp) AS latestMessageTimestamp from ChatEntity c INNER JOIN c.messagesById m where c.employeeByAssistantId = :employee and c.clientByClientId.name like %:clientName% group by c order by latestMessageTimestamp desc")
    List<ChatEntity> findByEmployeeAndClientNameAndRecent(@Param("employee") EmployeeEntity employee, @Param("clientName") String clientName);

    @Query("select c, MAX(m.timestamp) AS latestMessageTimestamp from ChatEntity c INNER JOIN c.messagesById m where c.employeeByAssistantId = :employee group by c order by latestMessageTimestamp desc")
    List<ChatEntity> findByEmployeeAndRecent(@Param("employee") EmployeeEntity employee);

    @Query("select c from ChatEntity c where c.id = (select max(c.id) from ChatEntity c)")
    List<ChatEntity> findByMaxId();
}
