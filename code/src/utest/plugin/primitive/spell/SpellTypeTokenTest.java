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

import org.junit.Before;
import org.junit.Test;
import plugin.lsttokens.ChooseLst;
import plugin.lsttokens.choose.SpellsToken;
import plugin.lsttokens.testsupport.AbstractPrimitiveTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.TokenRegistration;

public class SpellTypeTokenTest extends
		AbstractPrimitiveTokenTestCase<CDOMObject, Spell>
{
	static ChooseLst token = new ChooseLst();
	static SpellsToken subtoken = new SpellsToken();
	static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<>();

	private static final SpellTypeToken SPELLTYPE_TOKEN = new SpellTypeToken();

	public SpellTypeTokenTest()
	{
		super("SPELLTYPE", "SampleType");
	}
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(SPELLTYPE_TOKEN);
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

	@Test
	public void testPrimitiveIllegalNullTarget() throws PersistenceLayerException
	{
		doPrimitiveIllegalTarget(null);
	}

	@Test
	public void testPrimitiveIllegalNoTarget() throws PersistenceLayerException
	{
		doPrimitiveIllegalTarget("");
	}

	@Test
	public void testPrimitiveIllegalBadArgs() throws PersistenceLayerException
	{
		doPrimitiveIllegalTarget("Foo[Hi]");
	}

	@Test
	public void testPrimitiveIllegalBadKnownEquals()
			throws PersistenceLayerException
	{
		doPrimitiveIllegalTarget("Foo[KNOWN=]");
	}

	@Test
	public void testPrimitiveIllegalBadKnownEqualsBad()
			throws PersistenceLayerException
	{
		doPrimitiveIllegalTarget("Foo[KNOWN=Bad]");
	}

	@Test
	public void testPrimitiveIllegalBadLevelMax()
			throws PersistenceLayerException
	{
		doPrimitiveIllegalTarget("Foo[LEVELMAX]");
	}

	@Test
	public void testPrimitiveIllegalBadLevelMaxEquals()
			throws PersistenceLayerException
	{
		doPrimitiveIllegalTarget("Foo[LEVELMAX=]");
	}

	@Test
	public void testPrimitiveIllegalBadLevelMaxEqualsBad()
			throws PersistenceLayerException
	{
		doPrimitiveIllegalTarget("Foo[LEVELMAX=3-]");
	}

	@Test
	public void testPrimitiveIllegalBadLevelMin()
			throws PersistenceLayerException
	{
		doPrimitiveIllegalTarget("Foo[LEVELMIN]");
	}

	@Test
	public void testPrimitiveIllegalBadLevelMinEquals()
			throws PersistenceLayerException
	{
		doPrimitiveIllegalTarget("Foo[LEVELMIN=]");
	}

	@Test
	public void testPrimitiveIllegalBadLevelMinEqualsBad()
			throws PersistenceLayerException
	{
		doPrimitiveIllegalTarget("Foo[LEVELMIN=3+]");
	}

	@Test
	public void testRestriction() throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenName() + '|' + "SPELLTYPE=SampleType[KNOWN=YES]");
		
	}
}
