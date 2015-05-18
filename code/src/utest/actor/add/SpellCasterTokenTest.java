/*
 * 
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
package actor.add;

import org.junit.Test;

import pcgen.cdom.base.Persistent;
import pcgen.core.PCClass;
import plugin.lsttokens.add.SpellCasterToken;
import actor.testsupport.AbstractPersistentCDOMChoiceActorTestCase;

public class SpellCasterTokenTest extends
		AbstractPersistentCDOMChoiceActorTestCase<PCClass>
{

	static SpellCasterToken pca = new SpellCasterToken();

	@Test
	public void testEmpty()
	{
		// Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
	}

	@Override
	public Persistent<PCClass> getActor()
	{
		return pca;
	}

	@Override
	public Class<PCClass> getCDOMClass()
	{
		return PCClass.class;
	}
}
