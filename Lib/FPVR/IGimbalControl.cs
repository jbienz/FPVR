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
        /// Sets the attitude of the gimbal to the angle most comfortable for first-person view.
        /// </summary>
        void GoToFPV();

        /// <summary>
        /// Sets the attitude of the gimbal.
        /// </summary>
        /// <param name="attitude">
        /// The new <see cref="Attitude"/>.
        /// </param>
        /// <remarks>
        /// It may take time for the gimbal to actually reach the new attitude. 
        /// Gimbals should report their actual attitude by implementing the 
        /// <see cref="IGimbalInfo"/> interface.
        /// </remarks>
        void SetAttitude(Attitude attitude);
    }
}
