using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FPVR
{
    /// <summary>
    /// The interface for plugins that require a heartbeat Update method.
    /// </summary>
    /// <remarks>
    /// NOTE: Implementing this interface is *NOT* recommended because it gets called on every single frame. 
    /// It is recommended that plugins provide their own heartbeat via timers or a worker thread. 
    /// This interface is intended for plugins that need to participate in the rendering loop. For example, 
    /// a plugin that wishes to simulate a smooth rotation over time would want to know the delta time 
    /// provided by the <see cref="Update"/> method.
    /// </remarks>
    public interface IUpdateRequired
    {
        /// <summary>
        /// Called once per rendered frame.
        /// </summary>
        /// <param name="deltaTime">
        /// The amount of time that has passed since the last frame.
        /// </param>
        void Update(float deltaTime);
    }
}
