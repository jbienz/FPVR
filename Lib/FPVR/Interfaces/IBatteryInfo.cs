using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FPVR
{
    /// <summary>
    /// Provides information about battery levels and health.
    /// </summary>
    public interface IBatteryInfo
    {
        /// <summary>
        /// Gets the overall battery level in millivolts.
        /// </summary>
        /// <remarks>
        /// If the entity has more than one cell, this is the average or the sum of all cells.
        /// </remarks>
        uint Level { get; }
    }
}
