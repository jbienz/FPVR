package com.solersoft.fpvr.fpvrlib;

/**
 * Created by jbienz on 5/20/2015.
 */
public interface IGimbalControl extends IGimbalInfo, IVehicleService
{
    //region Public Methods
    /**
     * Moves the attitude of the gimbal to the angle most comfortable for first-person view.
     */
    public void moveToFPV();

    /**
     * Causes the gimbal to move to the specified attitude.
     * @param attitude An {@link Attitude} value that indicates the new absolute direction.
     */
    public void moveAbsolute(Attitude attitude);

    /**
     * Causes the gimbal to move to move at a continuous speed.
     * @param attitude An {@link Attitude} value where each member indicates the number of degrees per second.
     */
    public void moveContinuous(Attitude attitude);

    /**
     * Causes the gimbal to move the relative amount.
     * @param attitude An {@link Attitude} value where each member indicates the relative amount to move.
     */
    public void moveRelative(Attitude attitude);
    //endregion
}
