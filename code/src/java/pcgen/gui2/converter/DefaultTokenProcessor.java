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
package pcgen.gui2.converter;

import java.util.Collection;

import pcgen.cdom.base.CDOMObject;
import pcgen.gui2.converter.event.TokenProcessEvent;
import pcgen.gui2.converter.event.TokenProcessor;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

public class DefaultTokenProcessor implements TokenProcessor
{
	@Override
	public String process(TokenProcessEvent tpe)
	{
			LoadContext context = tpe.getContext();
			CDOMObject obj = tpe.getPrimary();
			if (context.processToken(obj, tpe.getKey(), tpe.getValue()))
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
				return ("Unable to unparse: " + tpe.getKey() + ":" + tpe.getValue());
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
}
