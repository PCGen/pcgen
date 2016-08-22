/*
 * CampaignFileLoader.java
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
 * Created on Apr 15, 2010, 4:00:56 PM
 */
package pcgen.persistence;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.persistence.lst.CampaignLoader;
import pcgen.system.ConfigurationSettings;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;
import pcgen.system.PCGenTask;
import pcgen.util.Logging;

/**
 *
 * @author Connor Petty &lt;cpmeister@users.sourceforge.net&gt;
 */
public class CampaignFileLoader extends PCGenTask
{
	private File alternateSourceFolder = null;

    /**
     * A {@link java.io.FilenameFilter FilenameFilter} that returns true if a given
     * file path ends with .pcc or is a directory.
     */
	private final FilenameFilter pccFileFilter = new FilenameFilter()
	{

        @Override
		public boolean accept(File parentDir, String fileName)
		{
			/*
			 * This is a specific "hack" in order to speed loading when
			 * in a development (Subversion-based) environment - Tom
			 * Parker 1/17/07
			 */
			if (".svn".equals(fileName))
			{
				return false;
			}
			if (StringUtils.endsWithIgnoreCase(fileName, ".pcc"))
			{
				return true;
			}
			return new File(parentDir, fileName).isDirectory();
		}

	};
    /**
     * A list of URIs for PCC files to load. Populated by {@link #findPCCFiles(java.io.File) findPCCFiles}.
     */
	private LinkedList<URI> campaignFiles = new LinkedList<URI>();

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
		initCampaigns();
	}

    /**
     * Recursively looks inside a given directory for PCC files and adds them to the {@link #campaignFiles campaignFiles} list.
     * @param aDirectory The directory to search.
     */
	private void findPCCFiles(File aDirectory)
	{
		if (!aDirectory.exists() || !aDirectory.isDirectory())
		{
			return;
		}
		for (File file : aDirectory.listFiles(pccFileFilter))
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
     * Passes the campaign PCC files referenced by {@link #campaignFiles campaignFiles} to a {@link pcgen.persistence.lst.CampaignLoader CampaignLoader},
     * which will load the data within into the {@link pcgen.rules.context.LoadContext LoadContext} of the {@link pcgen.core.Campaign Campaign}.
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
	private void initCampaigns()
	{
		// This may modify the globals list; need a local copy so
		// the iteration doesn't fail.
		List<Campaign> initialCampaigns =
				new ArrayList<Campaign>(Globals.getCampaignList());

        CampaignLoader campaignLoader = new CampaignLoader();
		for (Campaign c : initialCampaigns)
		{
			campaignLoader.initRecursivePccFiles(c);
		}
	}

	/**
	 * @return the alternateSourceFolder
	 */
	public File getAlternateSourceFolder()
	{
		return alternateSourceFolder;
	}

	/**
	 * @param alternateSourceFolder the alternateSourceFolder to set
	 */
	public void setAlternateSourceFolder(File alternateSourceFolder)
	{
		this.alternateSourceFolder = alternateSourceFolder;
	}

}
