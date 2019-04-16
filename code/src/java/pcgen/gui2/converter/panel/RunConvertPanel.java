/*
 * Copyright 2009 (C) James Dempsey
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
 */

package pcgen.gui2.converter.panel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Campaign;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.gui2.converter.ConversionDecider;
import pcgen.gui2.converter.LSTConverter;
import pcgen.gui2.converter.ObjectInjector;
import pcgen.gui2.converter.event.ProgressEvent;
import pcgen.gui2.converter.event.TaskStrategyMessage;
import pcgen.gui2.tools.Utility;
import pcgen.io.PCGFile;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.EditorLoadContext;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenPropBundle;
import pcgen.system.PCGenSettings;
import pcgen.util.Logging;

/**
 * The Class {@code RunConvertPanel} provides a display while
 * the conversion is being run.
 * 
 * 
 */
public class RunConvertPanel extends ConvertSubPanel implements Observer, ConversionDecider
{
	int totalFileCount = 0;
	int currentFileCount = 0;

	private JProgressBar progressBar;
	private ArrayList<Campaign> totalCampaigns;
	private final EditorLoadContext context;
	private JTextArea messageArea;
	private JScrollPane messageAreaContainer;
	private boolean errorState = false;
	private String lastNotifiedFilename = "";
	private String currFilename = "";
	private final Component statusField;
	private final File changeLogFile;

	public RunConvertPanel(Component statusField)
	{
		context = new EditorLoadContext();
		this.statusField = statusField;
		PCGenSettings context = PCGenSettings.getInstance();
		String dataLogFileName = context.initProperty(PCGenSettings.CONVERT_DATA_LOG_FILE, "dataChanges.log");
		changeLogFile = new File(dataLogFileName);
	}

	@Override
	public boolean autoAdvance(CDOMObject pc)
	{
		return false;
	}

