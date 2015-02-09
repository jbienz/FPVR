using FPVR;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;

namespace SamplePlugin
{
    public class ControllerCraft : 
        IVehicle, 
        IBarometerInfo, 
        IBatteryInfo, 
        IGimbalControl, 
        IGimbalInfo, 
        IGPSInfo, 
        ISpeedInfo,
        ITravelInfo,
        IUpdateRequired
    {
        #region Constants
        static private readonly Attitude fpvAttitude = new Attitude();
        #endregion // Constants

        #region Member Variables
        private double altitude;
        private Attitude gimbalAttitude;
        private double latitude;
        private double longitude;
        private double speed;
        private Attitude targetGimbalAttitude;
        private TravelAttitude travelAttitude;
        private double verticalSpeed;
        private float yaw;
        #endregion // Member Variables
        
        #region IBarometerInfo Interface
        double IBarometerInfo.Altitude
        {
            get { return altitude; }
        }

        double IBarometerInfo.VerticleSpeed
        {
            get { return verticalSpeed; }
        }
        #endregion // IBarometerInfo Interface

        #region IBatteryInfo Interface
        uint IBatteryInfo.Level
        {
            get { return 22200; /* 22.2 volts */ }
        }
        #endregion // IBatteryInfo Interface

        #region IGimbalControl Interface
        void IGimbalControl.GoToFPV()
        {
            targetGimbalAttitude = fpvAttitude;
        }

        void IGimbalControl.SetAttitude(Attitude attitude)
        {
            // Set target instead
            targetGimbalAttitude = attitude;
        }
        #endregion // IGimbalControl Interface

        #region IGimbalInfo Interface
        Attitude IGimbalInfo.Attitude
        {
            get { return gimbalAttitude; }
        }
        #endregion // IGimbalInfo Interface

        #region IGPSInfo Interface
        double IGPSInfo.Altitude
        {
            get { return altitude; }
        }

        GPSFix IGPSInfo.GPSFix
        {
            get { return GPSFix.Fix3D; }
        }

        double IGPSInfo.HorizontalDOP
        {
            get { return 2; }
        }

        double IGPSInfo.Latitude
        {
            get { return latitude; }
        }

        double IGPSInfo.Longitude
        {
            get { return longitude; }
        }

        uint IGPSInfo.SatellitesVisible
        {
            get { return 6; }
        }

        double IGPSInfo.VerticalDOP
        {
            get { return 2; }
        }

        double IGPSInfo.VerticleSpeed
        {
            get { return verticalSpeed; }
        }
        #endregion // IGPSInfo Interface

        #region ISpeedInfo Interface
        double ISpeedInfo.Speed
        {
            get { return speed; }
        }
        #endregion // ISpeedInfo Interface

        #region ITravelInfo Interface
        TravelAttitude ITravelInfo.TravelAttitude
        {
            get { return travelAttitude; }
        }
        #endregion // ITravelInfo Interface

        #region IUpdateRequired Interface
        void IUpdateRequired.Update(float deltaTime)
        {
            yaw += Input.GetAxis("Yaw") * 360;

            // Yaw, Pitch, Roll, Throttle
            travelAttitude.Course = (Input.GetAxis("Yaw") * 45);
            travelAttitude.Heading = travelAttitude.Course;
            travelAttitude.HeadingNoCompensation = travelAttitude.Course;

            travelAttitude.Pitch = (Input.GetAxis("Pitch") * 45);
            travelAttitude.Roll = (Input.GetAxis("Roll") * 45);
        }
        #endregion // IUpdateRequired Interface

        #region IVehicle Interface
        string IVehicle.Name
        {
            get { return "Controller Craft"; }
        }
        #endregion // IVehicle Interface
    }
}
