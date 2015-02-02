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
package plugin.lsttokens.testsupport;

import java.util.Collection;

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Race;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.ChooseLst;

public abstract class AbstractPCQualifierTokenTestCase<T extends CDOMObject>
		extends AbstractQualifierTokenTestCase<CDOMObject, T>
{

	private static ChooseLst token = new ChooseLst();
	private static CDOMTokenLoader<CDOMObject> loader =
			new CDOMTokenLoader<CDOMObject>();

	public AbstractPCQualifierTokenTestCase()
	{
		super("PC", null);
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

	@Override
	protected boolean allowsNotQualifier()
	{
		return true;
	}

	private T wp1, wp2, wp3;

	@Test
	public void testGetSet() throws PersistenceLayerException,
		InstantiationException, IllegalAccessException
	{
		setUpPC();
		TransparentPlayerCharacter pc = new TransparentPlayerCharacter();
		initializeObjects();
		assertTrue(parse(getSubTokenName() + "|PC"));
		finishLoad();

		ChooseInformation<?> info = primaryProf.get(ObjectKey.CHOOSE_INFO);
		Collection<?> set = info.getSet(pc);
		assertTrue(set.isEmpty());
		addToPCSet(pc, wp1);
		set = info.getSet(pc);
		assertFalse(set.isEmpty());
		assertEquals(1, set.size());
		assertEquals(wp1, set.iterator().next());
		addToPCSet(pc, wp2);
		set = info.getSet(pc);
		assertFalse(set.isEmpty());
		if (typeAllowsMult())
		{
			assertEquals(2, set.size());
			assertTrue(set.contains(wp1));
			assertTrue(set.contains(wp2));
		}
		else
		{
			assertEquals(1, set.size());
			assertTrue(set.contains(wp2));
		}
	}

	protected boolean typeAllowsMult()
	{
		return true;
	}

	@Test
	public void testGetSetFiltered() throws PersistenceLayerException,
		InstantiationException, IllegalAccessException
	{
		setUpPC();
		TransparentPlayerCharacter pc = new TransparentPlayerCharacter();
		initializeObjects();
		assertTrue(parse(getSubTokenName() + "|PC[TYPE=Masterful]"));
		finishLoad();

		ChooseInformation<?> info = primaryProf.get(ObjectKey.CHOOSE_INFO);
		Collection<?> set = info.getSet(pc);
		assertTrue(set.isEmpty());
		addToPCSet(pc, wp1);
		addToPCSet(pc, wp2);
		set = info.getSet(pc);
		assertFalse(set.isEmpty());
		assertEquals(1, set.size());
		assertEquals(wp2, set.iterator().next());
	}

	@Test
	public void testGetSetNegated() throws PersistenceLayerException,
		InstantiationException, IllegalAccessException
	{
		setUpPC();
		TransparentPlayerCharacter pc = new TransparentPlayerCharacter();
		initializeObjects();
		assertTrue(parse(getSubTokenName() + "|!PC[TYPE=Masterful]"));
		finishLoad();

		ChooseInformation<?> info = primaryProf.get(ObjectKey.CHOOSE_INFO);
		Collection<?> set = info.getSet(pc);
		assertFalse(set.isEmpty());
		assertEquals(2, set.size());
		assertTrue(set.contains(wp2));
		assertTrue(set.contains(wp3));
		addToPCSet(pc, wp1);
		addToPCSet(pc, wp2);
		set = info.getSet(pc);
		assertFalse(set.isEmpty());
		assertEquals(1, set.size());
		assertTrue(set.contains(wp3));
	}

	protected abstract void addToPCSet(TransparentPlayerCharacter pc, T item);

	private void initializeObjects() throws InstantiationException,
		IllegalAccessException
	{
		wp1 = getTargetClass().newInstance();
		wp1.setName("Eq1");
		primaryContext.unconditionallyProcess(wp1, "TYPE", "Boring");
		primaryContext.getReferenceContext().importObject(wp1);

		wp2 = getTargetClass().newInstance();
		wp2.setName("Wp2");
		primaryContext.unconditionallyProcess(wp2, "TYPE", "Masterful");
		primaryContext.getReferenceContext().importObject(wp2);

		wp3 = getTargetClass().newInstance();
		wp3.setName("Wp3");
		primaryContext.unconditionallyProcess(wp3, "TYPE", "Masterful");
		primaryContext.getReferenceContext().importObject(wp3);
	}
}