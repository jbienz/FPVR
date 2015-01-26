using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FPVR
{
    /// <summary>
    /// Represents an orientation for tracking the direction of travel.
    /// </summary>
    public struct TravelAttitude
    {
        #region Constructors
        /// <summary>
        /// Initializes a new <see cref="TravelAttitude"/>.
        /// </summary>
        /// <param name="course">
        /// The course (over ground) in degrees.
        /// </param>
        /// <param name="heading">
        /// The heading in degrees.
        /// </param>
        /// <param name="headingNoCompensation">
        /// The heading in degrees without tilt compensation.
        /// </param>
        /// <param name="pitch">
        /// The pitch in degrees.
        /// </param>
        /// <param name="roll">
        /// The roll in degrees.
        /// </param>
        public TravelAttitude(double course, double heading, double headingNoCompensation, double pitch, double roll)
        {
            this.Course = course;
            this.Heading = heading;
            this.HeadingNoCompensation = heading;
            this.Pitch = pitch;
            this.Roll = roll;
        }
        #endregion // Constructors

        #region Public Members
        /// <summary>
        /// The course (over ground) in degrees if supported; otherwise the same value as <see cref="Heading"/>.
        /// </summary>
        /// <remarks>
        /// <see cref="Course"/> can differ from <see cref="Heading"/> due to factors like wind speed and drift. 
        /// For more information see <see href="http://en.wikipedia.org/wiki/Course_(navigation)">Course</see>.
        /// </remarks>
        public double Course;

        /// <summary>
        /// The heading in degrees.
        /// </summary>
        public double Heading;

        /// <summary>
        /// The heading in degrees without tilt compensation if supported; 
        /// otherwise the same value as <see cref="Heading"/>.
        /// </summary>
        public double HeadingNoCompensation;

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
