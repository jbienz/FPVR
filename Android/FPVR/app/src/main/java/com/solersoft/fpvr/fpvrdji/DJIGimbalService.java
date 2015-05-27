package com.solersoft.fpvr.fpvrdji;

import android.util.Log;

import com.solersoft.fpvr.fpvrlib.*;

import java.security.InvalidParameterException;
import java.util.Timer;
import java.util.TimerTask;

import dji.sdk.api.DJIDrone;
import dji.sdk.api.Gimbal.DJIGimbalAttitude;
import dji.sdk.api.Gimbal.DJIGimbalCapacity;
import dji.sdk.api.Gimbal.DJIGimbalRotation;
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
    private static double ContinuousUpdatesPerSecond = 4;
    //endregion

    //region Member Variables
    private Attitude currentAttitude = new Attitude(0,0,0);
    private DJIGimbalAttitude currentAttitudeNative = new DJIGimbalAttitude();
    private AttitudeCapabilities capabilities;
    private DJIGimbalCapacity capabilitiesNative;
    private boolean initialized;
    private boolean initializing;
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
        if (!moveContinuousEnabled) { return; }

        // Calculate degrees for this tick
        double pitch = moveContinuousSpeed.pitch / ContinuousUpdatesPerSecond;
        double roll = moveContinuousSpeed.roll / ContinuousUpdatesPerSecond;
        double yaw = moveContinuousSpeed.yaw / ContinuousUpdatesPerSecond;

        // HACK: Since we don't have speed
        if (pitch > 0) { pitch = 100; }
        if (pitch < 0) { pitch = -100; }
        if (roll > 0) { roll = 100; }
        if (roll < 0) { roll = -100; }
        if (yaw > 0) { yaw = 100; }
        if (yaw < 0) { yaw = -100; }
        
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
        DJIDrone.getDjiGimbal().updateGimbalAttitude(pr, rr, yr);
    }

    private void verifyInitialized()
    {
        if (!initialized) { throw new UnsupportedOperationException("This member cannot be called before the class is initialized."); }
    }
    //endregion

    //region Callbacks / Event Handlers
    DJIGimbalUpdateAttitudeCallBack onGimbalAttitudeUpdate = new DJIGimbalUpdateAttitudeCallBack()
    {
        @Override
        public void onResult(final DJIGimbalAttitude a)
        {
            synchronized (currentAttitudeNative)
            {
                currentAttitudeNative = a;
            }
            synchronized (currentAttitude)
            {
                Log.d("GimbalUpdate", "p: " + a.pitch + " r: " + a.roll + " y: " + a.yaw);
                currentAttitude = new Attitude(a.pitch / 10, a.roll / 10, a.yaw / 10);
            }
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
        verifyInitialized();
        if (capabilities == null)
        {
            capabilities = new AttitudeCapabilities();
            capabilities.maxPitchAngle = capabilitiesNative.maxPitchRotationAngle;
            capabilities.maxRollAngle = capabilitiesNative.maxRollRotationAngle;
            capabilities.maxYawAngle = capabilitiesNative.minYawRotationAngle;
            capabilities.minPitchAngle = capabilitiesNative.minPitchRotationAngle;
            capabilities.minRollAngle = capabilitiesNative.minRollRotationAngle;
            capabilities.minYawAngle = capabilitiesNative.minYawRotationAngle;
            capabilities.pitchAvailable = capabilitiesNative.pitchAvailable;
            capabilities.rollAvailable = capabilitiesNative.rollAvailable;
            capabilities.yawAvailable = capabilitiesNative.yawAvailable;
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

        // Update
        synchronized (moveContinuousSpeed)
        {
            moveContinuousSpeed = new DJIGimbalAttitude();
            moveContinuousSpeed.pitch = attitude.pitch * 10d;
            moveContinuousSpeed.roll = attitude.roll * 10d;
            moveContinuousSpeed.yaw = attitude.yaw * 10d;
        }

        // Start or stop timer?
        if ((attitude.pitch == 0) && (attitude.roll == 0) && (attitude.yaw == 0))
        {
            // Stop?
            if (moveContinuousEnabled)
            {
                moveContinuousTimer.cancel();
                moveContinuousTimer = null;
                moveContinuousEnabled = false;
            }
        }
        else
        {
            // Start?
            if (!moveContinuousEnabled)
            {
                moveContinuousEnabled = true;
                moveContinuousTimer = new Timer();
                moveContinuousTimer.scheduleAtFixedRate(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        moveContinuousTick();
                    }
                }, 0, 1000 / (int) ContinuousUpdatesPerSecond);
            }
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

        // Move native
        moveNative(pitch, roll, yaw, true);
    }

    @Override
    public void moveToFPV()
    {
        if (!isEnabled()) { return; }

        // Angles determined by testing (not documented)
        moveAbsolute(new Attitude(77, 0, 63));
    }

    @Override
    public void initialize()
    {
        // If already tracking, bail
        if (initialized || initializing) { return; }

        // Initializing
        initializing = true;

        // Get the capacity (min / max values) of the gimbal
        capabilitiesNative = DJIDrone.getDjiGimbal().getDJIGimbalCapacity();

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
        DJIDrone.getDjiGimbal().setGimbalUpdateAttitudeCallBack(onGimbalAttitudeUpdate);

        // Start the updates
        initialized = DJIDrone.getDjiGimbal().startUpdateTimer(250);

        // No longer initializing
        initializing = false;

        // Update status
        if (initialized)
        {
            StatusUpdater.UpdateStatus(TAG, "Gimbal Tracking Started");
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
        synchronized (currentAttitude)
        {
            return currentAttitude;
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
