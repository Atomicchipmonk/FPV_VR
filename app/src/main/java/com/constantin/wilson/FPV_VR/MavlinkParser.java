package com.constantin.wilson.FPV_VR;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.o3dr.android.client.ControlTower;
import com.o3dr.android.client.Drone;
import com.o3dr.android.client.apis.ControlApi;
import com.o3dr.android.client.apis.VehicleApi;
import com.o3dr.android.client.apis.solo.SoloCameraApi;
import com.o3dr.android.client.interfaces.DroneListener;
import com.o3dr.android.client.interfaces.TowerListener;
import com.o3dr.services.android.lib.coordinate.LatLong;
import com.o3dr.services.android.lib.coordinate.LatLongAlt;
import com.o3dr.services.android.lib.drone.attribute.AttributeEvent;
import com.o3dr.services.android.lib.drone.attribute.AttributeType;
import com.o3dr.services.android.lib.drone.companion.solo.SoloAttributes;
import com.o3dr.services.android.lib.drone.companion.solo.SoloState;
import com.o3dr.services.android.lib.drone.connection.ConnectionParameter;
import com.o3dr.services.android.lib.drone.connection.ConnectionResult;
import com.o3dr.services.android.lib.drone.connection.ConnectionType;
import com.o3dr.services.android.lib.drone.property.Altitude;
import com.o3dr.services.android.lib.drone.property.Attitude;
import com.o3dr.services.android.lib.drone.property.Gps;
import com.o3dr.services.android.lib.drone.property.Home;
import com.o3dr.services.android.lib.drone.property.Battery;
import com.o3dr.services.android.lib.drone.property.Signal;
import com.o3dr.services.android.lib.drone.property.Speed;
import com.o3dr.services.android.lib.drone.property.State;
import com.o3dr.services.android.lib.drone.property.Type;
import com.o3dr.services.android.lib.drone.property.VehicleMode;
import com.o3dr.services.android.lib.model.AbstractCommandListener;
import com.o3dr.services.android.lib.model.SimpleCommandListener;


/**
 * Created by heisty on 4/19/17.
 */

public class MavlinkParser implements TelemetryParser, DroneListener, TowerListener {

    private Drone drone;
    private int droneType = Type.TYPE_UNKNOWN;
    private ControlTower controlTower;
    private Handler handler;

    private int UDP_PORT = 14550;
    boolean droneConnected = false;

    public MavlinkParser(int UDP_PORT, Context context){
        Looper.prepare();
        handler = new Handler();


//        final Context context = context;
        this.controlTower = new ControlTower(context);
        this.drone = new Drone(context);
        this.UDP_PORT = UDP_PORT;


    }


    @Override
    public void stop() {
        if (this.drone.isConnected()) {
            this.drone.disconnect();
        }

        this.controlTower.unregisterDrone(this.drone);
        this.controlTower.disconnect();
    }

    @Override
    public void start() {
        Log.i("MavlinkParser", "Mav Parser starting!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1");
        this.controlTower.connect(this);


        //I dont currently trust this
        //Bundle extraParams = new Bundle();
        //extraParams.putInt(ConnectionType.EXTRA_UDP_SERVER_PORT, UDP_PORT); // Set default baud rate to 14550

        ConnectionParameter connectionParams =  ConnectionParameter.newUdpConnection(UDP_PORT, null);
        this.drone.connect(connectionParams);
        Log.i("MavlinkParser", "Drone Connected");
    }


    @Override
    public void onTowerConnected() {
        //alertUser("3DR Services Connected");
        this.controlTower.registerDrone(this.drone, this.handler);
        this.drone.registerDroneListener(this);
    }

    @Override
    public void onTowerDisconnected() {

        //alertUser("3DR Service Interrupted");
    }


    // Drone Listener
    // ==========================================================

    @Override
    public void onDroneEvent(String event, Bundle extras) {

        switch (event) {
            case AttributeEvent.STATE_CONNECTED:
                Log.i("DRONE_EVENT", "Drone Connected");
                droneConnected = true;
                break;
            case AttributeEvent.STATE_DISCONNECTED:
                Log.i("DRONE_EVENT","Drone Disconnected");
                droneConnected = false;
                break;
            default:
                Log.i("DRONE_EVENT", event); //Uncomment to see events from the drone
                break;
        }

    }

    @Override
    public void onDroneServiceInterrupted(String s) {

    }

    public boolean getArmStatus(){
        if(droneConnected) {
            State droneState = this.drone.getAttribute(AttributeType.STATE);
            return droneState.isArmed();
        } else {
            return false;
        }
    }

    public String getVehicleMode(){
        if(droneConnected) {
            State droneState = this.drone.getAttribute(AttributeType.STATE);
            VehicleMode droneMode =  droneState.getVehicleMode();
            return droneMode.getLabel();
        } else {
            return "No Mode";
        }
    }


    @Override
    public float getVoltage() {
        if(droneConnected) {
            Battery droneBattery = this.drone.getAttribute(AttributeType.BATTERY);
            return (float) droneBattery.getBatteryVoltage();
        } else {
            return 0;
        }
    }

    @Override
    public float getAmpere() {
        if(droneConnected) {
            Battery droneBattery = this.drone.getAttribute(AttributeType.BATTERY);
            return (float) droneBattery.getBatteryCurrent();
        } else {
            return 0;
        }
    }

    @Override
    public float getBaroAltitude() {
        if(droneConnected) {
            Altitude droneAltitude = this.drone.getAttribute(AttributeType.ALTITUDE);
            return (float) droneAltitude.getAltitude();
        } else {
            return 0;
        }
    }

    @Override
    public float getAltitude() {
        return getBaroAltitude();
    }

    @Override
    public float getLongitude() {
        if(droneConnected && false) {
            Gps droneGps = this.drone.getAttribute(AttributeType.GPS);
            LatLong vehiclePosition = droneGps.getPosition();
            return (float) vehiclePosition.getLongitude();
        } else {
            return 0;
        }
    }

    @Override
    public float getLatitude() {
        if(droneConnected && false) {
            Gps droneGps = this.drone.getAttribute(AttributeType.GPS);
            LatLong vehiclePosition = droneGps.getPosition();
            return (float) vehiclePosition.getLatitude();
        } else {
            return 0;
        }
    }

    @Override
    public float getSpeed() {
        if(droneConnected) {
            Speed droneSpeed = this.drone.getAttribute(AttributeType.SPEED);
            return (float) droneSpeed.getGroundSpeed();
        } else {
            return 0;
        }
    }

    @Override
    public float getRoll() {
        if(droneConnected) {
            Attitude droneAttitude = this.drone.getAttribute(AttributeType.ATTITUDE);
            return (float) droneAttitude.getRoll();
        } else {
            return 0;
        }
    }

    @Override
    public float getPitch() {
        if(droneConnected) {
            Attitude droneAttitude = this.drone.getAttribute(AttributeType.ATTITUDE);
            return (float) droneAttitude.getPitch();
        } else {
            return 0;
        }
    }

    @Override
    public float getYaw() {
        if(droneConnected) {
            Attitude droneAttitude = this.drone.getAttribute(AttributeType.ATTITUDE);
            return (float) droneAttitude.getYaw();
        } else {
            return 0;
        }
    }

    @Override
    public float getRSSI() {
        if(droneConnected) {
            Signal droneSignal = this.drone.getAttribute(AttributeType.SIGNAL);
            return (float) droneSignal.getRssi();
        } else {
            return 0;
        }
    }
}
