using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FPVR
{
    /// <summary>
    /// The interface for a class that provides speed information.
    /// </summary>
    public interface ISpeedInfo
    {
        /// <summary>
        /// Gets the forward speed in meters per second.
        /// </summary>
        double ForwardSpeed { get; }
    }
}
