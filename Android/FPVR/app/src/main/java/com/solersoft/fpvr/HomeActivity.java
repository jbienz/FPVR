package com.solersoft.fpvr;

import com.solersoft.fpvr.fpvrdji.DJIInspire1Vehicle;
import com.solersoft.fpvr.fpvrdji.DJIVehicle;
import com.solersoft.fpvr.fpvrlib.Attitude;
import com.solersoft.fpvr.fpvrlib.Result;
import com.solersoft.fpvr.fpvrlib.ResultHandler;
import com.solersoft.fpvr.fpvrlib.StatusListener;
import com.solersoft.fpvr.fpvrlib.StatusUpdater;
import com.solersoft.fpvr.util.DJI;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.beans.PropertyChangeListener;

import dji.sdk.api.Camera.DJICameraSettingsTypeDef.*;
import dji.sdk.api.DJIDrone;
import dji.sdk.api.DJIDroneTypeDef.DJIDroneType;
import dji.sdk.api.DJIError;
import dji.sdk.api.Gimbal.DJIGimbalAttitude;
import dji.sdk.api.Gimbal.DJIGimbalRotation;
import dji.sdk.api.Gimbal.DJIGimbalTypeDef;
import dji.sdk.api.Gimbal.DJIGimbalTypeDef.*;
import dji.sdk.interfaces.DJIExecuteResultCallback;
import dji.sdk.interfaces.DJIGerneralListener;
import dji.sdk.interfaces.DJIGimbalUpdateAttitudeCallBack;


public class HomeActivity extends Activity
{

    /***************************************************************************
     * Constants
     ***************************************************************************/

    private static final String TAG = "HomeActivity";


    /***************************************************************************
     * Member Variables
     ***************************************************************************/
    private boolean connected;
    private boolean connecting;

    private DJIVehicle djiVehicle;

    private Button gimbalModeButton;
    private Button gimbalMoveButton;
    private Button gimbalStartButton;
    private TextView statusLabel;

    private TextView yawLabel;
    private TextView pitchLabel;
    private TextView rollLabel;


    /***************************************************************************
     * Internal Methods
     ***************************************************************************/

    /**
     * Starts the DJI SDK
     * @param inspire true if talking to an Inspire 1; otherwise talking to Vision
     */
    private void StartDJI(final boolean inspire)
    {
        // If already connected or initializing just bail
        if (connected) { return; }

        // Connecting
        connecting = true;

        // Get the context
        final Context context = this.getApplicationContext();

        StatusUpdater.UpdateStatus(TAG, "Connecting...");

        if (inspire)
        {
            djiVehicle = new DJIInspire1Vehicle(context);
            djiVehicle.connect(new ResultHandler()
            {
                @Override
                public void onResult(Result result)
                {
                    connecting = false;
                    connected = result.isSuccess();
                    StatusUpdater.UpdateStatus(TAG, "Connect to drone: " + connected);
                }
            });
        }
        else
        {
            throw  new UnsupportedOperationException("Not implemented");
        }
    }


    /**
     * Stop the DJI SDK if it was running.
     */
    private void StopDJI()
    {
        if (connected)
        {
            djiVehicle.disconnect();
            connected = false;
        }
    }

    /***************************************************************************
     * Overrides
     ***************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        statusLabel = (TextView) findViewById(R.id.statusLabel);

        gimbalModeButton = (Button) findViewById(R.id.GimbalModeButton);
        gimbalModeButton.setOnClickListener(gimbalModeButtonOnClick);

        gimbalMoveButton = (Button) findViewById(R.id.GimbalMoveButton);
        gimbalMoveButton.setOnClickListener(gimbalMoveButtonOnClick);

        gimbalStartButton = (Button) findViewById(R.id.GimbalStartButton);
        gimbalStartButton.setOnClickListener(gimbalStartButtonOnClick);

        yawLabel = (TextView) findViewById(R.id.yawLabel);
        pitchLabel = (TextView) findViewById(R.id.pitchLabel);
        rollLabel = (TextView) findViewById(R.id.rollLabel);

        // Start status updates
        StatusUpdater.setStatusListener(new StatusListener()
        {
            @Override
            public void onStatusChanged(final String tag, final String status)
            {
                Log.i(tag, status);
                HomeActivity.this.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        statusLabel.setText(status);
                    }
                });
            }
        });

        StartDJI(true);
    }

    @Override
    protected void onDestroy()
    {
        StopDJI();
        super.onDestroy();
    }


    /***************************************************************************
     * Callbacks / Event Handlers
     ***************************************************************************/

    /**
     * Called when the Start Gimbal mode button is clicked
     */
    View.OnClickListener gimbalModeButtonOnClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {

        }
    };

    /**
     * Called when the Start Gimbal button is clicked
     */
    View.OnClickListener gimbalMoveButtonOnClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if (connected)
            {
                djiVehicle.getGimbal().goToAttitude(new Attitude(200,0,0));
            }
        }
    };

    /**
     * Called when the Start Gimbal button is clicked
     */
    View.OnClickListener gimbalStartButtonOnClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            StartDJI(true);
        }
    };

}
