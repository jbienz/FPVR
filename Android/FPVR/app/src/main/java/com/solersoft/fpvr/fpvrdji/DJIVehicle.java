package com.solersoft.fpvr.fpvrdji;

import android.content.Context;

import com.solersoft.fpvr.fpvrlib.ConnectionState;
import com.solersoft.fpvr.fpvrlib.ILifecycleAware;
import com.solersoft.fpvr.fpvrlib.IVehicle;
import com.solersoft.fpvr.fpvrlib.IVehicleService;
import com.solersoft.fpvr.fpvrlib.LifecycleAware;
import com.solersoft.fpvr.fpvrlib.Result;
import com.solersoft.fpvr.fpvrlib.ResultHandler;
import com.solersoft.fpvr.fpvrlib.StatusUpdater;
import com.solersoft.fpvr.fpvrlib.Vehicle;
import com.solersoft.fpvr.util.DJI;
import com.solersoft.fpvr.util.TypeUtils;

import java.net.InetAddress;
import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import dji.sdk.api.DJIDrone;
import dji.sdk.api.DJIDroneTypeDef.*;
import dji.sdk.api.DJIError;
import dji.sdk.interfaces.DJIGerneralListener;

/**
 * A base class for DJI aircraft
 */
public abstract class DJIVehicle extends Vehicle
{
    /***************************************************************************
     * Constants
     ***************************************************************************/

    private static final String TAG = "DJIVehicle";

    //region Member Variables
    private DJIBatteryService battery;
    private DJICameraService camera;
    private boolean connectedToDrone;
    private Context context;
    private DJIDroneType droneType;
    private DJIGimbalService gimbal;
    private boolean initialized;
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

        // Create and add child services
        Collection<IVehicleService> services = getServices();
        battery = new DJIBatteryService();
        camera = new DJICameraService(context);
        gimbal = new DJIGimbalService();

        services.add(battery);
        services.add(camera);
        services.add(gimbal);
    }
    //endregion

    //region Overrides
    @Override
    protected void onConnect(final ResultHandler handler)
    {
        try
        {
            StatusUpdater.UpdateStatus(TAG, "Connecting to drone...");
            connectedToDrone = DJIDrone.connectToDrone();

            // Update status and bail if not connected
            if (!connectedToDrone)
            {
                StatusUpdater.UpdateStatus(TAG, "Unable to connect to drone.");
                if (handler != null) { handler.onResult(new Result(false));}
            }
            else
            {
                // Success, pass to base
                super.onConnect(handler);
            }
        }
        catch (Exception e)
        {
            StatusUpdater.UpdateStatus(TAG, "Unable to connect: " + e.getMessage());

            // Notify listener
            if (handler != null) { handler.onResult(new Result(e));}
        }
    }

    @Override
    protected void onCreate(final ResultHandler handler)
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
            if (handler != null) { handler.onResult(new Result(false)); }
        }

        StatusUpdater.UpdateStatus(TAG, "Checking permissions...");
        new Thread() // This MUST be run on a new thread because it may use networking on first validation
        {
            @Override
            public void run()
            {
                try
                {
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
                                // Success, pass to base
                                DJIVehicle.super.onCreate(handler);
                            }
                            else
                            {
                                // Notify listener
                                if (handler != null) { handler.onResult(new Result(false, desc)); }
                            }
                        }
                    });
                }
                catch (Exception e)
                {
                    // Notify listener
                    if (handler != null) { handler.onResult(new Result(e));}
                }
            }
        }.start();
    }

    @Override
    protected void onDisconnect()
    {
        if (connectedToDrone)
        {
            DJIDrone.disconnectToDrone();
            connectedToDrone = false;
        }

        // Pass to base
        super.onDisconnect();
    }
    //endregion
}
