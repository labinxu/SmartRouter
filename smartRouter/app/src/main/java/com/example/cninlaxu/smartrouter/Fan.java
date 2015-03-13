package com.example.cninlaxu.smartrouter;

import android.os.Bundle;

/**
 * Created by cninlaxu on 2015/3/2.
 */
public class Fan extends DeviceData {

    public Fan(byte[] binaryData)
    {
        super(binaryData);
    }
    @Override
    public String getDeviceDescribe()
    {

        //deviceIndex[]
        int index = (((int)deviceBinData[2])<<8)+ ((int)deviceBinData[3]);
        return "Fan "+index;
    }
    public String getParam1()
    {
        byte[] param = new byte[4];
        System.arraycopy(deviceBinData,9,param,0, 4);
        return String.valueOf(Utils.byteArray2int(param));
    }
    @Override
    public String getDeviceStatus()
    {
        if (deviceBinData[8]== 0x01){
            return "On";
        }
        else if (deviceBinData[8] == 0x03)
        {
            return "Timer";
        }
        else{
            return "Off";
        }
    }
    public byte[] update(Bundle bundle)
    {
        String deviceFunctions = bundle.get("deviceFunctions").toString();
        if (deviceFunctions.equals("On"))
        {
            deviceOn();
            String level = bundle.get("deviceParam1").toString();
            String timer = bundle.get("deviceParam2").toString();
            updateLevel(level);
            updateTimer(timer);
        }
        else if (deviceFunctions.equals("Off"))
        {
            deviceOff();
        }
        return getBinCode();
    }

    public byte[] updateLevel(String level){
        int iLevel=Integer.parseInt(level);
        byte[] var = Utils.int2ByteArray(iLevel);
        System.arraycopy(var,0, deviceBinData, 9, 4);
        return deviceBinData;
    }

    public byte[] deviceOn()
    {
        deviceBinData[8] = 0x01;
        String level = getParam1();
        if (level.equals("0"))
        {
            updateLevel("1");
        }
        return deviceBinData;
    }
    public byte[] updateTimer(String timer){
        // minutes
        int iinterval = Integer.parseInt(timer);
        // ms
        int macroSeconds = iinterval * 60 * 1000;
        byte[] cinterval = Utils.int2ByteArray(macroSeconds);
        System.arraycopy(cinterval, 0, deviceBinData, 13, 4);
        return deviceBinData;
    }

}