	@Override
	public boolean performAnalysis(final CDOMObject pc)
	{
		logSummary(pc);

		final File rootDir = pc.get(ObjectKey.DIRECTORY);
		final File outDir = pc.get(ObjectKey.WRITE_DIRECTORY);
		totalCampaigns = new ArrayList<>(pc.getSafeListFor(ListKey.CAMPAIGN));
		for (Campaign campaign : pc.getSafeListFor(ListKey.CAMPAIGN))
		{
			// Add all sub-files to the main campaign, regardless of exclusions
			campaign.getSafeListFor(ListKey.FILE_PCC)
			        .stream()
			        .map(CampaignSourceEntry::getURI)
			        .filter(PCGFile::isPCGenCampaignFile)
			        .map(uri -> Globals.getCampaignByURI(uri, false))
			        .filter(Objects::nonNull)
			        .forEach(subcampaign -> totalCampaigns.add(subcampaign));
		}
		sortCampaignsByRank(totalCampaigns);

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				Logging.registerHandler(getHandler());
				SettingsHandler.setGame(pc.get(ObjectKey.GAME_MODE).getName());
				GameMode mode = SettingsHandler.getGame();
				//Necessary for "good" behavior
				mode.resolveInto(context.getReferenceContext());
				//Necessary for those still using Globals.getContext
				mode.resolveInto(mode.getContext().getReferenceContext());
				LSTConverter converter;
				Writer changeLogWriter;
				try
				{
					changeLogWriter = new FileWriter(changeLogFile);

					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					String startTime = simpleDateFormat.format(new Date());
					changeLogWriter.append("PCGen Data Converter v" + PCGenPropBundle.getVersionNumber()
						+ " - conversion started at " + startTime + "\n");
					changeLogWriter.append("Outputting files to " + outDir.getAbsolutePath() + "\n");
				}
				catch (IOException e1)
				{
					Logging.errorPrint("Failed to initialise LSTConverter", e1);
					return;
				}
				converter = new LSTConverter(context, rootDir, outDir.getAbsolutePath(), RunConvertPanel.this,
					changeLogWriter);
				converter.addObserver(RunConvertPanel.this);
				int numFiles = 0;
				for (Campaign campaign : totalCampaigns)
				{
					numFiles += converter.getNumFilesInCampaign(campaign);
				}
				setTotalFileCount(numFiles);
				converter.initCampaigns(totalCampaigns);
				for (Campaign campaign : totalCampaigns)
				{
					converter.processCampaign(campaign);
				}
				ObjectInjector oi = new ObjectInjector(context, outDir, rootDir, converter);
				try
				{
					oi.writeInjectedObjects(totalCampaigns);
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try
				{
					changeLogWriter.close();
				}
				catch (IOException e)
				{
					Logging.errorPrint("LSTConverter.wrapUp failed", e);

				}
				converter.deleteObserver(RunConvertPanel.this);
				Logging.removeHandler(getHandler());
				try
				{
					// Wait for any left over messages to catch up
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					// Ignore exception
				}
				setCurrentFilename("");
				addMessage("\nConversion complete.");
				if (getHandler().getNumErrors() > 0)
				{
					JOptionPane.showMessageDialog(
						null, LanguageBundle.getFormattedString("in_lstConvErrorsFound", //$NON-NLS-1$
						getHandler().getNumErrors()), LanguageBundle.getString("in_lstConvErrorsTitle"), //$NON-NLS-1$
						JOptionPane.ERROR_MESSAGE);
				}
				progressBar.setValue(progressBar.getMaximum());

				fireProgressEvent(ProgressEvent.AUTO_ADVANCE);
			}
		}).start();
		return true;
	}

	@Override
	public void setupDisplay(JPanel panel, CDOMObject pc)
	{
		panel.setLayout(new GridBagLayout());

		JLabel introLabel = new JLabel("Conversion in progress");
		GridBagConstraints gbc = new GridBagConstraints();
		Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1, 1.0, 0);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 10, 5, 10);
		panel.add(introLabel, gbc);

		JLabel explainLabel = new JLabel("<html>The LST data is being converted. In the log, "
			+ "LSTERROR rows are errors that need to be manually corrected in the source data. "
			+ "LSTWARN rows indicate changes the converter is making. " + "See " + changeLogFile.getAbsolutePath()
			+ " for a log of all data changes.</html>");
		explainLabel.setFocusable(true);
		Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1, 1.0, 0);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 10, 5, 10);
		panel.add(explainLabel, gbc);

		progressBar = getProgressBar();
		Dimension d = progressBar.getPreferredSize();
		d.width = 400;
		progressBar.setPreferredSize(d);
		progressBar.setStringPainted(true);
		Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1, 1.0, 0);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(progressBar, gbc);

		messageAreaContainer = new JScrollPane(getMessageArea());
		Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1.0, 1.0);
		gbc.fill = GridBagConstraints.BOTH;
		panel.add(messageAreaContainer, gbc);

		panel.setPreferredSize(new Dimension(800, 500));
	}

	private LoadHandler handler = null;

	private LoadHandler getHandler()
	{
		if (handler == null)
		{
			handler = new LoadHandler();
		}
		return handler;
	}

	public void setCurrentFilename(String filename)
	{
		Graphics g = statusField.getGraphics();
		FontMetrics fm = g.getFontMetrics();
		String message = (filename == null || filename.isEmpty()) ? "" : "Converting " + filename;
		int width = fm.stringWidth(message);
		if (width >= statusField.getWidth())
		{
			message = Utility.shortenString(fm, message, statusField.getWidth());
		}

		TaskStrategyMessage.sendStatus(this, message);
		currFilename = filename;
	}

	public void addMessage(String message)
	{
		if (!currFilename.isEmpty() && !currFilename.equals(lastNotifiedFilename))
		{
			getMessageArea().append("\n" + currFilename + "\n");
			lastNotifiedFilename = currFilename;
		}
		getMessageArea().append(message + "\n");
	}

	/**
	 * Keeps track if there has been set an error message.
	 *
	 * @param errorState {@code true} if there was an error message
	 */
	public void setErrorState(boolean errorState)
	{
		this.errorState = errorState;
	}

	public boolean getErrorState()
	{
		return errorState;
	}

	/**
	 * This method initializes progressBar
	 *
	 * @return javax.swing.JProgressBar
	 */
	private JProgressBar getProgressBar()
	{
		if (progressBar == null)
		{
			progressBar = new JProgressBar();
			progressBar.setValue(0);
			progressBar.setStringPainted(true);
		}

		return progressBar;
	}

	/**
	 * This method initializes messageArea
	 *
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getMessageArea()
	{
		if (messageArea == null)
		{
			messageArea = new JTextArea();
			messageArea.setName("errorMessageBox");
			messageArea.setEditable(false);
			messageArea.setTabSize(8);
		}

		return messageArea;
	}

	@Override
	public void update(Observable o, Object arg)
	{
		if (arg instanceof URI)
		{
			setCurrentFileCount(currentFileCount + 1);

			final URI uri = (URI) arg;
			setCurrentFilename(uri.toString());
		}
		else if (arg instanceof Exception)
		{
			final Exception e = (Exception) arg;
			Runnable doWork = new Runnable()
			{
				@Override
				public void run()
				{
					addMessage(e.getMessage());
					setErrorState(true);
				}
			};
			SwingUtilities.invokeLater(doWork);
			System.out.println("Persistence Observer: ERROR: " + e.getMessage());
		}
		else
		{
			System.out.println("Persistence Observer: UNKNOWN: " + arg);
		}
	}

	/**
	 * @param iFileCount The totalFileCount to set.
	 */
	protected void setTotalFileCount(final int iFileCount)
	{
		totalFileCount = iFileCount;
		Runnable doWork = new Runnable()
		{
			@Override
			public void run()
			{
				getProgressBar().setMaximum(iFileCount);
			}
		};
		SwingUtilities.invokeLater(doWork);
	}

	public void setCurrentFileCount(int curr)
	{
		currentFileCount = curr;
		getProgressBar().setValue(curr);
	}

	/**
	 * A log handler to capture load errors and warnings and 
	 * display them in the message section of the panel.
	 */
	private class LoadHandler extends Handler
	{
		int numErrors = 0;
		int numWarnings = 0;

		public LoadHandler()
		{
			setLevel(Logging.LST_WARNING);
		}

		@Override
		public void close() throws SecurityException
		{
			// Nothing to do
		}

		@Override
		public void flush()
		{
			// Nothing to do
		}

		@Override
		public void publish(final LogRecord logRecord)
		{
			Runnable doWork = new Runnable()
			{
				@Override
				public void run()
				{
					if (logRecord.getLevel().intValue() > Logging.WARNING.intValue())
					{
						numErrors++;
					}
					else if (logRecord.getLevel().intValue() > Logging.INFO.intValue())
					{
						numWarnings++;
					}
					addMessage(logRecord.getLevel() + " " + logRecord.getMessage());
					setErrorState(true);
				}
			};
			SwingUtilities.invokeLater(doWork);
		}

		/**
		 * @return the numErrors
		 */
		public int getNumErrors()
		{
			return numErrors;
		}

		/**
		 * @return the numWarnings
		 */
		public int getNumWarnings()
		{
			return numWarnings;
		}

	}

	@Override
	public String getConversionDecision(String overallDescription, List<String> choiceDescriptions,
		List<String> choiceTokenResults, int defaultChoice)
	{
		final ConversionChoiceDialog ccd =
				new ConversionChoiceDialog(null, overallDescription, choiceDescriptions, defaultChoice);
		int result = 0;

		Runnable showDialog = new Runnable()
		{
			@Override
			public void run()
			{
				ccd.setVisible(true);
			}
		};
		try
		{
			SwingUtilities.invokeAndWait(showDialog);
		}
		catch (InterruptedException | InvocationTargetException e)
		{
			Logging.errorPrint("Failed to display user choice, due to: ", e);
		}
		result = ccd.getResult();
		return choiceTokenResults.get(result);
	}

	@Override
	public String getConversionInput(String overallDescription)
	{
		final ConversionInputDialog ccd = new ConversionInputDialog(null, overallDescription);

		Runnable showDialog = new Runnable()
		{
			@Override
			public void run()
			{
				ccd.setVisible(true);
			}
		};
		try
		{
			SwingUtilities.invokeAndWait(showDialog);
		}
		catch (InterruptedException | InvocationTargetException e)
		{
			Logging.errorPrint("Failed to display user choice, due to: ", e);
		}
		return ccd.getResult();
	}

	/**
	 * This method sorts the provided list of Campaign objects by rank.
	 *
	 * @param aSelectedCampaignsList List of Campaign objects to sort
	 */
	private static void sortCampaignsByRank(final List<Campaign> aSelectedCampaignsList)
	{
		aSelectedCampaignsList.sort(Comparator.comparingInt(campaign -> campaign.getSafe(IntegerKey.CAMPAIGN_RANK)));

	}

	private void logSummary(final CDOMObject pc)
	{
		Logging.log(Logging.INFO, "Running data conversion using the following settings:");
		Logging.log(Logging.INFO, "Source Folder: " + pc.get(ObjectKey.DIRECTORY).getAbsolutePath());
		Logging.log(Logging.INFO, "Destination Folder: " + pc.get(ObjectKey.WRITE_DIRECTORY).getAbsolutePath());
		Logging.log(Logging.INFO, "Game mode: " + pc.get(ObjectKey.GAME_MODE).getDisplayName());
		List<Campaign> campaigns = pc.getSafeListFor(ListKey.CAMPAIGN);
		StringBuilder campDisplay = new StringBuilder("");
		for (int i = 0; i < campaigns.size(); i++)
		{
			campDisplay.append(campaigns.get(i).getDisplayName());
			campDisplay.append("\n");
		}
		Logging.log(Logging.INFO, "Sources: " + campDisplay.toString());
		Logging.log(Logging.INFO, "Logging changes to " + changeLogFile.getAbsolutePath());
	}

}
