/*
 * Copyright 2010 Connor Petty <cpmeister@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package pcgen.system;

import java.util.Objects;
import java.util.logging.LogRecord;

import javax.swing.event.EventListenerList;

import pcgen.util.Logging;

public abstract class PCGenTask implements Runnable, ProgressContainer
{

    private final EventListenerList listenerList = new EventListenerList();
    private int progress = 0;
    private int maximum = 0;
    private String message;

    public void addPCGenTaskListener(PCGenTaskListener listener)
    {
        Objects.requireNonNull(listener);
        listenerList.add(PCGenTaskListener.class, listener);
    }

    public void removePCGenTaskListener(PCGenTaskListener listener)
    {
        Objects.requireNonNull(listener);
        listenerList.remove(PCGenTaskListener.class, listener);
    }

    @Override
    public abstract void run();

    @Override
    public int getMaximum()
    {
        return maximum;
    }

    @Override
    public int getProgress()
    {
        return progress;
    }

    @Override
    public String getMessage()
    {
        return message;
    }

    @Override
    public void setValues(int progress, int maximum)
    {
        this.progress = progress;
        this.maximum = maximum;
        fireProgressChangedEvent();
    }

    @Override
    public void setValues(String message, int progress, int maximum)
    {
        this.progress = progress;
        this.maximum = maximum;
        this.message = message;
        fireProgressChangedEvent();
    }

    @Override
    public void setProgress(int progress)
    {
        this.progress = progress;
        fireProgressChangedEvent();
    }

    @Override
    public void setProgress(String message, int progress)
    {
        this.message = message;
        this.progress = progress;
        fireProgressChangedEvent();
    }

    @Override
    public void setMaximum(int maximum)
    {
        this.maximum = maximum;
        fireProgressChangedEvent();
    }

    @Override
    public void fireProgressChangedEvent()
    {
        PCGenTaskEvent taskEvent = null;
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2;i >= 0;i -= 2)
        {
            if (listeners[i] == PCGenTaskListener.class)
            {
                // Lazily create the event:
                if (taskEvent == null)
                {
                    taskEvent = new PCGenTaskEvent(this);
                }
                ((PCGenTaskListener) listeners[i + 1]).progressChanged(taskEvent);
            }
        }
    }

    protected void sendErrorMessage(Throwable e)
    {
        LogRecord record = new LogRecord(Logging.ERROR, e.getMessage());
        record.setThrown(e);
        fireErrorOccurredEvent(record);
    }

    protected void sendErrorMessage(LogRecord record)
    {
        fireErrorOccurredEvent(record);
    }

    protected void fireErrorOccurredEvent(LogRecord message)
    {
        PCGenTaskEvent taskEvent = null;
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2;i >= 0;i -= 2)
        {
            if (listeners[i] == PCGenTaskListener.class)
            {
                // Lazily create the event:
                if (taskEvent == null)
                {
                    taskEvent = new PCGenTaskEvent(this, message);
                }
                ((PCGenTaskListener) listeners[i + 1]).errorOccurred(taskEvent);
            }
        }
    }

}
