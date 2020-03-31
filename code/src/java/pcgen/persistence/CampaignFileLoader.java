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
package pcgen.persistence;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;

import java.util.List;
import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.persistence.lst.CampaignLoader;
import pcgen.system.ConfigurationSettings;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;
import pcgen.system.PCGenTask;
import pcgen.util.Logging;

public class CampaignFileLoader extends PCGenTask
{
	private File alternateSourceFolder = null;

	@Override
	public String getMessage()
	{
		return LanguageBundle.getString("in_taskLoadCampaigns"); //$NON-NLS-1$
	}

	@Override
	public void run()
	{
		// Load the initial campaigns
		RecursiveFileFinder recursiveFileFinder = new RecursiveFileFinder();
		final List<URI> campaignFiles = new LinkedList<>();
		if (alternateSourceFolder != null)
		{
			recursiveFileFinder.findFiles(alternateSourceFolder, campaignFiles);
		}
		else
		{
			recursiveFileFinder.findFiles(new File(ConfigurationSettings.getPccFilesDir()), campaignFiles);
			final String vendorDataDir = PCGenSettings.getVendorDataDir();
            recursiveFileFinder.findFiles(new File(vendorDataDir), campaignFiles);
            final String homebrewDataDir = PCGenSettings.getHomebrewDataDir();
            recursiveFileFinder.findFiles(new File(homebrewDataDir), campaignFiles);
        }
		setMaximum(campaignFiles.size());
		loadCampaigns(campaignFiles);
		CampaignFileLoader.initCampaigns();
	}

	/**
	 * Passes the campaign PCC files referenced by
	 * {@link #campaignFiles campaignFiles} to a {@link pcgen.persistence.lst.CampaignLoader CampaignLoader},
	 * which will load the data within into the {@link pcgen.rules.context.LoadContext LoadContext}
	 * of the {@link pcgen.core.Campaign Campaign}.
	 * @param campaignFiles
	 */
	private void loadCampaigns(List<URI> campaignFiles)
	{
		int progress = 0;
		CampaignLoader campaignLoader = new CampaignLoader();
		for (URI uri : campaignFiles)
		{
			// Do not load campaign if already loaded
			if (Globals.getCampaignByURI(uri, false) == null)
			{
				try
				{
					// Pass this URI to campaign loader
					campaignLoader.loadCampaignLstFile(uri);
				}
				catch (PersistenceLayerException ex)
				{
					// LATER: This is not an appropriate way to deal with this exception.
					// Deal with it this way because of the way the loading takes place.  XXX
					Logging.errorPrint("PersistanceLayer", ex);
				}
			}
			setProgress(progress++);
		}
	}

	/**
	 * Goes through the campaigns in {@link #campaignFiles campaignFiles} and loads
	 * data associated with dependent campaigns.
	 */
	private static void initCampaigns()
	{
		// This may modify the globals list; need a local copy so
		// the iteration doesn't fail.
		// This may not be true.  this is only a shallow copy and doesn't duplicate the contents
		Iterable<Campaign> initialCampaigns = new ArrayList<>(Globals.getCampaignList());

		CampaignLoader campaignLoader = new CampaignLoader();
		for (final Campaign c : initialCampaigns)
		{
			campaignLoader.initRecursivePccFiles(c);
		}
	}

	/**
	 * @param alternateSourceFolder the alternateSourceFolder to set
	 */
	public void setAlternateSourceFolder(final File alternateSourceFolder)
	{
		this.alternateSourceFolder = alternateSourceFolder;
	}

}
