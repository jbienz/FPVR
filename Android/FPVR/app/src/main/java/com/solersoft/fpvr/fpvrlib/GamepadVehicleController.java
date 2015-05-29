package com.solersoft.fpvr.fpvrlib;

import android.view.MotionEvent;

import com.solersoft.fpvr.util.TypeUtils;

import java.security.InvalidParameterException;

/**
 * A controller for a vehicle that uses a gamepad attached to the system.
 */
public class GamepadVehicleController implements ISupportInitialize
{
    //region Constants
    private double maxDegreesPerSecond = 10;
    //endregion

    //region Member Variables
    private IGimbalControl gimbal;
    private boolean initialized;
    private IVehicle vehicle;
    //endregion

    //region Constructors
    public GamepadVehicleController(IVehicle vehicle)
    {
        // Validate
        if (vehicle == null) { throw new InvalidParameterException("vehicle cannot be null."); }

        // Store
        this.vehicle = vehicle;
    }
    //endregion

    //region Public Methods
    @Override
    public void initialize()
    {
        // Don't initialize again
        if (initialized) { return; }

        // Initialized
        initialized = true;

        // Look for gimbal service
        for (IVehicleService service : vehicle.getServices())
        {
            gimbal = TypeUtils.as(IGimbalControl.class, service);
            if (gimbal != null) { break; }
        }

        // If we have a gimbal, try to get capabilities and update max speed
        if (gimbal != null)
        {
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
    }

    public boolean handleMotionEvent(MotionEvent motionEvent)
    {
        // Not handled
        boolean handled = false;

        // If not initialized, ignore
        if (!initialized) { return handled; }

        // If no gimbal, bail
        if (gimbal == null) { return handled; }

        // Now we're handling it
        handled = true;

        // How much rotation?
        // TODO: Take into account deadzones via InputDevice.MotionRange.getFlat()
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

        // Done
        return handled;
    }
    //endregion

    //region Public Properties
    @Override
    public boolean isInitialized()
    {
        return initialized;
    }
    //endregion
}
