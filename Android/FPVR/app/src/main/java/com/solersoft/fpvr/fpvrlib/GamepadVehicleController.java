package com.solersoft.fpvr.fpvrlib;

import android.hardware.input.InputManager;
import android.view.InputDevice;
import android.view.MotionEvent;

import com.solersoft.fpvr.util.TypeUtils;

import java.security.InvalidParameterException;
import java.util.Iterator;

/**
 * A controller for a vehicle that uses a gamepad attached to the system.
 */
public class GamepadVehicleController implements ISupportInitialize
{
    private IGimbalControl gimbal;
    private boolean initialized;
    private IVehicle vehicle;

    public GamepadVehicleController(IVehicle vehicle)
    {
        // Validate
        if (vehicle == null) { throw new InvalidParameterException("vehicle cannot be null."); }

        // Store
        this.vehicle = vehicle;
    }

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

    @Override
    public boolean isInitialized()
    {
        return initialized;
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
        float z = motionEvent.getAxisValue(MotionEvent.AXIS_Z);
        float rz = motionEvent.getAxisValue(MotionEvent.AXIS_RZ);

        // Degrees per second
        final double dps = 20;

        // Calc relative degrees
        Attitude att = new Attitude(rz * dps, z * dps, 0);

        // TODO: What about throttling?
        // Move relative degrees
        gimbal.moveRelative(att);

        // Done
        return handled;
    }
}
