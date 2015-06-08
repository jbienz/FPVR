package com.solersoft.fpvr.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by jbienz on 5/25/2015.
 */
public class TypeUtils
{
    public static <T> T as(Class<T> t, Object o)
    {
        return t.isAssignableFrom(o.getClass()) ? t.cast(o) : null;
    }
}
