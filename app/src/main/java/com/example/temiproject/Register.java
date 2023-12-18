package com.example.temiproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.temiproject.inventory.Data;

public class Register extends AppCompatActivity {

    // 與建立資料庫有關之變數宣告
    private static final String DATABASE_NAME = "TemiRobot.db";   // 建立"資料庫名稱"常數
    private static final int DATABASE_VERSION = 1;                // 建立"資料庫版本"常數
    private static String DATABASE_TABLE1 = "user_id";            // 宣告"身分證資料表"常數
    private static String DATABASE_TABLE2 = "user";               // 宣告"使用者資料表"常數
    private SQLiteDatabase db;    // 宣告資料庫變數
    private MyDBHelper dbHelper;  // 宣告資料庫幫助器變數

    // 與圖形介面元件相關之變數宣告
    Button btnConfirm;
    TextView ID, password, name, birthday;
    Spinner gender;
    private Spinner genderTypeList;
    private GenderAdapter adapter;
    String genderStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        genderTypeList = findViewById(R.id.gender);

        adapter = new GenderAdapter(Register.this, Data.getGenderList());
        genderTypeList.setAdapter(adapter);

        try
        {
            btnConfirm = findViewById(R.id.confirmbtn);
            ID = findViewById(R.id.ID);
            password = findViewById(R.id.password);
            name = findViewById(R.id.name);
            gender = findViewById(R.id.gender);
            birthday = findViewById(R.id.birthday);

            gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedItem = parent.getItemAtPosition(position).toString();
                    genderStr = (selectedItem == "男" ? "男" : "女");
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // 如果沒有選擇項目時的處理邏輯
                }
            });

            dbHelper = new MyDBHelper(getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION );
            db = dbHelper.getWritableDatabase();
        }
        catch(Exception ex)
        {
            Toast.makeText(Register.this,"onCreate發生例外，原因如下： " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    // 當Activity停止時關閉database
    @Override
    protected void onStop()
    {
        super.onStop();
        db.close(); // 關閉資料庫
    }

    // 定義一個函數，傳入一個字串，回傳一個布林值
    public boolean checkID(String id) { //A111111113
        // 定義一個字串，儲存所有可能的英文字母
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        // 定義一個陣列，儲存各個英文字母對應的數字
        int[] letterCode = {10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35};
        // 定義一個陣列，儲存各個數字的權重
        int[] weight = {8, 7, 6, 5, 4, 3, 2, 1};
        // 取出字串的第一個字元，並轉換成大寫
        char firstChar = Character.toUpperCase(id.charAt(0));
        // 在字串中尋找第一個字元的位置
        int index = letters.indexOf(firstChar);
        // 檢查位置是否存在
        if (index == -1) return true;
        // 取出第一個字元對應的數字
        int firstNum = letterCode[index];
        // 將數字分成個位數和十位數
        int d1 = firstNum / 10;
        int d2 = firstNum % 10;
        // 初始化總和為 0
        int sum = 0;
        // 將十位數乘以 1，個位數乘以 9，並加到總和中
        sum += d1 * 1 + d2 * 9;
        // 用一個迴圈，從第二碼到第九碼，依序乘以權重，並加到總和中
        for (int i = 0, j = 1; i < 8; i++,j++) {
            // 取出第 i 個字元，並轉換成數字
            int num = Character.getNumericValue(id.charAt(j));
            // 將數字乘以權重，並加到總和中
            sum += num * weight[i];
        }
        // 取出第十碼，並轉換成數字
        int lastNum = Character.getNumericValue(id.charAt(9));
        // 將總和除以 10，取餘數，再用 10 減去餘數，得到檢查碼
        int checkNum = 10 - (sum % 10);
        // 如果檢查碼等於 10，則檢查碼為 0
        if (checkNum == 10) checkNum = 0;
        // 比較檢查碼和第十碼是否相等
        if (checkNum == lastNum) {
            // 如果相等，回傳 true
            return false;
        } else {
            // 如果不相等，回傳 false
            return true;
        }
    }

    // 定義一個函數，傳入一個字串，回傳一個布林值
    public static boolean isBirthdayString(String birthday) { //2002 03 02
        // 檢查字串的長度是否為 8
        if (birthday.length() != 8) return true;
        // 取出前四個字元，並轉換成數字
        int year = Integer.parseInt(birthday.substring(0, 4));
        // 檢查年份是否介於 1900 到 2100 之間
        if (year < 1969 && year > 2100) return true;
        // 取出第五和第六個字元，並轉換成數字
        int month = Integer.parseInt(birthday.substring(4, 6));
        // 檢查月份是否介於 01 到 12 之間
        if (month < 1 && month > 12) return true;
        // 取出第七和第八個字元，並轉換成數字
        int day = Integer.parseInt(birthday.substring(6));
        // 檢查日期是否介於 01 到 28、29、30 或 31 之間，並根據月份和閏年的規則進行調整
        if (day < 1 && day > 31) return true;
        if (month == 2) {
            // 如果是二月，檢查是否是閏年
            boolean isLeapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
            // 如果是閏年，日期不能超過 29
            if (isLeapYear && day > 29) return true;
            // 如果不是閏年，日期不能超過 28
            if (!isLeapYear && day > 28) return true;
        } else if (month == 4 || month == 6 || month == 9 || month == 11) {
            // 如果是 4、6、9 或 11 月，日期不能超過 30
            if (day > 30) return true;
        }
        // 如果以上條件都通過，回傳 true
        return false;
    }

    // "確認"按鈕之點擊事件處理方法，啟動LoginActivity，關閉本Activity，並儲存資料
    public void btnConfirm_Click(View view)
    {
        try
        {
            //儲存資料
            String idString = ID.getText().toString();             // 取得身分證之字串
            String nameString = name.getText().toString();         // 取得姓名之字串
            String birthdayString = birthday.getText().toString(); // 取得生日之字串
            if(birthdayString.length() > 8) {
                birthdayString = birthdayString.replaceAll("/", "");
            }
            // 定義一個正則表達式來匹配身分證字號的格式
            String regex = "^[A-Z][1-2]\\d{8}$";
            // 以下確保使用者有輸入身分證、密碼、姓名、性別及生日
            int empty=0;
            if(idString.length()==0) // 若沒有輸入身分證，則利用吐司訊息提醒使用者
            {
                Toast.makeText(this, "尚未填寫身分證字號!", Toast.LENGTH_LONG).show();
                empty++;
            }
            if(idString.length()!=10) // 檢查身分證字號是否符合正則表達式
            {
                Toast.makeText(this, "身分證字號長度錯誤!", Toast.LENGTH_LONG).show();
                empty++;
            }
            if(checkID(idString)) // 檢查身分證字號是否符合正則表達式
            {
                Toast.makeText(this, "身分證字號格式錯誤!", Toast.LENGTH_LONG).show();
                empty++;
            }
            if (nameString.length()==0) // 若沒有輸入姓名，則利用吐司訊息提醒使用者
            {
                Toast.makeText(this, "尚未填寫姓名!", Toast.LENGTH_LONG).show();
                empty++;
            }
            if (genderStr.length()==0) // 若沒有輸入性別，則利用吐司訊息提醒使用者
            {
                Toast.makeText(this, "尚未填寫性別!", Toast.LENGTH_LONG).show();
                empty++;
            }
            if (birthdayString.length()==0) // 若沒有輸入生日，則利用吐司訊息提醒使用者
            {
                Toast.makeText(this, "尚未填寫生日!", Toast.LENGTH_LONG).show();
                empty++;
            }
            if (isBirthdayString(birthdayString)) // 檢查生日是否符合 xxxx-xx-xx 的格式
            {
                Toast.makeText(this, "生日填寫錯誤!", Toast.LENGTH_LONG).show();
                empty++;
            }
            if (empty==0) // 若使用者有輸入身分證、密碼、姓名、性別及生日，則進行以下處理
            {
                try
                {
                    // 以下為SQL資料插入指令字串： 將(ID, name)兩個值存入user_id中，
                    // 分別要存入到ID,name兩個欄位中
                    String commandString1="INSERT INTO " + DATABASE_TABLE1 + " (ID, name) VALUES " +
                            "('" + idString + "', '" + nameString + "')";
                    db.execSQL(commandString1); //執行SQL資料插入指令

                    // 以下為SQL資料插入指令字串： 將(name, gender, birthday, ID)四個值存入user中，
                    // 分別要存入到name, gender, birthday, ID四個欄位中
                    String commandString2="INSERT INTO " + DATABASE_TABLE2 + " (name, gender, birthday, user_ID) VALUES " +
                            "('" + nameString + "', '" + genderStr + "', " + birthdayString + ", '" + idString + "')";
                    db.execSQL(commandString2);

                    Toast.makeText(this, "成功儲存新的使用者資料!", Toast.LENGTH_LONG).show(); //提示使用者已成功儲存新的消費紀錄

                    //回到"登陸介面"
                    Intent intent = new Intent(this, Login.class);
                    startActivity(intent);
                    finish();
                }
                catch(Exception ex)
                {
                    Toast.makeText(this, "第216行的try程式出現例外: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
        catch(Exception ex)
        {
            Toast.makeText(Register.this,"btnConfirm_Click發生例外，原因如下： " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void btnBack_Click(View view)
    {
        try
        {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
        }
        catch(Exception ex)
        {
            Toast.makeText(Register.this,"Register.java第153行的try程式出現例外： " + ex.getMessage(), Toast.LENGTH_LONG).show();
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