package com.kap_vlad.android.javachat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import java.io.IOException;


public class Handshake extends AsyncTask<Connect,Void,Integer> {

    private Connect connect;
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    protected Integer doInBackground(Connect... connects) {
        //подгружаем файл, в котором храним имя пользователя и пароль
        SharedPreferences preference = context.getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        connect = connects[0];
        try {
            while (true) {
                Message message = connect.receive(); // получаем сообщение, используя соединение connection.
                if (message.getType() == MessageType.NAME_REQUEST) { //если тип полученного сообщения NAME_REQUEST (сервер запросил имя)

                    String name = preference.getString("name", null);
                    String pass = preference.getString("password", null);

                    connect.send(new Message(MessageType.USER_NAME, name, pass)); //создать новое сообщение с именем пользователя и пароля
                } else if (message.getType() == MessageType.NAME_ACCEPTED) { //если тип полученного сообщения MessageType.NAME_ACCEPTED (сервер принял имя), значит сервер принял имя клиента
                    return  0; //ррукопожатие прошло успешно
                } else {
                    return  1; //неверное имя пользователя или пароль
                }
            }
        }
        catch (IOException e){
            return 2;
        }
        catch (ClassNotFoundException e){
            return 3;
        }
    }
}





