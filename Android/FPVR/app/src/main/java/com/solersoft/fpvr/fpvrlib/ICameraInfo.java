package com.solersoft.fpvr.fpvrlib;

import android.view.View;

/**
 * The interface for reading information about a camera on a vehicle.
 */
public interface ICameraInfo extends IVehicleService
{
    //region Public Properties
    /**
     * Gets the the main {@link View} that can be used to view the camera.
     * @return The main {@link View} that can be used to view the camera or null if camera preview
     * is not supported.
     */
    public View getCameraView();
}
