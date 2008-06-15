package pcgen.io;

import java.util.List;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PCTemplate;
import pcgen.util.Logging;

public class Compatibility
{

	public static PCTemplate getTemplateFor(PCTemplate template,
			String templateKey, String feat)
	{
		if (templateKey.charAt(0) == 'L')
		{
			int level = Integer.parseInt(templateKey.substring(1));
			List<PCTemplate> levelTemplates = template
					.getListFor(ListKey.LEVEL_TEMPLATES);
			for (PCTemplate templ : levelTemplates)
			{
				if (level == templ.get(IntegerKey.LEVEL))
				{
					return templ;
				}
			}
		}
		else
		{
			// Assume 'H'
			int minhd;
			int maxhd;
			String hdString = templateKey.substring(1);
			int minusLoc = hdString.indexOf('-');
			if (minusLoc == -1)
			{
				if (hdString.indexOf('+') == hdString.length() - 1)
				{
					minhd = Integer.parseInt(hdString.substring(0, hdString
							.length() - 1));
					maxhd = Integer.MAX_VALUE;
				}
				else
				{
					minhd = Integer.parseInt(hdString);
					maxhd = minhd;
				}
			}
			else
			{
				minhd = Integer.parseInt(hdString.substring(0, minusLoc));
				maxhd = Integer.parseInt(hdString.substring(minusLoc + 1));
			}
			List<PCTemplate> levelTemplates = template
					.getListFor(ListKey.HD_TEMPLATES);
			for (PCTemplate templ : levelTemplates)
			{
				if (minhd == templ.get(IntegerKey.HD_MIN)
						&& maxhd == templ.get(IntegerKey.HD_MAX))
				{
					return templ;
				}
			}
		}
		Logging
				.errorPrint("Unable to find appropriate Template for "
						+ templateKey + ":" + feat + " in "
						+ template.getDisplayName());
		return null;
	}

	public static String getKeyFor(PCTemplate pct)
	{
		Integer level = pct.get(IntegerKey.LEVEL);
		StringBuilder hd = new StringBuilder();
		if (level == null)
		{
			hd.append('H');
			Integer min = pct.get(IntegerKey.HD_MIN);
			Integer max = pct.get(IntegerKey.HD_MAX);
			hd.append(min);
			if (max == Integer.MAX_VALUE)
			{
				hd.append('+');
			}
			else if (max != min)
			{
				hd.append('-').append(max);
			}
		}
		else
		{
			hd.append('L');
			hd.append(level);
		}
		return hd.toString();
	}

}
