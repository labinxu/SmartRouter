package com.example.cninlaxu.smartrouter;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * Created by cninlaxu on 2015/2/26.
 */

public class ClientThread implements Runnable{

    private Socket s;
    public Handler handler = null;
    public Handler revHandler = null;
    // handle the data stream that from socket
    //private BufferedReader br = null;
    private BufferedInputStream br = null;
    public String serverIp = null;
    public String port = null;
    public ClientThread(Handler handler){

        this.handler = handler;
        isRunning = false;
    }
    private boolean isRunning;
    public boolean isRun()
    {
        return isRunning;
    }
    @Override
    public void run() {
        s = new Socket();
        try{

            if (serverIp == null) {
                serverIp = "10.0.2.2";
            }
            Log.d("handleMsg",serverIp+" "+port);

            s.connect(new InetSocketAddress(serverIp, Integer.parseInt(port)), 5000);
            isRunning = true;
            // gui show
            //br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            br = new BufferedInputStream(s.getInputStream());
           // final PrintWriter os = new PrintWriter(s.getOutputStream());
            final BufferedOutputStream bo = new BufferedOutputStream(s.getOutputStream());
            // new thread to get the data from server
            new Thread() {
                @Override
                public void run() {
                    String content = null;
                    try{
                        int len = 0;
                        //br.readLine()
                        byte[] buf = new byte[100];
                        while (isRunning && (len = br.read(buf)) != -1)
                        {

                            Log.d("handleMsg", "received len "+len);
                            Message msg = new Message();
                            msg.what = 0x123;
                            byte[] data = null;
                            if (len < 17) {
                                len = 17;

                            }
                            data = new byte[len];
                            for (int i =0; i<len; i++)
                            {
                                data[i]=buf[i];
                            }

                           /*for (int i =0; i< data.length; i++)
                            {

                                Log.d("handleMsg", "---byte len "+String.valueOf(data[i]).getBytes().length+" index: "+i);

                            }*/
                            msg.obj = data;
                            //Log.d("handleMsg", "get byte len "+new String(data).getBytes());
                            // gui show
                            handler.sendMessage(msg);
                        }
                        Message msg = new Message();
                        msg.what = 0x010;
                        msg.obj = String.valueOf("terminal");
                        // gui show
                        handler.sendMessage(msg);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

            // initialize the looper for current thread
            Looper.prepare();

            revHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (!isRun()){
                        return;
                    }
                    // handle gui message
                    if (msg.what == 0x345) {
                        try {
                            Log.d("handleMsg","send:"+((byte[])msg.obj).length);
                            bo.write((byte[])msg.obj);
                            //os.print(msg.obj.toString());
                            //os.print(msg.obj.toString());
                            //os.flush();
                            bo.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    if (msg.what==0x010){
                        try{
                            isRunning = false;
                            s.close();
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }
            };
            Looper.loop();

        }catch (SocketTimeoutException e){
            Message msg = new Message();
            msg.what=0x123;
            msg.obj="connect timeout";
            handler.sendMessage(msg);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}