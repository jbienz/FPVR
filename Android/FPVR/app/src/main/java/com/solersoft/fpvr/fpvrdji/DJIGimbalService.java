package com.solersoft.fpvr.fpvrdji;

import com.solersoft.fpvr.fpvrlib.*;

import java.security.InvalidParameterException;
import java.util.Timer;
import java.util.TimerTask;

import dji.sdk.api.DJIDrone;
import dji.sdk.api.Gimbal.DJIGimbalAttitude;
import dji.sdk.api.Gimbal.DJIGimbalRotation;
import dji.sdk.interfaces.DJIExecuteResultCallback;
import dji.sdk.interfaces.DJIGimbalUpdateAttitudeCallBack;

/**
 * An implementation of the {@link IGimbalControl} interface for DJI Aircraft.
 */
public class DJIGimbalService implements IGimbalControl, IGimbalInfo, ISupportInitialize
{
    /*
    private boolean fpv = false;
    private GimbalWorkMode gimbalMode = GimbalWorkMode.Free_Mode;

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


    */

    //region Constants
    private static final String TAG = "DJIGimbalService";
    //endregion

    //region Member Variables
    private Attitude attitude = new Attitude(0,0,0);
    private boolean initialized;
    private GimbalListener listener;
    private Attitude targetAttitude = new Attitude(0,0,0);
    private Timer targetTimer;
    //endregion

    //region Constructors
    public DJIGimbalService()
    {
    }
    //endregion

    //region Internal Methods
    // HACK: Right now the gimbal does not honor absolute degrees so we must calculate from relative ones
    private void moveToTarget()
    {
        if (!isEnabled()) { return; }

        /*
        yaw	is in range [-1800, +1800]. (real angle x10)
        pitch is in range [-900, 300]. (real angle x10)
        roll is in range [-1800, +1800]. (real angle x10)
        */

        int y,p,r,cy,cp,cr = 0;
        synchronized (targetAttitude)
        {
            synchronized (attitude)
            {
                // Convert double degrees to int range for gimbal API
                y = (int) Math.round(targetAttitude.yaw * 10);
                y = Math.min(y, -1800);
                y = Math.max(y, 1800);

                p = (int) Math.round(targetAttitude.pitch * 10);
                p = Math.min(p, 300);
                p = Math.max(p, -900);

                r = (int) Math.round(targetAttitude.roll * 10);
                r = Math.min(r, 1800);
                r = Math.max(r, -1800);

                // Absolute to relative
                cy = (int) Math.round(attitude.yaw * 10);
                cp = (int) Math.round(attitude.pitch * 10);
                cr = (int) Math.round(attitude.roll * 10);
            }
        }

        y = y - cy;
        p = p - cp;
        r = r - cr;

        // Convert angles to actual rotation values
        boolean enabled = true;
        boolean directionBackward = true;
        boolean typeRelative = false;
        DJIGimbalRotation yaw = new DJIGimbalRotation(enabled, directionBackward, typeRelative, y);
        DJIGimbalRotation pitch = new DJIGimbalRotation(enabled, directionBackward, typeRelative, p);
        DJIGimbalRotation roll = new DJIGimbalRotation(enabled, directionBackward, typeRelative, r);

        // Move the gimbal
        DJIDrone.getDjiGimbal().updateGimbalAttitude(pitch, roll, yaw);
    }
    //endregion

    //region Callbacks / Event Handlers
    DJIGimbalUpdateAttitudeCallBack onGimbalAttitudeUpdate = new DJIGimbalUpdateAttitudeCallBack()
    {
        @Override
        public void onResult(final DJIGimbalAttitude a)
        {
            synchronized (attitude)
            {
                attitude = new Attitude(a.yaw / 10, a.pitch / 10, a.roll / 10);
            }
            if (listener != null)
            {
                listener.onAttitudeChanged(DJIGimbalService.this);
            }
        }
    };

    TimerTask onMoveTimer = new TimerTask()
    {
        @Override
        public void run()
        {
            moveToTarget();
        }
    };
    //endregion

    //region Public Methods
    @Override
    public void goToAttitude(Attitude attitude)
    {
        if (!isEnabled()) { return; }

        // Set the target
        synchronized (targetAttitude)
        {
            targetAttitude = attitude;
        }

        // Move
        moveToTarget();
    }

    @Override
    public void goToFPV()
    {
        if (!isEnabled()) { return; }

        // Go to 0,0,0
        goToAttitude(new Attitude(0, 0, 0));
    }

    @Override
    public void initialize()
    {
        // If already tracking, bail
        if (initialized) { return; }

        // Set the callback
        DJIDrone.getDjiGimbal().setGimbalUpdateAttitudeCallBack(onGimbalAttitudeUpdate);

        // Start the updates
        initialized = DJIDrone.getDjiGimbal().startUpdateTimer(250);

        // Update status
        if (initialized)
        {
            StatusUpdater.UpdateStatus(TAG, "Gimbal Tracking Started");

            // Start the move timer to deal with absolute bug
            targetTimer = new Timer();
            targetTimer.scheduleAtFixedRate(onMoveTimer, 500, 500);
        }
        else
        {
            StatusUpdater.UpdateStatus(TAG, "Gimbal Tracking was unable to start");
        }
    }

    @Override
    public void setGimbalListener(GimbalListener l)
    {
        listener = l;
    }
    //endregion


    //region Public Properties
    @Override
    public Attitude getAttitude()
    {
        synchronized (attitude)
        {
            return attitude;
        }
    }

    @Override
    public boolean isEnabled()
    {
        return initialized;
    }

    @Override
    public boolean isInitialized()
    {
        return initialized;
    }
    //endregion
}
