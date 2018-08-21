package com.example.toni.mqttandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText txtServer,txtUser,txtTopic;
    private Button btnConnect;
    private static MqttAndroidClient m;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtServer = (EditText)findViewById(R.id.editText1);
        txtUser = (EditText)findViewById(R.id.editText2);
        txtTopic = (EditText)findViewById(R.id.editText3);
        btnConnect = (Button)findViewById(R.id.button);
        btnConnect.setOnClickListener(this);
    }
    public static MqttAndroidClient getMqttAndroidClient(){
        return m;
    }

    @Override
    public void onClick(View v) {
        if(txtServer.getText().toString().isEmpty()){
            Toast.makeText(this,"Server address field is empty!",Toast.LENGTH_LONG).show();
        }
         else if(txtUser.getText().toString().isEmpty()){
            Toast.makeText(this,"Username field is empty!",Toast.LENGTH_LONG).show();
        }
        else if(txtTopic.getText().toString().isEmpty()){
            Toast.makeText(this,"Topic field is empty!",Toast.LENGTH_LONG).show();
        }
        else{
            IMqttToken token;
            Toast.makeText(this,"Connecting...",Toast.LENGTH_SHORT).show();

            try {
                m=new MqttAndroidClient(this.getApplicationContext(),txtServer.getText().toString(),txtUser.getText().toString());
               token= m.connect();
            } catch (MqttException e) {
                Toast.makeText(this,"Connection failed!",Toast.LENGTH_SHORT).show();
                return;
            }
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(getApplicationContext(),"Connected!",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(),Gui.class);
                    i.putExtra("topic",txtTopic.getText().toString());
                    startActivity(i);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(getApplicationContext(),"Failed to connect.",Toast.LENGTH_LONG);
                }
            });

        }
    }
}
