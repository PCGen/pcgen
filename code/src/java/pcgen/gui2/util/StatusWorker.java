/**
 * @author Kim Winz <kwinz@users.sourceforge.net>
 * 
 * StatusWorker extends SwingWorker to handle progress display in the status bar.
 * 		It replaces TaskExecutor, which was a private class inside PCGenStatusBar.
 *
 */
package pcgen.gui2.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;

import javax.swing.SwingUtilities;

import pcgen.gui2.PCGenStatusBar;
import pcgen.system.PCGenTask;
import pcgen.system.PCGenTaskEvent;
import pcgen.system.PCGenTaskListener;
import pcgen.util.Logging;

public class StatusWorker extends SwingWorker<List<LogRecord>> implements PCGenTaskListener
{
		private final String statusMsg;
		private final PCGenTask task;
		private final PCGenStatusBar statusBar;
		private boolean dirty = false;
		private List<LogRecord> errors = new ArrayList<LogRecord>();

		/**
		 * @param statusMsg - text to display in status bar
		 * @param task to be executed
		 * @param the PCGen status Bar
		 */
		public StatusWorker(String statusMsg, PCGenTask task, PCGenStatusBar statusBar)
		{
			super();
			this.statusMsg = statusMsg;
			this.task = task;
			this.statusBar = statusBar;
		}

		@Override
		public List<LogRecord> construct()
		{	
			final String oldMessage = statusBar.getContextMessage();
			statusBar.startShowingProgress(statusMsg, false);
			statusBar.getProgressBar().getModel().setRangeProperties(task.getProgress(), 1, 0, task.getMaximum(), true);

			task.addPCGenTaskListener(this);

			try
			{
				task.execute();
			}
			catch (Exception e)
			{
				Logging.errorPrint(e.getLocalizedMessage(), e);
			}

			task.removePCGenTaskListener(this);

			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					statusBar.setContextMessage(oldMessage);
				}
			});
			return errors;
		}

		@Override
		public void finished()
		{
			statusBar.endShowingProgress();
			super.finished();
		}

		@Override
		public void progressChanged(final PCGenTaskEvent event)
		{
			if (!dirty)
			{
				dirty = true;
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						statusBar.getProgressBar().getModel().setRangeProperties(task.getProgress(), 1, 0, task.getMaximum(), true);
						statusBar.getProgressBar().setString(task.getMessage());
						dirty = false;
					}
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
			return errors;
		}
}
