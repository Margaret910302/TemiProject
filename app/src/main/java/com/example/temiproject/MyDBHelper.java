package com.example.temiproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

// 繼承SQLiteOpenHelper建立自己的資料庫幫助類別
public class MyDBHelper extends SQLiteOpenHelper {
    //資料庫幫助類別建構子
    public MyDBHelper(@Nullable Context context,
                      @Nullable String dbname,
                      @Nullable SQLiteDatabase.CursorFactory cursor_factory,
                      int db_version) {
        super(context, dbname, cursor_factory, db_version); // 執行父類別(SQLiteOpenHelper)之建構子
    }
    @Override // 複寫onCreate方法，在建立資料庫時，就會執行這個方法
    public void onCreate(SQLiteDatabase db) {

    }

    @Override // 複寫onUpgrade方法，在資料庫有新版本時(DATABASE_VERSION有增加時)，就執行此方法
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
