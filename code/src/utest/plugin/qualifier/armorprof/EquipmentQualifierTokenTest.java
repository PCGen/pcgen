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
package plugin.qualifier.armorprof;

import java.net.URISyntaxException;
import java.util.Collection;

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.ArmorProf;
import pcgen.core.Equipment;
import pcgen.core.Race;
import pcgen.core.ShieldProf;
import pcgen.core.WeaponProf;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.QualifierToken;
import plugin.lsttokens.ChooseLst;
import plugin.lsttokens.choose.ArmorProficiencyToken;
import plugin.lsttokens.testsupport.AbstractQualifierTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.lsttokens.testsupport.TransparentPlayerCharacter;

public class EquipmentQualifierTokenTest extends
		AbstractQualifierTokenTestCase<CDOMObject, Equipment>
{

	static ChooseLst token = new ChooseLst();
	static ArmorProficiencyToken subtoken = new ArmorProficiencyToken();
	static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<>();
	private static final plugin.qualifier.armorprof.EquipmentToken EQUIPMENT_TOKEN = new plugin.qualifier.armorprof.EquipmentToken();
	private WeaponProf wp1;
	private ShieldProf sp1;
	private ArmorProf ap1, ap2;
	private Equipment eq1, eq2, eq3, eq4;

	public EquipmentQualifierTokenTest()
	{
		super("EQUIPMENT", null);
	}

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(EQUIPMENT_TOKEN);
	}

	@Override
	public CDOMSecondaryToken<?> getSubToken()
	{
		return subtoken;
	}

	@Override
	public Class<Equipment> getTargetClass()
	{
		return Equipment.class;
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

	@Test
	public void testGetSet() throws PersistenceLayerException
	{
		setUpPC();
		initializeObjects();		
		assertTrue(parse(getSubTokenName() + "|EQUIPMENT[ALL]"));
		finishLoad();
		TransparentPlayerCharacter pc = new TransparentPlayerCharacter();

		ChooseInformation<?> info = primaryProf.get(ObjectKey.CHOOSE_INFO);
		Collection<?> set = info.getSet(pc);
		assertEquals(2, set.size());
		assertTrue(set.contains(ap1));
		assertTrue(set.contains(ap2));
	}

	@Test
	public void testGetSetFiltered() throws PersistenceLayerException
	{
		setUpPC();
		initializeObjects();		
		assertTrue(parse(getSubTokenName() + "|EQUIPMENT[TYPE=Masterful]"));
		finishLoad();
		TransparentPlayerCharacter pc = new TransparentPlayerCharacter();

		ChooseInformation<?> info = primaryProf.get(ObjectKey.CHOOSE_INFO);
		Collection<?> set = info.getSet(pc);
		assertFalse(set.isEmpty());
		assertEquals(1, set.size());
		assertEquals(ap2, set.iterator().next());
	}

	private void initializeObjects()
	{
		wp1 = new WeaponProf();
		wp1.setName("Eq1");
		primaryContext.getReferenceContext().importObject(wp1);
		eq1 = new Equipment();
		eq1.setName("Eq1");
		primaryContext.getReferenceContext().importObject(eq1);
		primaryContext.unconditionallyProcess(eq1, "TYPE", "WEAPON");
		primaryContext.unconditionallyProcess(eq1, "PROFICIENCY", "WEAPON|Eq1");
		
		sp1 = new ShieldProf();
		sp1.setName("Eq2");
		primaryContext.getReferenceContext().importObject(sp1);
		eq2 = new Equipment();
		eq2.setName("Eq2");
		primaryContext.getReferenceContext().importObject(eq2);
		primaryContext.unconditionallyProcess(eq2, "TYPE", "SHIELD.Masterful");
		primaryContext.unconditionallyProcess(eq2, "PROFICIENCY", "SHIELD|Eq2");

		ap1 = new ArmorProf();
		ap1.setName("Eq3");
		primaryContext.getReferenceContext().importObject(ap1);
		eq3 = new Equipment();
		eq3.setName("Eq3");
		primaryContext.getReferenceContext().importObject(eq3);
		primaryContext.unconditionallyProcess(eq3, "TYPE", "ARMOR");
		primaryContext.unconditionallyProcess(eq3, "PROFICIENCY", "ARMOR|Eq3");
		
		ap2 = new ArmorProf();
		ap2.setName("Ap2");
		primaryContext.getReferenceContext().importObject(ap2);
		eq4 = new Equipment();
		eq4.setName("Eq4");
		primaryContext.getReferenceContext().importObject(eq4);
		primaryContext.unconditionallyProcess(eq4, "TYPE", "ARMOR.Masterful");
		primaryContext.unconditionallyProcess(eq4, "PROFICIENCY", "ARMOR|Ap2");
	}

	@Override
	protected Class<? extends QualifierToken<?>> getQualifierClass()
	{
		return plugin.qualifier.armorprof.EquipmentToken.class;
	}
}
