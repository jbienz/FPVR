package com.solersoft.fpvr.fpvrlib;

/**
 * Created by jbienz on 5/20/2015.
 */
public class Attitude
{
    //region Constructors
    /**
     * Initializes a new Attitude instance.
     * @param yaw The yaw in degrees.
     * @param pitch The pitch in degrees.
     * @param roll The roll in degrees.
     */
    public Attitude(double yaw, double pitch, double roll)
    {
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
    }
    //endregion

    //region Public Members
    /**
     * The yaw in degrees.
     */
    public double yaw;

    /**
     * The pitch in degrees.
     */
    public double pitch;

    /**
     * The roll in degrees.
     */
    public double roll;
    //endregion

}
