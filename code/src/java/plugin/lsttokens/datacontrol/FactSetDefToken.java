/*
 * Copyright 2014 (C) Thomas Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens.datacontrol;

import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.content.factset.FactSetDefinition;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.StringPClassUtil;

public class FactSetDefToken extends
		AbstractTokenWithSeparator<FactSetDefinition> implements
		CDOMPrimaryToken<FactSetDefinition>
{

	@Override
	public String getTokenName()
	{
		return "FACTSETDEF";
	}

	@Override
	protected char separator()
	{
		return '|';
	}

	@Override
	protected ParseResult parseTokenWithSeparator(LoadContext context,
		FactSetDefinition def, String value)
	{
		StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE);
		String fileType = aTok.nextToken();
		if (!aTok.hasMoreTokens())
		{
			return new ParseResult.Fail(getTokenName()
				+ " expects 2 PIPE separated values, found 1 in: " + value,
				context);
		}
		String identifier = aTok.nextToken();
		if (aTok.hasMoreTokens())
		{
			return new ParseResult.Fail(getTokenName()
				+ " expects 3 PIPE separated values, found too many in: "
				+ value, context);
		}
		Class<? extends Loadable> cl;
		if ("GLOBAL".equals(fileType))
		{
			cl = CDOMObject.class;
		}
		else
		{
			cl = StringPClassUtil.getClassFor(fileType);
			if (cl == null)
			{
				throw new IllegalArgumentException(
					"Invalid Data Definition Location (no class): " + fileType);
			}
		}
		def.setUsableLocation(cl);
		def.setFactSetName(identifier);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, FactSetDefinition def)
	{
		Class cl = def.getUsableLocation();
		String name = def.getFactSetName();
		if (cl == null)
		{
			if (name == null)
			{
				return null;
			}
			else
			{
				context
					.addWriteMessage("Found FactDefinition with location but no name");
				return null;
			}
		}
		else if (name == null)
		{
			context
				.addWriteMessage("Found FactDefinition with name but no location");
			return null;
		}
		return new String[]{StringPClassUtil.getStringFor(cl) + Constants.PIPE
			+ name};
	}

	@Override
	public Class<FactSetDefinition> getTokenClass()
	{
		return FactSetDefinition.class;
	}
}
