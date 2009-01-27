/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.gui.converter;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.io.PCGFile;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.LoadContext;

public class BatchConverter
{

	private final String outDir;
	private final File rootDir;
	private final ArrayList<Campaign> campaigns;
	private final LoadContext context;

	public BatchConverter(LoadContext lc, String outputDirectory,
			String rootDirectory, List<Campaign> list)
	{
		outDir = outputDirectory;
		rootDir = new File(rootDirectory);
		campaigns = new ArrayList<Campaign>(list);
		context = lc;
	}

	public void process() throws PersistenceLayerException
	{
		ArrayList<Campaign> totalCampaigns = new ArrayList<Campaign>(campaigns);
		for (Campaign campaign : campaigns)
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
		startCampaign(totalCampaigns);
	}

	private void startCampaign(List<Campaign> list)
			throws PersistenceLayerException
	{
		File outFile = new File(rootDir, File.separator + outDir);
		LSTConverter converter =
				new LSTConverter(context, rootDir, outFile.getAbsolutePath(),
					null);
		converter.doStartup();
		for (Campaign campaign : list)
		{
			converter.processCampaign(campaign);
		}
		ObjectInjector oi = new ObjectInjector(context, outDir, rootDir,
				converter);
		try
		{
			oi.writeInjectedObjects(list);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
