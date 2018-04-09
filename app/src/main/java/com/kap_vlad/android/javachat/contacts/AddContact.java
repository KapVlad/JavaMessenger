package com.kap_vlad.android.javachat.contacts;

import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kap_vlad.android.javachat.DAO.AppDatabase;
import com.kap_vlad.android.javachat.DAO.Person;
import com.kap_vlad.android.javachat.R;
import com.kap_vlad.android.javachat.ShowHelper;

import java.util.List;
/*
* В этой активности добавляем новый контакт
*
*/


public class AddContact extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //устанавливаем полям ввода соответствующие типы
        final EditText email = (EditText) findViewById(R.id.add_contact_email);
        email.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        final EditText name = (EditText) findViewById(R.id.add_contact_name);
       // name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        Button addButton = (Button) findViewById(R.id.add_contact_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (email.getText().toString().isEmpty()){
                    ShowHelper.show(getApplicationContext(), "ADD YOUR E-MAIL");
                }
                else if (name.getText().toString().isEmpty()){
                    ShowHelper.show(getApplicationContext(), "ADD YOUR PASSWORD");
                }
                else {
                    String eMail = email.getText().toString();
                    String contactName = name.getText().toString();
                    //подключаемся к базе данных
                    AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                            AppDatabase.class, "database").build();

                    new AsyncTask<Object, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(Object... params) {
                            AppDatabase db = (AppDatabase)params[0];
                            String eMail = (String)params[1];
                            String contactName = (String)params[2];
                            //получаем все контакты из базы данных
                            List<Person> contacts = db.getPersonDao().getAllPerson();

                            //если в базе уже есть контакт с таким email возвращаем false
                            for (Person p: contacts){
                                 if (p.geteMail().equals(eMail)) return false;
                            }

                            //вставляем новый контакт в таблицу контактов
                            db.getPersonDao().insertAll(new Person(eMail, contactName));
                            return true;
                        }

                        @Override
                        protected void onPostExecute(Boolean isAdded) {
                            if (isAdded) ShowHelper.show(getApplicationContext(), "Contact saved.");
                            else ShowHelper.show(getApplicationContext(), "Error: A contact with this name already exists.");
                        }
                    }.execute(db, eMail, contactName);

                    finish();
                }
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
