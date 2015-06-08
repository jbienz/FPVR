package com.solersoft.fpvr;

import com.solersoft.fpvr.fpvrdji.DJIInspire1Vehicle;
import com.solersoft.fpvr.fpvrlib.Attitude;
import com.solersoft.fpvr.fpvrlib.ConnectionState;
import com.solersoft.fpvr.fpvrlib.GamepadGimbalController;
import com.solersoft.fpvr.fpvrlib.GimbalListener;
import com.solersoft.fpvr.fpvrlib.ICameraInfo;
import com.solersoft.fpvr.fpvrlib.IGimbalControl;
import com.solersoft.fpvr.fpvrlib.IGimbalInfo;
import com.solersoft.fpvr.fpvrlib.IVehicle;
import com.solersoft.fpvr.fpvrlib.Result;
import com.solersoft.fpvr.fpvrlib.ResultHandler;
import com.solersoft.fpvr.fpvrlib.StatusListener;
import com.solersoft.fpvr.fpvrlib.StatusUpdater;
import com.solersoft.fpvr.util.CollectionUtils;
import com.solersoft.fpvr.util.ThreadUtils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.net.InetAddress;


public class PilotActivity extends Activity
{

    /***************************************************************************
     * Constants
     ***************************************************************************/

    private static final String TAG = "PilotActivity";


    /***************************************************************************
     * Member Variables
     ***************************************************************************/
    private IVehicle vehicle;
    private GamepadGimbalController gamepadGimbalController;

    private FrameLayout cameraLayout;
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
     * Creates the vehicle
     * @param inspire true if talking to an Inspire 1; otherwise talking to Vision
     */
    private void createVehicle(boolean inspire, final ResultHandler handler)
    {
        // Get the context
        final Context context = this.getApplicationContext();

        if (inspire)
        {
            vehicle = new DJIInspire1Vehicle(context);
        }
        else
        {
            throw  new UnsupportedOperationException("Not implemented");
        }

        // Call create on the vehicle itself
        vehicle.create(handler);
    }

    /***************************************************************************
     * Overrides
     ***************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // This thread is the UI thread
        ThreadUtils.setUiThread();

        setContentView(R.layout.activity_pilot);

        cameraLayout = (FrameLayout) findViewById(R.id.cameraLayout);

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
                PilotActivity.this.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        statusLabel.setText(status);
                    }
                });
            }
        });

        // Create the vehicle
        createVehicle(true, new ResultHandler()
        {
            @Override
            public void onResult(Result result)
            {
                // Problem?
                if (!result.isSuccess())
                {
                    StatusUpdater.UpdateStatus(TAG, "Error creating vehicle");
                }
                else
                {
                    StatusUpdater.UpdateStatus(TAG, "Attempting to start camera preview");

                    // Try to get camera service
                    ICameraInfo camera = CollectionUtils.findFirst(ICameraInfo.class, vehicle.getServices());

                    if (camera == null)
                    {
                        StatusUpdater.UpdateStatus(TAG, "No camera service found on the vehicle.");
                    }
                    else
                    {
                        // Try to get the camera view
                        View cameraView = camera.getCameraView();


                        if (cameraView == null)
                        {
                            StatusUpdater.UpdateStatus(TAG, "Camera service did not return a valid preview source.");
                        }
                        else
                        {
                            // If the view was found, add it to the layout
                            cameraLayout.addView(cameraView);
                            StatusUpdater.UpdateStatus(TAG, "Camera preview started.");
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onPause()
    {
        if (vehicle != null) { vehicle.pause(); }
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        if (vehicle != null) { vehicle.resume(); }
        super.onResume();
    }

    @Override
    protected void onDestroy()
    {
        if (vehicle != null) { vehicle.destroy(); }
        super.onDestroy();
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event)
    {
        // If controller isn't available, we haven't handled it
        if (gamepadGimbalController == null) { return false; }

        // Let controller process
        return gamepadGimbalController.handleMotionEvent(event);
    }

    /***************************************************************************
     * Callbacks / Event Handlers
     ***************************************************************************/

    /*
    GimbalListener gimbalListener = new GimbalListener()
    {
        @Override
        public void onAttitudeChanged(final IGimbalInfo gimbal)
        {
            PilotActivity.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Attitude attitude = gimbal.getAttitude();
                    yawLabel.setText(Double.toString(attitude.yaw));
                    pitchLabel.setText(Double.toString(attitude.pitch));
                    rollLabel.setText(Double.toString(attitude.roll));
                }
            });
        }
    };*/

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
            if ((vehicle != null) && (vehicle.isCreated()))
            {
                // djiVehicle.getGimbal().moveToFPV();
                /*
                PilotActivity.this.view = PilotActivity.this.view + 1;
                if (PilotActivity.this.view > 2) { PilotActivity.this.view = 0; }

                switch (PilotActivity.this.view)
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
            if ((vehicle != null) && (vehicle.getConnectionState() == ConnectionState.Disconnected))
            {
                vehicle.connect(new ResultHandler()
                {
                    @Override
                    public void onResult(Result result)
                    {
                        // Problem?
                        if (!result.isSuccess())
                        {
                            StatusUpdater.UpdateStatus(TAG, "Error connecting to vehicle");
                        }
                        else
                        {
                            // If not already created, add gimbal controller
                            if (gamepadGimbalController == null)
                            {
                                // Attempt to get the gimbal controller
                                IGimbalControl gimbalControl = CollectionUtils.findFirst(IGimbalControl.class, vehicle.getServices());

                                // If gimbal found, subscribe to updates and create controller
                                if (gimbalControl != null)
                                {
                                    // gimbalControl.setGimbalListener(gimbalListener);
                                    gamepadGimbalController = new GamepadGimbalController(gimbalControl);
                                }
                            }
                        }
                    }
                });
            }
        }
    };

}
