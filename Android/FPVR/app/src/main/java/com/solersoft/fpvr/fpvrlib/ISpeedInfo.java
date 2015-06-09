package com.solersoft.fpvr.fpvrlib;

/**
 * The interface for a class that provides speed information.
 */
public interface ISpeedInfo extends IVehicleService
{
    //region Public Properties
    /**
     * Gets the forward speed in meters per second.
     */
    public double getForwardSpeed();
    //endregion
}
