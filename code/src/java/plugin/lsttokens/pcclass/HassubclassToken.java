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
package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with HASSUBCLASS Token
 */
public class HassubclassToken implements CDOMPrimaryToken<PCClass>
{

	public String getTokenName()
	{
		return "HASSUBCLASS";
	}

	public boolean parse(LoadContext context, PCClass obj, String value)
			throws PersistenceLayerException
	{
		return true;
	}

	public String[] unparse(LoadContext context, PCClass obj)
	{
		// Intentional
		// TODO Need to deprecate this token
		return null;
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}
