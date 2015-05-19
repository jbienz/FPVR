package com.solersoft.fpvr;

import com.solersoft.fpvr.util.DJI;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


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
    private boolean fpv = false;
    private GimbalWorkMode gimbalMode = GimbalWorkMode.Free_Mode;
    private boolean gimbalTracking;
    private boolean initializing;
    private boolean validated;

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
        if (connected || initializing) { return; }

        // Initializing
        initializing = true;

        // Get the context
        final Context context = this.getApplicationContext();

        try
        {
            UpdateStatus("Initializing...");

            if (inspire)
            {
                DJIDrone.initWithType(context, DJIDroneType.DJIDrone_Inspire1);
            }
            else
            {
                DJIDrone.initWithType(context, DJIDroneType.DJIDrone_Vision);
            }

            UpdateStatus("Connecting...");
            connected = DJIDrone.connectToDrone();

            // Update status and bail if not OK
            if (!connected)
            {
                UpdateStatus("Unable to connect to drone.");
                initializing = false;
                return;
            }

            UpdateStatus("Checking permissions...");

            DJIDrone.checkPermission(getApplicationContext(), new DJIGerneralListener()
            {

                @Override
                public void onGetPermissionResult(int result)
                {
                    // Validated based on success
                    validated = DJI.Success(result);

                    // Show permissions
                    String desc = DJIError.getCheckPermissionErrorDescription(result);
                    UpdateStatus("Permissions: " + desc);

                    // Done initializing
                    initializing = false;
                }
            });
        }
        catch (Exception e)
        {
            UpdateStatus("Unable to initialize: " + e.getMessage());
            e.printStackTrace();
            initializing = false;
        }
    }

    private boolean StartGimbalTracking()
    {
        // If already tracking, bail
        if (gimbalTracking) { return true; }

        // Set the callback
        DJIDrone.getDjiGimbal().setGimbalUpdateAttitudeCallBack(onGimbalAttitudeUpdate);

        // Start the updates
        gimbalTracking = DJIDrone.getDjiGimbal().startUpdateTimer(250);

        // Update status
        if (gimbalTracking)
        {
            UpdateStatus("Gimbal Tracking Started");
        }
        else
        {
            UpdateStatus("Gimbal Tracking was unable to start");
        }

        // Return tracking
        return gimbalTracking;
    }

    /**
     * Stop the DJI SDK if it was running.
     */
    private void StopDJI()
    {
        if (connected)
        {
            DJIDrone.disconnectToDrone();
            connected = false;
        }
    }

    private void StopGimbalTracking()
    {
        if (!gimbalTracking) { return; }
        DJIDrone.getDjiGimbal().stopUpdateTimer();
        UpdateStatus("Gimbal Tracking Stopped");
        gimbalTracking = false;
    }

    private void TakePhoto()
    {
        boolean bConnectState = DJIDrone.getDjiCamera().getCameraConnectIsOk();

        if (!bConnectState)
        {
            UpdateStatus("Camera Not Ready");
            return;
        }
        else
        {
            UpdateStatus("Camera IS Ready");
        }

        // Set to photo capture mode
        DJIDrone.getDjiCamera().setCameraMode(CameraMode.Camera_Capture_Mode, new DJIExecuteResultCallback()
        {
            @Override
            public void onResult(DJIError mErr)
            {
                // Test for success
                if (!DJI.Success(mErr.errorCode))
                {
                    String msg = "Could not switch to camera mode: " + mErr.errorCode + " " + DJIError.getErrorDescriptionByErrcode(mErr.errorCode);
                    UpdateStatus(msg);
                    return;
                }

                // Take photo
                DJIDrone.getDjiCamera().startTakePhoto(CameraCaptureMode.Camera_Single_Capture, new DJIExecuteResultCallback()
                {
                    @Override
                    public void onResult(DJIError mErr)
                    {
                        // Test for success
                        if (!DJI.Success(mErr.errorCode))
                        {
                            String msg = "Could not take photo: " + mErr.errorCode + " " + DJIError.getErrorDescriptionByErrcode(mErr.errorCode);
                            UpdateStatus(msg);
                            return;
                        }
                    }
                });
            }
        });
    }

    private void UpdateGimbalAttitude()
    {
        boolean enabled = true;
        boolean directionBackward = true;
        boolean typeRelative = false;
        DJIGimbalRotation pitch = new DJIGimbalRotation(false, directionBackward, typeRelative, 0);
        DJIGimbalRotation roll = new DJIGimbalRotation(false, directionBackward, typeRelative, 0);
        DJIGimbalRotation yaw = new DJIGimbalRotation(enabled, directionBackward, typeRelative, 200);

        DJIDrone.getDjiGimbal().updateGimbalAttitude(pitch, roll, yaw);
    }

    /**
     * Updates the status display.
     * @param msg The message to display.
     */
    private void UpdateStatus(final String msg)
    {
        HomeActivity.this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                statusLabel.setText(msg);
            }
        });
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
     * Called when the Gimbal Mode button is clicked
     */
    View.OnClickListener gimbalModeButtonOnClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            // Increment
            switch (gimbalMode)
            {
                case Free_Mode:
                    gimbalMode = GimbalWorkMode.Fpv;
                    break;
                case Fpv:
                    gimbalMode = GimbalWorkMode.Yaw_Follow;
                    break;
                case Yaw_Follow:
                    gimbalMode = GimbalWorkMode.Free_Mode;
                    break;

            }

            DJIGimbalAttitude attitude = new DJIGimbalAttitude();
            attitude.pitch = 0.0;
            attitude.roll = 0.0;
            attitude.yaw = 200;
            DJIDrone.getDjiGimbal().setGimbalControl(attitude, GimbalWorkMode.Free_Mode, new DJIExecuteResultCallback()
            {
                @Override
                public void onResult(DJIError djiError)
                {
                    int errorCode = djiError.errorCode;
                    if (!DJI.Success(errorCode))
                    {
                        String msg = "Could not update gimbal control to " + gimbalMode.name() + ": " + errorCode + " " + DJIError.getErrorDescriptionByErrcode(errorCode);
                        UpdateStatus(msg);
                    }
                    else
                    {
                        String msg = "Gimbal control updated to " + gimbalMode.name() + ".";
                        UpdateStatus(msg);
                    }
                }
            });
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
            UpdateGimbalAttitude();
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
            if (gimbalTracking)
            {
                fpv = !fpv;
                DJIDrone.getDjiGimbal().setGimbalFpvMode(fpv);
                UpdateStatus("FPV is now: " + fpv);
                // StopGimbalTracking();
            }
            else
            {
                StartGimbalTracking();
            }
        }
    };

    DJIGimbalUpdateAttitudeCallBack onGimbalAttitudeUpdate = new DJIGimbalUpdateAttitudeCallBack()
    {
        @Override
        public void onResult(final DJIGimbalAttitude djiGimbalAttitude)
        {
            HomeActivity.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    yawLabel.setText(Double.toString(djiGimbalAttitude.yaw));
                    pitchLabel.setText(Double.toString(djiGimbalAttitude.pitch));
                    rollLabel.setText(Double.toString(djiGimbalAttitude.roll));
                }
            });
        }
    };

}
