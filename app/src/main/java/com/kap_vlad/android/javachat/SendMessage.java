package com.kap_vlad.android.javachat;


import android.os.AsyncTask;

import com.kap_vlad.android.javachat.DAO.AppDatabase;
import com.kap_vlad.android.javachat.DAO.ListMessage;
import com.kap_vlad.android.javachat.DAO.Person;
import com.kap_vlad.android.javachat.messages.DateConverter;

import java.io.IOException;
import java.util.Date;


public class SendMessage extends AsyncTask<Message,Void,Boolean> {

    private Connect connect;

    public void setConnect(Connect connect) {
        this.connect = connect;
    }

    @Override
    protected Boolean doInBackground(Message ... messages) {
        //подключаемся к базе данных
        AppDatabase db = MainSpace.db;

        try {
            for (Message message: messages){
                connect.send(message); //отправляем сообщение

                //робуем получить имя отправителя из списка контактов по eMail
                Person person = db.getPersonDao().getPersonFromMail(message.getEmailReciever());

                //пишем сообщение во внутреннюю базу данных
                ListMessage lm = new ListMessage();
                lm.setCreateDate(DateConverter.outDate(new Date()));
                lm.setSender(message.getEmailSender());
                lm.setReceiver(message.getEmailReciever());
                lm.setNickname(person.getName());
                lm.setText(message.getText());
                db.getMessageDao().insertAll(lm);
            }
            return true;
        }
        catch (IOException e){

        }
        return false;
    }
}





