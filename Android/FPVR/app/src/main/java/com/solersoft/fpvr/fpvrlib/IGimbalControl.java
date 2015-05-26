package com.solersoft.fpvr.fpvrlib;

/**
 * Created by jbienz on 5/20/2015.
 */
public interface IGimbalControl extends IVehicleService
{
    //region Public Methods
    /**
     * Moves the attitude of the gimbal to the angle most comfortable for first-person view.
     */
    public void moveToFPV();

    /**
     * Causes the gimbal to move to the specified attitude.
     * @param attitude The new attitude to move to.
     */
    public void moveAbsolute(Attitude attitude);

    /**
     * Causes the gimbal to move the relative amount.
     * @param attitude An {@link Attitude} that represents the relative amount to move.
     */
    public void moveRelative(Attitude attitude);
    //endregion
}
