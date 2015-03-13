package com.example.cninlaxu.smartrouter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;


public class DeviceFan extends Activity {

    Switch sswitch = null;
    Switch timerSwitch = null;
    EditText levelText = null;
    EditText timerText = null;
    SeekBar levelSeekbar = null;
    SeekBar timerSeekbar = null;
    Button okBt = null;
    Button cancelBt = null;

    private void setWidgetEnable(boolean b){
        timerSwitch.setEnabled(b);

        levelSeekbar.setEnabled(b);
        levelText.setEnabled(b);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_fan_detail);
        final Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        TextView describeText = (TextView)findViewById(R.id.device_fan_item_name);
        describeText.setText(bundle.get("deviceDescribe").toString());
        //switch
        sswitch = (Switch)findViewById(R.id.device_fan_item_switch);
        timerSwitch = (Switch)findViewById(R.id.device_fan_timer_switch);

        levelText = (EditText)findViewById(R.id.device_fan_level_text);
        timerText = (EditText)findViewById(R.id.device_fan_timer_text);

        levelSeekbar = (SeekBar)findViewById(R.id.device_fan_level_seekbar);
        timerSeekbar = (SeekBar)findViewById(R.id.device_fan_timer_seekbar);
        okBt = (Button)findViewById(R.id.device_fan_detail_ok);
        cancelBt =(Button)findViewById(R.id.device_fan_detail_cancel);

        levelText.setText(bundle.get("deviceParam1").toString());
        timerText.setText(bundle.get("deviceParam2").toString());
        Log.d("handleMsg", "fan functions "+bundle.get("deviceFunctions"));
        if(bundle.get("deviceFunctions").equals("On")){
            sswitch.setChecked(true);
            setWidgetEnable(true);
        }
        else {
            sswitch.setChecked(false);
            setWidgetEnable(false);
        }

        sswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    setWidgetEnable(true);
                    if (!timerSwitch.isChecked()){
                        timerSeekbar.setEnabled(false);
                        timerText.setEnabled(false);
                    }
                }
                else {
                    setWidgetEnable(false);
                }
            }
        });
        if(timerSwitch.isChecked())
        {
            timerSeekbar.setEnabled(true);
            timerText.setEnabled(true);
        }
        else {
            timerSeekbar.setEnabled(false);
            timerText.setEnabled(false);
        }
        timerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    timerText.setEnabled(true);
                    timerSeekbar.setEnabled(true);
                }
                else {
                    timerSeekbar.setEnabled(false);
                    timerText.setEnabled(false);
                }
            }
        });

        //
        levelSeekbar.setMax(3);
        try{
            final int level = Integer.parseInt(levelText.getText().toString());

            levelSeekbar.setProgress(level);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        levelSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                levelText.setText(""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        try{
            final int timer = Integer.parseInt(timerText.getText().toString());
            timerSeekbar.setProgress(timer);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        timerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                timerText.setText(""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        okBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String level=levelText.getText().toString();
                if(sswitch.isChecked()){
                    bundle.putString("deviceParam1", level);
                    if(timerSwitch.isChecked()){
                        String timer = timerText.getText().toString();
                        bundle.putString("deviceParam2", timer);
                    }
                    else {
                        bundle.putString("deviceParam2","0");
                    }
                    bundle.putString("deviceFunctions", "On");
                }
                else
                {
                    bundle.putString("deviceFunctions", "Off");
                }
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Button cancelBt = (Button)findViewById(R.id.device_fan_detail_cancel);
        cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }


}
