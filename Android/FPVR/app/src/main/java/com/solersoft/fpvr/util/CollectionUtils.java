package com.solersoft.fpvr.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by jbienz on 6/7/2015.
 */
public class CollectionUtils
{
    public static <TItem, TIterator> TItem findFirst(Class<TItem> itemClass, Iterator<TIterator> iterator)
    {
        while (iterator.hasNext())
        {
            TIterator current = iterator.next();
            TItem item = TypeUtils.as(itemClass, current);
            if (item != null) { return item; }
        }

        // Not found
        return null;
    }

    public static <TItem, TIterable> TItem findFirst(Class<TItem> itemClass, Iterable<TIterable> iterable)
    {
        return findFirst(itemClass, iterable.iterator());
    }
}
