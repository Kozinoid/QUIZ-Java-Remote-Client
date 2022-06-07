package com.example.quiz_sheet_remote_control;

import java.util.EventObject;
class ScoreChangedEvent extends EventObject {

    private String team;
    private int score = 0;

    public ScoreChangedEvent(Object source, String tName, int s) {
        super(source);
        this.team = tName;
        this.score = s;
    }

    @Override
    public String toString(){
        return getClass().getName() + "[source = " + getSource() + ", message = " + team + ", score = " + score + "]";
    }

    public String getTeamName()
    {
        return team;
    }
    public int getScore()
    {
        return score;
    }
}

interface ScoreChangedEventListener {
    public void ScoreChanged(ScoreChangedEvent e);
}