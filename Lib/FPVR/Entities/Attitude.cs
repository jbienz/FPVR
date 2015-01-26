using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FPVR
{
    /// <summary>
    /// Represents an orientation.
    /// </summary>
    public struct Attitude
    {
        #region Constructors
        /// <summary>
        /// Initializes a new <see cref="Attitude"/>.
        /// </summary>
        /// <param name="yaw">
        /// The yaw in degrees.
        /// </param>
        /// <param name="pitch">
        /// The pitch in degrees.
        /// </param>
        /// <param name="roll">
        /// The roll in degrees.
        /// </param>
        public Attitude(double yaw, double pitch, double roll)
        {
            this.Yaw = yaw;
            this.Pitch = pitch;
            this.Roll = roll;
        }
        #endregion // Constructors

        #region Public Members
        /// <summary>
        /// The yaw in degrees.
        /// </summary>
        public double Yaw;

        /// <summary>
        /// The pitch in degrees.
        /// </summary>
        public double Pitch;

        /// <summary>
        /// The roll in degrees.
        /// </summary>
        public double Roll;
        #endregion // Public Members
    }
}
