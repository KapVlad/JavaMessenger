package com.kap_vlad.android.javachat.messages;


import java.util.ArrayList;
import java.util.List;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kap_vlad.android.javachat.DAO.AppDatabase;
import com.kap_vlad.android.javachat.DAO.ListMessage;
import com.kap_vlad.android.javachat.MainSpace;
import com.kap_vlad.android.javachat.R;
import com.kap_vlad.android.javachat.ShowHelper;

public class MessagesAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<ListMessage> objects;
    boolean isRemove;
    MainSpace mainSpace;


    public MessagesAdapter(Context context, List<ListMessage> objects, boolean isRemove, MainSpace mainSpace) {
        ctx = context;
        this.objects = (ArrayList<ListMessage>) objects;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.isRemove = isRemove;
        this.mainSpace = mainSpace;
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
            view = lInflater.inflate(R.layout.item_message, parent, false);
        }

        ListMessage lm = getMessage(position);

        //проверяем не наше ли сообщение
        boolean isIAmSender = lm.getSender().equals(MainSpace.messageSender);
        String nickname = lm.getNickname();

        if (nickname == null)  nickname = lm.getSender();


        // заполняем View в пункте списка данными: имя, email
        ((TextView) view.findViewById(R.id.message_date)).setText(lm.getCreateDate());
        ((TextView) view.findViewById(R.id.message_text)).setText(lm.getText());
        if (isIAmSender)((TextView) view.findViewById(R.id.message_sender)).setText("For: " + nickname);
        else ((TextView) view.findViewById(R.id.message_sender)).setText(nickname + ":");
        LinearLayout ll = view.findViewById(R.id.message_item); //сообщение
        LinearLayout cl = view.findViewById(R.id.message_wrapper);//контейнер для сообщения


        //устанавливаем внешний вид сообщения в зависимости от того кто отправитель
        ll.setBackgroundResource(isIAmSender ? R.drawable.bubble_green : R.drawable.bubble_yellow);
        cl.setGravity(isIAmSender ? Gravity.RIGHT : Gravity.LEFT);

        CheckBox cbBuy = (CheckBox) view.findViewById(R.id.cbBox);

        //находим кнопку для отправки сообщений
        FloatingActionButton button = (FloatingActionButton) mainSpace.findViewById(R.id.fab);

        if (isRemove == false){ //просмотр сообщений
            cbBuy.setVisibility(View.GONE);//скроем чекбокс
            button.setImageResource(R.drawable.ic_send_white_24dp);
          //  button.setOnClickListener(removeListener);
        }
        else { //удаление сообщений
            button.setImageResource(R.drawable.ic_delete_forever_white_24dp_2x);
            button.setOnClickListener(removeListener);
            // присваиваем чекбоксу обработчик
            cbBuy.setOnCheckedChangeListener(myCheckChangeList);
            // пишем позицию
            cbBuy.setTag(position);
            // заполняем данными: выбран контакт или нет
            cbBuy.setChecked(lm.isBox());
        }

        return view;
    }

    // сообщение по позиции
    ListMessage getMessage (int position) {
        return ((ListMessage) getItem(position));
    }

    // возвращает список всех выбранных сообщений
    List<ListMessage> getBox() {
        List<ListMessage> box = new ArrayList<ListMessage>();
        for (ListMessage lm : objects) {
            // если выбран
            if (lm.isBox())
                box.add(lm);
        }
        return box;
    }



    //обработчик нажатия кнопки при удалении сообщений
    View.OnClickListener removeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final List<ListMessage> messagesRemove = getBox();
            if (messagesRemove.size() < 1){
                ShowHelper.show(ctx, "Select messages to delete!");
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
                        for (ListMessage lm: messagesRemove){
                            db.getMessageDao().delete(lm);
                        }
                        return null;
                    }

                }.execute(db);
                ShowHelper.show(ctx, "Messages deleted.");
            }
            mainSpace.isRemove = false;

            mainSpace.restart(); //перегружаем главную активность
        }
    };


    // обработчик для чекбоксов
    OnCheckedChangeListener myCheckChangeList = new OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // меняем данные выбран или нет
            getMessage((Integer) buttonView.getTag()).setBox(isChecked);
        }
    };
}




