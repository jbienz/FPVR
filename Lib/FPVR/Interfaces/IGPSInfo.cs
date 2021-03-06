﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FPVR
{
    /// <summary>
    /// Defines the different types of GPS Fix.
    /// </summary>
    public enum GPSFix
    {
        Unknown,
        None,
        Fix2D,
        Fix3D,
        Differential
    };

    /// <summary>
    /// Provides information from a GPS sensor.
    /// </summary>
    public interface IGPSInfo
    {
        /// <summary>
        /// The altitude in meters.
        /// </summary>
        double Altitude { get; }

        /// <summary>
        /// Gets the current GPS fix level.
        /// </summary>
        GPSFix GPSFix { get; }

        /// <summary>
        /// Gets the horizontal Dilution of Precision.
        /// </summary>
        /// <value>
        /// The hoizontal Dilution of Precision if supported; otherwise 0.
        /// </value>
        /// For more information see 
        /// <see href="http://en.wikipedia.org/wiki/Dilution_of_precision_(GPS)">Dilution of Precision</see>.
        double HorizontalDilutionOfPrecision { get; }

        /// <summary>
        /// The latitude in degrees.
        /// </summary>
        double Latitude { get; }
        
        /// <summary>
        /// The longitude in degrees.
        /// </summary>
        double Longitude { get; }

        /// <summary>
        /// Gets the number of satellites visible.
        /// </summary>
        /// <value>
        /// The number of satellites visible if supported; otherwise 0.
        /// </value>
        uint SatellitesVisible { get; }

        /// <summary>
        /// Gets the vertical Dilution of Precision.
        /// </summary>
        /// <value>
        /// The vertical Dilution of Precision if supported; otherwise 0.
        /// </value>
        /// For more information see 
        /// <see href="http://en.wikipedia.org/wiki/Dilution_of_precision_(GPS)">Dilution of Precision</see>.
        double VerticalDilutionOfPrecision { get; }

        /// <summary>
        /// The vertical speed in meters per second.
        /// </summary>
        /// <value>
        /// The vertical speed in meters per second if supported; otherwise 0.
        /// </value>
        /// <remarks>
        /// This is also known as climb rate.
        /// </remarks>
        double VerticleSpeed { get; }
    }
}
