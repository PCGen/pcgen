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
package plugin.lsttokens.editcontext.equipment;

import pcgen.core.Equipment;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.editcontext.testsupport.AbstractTextPropertyIntegrationTestCase;
import plugin.lsttokens.equipment.SpropToken;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

public class SPropIntegrationTest extends
		AbstractTextPropertyIntegrationTestCase<Equipment>
{
	private static boolean classSetUpFired = false;

	private static SpropToken token = new SpropToken();
	private static CDOMTokenLoader<Equipment> loader = new CDOMTokenLoader<>();

	@Override
	public Class<Equipment> getCDOMClass()
	{
		return Equipment.class;
	}

	@Override
	public CDOMLoader<Equipment> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<Equipment> getToken()
	{
		return token;
	}

	@Override
	protected boolean isClearLegal()
	{
		return true;
	}

	@Override
	protected boolean getClassSetUpFired()
	{
		return classSetUpFired;
	}

	@Override
	protected void setClassSetUpFired(boolean b)
	{
		classSetUpFired = b;
	}

}
