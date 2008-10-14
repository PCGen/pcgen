/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class UdamLst implements CDOMPrimaryToken<CDOMObject>
{

	public String getTokenName()
	{
		return "UDAM";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (Constants.LST_DOT_CLEAR.equals(value))
		{
			/*
			 * TODO Need a hack for PCClass to clear all levels :(
			 */
			context.getObjectContext().removeList(obj, ListKey.UNARMED_DAMAGE);
		}
		else
		{
			final StringTokenizer tok = new StringTokenizer(value,
					Constants.COMMA);
			if (tok.countTokens() != 9)
			{
				Logging.errorPrint(getTokenName()
						+ " requires 9 comma separated values");
				return false;
			}
			if (context.getObjectContext().containsListFor(obj,
					ListKey.UNARMED_DAMAGE))
			{
				Logging.errorPrint(obj.getDisplayName() + " already has "
						+ getTokenName() + " set.");
				Logging.errorPrint(" It will be redefined, "
						+ "but you should be using " + getTokenName()
						+ ":.CLEAR");
				context.getObjectContext().removeList(obj,
						ListKey.UNARMED_DAMAGE);
			}
			while (tok.hasMoreTokens())
			{
				context.getObjectContext().addToList(obj,
						ListKey.UNARMED_DAMAGE, tok.nextToken());
			}
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<String> changes = context.getObjectContext().getListChanges(
				obj, ListKey.UNARMED_DAMAGE);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		List<String> returnList = new ArrayList<String>(2);
		if (changes.includesGlobalClear())
		{
			returnList.add(Constants.LST_DOT_CLEAR);
		}
		Collection<String> list = changes.getAdded();
		if (list.size() == 9)
		{
			returnList.add(StringUtil.join(list, Constants.COMMA));
		}
		if (returnList.isEmpty())
		{
			// TODO Error
			return null;
		}
		return returnList.toArray(new String[returnList.size()]);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
