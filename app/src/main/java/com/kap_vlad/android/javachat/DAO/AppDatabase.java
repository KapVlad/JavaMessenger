package com.kap_vlad.android.javachat.DAO;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Person.class , ListMessage.class,/* AThirdEntityType.class */}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PersonDao getPersonDao(); //получить таблицу, в которой хранятся контакты
    public abstract ListMessageDao getMessageDao(); //получить таблицу, в которой харнятся сообщения
}





