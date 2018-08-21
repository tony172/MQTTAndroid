package com.example.toni.mqttandroid;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.eclipse.paho.*;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Gui extends AppCompatActivity implements MqttCallback{
    private MqttAndroidClient m;
    private String topic;
    private Map<String,ProgressBar> bars;
    private Map<String,TextView> labels;
    private Map<String,Integer> mins;
    private Map<String,String> unit;
    private Map<String,String> title;
    private JSONObject Jobj;
    private JSONArray Jarray;
    String json;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gui);
        m=MainActivity.getMqttAndroidClient();
        topic=getIntent().getExtras().getString("topic");
        m.setCallback(this);
        try {
            m.subscribe(topic,1);
        } catch (MqttException e){}
        bars = new HashMap<>();
        labels = new HashMap<>();
        mins=new HashMap<>();
        unit = new HashMap<>();
        title = new HashMap<>();
        InputStream is=null;
        byte[] buffer=null;
        try {
            is = this.getAssets().open("conf.json");
            buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        json = new String(buffer);
        Log.println(Log.DEBUG,"d",json);
        try {
            Jobj=new JSONObject(new JSONTokener(json));
            Jarray = Jobj.getJSONArray("gauges");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LinearLayout cl=null;
        for(int i=0;i<Jarray.length();i++){
            ProgressBar progress=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
            progress.setVisibility(View.INVISIBLE);
            try {
                String topic=Jarray.getJSONObject(i).getString("topic");
                progress.setMax(Jarray.getJSONObject(i).getInt("max")- Jarray.getJSONObject(i).getInt("min"));
                bars.put(topic,progress);
                cl=(LinearLayout)findViewById(R.id.ll);

                cl.addView(progress);
                TextView text = new TextView(this);
                text.setVisibility(View.INVISIBLE);
                labels.put(topic,text);
                mins.put(topic,Jarray.getJSONObject(i).getInt("min"));
                unit.put(topic,Jarray.getJSONObject(i).getString("unit"));
                title.put(topic,Jarray.getJSONObject(i).getString("title"));
                cl.addView(text);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }



    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

            int res=0;
            res=Integer.parseInt(message.toString())-mins.get(topic);
            bars.get(topic).setProgress(res);
            bars.get(topic).setVisibility(View.VISIBLE);
            labels.get(topic).setText(title.get(topic) + " " +  message.toString() + " " +unit.get(topic));
            labels.get(topic).setVisibility(View.VISIBLE);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
