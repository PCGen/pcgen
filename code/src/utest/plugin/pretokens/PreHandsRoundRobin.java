/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.pretokens;

import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreHandsParser;
import plugin.pretokens.writer.PreHandsWriter;

public class PreHandsRoundRobin extends AbstractComparatorRoundRobin
{


	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreHandsParser());
		TokenRegistration.register(new PreHandsWriter());
	}

	public void testSimpleInteger()
	{
		runRoundRobin("1");
	}

	@Override
	public String getBaseString()
	{
		return "HANDS";
	}

	@Override
	public boolean isBaseAllowed()
	{
		return true;
	}

}
