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
import java.io.FilenameFilter;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.commons.lang3.StringUtils;

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

	/**
	 * A list of URIs for PCC files to load. Populated by {@link #findPCCFiles(java.io.File) findPCCFiles}.
	 */
	private final LinkedList<URI> campaignFiles = new LinkedList<>();

	@Override
	public String getMessage()
	{
		return LanguageBundle.getString("in_taskLoadCampaigns"); //$NON-NLS-1$
	}

	@Override
	public void execute()
	{
		// Load the initial campaigns
		if (alternateSourceFolder != null)
		{
			findPCCFiles(alternateSourceFolder);
		}
		else
		{
			findPCCFiles(new File(ConfigurationSettings.getPccFilesDir()));
			final String vendorDataDir = PCGenSettings.getVendorDataDir();
			if (vendorDataDir != null)
			{
				findPCCFiles(new File(vendorDataDir));
			}
			final String homebrewDataDir = PCGenSettings.getHomebrewDataDir();
			if (homebrewDataDir != null)
			{
				findPCCFiles(new File(homebrewDataDir));
			}
		}
		setMaximum(campaignFiles.size());
		loadCampaigns();
		CampaignFileLoader.initCampaigns();
	}

	/**
	 * Recursively looks inside a given directory for PCC files
	 * and adds them to the {@link #campaignFiles campaignFiles} list.
	 * @param aDirectory The directory to search.
	 */
	private void findPCCFiles(final File aDirectory)
	{
		final FilenameFilter pccFileFilter = (parentDir, fileName) -> StringUtils.endsWithIgnoreCase(fileName, ".pcc")
			|| new File(parentDir, fileName).isDirectory();

		if (!aDirectory.exists() || !aDirectory.isDirectory())
		{
			return;
		}
		for (final File file : aDirectory.listFiles(pccFileFilter))
		{
			if (file.isDirectory())
			{
				findPCCFiles(file);
				continue;
			}
			campaignFiles.add(file.toURI());
		}
	}

	/**
	 * Passes the campaign PCC files referenced by
	 * {@link #campaignFiles campaignFiles} to a {@link pcgen.persistence.lst.CampaignLoader CampaignLoader},
	 * which will load the data within into the {@link pcgen.rules.context.LoadContext LoadContext}
	 * of the {@link pcgen.core.Campaign Campaign}.
	 */
	private void loadCampaigns()
	{
		int progress = 0;
		CampaignLoader campaignLoader = new CampaignLoader();
		while (!campaignFiles.isEmpty())
		{
			// Pull the first URI from the list
			URI uri = campaignFiles.poll();
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
			progress++;
			setProgress(progress);
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
