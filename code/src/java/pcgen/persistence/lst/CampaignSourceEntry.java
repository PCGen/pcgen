/*
 * CampaignSourceEntry.java
 * Copyright 2003 (C) David Hibbs <sage_sam@users.sourceforge.net>
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
 * Created on November 17, 2003, 12:29 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.persistence.lst;

import java.util.ArrayList;
import java.util.List;

import pcgen.core.Campaign;
import pcgen.core.utils.CoreUtility;

/**
 * This class is used to match a source file to the campaign that
 * loaded it.
 */
public class CampaignSourceEntry
{
	private Campaign campaign = null;
	private List<String> excludeItems = new ArrayList<String>();
	private List<String> includeItems = new ArrayList<String>();
	private String file = null;
	private String stringForm = null;

	/**
	 * CampaignSourceEntry constructor.
	 *
	 * @param campaign Campaign that referenced the provided file.
	 *         Must not be null.
	 * @param lstFile String URL formatted path to an LST source file
	 *         Must not be null.
	 */
	public CampaignSourceEntry(Campaign campaign, String lstFile)
	{
		super();

		// make sure a campaign was provided
		if (campaign == null)
		{
			throw new IllegalArgumentException("campaign can't be null");
		}

		// make sure a file was provided
		if (lstFile == null)
		{
			throw new IllegalArgumentException("lstFile can't be null");
		}

		// store the campaign and file values
		this.campaign = campaign;
		this.file = getIncludesExcludes(lstFile);
	}

	/**
	 * This method gets the Campaign that was the source of the
	 * file. (I.e. the reason it was loaded)
	 * @return Campaign that requested the file be loaded
	 */
	public Campaign getCampaign()
	{
		return campaign;
	}

	/**
	 * This method gets a list of the items contained in the given source
	 * file to exclude from getting saved in memory.  All other objects
	 * in the file are to be included.
	 * @return List of String names of objects to exclude
	 */
	public List<String> getExcludeItems()
	{
		return excludeItems;
	}

	/**
	 * This method gets the file/path of the LST file.
	 * @return String url-formatted path to the LST file
	 */
	public String getFile()
	{
		return file;
	}

	/**
	 * This method gets a list of the items containined in the given source
	 * file to include in getting saved in memory.  All other objects
	 * in the file are to be excluded.
	 * @return List of String names of objects to include
	 */
	public List<String> getIncludeItems()
	{
		return includeItems;
	}

	/**
	 * @param arg0
	 * @return -1 if file does not compare
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(CampaignSourceEntry arg0)
	{
		if (arg0 == null)
		{
			return -1;
		}

		return this.file.compareTo(arg0.file);
	}

	/**
	 * @param arg0 
	 * @return true if equals
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object arg0)
	{
		if (! (arg0 instanceof CampaignSourceEntry))
		{
			return false;
		}

		return file.equals(((CampaignSourceEntry)arg0).file);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return this.file.hashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		if (stringForm == null)
		{
			StringBuffer sBuff = new StringBuffer();
			sBuff.append("Campaign: ");
			sBuff.append(campaign.getDisplayName());
			sBuff.append("; SourceFile: ");
			sBuff.append(file);

			stringForm = sBuff.toString();
		}

		return stringForm;
	}

	/**
	 * This method takes a filename as present in an LST file and processes
	 * the INCLUDE and EXCLUDE information appended at its end, loading
	 * the object names into either includeItems or excludeItems.  It
	 * then returns the simple name of the file.
	 *
	 * @param fileName String file name, as present in the LST file, with
	 *         or without INCLUDE and EXCLUDE information
	 * @return String containing only the pathname of the file
	 */
	private String getIncludesExcludes(String fileName)
	{
		// Clear any previous include/exclude requests
		includeItems.clear();
		excludeItems.clear();

		// Check if include/exclude items were present
		int pipePos = fileName.indexOf("|");

		if (pipePos > 0)
		{
			// Get the include/exclude item string
			String inExString = fileName.substring(pipePos + 1);

			// Check for surrounding parens
			while (inExString.startsWith("("))
			{
				// assume matching parens
				inExString = inExString.substring(1, inExString.length() - 1);
			}

			// Update the include or exclude items list, as appropriate
			if (inExString.startsWith("INCLUDE:"))
			{
				includeItems.addAll(CoreUtility.split(inExString.substring(8),
					'|'));
			}
			else
			{
				if (inExString.startsWith("EXCLUDE:"))
				{
					excludeItems.addAll(CoreUtility.split(inExString
						.substring(8), '|'));
				}
			}

			// Return the filename, without the include/exclude information
			// so that a FileNotFoundException will not be thrown
			return fileName.substring(0, pipePos);
		}
		// No extra info present; return the original file name
		return fileName;
	}
}
