package com.solersoft.fpvr.fpvrdji;

import android.content.Context;
import android.view.View;

import com.solersoft.fpvr.fpvrlib.Connectable;
import com.solersoft.fpvr.fpvrlib.ICameraInfo;
import com.solersoft.fpvr.fpvrlib.ILifecycleAware;
import com.solersoft.fpvr.fpvrlib.IVehicleService;
import com.solersoft.fpvr.fpvrlib.LifecycleAware;
import com.solersoft.fpvr.fpvrlib.Result;
import com.solersoft.fpvr.fpvrlib.ResultHandler;
import com.solersoft.fpvr.fpvrlib.StatusUpdater;
import com.solersoft.fpvr.util.DJI;
import com.solersoft.fpvr.util.ThreadUtils;

import java.security.InvalidParameterException;

import dji.sdk.api.Camera.DJICamera;
import dji.sdk.api.Camera.DJICameraSettingsTypeDef.*;
import dji.sdk.api.DJIDrone;
import dji.sdk.api.DJIDroneTypeDef.*;
import dji.sdk.api.DJIError;
import dji.sdk.interfaces.DJIExecuteResultCallback;
import dji.sdk.interfaces.DJIReceivedVideoDataCallBack;
import dji.sdk.widget.DjiGLSurfaceView;

/**
 * An implementation of the {@link ICameraInfo} service.
 */
public class DJICameraService extends Connectable implements ICameraInfo, IVehicleService
{
    //region Constants
    private static final String TAG = "DJICameraService";
    //endregion

    //region Member Variables
    private Context context;
    private DjiGLSurfaceView surfaceView;
    //endregion

    //region Constructors
    /**
     * Initializes a new {@link DJICameraService}.
     * @param context The application context used to initalize the view.
     */
    public DJICameraService(Context context)
    {
        // Validate
        if (context == null) {throw new InvalidParameterException("context may not be null"); }

        // Store
        this.context = context;
    }
    //endregion

    //region Overrides
    @Override
    protected void onConnect(final ResultHandler handler)
    {
        boolean connectOK = DJIDrone.getDjiCamera().getCameraConnectIsOk();
        DJIDrone.getDjiCamera().setCameraMode(CameraMode.Camera_Camera_Mode, new DJIExecuteResultCallback()
        {
            @Override
            public void onResult(DJIError djiError)
            {
                boolean success = DJI.Success(djiError.errorCode);
                String s = djiError.errorDescription;
                if (handler != null)
                {
                    handler.onResult(new Result(success));
                }
            }
        });
    }

    @Override
    protected void onCreate(final ResultHandler handler)
    {
        ThreadUtils.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                // Create the surface view
                surfaceView = new DjiGLSurfaceView(context);

                // Start it
                boolean started = surfaceView.start();

                if (!started)
                {
                    StatusUpdater.UpdateStatus(TAG, "Camera preview did not start");
                    if (handler != null) { handler.onResult(new Result(false)); }
                }
                else
                {
                    // Get camera
                    DJICamera camera = DJIDrone.getDjiCamera();

                    // Set callback
                    camera.setReceivedVideoDataCallBack(onVideo);

                    // If not inspire 1, choose highest res supported by Vision+
                    if (DJIDrone.getDroneType() != DJIDroneType.DJIDrone_Inspire1)
                    {
                        surfaceView.setStreamType(CameraPreviewResolustionType.Resolution_Type_640x480_30fps);
                    }

                    // Start updates
                    camera.startUpdateTimer(1000);

                    StatusUpdater.UpdateStatus(TAG, "Camera preview started");

                    // Success, pass to base
                    DJICameraService.super.onCreate(handler);
                }
            }
        });
    }

    @Override
    protected void onPause()
    {
        if (surfaceView != null)
        {
            surfaceView.onPause();
            DJIDrone.getDjiCamera().stopUpdateTimer();
        }
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        if (surfaceView != null)
        {
            surfaceView.onResume();
            DJIDrone.getDjiCamera().startUpdateTimer(1000);
        }
        super.onResume();
    }

    @Override
    protected void onDestroy()
    {
        if (surfaceView != null)
        {
            surfaceView.destroy();
            DJIDrone.getDjiCamera().setReceivedVideoDataCallBack(null);
        }
        super.onDestroy();
    }

    protected DJIReceivedVideoDataCallBack onVideo = new DJIReceivedVideoDataCallBack()
    {
        @Override
        public void onResult(byte[] videoBuffer, int size)
        {
            if (surfaceView != null)
            {
                surfaceView.setDataToDecoder(videoBuffer, size);
            }
        }
    };

    //endregion

    //region Public Properties
    @Override
    public View getCameraView()
    {
        return surfaceView;
    }
    //endregion
}
