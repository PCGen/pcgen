/*
 * Copyright 2006 (C) Andriy Sen
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
package pcgen.io.filters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.Constants;
import pcgen.core.utils.CoreUtility;
import pcgen.system.ConfigurationSettings;
import pcgen.util.Logging;

/**
 * An output filter that will convert patterns in the output. This is used for 
 * converting general formatting and reserved characters into a format 
 * suitable for a particular file type. e.g. Converting special characters 
 * into safe XML equivalents for outputting to xml files.
 *    
 */
public class PatternFilter implements OutputFilter
{
	private String outputFilterName = "";
	private List<String> match = null;
	private List<String> replace = null;

	/**
	 * Create a new PatternFilter instance suitable for processing output to 
	 * files produced using the supplied template.
	 *  
	 * @param templateFileName The file name of the output template file. 
	 * @throws IOException If the pattern filter cannot be read.
	 */
	public PatternFilter(String templateFileName) throws IOException
	{

		int idx = templateFileName.lastIndexOf('.');
		if (idx < 0)
		{
			idx = templateFileName.lastIndexOf('-');
		}

		String filterName = templateFileName;
		if (idx >= 0)
		{
			filterName = filterName.substring(idx + 1);
		}

		filterName = filterName.toLowerCase();

		if (filterName.equals(outputFilterName))
		{
			return;
		}

		filterName = new File(ConfigurationSettings.getSystemsDir()) + File.separator + "outputFilters" + File.separator
			+ "re" + filterName + Constants.EXTENSION_LIST_FILE;
		//		Logging.debugPrint("Creating filter from " + filterName);

		final File filterFile = new File(filterName);

		if (filterFile.canRead() && filterFile.isFile())
		{
			try (BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(filterFile),
					StandardCharsets.UTF_8
			)))
			{

				outputFilterName = filterName;
				match = new ArrayList<>();
				replace = new ArrayList<>();

				while (true)
				{
					final String aLine = br.readLine();
					//					Logging.debugPrint("Line read:" + aLine);

					if (aLine == null)
					{
						break;
					}

					String aLineWOComment;
					if (aLine.isEmpty() || aLine.charAt(0) == '#')
					{
						continue;
					}
					else if (aLine.indexOf("\t#") > 0)
					{
						aLineWOComment = aLine.substring(0, aLine.indexOf("\t#"));
					}
					else
					{
						aLineWOComment = aLine;
					}

					//					Logging.debugPrint("Stripped line:" + aLineWOComment);
					final List<String> filterEntry = CoreUtility.split(aLineWOComment, '\t');

					try
					{
						if (filterEntry.size() == 2)
						{
							match.add(filterEntry.get(0));

							//							Logging.debugPrint("Match: [" + filterEntry.get(0)
							//								+ "] and replace with [" + filterEntry.get(1)
							//								+ "]");
							replace.add(filterEntry.get(1).replaceAll("\\\\n", "\n").replaceAll("\\\\t", "\t"));
						}
						else if (filterEntry.size() == 1)
						{
							match.add(filterEntry.get(0));
							replace.add("");
							//						Logging.debugPrint("Match: [" + filterEntry.get(0)
							//							+ "] and replace with []");
						}
						else
						{
							Logging.errorPrint("Incorrect line format in PatternFilter: Line " + "ignored");
						}
					} catch (NullPointerException | NumberFormatException e)
					{
						Logging.errorPrint("Exception in setCurrentOutputFilter", e);
					}
				}

			}
		}
	}

	@Override
	public String filterString(String aString)
	{
		String aProcessedString = aString;
		//Logging.debugPrint("Filtering: " + aString);
		if ((match != null) && (!match.isEmpty()) && aString != null)
		{
			//Logging.debugPrint("Found " + match.size() + " filters");
			for (int i = 0; i < match.size(); i++)
			{
				//				String aPreprocessedString = aProcessedString;
				aProcessedString = aProcessedString.replaceAll(match.get(i), replace.get(i));
				//				if (!aProcessedString.equals(aPreprocessedString))
				//				{
				//					Logging.debugPrint("Match: [" + match.get(i)
				//						+ "] and replace with [" + replace.get(i) + "]");
				//					Logging.debugPrint("[" + aPreprocessedString + "]=>["
				//						+ aProcessedString + "]");
				//				}
			}
		}

		//		Logging.debugPrint("Filtered: " + aProcessedString);
		return aProcessedString;
	}

}
