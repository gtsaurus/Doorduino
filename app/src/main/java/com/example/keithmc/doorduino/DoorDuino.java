package com.example.keithmc.doorduino;





import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DoorDuino extends Activity {
    OutputStream outputStream = null;
    Socket socket = null;
    TextView textResponse,textAddress,textPort,textConnected;
    Button buttonConnect, buttonClear, buttonDoorUp,buttonDoorDown, buttonDoorLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_duino);
        textConnected  = (TextView)findViewById(R.id.ConnectedStatus);
        textAddress = (TextView)findViewById(R.id.address);
        textPort = (TextView)findViewById(R.id.port);
        buttonConnect = (Button)findViewById(R.id.connect);
        buttonClear = (Button)findViewById(R.id.clear);
        textResponse = (TextView)findViewById(R.id.response);
        buttonDoorUp = (Button)findViewById(R.id.DoorUp);
        buttonDoorDown = (Button)findViewById(R.id.DoorDown);
        buttonDoorLight = (Button)findViewById(R.id.DoorLight);
        buttonConnect.setOnClickListener(buttonConnectOnClickListener);
        buttonDoorUp.setOnClickListener(buttonDoorUpOnClickListener);
        buttonDoorDown.setOnClickListener(buttonDoorDownOnClickListener);
        buttonDoorLight.setOnClickListener(buttonDoorLightOnClickListener);
        buttonClear.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                textResponse.setText("");
            }});
        textAddress.setText("10.0.0.69");
        textPort.setText("3300");
        textConnected.setText("NOT CONNECTED");

    }





    OnClickListener buttonDoorUpOnClickListener =
              new OnClickListener(){
              @Override
              public void onClick(View arg0) {
            new MyDoorTask(72);
            new MyDoorTask(0x31);
            new MyDoorTask(0x30);
            new MyDoorTask(0x31);
            new MyDoorTask(0xA);
        }};

    OnClickListener buttonDoorDownOnClickListener =
            new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                new MyDoorTask(72);
                new MyDoorTask(0x31);
                new MyDoorTask(0x30);
                new MyDoorTask(0x32);
                new MyDoorTask(0xA);
            }};
    OnClickListener buttonDoorLightOnClickListener =
            new OnClickListener(){

                @Override
                public void onClick(View arg0) {
                    new MyDoorTask(72);
                    new MyDoorTask(0x31);
                    new MyDoorTask(0x30);
                    new MyDoorTask(0x33);
                    new MyDoorTask(0xA);

                }};

    public class MyDoorTask extends java.net.Socket {



        String errorString = "";


        MyDoorTask(int doorCode) {




            //  @Override
            //protected Void doInBackground(Void... arg0)


            try {


                outputStream.write(doorCode);


            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                errorString = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                errorString = "IOException: " + e.toString();
            }


        }


    }

    OnClickListener buttonConnectOnClickListener =
            new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    MyClientTask myClientTask = new MyClientTask("10.0.0.69", 3300);
                       /*     editTextAddress.getText().toString(),
                            Integer.parseInt(editTextPort.getText().toString()) */

                    myClientTask.execute();

                }
            };

    public class MyClientTask   extends AsyncTask<Context, Integer, String> {

        String dstAddress = "";
        int dstPort = 0;
        String response = "";
       //char newline = '\n';
        MyClientTask(String addr, int port ){
            dstAddress = addr;
            dstPort = port;

        }


        @Override
        public String doInBackground(Context...Params) {



            try {
                socket = new Socket(dstAddress, dstPort);

                ByteArrayOutputStream byteArrayOutputStream =
                        new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];

                int bytesRead;
                InputStream inputStream = socket.getInputStream();

                outputStream = socket.getOutputStream();

               if( socket.isConnected()) {

                   publishProgress(1);
               }







    /*
     * notice:
     * inputStream.read() will block if no data return
     */
              /* (!response.endsWith("Close Connection"))  cut from below*/


              while (!response.endsWith("\n")) {
                    bytesRead = inputStream.read(buffer);
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    if(byteArrayOutputStream.toString("UTF-8").endsWith("\n")) {
                        response += byteArrayOutputStream.toString("UTF-8");
                    }

                }

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            }finally{
                if(socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (values[0] == 1) textConnected.setText("CONNECTED");

        }

       @Override
        protected void onPostExecute(String result) {
          textResponse.setText(response);
           textConnected.setText("NOT CONNECTED");
            super.onPostExecute(result);
        }

    }


}