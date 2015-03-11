package com.example.cninlaxu.smartrouter;

import android.os.Bundle;
import android.util.Log;

import java.nio.charset.Charset;

public class LEDLight extends DeviceData
{
    public LEDLight(byte[] binaryData){
        super(binaryData);
    }
    @Override
    public String getDeviceDescribe()
    {

        //deviceIndex[]
        int index = (((int)deviceBinData[2])<<8)+ ((int)deviceBinData[3]);
        return "Light "+index;
    }
    @Override
    public String getDeviceStatus()
    {
        if (deviceBinData[8]== 0x01){
            return "On";
        }
        else if(deviceBinData[8]==0x03){
            return "Flash";
        }
        else{
            return "Off";
        }
    }

    public byte[] switchStatus()
    {
        if (deviceBinData[8]== 0x01) {
            return lightOff();
        }
        /*else if (deviceFunctions[3]==0x02)
        {
            return lightFlash();
        }*/
        return lightOn();
    }
    private byte[] lightOn(){

        if(deviceBinData==null){
            return null;
        }
        deviceBinData[8] = 0x01;
        return deviceBinData;
    }

    private byte[] lightOff()
    {
        if(deviceBinData==null){
            return null;
        }
        deviceBinData[8] = 0x02;
        return deviceBinData;
    }
    private byte[] lightFlash()
    {
        if(deviceBinData==null){
            return null;
        }
        deviceBinData[8] = 0x03;
        return deviceBinData;
    }
    private String lightFlashContinuous(String continueTime){
        // minutes
        int iinterval = Integer.parseInt(continueTime);
        // ms
        int macroSeconds = iinterval * 60 * 1000;
        byte[] cinterval = Utils.int2ByteArray(macroSeconds);
        System.arraycopy(cinterval, 0, deviceBinData, 13, 4);
        return new String(deviceBinData);
    }
    private String lightFlashInterval(String interval){
        //9-12
        int iinterval = Integer.parseInt(interval);
        // macros seconds
        int marcoSeconds = iinterval * 1000;
        byte[] var = Utils.int2ByteArray(marcoSeconds);
        System.arraycopy(var,0, deviceBinData, 9, 4);
        return new String(deviceBinData);
    }
    public byte[] update(Bundle bundle)
    {
        String deviceFunctions = bundle.get("deviceFunctions").toString();
        if (deviceFunctions.equals("Flash"))
        {
            this.lightFlash();
            String deviceFlashInterval = bundle.get("deviceParam1").toString();
            String deviceFlashContinuous = bundle.get("deviceParam2").toString();
            lightFlashContinuous(deviceFlashContinuous);
            lightFlashInterval(deviceFlashInterval);
        }
        else if (deviceFunctions.equals("On"))
        {
            lightOn();
        }
        else if (deviceFunctions.equals("Off"))
        {
            lightOff();
        }
        return getBinCode();
    }
}
