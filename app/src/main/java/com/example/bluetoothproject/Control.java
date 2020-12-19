package com.example.bluetoothproject;

import android.os.Bundle;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.UUID;


public class Control extends AppCompatActivity {

    // Button btnOn, btnOff, btnDis;
    ImageButton f,b,r,l;
    Boolean up=false,down=false,left=false,right=false ;
    String x;
    Button Discnt,bip;
    Button Abt;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            send("s");
            btSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        address = intent.getStringExtra(DeviceList.EXTRA_ADDRESS); //receive the address of the bluetooth device

        //view of the ledControl
        setContentView(R.layout.activity_control);

        //call the widgets
        f = (ImageButton)findViewById(R.id.f);
        b = (ImageButton)findViewById(R.id.b);
        r = (ImageButton)findViewById(R.id.r);
        l = (ImageButton)findViewById(R.id.l);
        Discnt = (Button)findViewById(R.id.dis_btn);
        bip = (Button)findViewById(R.id.bip);


        new ConnectBT().execute(); //Call the class to connect

        //commands to be sent to bluetooth
        f.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    up=true;
                }
                 if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    up=false;
                }
                test();
                return false;
            }


        });

        b.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    down=true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    down=false;
                }
                test();
                return false;
            }


        });
        r.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    right=true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    right=false;
                }
                test();
                return false;
            }


        });
        l.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    left=true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    left=false;
                }
                test();
                return false;
            }


        });

        bip.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
            send("5");
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    send("s");
                }
                return false;
            }


        });

        Discnt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Disconnect(); //close connection
            }
        });


    }
    public void test(){
        if(up && right){
            send("9");
        }
       else if(up && left){
            send("7");
        }
        else if(down && right){
            send("3");
        }
        else if(down && left){
            send("1");
        }
        else  if(up){
            send("8");
        }
        else if(down){
            send("2");
        }
        else if(right){
            send("6");
        }
        else if(left){
            send("4");
        }
        else send("s");
    }

    private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            { msg("Error");}
        }
        finish(); //return to the first layout

    }



    private void send(String x)
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write(x.toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }




    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(Control.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}
