using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FPVR
{
    /// <summary>
    /// Provides speed information.
    /// </summary>
    public interface ISpeedInfo
    {
        /// <summary>
        /// The speed in meters per second.
        /// </summary>
        double Speed { get; }
    }
}
