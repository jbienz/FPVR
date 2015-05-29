package com.solersoft.fpvr;

import com.solersoft.fpvr.fpvrdji.DJIInspire1Vehicle;
import com.solersoft.fpvr.fpvrdji.DJIVehicle;
import com.solersoft.fpvr.fpvrlib.Attitude;
import com.solersoft.fpvr.fpvrlib.GamepadVehicleController;
import com.solersoft.fpvr.fpvrlib.Result;
import com.solersoft.fpvr.fpvrlib.ResultHandler;
import com.solersoft.fpvr.fpvrlib.StatusListener;
import com.solersoft.fpvr.fpvrlib.StatusUpdater;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


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
    private GamepadVehicleController gamepadController;

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
        if (connected || connecting) { return; }

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
                    connected = result.isSuccess();
                    connecting = false;
                    StatusUpdater.UpdateStatus(TAG, "Connected to drone: " + connected);

                    // If connected, create and initialize controller
                    if (connected)
                    {
                        gamepadController = new GamepadVehicleController(djiVehicle);
                        gamepadController.initialize();
                    }
                }
            });
        }
        else
        {
            connecting = false;
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

        // StartDJI(true);
    }

    @Override
    protected void onDestroy()
    {
        StopDJI();
        super.onDestroy();
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event)
    {
        // If not initialized then don't process
        if ((gamepadController == null) || (!gamepadController.isInitialized())) { return false; }

        // Let controller process
        return gamepadController.handleMotionEvent(event);
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

    int view = -1;
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
                djiVehicle.getGimbal().moveToFPV();
                /*
                HomeActivity.this.view = HomeActivity.this.view + 1;
                if (HomeActivity.this.view > 2) { HomeActivity.this.view = 0; }

                switch (HomeActivity.this.view)
                {
                    case 0:
                        djiVehicle.getGimbal().moveAbsolute(new Attitude(63.8, 77.3, 0)); // FPV
                        break;
                    case 1:
                        djiVehicle.getGimbal().moveAbsolute(new Attitude(64.1, 52.5, 0)); // Down
                        break;
                    case 2:
                        djiVehicle.getGimbal().moveAbsolute(new Attitude(-90.0, 77.3, 0)); // R/L
                        break;
                }

                // djiVehicle.getGimbal().moveAbsolute(new Attitude(r,0,0));
                */
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
