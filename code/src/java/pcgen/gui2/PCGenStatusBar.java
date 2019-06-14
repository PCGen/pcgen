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
package pcgen.gui2;

import java.util.List;
import java.util.logging.LogRecord;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import pcgen.gui2.tools.CursorControlUtilities;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.util.StatusWorker;
import pcgen.gui3.GuiAssertions;
import pcgen.gui3.GuiUtility;
import pcgen.system.PCGenTask;
import pcgen.util.Logging;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * This is the southern component of the PCGenFrame.
 * It will show source loading progress and a corresponding error icon
 * (if there are errors)
 * TODO: add support for concurrent task execution
 */
public final class PCGenStatusBar extends JPanel
{
	private final PCGenFrame frame;
	private final JLabel messageLabel;
	private final JProgressBar progressBar;
	private final Button loadStatusButton;

	PCGenStatusBar(PCGenFrame frame)
	{
		this.frame = frame;
		this.messageLabel = new JLabel();
		this.progressBar = new JProgressBar();
		this.loadStatusButton = new Button();
		initComponents();
	}

	private void initComponents()
	{
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		add(messageLabel);
		add(Box.createHorizontalGlue());
		progressBar.setStringPainted(true);
		progressBar.setVisible(false);
		add(progressBar);
		add(Box.createHorizontalGlue());
		add(GuiUtility.wrapParentAsJFXPanel(loadStatusButton));
		loadStatusButton.setOnAction(this::loadStatusLabelAction);
	}

	public void setContextMessage(String message)
	{
		messageLabel.setText(message);
	}

	public String getContextMessage()
	{
		return messageLabel.getText();
	}

	public JProgressBar getProgressBar()
	{
		return progressBar;
	}

	void setSourceLoadErrors(List<LogRecord> errors)
	{
		GuiAssertions.assertIsNotJavaFXThread();
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

			Image image;
			if (nerrors > 0)
			{
				image = Icons.Stop16.asJavaFX();
			}
			else if (nwarnings > 0)
			{
				image = Icons.Alert16.asJavaFX();
			}
			else
			{
				image = Icons.Ok16.asJavaFX();
			}
			int finalNerrors = nerrors;
			int finalNwarnings = nwarnings;
			Platform.runLater(() -> {
				loadStatusButton.setGraphic(new ImageView(image));
				Tooltip tooltip = new Tooltip(String.format(
						"%d errors and %d warnings occurred while loading the sources",
						finalNerrors,
						finalNwarnings
				));
				loadStatusButton.setTooltip(tooltip);
			});
		}
	}

	/**
	 * This creates a swing worker that encapsulates a PCGenTask.
	 * As the worker is executed information regarding its progress
	 * will be updated to the PCGenStatusBar's progress bar.
	 * Upon completion of the task execution the worker returns a
	 * list of error messages that occurred during the task execution.
	 * Its up to the caller of this method figure out what to do with
	 * the messages (if any).
	 * @param taskName a string describing the task
	 * @param task a PCGenTask
	 * @return a SwingWorker
	 */
	SwingWorker<List<LogRecord>, List<LogRecord>> createWorker(String taskName, PCGenTask task)
	{
		return new StatusWorker(taskName, task, this);
	}

	/**
	 * Shows the progress bar, in indeterminate mode
	 * 
	 * @param msg message to show on status bar
	 * @param indeterminate
	 */
	public void startShowingProgress(final String msg, boolean indeterminate)
	{
		if (!PCGenStatusBar.this.isValid())
		{
			// Do nothing if called during startup or shutdown
			return;
		}
		setVisible(true);
		CursorControlUtilities.startWaitCursor(this);
		setContextMessage(msg);
		getProgressBar().setVisible(true);
		getProgressBar().setIndeterminate(indeterminate);
		getProgressBar().setStringPainted(true);
		getProgressBar().setString(msg);
	}

	/**
	 * Clears the progress bar and turns off the wait cursor
	 */
	public void endShowingProgress()
	{
		CursorControlUtilities.stopWaitCursor(this);
		setContextMessage(null);
		getProgressBar().setString(null);
		getProgressBar().setVisible(false);
	}

	/**
	 * Shows the log window when the load status icon is clicked.
	 */
	private void loadStatusLabelAction(final ActionEvent actionEvent)
	{
		frame.getActionMap().get(PCGenActionMap.LOG_COMMAND).actionPerformed(null);
	}
}
