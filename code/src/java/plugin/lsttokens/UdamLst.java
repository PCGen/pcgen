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
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.PCClass;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ParseResult;

/**
 * @author djones4
 *
 */
public class UdamLst implements CDOMPrimaryToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "UDAM";
	}

	@Override
	public ParseResult parseToken(LoadContext context, CDOMObject obj,
		String value)
	{
		ParseResult pr = ParseResult.SUCCESS;
		if (Constants.LST_DOT_CLEAR.equals(value))
		{
			/*
			 * TODO This cross-polluting and certainly not "editor friendly",
			 * thus will need to be changed after 5.16
			 */
			if (obj instanceof PCClassLevel || obj instanceof PCClass)
			{
				PCClass pcc;
				if (obj instanceof PCClassLevel)
				{
					pcc = (PCClass) obj.get(ObjectKey.TOKEN_PARENT);
				}
				else
				{
					pcc = (PCClass) obj;
				}
				context.getObjectContext().removeList(pcc,
						ListKey.UNARMED_DAMAGE);
				for (PCClassLevel level : pcc.getOriginalClassLevelCollection())
				{
					context.getObjectContext().removeList(level,
							ListKey.UNARMED_DAMAGE);
				}
			}
			else
			{
				context.getObjectContext().removeList(obj,
						ListKey.UNARMED_DAMAGE);
			}
		}
		else
		{
			final StringTokenizer tok = new StringTokenizer(value,
					Constants.COMMA);
			if (tok.countTokens() != 9 && tok.countTokens() != 1)
			{
				return new ParseResult.Fail(getTokenName()
						+ " requires either a single value or 9 comma separated values", context);
			}
			if (context.getObjectContext().containsListFor(obj,
					ListKey.UNARMED_DAMAGE))
			{
				ComplexParseResult cpr = new ComplexParseResult();
				cpr.addWarningMessage(obj.getDisplayName()
						+ " already has " + getTokenName() + " set.");
				cpr.addWarningMessage(" It will be redefined, "
						+ "but you should be using " + getTokenName()
						+ ":.CLEAR");
				pr = cpr;
				context.getObjectContext().removeList(obj,
						ListKey.UNARMED_DAMAGE);
			}
			while (tok.hasMoreTokens())
			{
				context.getObjectContext().addToList(obj,
						ListKey.UNARMED_DAMAGE, tok.nextToken());
			}
		}
		return pr;
	}

	@Override
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
		if (list != null && (list.size() == 9 || list.size() == 1))
		{
			returnList.add(StringUtil.join(list, Constants.COMMA));
		}
		if (returnList.isEmpty())
		{
			context.addWriteMessage(getTokenName() + " requires either 1 value or 9 values");
			return null;
		}
		return returnList.toArray(new String[returnList.size()]);
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
