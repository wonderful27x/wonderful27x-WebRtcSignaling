package com.example.webrtcsignaling;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import webrtc.signaling.model.BaseMessage;
import webrtc.signaling.model.Message;
import webrtc.signaling.type.MessageType;

/**
 * @author wonderful
 * @date 2020-7-?
 * @version 1.0
 * @description Message测试及使用
 * @license Apache License 2.0
 */
public class MessageTestActivity extends AppCompatActivity {

    private Button messageTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_test);
        messageTest = findViewById(R.id.messageTest);

        messageTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void messageTest(){

//        //将对象转成json - 方式一
//        Gson gson = new Gson();
//        TestClass testClass = new TestClass("wonderful",22);
//        MessageType messageType = MessageType.ANSWER;
//        Message message = new Message();
//        message.setMessageType(messageType);
//        message.setMessage(gson.toJson(testClass));
//        message.setExtra("abc");
//        String json = gson.toJson(message);

//        //将对象转成json - 方式二
//        Gson gson = new Gson();
//        TestClass testClass = new TestClass("wonderful",22);
//        MessageType messageType = MessageType.ANSWER;
//        MessageTest message = new MessageTest();
//        message.setMessageType(messageType);
//        message.setMessage(testClass);
//        message.setExtra("abc");
//        String json = gson.toJson(message);

//        //将对象转成json - 方式三
//        //TODO 这种匿名类导致转换后的json为null
//        Gson gson = new Gson();
//        TestClass testClass = new TestClass("wonderful",22);
//        MessageType messageType = MessageType.ANSWER;
//        BaseMessage<TestClass,String> message = new BaseMessage<TestClass, String>() {};
//        message.setMessageType(messageType);
//        message.setMessage(testClass);
//        message.setExtra("abc");
//        String json = gson.toJson(message);

        //将对象转成json - 方式四，推荐方式
        TestClass testClass = new TestClass("wonderful",22);
        MessageType messageType = MessageType.ANSWER;
        BaseMessage<TestClass,String> message = new BaseMessage<TestClass, String>() {};
        message.setMessageType(messageType);
        message.setMessage(testClass);
        message.setExtra("abc");
        String json = message.toJson();

        //将json转成对象需要两步
        //第一步将message和extra先转成String
        Message msg = new Message(json);
        //第二部根据type类型转换成不同的对象
        switch (msg.getMessageType()){
            case COME:
                BaseMessage<String, String> baseMessageA = msg.transForm(new BaseMessage<String, String>() {});
                baseMessageA.getClass();
                break;
            case LEAVE:
                BaseMessage<Integer, String> baseMessageB = msg.transForm(new BaseMessage<Integer, String>() {});
                final String extra = baseMessageB.getExtra();
                break;
            case ANSWER:
                BaseMessage<TestClass, String> baseMessageC = msg.transForm(new BaseMessage<TestClass, String>() {});
                final TestClass messageX = baseMessageC.getMessage();
                break;
        }

        String end;
    }
}