package com.example.webrtcsignaling;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;

/**
 * @author wonderful
 * @date 2020-7-?
 * @version 1.0
 * @description 信令测试webSocket客户端
 * @license Apache License 2.0
 */
public class SignalingWebSocketClient extends WebSocketClient {

    private static final String TAG = "SignalingWebSocket";
    private Context context;

    public SignalingWebSocketClient(URI serverUri,Context context) {
        super(serverUri);
        this.context = context;
        Log.d(TAG, "webSocketClient 实例被创建");
        showMessage("webSocketClient 实例被创建");
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d(TAG, "onOpen: ");
        showMessage("onOpen: ");
    }

    @Override
    public void onMessage(String message) {
        Log.d(TAG, "onMessage: " + message);
        showMessage("onMessage: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d(TAG, "onClose-reason: " + reason);
        showMessage("onClose-reason: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        Log.d(TAG, "onError: " + ex.getMessage());
        showMessage("onError: " + ex.getMessage());
    }

    private void showMessage(final String message){
        new Handler(context.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
