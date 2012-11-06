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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import pcgen.base.lang.StringUtil;
import pcgen.base.lang.UnreachableError;
import pcgen.base.util.HashMapToList;
import pcgen.base.util.MapToList;
import pcgen.cdom.base.Constants;
import pcgen.core.Campaign;
import pcgen.core.utils.CoreUtility;
import pcgen.system.ConfigurationSettings;
import pcgen.util.Logging;

/**
 * This class is used to match a source file to the campaign that
 * loaded it.
 */
public class CampaignSourceEntry implements SourceEntry
{
	public static final URI FAILED_URI;

	static
	{
		try
		{
			FAILED_URI = new URI("file:/FAIL");
		}
		catch (URISyntaxException e)
		{
			throw new UnreachableError(e);
		}
	}
	private Campaign campaign = null;
	private List<String> excludeItems = new ArrayList<String>();
	private List<String> includeItems = new ArrayList<String>();
	private URI uri = null;
	private URI writeURI = null;
	private URIFactory uriFac = null;
	private String stringForm = null;

	/**
	 * CampaignSourceEntry constructor.
	 *
	 * @param campaign Campaign that referenced the provided file.
	 *         Must not be null.
	 * @param lstLoc URL path to an LST source file
	 *         Must not be null.
	 */
	public CampaignSourceEntry(Campaign campaign, URI lstLoc)
	{
		super();
		if (campaign == null)
		{
			throw new IllegalArgumentException("campaign can't be null");
		}
		if (lstLoc == null)
		{
			throw new IllegalArgumentException("lstLoc can't be null");
		}
		this.campaign = campaign;
		this.uri = lstLoc;
	}

	public CampaignSourceEntry(Campaign campaign, URIFactory fac)
	{
		super();
		if (campaign == null)
		{
			throw new IllegalArgumentException("campaign can't be null");
		}
		if (fac == null)
		{
			throw new IllegalArgumentException("URI Factory can't be null");
		}
		this.campaign = campaign;
		this.uriFac = fac;
	}

	public static class URIFactory
	{
		private final URI u;
		private final String s;

		public URIFactory(URI source, String value)
		{
			if (source == null)
			{
				throw new IllegalArgumentException("URI cannot be null");
			}
			if (value == null || value.length() == 0)
			{
				throw new IllegalArgumentException("URI cannot be null");
			}
			u = source;
			s = value;
		}

		public URI getURI()
		{
			return getPathURI(u, s);
		}
		
        @Override
		public int hashCode()
		{
			return s.hashCode();
		}
		
        @Override
		public boolean equals(Object o)
		{
			if (o instanceof URIFactory)
			{
				URIFactory other = (URIFactory) o;
				return s.equals(other.s) && u.equals(other.u);
			}
			return false;
		}
	}

	/**
	 * This method gets the Campaign that was the source of the
	 * file. (I.e. the reason it was loaded)
	 * @return Campaign that requested the file be loaded
	 */
    @Override
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
    @Override
	public List<String> getExcludeItems()
	{
		return excludeItems;
	}

	/**
	 * This method gets the file/path of the LST file.
	 * @return String url-formatted path to the LST file
	 */
    @Override
	public URI getURI()
	{
		if (uri == null)
		{
			uri = uriFac.getURI();
		}
		return uri;
	}

	/**
	 * This method gets a list of the items containined in the given source
	 * file to include in getting saved in memory.  All other objects
	 * in the file are to be excluded.
	 * @return List of String names of objects to include
	 */
    @Override
	public List<String> getIncludeItems()
	{
		return includeItems;
	}

