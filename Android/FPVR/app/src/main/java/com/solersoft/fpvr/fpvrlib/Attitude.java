package com.solersoft.fpvr.fpvrlib;

/**
 * Created by jbienz on 5/20/2015.
 */
public class Attitude
{
    //region Constructors

    /**
     * Initializes a new Attitude instance where all angles are zero.
     */
    public Attitude(){}

    /**
     * Initializes a new Attitude instance.
     * @param pitch The pitch in degrees.
     * @param roll The roll in degrees.
     * @param yaw The yaw in degrees.
     */
    public Attitude(double pitch, double roll, double yaw)
    {
        this.pitch = pitch;
        this.roll = roll;
        this.yaw = yaw;
    }
    //endregion

    //region Public Members
    /**
     * The yaw in degrees.
     */
    public double yaw=0;

    /**
     * The pitch in degrees.
     */
    public double pitch=0;

    /**
     * The roll in degrees.
     */
    public double roll=0;
    //endregion

}
