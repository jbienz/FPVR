using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FPVR
{
    /// <summary>
    /// Provides orientation information, usually from a gyroscope.
    /// </summary>
    public interface IOrientationInfo
    {
        /// <summary>
        /// Gets the course (over ground) in degrees.
        /// </summary>
        /// <value>
        /// The course (over ground) in degrees if supported; otherwise the same value as <see cref="Heading"/>.
        /// </value>
        /// <remarks>
        /// <see cref="Course"/> can differ from <see cref="Heading"/> due to factors like wind speed and drift. 
        /// For more information see <see href="http://en.wikipedia.org/wiki/Course_(navigation)">Course</see>.
        /// </remarks>
        double Course { get; }

        /// <summary>
        /// Gets the heading in degrees.
        /// </summary>
        /// <value>
        /// The heading in degrees.
        /// </value>
        double Heading { get; }

        /// <summary>
        /// Gets the heading in degrees without tilt compensation.
        /// </summary>
        /// <value>
        /// The heading in degrees without tilt compensation if supported; 
        /// otherwise the same value as <see cref="Heading"/>.
        /// </value>
        double HeadingNoCompensation { get; }

        /// <summary>
        /// Gets the pitch in degrees.
        /// </summary>
        /// <value>
        /// The pitch in degrees.
        /// </value>
        double Pitch { get; }

        /// <summary>
        /// Gets the roll in degrees.
        /// </summary>
        /// <value>
        /// The roll in degrees.
        /// </value>
        double Roll { get; }
    }
}