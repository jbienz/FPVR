package com.solersoft.fpvr.fpvrlib;

/**
 * Created by jbienz on 5/20/2015.
 */
public class VehicleInfo
{
    //region Member Variables
    private String id;
    private String modelName;
    private String name;
    private IVehicleProvider provider;
    private VehicleType type = VehicleType.Unknown;
    //endregion

    /**
     * Gets an ID for the vehicle that is unique to the provider.
     * @return An ID for the vehicle that is unique to the provider.
     */
    public String getId(){ return id; }

    /**
     * Sets an ID for the vehicle that is unique to the provider.
     * @param value An ID for the vehicle that is unique to the provider.
     */
    public void setId(String value) { id = value; }

    /**
     * Gets the 3D model name, if any, that can be used to represent the vehicle.
     * @return The 3D model name, if any, that can be used to represent the vehicle.
     */
    public String getModelName(){ return modelName; }

    /**
     * Sets the 3D model name, if any, that can be used to represent the vehicle.
     * @param value The 3D model name, if any, that can be used to represent the vehicle.
     */
    public void setModelName(String value) { modelName = value; }

    /**
     * Gets the name of the vehicle.
     * @return The name of the vehicle.
     */
    public String getName(){ return name; }

    /**
     * Sets the name of the vehicle.
     * @param value The name of the vehicle.
     */
    public void setName(String value) { name = value; }

    /**
     * Gets the provider that contains the vehicle.
     * @return The provider that contains the vehicle.
     */
    public IVehicleProvider getProvider(){ return provider; }

    /**
     * Sets the provider that contains the vehicle.
     * @param value The provider that contains the vehicle.
     */
    public void setProvider(IVehicleProvider value) { provider = value; }

    /**
     * Gets the general type of the vehicle.
     * @return The general type of the vehicle.
     */
    public VehicleType getType(){ return type; }

    /**
     * Sets the general type of the vehicle.
     * @param value The general type of the vehicle.
     */
    public void setType(VehicleType value) { type = value; }
}
