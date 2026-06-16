/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.primitive.race;

import java.net.URISyntaxException;

import pcgen.TestConstants;
import pcgen.cdom.base.CDOMObject;
import pcgen.core.Race;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;

import plugin.lsttokens.choose.RaceToken;
import plugin.lsttokens.testsupport.AbstractPrimitiveTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.BeforeEach;

public class RaceTypeTokenTest extends
		AbstractPrimitiveTokenTestCase<CDOMObject, Race>
{

	private static final RaceToken SUBTOKEN = new RaceToken();
	private static final RaceTypeToken RACETYPE_TOKEN = new RaceTypeToken();

	public RaceTypeTokenTest()
	{
		super("RACETYPE", "SampleType");
	}

	@BeforeEach
	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(RACETYPE_TOKEN);
	}

	@Override
	public CDOMSecondaryToken<?> getSubToken()
	{
		return SUBTOKEN;
	}

	@Override
	public Class<Race> getTargetClass()
	{
		return Race.class;
	}

	@Override
	public Class<Race> getCDOMClass()
	{
		return Race.class;
	}

	@Override
	public CDOMLoader<CDOMObject> getLoader()
	{
		return TestConstants.TOKEN_LOADER;
	}

	@Override
	public CDOMPrimaryToken<CDOMObject> getToken()
	{
		return TestConstants.CHOOSE_TOKEN;
	}

}
