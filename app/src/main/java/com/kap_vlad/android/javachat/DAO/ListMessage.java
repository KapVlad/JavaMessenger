package com.kap_vlad.android.javachat.DAO;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity (tableName = "listmessage")
public class ListMessage {
    @PrimaryKey(autoGenerate = true)
    private long id; //id сообщения с автогенерацией

    @ColumnInfo(name = "message_text")
    private String text;    //текст сообщения

    @ColumnInfo(name = "message_sender")
    private String sender;  //отправитель сообщения

    @ColumnInfo(name = "message_receiver")
    private String receiver;//получатель сообщения

    @ColumnInfo(name = "message_nickname")
    private String nickname;//если получатель или отправитель письма есть в нашем спике контактов, пишем сюда его никнейм


    @ColumnInfo(name = "created_date")
    private String createDate;

    @ColumnInfo(name = "message_checkbox")
    private boolean box; //


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public boolean isBox() {
        return box;
    }

    public void setBox(boolean box) {
        this.box = box;
    }
}





