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

import pcgen.cdom.content.ContentDefinition;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.rules.types.FormatManager;
import pcgen.rules.types.FormatManagerLibrary;
import pcgen.util.Logging;

public class DataFormatToken extends AbstractNonEmptyToken<ContentDefinition>
		implements CDOMPrimaryToken<ContentDefinition>,
		DeferredToken<ContentDefinition>
{

	private static final Class<ContentDefinition> CONTENTDEF_CLASS =
			ContentDefinition.class;

	@Override
	public String getTokenName()
	{
		return "DATAFORMAT";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context,
		ContentDefinition def, String value)
	{
		FormatManager<?> fmtMgr = FormatManagerLibrary.getFormatManager(value);
		FormatManager<?> old = def.setFormatManager(fmtMgr);
		if (old != null)
		{
			return new ParseResult.Fail("Content Definition "
				+ def.getClass().getSimpleName() + " " + def.getKeyName()
				+ " was defined as " + old.getIdentifierType() + " and "
				+ value + " (using " + value + ")");
		}
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, ContentDefinition def)
	{
		FormatManager<?> manager = def.getFormatManager();
		if (manager == null)
		{
			return null;
		}
		return new String[]{manager.getIdentifierType()};
	}

	@Override
	public Class<ContentDefinition> getTokenClass()
	{
		return CONTENTDEF_CLASS;
	}

	@Override
	public boolean process(LoadContext context, ContentDefinition def)
	{
		boolean isMissingFormatManager = (def.getFormatManager() == null);
		if (isMissingFormatManager)
		{
			Logging.errorPrint("Content Definition "
				+ def.getClass().getSimpleName() + " " + def.getKeyName()
				+ " did not have a " + getTokenName());
		}
		return !isMissingFormatManager;
	}

	@Override
	public Class<ContentDefinition> getDeferredTokenClass()
	{
		return CONTENTDEF_CLASS;
	}
}
