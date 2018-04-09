package com.kap_vlad.android.javachat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.net.Socket;

public class Register extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Context context = getApplicationContext();

        //устанавливаем полям ввода соответствующие типы
        final EditText email = (EditText) findViewById(R.id.register_email);
        email.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        final EditText password = (EditText) findViewById(R.id.register_password);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        final EditText passwordAgain = (EditText) findViewById(R.id.register_password_again);
        passwordAgain.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        Button signInButton = (Button) findViewById(R.id.register_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //проверки на корректность ввода
                if (email.getText().length() < 5){
                    ShowHelper.show(context, "Your E-Mail is too short");
                }
                else if (password.getText().length() < 3){
                    ShowHelper.show(context, "Your password must be at least 5 characters long");
                }
                else if (passwordAgain.getText().toString().isEmpty()){
                    ShowHelper.show(context, "Add password(again)");
                }
                else if (!password.getText().toString().equals(passwordAgain.getText().toString())) {
                    ShowHelper.show(context, "Password and confirmation are not the same!");
                }
                else{
                    RegisterTask rt = new RegisterTask();
                    rt.execute(email.getText().toString(), password.getText().toString());
                    Integer i = 5;
                    try {
                        i = rt.get();
                    }
                    catch (Exception e){
                        ShowHelper.show(context,"Internal Error");
                    }
                    switch (i) {
                        case 0: ShowHelper.show(context, "Registration completed successfully!");
                                finish(); //закрываем текущую активность
                                //открываем главную активность
                                Intent intent = new Intent(Register.this, MainSpace.class);
                                startActivity(intent);
                                break;
                        case 1: ShowHelper.show(context, "A user with this name already exists!");
                                break;
                        case 2: ShowHelper.show(context, "No connect!");
                                break;
                        case 3: ShowHelper.show(context, "ClassNotFoundException");
                                break;
                        case 4: ShowHelper.show(context, "Exception");
                                break;

                    }
                }
            }
        });
    }

    class RegisterTask extends AsyncTask<String,Void,Integer> {

        @Override
        protected Integer doInBackground(String... strings) {

            String name = strings[0];
            String pass = Security.get_SHA_512_SecurePassword(strings[1]);
            try {

                Socket socket = new Socket(Link.serverAdress, Link.serverPort);//создаем новый объект класса java.net.Socket, используя данные, полученные в предыдущем пункте
                Connect connect = new Connect(socket); //создаем объект класса Connection, используя сокет

                while (true) {
                    Message message = connect.receive(); // получаем сообщение, используя соединение connection.
                    if (message.getType() == MessageType.NAME_REQUEST) { //если тип полученного сообщения NAME_REQUEST (сервер запросил имя)
                        connect.send(new Message(MessageType.REGISTER, name, pass)); //создать новое сообщение с именем пользователя и пароля для регистрации
                    } else if (message.getType() == MessageType.NAME_ACCEPTED) { //если тип полученного сообщения MessageType.NAME_ACCEPTED (сервер принял имя), значит сервер принял имя клиента
                        //сохраняем значения id, имени пользователя и пароля
                        SharedPreferences preference = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
                        //  preference.edit().putInt("id", message.getId);
                        preference.edit().putString("name", name).apply();
                        preference.edit().putString("password", pass).apply();
                        connect.close();
                        return  0; //регистрация прошла успешно
                    } else if (message.getType() == MessageType.INVALID_NAME_OR_PASSWORD){
                        connect.close();
                        return  1; //тако имя пользователя уже занято
                    }
                }

            } catch (IOException e) {
                return  2; //IOException
            } catch (ClassNotFoundException e) {
                return  3; //ClassNotFoundException
            } catch (Exception e) {
                return  4;
            }
        }



    }
    @Override
    public boolean onSupportNavigateUp(){
        Intent intent = new Intent(Register.this, Enter.class);
        startActivity(intent);
        finish();
        return true;
    }
}
