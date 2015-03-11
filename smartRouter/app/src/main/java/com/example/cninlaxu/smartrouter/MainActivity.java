package com.example.cninlaxu.smartrouter;

import android.app.Activity;
import android.bluetooth.BluetoothClass;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.util.Arrays;
import java.util.HashMap;


public class MainActivity extends Activity {
    private Handler handler;
    private HashMap<String, DeviceData> deviceDataHashMap;
    private ClientThread clientThread;
    private ListView deviceView;
    DeviceDataAdapter adapter;
    EditText message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        deviceDataHashMap = new HashMap<String, DeviceData>();
        // device list view
        ListView deviceView = (ListView)findViewById(R.id.main_device_listview);
        adapter = new DeviceDataAdapter(this);
        deviceView.setAdapter(adapter);
        //message = (EditText)findViewById(R.id.main_message);
        //message.setMovementMethod(ScrollingMovementMethod.getInstance());
       // message.setSelection(message.getText().length(), message.getText().length());
        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg){
                if(msg.what == 0x123){
                    // message from subthread
                    byte[] serverMsg = (byte[])msg.obj;
                    // light
                    if (serverMsg[0] == 0x00 && 0x01 == serverMsg[1])
                    {
                        DeviceData device = handleLight(serverMsg);
                        String identify = device.getIdentify();
                        Log.d("handleMsg", "identity "+identify);
                        //exist
                        if (deviceDataHashMap.get(identify)==null){
                            // insert
                            deviceDataHashMap.put(identify, device);
                            adapter.addItem(device);
                        }
                        else{
                            //update
                            deviceDataHashMap.put(identify, device);
                            adapter.change(device);
                        }
                        //message.append("R:"+device.formatBinCode()+"\n");
                    }
                    else{

                        //message.append(msg.obj.toString());
                    }
                }
                else
                {
                    //message.append(msg.obj.toString());
                }
            }
        };
        //final EditText inputTxt = (EditText)findViewById(R.id.main_server);
        final EditText servIp = (EditText)findViewById(R.id.main_server);
        //servIp.setText("192.168.1.1");
        final EditText servport = (EditText)findViewById(R.id.main_port);
        servport.setText("8120");
        //final EditText logMsgText = (EditText)findViewById(R.id.main_message);
        clientThread = new ClientThread(handler);
        final Button recnnBt = (Button)findViewById(R.id.main_open);
        recnnBt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (clientThread.isRun()){
                    recnnBt.setText("打开");
                    Message msg = new Message();
                    msg.what = 0x010;
                    send(msg);
                    deviceDataHashMap.clear();
                    adapter.clear();
                }
                else {
                    String server = servIp.getText().toString();
                    if (server.length() < 1) {
                        clientThread.serverIp = null;
                    } else {
                        clientThread.serverIp = servIp.getText().toString();
                    }
                    clientThread.port = servport.getText().toString();
                    new Thread(clientThread).start();
                    recnnBt.setText("关闭");
                }
            }
        });


        // Start to connect server
        //new Thread(clientThread).start();



        final Button clearBt = (Button)findViewById(R.id.main_clear);
        final Button sendbt = (Button)findViewById(R.id.main_send);
        sendbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    //Message msg = new Message();
                    //msg.what=0x345;
                    //send(servIp.getText().toString());
                    //msg.obj = inputTxt.getText().toString();
                    //clientThread.revHandler.sendMessage(msg);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        clearBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //logMsgText.setText("");
            }
        });

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.device_item,null);


        deviceView = (ListView)findViewById(R.id.main_device_listview);
        deviceView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, DeviceDetail.class);
                DeviceDataAdapter.ItemHolder holder = (DeviceDataAdapter.ItemHolder)view.getTag();
                intent.putExtra("deviceIndentity", holder.deviceIdentity);
                intent.putExtra("deviceDescribe", holder.deviceDescribe.getText().toString());
                intent.putExtra("deviceFunctions", holder.deviceFunctions);
                intent.putExtra("deviceParam1", holder.deviceParam1);
                intent.putExtra("deviceParam2",holder.deviceParam2);
                startActivityForResult(intent, 1);
                //startActivity(intent);

                //DeviceDataAdapter.ItemHolder item = (DeviceDataAdapter.ItemHolder)view.getTag();
                //LEDLight light = (LEDLight)deviceDataHashMap.get(item.deviceIdentity);
                //send(light.switchStatus());
                //Log.d("handleMsg", "status "+light.getDeviceStatus());

            }
        });
    }
    public void itemButtonClicked(String deviceIdentity){
        LEDLight light = (LEDLight)deviceDataHashMap.get(deviceIdentity);
        light.resetOrient();
        send(light.switchStatus());
        //message.append("S:"+light.formatBinCode()+"\n");
    }
    private void send(byte[] object){
        Message msg = new Message();
        msg.what = 0x345;
        msg.obj = object;
        clientThread.revHandler.sendMessage(msg);
    }
    private void send(Message msg ){
        clientThread.revHandler.sendMessage(msg);
    }
    private DeviceData handleLight(byte[] lightData){
        // index
        LEDLight light = new LEDLight(lightData);
        return light;
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent intent){

        switch (resultCode){
            case RESULT_OK:
                Bundle bundle = intent.getExtras();
                String deviceIndentity = bundle.get("deviceIndentity").toString();
                DeviceData deviceData = deviceDataHashMap.get(deviceIndentity);
                //light
                deviceData.update(bundle);
                deviceData.resetOrient();
               // message.append("S:" + deviceData.formatBinCode() + "\n");
                send(deviceData.getBinCode());
                break;
            case RESULT_CANCELED:
                break;

        }
    }

}
