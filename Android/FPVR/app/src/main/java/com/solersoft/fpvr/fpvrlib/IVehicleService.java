package com.solersoft.fpvr.fpvrlib;

/**
 * Created by jbienz on 5/20/2015.
 */

/**
 * Represents a service available within a vehicle, such as gimbal control or GPS Info.
 */
public interface IVehicleService
{
    //region Public Properties
    /**
     * Gets a value that indicates if the service has been enabled.
     * @return <code>true</code> if the service is enabled; otherwise <code>false</code>.
     */
    boolean isEnabled();
    //endregion
}
