package com.constantin.wilson.FPV_VR;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Created by heisty on 4/19/17.
 */

public class LTMParser implements TelemetryParser{

    Thread receiveFromUDPThread;
    private boolean running;
    private DatagramSocket s = null;


    public LTMParser(int port){

        receiveFromUDPThread=new Thread(){
            @Override
            public void run() {
                running = true;
                receiveFromUDP();
            }
        };

    }



    @Override
    public void stop() {

    }

    @Override
    public void start() {

    }


    private void receiveFromUDP() {
        int server_port = 5001;
        byte[] message = new byte[1024];
        DatagramPacket p = new DatagramPacket(message, message.length);
        boolean exception=false;
        try {s = new DatagramSocket(server_port);
            s.setSoTimeout(500);
        } catch (SocketException e) {e.printStackTrace();}
        while (running && s != null) {
            try {
                s.receive(p);
            } catch (IOException e) {
                exception=true;
                if(! (e instanceof SocketTimeoutException)){
                    e.printStackTrace();
                }
            }
            if(!exception){
                System.out.println("Receiving OSD Data; Parsing required; length:"+p.getLength());
                //we have to parse Telemetry Data
                parseLTM(message,p.getLength());
            }else{exception=false;}
        }
        if (s != null) {
            s.close();
            s=null;
        }
    }




    public void parseLTM(byte[] b, int length) {

    }

    @Override
    public float getVoltage() {
        return 0;
    }

    @Override
    public float getAmpere() {
        return 0;
    }

    @Override
    public float getBaroAltitude() {
        return 0;
    }

    @Override
    public float getAltitude() {
        return 0;
    }

    @Override
    public float getLongitude() {
        return 0;
    }

    @Override
    public float getLatitude() {
        return 0;
    }

    @Override
    public float getSpeed() {
        return 0;
    }

    @Override
    public float getRoll() {
        return 0;
    }

    @Override
    public float getPitch() {
        return 0;
    }

    @Override
    public float getYaw() {
        return 0;
    }

    @Override
    public float getRSSI() {
        return 0;
    }
}
