/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.converter;

import java.util.Collection;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.inst.ObjectCache;
import pcgen.gui2.converter.ConversionDecider;
import pcgen.gui2.converter.TokenConverter;
import pcgen.gui2.converter.event.TokenProcessEvent;
import pcgen.gui2.converter.event.TokenProcessorPlugin;
import pcgen.rules.context.EditorLoadContext;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

public class BonusConvertPlugin implements TokenProcessorPlugin
{
	private static int bonusCount = 1;

	// Just process over these magical tokens for now
	@Override
	public String process(TokenProcessEvent tpe)
	{
		String value = tpe.getValue();
		StringBuilder result = new StringBuilder(value.length() + 10);
		String objectName = tpe.getObjectName();
		while (true)
		{
			int pipeLoc = value.lastIndexOf('|');
			String preString = value.substring(pipeLoc + 1);
			if (pipeLoc == -1 || (!preString.startsWith("PRE") && !preString.startsWith("!PRE"))
				|| preString.startsWith("PREAPPLY"))
			{
				break;
			}
			String pre = process(tpe.getContext(), tpe.getDecider(), objectName, preString);
			result.insert(0, pre);
			result.insert(0, '|');
			value = value.substring(0, pipeLoc);
		}

		return processBonus(tpe, tpe.getKey(), value + result);
	}

	private String process(EditorLoadContext context, ConversionDecider decider, String objectName, String token)
	{
		final int colonLoc = token.indexOf(':');
		if (colonLoc == -1)
		{
			Logging.errorPrint("Invalid Token - does not contain a colon: " + token);
			return null;
		}
		else if (colonLoc == 0)
		{
			Logging.errorPrint("Invalid Token - starts with a colon: " + token);
			return null;
		}

		String key = token.substring(0, colonLoc);
		String value = (colonLoc == token.length() - 1) ? null : token.substring(colonLoc + 1);

		CDOMObject cdo = new ObjectCache();
		cdo.setName("BONUS" + bonusCount++);
		TokenProcessEvent tpe = new TokenProcessEvent(context, decider, key, value, objectName, cdo);
		String error = TokenConverter.process(tpe);
		context.purge(cdo);
		TokenConverter.clearConstants();
		if (tpe.isConsumed())
		{
			return tpe.getResult();
		}
		else
		{
			Logging.errorPrint(error);
		}
		return null;
	}

	private String processBonus(TokenProcessEvent tpe, String key, String value)
	{
			LoadContext context = tpe.getContext();
			CDOMObject obj = tpe.getPrimary();
			if (context.processToken(obj, key, value))
			{
				context.commit();
			}
			else
			{
				context.rollback();
				Logging.replayParsedMessages();
			}
			Logging.clearParseMessages();
			Collection<String> output = context.unparse(obj);
			if (output == null || output.isEmpty())
			{
				// Uh Oh
				return ("Unable to unparse: " + key + ':' + value);
			}
			boolean needTab = false;
			for (String s : output)
			{
				if (needTab)
				{
					tpe.append('\t');
				}
				needTab = true;
				tpe.append(s);
			}
			tpe.consume();

		return null;
	}

	@Override
	public Class<? extends CDOMObject> getProcessedClass()
	{
		return CDOMObject.class;
	}

	@Override
	public String getProcessedToken()
	{
		return "BONUS";
	}
}
