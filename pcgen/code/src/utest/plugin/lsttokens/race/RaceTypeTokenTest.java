/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.race;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.RaceType;
import pcgen.core.Race;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTypeSafeTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

public class RaceTypeTokenTest extends AbstractTypeSafeTokenTestCase<Race, RaceType>
{

	static RacetypeToken token = new RacetypeToken();
	static CDOMTokenLoader<Race> loader = new CDOMTokenLoader<>();

	@Override
	public Class<Race> getCDOMClass()
	{
		return Race.class;
	}

	@Override
	public CDOMLoader<Race> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<Race> getToken()
	{
		return token;
	}

	@Override
	public RaceType getConstant(String string)
	{
		return RaceType.getConstant(string);
	}

	@Override
	public ObjectKey<RaceType> getObjectKey()
	{
		return ObjectKey.RACETYPE;
	}

	@Override
	protected boolean requiresPreconstruction()
	{
		return false;
	}

	@Override
	public boolean isClearLegal()
	{
		return false;
	}
}
