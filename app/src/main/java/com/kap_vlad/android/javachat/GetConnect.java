package com.kap_vlad.android.javachat;


import android.os.AsyncTask;
import java.io.IOException;
import java.net.Socket;

public class GetConnect extends AsyncTask<Void,Void,Connect> {

    private static Connect connect = null;

    @Override
    protected Connect doInBackground(Void ... voids) {
        try {
            Socket socket = new Socket(Link.serverAdress, Link.serverPort);//создаем новый объект класса java.net.Socket, используя данные, полученные в предыдущем пункте
            connect = new Connect(socket); //создаем объект класса Connection, используя сокет
        }
        catch (IOException e){
            return null;
        }
        return connect;
    }
}





