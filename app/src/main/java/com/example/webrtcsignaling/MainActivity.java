package com.example.webrtcsignaling;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


/**
 * @author wonderful
 * @date 2020-7-?
 * @version 1.0
 * @description 主Activity，Message测试及使用
 * @license Apache License 2.0
 */
public class MainActivity extends AppCompatActivity {

    private Button messageTest;
    private Button signalingTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messageTest = findViewById(R.id.messageTest);
        signalingTest = findViewById(R.id.signalingTest);

        messageTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,MessageTestActivity.class);
                startActivity(intent);
            }
        });

        signalingTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SignalingTestActivity.class);
                startActivity(intent);
            }
        });
    }
}