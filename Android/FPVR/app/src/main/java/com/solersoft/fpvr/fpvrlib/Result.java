package com.solersoft.fpvr.fpvrlib;

import java.security.InvalidParameterException;

/**
 * Represents the result of an asynchronous action.
 */
public class Result
{
    //region Member Variables
    private Exception exception;
    private boolean success;
    private String statusMessage;
    //endregion

    //region Constructors
    /**
     * Initializes a new {@link Result} based on success.
     * @param success Whether or not the result is successful.
     */
    public Result(boolean success)
    {
        this.success = success;
    }

    /**
     * Initializes a new {@link Result} based on success and a status message.
     * @param success Whether or not the result is successful.
     */
    public Result(boolean success, String statusMessage)
    {
        this.success = success;
        this.statusMessage = statusMessage;
    }

    /**
     * Initializes a new {@link Result} based on an exception.
     * @param exception The exception.
     */
    public Result(Exception exception)
    {
        // Validate
        if (exception == null) { throw new InvalidParameterException("exception cannot be null"); }

        // Store
        this.exception = exception;
        this.success = false;
    }
    //endregion

    @Override
    public String toString()
    {
        return getStatusMessage();
    }

    //region Public Properties
    /**
     * Gets the exception, if any, thrown during execution.
     * @return The exception, if any, thrown during execution.
     */
    public Exception getException()
    {
        return exception;
    }

    /**
     * Gets a value that indicates if the result is a success.
     * @return <code>true</code> if the result is a success; otherwise <code>false</code>.
     */
    public boolean isSuccess()
    {
        return success;
    }

    /**
     * Gets a value that indicates the status of the result.
     * @return a string that indicates the status of the result.
     */
    public String getStatusMessage()
    {
        if (statusMessage != null)
        {
            return statusMessage;
        }
        else if (success)
        {
            return "Success";
        }
        else if (exception != null)
        {
            return "Error: " + exception.getMessage();
        }
        else
        {
            return "Unknown error";
        }
    }
    //endregion
}