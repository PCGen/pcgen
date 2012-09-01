/*
 * RunConvertPanel.java
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
 *
 * Created on 18/01/2009 11:31:57 AM
 *
 * $Id$
 */

package pcgen.gui.converter.panel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.swing.JLabel;
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
import pcgen.gui.converter.ConversionDecider;
import pcgen.gui.converter.LSTConverter;
import pcgen.gui.converter.ObjectInjector;
import pcgen.gui.converter.event.ProgressEvent;
import pcgen.gui.converter.event.TaskStrategyMessage;
import pcgen.gui.utils.Utility;
import pcgen.io.PCGFile;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.EditorLoadContext;
import pcgen.util.Logging;

/**
 * The Class <code>RunConvertPanel</code> provides a display while 
 * the conversion is being run.
 * 
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
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
	private Component statusField;

	public RunConvertPanel(Component statusField)
	{
		context = new EditorLoadContext();
		this.statusField = statusField;
	}
	
	/* (non-Javadoc)
	 * @see pcgen.gui.converter.panel.ConvertSubPanel#autoAdvance(pcgen.cdom.base.CDOMObject)
	 */
	@Override
	public boolean autoAdvance(CDOMObject pc)
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see pcgen.gui.converter.panel.ConvertSubPanel#performAnalysis(pcgen.cdom.base.CDOMObject)
	 */
	@Override
	public boolean performAnalysis(final CDOMObject pc)
	{
		final File rootDir = pc.get(ObjectKey.DIRECTORY);
		final File outDir = pc.get(ObjectKey.WRITE_DIRECTORY);
		totalCampaigns = new ArrayList<Campaign>(pc.getSafeListFor(ListKey.CAMPAIGN));
		for (Campaign campaign : pc.getSafeListFor(ListKey.CAMPAIGN))
		{
			// Add all sub-files to the main campaign, regardless of exclusions
			for (CampaignSourceEntry fName : campaign
					.getSafeListFor(ListKey.FILE_PCC))
			{
				URI uri = fName.getURI();
				if (PCGFile.isPCGenCampaignFile(uri))
				{
					Campaign c = Globals.getCampaignByURI(uri, false);
					totalCampaigns.add(c);
				}
			}
		}
		sortCampaignsByRank(totalCampaigns);
		
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				Logging.registerHandler( getHandler() );
				SettingsHandler.setGame(pc.get(ObjectKey.GAME_MODE).getName());
				GameMode mode = SettingsHandler.getGame();
				//Necessary for "good" behavior
				mode.resolveInto(context.ref);
				//Necessary for those still using Globals.getContext
				mode.resolveInto(mode.getContext().ref);
				LSTConverter converter = new LSTConverter(context, rootDir,
						outDir.getAbsolutePath(), RunConvertPanel.this);
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
				ObjectInjector oi = new ObjectInjector(context, outDir,
						rootDir, converter);
				try
				{
					oi.writeInjectedObjects(totalCampaigns);
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				converter.deleteObserver( RunConvertPanel.this );
				Logging.removeHandler( getHandler() );
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
				addMessage("\nConversion complete, press next button to finish...");
				progressBar.setValue(progressBar.getMaximum());
		        
				fireProgressEvent(ProgressEvent.ALLOWED);
			}
		}).start();
		return true;
	}

	/* (non-Javadoc)
	 * @see pcgen.gui.converter.panel.ConvertSubPanel#setupDisplay(javax.swing.JPanel, pcgen.cdom.base.CDOMObject)
	 */
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

	private Handler getHandler()
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
		String message =
				(filename == null || filename.length() == 0) ? ""
					: "Converting " + filename;
		int width = fm.stringWidth(message);
		if (width >= statusField.getWidth())
		{
			message =
					Utility.shortenString(fm, message, statusField.getWidth());
		}

		TaskStrategyMessage.sendStatus(this, message);
		currFilename = filename;
	}

	public void addMessage(String message)
	{
		if (currFilename.length() > 0 && !currFilename.equals(lastNotifiedFilename))
		{
			getMessageArea().append("\n" + currFilename + "\n");
			lastNotifiedFilename = currFilename;
		}
		getMessageArea().append(message + "\n");
	}

	/**
	 * Keeps track if there has been set an error message.
	 *
	 * @param errorState <code>true</code> if there was an error message
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
			final Exception e = (Exception)arg;
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
		Runnable doWork = new Runnable() {
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
		public void publish(final LogRecord arg0)
		{
			Runnable doWork = new Runnable()
			{
				@Override
				public void run()
				{
					addMessage(arg0.getLevel() + " " + arg0.getMessage());
					setErrorState(true);
				}
			};
			SwingUtilities.invokeLater(doWork);
		}
		
	}


	/* (non-Javadoc)
	 * @see pcgen.gui.converter.ConversionDecider#getConversionDecision(java.lang.String, java.util.List, java.util.List)
	 */
	@Override
	public String getConversionDecision(String overallDescription,
		List<String> choiceDescriptions, List<String> choiceTokenResults,
		int defaultChoice)
	{
		final ConversionChoiceDialog ccd =
				new ConversionChoiceDialog(null, overallDescription,
					choiceDescriptions, defaultChoice);
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
		catch (InterruptedException e)
		{
			Logging.errorPrint("Failed to display user choice, due to: ", e);
		}
		catch (InvocationTargetException e)
		{
			Logging.errorPrint("Failed to display user choice, due to: ", e);
		}
		result = ccd.getResult();
		return choiceTokenResults.get(result);
	}

	@Override
	public String getConversionInput(String overallDescription)
	{
		final ConversionInputDialog ccd = new ConversionInputDialog(null,
				overallDescription);

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
		catch (InterruptedException e)
		{
			Logging.errorPrint("Failed to display user choice, due to: ", e);
		}
		catch (InvocationTargetException e)
		{
			Logging.errorPrint("Failed to display user choice, due to: ", e);
		}
		return ccd.getResult();
	}

	/**
	 * This method sorts the provided listof Campaign objects by rank.
	 *
	 * @param aSelectedCampaignsList List of Campaign objects to sort
	 */
	private void sortCampaignsByRank(final List<Campaign> aSelectedCampaignsList)
	{
		Collections.sort(aSelectedCampaignsList, new Comparator<Campaign>()
		{
			@Override
			public int compare(Campaign c1, Campaign c2)
			{
				return c1.getSafe(IntegerKey.CAMPAIGN_RANK) - c2.getSafe(IntegerKey.CAMPAIGN_RANK);
			}

		});

	}

}
