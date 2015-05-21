package com.solersoft.fpvr.fpvrlib;

/**
 * Interface for handling status changes.
 */
public interface StatusListener
{
    /**
     * Called when status changes.
     */
    public void onStatusChanged(String tag, String status);
}
