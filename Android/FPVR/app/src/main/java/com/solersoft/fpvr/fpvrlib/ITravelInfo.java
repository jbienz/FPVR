package com.solersoft.fpvr.fpvrlib;

/**
 * Provides orientation information a vehicles direction of travel.
 */
public interface ITravelInfo
{
    /**
     * Gets information about the vehicles direction of travel.
     * A {@Link NavigationAttitude} instance with information about the vehicles direction of travel.
     */
    public TravelAttitude getTravelAttitude();
}