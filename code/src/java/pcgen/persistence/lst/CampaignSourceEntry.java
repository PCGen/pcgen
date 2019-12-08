/*
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
 *
 *
 */
package pcgen.persistence.lst;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.base.util.HashMapToList;
import pcgen.base.util.MapToList;
import pcgen.cdom.base.Constants;
import pcgen.core.Campaign;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.utils.CoreUtility;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

/**
 * This class is used to match a source file to the campaign that
 * loaded it.
 */
public class CampaignSourceEntry implements SourceEntry
{
	private Campaign campaign = null;
	private List<String> excludeItems = new ArrayList<>();
	private List<String> includeItems = new ArrayList<>();
	private List<Prerequisite> prerequisites = new ArrayList<>();
	private URIEntry uri = null;

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
		this.campaign = Objects.requireNonNull(campaign);
		this.uri = new URIEntry(campaign.getDisplayName(), Objects.requireNonNull(lstLoc));
	}

	public CampaignSourceEntry(Campaign campaign, URIEntry entry)
	{
		super();
		this.campaign = Objects.requireNonNull(campaign);
		this.uri = Objects.requireNonNull(entry);
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
		return uri.getURI();
	}

	/**
	 * This method gets a list of the items contained in the given source
	 * file to include in getting saved in memory.  All other objects
	 * in the file are to be excluded.
	 * @return List of String names of objects to include
	 */
	@Override
	public List<String> getIncludeItems()
	{
		return includeItems;
	}

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
		return uri.equals(other.uri) && excludeItems.equals(other.excludeItems)
			&& includeItems.equals(other.includeItems);
	}

	@Override
	public int hashCode()
	{
		return this.uri.getLSTformat().hashCode();
	}

	@Override
	public String toString()
	{
		String sBuff = "Campaign: "
				+ campaign.getDisplayName()
				+ "; SourceFile: "
				+ getURI();
		return sBuff;
	}

	public static CampaignSourceEntry getNewCSE(Campaign campaign2, URI sourceUri, String value)
	{
		if (value == null || value.isEmpty())
		{
			Logging.errorPrint("Cannot build CampaignSourceEntry for empty value in " + sourceUri);
			return null;
		}

		// Check if include/exclude items were present
		int pipePos = value.indexOf('|');

		CampaignSourceEntry cse;

		if (pipePos == -1)
		{
			if (value.startsWith("("))
			{
				Logging.errorPrint("Invalid Campaign File, cannot start with (:" + value);
				return null;
			}
			URIEntry uri = URIEntry.getURIEntry(campaign2.getDisplayName(), sourceUri, value);
			cse = new CampaignSourceEntry(campaign2, uri);
		}
		else
		{
			URIEntry uri = URIEntry.getURIEntry(campaign2.getDisplayName(), sourceUri, value.substring(0, pipePos));
			cse = new CampaignSourceEntry(campaign2, uri);

			// Get the include/exclude item string
			String inExString = value.substring(pipePos + 1);

			List<String> tagList = parseSuffix(inExString, sourceUri, value);
			for (String tagString : tagList)
			{
				// Check for surrounding parens
				if (tagString.startsWith("(("))
				{
					Logging.errorPrint("Found Suffix in Campaign Source with multiple parenthesis: "
						+ "Single set of parens required around INCLUDE/EXCLUDE");
					Logging.errorPrint("Found: '" + tagString + "' in " + value);
					return null;
				}

				// Update the include or exclude items list, as appropriate
				if (tagString.startsWith("(INCLUDE:"))
				{
					// assume matching parens
					tagString = inExString.substring(1, tagString.length() - 1);
					List<String> splitIncExc = cse.splitInExString(tagString);
					if (splitIncExc == null)
					{
						//Error
						return null;
					}
					cse.includeItems = splitIncExc;
				}
				else if (tagString.startsWith("(EXCLUDE:"))
				{
					// assume matching parens
					tagString = inExString.substring(1, tagString.length() - 1);
					List<String> splitIncExc = cse.splitInExString(tagString);
					if (splitIncExc == null)
					{
						//Error
						return null;
					}
					cse.excludeItems = splitIncExc;
				}
				else if (PreParserFactory.isPreReqString(tagString))
				{
					Prerequisite prereq;
					try
					{
						prereq = PreParserFactory.getInstance().parse(tagString);
					}
					catch (PersistenceLayerException e)
					{
						Logging.errorPrint("Error Initializing PreParserFactory.", e);
						return null;
					}
					if (prereq == null)
					{
						Logging.errorPrint(
							"Found invalid prerequisite in Campaign Source: '" + tagString + "' in " + value);
						return null;
					}
					cse.prerequisites.add(prereq);
				}
				else
				{
					Logging.errorPrint("Invalid Suffix (must have " + "'(INCLUDE' '(EXCLUDE' or a PRExxx immediately "
						+ "following the pipe (no spaces).  Found: '" + inExString + "' on Campaign Source: '" + value
						+ "' in " + sourceUri);
					return null;
				}
			}
			validatePrereqs(cse.getPrerequisites(), sourceUri);
		}
		return cse;
	}

	/**
	 * Convert a string occurring after the first | into a list of tokens. We 
	 * expect INCLUDE or EXCLUSE in brackets (as these can contain |) 
	 * and PREreqs.
	 * 
	 * @param suffix  The string to be parsed, should only be the suffix
	 * @param sourceUri The source we can use to report errors against.
	 * @param value The full value we can use to report errors against.
	 * @return A list of the discrete tags that were specified, null if there 
	 * was an error reported to the log. 
	 */
	static List<String> parseSuffix(String suffix, URI sourceUri, String value)
	{
		List<String> tagList = new ArrayList<>();
		StringBuilder currentTag = new StringBuilder();
		int bracketLevel = 0;

		StringTokenizer tokenizer = new StringTokenizer(suffix, "|()", true);
		while (tokenizer.hasMoreTokens())
		{
			String token = tokenizer.nextToken();
			switch (token)
			{
				case "(":
					currentTag.append(token);
					bracketLevel++;

					break;
				case ")":
					if (bracketLevel > 0)
					{
						bracketLevel--;
					}
					currentTag.append(token);
					break;
				case "|":
					if (bracketLevel > 0)
					{
						currentTag.append(token);
					} else if (currentTag.length() > 0)
					{
						tagList.add(currentTag.toString());
						currentTag = new StringBuilder();
					}
					break;
				default:
					currentTag.append(token);
					break;
			}
		}
		if (currentTag.length() > 0)
		{
			tagList.add(currentTag.toString());
		}

		// Check for a bracket mismatch
		if (bracketLevel > 0)
		{
			Logging.errorPrint("Suffix in Campaign Source with missing closing parenthesis, Found: '" + suffix
				+ "' on Campaign Source: '" + value + "' in " + sourceUri);
			return null;
		}
		return tagList;
	}

	/**
	 * Check that all prerequisites specified in the PCC file are 
	 * supported. Any unsupported prereqs will be reported as LST 
	 * errors. This is a recursive function allowing it to 
	 * check nested prereqs.
	 * 
	 * @param prereqList The prerequisites to be checked.
	 */
	private static void validatePrereqs(List<Prerequisite> prereqList, URI sourceUri)
	{
		if (prereqList == null || prereqList.isEmpty())
		{
			return;
		}

		for (Prerequisite prereq : prereqList)
		{
			if (prereq.isCharacterRequired())
			{
				final PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
				ArrayList<Prerequisite> displayList = new ArrayList<>();
				displayList.add(prereq);
				String lstString = prereqWriter.getPrerequisiteString(displayList, Constants.TAB);
				Logging.log(Logging.LST_ERROR,
					"Prereq '" + prereq.getKind() + "' is not supported in PCC files. Prereq was '" + lstString
						+ "' in " + sourceUri + ". Prereq will be ignored.");
			}
			else
			{
				validatePrereqs(prereq.getPrerequisites(), sourceUri);
			}
		}
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
		List<String> catKeyList = new ArrayList<>();
		String target = inExString.substring(8);
		if (target.isEmpty())
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
				List<String> abilityKeyList = CoreUtility.split(key.substring(9), ',');
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
			Logging.log(Logging.LST_ERROR, "Invalid " + inExString.substring(0, 7) + " value on " + uri.getLSTformat()
				+ " in " + campaign.getDisplayName() + ". Abilities must always have categories (e.g. "
				+ inExString.substring(0, 8) + "CATEGORY=cat1,key1,key2|CATEGORY=cat2,key1 ) and "
				+ "other file types should never have categories (e.g. " + inExString.substring(0, 8) + "key1|key2 ).");
			return null;
		}

		return catKeyList;
	}

	public String getLSTformat()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(uri.getLSTformat());
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

	private String joinIncExcList(List<String> list)
	{
		MapToList<String, String> map = new HashMapToList<>();
		for (String s : list)
		{
			int commaLoc = s.indexOf(',');
			if (commaLoc == -1)
			{
				return StringUtil.join(list, Constants.PIPE);
			}
			else
			{
				map.addToListFor(s.substring(0, commaLoc), s.substring(commaLoc + 1));
			}
		}
		StringBuilder sb = new StringBuilder(200);
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
			sb.append(StringUtil.join(map.getListFor(category), Constants.COMMA));
		}
		return sb.toString();
	}

	public CampaignSourceEntry getRelatedTarget(String fileName)
	{
		return new CampaignSourceEntry(campaign, uri.getRelatedTarget(fileName));
	}

	public List<Prerequisite> getPrerequisites()
	{
		return prerequisites;
	}
}
