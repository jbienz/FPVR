package com.solersoft.fpvr.fpvrlib;

/**
 * Created by jbienz on 5/21/2015.
 */
public class StatusUpdater
{
    //region Member Variables
    static private StatusListener listener;
    //endregion

    //region Public Methods
    /**
     * Updates the status for the listener.
     * @param tag A tag that represents the context of the update.
     * @param status The new status.
     */
    static public void UpdateStatus(String tag, String status)
    {
        if (listener != null)
        {
            listener.onStatusChanged(tag, status);
        }
    }

    /**
     * Register a callback to be invoked when gimbal values have changed.
     * @param l The callback that will run
     */
    static public void setStatusListener(StatusListener l)
    {
        listener = l;
    }
    //endregion

}
