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
package plugin.primitive.domain;

import java.net.URISyntaxException;

import pcgen.TestConstants;
import pcgen.cdom.base.CDOMObject;
import pcgen.core.Domain;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;

import plugin.lsttokens.choose.DomainToken;
import plugin.lsttokens.testsupport.AbstractPrimitiveTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.BeforeEach;

public class DeityTokenTest extends
		AbstractPrimitiveTokenTestCase<CDOMObject, Domain>
{

	private static final DomainToken SUBTOKEN = new DomainToken();
	private static final plugin.primitive.domain.DeityToken DEITY_TOKEN = new plugin.primitive.domain.DeityToken();

	public DeityTokenTest()
	{
		super("DEITY", null);
	}

	@BeforeEach
	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(DEITY_TOKEN);
	}

	@Override
	public CDOMSecondaryToken<?> getSubToken()
	{
		return SUBTOKEN;
	}

	@Override
	public Class<Domain> getTargetClass()
	{
		return Domain.class;
	}

	@Override
	public Class<Domain> getCDOMClass()
	{
		return Domain.class;
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
