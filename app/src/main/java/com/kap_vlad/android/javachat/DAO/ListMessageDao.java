package com.kap_vlad.android.javachat.DAO;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ListMessageDao {

    // Добавление сообщений в бд
    @Insert
    void insertAll(ListMessage...lm);

    // Удаление сообщения из бд
    @Delete
    void delete(ListMessage lm);

    // Получение всех записей из бд
    @Query("SELECT * FROM listmessage")
    List<ListMessage> getAllListMessage();

    // Получение сообщений из бд по eMail отправителя
    @Query("SELECT * FROM listmessage WHERE message_sender LIKE :sender")
    List<ListMessage> getListFromSender(String sender);

    // Получение сообщений из бд по eMail получателя
    @Query("SELECT * FROM listmessage WHERE message_receiver LIKE :receiver")
    List<ListMessage> getListFromReceiver(String receiver);

    // получить сообщения id которых между from и to
    @Query("SELECT * FROM listmessage WHERE id BETWEEN :from AND :to")
    List<ListMessage> fetchMessageBetweenId(long from, long to);

    // получить максимальный id
    @Query("SELECT MAX(id) FROM listmessage")
    long getMessageMaxId();

    @Query("SELECT COUNT(*) from listmessage")
    long countMessages();
}





