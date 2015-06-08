package com.solersoft.fpvr.fpvrlib;

/**
 * The base class for an object that supports connections.
 */
public abstract class Connectable extends LifecycleAware implements IConnectable
{
    //region Constants
    private static final String TAG = "Connectable";
    //endregion

    //region Member Variables
    private ConnectionState connectionState = ConnectionState.Disconnected;

    //region Overridables
    abstract protected void onConnect(ResultHandler handler);

    protected void onDisconnect()
    {

    }
    //endregion


    //region Public Methods
    final public void connect(final ResultHandler handler)
    {
        // If already connected or connecting just bail
        if (connectionState == ConnectionState.Connected || connectionState == ConnectionState.Connecting) { return; }

        // Connecting
        connectionState = ConnectionState.Connecting;

        try
        {
            // Must be created
            verifyCreated();

            StatusUpdater.UpdateStatus(TAG, "Connecting...");
            onConnect(new ResultHandler()
            {
                @Override
                public void onResult(Result result)
                {
                    // Update status and bail if not connected
                    if (!result.isSuccess())
                    {
                        StatusUpdater.UpdateStatus(TAG, "Unable to connect.");
                        connectionState = ConnectionState.Disconnected;
                    }
                    else
                    {
                        connectionState = ConnectionState.Connected;
                    }

                    // If handler, notify
                    if (handler != null)
                    {
                        handler.onResult(new Result(result.isSuccess()));
                    }
                }
            });

        }
        catch (Exception e)
        {
            StatusUpdater.UpdateStatus(TAG, "Unable to connect: " + e.getMessage());

            // Disconnect to close connection and set proper state
            disconnect();

            // Notify listener
            if (handler != null) { handler.onResult(new Result(e));}
        }
    }

    final public void disconnect()
    {
        // If not connected, ignore
        if (connectionState == connectionState.Disconnected)
        {
            return;
        }

        // Disconnect
        onDisconnect();

        // Set state
        connectionState = connectionState.Disconnected;
    }
    //endregion

    //region Public Properties
    public ConnectionState getConnectionState()
    {
        return connectionState;
    }
    //endregion
}
