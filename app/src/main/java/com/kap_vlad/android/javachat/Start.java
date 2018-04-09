package com.kap_vlad.android.javachat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class Start extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //выполняем код в отдельном потоке
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                //считываем из файла настроек имя пользователя и пароль
                SharedPreferences preference = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
                String name = preference.getString("name", null);
                String password = preference.getString("password", null);
                //проверяем есть ли имя пользователя и пароль
                if (name == null || password == null) {
                    //открываем активность входа
                    Intent enter = new Intent(Start.this, Enter.class);
                    startActivity(enter);
                }
                else {
                    //открываем главную входа
                    Intent main = new Intent(Start.this, MainSpace.class);
                    startActivity(main);
                }
                finish(); //закрываем текущую активность
            }
        }, 4000);
    }
}
