package com.solersoft.fpvr.fpvrlib;

/**
 * Created by jbienz on 5/20/2015.
 */

/**
 * The interface for an object that supports initialization.
 */
public interface ISupportInitialize
{
    //region Public Methods
    /**
     * Initializes the object.
     */
    public void initialize();
    //endregion

    //region Public Properties
    /**
     * Gets a value that indicates if the object has been initialized.
     * @return <code>true</code> if the object has been initialized; otherwise <code>false</code>.
     */
    public boolean isInitialized();
    //endregion
}
