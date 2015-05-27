package com.solersoft.fpvr.fpvrlib;

/**
 * Represents the capabilities and ranges of the attitudes supported by the vehicle.
 */
public class AttitudeCapabilities
{
    //region Constructors
    /**
     * Initializes a new {@link AttitudeCapabilities} instance.
     */
    public AttitudeCapabilities() {}
    //endregion

    //region Public Fields
    /**
     * Gets a value that indicates if pitch is available.
     */
    public boolean pitchAvailable = false;
    
    /**
     * Gets a value that indicates if roll is available.
     */
    public boolean rollAvailable = false;

    /**
     * Gets a value that indicates if yaw is available.
     */
    public boolean yawAvailable = false;

    /**
     * Gets the maximum pitch angle, if supported; otherwise -1.
     */
    public float maxPitchAngle = -1.0F;

    /**
     * Gets the minimum pitch angle, if supported; otherwise -1.
     */
    public float minPitchAngle = -1.0F;

    /**
     * Gets the maximum roll angle, if supported; otherwise -1.
     */
    public float maxRollAngle = -1.0F;

    /**
     * Gets the minimum roll angle, if supported; otherwise -1.
     */
    public float minRollAngle = -1.0F;

    /**
     * Gets the maximum yaw angle, if supported; otherwise -1.
     */
    public float maxYawAngle = -1.0F;

    /**
     * Gets the minimum yaw angle, if supported; otherwise -1.
     */
    public float minYawAngle = -1.0F;
    //endregion
}
