package com.solersoft.fpvr.fpvrdji;

import android.content.Context;

import com.solersoft.fpvr.fpvrlib.ConnectionState;
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
    private boolean connectedToDrone = false;
    private ConnectionState connectionState = ConnectionState.Disconnected;
    private Context context;
    private DJIDroneType droneType;
    private DJIGimbalService gimbal;
    private boolean initialized = false;
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
        if (connectionState == ConnectionState.Connected || connectionState == ConnectionState.Connecting) { return; }

        // Connecting
        connectionState = ConnectionState.Connecting;

        try
        {
            // If not already initialized, initialize
            if (!initialized)
            {
                StatusUpdater.UpdateStatus(TAG, "Initializing...");
                initialized = DJIDrone.initWithType(context, droneType);
            }

            // Update status and bail if not initialized
            if (!initialized)
            {
                StatusUpdater.UpdateStatus(TAG, "Unable to initialize as type " + droneType);
                connectionState = ConnectionState.Disconnected;
                if (handler != null) { handler.onResult(new Result(false));}
                return;
            }

            StatusUpdater.UpdateStatus(TAG, "Connecting to drone...");
            connectedToDrone = DJIDrone.connectToDrone();

            // Update status and bail if not connected
            if (!connectedToDrone)
            {
                StatusUpdater.UpdateStatus(TAG, "Unable to connect to drone.");
                connectionState = ConnectionState.Disconnected;
                if (handler != null) { handler.onResult(new Result(false));}
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

                    // Done connecting
                    if (validated)
                    {
                        // Initialize child services
                        gimbal.initialize();

                        // Connected
                        connectionState = ConnectionState.Connected;

                        // Notify listener
                        if (handler != null) { handler.onResult(new Result(true));}
                    }
                    else
                    {
                        // Failed validation. Disconnect to close connection and set proper state
                        disconnect();

                        // Notify listener
                        if (handler != null) { handler.onResult(new Result(false));}
                    }
                }
            });
        }
        catch (Exception e)
        {
            StatusUpdater.UpdateStatus(TAG, "Unable to connect: " + e.getMessage());
            e.printStackTrace();

            // Disconnect to close connection and set proper state
            disconnect();

            // Notify listener
            if (handler != null) { handler.onResult(new Result(e));}
        }
    }

    public void disconnect()
    {
        if (connectedToDrone)
        {
            DJIDrone.disconnectToDrone();
            connectedToDrone = false;
        }
        connectionState = connectionState.Disconnected;
    }
    //endregion

    //region Public Properties
    @Override
    public ConnectionState getConnectionState()
    {
        return connectionState;
    }

    public DJIGimbalService getGimbal()
    {
        return gimbal;
    }

    @Override
    public Collection<IVehicleService> getServices()
    {
        return services;
    }
    //endregion
}
