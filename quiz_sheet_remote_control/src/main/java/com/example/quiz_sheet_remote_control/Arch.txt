package com.example.quiz_sheet_remote_control;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private LinearLayout llMain;
    private List<TeamFieldViewClass> teamList = new ArrayList<TeamFieldViewClass>();
    private TeamFieldViewClass currentTeam;
    private boolean renameTeamProcess = false;
    private Button mButtonEnter;
    private Button mButtonIPEnter;
    private EditText mQuickNameEditText;
    private TextView mIPTextView;
    private com.example.quiz_sheet_remote_control.DBHelper dbHelper;
    //------------------------------------  TCP Section  -------------------------------------------
    final String SAVED_TEXT = "saved_text";

    private TCP_Client_Connection myTCPDevice;
    private static final int tcpPort = 8060;
    private SharedPreferences sPref;
    private String tcpConnectionIDText = "";

    private UDP_Client_Device myUDPDevice;
    private static final int udpPort = 9010;
    private static final String udpIPAddresText = "235.5.5.254";
    private String appName = "#Q#U#I#Z";  // Код приложения для UDP
    //----------------------------------------------------------------------------------------------

    // Создание activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InitializeAll();
    }

    // Restart
    @Override
    protected void onRestart() {
        super.onRestart();

        InitializeAll();
    }

    // Init All
    private void InitializeAll() {
        CreateUI();
        LoadAll();
        LoadIP();
        Connect();
    }

    // Инициализация элементов управления
    private void CreateUI()
    {
        setContentView(R.layout.activity_main);
        llMain = findViewById(R.id.main_layout);
        llMain.setBackgroundColor(Color.BLACK);
        mButtonEnter = findViewById(R.id.bt_enter);
        mButtonEnter.setOnClickListener(mButtonClickListener);
        mButtonIPEnter = findViewById(R.id.bt_connect);
        mButtonIPEnter.setOnClickListener(mButtonClickListener);
        mQuickNameEditText = findViewById(R.id.text_input);
        mIPTextView = findViewById(R.id.ip_text);
        dbHelper = new com.example.quiz_sheet_remote_control.DBHelper(this);
    }

    // Activity приостановлено
    @Override
    protected void onStop() {
        SaveAll();
        SaveIP();
        Disconnect();
        super.onStop();
    }

    //**********************************************************************************************
    //*                                СЕКЦИЯ РАБОТЫ С СЕТЬЮ                                       *
    //**********************************************************************************************

    //----------------------------------------------------------------------------------------------
    private void Connect() {
        myTCPDevice = new TCP_Client_Connection();
        myUDPDevice = new UDP_Client_Device(udpIPAddresText, udpPort, appName, onServerFound);
        myUDPDevice.Connect();
    }

    //----------------------------------------------------------------------------------------------
    private void Disconnect()
    {
        setConnectionState(false);
        if (myTCPDevice.connected) myTCPDevice.Disconnect();
        //myUDPDevice.Disconnect();
    }

    //------------------ Получает TCP сообщения в виде строки в msg.obj ----------------------------
    Handler.Callback onRecieveMessage = new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case TCP_Client_Connection.CONNECTED:
                    setConnectionState(true);
                    break;

                case TCP_Client_Connection.TEXT_MESSAGE:
                    ProcessMessage((String) msg.obj);
                    break;
            }
            return false;
        }
    };

    //----------------- Получает IP найденного сервера ---------------------------------------------
    Handler.Callback onServerFound = new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == UDP_Client_Device.FOUND)
            {

                tcpConnectionIDText = GetConnectionID((String) msg.obj, String.valueOf(tcpPort));
                mIPTextView.setText(tcpConnectionIDText);
                myTCPDevice.Connect(tcpConnectionIDText, onRecieveMessage);

                myUDPDevice.Disconnect();
            }
            return false;
        }
    };

    //---------------------------  Состояние соединения  -------------------------------------------
    private void setConnectionState(boolean state)
    {
        if (state)
        {
            // Отображаем, что подключение есть
            mIPTextView.setTextColor(getResources().getColor(R.color.green));
            mButtonIPEnter.setEnabled(false);
            SendTabl();
        }
        else
        {
            // Отображаем, что подключения нет
            mIPTextView.setTextColor(getResources().getColor(R.color.red));
            mButtonIPEnter.setEnabled(true);
        }
    }

    // Обработка команд от сервера
    private void ProcessMessage(String message)
    {

    }

    //**************************  КОМАНДЫ СЕРВЕРУ  *************************************************
    //--------------------------  Результат одной команды  -----------------------------------------
    private void SendOneTeam(String name, int score)
    {
        String message = "#r#e#f<" + name + ">" + score + "#";
        if (myTCPDevice.connected) myTCPDevice.onSendRequest(message);
    }

    //--------------------------  Переименование команды  ------------------------------------------
    private void SendRenameTeam(String name, String newname)
    {
        String message = "#r#e#n<" + name + "><" + newname + ">#";
        if (myTCPDevice.connected) myTCPDevice.onSendRequest(message);
    }

    //--------------------------  Добавление команды  ----------------------------------------------
    private void SendAddTeam(String name, int score)
    {
        String message = "#a#d#d<" + name + ">" + score + "#";
        if (myTCPDevice.connected) myTCPDevice.onSendRequest(message);
    }

    //---------------------------  Удаление команды  -----------------------------------------------
    private void SendDeleteTeam(String name)
    {
        String message = "#d#e#l<" + name + ">#";
        if (myTCPDevice.connected) myTCPDevice.onSendRequest(message);
    }

    //------------------------  Вся таблица  -------------------------------------------------------
    private void SendTabl()
    {
        String message = "#t#a#b";
        if (myTCPDevice.connected) myTCPDevice.onSendRequest(message);
        for (int i = 0; i < teamList.size(); i++)
        {
            SendAddTeam(teamList.get(i).getTeamName(), teamList.get(i).getScore());
        }
    }

    //-----------------------  Полноэкранный режим  ------------------------------------------------
    private void SendFullScreen()
    {
        String message = "#f#s#m";
        if (myTCPDevice.connected) myTCPDevice.onSendRequest(message);
    }

    //-----------------------  Сохраняем текущий IP  -----------------------------------------------
    private void SaveIP()
    {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(SAVED_TEXT, tcpConnectionIDText);
        ed.commit();
    }

    //-----------------------  Загружаем текущий IP  -----------------------------------------------
    private void LoadIP()
    {
        sPref = getPreferences(MODE_PRIVATE);
        mIPTextView.setText(sPref.getString(SAVED_TEXT, "tcp://192.168.0.99:8060/"));
    }

    //**********************************************************************************************
    //*                                   ГЛАВНЫЙ ИНТЕРФЕЙС                                        *
    //**********************************************************************************************
    // Создание меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Контекстное меню
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        currentTeam = (TeamFieldViewClass)v;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
        menu.setHeaderTitle(currentTeam.getTeamName());
    }

    // Пункт контекстного  меню
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.menu_delete_team:
                SendDeleteTeam(currentTeam.getTeamName());
                DeleteTeam(currentTeam);
                RefreshNumeration();
                ColorSort();
                break;

            case R.id.menu_reset_team:
                ResetTeamScore(currentTeam);
                RefreshNumeration();
                ColorSort();
                SendOneTeam(currentTeam.getTeamName(), currentTeam.getScore());
                break;

            case R.id.menu_rename_team:
                renameTeamProcess = true;
                GetTeamNameDialogShow(currentTeam.getTeamName());
                break;
        }
        return super.onContextItemSelected(item);
    }

    // Обработка выбора пункта меню
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id)
        {
            case R.id.add_settings:
                renameTeamProcess = false;
                GetTeamNameDialogShow("");
                break;

            case R.id.reset_all_settings:
                ResetAllScores();
                RefreshNumeration();
                ColorSort();
                SendTabl();
                break;

            case R.id.refresh_settings:
                Sort();
                RefreshNumeration();
                SendTabl();
                break;

            case R.id.fullscreen_settings:
                SendFullScreen();
                break;

            case R.id.delete_all_settings:
                DeleteAllTeams();
                SendTabl();
                break;

            case R.id.save_settings:
                SaveAll();
                break;

            case R.id.open_settings:
                LoadAll();
                SendTabl();
                break;

            case R.id.exit_settings:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //**************************************  SAVE DATA  *******************************************
    private void SaveAll()
    {
        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // удаляем все записи
        int clearCount = db.delete("mytable", null, null);
        int size = teamList.size();
        for (int i = 0; i < size; i++)
        {
            // создаем объект для данных
            ContentValues cv = new ContentValues();
            cv.put("name", teamList.get(i).getTeamName());
            cv.put("score", teamList.get(i).getScore());
            long rowID = db.insert("mytable", null, cv);
        }

        // закрываем подключение к БД
        dbHelper.close();
    }

    //**************************************  LOAD DATA  *******************************************
    private void LoadAll()
    {
        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        DeleteAllTeams();

        // делаем запрос всех данных из таблицы mytable, получаем Cursor
        Cursor c = db.query("mytable", null, null, null, null, null, null);

        // ставим позицию курсора на первую строку выборки
        // если в выборке нет строк, вернется false
        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int scoreColIndex = c.getColumnIndex("score");

            do {
                AddNewTeam(c.getString(nameColIndex), c.getInt(scoreColIndex));

                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
            } while (c.moveToNext());
        }
        c.close();

        // закрываем подключение к БД
        dbHelper.close();
        RefreshNumeration();
        ColorSort();
    }

    //***************************************  ОЧИСТИТЬ ВСЕ РЕЗУЛЬТАТЫ  ****************************
    private void ResetAllScores()
    {
        for (int i = 0; i < teamList.size(); i++)
        {
            teamList.get(i).setScore(0);
        }
    }

    //************************************  УДАЛИТЬ ВСЕ КОМАНДЫ  ***********************************
    private void DeleteAllTeams()
    {
        int size = teamList.size();
        for (int i = 0; i < size; i++)
        {
            DeleteTeam(teamList.get(0));
        }
    }

    //*****************************************  ВЫЗОВ ДИАЛОГА  ************************************
    private void GetTeamNameDialogShow(String name)
    {
        GetStringDialogFragment dialog = new GetStringDialogFragment(this, myDialogClickListener, name);
        dialog.show(getSupportFragmentManager(), "TeamNameDialog");
    }

    //*********************************  ОБРАБОТКА СОБЫТИЙ ДИАЛОГА  ********************************
    DialogInterface.OnClickListener myDialogClickListener = new DialogInterface.OnClickListener()
    {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            AlertDialog aDialog = (AlertDialog) dialog;
            EditText nameText = (EditText) aDialog.findViewById(R.id.new_name_dialog_field);
            String currentName = nameText.getText().toString();
            switch (which)
            {
                case Dialog.BUTTON_POSITIVE:
                    if (renameTeamProcess)
                    {
                        //  Переименовываем команду
                        SendRenameTeam(currentTeam.getTeamName(), currentName);
                        RenameTeam(currentTeam, currentName);

                    }
                    else
                    {
                        // Создаем новую команду
                        currentName = NameCorrecting(currentName);
                        AddNewTeam(currentName, 0);
                        ColorSort();
                        RefreshNumeration();
                        SendAddTeam(currentName, 0);
                    }
                    break;
                case Dialog.BUTTON_NEGATIVE:
                    dialog.cancel();
                    break;
            }
        }
    };

    //************************  ОБРАБОТКА СОБЫТИЙ КНОПОК  ******************************************
    View.OnClickListener mButtonClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.bt_enter:
                    String name = NameCorrecting(mQuickNameEditText.getText().toString());
                    AddNewTeam(name, 0);
                    ColorSort();
                    RefreshNumeration();
                    SendAddTeam(name, 0);
                    mQuickNameEditText.setText("");
                    break;

                case R.id.bt_connect:
                    String ipText = mQuickNameEditText.getText().toString();
                    if (ipText.equals(""))
                    {
                        tcpConnectionIDText = mIPTextView.getText().toString();
                    }
                    else
                    {
                        tcpConnectionIDText = GetConnectionID("/" + ipText, String.valueOf(tcpPort));
                        mQuickNameEditText.setText("");
                        mIPTextView.setText(tcpConnectionIDText);
                    }

                    myTCPDevice.Connect(tcpConnectionIDText, onRecieveMessage);
                    myUDPDevice.Disconnect();

                    break;
            }

        }
    };

    //--------------------------  Get Connection ID  -----------------------------------------------
    private String GetConnectionID(String addr, String port)
    {
        return "tcp:/" + addr + ":" + port + "/";
    }

    //----------------------------  Корректировка пустых имен  -------------------------------------
    private String NameCorrecting(String name)
    {
        String res = name;
        if (name.equals(""))
        {
            res = getResources().getString(R.string.unnamed_text) + " " + (teamList.size() + 1);
        }
        return res;
    }

    //*****************************  ДОБАВИТЬ НОВУЮ КОМАНДУ  ***************************************
    private void AddNewTeam(String name, int score)
    {
        String newName = name;
        TeamFieldViewClass team = new TeamFieldViewClass(this);
        team.setTeamName(newName);
        team.setScore(score);
        registerForContextMenu(team);
        teamList.add(team);
        team.addScoreChangedEventListener(scel);

        // Создание LayoutParams c шириной и высотой по содержимому
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llMain.addView(team, lParams);

    }

    //*****************************  УДАЛИТЬ ТЕКУЩУЮ КОМАНДУ  **************************************
    private void DeleteTeam(TeamFieldViewClass team)
    {
        team.removeCScoreChangedEventListener(scel);
        llMain.removeView(team);
        teamList.remove(team);
    }

    //*****************************  SCORE LISTENER  ***********************************************
    ScoreChangedEventListener scel = new ScoreChangedEventListener()
    {
        @Override
        public void ScoreChanged(ScoreChangedEvent e) {
            ColorSort();
            //ConsoleMessageOutputClass.ConsoleWriteLine(e.getTeamName() + " - " + e.getScore());
            SendOneTeam(e.getTeamName(), e.getScore());
        }
    };

    //***************************  ПЕРЕИМЕНОВАТЬ ТЕКУЩУЮ КОМАНДУ  **********************************
    private void RenameTeam(TeamFieldViewClass team, String name)
    {
        team.setTeamName(name);
    }

    //***************************  СБРОСИТЬ РЕЗУЛЬТАТ КОМАНДЫ  *************************************
    private void ResetTeamScore(TeamFieldViewClass team)
    {
        team.setScore(0);
    }

    //**************************  ЦВЕТОВАЯ ГРАДАЦИЯ  ***********************************************
    private void ColorSort()
    {
        if (teamList.size() > 0)
        {
            int max = teamList.get(0).getScore();
            int min = max;

            int i = 1;
            while(i < teamList.size())
            {
                if (teamList.get(i).getScore() > max) max = teamList.get(i).getScore();
                if (teamList.get(i).getScore() < min) min = teamList.get(i).getScore();
                i++;
            }

            for (i = 0; i < teamList.size(); i++)
            {
                if (teamList.get(i).getScore() == max) teamList.get(i).setColor(TeamFieldViewClass.HIGH_SCORE);
                else if ((min < max)&&(teamList.get(i).getScore() == min)) teamList.get(i).setColor(TeamFieldViewClass.LOW_SCORE);
                else teamList.get(i).setColor(TeamFieldViewClass.MID_SCORE);
            }
        }
    }

    //**************************  СОРТИРОВКА ПО ВОЗРАСТАНИЮ  ***************************************
    private void Sort()
    {
        int count = teamList.size();
        for (int i = 0; i < count; i++)
        {
            llMain.removeView(teamList.get(i));
        }

        Collections.sort(teamList, TeamFieldViewClass.COMPARE_BY_SCORE);

        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < count; i++)
        {
            // Создание LayoutParams c шириной и высотой по содержимому

            llMain.addView(teamList.get(i), lParams);
        }
    }

    //******************************  НУМЕРАЦИЯ  ***************************************************
    private void RefreshNumeration()
    {
        int count = teamList.size();
        for (int i = 0; i < count; i++)
        {
            teamList.get(i).setNumber(i + 1);
        }
    }
}
