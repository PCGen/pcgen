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
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreRaceWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PreRaceRoundRobin extends AbstractBasicRoundRobin
{
	@BeforeEach
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreRaceParser());
		TokenRegistration.register(new PreRaceWriter());
	}

	@Override
	public String getBaseString()
	{
		return "RACE";
	}

	@Override
	public boolean isTypeAllowed()
	{
		return true;
	}

	@Test
	void testRaceType()
	{
		runRoundRobin("PRE" + getBaseString() + ":1,RACETYPE=Foo");
	}

	@Test
	void testRaceTypeCount()
	{
		runRoundRobin("PRE" + getBaseString() + ":2,RACETYPE=Foo");
	}

	@Test
	void testMultipleRaceType()
	{
		runRoundRobin("PRE" + getBaseString() + ":1,RACETYPE=Bar,RACETYPE=Foo");
	}

	@Test
	void testRaceTypeComplex()
	{
		runRoundRobin("PRE" + getBaseString() + ":3,Foo,RACETYPE=Foo.Bar");
	}

	@Test
	void testRaceSubType()
	{
		runRoundRobin("PRE" + getBaseString() + ":1,RACESUBTYPE=Foo");
	}

	@Test
	void testRaceSubTypeCount()
	{
		runRoundRobin("PRE" + getBaseString() + ":2,RACESUBTYPE=Foo");
	}

	@Test
	void testMultipleRaceSubType()
	{
		runRoundRobin("PRE" + getBaseString()
				+ ":1,RACESUBTYPE=Bar,RACESUBTYPE=Foo");
	}

	@Test
	void testRaceSubTypeComplex()
	{
		runRoundRobin("PRE" + getBaseString() + ":3,Foo,RACESUBTYPE=Bar");
	}

	@Test
	void testNegateItem()
	{
		AbstractPreRoundRobin.runSimpleRoundRobin("PRE" + getBaseString() + ":1,Foo,[TYPE=Bar]",
				"PREMULT:2,[PRE" + getBaseString() + ":1,Foo],[!PRE"
						+ getBaseString() + ":1,TYPE=Bar]");
	}

	@Test
	void testNegateItemRaceType()
	{
		AbstractPreRoundRobin.runSimpleRoundRobin("PRE" + getBaseString()
				+ ":1,Foo,[RACETYPE=Bar]", "PREMULT:2,[PRE" + getBaseString()
				+ ":1,Foo],[!PRE" + getBaseString() + ":1,RACETYPE=Bar]");
	}

	@Test
	void testNegateItemRaceSubType()
	{
		AbstractPreRoundRobin.runSimpleRoundRobin("PRE" + getBaseString()
				+ ":1,Foo,[RACESUBTYPE=Bar]", "PREMULT:2,[PRE"
				+ getBaseString() + ":1,Foo],[!PRE" + getBaseString()
				+ ":1,RACESUBTYPE=Bar]");
	}
}
