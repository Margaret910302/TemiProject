package com.example.temiproject;

import static com.example.temiproject.Register.isBirthdayString;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
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

public class UserData extends AppCompatActivity {

    // 與建立資料庫有關之變數宣告
    private static final String DATABASE_NAME = "TemiRobot.db";   // 建立"資料庫名稱"常數
    private static final int DATABASE_VERSION = 1;                // 建立"資料庫版本"常數
    private static String DATABASE_TABLE1 = "user_id";               // 宣告"使用者資料表"常數
    private static String DATABASE_TABLE2 = "user";               // 宣告"使用者資料表"常數
    private SQLiteDatabase db;    // 宣告資料庫變數
    private MyDBHelper dbHelper;  // 宣告資料庫幫助器變數

    // 與圖形介面元件相關之變數宣告
    private TextView ID, name, birthday;
    private Spinner gender;
    private Spinner genderTypeList;
    private GenderAdapter adapter;
    private Button btnConfirm;      //"確認"之按鈕變數
    String genderStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);

        genderTypeList = findViewById(R.id.gender);

        adapter = new GenderAdapter(UserData.this, Data.getGenderList());
        genderTypeList.setAdapter(adapter);

        try
        {
            // 取得使用者介面上之所有元件
            ID = findViewById(R.id.userid);                 //取得"身分證字號"之可編輯文字方塊元件
            name = findViewById(R.id.name);                //取得"姓名"之可編輯文字方塊元件
            gender = findViewById(R.id.gender);            //取得"性別"之下拉式清單元件
            birthday = findViewById(R.id.birthday);        //取得"生日"之可編輯文字方塊元件
            btnConfirm = findViewById(R.id.confirmbtn);    //取得"確認"之按鈕元件

            dbHelper = new MyDBHelper(getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION );
            db = dbHelper.getWritableDatabase();
            String commandString = "SELECT * FROM " + DATABASE_TABLE2 + " WHERE user_ID = '" + Login.ID + "'";
            Cursor cursor = db.rawQuery(commandString, null); //執行資料表查詢命令
            if(cursor != null){ //如果資料表有資料
                cursor.moveToFirst();
                ID.setText(cursor.getString(4));
                name.setText(cursor.getString(1));
                genderStr = (cursor.getString(2) == "男" ? "男" : "女");
                gender.setSelection(Data.getGenderList().indexOf(genderStr));
                birthday.setText(cursor.getString(3));
                cursor.close();
            }

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
        }
        catch(Exception ex)
        {
            Toast.makeText(UserData.this,"UserData.java第31行發生例外，原因如下： " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        db.close();
    }

    // "確認"按鈕之點擊事件處理方法，啟動用戶資料Activity，並關閉本Activity，並儲存資料
    public void btnConfirm_Click(View view)
    {
        try
        {
            //儲存資料
            String nameString = name.getText().toString();         // 取得姓名之字串
            String birthdayString = birthday.getText().toString(); // 取得生日之字串
            // 以下確保使用者有輸入身分證、密碼、姓名、性別及生日
            int empty=0;
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
                    Login.NAME = nameString;
                    String commandString1="UPDATE " + DATABASE_TABLE1 +
                            " SET " + " name = '" + nameString + "'" +
                            " WHERE" + " ID = '" + Login.ID + "'";
                    db.execSQL(commandString1);
                    // 以下為SQL資料插入指令字串： 將(name, gender, birthday, user_id)兩個值存入user中，
                    // 分別要存入到name, gender, birthday, user_id五個欄位中
                    String commandString2="UPDATE " + DATABASE_TABLE2 +
                            " SET " + " name = '" + nameString + "', gender = '" + genderStr + "',  birthday = " + birthdayString +
                            " WHERE" + " user_id = '" + Login.ID + "'";
                    db.execSQL(commandString2);

                    Toast.makeText(this, "成功更新的使用者資料!", Toast.LENGTH_LONG).show();

                    //回到"主介面"
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                catch(Exception ex)
                {
                    Toast.makeText(this, "UserData.java第103行的try程式出現例外: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
        catch(Exception ex)
        {
            Toast.makeText(UserData.this,"UserData.java第72行發生例外，原因如下： " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

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
            Toast.makeText(UserData.this,"UserData.java第146行的try程式出現例外： " + ex.getMessage(), Toast.LENGTH_LONG).show();
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