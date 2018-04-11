package com.kap_vlad.android.javachat.contacts;


import java.util.ArrayList;
import java.util.List;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kap_vlad.android.javachat.DAO.AppDatabase;
import com.kap_vlad.android.javachat.DAO.Person;
import com.kap_vlad.android.javachat.MainSpace;
import com.kap_vlad.android.javachat.Message;
import com.kap_vlad.android.javachat.MessageType;
import com.kap_vlad.android.javachat.R;
import com.kap_vlad.android.javachat.SendMessage;
import com.kap_vlad.android.javachat.ShowHelper;

public class ContactsAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Person> objects;
    Contacts doIt;
    AppCompatActivity showContacts;


    public ContactsAdapter(Context context, List<Person> contacts, Contacts doIt, AppCompatActivity showContacts) {
        ctx = context;
        objects = (ArrayList<Person>) contacts;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.doIt = doIt;
        this.showContacts = showContacts;
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item_contact, parent, false);
        }

        Person p = getPerson(position);

        // заполняем View в пункте списка данными: имя, email
        ((TextView) view.findViewById(R.id.contact_name)).setText(p.getName());
        ((TextView) view.findViewById(R.id.contact_mail)).setText(p.geteMail());
        CheckBox cbBuy = (CheckBox) view.findViewById(R.id.cbBox);
        LinearLayout lll = (LinearLayout) view.findViewById( R.id.linearLayout1);
        Button button = (Button) showContacts.findViewById(R.id.show_contacts_button);

        if (doIt == Contacts.SEND){ //отправка сообщения одному контакту
            cbBuy.setVisibility(View.GONE);//скроем чекбокс
            lll.setOnClickListener(sendListener);
        }
        else if (doIt == Contacts.REMOVE){ //удаление контактов
            button.setVisibility(View.VISIBLE);
            button.setText("REMOVE");
            button.setOnClickListener(removeListener);
        }
        else if (doIt == Contacts.SHARE){ // отправка многим пользователям
            button.setVisibility(View.VISIBLE);
            button.setText("SHARE");
            button.setOnClickListener(shareListener);
        }



        //скроем чекбокс и кнопку если нужно просто отобразить контакты
        if (doIt == Contacts.SHOW )cbBuy.setVisibility(View.GONE);

        // присваиваем чекбоксу обработчик
        cbBuy.setOnCheckedChangeListener(myCheckChangeList);
        // пишем позицию
        cbBuy.setTag(position);
        // заполняем данными: выбран контакт или нет
        cbBuy.setChecked(p.isBox());
        return view;
    }

    // контакт по позиции
    Person getPerson(int position) {
        return ((Person) getItem(position));
    }

    // возвращает список всех выбранных контактов
    List<Person> getBox() {
        List<Person> box = new ArrayList<Person>();
        for (Person p : objects) {
            // если выбран
            if (p.isBox())
                box.add(p);
        }
        return box;
    }


    //обработчик нажатия на контакт при отправке сообщения
    View.OnClickListener sendListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            v.setBackgroundColor(Color.LTGRAY);
            TextView t = (TextView) v.findViewById(R.id.contact_mail);
            String messageReceiver = t.getText().toString(); //адрес получателя письма

            Message message = new Message(MessageType.TEXT_CLIENT,MainSpace.messageSender, messageReceiver, MainSpace.messageForSend);


           // Log.d("START", messageReceiver);


            if (MainSpace.connect == null){
                ShowHelper.show(ctx, "No connect!");
                showContacts.finish();
            }
            else {
                //отправляем сообщение при помощи AsyncTask
                SendMessage send = new SendMessage();
                send.setConnect(MainSpace.connect);
                send.execute(message);
                showContacts.finish();
                try {
                    boolean sended = send.get();
                    if (sended){
                        ShowHelper.show(ctx, "Message sent.");
                        MainSpace ms = MainSpace.mainSpace;
                        if (ms != null) ms.restart();
                    }
                }
                catch (Exception e){
                    ShowHelper.show(ctx, "Error sending.");
                }
            }
        }
    };


    //обработчик нажатия кнопки при рассылке сообщения
    View.OnClickListener shareListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (MainSpace.connect == null){
                ShowHelper.show(ctx, "No connect!");
                showContacts.finish();
            }
            else {
                final List<Person> personShare = getBox();
                if (personShare.size() < 1){
                    ShowHelper.show(ctx, "Select contacts to send!");
                }
                else {
                    Message messages[]= new Message[personShare.size()];
                    for (int i = 0, j = personShare.size() ; i < j; i++){
                        Message message = new Message(MessageType.TEXT_CLIENT,MainSpace.messageSender, personShare.get(i).geteMail(), MainSpace.messageForSend);
                        messages[i] = message;
                    }

                    //отправляем все сообщения при помощи AsyncTask
                    SendMessage send = new SendMessage();
                    send.setConnect(MainSpace.connect);
                    send.execute(messages);
                    showContacts.finish();
                    try {
                        boolean sended = send.get();
                        if (sended){
                            ShowHelper.show(ctx, "Messages sent.");
                            MainSpace ms = MainSpace.mainSpace;
                            if (ms != null) ms.restart();
                        }
                    }
                    catch (Exception e){
                        ShowHelper.show(ctx, "Error sending!");
                    }
                }
            }

        }

    };


    //обработчик нажатия кнопки при удалении контактов
    View.OnClickListener removeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final List<Person> personRemove = getBox();
            if (personRemove.size() < 1){
                ShowHelper.show(ctx, "Select contacts to delete!");
            }
            else {
                //подключаемся к базе данных
                AppDatabase db = Room.databaseBuilder(ctx,
                        AppDatabase.class, "database").build();

                new AsyncTask<AppDatabase, Void, Void>() {
                    @Override
                    protected Void doInBackground(AppDatabase... params) {
                        AppDatabase db = (AppDatabase)params[0];
                        //удаляем все отмеченные контакты
                        for (Person p: personRemove){
                            db.getPersonDao().delete(p);
                        }
                        return null;
                    }

                }.execute(db);
                ShowHelper.show(ctx, "Contacts removed!");
                showContacts.finish();
            }
        }

    };

    // обработчик для чекбоксов
    OnCheckedChangeListener myCheckChangeList = new OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // меняем данные выбран или нет
            getPerson((Integer) buttonView.getTag()).setBox(isChecked);
        }
    };
}




