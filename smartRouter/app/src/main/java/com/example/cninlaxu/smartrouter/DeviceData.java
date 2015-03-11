package com.example.cninlaxu.smartrouter;

import android.os.Bundle;
import android.util.Log;

/**
 * Created by cninlaxu on 2015/2/26.
 */
public class DeviceData {
    protected byte[] deviceBinData=null;
    public boolean equalsTo(DeviceData device){
        for (int i =0; i < 4; i++)
        {
            if(deviceBinData[i] != device.deviceBinData[i]){
                return false;
            }
        }
        return true;
    }
    public static int bin2oct(byte[] deviceIndex)
    {
        int index = (((int)deviceIndex[0])<<8)+(((int)deviceIndex[1]));
        return index;
    }
    public String getParam1()
    {
        byte[] param = new byte[4];
        System.arraycopy(deviceBinData,9,param,0, 4);
        return String.valueOf(Utils.byteArray2int(param)/1000);
    }

    public String getParam2()
    {
        byte[] param = new byte[4];
        System.arraycopy(deviceBinData,13,param,0, 4);
        return String.valueOf(Utils.byteArray2int(param)/60000);
    }
    public void resetOrient(){
        deviceBinData[4]=0x01;
    }
    public byte[] getBinCode()
    {
        return deviceBinData;
    }
    public String getIdentify(){
        return getDeviceType() + getIndex();
    }

    public String getIndex()
    {
        byte[] tmpdata = new byte[2];
        tmpdata[0] = deviceBinData[2];
        tmpdata[1] = deviceBinData[3];
        return String.valueOf(bin2oct(tmpdata));
    }
    public String getDeviceType()
    {
        byte[] tmpdata= new byte[2];
        tmpdata[0] = deviceBinData[0];
        tmpdata[1] = deviceBinData[1];
        return String.valueOf(bin2oct(tmpdata));
    }
    public String getDeviceDescribe()
    {
        return "";
    }
    public String getDeviceStatus()
    {

        return "";
    }
    public byte[] update(Bundle bundle)
    {
        return null;
    }
    public static String formatHex(String var)
    {
        String pholder = "";
        int phsize = 4-var.length();
        while (phsize>0)
        {
            phsize--;
            pholder += "0";
        }
        pholder += var;
        return pholder;
    }

    public String formatBinCode() {
        String logmsg = "";
        for (int i = 0; i < deviceBinData.length; i++) {
            String var = Integer.toHexString((int) deviceBinData[i]);
            if (var.length() < 2) {
                logmsg += "0";
            }
            logmsg += var;
            logmsg += " ";
        }
        return logmsg;
    }
    public DeviceData(byte[] data)
    {
        deviceBinData = data;
    }

};