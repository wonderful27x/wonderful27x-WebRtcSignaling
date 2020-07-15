package webrtc.signaling.model;

import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import webrtc.signaling.type.MessageType;

/**
 * @author wonderful
 * @date 2020-7-?
 * @version 1.0
 * @description 消息实体，通过他的一参构造函数能直接将一个json数据转换得到消息类型messageType，
 * 而transForm方法能根据指定的泛型转换成具体的消息类型，这是核心方法
 * @license Apache License 2.0
 */
public class Message extends BaseMessage<String,String>{

    public Message(){}

    public Message(String json){
        try {
            Gson gson = new Gson();
            JSONObject jsonObject = new JSONObject(json);
            //先统一转成String
            String messageType = jsonObject.getString("messageType");
            String message = jsonObject.getString("message");
            String extra = jsonObject.getString("extra");
            //MessageType是固定的直接转成对象，message和extra是动态变化的保留String类型
            setMessageType(gson.fromJson(messageType, MessageType.class));
            setMessage(message);
            setExtra(extra);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //调用此函数，通过传递进来的泛型，将message和Extra转成指定泛型的类型
    public <T,E> BaseMessage<T,E> transForm(BaseMessage<T,E> baseMessage){
        Gson gson = new Gson();
        //先获取泛型实际类型
        Type[] types = getClassType(baseMessage.getClass());
        //通过Gson转换
        T message = gson.fromJson(getMessage(),types[0]);
        E extra = gson.fromJson(getExtra(),types[1]);
        //赋值并返回
        baseMessage.setMessageType(getMessageType());
        baseMessage.setMessage(message);
        baseMessage.setExtra(extra);

        return baseMessage;
    }
}
