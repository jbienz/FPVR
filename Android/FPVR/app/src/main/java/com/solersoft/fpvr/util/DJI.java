package com.solersoft.fpvr.util;

/**
 * Created by jbienz on 5/13/2015.
 */
public class DJI
{
    /**
     * Returns true if the result is considered a success.
     * @param errorCode the result.
     * @return true if result is a success; otherwise false.
     */
    public static boolean Success(int errorCode)
    {
        return errorCode == 0;
    }
}
