package com.solersoft.fpvr.fpvrlib;

/**
 * Interface for handling battery events.
 */
public interface BatteryListener
{
    /**
     * Called when values of the {@link IBatteryInfo} have changed.
     */
    public void onBatteryChanged(IBatteryInfo battery);
}
