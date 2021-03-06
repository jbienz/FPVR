package com.solersoft.fpvr.fpvrdji;

import android.util.Log;

import com.solersoft.fpvr.fpvrlib.*;
import com.solersoft.fpvr.util.DJI;

import java.security.InvalidParameterException;
import java.util.Timer;
import java.util.TimerTask;

import dji.sdk.api.DJIDrone;
import dji.sdk.api.DJIError;
import dji.sdk.api.Gimbal.DJIGimbal;
import dji.sdk.api.Gimbal.DJIGimbalAttitude;
import dji.sdk.api.Gimbal.DJIGimbalCapacity;
import dji.sdk.api.Gimbal.DJIGimbalRotation;
import dji.sdk.interfaces.DJIExecuteResultCallback;
import dji.sdk.interfaces.DJIGimbalUpdateAttitudeCallBack;

/**
 * An implementation of the {@link IGimbalControl} interface for DJI Aircraft.
 */
public class DJIGimbalService extends Connectable implements IGimbalControl, IGimbalInfo
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

            DJIGimbalAttitude currentAttitude = new DJIGimbalAttitude();
            currentAttitude.pitch = 0.0;
            currentAttitude.roll = 0.0;
            currentAttitude.yaw = 200;
            DJIDrone.getDjiGimbal().setGimbalControl(currentAttitude, GimbalWorkMode.Free_Mode, new DJIExecuteResultCallback()
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
    private static final int DefaultGimbalSpeed = 100;
    private static final int GimbalUpdatesPerSecond = 4;
    private static final double MaxGimbalSpeed = 18; // 18 degrees per second
    //endregion

    //region Member Variables
    private Attitude currentAttitude = new Attitude(0,0,0);
    private DJIGimbalAttitude currentAttitudeNative = new DJIGimbalAttitude();
    private AttitudeCapabilities capabilities;
    private DJIGimbalCapacity capabilitiesNative;
    private GimbalListener listener;
    private DJIGimbalAttitude moveAbsoluteTarget = new DJIGimbalAttitude();
    private Timer moveAbsoluteTimer;
    private boolean moveContinuousEnabled;
    private DJIGimbalAttitude moveContinuousSpeed = new DJIGimbalAttitude();
    private Timer moveContinuousTimer;
    //endregion

    //region Constructors
    public DJIGimbalService()
    {
    }
    //endregion
    
    private Attitude clamp(Attitude attitude)
    {
        Attitude result = new Attitude();
        result.pitch = clampPitch(attitude.pitch);
        result.roll = clampRoll(attitude.roll);
        result.yaw = clampYaw(attitude.yaw);
        return result;
    }

    private double clampPitch(double pitch)
    {
        if ((capabilitiesNative == null) || (!capabilitiesNative.pitchAvailable)) { return 0; }
        pitch = Math.max(pitch, capabilitiesNative.minPitchRotationAngle);
        pitch = Math.min(pitch, capabilitiesNative.maxPitchRotationAngle);
        return pitch;
    }

    private double clampRoll(double roll)
    {
        if ((capabilitiesNative == null) || (!capabilitiesNative.rollAvailable)) { return 0; }
        roll = Math.max(roll, capabilitiesNative.minRollRotationAngle);
        roll = Math.min(roll, capabilitiesNative.maxRollRotationAngle);
        return roll;
    }

    private double clampSpeed(double speed)
    {
        if (speed < -MaxGimbalSpeed) { return -MaxGimbalSpeed; }
        if (speed > MaxGimbalSpeed) { return MaxGimbalSpeed; }
        return speed;
    }

    private double clampYaw(double yaw)
    {
        if ((capabilitiesNative == null) || (!capabilitiesNative.yawAvailable)) { return 0; }
        yaw = Math.max(yaw, capabilitiesNative.minYawRotationAngle);
        yaw = Math.min(yaw, capabilitiesNative.maxYawRotationAngle);
        return yaw;
    }

    //region Internal Methods
    // HACK: Right now the gimbal does not honor absolute degrees so we must calculate from relative ones
    private void moveAbsoluteTick()
    {
        if (!isEnabled()) { return; }

        double p,r,y = 0;
        synchronized (moveAbsoluteTarget)
        {
            // Scale
            p = moveAbsoluteTarget.pitch * 10;
            r = moveAbsoluteTarget.roll * 10;
            y = moveAbsoluteTarget.yaw * 10;
        }

        // Move native
        moveNative(p,r,y, false);
    }

    private void moveContinuousTick()
    {
        double pitch, roll, yaw = 0;

        // Threadsafe
        synchronized (moveContinuousSpeed)
        {
            pitch = moveContinuousSpeed.pitch;
            roll = moveContinuousSpeed.roll;
            yaw = moveContinuousSpeed.yaw;
        }

        Log.i(TAG, "Continuous Tick - Pitch Speed: " + pitch + " Roll Speed: " + roll + " Yaw Speed: " + yaw);

        // Native move
        moveNative(pitch, roll, yaw, true);
    }

    private void moveNative(double pitch, double roll, double yaw, boolean relative)
    {
        relative = false;
        if (relative)
        {
            synchronized (currentAttitudeNative)
            {
                pitch = pitch - currentAttitudeNative.pitch;
                roll = roll - currentAttitudeNative.roll;
                yaw = yaw - currentAttitudeNative.yaw;
            }
        }

        // Clamp
        pitch = clampPitch(pitch);
        roll = clampRoll(roll);
        yaw = clampYaw(yaw);

        // Scale
        int ps = (int)Math.round(pitch);
        int rs = (int)Math.round(roll);
        int ys = (int)Math.round(yaw);

        boolean enabled = true;
        boolean directionBackward = false;
        DJIGimbalRotation pr = new DJIGimbalRotation(enabled, directionBackward, relative, ps);
        DJIGimbalRotation rr = new DJIGimbalRotation(enabled, directionBackward, relative, rs);
        DJIGimbalRotation yr = new DJIGimbalRotation(enabled, directionBackward, relative, ys);

        // Move the gimbal
        Log.i(TAG, "Native Move: PS: " + ps + " RS: " + rs + " YS: " + ys);
        DJIDrone.getDjiGimbal().updateGimbalAttitude(pr, rr, yr);
    }

    private void setGimbalSpeed(int pitchSpeed, int rollSpeed, int yawSpeed)
    {
        // Make sure enabled
        if (!isEnabled()) { return; }

        // Set Gimbal Move Speed
        Log.i(TAG, "Updating gimbal speed: P: " + pitchSpeed + " R: " + rollSpeed + " Y: " + yawSpeed);
        DJIDrone.getDjiRemoteController().setGimbalControlSpeed(pitchSpeed, rollSpeed, yawSpeed, new DJIExecuteResultCallback()
        {
            @Override
            public void onResult(DJIError djiError)
            {
                if (!DJI.Success(djiError.errorCode))
                {
                    StatusUpdater.UpdateStatus(TAG, "Could not update gimbal speed: " + djiError.errorDescription);
                }
            }
        });
    }
    //endregion

    //region Overrides
    @Override
    protected void onConnect(ResultHandler handler)
    {
        // Get the gimbal
        DJIGimbal nativeGimbal = DJIDrone.getDjiGimbal();

        // Get the capacity (min / max values) of the gimbal
        capabilitiesNative = nativeGimbal.getDJIGimbalCapacity();

        // HACK for bug in SDK
        if (capabilitiesNative == null)
        {
            capabilitiesNative = new DJIGimbalCapacity();
            capabilitiesNative.pitchAvailable = true;
            capabilitiesNative.rollAvailable = true;
            capabilitiesNative.yawAvailable = true;
            capabilitiesNative.minPitchRotationAngle = -2000;
            capabilitiesNative.maxPitchRotationAngle = 2000;
            capabilitiesNative.minRollRotationAngle = -2000;
            capabilitiesNative.maxRollRotationAngle = 2000;
            capabilitiesNative.minYawRotationAngle = -2000;
            capabilitiesNative.maxYawRotationAngle = 2000;
        }

        // Set the callback
        nativeGimbal.setGimbalUpdateAttitudeCallBack(onGimbalAttitudeUpdate);

        // Start the updates
        boolean started = nativeGimbal.startUpdateTimer(1000 / GimbalUpdatesPerSecond);

        // Update status
        if (started)
        {
            StatusUpdater.UpdateStatus(TAG, "Gimbal Tracking Started");
        }
        else
        {
            StatusUpdater.UpdateStatus(TAG, "Gimbal Tracking was unable to start");
        }

        // If handler, notify
        if (handler != null)
        {
            handler.onResult(new Result(started));
        }
    }
    //endregion


    //region Callbacks / Event Handlers
    DJIGimbalUpdateAttitudeCallBack onGimbalAttitudeUpdate = new DJIGimbalUpdateAttitudeCallBack()
    {
        @Override
        public void onResult(final DJIGimbalAttitude a)
        {
            // Update variables
            synchronized (currentAttitudeNative)
            {
                currentAttitudeNative = a;
            }
            synchronized (currentAttitude)
            {
                Log.i("GimbalUpdate", "p: " + a.pitch + " r: " + a.roll + " y: " + a.yaw);
                currentAttitude = new Attitude(a.pitch / 10, a.roll / 10, a.yaw / 10);
            }

            // If in continuous mode, call a continuous tick
            if (moveContinuousEnabled)
            {
                moveContinuousTick();
            }

            // If listeners, notify
            if (listener != null)
            {
                listener.onAttitudeChanged(DJIGimbalService.this);
            }
        }
    };

    TimerTask onMoveAbsoluteTimer = new TimerTask()
    {
        @Override
        public void run()
        {
            moveAbsoluteTick();
        }
    };
    //endregion

    //region Public Methods
    @Override
    public AttitudeCapabilities getAttitudeCapabilities()
    {
        verifyCreated();
        if (capabilities == null)
        {
            // Create
            capabilities = new AttitudeCapabilities();

            // Copy from native
            capabilities.maxPitchAngle = capabilitiesNative.maxPitchRotationAngle;
            capabilities.maxRollAngle = capabilitiesNative.maxRollRotationAngle;
            capabilities.maxYawAngle = capabilitiesNative.minYawRotationAngle;
            capabilities.minPitchAngle = capabilitiesNative.minPitchRotationAngle;
            capabilities.minRollAngle = capabilitiesNative.minRollRotationAngle;
            capabilities.minYawAngle = capabilitiesNative.minYawRotationAngle;
            capabilities.pitchAvailable = capabilitiesNative.pitchAvailable;
            capabilities.rollAvailable = capabilitiesNative.rollAvailable;
            capabilities.yawAvailable = capabilitiesNative.yawAvailable;

            // Fill in values not supplied by native
            capabilities.maxPitchSpeed = MaxGimbalSpeed;
            capabilities.maxRollSpeed = MaxGimbalSpeed;
            capabilities.maxYawSpeed = MaxGimbalSpeed;
        }
        return capabilities;
    }

    @Override
    public void moveAbsolute(Attitude attitude)
    {
        // Validate
        if (attitude == null) { throw new InvalidParameterException("attitude cannot be null"); }

        if (!isEnabled()) { return; }

        // Set the target
        synchronized (moveAbsoluteTarget)
        {
            moveAbsoluteTarget.pitch = clampPitch(attitude.pitch * 10d);
            moveAbsoluteTarget.roll = clampRoll(attitude.roll * 10d);
            moveAbsoluteTarget.yaw = clampYaw(attitude.yaw * 10d);
        }

        // Move
        moveAbsoluteTick();

        // TODO: Start timer?
        // moveAbsoluteTimer.scheduleAtFixedRate(onMoveAbsoluteTimer, 500, 500);
    }

    @Override
    public void moveContinuous(Attitude attitude)
    {
        // Validate
        if (attitude == null) { throw new InvalidParameterException("attitude cannot be null"); }

        if (!isEnabled()) { return; }

        // Start or stop continuous mode
        if ((attitude.pitch == 0) && (attitude.roll == 0) && (attitude.yaw == 0))
        {
            // Stop
            moveContinuousEnabled = false;
        }
        else
        {
            /*****************************************************************************
             * Observations:
             *
             * Speed is a value between 0 and 255 (on iOS it's uint8)
             * Top speed (255) is approx equal to 180 units or 18 degrees per second
             * Duration is always approx 1 second from the time move is called
             *
             ****************************************************************************/

            // Clamp
            double pitchSpeed = clampSpeed(attitude.pitch);
            double rollSpeed = clampSpeed(attitude.roll);
            double yawSpeed = clampSpeed(attitude.yaw);

            // Update class variable used in timer ticks
            synchronized (moveContinuousSpeed)
            {
                moveContinuousSpeed.pitch = (pitchSpeed / MaxGimbalSpeed) * 255;
                moveContinuousSpeed.roll = (-rollSpeed / MaxGimbalSpeed) * 255; // Roll is inverted
                moveContinuousSpeed.yaw = (-yawSpeed / MaxGimbalSpeed) * 255; // Yaw is inverted
            }

            // Start
            moveContinuousEnabled = true;
        }
    }

    public void moveRelative(Attitude attitude)
    {
        // Validate
        if (attitude == null) { throw new InvalidParameterException("attitude cannot be null"); }

        if (!isEnabled()) { return; }

        // Scale
        double pitch = attitude.pitch * 10d;
        double roll = attitude.roll * 10d;
        double yaw = attitude.yaw * 10d;

        // Return to default speed
        Log.i(TAG, "Returning gimbal to default speed.");
        setGimbalSpeed(DefaultGimbalSpeed, DefaultGimbalSpeed, DefaultGimbalSpeed);

        // Move native
        moveNative(pitch, roll, yaw, true);
    }

    private int moveDegrees = 0;
    private double moveTarget = -999;
    @Override
    public void moveToFPV()
    {
        if (!isEnabled()) { return; }

        if (moveDegrees == 0)
        {
            moveDegrees = 10;
        }
        else if (moveDegrees == 10)
        {
            moveDegrees = 20;
        }
        else if (moveDegrees == 20)
        {
            moveDegrees = 50;
        }
        else if (moveDegrees == 50)
        {
            moveDegrees = 100;
        }
        else if (moveDegrees == 100)
        {
            moveDegrees = 500;
        }
        else if (moveDegrees == 500)
        {
            moveDegrees = 1000;
        }
        else if (moveDegrees == 1000)
        {
            moveDegrees = 2000;
        }
        else if (moveDegrees == 2000)
        {
            moveDegrees = 5000;
        }

        /*
        if (moveTarget == -999)
        {
            moveTarget = 999;
        }
        else
        {
            moveTarget = -999;
        }
        */


        Log.i(TAG, "MOVE TEST -- Degrees: " + moveDegrees);
        // setGimbalSpeed(moveDegrees, moveDegrees, moveDegrees);
        moveNative(moveTarget, 0d, 0d, true);

        // Angles determined by testing (not documented)
        // moveAbsolute(new Attitude(77, 0, 63));
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
        synchronized (currentAttitude)
        {
            return currentAttitude;
        }
    }

    public boolean isEnabled()
    {
        return true;
    }
    //endregion
}
