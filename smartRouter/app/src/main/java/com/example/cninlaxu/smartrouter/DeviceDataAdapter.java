package com.example.cninlaxu.smartrouter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by cninlaxu on 2015/2/26.
 */
public class DeviceDataAdapter extends BaseAdapter {

    public Context context;
    public DeviceDataAdapter(Context context, ArrayList<DeviceData> devices ){
        this.context = context;
        inflater = LayoutInflater.from(context);
        data = new ArrayList<HashMap<String, String>>();
    }

    public DeviceDataAdapter(Context context)
    {
        this.context = context;
        inflater = LayoutInflater.from(context);
        data = new ArrayList<HashMap<String, String>>();
    }

    protected void initData(ArrayList<DeviceData> deviceList){
        Iterator it = deviceList.iterator();
        while (it.hasNext())
        {
            DeviceData device = (DeviceData)it.next();
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("deviceDescribe", device.getDeviceDescribe());
            map.put("deviceStatus", device.getDeviceStatus());
            data.add(map);
        }
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ItemHolder holder;
        if(convertView == null){
            holder = new ItemHolder();
            convertView = inflater.inflate(R.layout.device_item, null);
            holder.deviceDescribe = (TextView)convertView.findViewById(R.id.device_item_name);
            holder.deviceStatus = (ImageButton)convertView.findViewById(R.id.device_item_status);
            //holder.context = this.context;
            convertView.setTag(holder);
        }
        else
        {
            holder = (ItemHolder)convertView.getTag();
        }
        HashMap<String, String> mapitem = data.get(position);
        holder.deviceDescribe.setText(mapitem.get("deviceDescribe"));
        holder.deviceIdentity = mapitem.get("type") + mapitem.get("index");
        holder.deviceFunctions = mapitem.get("deviceStatus");
        holder.deviceParam1 = mapitem.get("deviceParam1");
        holder.deviceParam2 = mapitem.get("deviceParam2");
        if ( holder.deviceFunctions == "On"){
            holder.deviceStatus.setImageResource(R.drawable.light_on);
        }
        else if(holder.deviceFunctions == "Flash"){
            holder.deviceStatus.setImageResource(R.drawable.light_flash);
        }
        else {
            holder.deviceStatus.setImageResource(R.drawable.light_off);
        }

        holder.deviceStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)DeviceDataAdapter.this.context).itemButtonClicked(holder.deviceIdentity);
            }
        });
        return convertView;
    }

    public void addItem(DeviceData dvData){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("type", dvData.getDeviceType());
        map.put("index",dvData.getIndex());
        map.put("deviceDescribe", dvData.getDeviceDescribe());
        map.put("deviceStatus", dvData.getDeviceStatus());
        map.put("deviceParam1", dvData.getParam1());
        map.put("deviceParam2", dvData.getParam2());
        data.add(map);
        notifyDataSetChanged();
    }
    public void clear()
    {
        data.clear();
        notifyDataSetChanged();

    }
    public void change(DeviceData dvData)
    {
        Iterator it = data.iterator();
        while (it.hasNext())
        {
            HashMap<String,Object> dvd = (HashMap<String, Object>)it.next();
            if(dvd.get("type") == dvData.getDeviceType()
                    && dvd.get("index")== dvData.getIndex())
            {
                dvd.put("deviceStatus", dvData.getDeviceStatus());
                dvd.put("deviceParam1", dvData.getParam1());
                dvd.put("deviceParam2", dvData.getParam2());
               // Log.d("handleMsg", "update dv "+dvData.getIndex()+" "+dvData.getDeviceStatus());
            }
        }
        //
        notifyDataSetChanged();
    }
    public final class ItemHolder{
        //public Context context;
        public String deviceIdentity;
        public TextView deviceDescribe;
        public ImageButton deviceStatus;
        public String deviceFunctions;
        public String deviceParam1;
        public String deviceParam2;
    };
    private LayoutInflater inflater;
    public ArrayList<HashMap<String, String>> data;


}
