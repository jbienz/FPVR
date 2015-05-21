package com.solersoft.fpvr.fpvrlib;

/**
 * The interface for an object that supports connecting and disconnecting.
 */
public interface ISupportConnection
{
    //region Public Methods
    /**
     * Attempts to open the connection.
     * @param handler A callback that will handle the result of the connection.
     */
    public void connect(ResultHandler handler);

    /**
     * Attempts to close the connection.
     */
    public void disconnect();
    //endregion

    //region Public Properties
    /**
     * Gets a value that indicates if the entity is connected.
     * @return <code>true</code> if the entity is connected; otherwise <code>false</code>.
     */
    public boolean isConnected();
    //endregion
}
