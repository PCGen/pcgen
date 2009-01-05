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

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.PCTemplate;
import pcgen.core.WeaponProf;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

public class ChangeProfLstTest extends AbstractGlobalTokenTestCase
{

	static CDOMPrimaryToken<CDOMObject> token = new ChangeprofLst();
	static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<PCTemplate>(
			PCTemplate.class);

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
	public void testInvalidSourceOnly() throws PersistenceLayerException
	{
		assertFalse(parse("Hammer"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidSourceEqualOnly() throws PersistenceLayerException
	{
		assertFalse(parse("Hammer="));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidSourceEqualOnlyTypeTwo()
			throws PersistenceLayerException
	{
		assertFalse(parse("Hammer=Martial|Pipe="));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptySource() throws PersistenceLayerException
	{
		assertFalse(parse("=Martial"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidTwoEquals() throws PersistenceLayerException
	{
		assertFalse(parse("Hammer==Martial"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidTwoEqualsTypeTwo() throws PersistenceLayerException
	{
		assertFalse(parse("Hammer=TYPE.Heavy=Martial"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidBarEnding() throws PersistenceLayerException
	{
		assertFalse(parse("Hammer=Martial|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidBarStarting() throws PersistenceLayerException
	{
		assertFalse(parse("|Hammer=Martial"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDoublePipe() throws PersistenceLayerException
	{
		assertFalse(parse("Hammer=Martial||Pipe=Exotic"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidReversed() throws PersistenceLayerException
	{
		assertTrue(parse("Martial=Hammer"));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testInvalidResultPrimitive() throws PersistenceLayerException
	{
		assertTrue(parse("Hammer=Pipe"));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testInvalidResultType() throws PersistenceLayerException
	{
		try
		{
			assertFalse(parse("Hammer=TYPE.Heavy"));
		}
		catch (IllegalArgumentException e)
		{
			// This is okay too
		}
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(WeaponProf.class, "Hammer");
		secondaryContext.ref.constructCDOMObject(WeaponProf.class, "Hammer");
		runRoundRobin("Hammer=Martial");
	}

	@Test
	public void testRoundRobinTwo() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(WeaponProf.class, "Hammer");
		secondaryContext.ref.constructCDOMObject(WeaponProf.class, "Hammer");
		primaryContext.ref.constructCDOMObject(WeaponProf.class, "Pipe");
		secondaryContext.ref.constructCDOMObject(WeaponProf.class, "Pipe");
		runRoundRobin("Hammer,Pipe=Martial");
	}

	@Test
	public void testRoundRobinType() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(WeaponProf.class, "Hammer");
		secondaryContext.ref.constructCDOMObject(WeaponProf.class, "Hammer");
		runRoundRobin("Hammer,TYPE.Heavy=Martial");
	}

	@Test
	public void testRoundRobinTwoResult() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(WeaponProf.class, "Hammer");
		secondaryContext.ref.constructCDOMObject(WeaponProf.class, "Hammer");
		primaryContext.ref.constructCDOMObject(WeaponProf.class, "Pipe");
		secondaryContext.ref.constructCDOMObject(WeaponProf.class, "Pipe");
		runRoundRobin("Hammer=Martial|Pipe=Exotic");
	}

	@Test
	public void testRoundRobinComplex() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(WeaponProf.class, "Hammer");
		secondaryContext.ref.constructCDOMObject(WeaponProf.class, "Hammer");
		primaryContext.ref.constructCDOMObject(WeaponProf.class, "Nail");
		secondaryContext.ref.constructCDOMObject(WeaponProf.class, "Nail");
		runRoundRobin("Hammer,TYPE.Heavy,TYPE.Medium=Martial|Nail,TYPE.Crazy,TYPE.Disposable=Exotic");
	}
}