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
package plugin.lsttokens.testsupport;

import pcgen.cdom.base.CDOMObject;

import org.junit.Assert;

public abstract class AbstractCDOMTokenTestCase<T extends CDOMObject> extends
		AbstractTokenTestCase<T>
{
	@Override
	public void isCDOMEqual(T cdo1, T cdo2)
	{
		Assert.assertTrue("Not equal " + cdo1 + " and " + cdo2, cdo1.isCDOMEqual(cdo2));
	}
}
