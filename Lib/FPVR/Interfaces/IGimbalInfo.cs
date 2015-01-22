using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FPVR
{
    public interface IGimbalInfo
    {
        /// <summary>
        /// Gets the current attitude of the gimbal.
        /// </summary>
        /// <value>
        /// The current attitude of the gimbal.
        /// </value>
        Attitude Attitude { get; }
    }
}
