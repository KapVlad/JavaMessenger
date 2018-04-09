package com.kap_vlad.android.javachat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class SignIn extends AppCompatActivity {
    Connect connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Context context = getApplicationContext();

        //устанавливаем полям ввода соответствующие типы
        final EditText email = (EditText) findViewById(R.id.sign_email);
        email.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        final EditText password = (EditText) findViewById(R.id.sign_password);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (email.getText().toString().isEmpty()){
                    ShowHelper.show(context, "ADD YOUR E-MAIL");
                }
                else if (password.getText().toString().isEmpty()){
                    ShowHelper.show(context, "ADD YOUR PASSWORD");
                }

                //пробуем соединиться с сервером
                GetConnect gc = new GetConnect();
                gc.execute();

                String name = email.getText().toString();
                String pass = Security.get_SHA_512_SecurePassword(password.getText().toString());

                //сохраняем значения имени пользователя и пароля в файл, т.к. Handshake получает значения из него
                SharedPreferences preference = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
                preference.edit().putString("name", name).apply();
                preference.edit().putString("password", pass).apply();

                try {
                    //если сервер не отвечает в течении 5 секунд, сгенерируется исключение
                    connect = gc.get(5, TimeUnit.SECONDS);
                    //если gc.get вернул null генерируем исключение сами
                    if (connect == null) throw new Exception();
                }
                catch (Exception e){
                    ShowHelper.show(context, "No connect!");
                    gc.cancel(false);//закрываем GetConnect gc
                }

                if (connect != null){
                    //пробуем совершить рукопожатие
                    Handshake handshake = new Handshake();
                    handshake.setContext(getApplicationContext());//устанавливаем контекст для рукопожатия
                    handshake.execute(connect);
                    int i = 4;
                    try {
                        i = handshake.get(); //получаем результат обработки рукопожатия
                    }
                    catch (Exception e){

                    }
                    switch (i) {
                        case 0: ShowHelper.show(context, "Signed in!");
                                finish(); //закрываем текущую активность
                                //открываем главную активность
                                Intent intent = new Intent(SignIn.this, MainSpace.class);
                                startActivity(intent);
                                break;
                        case 1: ShowHelper.show(context,"Invalid username or password!");

                                break;
                        case 2: ShowHelper.show(context,"IOException");
                                break;
                        case 3: ShowHelper.show(context,"ClassNotFoundException");
                                break;
                        case 4: ShowHelper.show(context,"Exception");
                                break;
                    }
                    //закрываем соединение
                    try {
                        connect.close();
                    }
                    catch (IOException e){

                    }
                }
            }
        });

    }
    @Override
    public boolean onSupportNavigateUp(){
        Intent intent = new Intent(SignIn.this, Enter.class);
        startActivity(intent);
        finish();
        return true;
    }
}
