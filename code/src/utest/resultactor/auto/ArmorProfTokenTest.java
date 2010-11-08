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
package resultactor.auto;

import org.junit.Test;

import pcgen.cdom.base.ChooseResultActor;
import pcgen.core.ArmorProf;
import plugin.lsttokens.auto.ArmorProfToken;
import resultactor.testsupport.AbstractResultActorTest;

public class ArmorProfTokenTest extends AbstractResultActorTest<ArmorProf>
{

	static ArmorProfToken cra = new ArmorProfToken();

	@Test
	public void testEmpty()
	{
		// Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
	}

	@Override
	public ChooseResultActor getActor()
	{
		return cra;
	}

	@Override
	public Class<ArmorProf> getCDOMClass()
	{
		return ArmorProf.class;
	}

	@Override
	public boolean isGranted()
	{
		return false;
	}
}
