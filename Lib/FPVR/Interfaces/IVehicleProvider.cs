using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;

namespace FPVR
{
    /// <summary>
    /// The interface for a provider of vehicles.
    /// </summary>
    public interface IVehicleProvider
    {
        #region Public Methods
        /// <summary>
        /// Gets a list of all vehicles stored in the provider.
        /// </summary>
        /// <returns>
        /// A <see cref="Collection<VehicleInfo>"/> containing all the vehicles.
        /// </returns>
        Collection<VehicleInfo> GetVehicles();

        /// <summary>
        /// Gets the vehicle that matches the specified info.
        /// </summary>
        /// <param name="info">
        /// The <see cref="VehicleInfo"/> of the vehicle to obtain.
        /// </param>
        /// <returns>
        /// The <see cref="IVehicle"/> instance.
        /// </returns>
        IVehicle GetVehicle(VehicleInfo info);
        #endregion // Public Methods
    }
}
