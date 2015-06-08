package com.solersoft.fpvr.fpvrlib;

/**
 * The interface for a class that participates in application lifecycle.
 */
public interface ILifecycleAware
{
    //region Public Methods
    /**
     * Creates the object.
     * @param handler A callback that will handle the result of creation.
     */
    public void create(ResultHandler handler);

    /**
     * Pauses the object.
     */
    public void pause();

    /**
     * Resumes the object.
     */
    public void resume();

    /**
     * Destroys the object.
     */
    public void destroy();
    //endregion

    //region Public Properties
    /**
     * Gets a value that indicates if the object has completed the create phase.
     * @return <code>true</code> if the object has completed the create phase;
     * otherwise <code>false</code>.
     */
    public boolean isCreated();

    /**
     * Gets a value that indicates if the object is paused.
     * @return <code>true</code> if the object is paused; otherwise <code>false</code>.
     */
    public boolean isPaused();
//endregion
}
