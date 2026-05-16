package com.taw.polybank.dao;

import com.taw.polybank.entity.ChatEntity;
import com.taw.polybank.entity.MessageEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author Javier Jordán Luque
 */
@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Integer> {
  @Query("select m from MessageEntity m where m.chatByChatId = :chat")
  List<MessageEntity> findByChat(@Param("chat") ChatEntity chat);
}
