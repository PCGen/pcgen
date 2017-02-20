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
package plugin.lsttokens;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class AddTokenTest extends AbstractGlobalTokenTestCase
{

	static CDOMPrimaryToken<CDOMObject> token = new AddLst();
	static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<>();

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
	}

	@Override
	public CDOMLoader<PCTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	public CDOMPrimaryToken<CDOMObject> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidClearLevel() throws PersistenceLayerException
	{
		assertFalse(parse(".CLEAR.LEVEL1"));
		assertNoSideEffects();
	}

	@Test
	public void testValidClear() throws PersistenceLayerException
	{
		assertTrue(parse(Constants.LST_DOT_CLEAR));
	}

	@Test
	public void testInvalidLevelNonClearLevel()
			throws PersistenceLayerException
	{
		primaryProf = new PCClassLevel();
		primaryProf.put(IntegerKey.LEVEL, 1);
		secondaryProf = new PCClassLevel();
		secondaryProf.put(IntegerKey.LEVEL, 1);
		assertFalse(parse(Constants.LST_DOT_CLEAR));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidLevelClearWrongLevel()
			throws PersistenceLayerException
	{
		primaryProf = new PCClassLevel();
		primaryProf.put(IntegerKey.LEVEL, 1);
		secondaryProf = new PCClassLevel();
		secondaryProf.put(IntegerKey.LEVEL, 1);
		assertFalse(parse(".CLEAR.LEVEL2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidLevelClearLevelNaN()
			throws PersistenceLayerException
	{
		primaryProf = new PCClassLevel();
		primaryProf.put(IntegerKey.LEVEL, 1);
		secondaryProf = new PCClassLevel();
		secondaryProf.put(IntegerKey.LEVEL, 1);
		assertFalse(parse(".CLEAR.LEVELx"));
		assertNoSideEffects();
	}

	@Test
	public void testValidClearLevel() throws PersistenceLayerException
	{
		primaryProf = new PCClassLevel();
		primaryProf.put(IntegerKey.LEVEL, 1);
		secondaryProf = new PCClassLevel();
		secondaryProf.put(IntegerKey.LEVEL, 1);
		assertTrue(parse(".CLEAR.LEVEL1"));
	}

	@Override
	protected String getAlternateLegalValue()
	{
		// Not worth it, nothing ever unparses
		return null;
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		// Not worth it, nothing ever unparses
		return null;
	}

	@Override
	protected String getLegalValue()
	{
		// Not worth it, nothing ever unparses
		return null;
	}

	@Override
	public void testOverwrite() throws PersistenceLayerException
	{
		// Can't be done, nothing ever unparses
	}

}