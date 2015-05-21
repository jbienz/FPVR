package com.solersoft.fpvr.fpvrlib;

/**
 * Provides information about battery levels and health.
 */
public interface IBatteryInfo
{
    //region Public Properties
    /**
     * Gets the overall battery level in millivolts. If the vehicle has more than one cell, this is the average or the sum of all cells.
     * @return The overall battery level in millivolts.
     */
    public int getLevel();
    //endregion
}
