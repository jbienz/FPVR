package com.solersoft.fpvr.fpvrdji;

import android.content.Context;
import android.util.Log;

import com.solersoft.fpvr.fpvrlib.ISupportInitialize;
import com.solersoft.fpvr.fpvrlib.IVehicle;
import com.solersoft.fpvr.fpvrlib.IVehicleService;
import com.solersoft.fpvr.util.DJI;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.HashSet;

import dji.sdk.api.DJIDrone;
import dji.sdk.api.DJIDroneTypeDef.*;
import dji.sdk.api.DJIError;
import dji.sdk.interfaces.DJIGerneralListener;

/**
 * A base class for DJI aircraft
 */
public abstract class DJIVehicle implements IVehicle, ISupportInitialize
{
    /***************************************************************************
     * Constants
     ***************************************************************************/

    private static final String TAG = "DJIVehicle";

    //region Member Variables
    private Context context;
    private DJIDroneType droneType;
    private DJIGimbalControl gimbalControl;
    private boolean initialized;
    private boolean initializing;
    private Collection<IVehicleService> services;
    private boolean validated;
    //endregion

    //region Constructors
    /**
     * Initializes a new {@link DJIVehicle}.
     * @param context The application context used to initalize the SDK.
     */
    public DJIVehicle(Context context, DJIDroneType droneType)
    {
        // Validate
        if (context == null) {throw new InvalidParameterException("context may not be null"); }
        if (droneType == null) {throw new InvalidParameterException("droneType may not be null"); }

        // Store
        this.context = context;
        this.droneType = droneType;

        // Create services collection
        services = new HashSet<IVehicleService>();

        // Create and add child services
        gimbalControl = new DJIGimbalControl(this);
        services.add(gimbalControl);
    }
    //endregion

    /**
     * Updates the status display.
     * @param msg The message to display.
     */
    private void UpdateStatus(final String msg)
    {
        Log.i(TAG, msg);
        /*HomeActivity.this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                statusLabel.setText(msg);
            }
        });*/
    }

    //region Public Methods
    @Override
    public void Initialize()
    {
        // If already initialized or initializing just bail
        if (initialized || initializing) { return; }

        // Initializing
        initializing = true;

        try
        {
            UpdateStatus("Initializing...");
            DJIDrone.initWithType(context, droneType);

            UpdateStatus("Connecting...");
            initialized = DJIDrone.connectToDrone();

            // Update status and bail if not OK
            if (!initialized)
            {
                UpdateStatus("Unable to connect to drone.");
                initializing = false;
                return;
            }

            UpdateStatus("Checking permissions...");

            DJIDrone.checkPermission(context, new DJIGerneralListener()
            {

                @Override
                public void onGetPermissionResult(int result)
                {
                    // Validated based on success
                    validated = DJI.Success(result);

                    // Show permissions
                    String desc = DJIError.getCheckPermissionErrorDescription(result);
                    UpdateStatus("Permissions: " + desc);

                    // Done initializing
                    initializing = false;
                }
            });
        }
        catch (Exception e)
        {
            UpdateStatus("Unable to initialize: " + e.getMessage());
            e.printStackTrace();
            initializing = false;
        }

    }
    //endregion

    //region Public Properties
    @Override
    public Collection<IVehicleService> getServices()
    {
        return services;
    }

    @Override
    public boolean isInitialized()
    {
        return initialized;
    }
    //endregion
}
