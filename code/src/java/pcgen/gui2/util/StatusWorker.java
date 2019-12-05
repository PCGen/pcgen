/*
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
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

/**
 * StatusWorker extends SwingWorker to handle progress display in the status bar.
 * It replaces TaskExecutor, which was a private class inside PCGenStatusBar.
 */
package pcgen.gui2.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.LogRecord;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import pcgen.gui2.PCGenStatusBar;
import pcgen.system.PCGenTask;
import pcgen.system.PCGenTaskEvent;
import pcgen.system.PCGenTaskListener;
import pcgen.util.Logging;

public class StatusWorker extends SwingWorker<List<LogRecord>, List<LogRecord>> implements PCGenTaskListener
{
    private final String statusMsg;
    private final PCGenTask task;
    private final PCGenStatusBar statusBar;
    private boolean dirty = false;
    private final List<LogRecord> errors = new ArrayList<>();

    /**
     * @param statusMsg - text to display in status bar
     * @param task      to be executed
     * @param statusBar the PCGen status Bar
     */
    public StatusWorker(String statusMsg, PCGenTask task, PCGenStatusBar statusBar)
    {
        this.statusMsg = statusMsg;
        this.task = task;
        this.statusBar = statusBar;
    }


    @Override
    public void done()
    {
        super.done();
        statusBar.endShowingProgress();
    }

    @Override
    public void progressChanged(final PCGenTaskEvent event)
    {
        if (!dirty)
        {
            dirty = true;
            SwingUtilities.invokeLater(() -> {
                statusBar.getProgressBar().getModel().setRangeProperties(task.getProgress(), 1, 0,
                        task.getMaximum(), true);
                statusBar.getProgressBar().setString(task.getMessage());
                dirty = false;
            });
        }
    }

    @Override
    public void errorOccurred(PCGenTaskEvent event)
    {
        errors.add(event.getErrorRecord());
    }

    /**
     * @return records for all errors reported by the task
     */
    public List<LogRecord> getErrors()
    {
        return Collections.unmodifiableList(errors);
    }

    @Override
    protected List<LogRecord> doInBackground()
    {
        final String oldMessage = statusBar.getContextMessage();
        statusBar.startShowingProgress(statusMsg, false);
        statusBar.getProgressBar().getModel().setRangeProperties(task.getProgress(), 1, 0, task.getMaximum(), true);

        task.addPCGenTaskListener(this);

        try
        {
            task.run();
        } catch (Exception e)
        {
            Logging.errorPrint(e.getLocalizedMessage(), e);
        }

        task.removePCGenTaskListener(this);

        SwingUtilities.invokeLater(() -> statusBar.setContextMessage(oldMessage));
        return Collections.unmodifiableList(errors);
    }
}
