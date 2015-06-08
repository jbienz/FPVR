package com.solersoft.fpvr.util;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by jbienz on 6/7/2015.
 */
public class ThreadUtils
{
    static Thread mUiThread;
    static Handler mHandler;

    static public void setUiThread()
    {
        mUiThread = Thread.currentThread();
    }

    static public final void runOnUiThread(Runnable action)
    {
        if ((mUiThread != null) && (Thread.currentThread() == mUiThread))
        {
            action.run();

        }
        else
        {
            if (mHandler == null)
            {
                mHandler = new Handler(Looper.getMainLooper());
            }
            mHandler.post(action);
        }
    }
}
