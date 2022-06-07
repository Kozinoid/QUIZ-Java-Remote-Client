package com.example.quiz_sheet_remote_control;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Comparator;

public class TeamFieldViewClass extends LinearLayout
{
    public static final int HIGH_SCORE = 1;
    public static final int MID_SCORE = 0;
    public static final int LOW_SCORE = -1;
    private Button mDecButton;
    private Button mIncButton;
    private TextView mTeamName;
    private TextView mScore;
    private TextView mNumber;
    private ArrayList<ScoreChangedEventListener> listeners = new ArrayList<ScoreChangedEventListener>();
    private Paint paint;

    private int score = 0;

    //********************************  КОНСТРУКТОРЫ  **********************************************
    public TeamFieldViewClass(Context context) {
        super(context);

        setWillNotDraw(false) ;
        paint = new Paint();

        initializeViews(context);
    }

    public TeamFieldViewClass(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setWillNotDraw(false) ;
        paint = new Paint();

        initializeViews(context);
    }

    public TeamFieldViewClass(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setWillNotDraw(false) ;
        paint = new Paint();

        initializeViews(context);
    }

    //*******************************  ПРОРИСОВКА КОНТУРА  *****************************************
    @Override
    protected void onDraw(Canvas canvas) {
        Paint strokePaint = paint;
        //strokePaint.setColor(R.color.black);
        //strokePaint.setColor(getResources().getColor(R.color.black));
        strokePaint.setColor(getResources().getColor(R.color.black));
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(2);
        Rect r = canvas.getClipBounds() ;
        Rect outline = new Rect( 1,1,r.right-1, r.bottom-1) ;
        canvas.drawRect(outline, strokePaint) ;
    }

    //******************************  ИНИЦИАЛИЗИРУЕМ МАКЕТ  ****************************************
    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.team_field_view, this);
        InitViewElements();
    }

    //*****************************  ИНИЦИАЛИЗИРУЕМ ЭЛЕМЕНТЫ  **************************************
    private void InitViewElements()
    {
        mDecButton = this.findViewById(R.id.team_view_dec);
        mDecButton.setOnClickListener(scoreClickListener);
        mDecButton.setText(getResources().getText(R.string.bt_dec));

        mIncButton = this.findViewById(R.id.team_view_add);
        mIncButton.setOnClickListener(scoreClickListener);
        mIncButton.setText(getResources().getText(R.string.bt_inc));

        mTeamName = this.findViewById(R.id.team_name);
        mScore = this.findViewById(R.id.score_value);
        mNumber = this.findViewById(R.id.team_number);

        setTeamName(getResources().getString(R.string.unnamed_text));
        setScore(score);

        setColor(HIGH_SCORE);
    }

    //************************  ОБРАБОРТЧИК НАЖАТИЙ КНОПОУ "+" и "-"  ******************************
    OnClickListener scoreClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.team_view_dec:
                    setScore(score - 1);
                    break;

                case R.id.team_view_add:
                    setScore(score + 1);
                    break;
            }

            fireScoreChangedEvent(((TeamFieldViewClass)v.getParentForAccessibility()).getTeamName(),
                    ((TeamFieldViewClass)v.getParentForAccessibility()).getScore());
        }
    };

    //****************************  ПАРАМЕТРЫ ЭКЗЕМПЛЯРА КЛАССА  ***********************************
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
        if (score < 0) score = 0;
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
            setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));
        }
        else if (state < 0)
        {
            setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
        }
        else
        {
            setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue));
        }
    }

    public void setNumber(int num)
    {
        mNumber.setText(String.valueOf(num));
    }

    //*********************  LISTENER  *************************************************************
    public void addScoreChangedEventListener(ScoreChangedEventListener listener){
        listeners.add(listener);
    }

    public ScoreChangedEventListener[] getComDataEnableEventListeners(){
        return listeners.toArray(new ScoreChangedEventListener[listeners.size()]);
    }

    public void removeCScoreChangedEventListener(ScoreChangedEventListener listener){
        listeners.remove(listener);
    }

    protected void fireScoreChangedEvent(String name, int score){
        ScoreChangedEvent ev = new ScoreChangedEvent(this, name, score);
        for (ScoreChangedEventListener listener : listeners){
            listener.ScoreChanged(ev);
        }
    }

    //*************************  COMPARATOR  *******************************************************
    public static final Comparator<TeamFieldViewClass> COMPARE_BY_SCORE = new Comparator<TeamFieldViewClass>() {
        @Override
        public int compare(TeamFieldViewClass lhs, TeamFieldViewClass rhs) {
            return rhs.getScore() - lhs.getScore();
        }
    };
}
