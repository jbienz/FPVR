using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FPVR
{
    /// <summary>
    /// Defines the different types of vehicle.
    /// </summary>
    public enum VehicleType
    {
        Unknown,
        Drone,
        Plane,
        Helicopter,
        Car,
        Truck,
        Boat,
        Hovercraft,
        Other
    };
    
    /// <summary>
    /// Provides information about a specific vehicle.
    /// </summary>
    public class VehicleInfo
    {
        /// <summary>
        /// Gets or sets an ID for the vehicle that is unique to the provider.
        /// </summary>
        public string Id { get; set; }

        /// <summary>
        /// Gets or sets the 3D model name, if any, that can be used to represent the vehicle.
        /// </summary>
        public string ModelName { get; set; }

        /// <summary>
        /// Gets or sets the name of the vehicle. 
        /// </summary>
        public string Name { get; set; }

        /// <summary>
        /// Gets or sets the provider that contains the vehicle.
        /// </summary>
        public IVehicleProvider Provider { get; set; }

        /// <summary>
        /// Gets or set the general type of the vehicle.
        /// </summary>
        public VehicleType Type { get; set; }
    }
}
