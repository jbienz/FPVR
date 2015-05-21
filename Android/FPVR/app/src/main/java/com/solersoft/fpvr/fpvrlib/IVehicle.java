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

    public void connect();

    public void disconnect();

    //region Public Properties
    /**
     * Gets the name of the vehicle.
     * @return The name of the vehicle.
     */
    public String getName();

    /**
     * Gets the collection of services provided by the vehicle.
     * @return the collection of services provided by the vehicle.
     */
    public Collection<IVehicleService> getServices();

    /**
     * Gets a value that indicates if the vehicle is connected.
     * @return @return <code>true</code> if the vehicle is connected; otherwise <code>false</code>.
     */
    public boolean isConnected();
    //endregion
}
