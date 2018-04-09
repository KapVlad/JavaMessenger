package com.kap_vlad.android.javachat.DAO;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Person {
    @PrimaryKey @NonNull
    @ColumnInfo(name = "user_mail")
    String eMail;

    @ColumnInfo(name = "user_name")
    String name;

    @ColumnInfo(name = "check_box")
    boolean box; //для чекбокса при использовании списка

    public Person(@NonNull String eMail, String name) {
        this.eMail = eMail;
        this.name = name;
        box = false;
    }

    public String geteMail() {
        return eMail;
    }

    public String getName() {
        return name;
    }

    public boolean isBox() {
        return box;
    }

    public void setBox(boolean box) {
        this.box = box;
    }
}





