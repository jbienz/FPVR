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
        this.Course = course;
        this.Heading = heading;
        this.HeadingNoCompensation = heading;
        this.Pitch = pitch;
        this.Roll = roll;
    }
    // endregion Constructors

    // region Public Members
    /**
     * The course (over ground) in degrees if supported; otherwise the same value as {@link #Heading}.
     */
    public double Course;

    /**
     * The heading in degrees.
     */
    public double Heading;

    /**
     *  The heading in degrees without tilt compensation if supported; otherwise the same value as {@link #Heading}.
     */
    public double HeadingNoCompensation;

    /**
     * The pitch in degrees.
     */
    public double Pitch;

    /**
     * The roll in degrees.
     */
    public double Roll;
    // endregion Public Members
}
