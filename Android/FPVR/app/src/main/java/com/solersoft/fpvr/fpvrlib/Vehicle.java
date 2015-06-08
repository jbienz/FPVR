package com.solersoft.fpvr.fpvrlib;

import com.solersoft.fpvr.util.TypeUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A base class implementation of the {@link IVehicle} interface.
 */
public abstract class Vehicle extends Connectable implements IVehicle
{
    //region Constants
    private static final String TAG = "Vehicle";
    //endregion

    //region Member Variables
    private Collection<IVehicleService> services = new HashSet<IVehicleService>();
    //endregion

    //region Constructors
    /**
     * Initializes a new {@link Vehicle}.
     */
    public Vehicle()
    {
    }
    //endregion

    //region Internal Methods
    protected void connectService(final Iterator<IVehicleService> iterator, final boolean allConnected, final ResultHandler handler)
    {
        if (iterator.hasNext())
        {
            // Get next service
            IVehicleService service = iterator.next();

            // Try to get lifecycle
            IConnectable connectable = TypeUtils.as(IConnectable.class, service);
            if (connectable != null)
            {
                connectable.connect(new ResultHandler()
                {
                    @Override
                    public void onResult(Result result)
                    {
                        // All still successful?
                        boolean allSuccess = (allConnected && result.isSuccess());

                        // Go on to next service
                        connectService(iterator, allSuccess, handler);
                    }
                });
            }
            else
            {
                // Just go on to the next service
                connectService(iterator, allConnected, handler);
            }
        }
        else
        {
            // No more children. Call handler if it was passed.
            if (handler != null)
            {
                handler.onResult(new Result(allConnected));
            }
        }
    }

    protected void createService(final Iterator<IVehicleService> iterator, final boolean allCreated, final ResultHandler handler)
    {
        if (iterator.hasNext())
        {
            // Get next service
            IVehicleService service = iterator.next();

            // Try to get lifecycle
            ILifecycleAware lifecycle = TypeUtils.as(ILifecycleAware.class, service);
            if (lifecycle != null)
            {
                lifecycle.create(new ResultHandler()
                {
                    @Override
                    public void onResult(Result result)
                    {
                        // All still successful?
                        boolean allSuccess = (allCreated && result.isSuccess());

                        // Go on to next service
                        createService(iterator, allSuccess, handler);
                    }
                });
            }
            else
            {
                // Just go on to the next service
                createService(iterator, allCreated, handler);
            }
        }
        else
        {
            // No more children. Call handler if it was passed.
            if (handler != null)
            {
                handler.onResult(new Result(allCreated));
            }
        }
    }
    //endregion

    //region Overrides
    @Override
    protected void onConnect(final ResultHandler handler)
    {
        StatusUpdater.UpdateStatus(TAG, "Connecting to vehicle...");
        try
        {
            // Connect all child services and notify the handler when done
            StatusUpdater.UpdateStatus(TAG, "Connecting vehicle services...");
            onConnectServices(new ResultHandler()
            {
                @Override
                public void onResult(Result result)
                {
                    StatusUpdater.UpdateStatus(TAG, "Connect to vehicle services: " + result);
                    if (handler != null)
                    {
                        handler.onResult(result);
                    }
                }
            });
        }
        catch (Exception e)
        {
            StatusUpdater.UpdateStatus(TAG, "Unable to connect to vehicle: " + e.getMessage());

            // Notify listener
            if (handler != null) { handler.onResult(new Result(e));}
        }
    }

    protected void onConnectServices(ResultHandler handler)
    {
        connectService(services.iterator(), true, handler);
    }

    @Override
    protected void onCreate(final ResultHandler handler)
    {
        StatusUpdater.UpdateStatus(TAG, "Creating vehicle...");
        try
        {
            // Create all child services and notify the handler when done
            StatusUpdater.UpdateStatus(TAG, "Creating vehicle services...");
            onCreateServices(handler);
        }
        catch (Exception e)
        {
            StatusUpdater.UpdateStatus(TAG, "Unable to create vehicle: " + e.getMessage());

            // Notify listener
            if (handler != null) { handler.onResult(new Result(e));}
        }
    }

    protected void onCreateServices(ResultHandler handler)
    {
        createService(services.iterator(), true, handler);
    }

    @Override
    protected void onDestroy()
    {
        // Disconnect
        disconnect();

        // Destroy all child services
        for (IVehicleService service : getServices())
        {
            ILifecycleAware lifecycle = TypeUtils.as(ILifecycleAware.class, service);
            if (lifecycle != null)
            {
                lifecycle.destroy();
            }
        }

        // Clear child services
        getServices().clear();

        // Pass to base
        super.onDestroy();
    }

    @Override
    protected void onDisconnect()
    {
        // Disconnect all child services
        for (IVehicleService service : getServices())
        {
            IConnectable connectable = TypeUtils.as(IConnectable.class, service);
            if (connectable != null)
            {
                connectable.disconnect();
            }
        }

        // Pass to base
        super.onDisconnect();
    }


    @Override
    protected void onPause()
    {
        // Pause all child services
        for (IVehicleService service : getServices())
        {
            ILifecycleAware lifecycle = TypeUtils.as(ILifecycleAware.class, service);
            if (lifecycle != null)
            {
                lifecycle.pause();
            }
        }

        // Pass to base
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        // Resume all child servires
        for (IVehicleService service : getServices())
        {
            ILifecycleAware lifecycle = TypeUtils.as(ILifecycleAware.class, service);
            if (lifecycle != null)
            {
                lifecycle.resume();
            }
        }

        // Pass to base
        super.onResume();
    }
    //endregion

    //region Public Properties
    @Override
    public Collection<IVehicleService> getServices()
    {
        return services;
    }
    //endregion
}
