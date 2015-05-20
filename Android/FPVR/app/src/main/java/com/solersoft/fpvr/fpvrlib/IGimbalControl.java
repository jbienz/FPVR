package com.solersoft.fpvr.fpvrlib;

/**
 * Created by jbienz on 5/20/2015.
 */
public interface IGimbalControl extends IVehicleService
{
    /**
     * Sets the attitude of the gimbal to the angle most comfortable for first-person view.
     */
    void GoToFPV();

    /**
     * Sets the attitude of the gimbal.
     * @param attitude The new Attitude.
     */
    void SetAttitude(Attitude attitude);
}
