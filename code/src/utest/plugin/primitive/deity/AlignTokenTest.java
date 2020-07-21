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
package plugin.primitive.deity;

import java.net.URISyntaxException;

import pcgen.TestConstants;
import pcgen.cdom.base.CDOMObject;
import pcgen.core.Deity;
import pcgen.core.PCAlignment;
import pcgen.core.Race;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;

import plugin.lsttokens.choose.DeityToken;
import plugin.lsttokens.testsupport.AbstractPrimitiveTokenTestCase;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.BeforeEach;

public class AlignTokenTest extends
		AbstractPrimitiveTokenTestCase<CDOMObject, Deity>
{
	private static final DeityToken SUBTOKEN = new DeityToken();
	private static final AlignToken ALIGN_TOKEN = new AlignToken();

	public AlignTokenTest()
	{
		super("ALIGN", "LG");
	}

	@BeforeEach
	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		PCAlignment lg = BuildUtilities.createAlignment("Lawful Good", "LG");
		primaryContext.getReferenceContext().importObject(lg);
		PCAlignment slg = BuildUtilities.createAlignment("Lawful Good", "LG");
		secondaryContext.getReferenceContext().importObject(slg);
		TokenRegistration.register(ALIGN_TOKEN);
	}

	@Override
	public CDOMSecondaryToken<?> getSubToken()
	{
		return SUBTOKEN;
	}

	@Override
	public Class<Deity> getTargetClass()
	{
		return Deity.class;
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

	public void testPrimitiveIllegalSpelledOut()
	{
		doPrimitiveIllegalTarget("LawfulGood");
	}

	public void testPrimitiveIllegalMultiple()
	{
		doPrimitiveIllegalTarget("LG.NG");
	}
}
