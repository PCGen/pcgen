/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class AutoLst extends AbstractToken implements
		CDOMPrimaryToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "AUTO";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
			throws PersistenceLayerException
	{
		if (isEmpty(value))
		{
			return false;
		}
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
					+ " requires a SubToken");
			return false;
		}
		return context.processSubToken(obj, getTokenName(), value.substring(0,
				pipeLoc), value.substring(pipeLoc + 1));
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		return context.unparse(obj, getTokenName());
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
