package com.solersoft.fpvr.fpvrdji;

import com.solersoft.fpvr.fpvrlib.*;

import java.security.InvalidParameterException;

/**
 * An implementation of the {@link IGimbalControl} interface for DJI Aircraft.
 */
public class DJIGimbalControl implements IGimbalControl
{
    //region Member Variables
    private DJIVehicle djiVehicle;
    //endregion

    //region Constructors
    public DJIGimbalControl(DJIVehicle djiVehicle)
    {
        // Validate
        if (djiVehicle == null) {throw new InvalidParameterException("djiVehicle may not be null"); }

        // Store
        this.djiVehicle = djiVehicle;
    }
    //endregion

    //region Public Methods
    @Override
    public void GoToFPV()
    {
        if (!isEnabled()) { return; }
    }

    @Override
    public void SetAttitude(Attitude attitude)
    {
        if (!isEnabled()) { return; }
    }
    //endregion

    //region Public Properties
    @Override
    public boolean isEnabled()
    {
        return djiVehicle.isInitialized();
    }
    //endregion
}