	/**
	 * @param arg0 
	 * @return true if equals
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object arg0)
	{
		if (arg0 == this)
		{
			return true;
		}
		if (!(arg0 instanceof CampaignSourceEntry))
		{
			return false;
		}
		CampaignSourceEntry other = (CampaignSourceEntry) arg0;
		if (this.uriFac == null)
		{
			if (other.uriFac != null || !getURI().equals(other.getURI()))
			{
				return false;
			}
		}
		else
		{
			if (!uriFac.equals(other.uriFac))
			{
				return false;
			}
		}
		return excludeItems.equals(other.excludeItems)
			&& includeItems.equals(other.includeItems);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return this.getURIIdentifier().hashCode();
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
			sBuff.append(getURI());

			stringForm = sBuff.toString();
		}

		return stringForm;
	}

	/**
	 * This method converts the provided filePath to either a URL
	 * or absolute path as appropriate.
	 *
	 * @param pccPath  URL where the Campaign that contained the source was at
	 * @param basePath String path that is to be converted
	 * @return String containing the converted absolute path or URL
	 *         (as appropriate)
	 */
	private static URI getPathURI(URI pccPath, String basePath)
	{
		if (basePath.length() <= 0)
		{
			Logging.errorPrint("Empty Path to LST file in " + pccPath);
			return FAILED_URI;
		}

		/*
		 * Figure out where the PCC file came from that we're processing, so
		 * that we can prepend its path onto any LST file references (or PCC
		 * refs, for that matter) that are relative. If the source line in
		 * question already has path info, then don't bother
		 */
		if (basePath.charAt(0) == '@')
		{
			String pathNoLeader =
					trimLeadingFileSeparator(basePath.substring(1));
			String path = CoreUtility.fixFilenamePath(pathNoLeader);
			return new File(ConfigurationSettings.getPccFilesDir(), path)
				.toURI();
		}
		else if (basePath.charAt(0) == '&')
		{
			String pathNoLeader =
					trimLeadingFileSeparator(basePath.substring(1));
			String path = CoreUtility.fixFilenamePath(pathNoLeader);
			return new File(ConfigurationSettings.getVendorDataDir(), path)
				.toURI();
		}
		else if (basePath.charAt(0) == '*')
		{
			String pathNoLeader =
					trimLeadingFileSeparator(basePath.substring(1));
			String path = CoreUtility.fixFilenamePath(pathNoLeader);
			File pccFile =
					new File(ConfigurationSettings.getVendorDataDir(), path);
			if (pccFile.exists())
			{
				return pccFile.toURI();
			}
			return new File(ConfigurationSettings.getPccFilesDir(), path)
				.toURI();
		}
		/*
		 * If the line doesn't use "@" or "&" then it's a relative path
		 * 
		 * 1) If the path starts with '/data', assume it means the PCGen
		 * data dir 2) Otherwise, assume that the path is relative to the
		 * current PCC file URL
		 */
		String pathNoLeader = trimLeadingFileSeparator(basePath);

		if (pathNoLeader.startsWith("data"))
		{
			// substring 5 to eliminate the separator after data
			String path =
					CoreUtility.fixFilenamePath(pathNoLeader.substring(5));
			return new File(ConfigurationSettings.getPccFilesDir(), path)
				.toURI();
		}
		else
		{
			if (basePath.indexOf(':') > 0)
			{
				try
				{
					// if it's a URL, then we are all done, just return a URI
					URL url = new URL(basePath);
					return new URI(url.getProtocol(), url.getHost(), url
						.getPath(), null);
				}
				catch (URISyntaxException e)
				{
					//Something broke, so wasn't a URL
				}
				catch (MalformedURLException e)
				{
					//Protocol was unknown, so wasn't a URL
				}
			}

			String path = pccPath.getPath();
			// URLs always use forward slash; take off the file name
			try
			{
				return new URI(pccPath.getScheme(), null, (path.substring(0,
					path.lastIndexOf('/') + 1) + basePath.replace('\\', '/')),
					null);
			}
			catch (URISyntaxException e)
			{
				Logging.errorPrint("GPURI failed to convert "
					+ path.substring(0, path.lastIndexOf('/') + 1) + basePath
					+ " to a URI: " + e.getLocalizedMessage());
			}
		}
		return FAILED_URI;
	}

	/**
	 * This method trims the leading file separator or URL separator from the
	 * front of a string.
	 *
	 * @param basePath String containing the base path to trim
	 * @return String containing the trimmed path String
	 */
	private static String trimLeadingFileSeparator(String basePath)
	{
		String pathNoLeader = basePath;

		if (pathNoLeader.startsWith("/")
			|| pathNoLeader.startsWith(File.separator))
		{
			pathNoLeader = pathNoLeader.substring(1);
		}

		return pathNoLeader;
	}

	public static CampaignSourceEntry getNewCSE(Campaign campaign2,
		URI sourceUri, String value)
	{
		if (value == null || value.length() == 0)
		{
			Logging
				.errorPrint("Cannot build CampaignSourceEntry for empty value in "
					+ sourceUri);
			return null;
		}
		
		// Check if include/exclude items were present
		int pipePos = value.indexOf("|");

		CampaignSourceEntry cse;

		if (pipePos == -1)
		{
			if (value.startsWith("("))
			{
				Logging.errorPrint("Invalid Campaign File, cannot start with (:" + value);
				return null;
			}
			cse =
					new CampaignSourceEntry(campaign2, new URIFactory(
						sourceUri, value));
		}
		else
		{
			cse =
					new CampaignSourceEntry(campaign2, new URIFactory(
						sourceUri, value.substring(0, pipePos)));

			// Get the include/exclude item string
			String inExString = value.substring(pipePos + 1);
			
			if (inExString.startsWith("("))
			{
				// assume matching parens
				inExString = inExString.substring(1, inExString.length() - 1);
			}
			else
			{
				Logging
						.errorPrint("Found Suffix in Campaign Source without parenthesis: "
								+ "Parens required around INCLUDE/EXCLUDE");
				Logging.errorPrint("Found: '" + inExString + "' in " + value);
				return null;
			}

			// Check for surrounding parens
			while (inExString.startsWith("("))
			{
				Logging
						.errorPrint("Found Suffix in Campaign Source with multiple parenthesis: "
								+ "Single set of parens required around INCLUDE/EXCLUDE");
				Logging.errorPrint("Found: '" + value.substring(pipePos + 1)
						+ "' in " + value);
				return null;
			}

			// Update the include or exclude items list, as appropriate
			if (inExString.startsWith("INCLUDE:"))
			{
				List<String> splitIncExc = cse.splitInExString(inExString);
				if (splitIncExc == null)
				{
					//Error
					return null;
				}
				cse.includeItems = splitIncExc;
			}
			else if (inExString.startsWith("EXCLUDE:"))
			{
				List<String> splitIncExc = cse.splitInExString(inExString);
				if (splitIncExc == null)
				{
					//Error
					return null;
				}
				cse.excludeItems = splitIncExc;
			}
			else
			{
				Logging.errorPrint("Invalid Suffix: " + inExString
					+ " on Campaign Source: '" + value + "' in " + sourceUri);
				return null;
			}
		}
		return cse;
	}

