using FPVR;
using SolerSoft.Plugins;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;

namespace SamplePlugin
{
    [PluginInfo("Sample Vehicles", Description="Sample vehicles from the SamplePlugin project.")]
    public class SampleVehicleProvider : IVehicleProvider
    {
        private Collection<VehicleInfo> vehicles;

        public Collection<VehicleInfo> GetVehicles()
        {
            if (vehicles == null)
            {
                vehicles = new Collection<VehicleInfo>()
                {
                    new VehicleInfo()
                    {
                        Id = "CC",
                        ModelName = "ControllerCraft",
                        Name = "Controller Craft",
                        Provider = this,
                    }
                };
            }

            return vehicles;
        }

        public IVehicle GetVehicle(VehicleInfo info)
        {
            switch (info.Id)
            {
                case "CC":
                    return new ControllerCraft();
                default:
                    throw new InvalidOperationException("Unknown ID");
            }
        }
    }
}
