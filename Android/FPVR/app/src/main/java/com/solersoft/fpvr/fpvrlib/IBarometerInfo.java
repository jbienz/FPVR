package com.solersoft.fpvr.fpvrlib;

/**
 * Provides information from a barometric sensor.
 */
public interface IBarometerInfo
{
    //region Public Properties
    /**
     * Gets the altitude in meters.
     * @return The altitude in meters.
     */
    public double getAltitude();

    /**
     * Gets the vertical speed in meters per second. This is also known as climb rate.
     * @return The vertical speed in meters per second.
     */
    public double getVerticleSpeed();
    //endregion
}
