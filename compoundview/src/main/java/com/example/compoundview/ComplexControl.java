package com.example.compoundview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class ComplexControl extends LinearLayout
{
    public static final int upColor = Color.GREEN;
    public static final int mdColor = Color.BLUE;
    public static final int dnColor = Color.RED;

    private Button mDecButton;
    private Button mIncButton;
    private TextView mTeamName;
    private TextView mScore;

    private int score = 0;
    private int color = upColor;

    public ComplexControl(Context context) {
        super(context);
        initializeViews(context);
    }

    public ComplexControl(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public ComplexControl(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    private void initializeViews(Context context)
    {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.controlcomplex, this);
        InitViewElements();
        
    }

    private void InitViewElements()
    {
        //Настраиваем для обоих кнопок изображения.
        //Будем использовать стандартные изображения:
        mDecButton = this.findViewById(R.id.team_view_dec);
        mDecButton.setText("-");

        mIncButton = this.findViewById(R.id.team_view_add);
        mIncButton.setText("+");

        mTeamName = this.findViewById(R.id.team_name);
        mScore = this.findViewById(R.id.score_value);

        setTeamName("Noname team");
        setScore(score);

        setBackgroundColor(Color.RED);
    }

    public void setTeamName(String name)
    {
        mTeamName.setText(name);
    }

    public String getTeamName()
    {
        return (String) mTeamName.getText();
    }

    public void setScore(int sc)
    {
        score = sc;
        mScore.setText(String.valueOf(score));
    }

    public int getScore()
    {
        return score;
    }

    public void setColor(int state)
    {
        if (state > 0)
        {
            color = upColor;
            setBackgroundColor(upColor);
        }
        else if (state < 0)
        {
            color = dnColor;
            setBackgroundColor(dnColor);
        }
        else
        {
            color = mdColor;
            setBackgroundColor(mdColor);
        }
    }

    public int getColor()
    {
        return color;
    }
}
