package com.kap_vlad.android.javachat;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.kap_vlad.android.javachat.DAO.AppDatabase;
import com.kap_vlad.android.javachat.DAO.ListMessage;
import com.kap_vlad.android.javachat.DAO.Person;
import com.kap_vlad.android.javachat.contacts.AddContact;
import com.kap_vlad.android.javachat.contacts.Contacts;
import com.kap_vlad.android.javachat.contacts.ShowContacts;
import com.kap_vlad.android.javachat.messages.DateConverter;
import com.kap_vlad.android.javachat.messages.MessagesAdapter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainSpace extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    public static Connect connect; //connect нам понадобится и в других активити
    public static String messageForSend;//текст сообщения для отправки
    private volatile boolean clientConnected = true;//есть ли соединение
    public static volatile Contacts doIt; //режим для отображения контактов
    public static String messageSender; //отправитель писем (наш емейл)
    List<ListMessage> listMessages; //поле для хранения сообщений
    MessagesAdapter messagesAdapter;//адаптер для вывода сообщений
    public boolean isRemove = false;
    public static AppDatabase db;
    public static MainSpace mainSpace;

    SoundPool sp;
    int sound;

    LinearLayout messageLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_spase);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainSpace = this;

        final Context context = getApplicationContext();


        sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
       // sp.setOnLoadCompleteListener(this);

       sound = sp.load(this, R.raw.hangouts_message, 1);




        //подгружаем файл, в котором храним имя пользователя и пароль
        SharedPreferences preference = context.getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        messageSender = preference.getString("name", null);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ///////////////////////////////////////////////////////////////////////////////////////////////

        run();//запускаем основную логику программы

    }

    @Override
    protected void onStart() {
        super.onStart();

        //подключаемся к базе данных
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database").build();

        AsyncTask at = new AsyncTask<Void, Void, List<ListMessage>>() {
            @Override
            protected List<ListMessage> doInBackground(Void... params) {
                //получаем все сообщения из базы данных
                List<ListMessage> messages = db.getMessageDao().getAllListMessage();

                return messages;
            }
        }.execute();

        try {
            listMessages = (List<ListMessage>) at.get();
        }
        catch (Exception e){

        }

        //создаем адаптер
        messagesAdapter = new MessagesAdapter(this, listMessages, isRemove, this);


        // настраиваем список
        ListView lvMain = (ListView) findViewById(R.id.main_messages);
        lvMain.setAdapter(messagesAdapter);

        //устанавливаем фокус на последнем элементе списка
        lvMain.setSelection(lvMain.getAdapter().getCount()-1);


        //кнопка отправки сообщений
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        if (isRemove == false){
            fab.setImageResource(R.drawable.ic_send_white_24dp);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                    EditText text = (EditText) findViewById(R.id.edit_message);
                    if (connect == null) ShowHelper.show(getApplicationContext(),"no connect!");
                    else {
                        messageForSend = text.getText().toString(); //получаем текст из эдитТекст
                        text.getText().clear(); //очищаем эдитТекст
                        // Check if no view has focus:

                        //бубен, что бы убрать клавиатуру с экрана
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    /*//отправляем сообщение при помощи AsyncTask
                    SendMessage send = new SendMessage();
                    send.setConnect(connect);
                    send.execute(new Message(MessageType.TEXT, s));*/


                        //показываем все контакты
                        doIt = Contacts.SEND;
                        Intent intent = new Intent(MainSpace.this, ShowContacts.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_spase, menu);
        return true;
    }

    @Override //обработка нажания на пункты меню
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout) {
            //обнуляем значения имени пользователя и пароля
            SharedPreferences preference = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
            preference.edit().putString("name", null).apply();
            preference.edit().putString("password", null).apply();

            finish(); //закрываем текущую активность

            //открываем активность ввода
            Intent intent = new Intent(MainSpace.this, Enter.class);
            startActivity(intent);
        }
        else if (id == R.id.action_my_profile) {
            //открываем активность своего профиля
            Intent intent = new Intent(MainSpace.this, MyProfile.class);
            startActivity(intent);

        } else if (id == R.id.action_remove_messages) {
            isRemove = true;
            this.onStart();
        }
        this.onStart();//перегружаем поле с сообщениями

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add_user) {
            //открываем активность добавления контакта
            Intent intent = new Intent(MainSpace.this, AddContact.class);
            startActivity(intent);
        } else if (id == R.id.nav_show_contacts) {
            //показываем все контакты
            doIt = Contacts.SHOW;
            Intent intent = new Intent(MainSpace.this, ShowContacts.class);
            startActivity(intent);

        } else if (id == R.id.nav_select_contact) {

        } else if (id == R.id.nav_remove_contacts) {
            //показываем контакты для удаленя
            doIt = Contacts.REMOVE;
            Intent intent = new Intent(MainSpace.this, ShowContacts.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {
            EditText text = (EditText) findViewById(R.id.edit_message);
            if (connect == null) ShowHelper.show(getApplicationContext(),"no connect!");
            else {
                messageForSend = text.getText().toString(); //получаем текст из эдитТекст
                text.getText().clear(); //очищаем эдитТекст

                //показываем все контакты
                doIt = Contacts.SHARE;
                Intent intent = new Intent(MainSpace.this, ShowContacts.class);
                startActivity(intent);
            }

        } else if (id == R.id.nav_send) {

            EditText text = (EditText) findViewById(R.id.edit_message);
            if (connect == null) ShowHelper.show(getApplicationContext(),"no connect!");
            else {
                messageForSend = text.getText().toString(); //получаем текст из эдитТекст
                text.getText().clear(); //очищаем эдитТекст

                //показываем все контакты
                doIt = Contacts.SEND;
                Intent intent = new Intent(MainSpace.this, ShowContacts.class);
                startActivity(intent);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void restart(){
        this.onStart();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////


    protected SocketThread getSocketThread() { //должен создавать и возвращать новый объект класса SocketThread
        return new SocketThread();
    }


    public void run() {
        SocketThread socketThread = getSocketThread(); //создаем новый сокетный поток
        socketThread.setDaemon(true); // присваиваем ему значения Демона
        socketThread.start();  // стартуем
    }

    public class SocketThread extends Thread { //должен выводить текст message



        protected void clientMainLoop() throws IOException, ClassNotFoundException { //Этот метод будет реализовывать главный цикл обработки сообщений сервера
            while (connect != null) {
                final Message message = connect.receive(); // получаем сообщение, используя соединение connection.
                if (message.getType() == MessageType.TEXT_SERVER) { //если это текстовое сообщение

                    sp.play(sound, 1, 1, 0, 0, 1);

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {

                        @Override
                        public void run() {

                           ListMessage lm = new ListMessage();
                           lm.setText(message.getText());
                           lm.setSender(message.getEmailSender());
                           lm.setReceiver(message.getEmailReciever());
                           lm.setCreateDate(DateConverter.outDate(message.getDate()));

                           AsyncTask writer = new AsyncTask<ListMessage, Void, Boolean>() {
                                @Override
                                protected Boolean doInBackground(ListMessage ... lm) {
                                    try {
                                        ListMessage lmSingle = lm[0];
                                        //робуем получить имя отправителя из списка контактов по eMail
                                        Person person = db.getPersonDao().getPersonFromMail(message.getEmailSender());
                                        if (person != null)lmSingle.setNickname(person.getName());
                                        //добавляем сообщение в базу данных
                                        db.getMessageDao().insertAll(lmSingle);
                                        connect.send(new Message(MessageType.TEXT_SENDED, message.getId()));
                                        return true;
                                    }
                                    catch (Exception e){
                                        return false;
                                    }
                                }
                            }.execute(lm);

                           boolean send;
                           try {
                               send = (boolean)writer.get();
                           }
                           catch (Exception e){
                               send = false;
                           }
                           if (send) restart(); //перезагружаем активность если получили новое сообщение

                        }
                    });

                    Log.d("START", "added TextView in Layout");
                } else {
                    throw new IOException("Unexpected MessageType");
                }
            }
        }

        @Override
        public void run() { //главный метод нити
            //нить работает, пробуя подключиться пока не установим переменную clientConnected в false
            while (clientConnected){
                //пробуем соединиться с сервером
                GetConnect gc = new GetConnect();
                gc.execute();

                try {
                    //если сервер не отвечает в течении 5 секунд, сгенерируется исключение
                    connect = gc.get(5, TimeUnit.SECONDS);
                    //если gc.get вернул null генерируем исключение сами
                    if (connect == null) throw new Exception();
                }
                catch (Exception e){
                    gc.cancel(false);//закрываем GetConnect gc
                }

                if (connect != null){
                    Handshake handshake = new Handshake();
                    handshake.setContext(getApplicationContext());
                    handshake.execute(connect);
                    int i = 4;
                    try {
                        i = handshake.get(); //получаем результат обработки рукопожатия
                    }
                    catch (Exception e){

                    }
                    if (i == 1){
                        finish(); //закрываем текущую активность
                        //открываем активность входа
                        Intent intent = new Intent(MainSpace.this, Enter.class);
                        startActivity(intent);
                        interrupt();//прекращаем работу текущей нити
                    }
                    else if (i != 1 && i != 0) { //непонятная ошибка
                        connect = null;
                    }
                }

                //пока есть соединение
                while (connect != null) {
                    try {
                        //принимаем текстовые сообщения
                        clientMainLoop();
                    }
                    catch (IOException e){

                    }
                    catch (ClassNotFoundException e){

                    }

                }

            }

        }

    }
}




