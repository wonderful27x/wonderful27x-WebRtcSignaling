package com.example.webrtcsignaling;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author wonderful
 * @date 2020-7-?
 * @version 1.0
 * @description 信令测试
 * @license Apache License 2.0
 */
public class SignalingTestActivity extends AppCompatActivity {

    private Button signalingTest;
    private Button close;
    private SignalingWebSocketClient webSocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signaling_test);
        signalingTest = findViewById(R.id.signalingTest);
        close = findViewById(R.id.close);

        signalingTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signalingTest();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webSocketClient.close();
            }
        });
    }

    private void signalingTest(){
        String socketUri = "ws://172.16.164.82:8080/webRtcSignalingService/webRtcSignaling/0/1";
        URI uri = null;
        try {
            uri = new URI(socketUri);
            webSocketClient = new SignalingWebSocketClient(uri,this);
            webSocketClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}