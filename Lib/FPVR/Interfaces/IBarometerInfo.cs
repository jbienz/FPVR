using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FPVR
{
    /// <summary>
    /// Provides information from a barometric sensor.
    /// </summary>
    public interface IBarometerInfo
    {
        /// <summary>
        /// The altitude in meters.
        /// </summary>
        double Altitude { get; }

        /// <summary>
        /// The vertical speed in meters per second.
        /// </summary>
        /// <remarks>
        /// This is also known as climb rate.
        /// </remarks>
        double VerticleSpeed { get; }
    }
}
