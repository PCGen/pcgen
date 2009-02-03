/*
 * Copyright 2008 (C) Thomas Parker <thpr@users.sourceforge.net>
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
				Logging.log(Logging.LST_ERROR, getTokenName()
						+ " requires 9 comma separated values");
				return false;
			}
			if (context.getObjectContext().containsListFor(obj,
					ListKey.UNARMED_DAMAGE))
			{
				Logging.log(Logging.LST_ERROR, obj.getDisplayName() + " already has "
						+ getTokenName() + " set.");
				Logging.log(Logging.LST_ERROR, " It will be redefined, "
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
		if (list != null && list.size() == 9)
		{
			returnList.add(StringUtil.join(list, Constants.COMMA));
		}
		if (returnList.isEmpty())
		{
			context.addWriteMessage(getTokenName() + " requires 9 values");
			return null;
		}
		return returnList.toArray(new String[returnList.size()]);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
