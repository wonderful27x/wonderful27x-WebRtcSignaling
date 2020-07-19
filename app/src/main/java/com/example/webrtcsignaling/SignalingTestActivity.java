package com.example.webrtcsignaling;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.net.URI;
import java.net.URISyntaxException;

import webrtc.signaling.model.BaseMessage;
import webrtc.signaling.model.NegotiationMessage;
import webrtc.signaling.type.MessageType;
import webrtc.signaling.type.RoomType;

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
    private Button join;
    private SignalingWebSocketClient webSocketClient;
    private String userId = "wonderful123456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signaling_test);
        signalingTest = findViewById(R.id.signalingTest);
        close = findViewById(R.id.close);
        join = findViewById(R.id.join);

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
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                join();
            }
        });
    }

    private void signalingTest(){
        String socketUri = "ws://192.168.0.103:8080/webRtcSignalingService/webRtcSignaling/" + userId + "/1";
        URI uri = null;
        try {
            uri = new URI(socketUri);
            webSocketClient = new SignalingWebSocketClient(uri,this);
            webSocketClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void join(){
        BaseMessage<NegotiationMessage,Object> baseMessage = new BaseMessage<NegotiationMessage, Object>() {};
        NegotiationMessage message = new NegotiationMessage();
        message.userId = userId;
        message.roomType = RoomType.MEETING;
        message.roomId = "1234567";
        baseMessage.setMessage(message);
        baseMessage.setMessageType(MessageType.JOIN);
        String jsonData = baseMessage.toJson();
        webSocketClient.send(jsonData);
    }
}