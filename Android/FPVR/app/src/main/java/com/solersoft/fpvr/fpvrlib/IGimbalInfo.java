package com.solersoft.fpvr.fpvrlib;

/**
 * The interface for reading gimbal state
 */
public interface IGimbalInfo
{
    //region Public Methods
    /**
     * Register a callback to be invoked when gimbal values have changed.
     * @param l The callback that will run
     */
    public void setGimbalListener(GimbalListener l);
    //endregion

    //region Public Properties
    /**
     * Gets the current attitude of the gimbal.
     * @return The current attitude of the gimbal.
     */
    public Attitude getAttitude();

    /**
     * Gets the attitude capabilities of the gimbal.
     * @return An {@link AttitudeCapabilities} that describes the capabilities of the gimbal.
     */
    public AttitudeCapabilities getAttitudeCapabilities();
    //endregion
}
