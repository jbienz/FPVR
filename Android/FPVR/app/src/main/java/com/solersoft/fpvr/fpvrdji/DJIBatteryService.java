package com.solersoft.fpvr.fpvrdji;

import com.solersoft.fpvr.fpvrlib.BatteryLevelEvent;
import com.solersoft.fpvr.fpvrlib.BatteryLevelEventType;
import com.solersoft.fpvr.fpvrlib.BatteryListener;
import com.solersoft.fpvr.fpvrlib.Connectable;
import com.solersoft.fpvr.fpvrlib.IBatteryInfo;
import com.solersoft.fpvr.fpvrlib.Result;
import com.solersoft.fpvr.fpvrlib.ResultHandler;
import com.solersoft.fpvr.util.DJI;

import java.util.Collection;
import java.util.HashSet;

import dji.sdk.api.Battery.DJIBattery;
import dji.sdk.api.Battery.DJIBatteryProperty;
import dji.sdk.api.DJIDrone;
import dji.sdk.api.DJIDroneTypeDef.*;
import dji.sdk.api.DJIError;
import dji.sdk.interfaces.DJIBatteryGetPartVoltageCallBack;
import dji.sdk.interfaces.DJIBatteryUpdateInfoCallBack;
import dji.sdk.interfaces.DJISmartBatteryExecuteResultCallback;

/**
 * Created by jbienz on 6/8/2015.
 */
public class DJIBatteryService extends Connectable implements IBatteryInfo
{
    //region Constants
    static private final int UpdateInterval = 2000;
    //endregion

    //region Member Variables
    private double criticalPercent;
    private Collection<BatteryLevelEvent> eventLevels = new HashSet<BatteryLevelEvent>();
    private BatteryListener listener;
    private double lowPercent;
    private double remainingPercent;
    private double voltage;
    //endregion

    //region Overrides and Callbacks
    private DJIBatteryUpdateInfoCallBack batteryCallback = new DJIBatteryUpdateInfoCallBack()
    {
        @Override
        public void onResult(DJIBatteryProperty djiBatteryProperty)
        {
            remainingPercent = ((double)djiBatteryProperty.remainLifePercent) / 100d; // V is in percent but as a whole number
            voltage = djiBatteryProperty.currentVoltage;

            if (listener != null)
            {
                listener.onBatteryChanged(DJIBatteryService.this);
            }
        }
    };

    @Override
    protected void onConnect(final ResultHandler handler)
    {
        // Get battery
        final DJIBattery battery = DJIDrone.getDjiBattery();

/*        // Get drone type
        DJIDroneType droneType = DJIDrone.getDroneType();
        if (droneType == DJIDroneType.DJIDrone_Inspire1)
        {
            fullVoltage = 22.8d;
        }
        else if (droneType == DJIDroneType.DJIDrone_Vision)
        {
            fullVoltage = 11.1d;
        }

        // If we don't know the full voltage there's nothing else we can do
        if (fullVoltage <= 0)
        {
            if (handler != null) { handler.onResult(new Result(true)); }
            return;
        }*/

        // Get event levels
        battery.getSmartBatteryGohomeBatteryLevel(new DJISmartBatteryExecuteResultCallback()
        {
            @Override
            public void onResult(double v, DJIError djiError)
            {
                if (DJI.Success(djiError.errorCode))
                {
                    lowPercent = v / 100d; // V is in percent but as a whole number
                    eventLevels.add(new BatteryLevelEvent(BatteryLevelEventType.ReturnHome, v));
                }

                battery.getSmartBatteryLandBatteryLevel(new DJISmartBatteryExecuteResultCallback()
                {
                    @Override
                    public void onResult(double v, DJIError djiError)
                    {
                        if (DJI.Success(djiError.errorCode))
                        {
                            criticalPercent = v / 100d; // V is in percent but as a whole number
                            eventLevels.add(new BatteryLevelEvent(BatteryLevelEventType.AutoLand, v));
                        }

                        if (handler != null)
                        {
                            handler.onResult(new Result(true));
                        }
                    }
                });
            }
        });

    }

    @Override
    protected void onCreate(ResultHandler handler)
    {
        DJIBattery battery = DJIDrone.getDjiBattery();
        battery.setBatteryUpdateInfoCallBack(batteryCallback);
        battery.startUpdateTimer(UpdateInterval);
        super.onCreate(handler);
    }

    @Override
    protected void onDestroy()
    {
        eventLevels.clear();
        DJIDrone.getDjiBattery().setBatteryUpdateInfoCallBack(null);
        super.onDestroy();
    }

    @Override
    protected void onDisconnect()
    {
        eventLevels.clear();
        super.onDisconnect();
    }

    @Override
    protected void onPause()
    {
        DJIDrone.getDjiBattery().stopUpdateTimer();
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        DJIDrone.getDjiBattery().startUpdateTimer(UpdateInterval);
        super.onResume();
    }
    //endregion

    //region Public Methods
    @Override
    public void setBatteryListener(BatteryListener l)
    {
        listener = l;
    }
    //endregion

    //region Public Properties
    @Override
    public double getCriticalPercent() { return criticalPercent; }

    @Override
    public Collection<BatteryLevelEvent> getLevelEvents()
    {
        return eventLevels;
    }

    @Override
    public double getLowPercent() { return lowPercent; }

    @Override
    public double getRemainingPercent()
    {
        return remainingPercent;
    }

    @Override
    public double getVoltage()
    {
        return voltage;
    }
    //endregion
}
