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

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.gui.converter.LSTConverter;
import pcgen.gui.converter.ObjectInjector;
import pcgen.gui.converter.UnstretchingGridLayout;
import pcgen.gui.converter.event.ProgressEvent;
import pcgen.io.PCGFile;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.EditorLoadContext;
import pcgen.rules.context.LoadContext;

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
public class RunConvertPanel extends ConvertSubPanel
{

	private JPanel message;
	private JProgressBar progressBar;
	private ArrayList<Campaign> totalCampaigns;
	private final LoadContext context;

	public RunConvertPanel()
	{
		context = new EditorLoadContext();
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
	public boolean performAnalysis(CDOMObject pc)
	{
		final File rootDir = pc.get(ObjectKey.DIRECTORY);
		final String outDir = pc.get(ObjectKey.WRITE_DIRECTORY).getAbsolutePath();
		totalCampaigns = new ArrayList<Campaign>();
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
		
		new Thread(new Runnable()
		{
			public void run()
			{
				LSTConverter converter = new LSTConverter(context, rootDir,
						outDir);
				try
				{
					converter.doStartup();
				}
				catch (PersistenceLayerException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int step = 1;
				for (Campaign campaign : totalCampaigns)
				{
					converter.processCampaign(campaign);
					progressBar.setValue(step++);
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

				message.add(new JLabel("Conversion complete, press next button to finish..."));
				message.revalidate();
		        
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
				
		message = new JPanel();
		message.setLayout(new UnstretchingGridLayout(0, 1));
		message.add(new JLabel("Conversion in progress"));
		message.add(new JLabel(" "));

        progressBar = new JProgressBar(0, totalCampaigns.size());
        progressBar.setValue(0);
        Dimension d = progressBar.getPreferredSize();
        d.width = 400;
        progressBar.setPreferredSize(d);
        progressBar.setStringPainted(true);

        message.add(progressBar);
		message.add(new JLabel(" "));
		panel.add(message);
		panel.setPreferredSize(new Dimension(800, 500));
	}
}
