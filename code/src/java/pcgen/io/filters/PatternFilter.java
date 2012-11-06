/**
 * 
 */
package pcgen.io.filters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

import pcgen.cdom.base.Constants;
import pcgen.core.utils.CoreUtility;
import pcgen.system.ConfigurationSettings;
import pcgen.util.Logging;

/**
 * @author apsen
 *
 */
public class PatternFilter implements OutputFilter
{
	private String outputFilterName = "";
	private List<String> match = null;
	private List<String> replace = null;

	/**
	 * 
	 */
	public PatternFilter(String filterName) throws IOException
	{
		super();

		final int idx = filterName.lastIndexOf('.');

		if (idx >= 0)
		{
			filterName = filterName.substring(idx + 1);
		}

		filterName = filterName.toLowerCase();

		if (filterName.equals(outputFilterName))
		{
			return;
		}

		filterName =
					new File(ConfigurationSettings.getSystemsDir())
					+ File.separator + "outputFilters" + File.separator + "re"
					+ filterName + Constants.EXTENSION_LIST_FILE;
		Logging.debugPrint("Creating filter from " + filterName);

		final File filterFile = new File(filterName);

		if (filterFile.canRead() && filterFile.isFile())
		{
			final BufferedReader br =
					new BufferedReader(new InputStreamReader(
						new FileInputStream(filterFile), "UTF-8"));

				outputFilterName = filterName;
				match = new ArrayList<String>();
				replace = new ArrayList<String>();

				for (;;)
				{
					final String aLine = br.readLine();
					Logging.debugPrint("Line read:" + aLine);

					if (aLine == null)
					{
						break;
					}

					String aLineWOComment;
					if (aLine.length() == 0 || aLine.charAt(0) == '#') {
                                        continue;
                                    }
					else if (0 < aLine.indexOf("\t#")) {
                                        aLineWOComment =
                                                        aLine.substring(0, aLine.indexOf("\t#"));
                                    }
					else {
                                        aLineWOComment = aLine;
                                    }

					Logging.debugPrint("Stripped line:" + aLineWOComment);
					final List<String> filterEntry =
							CoreUtility.split(aLineWOComment, '\t');

					try
					{
						if (filterEntry.size() == 2)
						{
							match.add(filterEntry.get(0));

							Logging.debugPrint("Match: [" + filterEntry.get(0)
								+ "] and replace with [" + filterEntry.get(1)
								+ "]");
							replace.add(filterEntry.get(1).replaceAll("\\\\n",
								"\n").replaceAll("\\\\t", "\t"));
						}
						else if (filterEntry.size() == 1)
						{
							match.add(filterEntry.get(0));
							replace.add("");
							Logging.debugPrint("Match: [" + filterEntry.get(0)
								+ "] and replace with []");
						}
						else
						{
							Logging
								.errorPrint("Incorrect line format in PatternFilter: Line ignored");
						}
					}
					catch (NullPointerException e)
					{
						Logging.errorPrint(
							"Exception in setCurrentOutputFilter", e);
					}
					catch (NumberFormatException e)
					{
						Logging.errorPrint(
							"Exception in setCurrentOutputFilter", e);
					}
				}

				br.close();
		}
	}

    @Override
	public String filterString(String aString)
	{
		String aProcessedString = aString;
		Logging.debugPrint("Filtering: " + aString);
		if ((match != null) && (!match.isEmpty()) && aString != null)
		{
			Logging.debugPrint("Found " + match.size() + " filters");
			for (int i = 0; i < match.size(); i++)
			{
				String aPreprocessedString = aProcessedString;
				aProcessedString =
						aProcessedString.replaceAll(match.get(i), replace
							.get(i));
				if (!aProcessedString.equals(aPreprocessedString))
				{
					Logging.debugPrint("Match: [" + match.get(i)
						+ "] and replace with [" + replace.get(i) + "]");
					Logging.debugPrint("[" + aPreprocessedString + "]=>["
						+ aProcessedString + "]");
				}
			}
		}

		Logging.debugPrint("Filtered: " + aProcessedString);
		return aProcessedString;
	}

}