	public URI getWriteURI()
	{
		return writeURI;
	}

	public void setWriteURI(URI writeURI)
	{
		this.writeURI = writeURI;
	}

	/**
	 * Split an include or exclude string accounting for the possible presence 
	 * of a leading category.
	 * @param inExString The string to be split
	 * @return A list of keys, optionally with leading category keys
	 */
	private List<String> splitInExString(String inExString)
	{
		boolean hasCategory = false;
		boolean hasKeyOnly = false;
		List<String> catKeyList = new ArrayList<String>();
		String target = inExString.substring(8);
		if (target == null || target.length() == 0)
		{
			Logging.errorPrint("Must Specify Items after :");
			return null;
		}
		List<String> keyList = CoreUtility.split(target, '|');
		for (String key : keyList)
		{
			if (key.startsWith("CATEGORY="))
			{
				hasCategory = true;
				List<String> abilityKeyList =
						CoreUtility.split(key.substring(9), ',');
				String category = abilityKeyList.get(0);
				abilityKeyList.remove(0);
				for (String string : abilityKeyList)
				{
					catKeyList.add(category + ',' + string);
				}
			}
			else
			{
				hasKeyOnly = true;
				catKeyList.add(key);
			}
		}
		if (hasKeyOnly && hasCategory)
		{
			Logging.log(Logging.LST_ERROR, "Invalid "
				+ inExString.substring(0, 7) + " value on " + getURIIdentifier() + " in "
				+ campaign.getDisplayName()
				+ ". Abilities must always have categories (e.g. "
				+ inExString.substring(0, 8)
				+ "CATEGORY=cat1,key1,key2|CATEGORY=cat2,key1 ) and "
				+ "other file types should never have categories (e.g. "
				+ inExString.substring(0, 8) + "key1|key2 ).");
			return null;
		}

		return catKeyList;
	}

	private String getURIIdentifier()
	{
		if (uriFac == null)
		{
			return uri.toString();
		}
		else
		{
			return uriFac.s;
		}
	}

	public String getLSTformat()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getURIIdentifier());
		if (!includeItems.isEmpty())
		{
			sb.append(Constants.PIPE);
			sb.append("(INCLUDE:");
			sb.append(joinIncExcList(includeItems));
			sb.append(')');
		}
		else if (!excludeItems.isEmpty())
		{
			sb.append(Constants.PIPE);
			sb.append("(EXCLUDE:");
			sb.append(joinIncExcList(excludeItems));
			sb.append(')');
		}
		return sb.toString();
	}

	private StringBuilder joinIncExcList(List<String> list)
	{
		MapToList<String, String> map = new HashMapToList<String, String>();
		for (String s : list)
		{
			int commaLoc = s.indexOf(',');
			if (commaLoc == -1)
			{
				return StringUtil.joinToStringBuffer(list, Constants.PIPE);
			}
			else
			{
				map.addToListFor(s.substring(0, commaLoc), s
					.substring(commaLoc + 1));
			}
		}
		StringBuilder sb = new StringBuilder();
		boolean needPipe = false;
		for (String category : map.getKeySet())
		{
			if (needPipe)
			{
				sb.append(Constants.PIPE);
			}
			needPipe = true;
			sb.append("CATEGORY=");
			sb.append(category);
			sb.append(Constants.COMMA);
			sb.append(StringUtil.joinToStringBuffer(map.getListFor(category),
				Constants.COMMA));
		}
		return sb;
	}

	public CampaignSourceEntry getRelatedTarget(String fileName)
	{
		if (uriFac == null)
		{
			throw new IllegalStateException(
					"getRelatedTarget can only be called on a CampaignSourceEntry that uses a URI Factory");
		}
		return new CampaignSourceEntry(campaign, new URIFactory(uriFac.u,
				fileName));
	}
}
