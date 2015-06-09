package com.solersoft.fpvr.fpvrlib;

import java.util.Collection;

/**
 * Provides information about battery levels and health.
 */
public interface IBatteryInfo extends IVehicleService
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
     * Gets the percentage of remaining power at which the battery should be considered critical.
     * @return The percentage of remaining power at which the battery should be considered critical.
     */
    public double getCriticalPercent();

    /**
     * Gets the collection of events that happen at various battery levels.
     * @return the collection of events that happen at various battery levels.
     */
    public Collection<BatteryLevelEvent> getLevelEvents();

    /**
     * Gets the percentage of remaining power at which the battery should be considered low.
     * @return The percentage of remaining power at which the battery should be considered low.
     */
    public double getLowPercent();

    /**
     * Gets the remaining power in the battery as a percentage of total capacity.
     * @return The remaining power in the battery as a percentage of total capacity.
     */
    public double getRemainingPercent();

    /**
     * Gets the overall battery level in millivolts. If the vehicle has more than one cell, this is the average or the sum of all cells.
     * @return The overall battery level in millivolts.
     */
    public double getVoltage();
    //endregion
}
