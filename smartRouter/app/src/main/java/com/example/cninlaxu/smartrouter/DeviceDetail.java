package com.example.cninlaxu.smartrouter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;


public class DeviceDetail extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);
        final Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();

        final TextView  describeText = (TextView)findViewById(R.id.device_item_name);
        describeText.setText(bundle.get("deviceDescribe").toString());

        final EditText intervalTime = (EditText)findViewById(R.id.device_item_flash_interval_text);
        final SeekBar seekBar = (SeekBar)findViewById(R.id.device_item_flash_interval_seekbar);
        seekBar.setMax(10);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Log.d("handleMsg", "progress" + progress);
                intervalTime.setText(""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        final Switch sswitch = (Switch)findViewById(R.id.device_item_switch);
        final Switch deviceFlashSwitch = (Switch)findViewById(R.id.device_item_flash_switch);
        final String deviceFunctions = bundle.get("deviceFunctions").toString();
        if (deviceFunctions.equals("Off"))
        {
            sswitch.setChecked(false);
            deviceFlashSwitch.setEnabled(false);
        }
        else
        {
            sswitch.setChecked(true);
        }

        //

        final EditText continuousText = (EditText)findViewById(R.id.device_item_flash_continuous_text);
        final SeekBar continuousSeekbar = (SeekBar)findViewById(R.id.device_item_flash_continuous_seekbar);

        if(deviceFunctions.equals("Flash"))
        {
            deviceFlashSwitch.setChecked(true);
            continuousText.setText(bundle.get("deviceParam2").toString());
            intervalTime.setText(bundle.get("deviceParam1").toString());
            seekBar.setProgress(Integer.parseInt(intervalTime.getText().toString()));
            continuousSeekbar.setProgress(Integer.parseInt(continuousText.getText().toString()));
        }
        else
        {
            seekBar.setEnabled(false);
            continuousSeekbar.setEnabled(false);
            intervalTime.setEnabled(false);
            continuousText.setEnabled(false);
        }

        deviceFlashSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    seekBar.setEnabled(true);
                    continuousSeekbar.setEnabled(true);
                    intervalTime.setEnabled(true);
                    continuousText.setEnabled(true);
                }
                else {
                    seekBar.setEnabled(false);
                    continuousSeekbar.setEnabled(false);
                    intervalTime.setEnabled(false);
                    continuousText.setEnabled(false);
                }

            }
        });
        continuousSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                continuousText.setText(""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    deviceFlashSwitch.setEnabled(true);
                    if (deviceFlashSwitch.isChecked())
                    {
                        seekBar.setEnabled(true);
                        continuousSeekbar.setEnabled(true);
                        intervalTime.setEnabled(true);
                        continuousText.setEnabled(true);
                    }

                }
                else{
                    deviceFlashSwitch.setEnabled(false);
                    seekBar.setEnabled(false);
                    continuousSeekbar.setEnabled(false);
                    intervalTime.setEnabled(false);
                    continuousText.setEnabled(false);
                }

            }
        });
        //
        Button okBt = (Button)findViewById(R.id.device_item_detail_ok);
        okBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                //Bundle date = new Bundle(bundle);
                if(sswitch.isChecked()){
                    if(deviceFlashSwitch.isChecked()){
                        bundle.putString("deviceFunctions", "Flash");
                        String intervaltime = intervalTime.getText().toString();
                        bundle.putString("deviceParam1", intervaltime);
                        String continuousTime = continuousText.getText().toString();
                        bundle.putString("deviceParam2", continuousTime);
                    }
                    else {
                        bundle.putString("deviceFunctions", "On");
                    }
                }
                else {
                    bundle.putString("deviceFunctions", "Off");
                }
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
    }
        });

        Button cancelBt = (Button)findViewById(R.id.device_item_detail_cancel);
        cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });


    }
}
