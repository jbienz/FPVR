package com.solersoft.fpvr.fpvrlib;

/**
 * Represents the handler of an action with a value.
 */
public interface ResultWithValueHandler<T>
{
    /**
     * Handles the result of the action.
     * @param result The result of the action.
     */
    void onResult(ResultWithValue<T> result);
}
