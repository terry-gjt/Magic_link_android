package com.magic.terry.server_magic.Communication;

/**
 * Created by terry on 2019-01-15.
 */

public class MyMessage {
    private String recvMessageServer []= new String[10];//用于缓存收到的信息
    private int front=0,rear=0;//循环队列指针
    public String getmess(){
        String s=recvMessageServer[front];
        recvMessageServer[front]=null;
        if(front<9)front++;
        else {
            front=0;
        }
        return s;
    }
    public void putmess(String ss){
        if(ss.length()>0){
            recvMessageServer[rear]=ss;//放入消息
            if(rear<9)rear++;
            else {
                rear=0;
            }
        }
    }
}
