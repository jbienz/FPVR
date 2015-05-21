package com.solersoft.fpvr.fpvrlib;

/**
 * Provides information from a GPS sensor.
 */
public interface IGPSInfo
{
    //region Public Properties
    /**
     * Gets the altitude in meters.
     */
    public double getAltitude();

    /**
     * Gets the current GPS fix level.
     */
    public GPSFix getGPSFix();

    /**
     * Gets the horizontal Dilution of Precision.
     * @return The hoizontal Dilution of Precision if supported; otherwise 0.
     * For more information see
     * <a href="http://en.wikipedia.org/wiki/Dilution_of_precision_(GPS)">Dilution of Precision</a>.
     */
    public double getHorizontalDilutionOfPrecision();

    /**
     * The latitude in degrees.
     */
    public double getLatitude();

    /**
     * The longitude in degrees.
     */
    public double getLongitude();

    /**
     * Gets the number of satellites visible.
     * @return The number of satellites visible if supported; otherwise 0.
     */
    public int getSatellitesVisible();

    /**
     * Gets the vertical Dilution of Precision.
     * @return The vertical Dilution of Precision if supported; otherwise 0.
     * For more information see
     * <a href="http://en.wikipedia.org/wiki/Dilution_of_precision_(GPS)">Dilution of Precision</a>.
     */
    public double getVerticalDilutionOfPrecision();

    /**
     * The vertical speed in meters per second. This is also known as climb rate.
     * @return The vertical speed in meters per second if supported; otherwise 0.
     */
    public double getVerticleSpeed();
    //endregion
}
