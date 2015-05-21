package com.solersoft.fpvr.fpvrlib;

/**
 * Represents the handler of an action.
 */
public interface ResultHandler
{
    /**
     * Handles the result of the action.
     * @param result The result of the action.
     */
    void onResult(Result result);
}
