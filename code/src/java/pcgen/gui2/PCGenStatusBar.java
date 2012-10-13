/*
 * PCGenStatusBar.java
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
 * Created on May 1, 2010, 4:00:24 PM
 */
package pcgen.gui2;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.util.SwingWorker;
import pcgen.system.PCGenTask;
import pcgen.system.PCGenTaskEvent;
import pcgen.system.PCGenTaskListener;
import pcgen.util.Logging;

/**
 * This is the southern component of the PCGenFrame.
 * It will show source loading progress and a corresponding error icon
 * (if there are errors)
 * TODO: add support for concurrent task execution
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public final class PCGenStatusBar extends JPanel
{

	private PCGenFrame frame;
	private JLabel messageLabel;
	private JProgressBar progressBar;
	private JLabel loadStatusLabel;

	public PCGenStatusBar(PCGenFrame frame)
	{
		this.frame = frame;
		this.messageLabel = new JLabel();
		this.progressBar = new JProgressBar();
		this.loadStatusLabel = new JLabel();
		initComponents();
	}

	private void initComponents()
	{
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(messageLabel);
		add(Box.createHorizontalGlue());
		progressBar.setStringPainted(true);
		progressBar.setVisible(false);
		add(progressBar);
		add(loadStatusLabel);
	}

	public void setContextMessage(String message)
	{
		messageLabel.setText(message);
	}

	public void setSourceLoadErrors(List<LogRecord> errors)
	{
		if (errors != null && !errors.isEmpty())
		{
			int nerrors = 0;
			int nwarnings = 0;
			for (LogRecord logRecord : errors)
			{
				if (logRecord.getLevel().intValue() > Logging.WARNING.intValue())
				{
					nerrors++;
				}
				else if (logRecord.getLevel().intValue() > Logging.INFO.intValue())
				{
					nwarnings++;
				}
			}
			if (nerrors > 0)
			{
				loadStatusLabel.setIcon(Icons.Stop16.getImageIcon());
			}
			else if (nwarnings > 0)
			{
				loadStatusLabel.setIcon(Icons.Alert16.getImageIcon());
			}
			else
			{
				loadStatusLabel.setIcon(Icons.Ok16.getImageIcon());
			}
			loadStatusLabel.setToolTipText(nerrors + " errors and " + nwarnings +
					" warnings occured while loading the sources");
		}
	}

	/**
	 * This creates a swing worker that encapsulates a PCGenTask.
	 * As the worker is executed information regarding its progress
	 * will be updated to the PCGenStatusBar's progress bar.
	 * Upon completion of the task execution the worker returns a
	 * list of error messages that occured during the task execution.
	 * Its up to the caller of this method figure out what to do with
	 * the messages (if any).
	 * @param taskName a string describing the task
	 * @param task a PCGenTask
	 * @return a SwingWorker
	 */
	public SwingWorker<List<LogRecord>> createWorker(String taskName, PCGenTask task)
	{
		return new TaskExecutor(taskName, task);
	}

	private class TaskExecutor extends SwingWorker<List<LogRecord>> implements PCGenTaskListener
	{

		private final String name;
		private final PCGenTask task;
		private boolean dirty = false;
		private List<LogRecord> errors = new ArrayList<LogRecord>();

		public TaskExecutor(String name, PCGenTask task)
		{
			this.name = name;
			this.task = task;
		}

		@Override
		public List<LogRecord> construct()
		{
			try
			{
				SwingUtilities.invokeAndWait(new Runnable()
				{

					@Override
					public void run()
					{
						progressBar.setVisible(true);
					}

				});
			}
			catch (Exception ex)
			{
				//Not much we can do about this
			}
			String oldMessage = messageLabel.getText();
			setContextMessage(name);
			task.addPCGenTaskListener(this);
			task.execute();
			task.removePCGenTaskListener(this);
			setContextMessage(oldMessage);
			return errors;
		}

		@Override
		public void finished()
		{
			progressBar.setVisible(false);
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
						progressBar.getModel().setRangeProperties(task.getProgress(), 1, 0, task.getMaximum(), true);
						progressBar.setString(task.getMessage());
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

	}

}
