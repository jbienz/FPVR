package com.solersoft.fpvr.fpvrlib;

/**
 * Created by jbienz on 5/20/2015.
 */
public class TravelAttitude
{
    // region Constructors
    /**
     * Initializes a new TravelAttitude instance.
     * @param course The course (over ground) in degrees.
     * @param heading The heading in degrees.
     * @param headingNoCompensation The heading in degrees without tilt compensation.
     * @param pitch The pitch in degrees.
     * @param roll The roll in degrees.
     */
    public TravelAttitude(double course, double heading, double headingNoCompensation, double pitch, double roll)
    {
        this.course = course;
        this.heading = heading;
        this.headingNoCompensation = headingNoCompensation;
        this.pitch = pitch;
        this.roll = roll;
    }
    // endregion Constructors

    // region Public Members
    /**
     * The course (over ground) in degrees if supported; otherwise the same value as {@link #heading}.
     */
    public double course;

    /**
     * The heading in degrees.
     */
    public double heading;

    /**
     *  The heading in degrees without tilt compensation if supported; otherwise the same value as {@link #heading}.
     */
    public double headingNoCompensation;

    /**
     * The pitch in degrees.
     */
    public double pitch;

    /**
     * The roll in degrees.
     */
    public double roll;
    // endregion Public Members
}
