package com.solersoft.fpvr.fpvrlib;

/**
 * Provides information about battery levels and health.
 */
public interface IBatteryInfo
{
    //region Public Methods
    /**
     * Register a callback to be invoked when battery values have changed.
     * @param l The callback that will run
     */
    public void setBatteryListener(BatteryListener l);
    //endregion

    //region Public Properties
    /**
     * Gets the percentage of total capacity at which the aircraft will automatically land.
     * @return The percentage at which the aircraft will automatically land or -1 if the value is not available.
     */
    public double getAutoLandPercent();

    /**
     * Gets the remaining power in the battery as a percentage of total capacity.
     * @return The remaining power in the battery as a percentage of total capacity.
     */
    public double getRemainingPercent();

    /**
     * Gets the percentage of total capacity at which the aircraft will automatically return home.
     * @return The percentage at which the aircraft will return home or -1 if the value is not available.
     */
    public double getReturnHomePercent();

    /**
     * Gets the overall battery level in millivolts. If the vehicle has more than one cell, this is the average or the sum of all cells.
     * @return The overall battery level in millivolts.
     */
    public double getVoltage();
    //endregion
}
