package com.solersoft.fpvr.fpvrlib;

import java.util.Collection;

/**
 * Created by jbienz on 5/20/2015.
 */
public interface IVehicleProvider
{
    // region Public Methods
    /// <summary>
    /// Gets a list of all vehicles stored in the provider.
    /// </summary>
    /// <returns>
    /// A <see cref="Collection<VehicleInfo>"/> containing all the vehicles.
    /// </returns>
    public Collection<VehicleInfo> getVehicles();

    /// <summary>
    /// Gets the vehicle that matches the specified info.
    /// </summary>
    /// <param name="info">
    /// The <see cref="VehicleInfo"/> of the vehicle to obtain.
    /// </param>
    /// <returns>
    /// The <see cref="IVehicle"/> instance.
    /// </returns>
    public IVehicle getVehicle(VehicleInfo info);
    // endregion Public Methods
}
