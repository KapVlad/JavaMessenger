package com.kap_vlad.android.javachat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Enter extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        Button signButton = (Button) findViewById(R.id.sign);
        signButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();//закрываем текущю активность
                //открываем активность ввода логина
                Intent intent = new Intent(Enter.this, SignIn.class);
                startActivity(intent);
            }
        });

        Button regButton = (Button) findViewById(R.id.reg);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();//закрываем текущю активность
                //открываем активность регистрации
                Intent intent = new Intent(Enter.this, Register.class);
                startActivity(intent);
            }
        });
    }
}
