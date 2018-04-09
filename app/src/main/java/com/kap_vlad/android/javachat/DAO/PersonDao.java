package com.kap_vlad.android.javachat.DAO;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface PersonDao {

    // Добавление Person в бд
    @Insert
    void insertAll(Person... people);

    // Удаление Person из бд
    @Delete
    void delete(Person person);

    // Получение всех Person из бд
    @Query("SELECT * FROM person")
    List<Person> getAllPerson();

    // Получение Person из бд по eMail
    @Query("SELECT * FROM person WHERE user_mail LIKE :eMail")
    Person getPersonFromMail(String eMail);

}





