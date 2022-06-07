package com.example.layoutsvariation;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class FastTextEnter extends LinearLayout {

    private EditText mTextEditor;
    private Button mEnterButton;

    public FastTextEnter(Context context) {
        super(context);
        initializeViews(context);
    }

    public FastTextEnter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public FastTextEnter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.fast_text_enter, this);
        InitViewElements();
    }

    private void InitViewElements()
    {
        //Настраиваем для обоих кнопок изображения.
        //Будем использовать стандартные изображения:

        mTextEditor = (EditText)findViewById(R.id.text_input);
        mEnterButton = (Button) findViewById(R.id.bt_enter);
        mEnterButton.setText("Enter");
    }
}
