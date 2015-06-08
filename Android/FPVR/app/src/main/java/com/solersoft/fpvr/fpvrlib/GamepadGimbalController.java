package com.solersoft.fpvr.fpvrlib;

import android.view.MotionEvent;

import com.solersoft.fpvr.util.CollectionUtils;
import com.solersoft.fpvr.util.TypeUtils;

import java.security.InvalidParameterException;

/**
 * A service that controls a vehicle gimbal using a gamepad.
 */
public class GamepadGimbalController
{
    //region Constants
    private double maxDegreesPerSecond = 10;
    //endregion

    //region Member Variables
    private IGimbalControl gimbal;
    //endregion

    //region Constructors
    public GamepadGimbalController(IGimbalControl gimbal)
    {
        // Validate
        if (gimbal == null) { throw new InvalidParameterException("gimbal cannot be null."); }

        // Store
        this.gimbal = gimbal;

        AttitudeCapabilities caps = gimbal.getAttitudeCapabilities();
        double max = 0;
        max = Math.max(max, caps.maxPitchSpeed);
        max = Math.max(max, caps.maxRollSpeed);
        max = Math.max(max, caps.maxYawSpeed);
        if (max > 0)
        {
            maxDegreesPerSecond = max;
        }
    }
    //endregion

    //region Public Methods
    public boolean handleMotionEvent(MotionEvent motionEvent)
    {
        // How much rotation?
        float xflat = motionEvent.getDevice().getMotionRange(MotionEvent.AXIS_X).getFlat();
        float zflat = motionEvent.getDevice().getMotionRange(MotionEvent.AXIS_Z).getFlat();
        float rzflat = motionEvent.getDevice().getMotionRange(MotionEvent.AXIS_RZ).getFlat();
        float x = motionEvent.getAxisValue(MotionEvent.AXIS_X);
        float z = motionEvent.getAxisValue(MotionEvent.AXIS_Z);
        float rz = motionEvent.getAxisValue(MotionEvent.AXIS_RZ);

        if (Math.abs(x) < xflat) { x = 0; }
        if (Math.abs(z) < zflat) { z = 0; }
        if (Math.abs(rz) < rzflat) { rz = 0; }

        // Calc speed per axis
        Attitude att = new Attitude(rz * maxDegreesPerSecond, x * maxDegreesPerSecond, z * maxDegreesPerSecond);

        // Update constant motion
        gimbal.moveContinuous(att);

        // Handled
        return true;
    }
    //endregion
}
