package com.example.quiz_sheet_remote_control;

import android.util.Log;


public class ConsoleMessageOutputClass {

    private static String Tag = "myLogs";

    public static void ConsoleWriteLine(String text)
    {
        Log.d(Tag, text);
    }
}