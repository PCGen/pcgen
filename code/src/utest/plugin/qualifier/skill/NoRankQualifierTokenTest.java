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
package plugin.qualifier.skill;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;
import java.util.Collection;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.core.Skill;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.LstToken;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.QualifierToken;
import plugin.lsttokens.ChooseLst;
import plugin.lsttokens.choose.SkillToken;
import plugin.lsttokens.testsupport.AbstractQualifierTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.lsttokens.testsupport.TransparentPlayerCharacter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NoRankQualifierTokenTest extends
		AbstractQualifierTokenTestCase<CDOMObject, Skill>
{

	private static final CDOMPrimaryToken TOKEN = new ChooseLst();
	private static final CDOMSecondaryToken SUBTOKEN = new SkillToken();
	private static final CDOMLoader<CDOMObject> LOADER = new CDOMTokenLoader<>();
	private Skill s1, s2, s3;
	private PCClass cl1;

	private static final LstToken NORANK_TOKEN = new NoRankToken();

	public NoRankQualifierTokenTest()
	{
		super("NORANK", null);
	}

	@BeforeEach
	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(NORANK_TOKEN);
	}

	@Override
	public CDOMSecondaryToken<?> getSubToken()
	{
		return SUBTOKEN;
	}

	@Override
	public Class<Skill> getTargetClass()
	{
		return Skill.class;
	}

	@Override
	public Class<Skill> getCDOMClass()
	{
		return Skill.class;
	}

	@Override
	public CDOMLoader<CDOMObject> getLoader()
	{
		return LOADER;
	}

	@Override
	public CDOMPrimaryToken<CDOMObject> getToken()
	{
		return TOKEN;
	}

	@Override
	protected boolean allowsNotQualifier()
	{
		return false;
	}

	@Test
	public void testGetSet()
	{
		setUpPC();
		initializeObjects();
		assertTrue(parse(getSubTokenName() + "|NORANK[ALL]"));
		finishLoad();
		TransparentPlayerCharacter pc = new TransparentPlayerCharacter();

		ChooseInformation<?> info = primaryProf.get(ObjectKey.CHOOSE_INFO);
		pc.classMap.put(cl1, 1);
		Collection<?> set = info.getSet(pc);
		assertFalse(set.isEmpty());
		assertEquals(4, set.size());
		assertTrue(set.contains(s1));
		assertTrue(set.contains(s2));
		assertTrue(set.contains(s3));
		assertTrue(set.contains(primaryProf));
		pc.skillSet.put(s1, 1);
		pc.skillSet.put(s2, 2);
		set = info.getSet(pc);
		assertEquals(2, set.size());
		assertTrue(set.contains(s3));
		assertTrue(set.contains(primaryProf));
	}

	@Test
	public void testGetSetFiltered()
	{
		setUpPC();
		initializeObjects();
		assertTrue(parse(getSubTokenName() + "|NORANK[TYPE=Masterful]"));
		finishLoad();
		TransparentPlayerCharacter pc = new TransparentPlayerCharacter();

		ChooseInformation<?> info = primaryProf.get(ObjectKey.CHOOSE_INFO);
		pc.classMap.put(cl1, 1);
		Collection<?> set = info.getSet(pc);
		assertEquals(2, set.size());
		assertTrue(set.contains(s2));
		assertTrue(set.contains(s3));
		pc.skillSet.put(s1, 1);
		pc.skillSet.put(s2, 2);
		set = info.getSet(pc);
		assertFalse(set.isEmpty());
		assertEquals(1, set.size());
		assertTrue(set.contains(s3));
	}

	private void initializeObjects()
	{
		s1 = new Skill();
		s1.setName("s1");
		primaryContext.getReferenceContext().importObject(s1);

		s2 = new Skill();
		s2.setName("s2");
		primaryContext.getReferenceContext().importObject(s2);
		primaryContext.unconditionallyProcess(s2, "TYPE", "Masterful");

		s3 = new Skill();
		s3.setName("s3");
		primaryContext.getReferenceContext().importObject(s3);
		primaryContext.unconditionallyProcess(s3, "TYPE", "Masterful");

		cl1 = new PCClass();
		cl1.setName("MyClass");
		primaryContext.getReferenceContext().importObject(cl1);
	}

	@Override
	protected Class<? extends QualifierToken<?>> getQualifierClass()
	{
		return plugin.qualifier.skill.NoRankToken.class;
	}
}
