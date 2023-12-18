package com.example.temiproject;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // 與建立資料庫有關之變數宣告
    private static final String DATABASE_NAME = "TemiRobot.db";   // 建立"資料庫名稱"常數
    private static final int DATABASE_VERSION = 1;                // 建立"資料庫版本"常數
    private SQLiteDatabase db;   // 宣告資料庫變數
    private MyDBHelper dbHelper; // 宣告資料庫幫助器變數


    // 與圖形介面元件相關之變數宣告
    private Button userDataBtn, sportsDataBtn, startSportsBtn, signoutBtn;      //"用戶資料", "運動資料", "開始運動", "登出"之按鈕變數


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try
        {
            // 取得使用者介面上之所有元件
            userDataBtn = findViewById(R.id.userDataBtn);          //取得"用戶資料"之可編輯文字方塊元件
            sportsDataBtn = findViewById(R.id.sportsDataBtn);      //取得"運動資料"之可編輯文字方塊元件
            startSportsBtn = findViewById(R.id.startSportsBtn);    //取得"開始運動"之下拉式清單元件
            signoutBtn = findViewById(R.id.signoutBtn);            //取得"登出"之按鈕元件


            dbHelper = new MyDBHelper(getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION );
            db = dbHelper.getWritableDatabase();
        }
        catch(Exception ex)
        {
            Toast.makeText(MainActivity.this,"載入GUI元件發生例外，原因如下： " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        db.close();
    }

    // "用戶資料"按鈕之點擊事件處理方法，啟動用戶資料Activity
    public void btnUserData_Click(View view)
    {
        try
        {
            Intent intent = new Intent(this, UserData.class);
            startActivity(intent);
        }
        catch(Exception ex)
        {
            Toast.makeText(MainActivity.this,"btnUserData_Click，原因如下： " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // "運動資料"按鈕之點擊事件處理方法，啟動運動資料Activity
    public void btnSportsData_Click(View view)
    {
        try
        {
            Intent intent = new Intent(this, SportsData.class);
            startActivity(intent);
        }
        catch(Exception ex)
        {
            Toast.makeText(MainActivity.this,"btnSportsData_Click，原因如下： " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    // "開始運動"按鈕之點擊事件處理方法，啟動ChooseSport Activity
    public void btnStartSports_Click(View view)
    {
        try
        {
            Intent intent = new Intent(this, ChooseSports.class);
            startActivity(intent);
        }
        catch(Exception ex)
        {
            Log.d("開始運動", ex.getMessage());   // DEBUG 級別
            Toast.makeText(MainActivity.this,"" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    // "登出"按鈕之點擊事件處理方法，啟動用戶資料Activity，並關閉本Activity
    public void btnLogout_Click(View view)
    {
        try
        {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
        }
        catch(Exception ex)
        {
            Toast.makeText(MainActivity.this,"btnLogout_Click，原因如下： " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //android 返回鍵
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //android Home鍵
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
    }

    //android menu鍵
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_MENU) {
            Toast.makeText(this, "Menu鍵測試", Toast.LENGTH_SHORT).show();
            super.openOptionsMenu(); //跳出menu
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}