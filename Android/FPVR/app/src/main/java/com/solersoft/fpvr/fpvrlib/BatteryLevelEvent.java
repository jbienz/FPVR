package com.solersoft.fpvr.fpvrlib;

import java.security.InvalidParameterException;

/**
 * Identifies an event that will happen at a specified battery level.
 */
public class BatteryLevelEvent
{
    //region Member Variables
    private Double eventPercent;
    private BatteryLevelEventType eventType;
    //endregion

    //region Constructors
    /**
     * Initializes a new {@link BatteryLevelEvent} instance.
     * @param eventType A {@link BatteryLevelEventType} value that indicates the type of event.
     * @param eventPercent A {@link Double} that indicates the percentage of total capacity at which the event will occur.
     */
    public BatteryLevelEvent(BatteryLevelEventType eventType, double eventPercent)
    {
        // Validate
        if (eventType == null) { throw new InvalidParameterException("eventType cannot be null"); }
        if ((eventPercent < 0) || (eventPercent > 1)) { throw new InvalidParameterException("eventPercent must be between 0 and 1"); }

        // Store
        this.eventType = eventType;
        this.eventPercent = eventPercent;
    }
    //endregion

    //region Public Properties
    /**
     * Gets a value that indicates when the event will occur.
     * @return A {@link Double} that indicates the percentage of total capacity at which the event will occur.
     */
    public double getEventPercent()
    {
        return eventPercent;
    }

    /**
     * Gets a value that indicates the type of event.
     * @return A {@link BatteryLevelEventType} value that indicates the type of event.
     */
    public BatteryLevelEventType getEventType()
    {
        return eventType;
    }
    //endregion
}
