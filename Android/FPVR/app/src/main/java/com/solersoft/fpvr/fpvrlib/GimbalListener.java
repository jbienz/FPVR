package com.solersoft.fpvr.fpvrlib;

/**
 * Interface for handling gimbal events.
 */
public interface GimbalListener
{
    /**
     * Called when the value of the {@link IGimbalInfo#getAttitude()} property has changed.
     */
    public void onAttitudeChanged(IGimbalInfo gimbal);
}
