package com.solersoft.fpvr.fpvrdji;

import android.content.Context;

import com.solersoft.fpvr.fpvrlib.ISupportConnection;
import com.solersoft.fpvr.fpvrlib.IVehicle;
import com.solersoft.fpvr.fpvrlib.IVehicleService;
import com.solersoft.fpvr.fpvrlib.Result;
import com.solersoft.fpvr.fpvrlib.ResultHandler;
import com.solersoft.fpvr.fpvrlib.StatusUpdater;
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
public abstract class DJIVehicle implements IVehicle, ISupportConnection
{
    /***************************************************************************
     * Constants
     ***************************************************************************/

    private static final String TAG = "DJIVehicle";

    //region Member Variables
    private boolean connected;
    private boolean connecting;
    private Context context;
    private DJIDroneType droneType;
    private DJIGimbalService gimbal;
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
        gimbal = new DJIGimbalService();
        services.add(gimbal);
    }
    //endregion

    //region Public Methods
    @Override
    public void connect(final ResultHandler handler)
    {
        // If already connected or connecting just bail
        if (connected || connecting) { return; }

        // Initializing
        connecting = true;

        try
        {
            StatusUpdater.UpdateStatus(TAG, "Initializing...");
            DJIDrone.initWithType(context, droneType);

            StatusUpdater.UpdateStatus(TAG, "Connecting...");
            connected = DJIDrone.connectToDrone();

            // Update status and bail if not OK
            if (!connected)
            {
                StatusUpdater.UpdateStatus(TAG, "Unable to connect to drone.");
                connecting = false;
                return;
            }

            StatusUpdater.UpdateStatus(TAG, "Checking permissions...");

            DJIDrone.checkPermission(context, new DJIGerneralListener()
            {

                @Override
                public void onGetPermissionResult(int result)
                {
                    // Validated based on success
                    validated = DJI.Success(result);

                    // Show permissions
                    String desc = DJIError.getCheckPermissionErrorDescription(result);
                    StatusUpdater.UpdateStatus(TAG, "Permissions: " + desc);

                    // If validated, initialize child services
                    if (validated)
                    {
                        gimbal.initialize();
                    }

                    // Done connecting
                    connecting = false;

                    // Notify callback
                    if (handler != null)
                    {
                        handler.onResult(new Result(validated));
                    }
                }
            });
        }
        catch (Exception e)
        {
            StatusUpdater.UpdateStatus(TAG, "Unable to initialize: " + e.getMessage());
            e.printStackTrace();
            connecting = false;

            // Notify callback
            if (handler != null)
            {
                handler.onResult(new Result(e));
            }
        }
    }

    public void disconnect()
    {
        if (connected)
        {
            DJIDrone.disconnectToDrone();
            connected = false;
        }
    }
    //endregion

    //region Public Properties
    public DJIGimbalService getGimbal()
    {
        return gimbal;
    }

    @Override
    public Collection<IVehicleService> getServices()
    {
        return services;
    }

    @Override
    public boolean isConnected()
    {
        return connected;
    }
    //endregion
}
