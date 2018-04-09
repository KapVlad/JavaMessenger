package com.kap_vlad.android.javachat.contacts;

import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.kap_vlad.android.javachat.DAO.AppDatabase;
import com.kap_vlad.android.javachat.DAO.Person;
import com.kap_vlad.android.javachat.MainSpace;
import com.kap_vlad.android.javachat.R;

import java.util.List;

public class ShowContacts extends AppCompatActivity {

    List<Person> contacts;
    ContactsAdapter contactsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_contacts);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onStart() {
        super.onStart();
        AsyncTask at = new AsyncTask<Void, Void, List<Person>>() {
            @Override
            protected List<Person> doInBackground(Void... params) {

                //подключаемся к базе данных
                AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "database").build();


                //получаем все контакты из базы данных
                List<Person> contacts = db.getPersonDao().getAllPerson();

                return contacts;
            }
        }.execute();

        try {
            contacts = (List<Person>) at.get();
        }
        catch (Exception e){

        }

        //создаем адаптер
        contactsAdapter = new ContactsAdapter(this, contacts, MainSpace.doIt, this);
        // настраиваем список
        ListView lvMain = (ListView) findViewById(R.id.lvMain);
        lvMain.setAdapter(contactsAdapter);

    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
