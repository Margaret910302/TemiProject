package com.example.temiproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ChooseSports extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_sports);

        Button button1 = findViewById(R.id.向下甩手);
        Button button2 = findViewById(R.id.搖頭擺尾);
        Button button3 = findViewById(R.id.清提腳跟);
        Button button4 = findViewById(R.id.轉體運動);
        Button button5 = findViewById(R.id.雙手托天);
        Button button6 = findViewById(R.id.蹲下起立);
        Button button7 = findViewById(R.id.confirmbtn);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 處理按鈕1的點擊事件
                Log.d("ChooseSports", "btn_handDown clicked");
                jumpToCamera("向下甩手");
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 處理按鈕2的點擊事件
                Log.d("ChooseSports", "btn_shakeHead clicked");
                jumpToCamera("搖頭擺尾");
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 處理按鈕3的點擊事件
                Log.d("ChooseSports", "btn_tiptoe clicked");
                jumpToCamera("輕提腳跟");
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 處理按鈕4的點擊事件
                Log.d("ChooseSports", "btn_rotateBody clicked");
                jumpToCamera("轉體運動");
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 處理按鈕5的點擊事件
                Log.d("ChooseSports", "btn_handUp clicked");
                jumpToCamera("雙手托天");
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 處理按鈕5的點擊事件
                Log.d("ChooseSports", "btn_squatDown clicked");
                jumpToCamera("蹲下起立");
            }
        });

        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 處理按鈕5的點擊事件
                Intent intent = new Intent(ChooseSports.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void jumpToCamera(String exerciseName) {
        Intent intent = new Intent(ChooseSports.this, StartSports.class);
        intent.putExtra("chooseExerciseName", exerciseName);
        startActivity(intent);
    }

}
