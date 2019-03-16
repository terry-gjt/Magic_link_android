package com.magic.terry.magic_link_all.utils;

import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by terry on 2018-05-20.
 */

public class Filesteam extends Thread{
    @Override
    public void run(){

    }
    private void sendphoto(String fileth){
        try
        {
            File fileok=new File(fileth);
            FileInputStream fis=new FileInputStream(fileok);
            BufferedInputStream bis=new BufferedInputStream(fis,1000000);
            String  filelenth = String.valueOf(fileok.length());
            byte[] filelenthb = filelenth.getBytes();
            byte[] filelenthb2 = new byte[8];
            for (int i = 0; i < filelenthb.length; i++)
            {
                filelenthb2[i] = filelenthb[i];
            }
            String t = new String(filelenthb2);
            String b=t.trim();
            long l = Long.parseLong(b);
//            photoout.write(filelenthb2, 0, 8);//发送长度
            byte[] buf = new byte[102400];
            int len = 0;
            while((len=bis.read(buf))>=0){
//                photoout.write(buf, 0, len);
            }
            bis.close();
            fis.close();
        }
        catch(IOException e) {
            Log.i("线程运行","IO异常,文件不存在或路径有误!");
//            Toast.makeText(getActivity(), "文件不存在或路径有误!", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Log.i("线程运行","其他异常："+e.getMessage());
        }

    }
}
