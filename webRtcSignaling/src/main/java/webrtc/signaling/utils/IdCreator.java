package webrtc.signaling.utils;

import webrtc.signaling.annotation_interface.IdFactory;

/**
 * id 产生器,用于产生唯一id
 */
public class IdCreator implements IdFactory {

    private static IdCreator idCreator = null;

    private IdCreator(){}

    public static IdCreator getInstance(){
        if (idCreator == null){
            synchronized (IdCreator.class){
                if (idCreator == null){
                    idCreator = new IdCreator();
                }
            }
        }
        return idCreator;
    }

    @Override
    public String createId(String id) {
        if (id != null && id.length() >5)return id;
        if (id == null)id = "";
        return id + createId();
    }

    private synchronized String createId(){
        long id = System.currentTimeMillis();
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return String.valueOf(id);
    }
}
