package com.solersoft.fpvr.fpvrlib;

/**
 * A base class that implements the {@link ILifecycleAware} interface.
 */
public class LifecycleAware implements ILifecycleAware
{
    //region Member Variables
    private boolean created;
    private boolean creating;
    private boolean paused;
    //endregion

    //region Overridables and Event Triggers
    protected void onCreate(ResultHandler handler)
    {
        if (handler != null)
        {
            handler.onResult(new Result(true));
        }
    }

    protected void onPause()
    {
    }

    protected void onResume()
    {
    }

    protected void onDestroy()
    {
    }
    //endregion

    //region Internal Methods
    protected void verifyCreated()
    {
        if (!created) { throw new UnsupportedOperationException("This member cannot be called before create."); }
    }
    //endregion

    //region Public Methods
    @Override
    final public void create(final ResultHandler handler)
    {
        // If already created or creating, ignore
        if (created || creating) { return; }

        // Creating
        creating = true;

        // Call override
        onCreate(new ResultHandler()
        {
            @Override
            public void onResult(Result result)
            {
                // No longer creating
                creating = false;

                // Created successfully?
                created = result.isSuccess();

                // If handler notify
                if (handler != null)
                {
                    handler.onResult(new Result(created));
                }
            }
        });
    }

    @Override
    final public void pause()
    {
        // If already paused, ignore
        if (paused) { return; }

        // Pause
        paused = true;

        // Call override
        onPause();
    }

    @Override
    final public void resume()
    {
        // If not paused, ignore
        if (!paused) { return; }

        // Resume
        paused = false;

        // Call override
        onResume();
    }

    @Override
    final public void destroy()
    {
        // If not created, ignore
        if (!created) { return; }

        // No longer created
        created = false;

        // Call override
        onDestroy();
    }
    //endregion

    //region Public Properties
    @Override
    public boolean isCreated()
    {
        return created;
    }

    @Override
    public boolean isPaused()
    {
        return paused;
    }
    //endregion
}
