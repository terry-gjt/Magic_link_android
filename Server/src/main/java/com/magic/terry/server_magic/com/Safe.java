package com.magic.terry.server_magic.com;

/**
 * Created by terry on 2019-01-16.
 */

public class Safe {
    public static String encipher(String s){
        int start = s.indexOf("192.168.");//正常返回0
        if((start!=-1)&&(start+1<s.length())){
            String sPort = s.substring(8);//获取剩余字符串
            start = sPort.indexOf(".");
            if((start!=-1)&&(start+1<sPort.length())) {
                String sIP = sPort.substring(0, start);
                int port1 = Integer.parseInt(sIP);
                port1+=213;
                sIP = sPort.substring(start + 1);
                int port2 = Integer.parseInt(sIP);
                port1+=port2;
                port2+=132;
                s = ""+port1+port2;
            }
        }
        return s;
    }
    public static String getfront(String s){
        String sss="no";
        int start = s.indexOf(".,.");//正常返回4
//        Log.i("线程getfront","start+"+start+"+s.lenth+"+s.length());
        if((start!=-1)&&(start+1<s.length())){
            sss = s.substring(0,start);//获取前面字符串
        }
        return sss;
    }
    public static int getclientnum(String s){
        int port=0;
        int start = s.indexOf(".,.");//正常返回0
        if((start!=-1)&&(start+1<s.length())){
            String sPort = s.substring(start+3);//获取后面字符串
            port = Integer.parseInt(sPort);
        }
        return port;
    }
}
