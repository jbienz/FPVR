package com.solersoft.fpvr.fpvrdji;

import android.content.Context;
import android.util.Log;

import com.solersoft.fpvr.fpvrlib.IVehicle;
import com.solersoft.fpvr.fpvrlib.IVehicleService;
import com.solersoft.fpvr.util.DJI;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.HashSet;

import dji.sdk.api.DJIDrone;
import dji.sdk.api.DJIDroneTypeDef.*;
import dji.sdk.api.DJIError;
import dji.sdk.interfaces.DJIGerneralListener;

/**
 * Represents a DJI Inspire 1 aircraft.
 */
public class DJIInspire1Vehicle extends DJIVehicle
{
    //region Constructors
    public DJIInspire1Vehicle(Context context)
    {
        super(context, DJIDroneType.DJIDrone_Inspire1);
    }
    //endregion

    //region Public Properties
    @Override
    public String getName()
    {
        return "DJI Inspire 1";
    }
    //endregion
}
