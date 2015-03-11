package com.example.cninlaxu.smartrouter;

/**
 * Created by cninlaxu on 2015/3/3.
 */
public class Utils {

    /**
     * 将4字节的byte数组转成一个int值
     * @param b
     * @return
     */
    public static int charArray2int(char[] b){
        char[] a = new char[4];
        int i = a.length - 1,j = b.length - 1;
        for (; i >= 0; i--,j--) {//从b的尾部(即int值的低位)开始copy数据
            if(j >= 0)
                a[i] = b[j];
            else
                a[i] = 0;//如果b.length不足4,则将高位补0
        }
        int v0 = (a[0] & 0xff) << 24;//&0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位
        int v1 = (a[1] & 0xff) << 16;
        int v2 = (a[2] & 0xff) << 8;
        int v3 = (a[3] & 0xff) ;
        return v0 + v1 + v2 + v3;
    }
    public static int byteArray2int(byte[] b){
        byte[] a = new byte[4];
        int i = a.length - 1,j = b.length - 1;
        for (; i >= 0; i--,j--) {//从b的尾部(即int值的低位)开始copy数据
            if(j >= 0)
                a[i] = b[j];
            else
                a[i] = 0;//如果b.length不足4,则将高位补0
        }
        int v0 = (a[0] & 0xff) << 24;//&0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位
        int v1 = (a[1] & 0xff) << 16;
        int v2 = (a[2] & 0xff) << 8;
        int v3 = (a[3] & 0xff) ;
        return v0 + v1 + v2 + v3;
    }

    public static char[] byteArray2charArray(byte[] b)
    {
        char [] a = new char[b.length];
        for(int i=0; i< b.length; i++)
        {
            a[i]=(char)b[i];
        }
        return a;
    }
    public static byte[] int2ByteArray(int interval)
    {

        byte[] b=new byte[4];
        //向右移位是   低位舍弃，高位补符号位
        //向右移位运算，移动24位后，高8位，被移动到低8位上，二、三、四组都会被丢弃
        b[0]=(byte) (interval>>24);
        //向右移动16位，高16位到低16位地方，第三、四组会被舍弃，至于&0xff这里不容易看出来，b[3]那一行能看出来
        b[1]=(byte) ((interval>>16)&0xff);
        //移动8位第四组会被丢弃，结果还是0
        b[2]=(byte) ((interval>>8)&0xff);
        b[3]=(byte)((interval&0xff));
        return b;
    }
    public static char[] int2charArray(int num) {
        return byteArray2charArray(int2ByteArray(num));
    }


}
