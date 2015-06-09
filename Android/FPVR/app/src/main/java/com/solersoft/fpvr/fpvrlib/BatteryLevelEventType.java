package com.solersoft.fpvr.fpvrlib;

/**
 * Types of events that happen at various battery levels.
 */
public enum BatteryLevelEventType
{
    /**
     * The aircraft attempts to automatically land.
     */
    AutoLand,

    /**
     * The vehicle attempts to automatically return to its home point.
     */
    ReturnHome,
}
