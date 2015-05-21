package com.solersoft.fpvr.fpvrlib;

/**
 * Represents a result with a value.
 *
 */
public class ResultWithValue<T> extends Result
{
    //region Member Variables
    private T value;
    //endregion

    //region Constructors
    /**
     * Initializes a new {@link ResultWithValue} based on success and a value.
     * @param success Whether or not the result is successful.
     */
    public ResultWithValue(boolean success, T value)
    {
        super(success);
        this.value = value;
    }

    /**
     * Initializes a new {@link ResultWithValue} based on an exception.
     * @param exception The exception.
     */
    public ResultWithValue(Exception exception)
    {
        super(exception);
    }
    //endregion

    //region Public Properties
    /**
     * Gets the value returned by the result. If an exception occurred this value will be null.
     * @return The value returned by the result.
     */
    public T getValue()
    {
        return value;
    }
    //endregion
}
