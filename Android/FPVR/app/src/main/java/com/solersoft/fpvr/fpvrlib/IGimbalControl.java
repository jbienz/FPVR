package com.solersoft.fpvr.fpvrlib;

/**
 * Created by jbienz on 5/20/2015.
 */
public interface IGimbalControl extends IVehicleService
{
    //region Public Methods
    /**
     * Sets the attitude of the gimbal to the angle most comfortable for first-person view.
     */
    public void goToFPV();
    //endregion

    //region Pubic Properties
    /**
     * Causes the gimbal to move to the specified attitude.
     * @param attitude The new attitude to move to.
     */
    public void goToAttitude(Attitude attitude);
    //endregion
}
