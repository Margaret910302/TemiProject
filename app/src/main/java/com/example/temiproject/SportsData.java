package com.example.temiproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SportsData extends AppCompatActivity {

    // 與建立資料庫有關之變數宣告
    private static final String DATABASE_NAME = "TemiRobot.db";   // 建立"資料庫名稱"常數
    private static final int DATABASE_VERSION = 1;                // 建立"資料庫版本"常數
    private static String DATABASE_TABLE1 = "sports_message";     // 宣告"運動紀錄資料表"常數
    private SQLiteDatabase db;    // 宣告資料庫變數
    private MyDBHelper dbHelper;  // 宣告資料庫幫助器變數
    Button btnConfirm;
    TextView txtOutput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports_data);

        try
        {
            // 取得使用者介面上之所有元件
            btnConfirm = findViewById(R.id.confirmbtn);   //取得"確認"之按鈕元件
            txtOutput = findViewById(R.id.txtOutput); //取得"主顯示區"之文字方塊元件

            dbHelper = new MyDBHelper(getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION );
            db = dbHelper.getWritableDatabase();

            // 清除主顯示區域
            txtOutput.setText("");

            // 以下為SQL條件式資料讀取指令字串：從運動紀錄資料表sports_message中讀取介於起始日期與結束日期間的所有運動紀錄，且依照日期先後排列(包含所有欄位)
            String commandString = "SELECT * FROM " + DATABASE_TABLE1 + " WHERE name = '" + Login.NAME + "'";
            Cursor cursor = db.rawQuery(commandString, null); //執行資料表查詢命令
            if(cursor != null) // 若有回傳運動紀錄，則進行以下處理
            {
                int n = cursor.getCount(); //取得資料筆數
                String str = "您共有"+ n + "筆運動紀錄:\n";
                cursor.moveToFirst();  // 將指標移到第1筆紀錄
                // 顯示消費紀錄之每一個欄位之抬頭
//                String[] colNames = {"編號", "姓名", "運動種類", "卡路里", "運動時長", "運動日期", "備註"};
                String[] colNames = {"編號", "姓名", "運動種類", "次數", "日期"};
                for (int i = 0; i < colNames.length; i++)
                    str += String.format("%5s\t", colNames[i]); // 將每一個欄位的抬頭串接到顯示字串(str)中
                str += "\n";

                cursor.moveToFirst();  // 將指標移到第1筆紀錄
                // 顯示欄位值
                for (int i = 0; i < n; i++) //利用迴圈讀取每一筆紀錄之各個欄位
                {
                    str += String.format("%6s\t", (i+1)); // 串接記錄編號(索引值+1)
                    str += String.format("%8s\t", Login.NAME); // 串接第1個欄位值(即姓名)
                    str += String.format("%4s\t", cursor.getString(2)); // 串接第2個欄位值(即運動種類)
                    str += String.format("%7s\t", cursor.getString(3)); // 串接第3個欄位值(即次數)
                    str += String.format("%10s\t", cursor.getString(4));// 串接第4個欄位值(即日期)
                    str += "\n";
                    cursor.moveToNext();  // 移動到下一筆
                }
                txtOutput.setText(str); //將顯示字串的內容顯示在主顯示區文字盒上
            }
            else // 若沒有回傳消費紀錄，則顯示並沒有消費紀錄
            {
                txtOutput.setText("您並沒有運動紀錄!\n");
            }
        }
        catch(Exception ex)
        {
            Toast.makeText(SportsData.this,"SportsData.java第29行發生例外，原因如下： " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        db.close();
    }

    // "返回"按鈕之點擊事件處理方法，啟動用戶資料Activity，並關閉本Activity
    public void btnBack_Click(View view)
    {
        try
        {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        catch(Exception ex)
        {
            Toast.makeText(SportsData.this,"btnBack_Click，原因如下： " + ex.getMessage(), Toast.LENGTH_LONG).show();
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