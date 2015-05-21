using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FPVR
{
    /// <summary>
    /// The interface for a gimbal controller.
    /// </summary>
    public interface IGimbalControl
    {
        /// <summary>
        /// Causes the gimbal to move to the specified attitude.
        /// </summary>
        /// <value>
        /// The new attitude to move to.
        /// </value>
        /// <remarks>
        /// It may take time for the gimbal to actually reach the new attitude. 
        /// Gimbals should report their actual attitude by implementing the 
        /// <see cref="IGimbalInfo"/> interface.
        /// </remarks>
        void GoToAttitude(Attitude attitude);

        /// <summary>
        /// Sets the attitude of the gimbal to the angle most comfortable for first-person view.
        /// </summary>
        void GoToFPV();
    }
}
