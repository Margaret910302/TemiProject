package com.example.temiproject;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class Login extends AppCompatActivity {

    //資料庫查詢會用到
    public static String ID;
    public static String NAME;

    // 與建立資料庫有關之變數宣告
    private static final String DATABASE_NAME = "TemiRobot.db";   // 建立"資料庫名稱"常數
    private static final int DATABASE_VERSION = 1;                // 建立"資料庫版本"常數
    private static String DATABASE_TABLE1 = "user_id";            // 宣告"身分證資料表"常數
    private static String DATABASE_TABLE2 = "user";               // 宣告"使用者資料表"常數
    private static String DATABASE_TABLE3 = "sports";             // 宣告"運動種類資料表"常數
    private static String DATABASE_TABLE4 = "sports_message";     // 宣告"運動紀錄資料表"常數
    private SQLiteDatabase db;    // 宣告資料庫變數
    private MyDBHelper dbHelper;  // 宣告資料庫幫助器變數

    // 其他變數宣告
    String [] SportsTypes = { "向下甩手", "搖頭擺尾", "輕提腳跟", "轉體運動", "雙手托天", "蹲下起立"}; //初始運動種類陣列
    Cursor cursor;
    int n;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        Stetho.initializeWithDefaults(this);

        TextView username = (TextView) findViewById(R.id.username);
        TextView password = (TextView) findViewById(R.id.password);

        MaterialButton loginbtn = (MaterialButton) findViewById(R.id.loginbtn);

        dbHelper = new MyDBHelper(getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION );
        db = dbHelper.getWritableDatabase();

        // 檢查資料表DATABASE_TABLE1(身分證資料表)是否已經存在，如果不存在，就建立一個。
        cursor = db.rawQuery(
                "select DISTINCT tbl_name from sqlite_master where tbl_name = '" +
                        DATABASE_TABLE1 + "'", null);
        if(cursor != null)
        {
            if(cursor.getCount() == 0)	// 沒有資料表，要建立一個資料表。
            {
                // 執行SQL建立資料表指令， 建立身分證資料表(user_id)，此資料表有以下 3 個欄位:
                // _id(編號): 整數，為主鍵，數值會自動增加
                // ID(身分證): 字串(長度最大10)，不可為null(空白)
                // name(姓名): 字串(長度最大5)，不可為null(空白)
                db.execSQL("CREATE TABLE user_id (_id integer primary key autoincrement, "
                        + "ID nvarchar(10) not null, name nvarchar(5) not null)");
            }

        }

        // 檢查資料表DATABASE_TABLE2(使用者資料表)是否已經存在，如果不存在，就建立一個。
        cursor = db.rawQuery(
                "select DISTINCT tbl_name from sqlite_master where tbl_name = '" +
                        DATABASE_TABLE2 + "'", null);
        if(cursor != null)
        {
            if(cursor.getCount() == 0)	// 沒有資料表，要建立一個資料表。
            {
                // 執行SQL建立資料表指令， 建立使用者資料表(user)，此資料表有以下 5 個欄位:
                // _id(編號): 整數，為主鍵，數值會自動增加
                // name(姓名): 字串(長度最大5)，不可為null(空白)
                // gender(性別): 字串(長度最大1)，不可為null(空白)
                // birthday(生日): Date，不可為null(空白)
                // user_id(身分證): 字串(長度最大10)，不可為null(空白)
                db.execSQL("CREATE TABLE user (_id integer primary key autoincrement, "
                        + "name nvarch(5) not null,"
                        + "gender nvarch(1) not null,"
                        + "birthday date not null,"
                        + "user_id nvarch(10) not null)");
            }
        }

        // 檢查資料表DATABASE_TABLE3(運動種類資料表)是否已經存在，如果不存在，就建立一個。
        cursor = db.rawQuery(
                "select DISTINCT tbl_name from sqlite_master where tbl_name = '" +
                        DATABASE_TABLE3 + "'", null);
        if(cursor != null)
        {
            if(cursor.getCount() == 0)	// 沒有資料表，要建立一個資料表。
            {
                // 執行SQL建立資料表指令， 建立運動種類資料表(sports)，此資料表有以下 2 個欄位:
                // _id(編號): 整數，為主鍵，數值會自動增加
                // sportsType(運動種類): 字串(長度最大4)，不可為null(空白)
                db.execSQL("CREATE TABLE sports (_id integer primary key autoincrement, "
                        + "sportsType nvarch(4) not null)");
            }
        }

        // 檢查資料表DATABASE_TABLE4(運動紀錄資料表)是否已經存在，如果不存在，就建立一個。
        cursor = db.rawQuery(
                "select DISTINCT tbl_name from sqlite_master where tbl_name = '" +
                        DATABASE_TABLE4 + "'", null);
        if(cursor != null)
        {
            if(cursor.getCount() == 0)	// 沒有資料表，要建立一個資料表。
            {
                // 執行SQL建立資料表指令， 建立運動紀錄資料表(sports_message)，此資料表有以下 7 個欄位:
                // _id(編號): 整數，為主鍵，數值會自動增加
                // name(姓名): 字串(長度最大5)，不可為null(空白)
                // sportsType(運動種類):字串(長度最大4)，不可為null(空白)
                // calories(卡路里): 浮點數，不可為null(空白)              暫時拿掉
                // timeLong(運動時長): 整數，不可為null(空白)              暫時拿掉
                // date(運動日期): date，不可為null(空白)
                // remark(備註): 字串(長度最大20)                         暫時拿掉
//                db.execSQL("CREATE TABLE sports_message (_id integer primary key autoincrement, "
//                        + "name nvarch(5) not null,"
//                        + "sportsType nvarch(4) not null,"
//                        + "calories decimal(10,2) not null,"
//                        + "timeLong integer not null,"
//                        + "date date not null,"
//                        + "remark nvarch(20))");

                db.execSQL("CREATE TABLE sports_message (_id integer primary key autoincrement, "
                        + "name nvarch(5) not null,"
                        + "sportsType nvarch(4) not null,"
                        + "times integer not null,"
                        + "date date not null)");
            }
        }

        // 若是資料表DATABASE_TABLE3中沒有任何消費種類，則在該資料表中建立預設的消費種類
        cursor = db.rawQuery("SELECT * FROM " + DATABASE_TABLE3, null);
        if(cursor!=null)
        {
            n = cursor.getCount();//取得運動種類記錄筆數
            if(n==0) //若沒有運動種類，則將初始消費種類存入資料表DATABASE_TABLE3中
            {
                for (int i=0; i<SportsTypes.length; i++)
                    db.execSQL("INSERT INTO " + DATABASE_TABLE3 + " (sportsType) VALUES ('" +
                            SportsTypes[i] +  "')");
            }
        }

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    String commandString = "SELECT * FROM " + DATABASE_TABLE1 + " ORDER BY _id";
                    Cursor cursor = db.rawQuery(commandString, null);

                    if(cursor != null)
                    {
                        boolean isPass = false;
                        int n = cursor.getCount();
                        cursor.moveToFirst();
                        for (int i = 0; i < n; i++) {
                            String pass = cursor.getString(1);
                            String name = cursor.getString(2);
                            if (username.getText().toString().equals(name) && password.getText().toString().equals(pass)) {
                                ID = pass;
                                NAME = name;
                                isPass = true;
                            }
                            cursor.moveToNext();
                        }
                        if(isPass) {
                            Toast.makeText(Login.this,"登錄成功!!!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(Login.this,"登錄失敗!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else { // 若沒有消費紀錄，則顯示並沒有消費紀錄
                        Toast.makeText(Login.this, "沒有使用者紀錄", Toast.LENGTH_LONG).show();
                    }
                }
                catch(Exception ex)
                {
                    Toast.makeText(Login.this,"Login.java第42行的try程式出現例外： " + ex.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    // 當Activity停止時關閉database
    @Override
    protected void onStop()
    {
        super.onStop();
        db.close();
    }

    // "註冊"按鈕之點擊事件處理方法，啟動註冊Activity
    public void btnRegister_Click(View view)
    {
        try
        {
            Intent intent = new Intent(this, Register.class);
            startActivity(intent);
        }
        catch(Exception ex)
        {
            Toast.makeText(Login.this,"Login.java第93行的try程式出現例外： " + ex.getMessage(), Toast.LENGTH_LONG).show();
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