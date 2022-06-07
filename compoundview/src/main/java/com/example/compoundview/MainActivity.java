package com.example.compoundview;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    ComplexControl cControl1;
    ComplexControl cControl2;
    ComplexControl cControl3;
    LinearLayout llMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        llMain = findViewById(R.id.main_layout);
        llMain.setBackgroundColor(Color.BLACK);

        AddNewTeam();
    }

    //**********************************  ADD NEW TEAM  ********************************************
    private void AddNewTeam()
    {
        // Создание LayoutParams c шириной и высотой по содержимому
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // создаем Button, пишем текст и добавляем в LinearLayout
        cControl1 = new ComplexControl(this);
        cControl1.setColor(1);
        cControl2 = new ComplexControl(this);
        cControl2.setColor(0);
        cControl3 = new ComplexControl(this);
        cControl3.setColor(-1);

        llMain.addView(cControl1, lParams);
        llMain.addView(cControl2, lParams);
        llMain.addView(cControl3, lParams);
    }
}
