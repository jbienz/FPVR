using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FPVR
{
    /// <summary>
    /// The interface for a vehicle instance.
    /// </summary>
    /// <remarks>
    /// This interface provides limited information about the vehicle state. Vehicles should implement 
    /// additional interfaces like <see cref="IGPSInfo"/> to provide additonal state at runtime.
    /// </remarks>
    public interface IVehicle
    {
        /// <summary>
        /// Gets the name of the vehicle.
        /// </summary>
        string Name { get; }
    }
}
