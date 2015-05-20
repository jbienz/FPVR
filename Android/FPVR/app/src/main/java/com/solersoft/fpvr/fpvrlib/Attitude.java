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
        this.Yaw = yaw;
        this.Pitch = pitch;
        this.Roll = roll;
    }
    //endregion

    //region Public Members
    /**
     * The yaw in degrees.
     */
    public double Yaw;

    /**
     * The pitch in degrees.
     */
    public double Pitch;

    /**
     * The roll in degrees.
     */
    public double Roll;
    //endregion

}
