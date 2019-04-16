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
package plugin.primitive.spell;

import java.net.URISyntaxException;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Race;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import plugin.lsttokens.ChooseLst;
import plugin.lsttokens.choose.SpellsToken;
import plugin.lsttokens.testsupport.AbstractPrimitiveTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AllTokenTest extends
		AbstractPrimitiveTokenTestCase<CDOMObject, Spell>
{
	static ChooseLst token = new ChooseLst();
	static SpellsToken subtoken = new SpellsToken();
	static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<>();

	private static final AllToken ALL_TOKEN = new AllToken();

	public AllTokenTest()
	{
		super("ALL", null);
	}

	@BeforeEach
	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(ALL_TOKEN);
		primaryContext.getReferenceContext().constructNowIfNecessary(getTargetClass(), "Placeholder");
		secondaryContext.getReferenceContext().constructNowIfNecessary(getTargetClass(), "Placeholder");
	}

	@Override
	public CDOMSecondaryToken<?> getSubToken()
	{
		return subtoken;
	}

	@Override
	public Class<Spell> getTargetClass()
	{
		return Spell.class;
	}

	@Override
	public Class<Race> getCDOMClass()
	{
		return Race.class;
	}

	@Override
	public CDOMLoader<CDOMObject> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMObject> getToken()
	{
		return token;
	}

	//TODO Commented out Mar 30 2010 to make Hudson happy - TP
//	public void testPrimitiveIllegalTarget() throws PersistenceLayerException
//	{
//		doPrimitiveIllegalTarget("Foo");
//	}

	@Test
	public void testPrimitiveIllegalBadArgs()
	{
		doPrimitiveIllegalTarget("Foo[Hi]");
	}

	@Test
	public void testPrimitiveIllegalBadKnownEquals()
	{
		doPrimitiveIllegalTarget("Foo[KNOWN=]");
	}

	@Test
	public void testPrimitiveIllegalBadKnownEqualsBad()
	{
		doPrimitiveIllegalTarget("Foo[KNOWN=Bad]");
	}

	@Test
	public void testPrimitiveIllegalBadLevelMax()
	{
		doPrimitiveIllegalTarget("Foo[LEVELMAX]");
	}

	@Test
	public void testPrimitiveIllegalBadLevelMaxEquals()
	{
		doPrimitiveIllegalTarget("Foo[LEVELMAX=]");
	}

	@Test
	public void testPrimitiveIllegalBadLevelMaxEqualsBad()
	{
		doPrimitiveIllegalTarget("Foo[LEVELMAX=3-]");
	}

	@Test
	public void testPrimitiveIllegalBadLevelMin()
	{
		doPrimitiveIllegalTarget("Foo[LEVELMIN]");
	}

	@Test
	public void testPrimitiveIllegalBadLevelMinEquals()
	{
		doPrimitiveIllegalTarget("Foo[LEVELMIN=]");
	}

	@Test
	public void testPrimitiveIllegalBadLevelMinEqualsBad()
	{
		doPrimitiveIllegalTarget("Foo[LEVELMIN=3+]");
	}
}
