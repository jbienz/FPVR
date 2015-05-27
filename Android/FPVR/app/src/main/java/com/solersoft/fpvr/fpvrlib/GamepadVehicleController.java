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
    private final double maxDegreesPerSecond = 10;
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

        // Look for a controller
        // InputDevice.getDevice(0).
        // InputManager.InputDeviceListener
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
        float zflat = motionEvent.getDevice().getMotionRange(MotionEvent.AXIS_Z).getFlat();
        float rzflat = motionEvent.getDevice().getMotionRange(MotionEvent.AXIS_RZ).getFlat();
        float z = motionEvent.getAxisValue(MotionEvent.AXIS_Z);
        float rz = motionEvent.getAxisValue(MotionEvent.AXIS_RZ);

        if (Math.abs(z) < zflat) { z = 0; }
        if (Math.abs(rz) < rzflat) { rz = 0; }

        // Calc speed per axis
        Attitude att = new Attitude(rz * maxDegreesPerSecond, 0, z * maxDegreesPerSecond);

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
