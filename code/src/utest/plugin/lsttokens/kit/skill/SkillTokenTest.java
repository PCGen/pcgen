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
package plugin.lsttokens.kit.skill;

import org.junit.Test;

import pcgen.core.Skill;
import pcgen.core.kit.KitSkill;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMSubLineLoader;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import plugin.lsttokens.testsupport.AbstractSubTokenTestCase;

public class SkillTokenTest extends AbstractSubTokenTestCase<KitSkill>
{

	static SkillToken token = new SkillToken();
	static CDOMSubLineLoader<KitSkill> loader = new CDOMSubLineLoader<KitSkill>(
			"*KITTOKEN", "SKILL", KitSkill.class);

	@Override
	public Class<KitSkill> getCDOMClass()
	{
		return KitSkill.class;
	}

	@Override
	public CDOMSubLineLoader<KitSkill> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMSecondaryToken<KitSkill> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputEmptyCount() throws PersistenceLayerException
	{
		assertTrue(parse("Fireball"));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Skill.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(Skill.class, "Fireball");
		runRoundRobin("Fireball");
	}

	@Test
	public void testInvalidInputEmptyType() throws PersistenceLayerException
	{
		assertFalse(parse("TYPE="));
	}

	@Test
	public void testInvalidInputTrailingType() throws PersistenceLayerException
	{
		assertFalse(parse("TYPE=One."));
	}

	@Test
	public void testInvalidInputDoubleType() throws PersistenceLayerException
	{
		assertFalse(parse("TYPE=One..Two"));
	}

	@Test
	public void testRoundRobinType() throws PersistenceLayerException
	{
		runRoundRobin("TYPE=Foo");
	}
}
