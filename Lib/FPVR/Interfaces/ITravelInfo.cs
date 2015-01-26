using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FPVR
{
    /// <summary>
    /// Provides orientation information a vehicles direction of travel.
    /// </summary>
    public interface ITravelInfo
    {
        /// <summary>
        /// Gets information about the vehicles direction of travel.
        /// </summary>
        /// <remarks>
        /// A <see cref="NavigationAttitude"/> instance with information about the vehicles direction of travel.
        /// </remarks>
        TravelAttitude TravelAttitude { get; }
    }
}