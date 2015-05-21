using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FPVR
{
    /// <summary>
    /// The interface for reading gimbal state.
    /// </summary>
    public interface IGimbalInfo
    {
        /// <summary>
        /// Gets the current attitude of the gimbal.
        /// </summary>
        /// <value>
        /// The current attitude of the gimbal.
        /// </value>
        Attitude Attitude { get; }

        /// <summary>
        /// Raised when the value of the <see cref="Attitude"/> property has changed.
        /// </summary>
        public event EventHandler AttitudeChanged;
    }
}
