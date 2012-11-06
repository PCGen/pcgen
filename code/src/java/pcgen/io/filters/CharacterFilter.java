package pcgen.io.filters;

import pcgen.cdom.base.Constants;
import pcgen.core.utils.CoreUtility;
import pcgen.util.Delta;
import pcgen.util.Logging;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pcgen.system.ConfigurationSettings;

public class CharacterFilter implements OutputFilter
{
	private String outputFilterName = "";
	private Map<Integer, String> outputFilter = null;

	public CharacterFilter(String filterName)
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

		outputFilter = null;

		filterName =
					new File(ConfigurationSettings.getSystemsDir())
					+ File.separator + "outputFilters" + File.separator
					+ filterName + Constants.EXTENSION_LIST_FILE;

		final File filterFile = new File(filterName);

		try
		{
			if (filterFile.canRead() && filterFile.isFile())
			{
				final BufferedReader br =
						new BufferedReader(new InputStreamReader(
							new FileInputStream(filterFile), "UTF-8"));

					outputFilterName = filterName;
					outputFilter = new HashMap<Integer, String>();

					for (;;)
					{
						final String aLine = br.readLine();

						if (aLine == null)
						{
							break;
						}

						final List<String> filterEntry =
								CoreUtility.split(aLine, '\t');

						if (filterEntry.size() >= 2)
						{
							try
							{
								final Integer key =
										Delta.decode(filterEntry.get(0));
								outputFilter.put(key, filterEntry.get(1));
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
					}

					br.close();
			}
		}
		catch (IOException e)
		{
			//Should this be ignored?
		}
	}

    @Override
	public String filterString(String aString)
	{
		if ((outputFilter != null) && (!outputFilter.isEmpty())
			&& aString != null)
		{
			final StringBuffer xlatedString =
					new StringBuffer(aString.length());

			for (int i = 0; i < aString.length(); i++)
			{
				final char c = aString.charAt(i);
				final String xlation = outputFilter.get((int) c);

				if (xlation != null)
				{
					xlatedString.append(xlation);
				}
				else
				{
					xlatedString.append(c);
				}
			}

			aString = xlatedString.toString();
		}
		return aString;
	}

}
