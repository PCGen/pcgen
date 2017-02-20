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
package plugin.qualifier.weaponprof;

import java.net.URISyntaxException;
import java.util.Collection;

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Race;
import pcgen.core.WeaponProf;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.QualifierToken;
import plugin.lsttokens.ChooseLst;
import plugin.lsttokens.choose.WeaponProficiencyToken;
import plugin.lsttokens.testsupport.AbstractQualifierTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.lsttokens.testsupport.TransparentPlayerCharacter;

public class SpellcasterQualifierTokenTest extends
		AbstractQualifierTokenTestCase<CDOMObject, WeaponProf>
{

	static ChooseLst token = new ChooseLst();
	static WeaponProficiencyToken subtoken = new WeaponProficiencyToken();
	static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<>();

	private static final plugin.qualifier.weaponprof.SpellCasterToken PC_TOKEN = new plugin.qualifier.weaponprof.SpellCasterToken();
	private WeaponProf wp1, wp2, wp3;

	public SpellcasterQualifierTokenTest()
	{
		super("SPELLCASTER", null);
	}

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(PC_TOKEN);
	}

	@Override
	public CDOMSecondaryToken<?> getSubToken()
	{
		return subtoken;
	}

	@Override
	public Class<WeaponProf> getTargetClass()
	{
		return WeaponProf.class;
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
		return false;
	}

	@Override
	protected boolean allowsLoneQualifier()
	{
		return false;
	}

	@Test
	public void testGetSet() throws PersistenceLayerException
	{
		setUpPC();
		initializeObjects();		
		assertTrue(parse(getSubTokenName() + "|SPELLCASTER[ALL]"));
		finishLoad();
		TransparentPlayerCharacter pc = new TransparentPlayerCharacter();

		ChooseInformation<?> info = primaryProf.get(ObjectKey.CHOOSE_INFO);
		Collection<?> set = info.getSet(pc);
		assertTrue(set.isEmpty());
		pc.weaponProfSet.add(wp1);
		pc.weaponProfSet.add(wp2);
		set = info.getSet(pc);
		assertTrue(set.isEmpty());
		pc.spellcastinglevel = 4;
		set = info.getSet(pc);
		assertFalse(set.isEmpty());
		//Note the INTENTIOANL effect here is to ADD ALL regardless of what the PC has
		assertEquals(3, set.size());
		assertTrue(set.contains(wp1));
		assertTrue(set.contains(wp2));
		assertTrue(set.contains(wp3));
	}

	@Test
	public void testGetSetFiltered() throws PersistenceLayerException
	{
		setUpPC();
		initializeObjects();		
		assertTrue(parse(getSubTokenName() + "|SPELLCASTER[TYPE=Masterful]"));
		finishLoad();
		TransparentPlayerCharacter pc = new TransparentPlayerCharacter();

		ChooseInformation<?> info = primaryProf.get(ObjectKey.CHOOSE_INFO);
		Collection<?> set = info.getSet(pc);
		assertTrue(set.isEmpty());
		pc.spellcastinglevel = 3;
		assertTrue(set.isEmpty());
		pc.weaponProfSet.add(wp1);
		pc.weaponProfSet.add(wp2);
		set = info.getSet(pc);
		assertFalse(set.isEmpty());
		//Note again the intentional effect of adding everything regardless of whether the PC has it
		assertEquals(2, set.size());
		assertTrue(set.contains(wp2));
		assertTrue(set.contains(wp3));
	}

	private void initializeObjects()
	{
		wp1 = new WeaponProf();
		wp1.setName("Wp1");
		primaryContext.getReferenceContext().importObject(wp1);
		
		wp2 = new WeaponProf();
		wp2.setName("Wp2");
		primaryContext.getReferenceContext().importObject(wp2);
		primaryContext.unconditionallyProcess(wp2, "TYPE", "WEAPON.Masterful");

		wp3 = new WeaponProf();
		wp3.setName("Wp3");
		primaryContext.getReferenceContext().importObject(wp3);
		primaryContext.unconditionallyProcess(wp3, "TYPE", "WEAPON.Masterful");
	}

	@Override
	protected Class<? extends QualifierToken<?>> getQualifierClass()
	{
		return plugin.qualifier.weaponprof.SpellCasterToken.class;
	}
}
