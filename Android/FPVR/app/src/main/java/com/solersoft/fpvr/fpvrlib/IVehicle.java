package com.solersoft.fpvr.fpvrlib;

import java.util.Collection;

/**
 * Created by jbienz on 5/20/2015.
 */

/**
 * The interface that represents a single vehicle or aircraft.
 */
public interface IVehicle
{
    //region Public Properties
    /**
     * Gets the name of the vehicle.
     * @return The name of the vehicle.
     */
    String getName();

    /**
     * Gets the collection of services provided by the vehicle.
     * @return the collection of services provided by the vehicle.
     */
    Collection<IVehicleService> getServices();
    //endregion
}
