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

import org.junit.Before;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreSkillParser;
import plugin.pretokens.writer.PreSkillMultWriter;

public class PreSkillMultRoundRobin extends AbstractRankedRoundRobin
{

	public static void main(String args[])
	{
		TestRunner.run(PreSkillMultRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreSkillMultRoundRobin.class);
	}



	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreSkillParser());
		TokenRegistration.register(new PreSkillMultWriter());
	}

	@Override
	public String getBaseString()
	{
		return "SKILLMULT";
	}

	@Override
	public boolean isAnyAllowed()
	{
		return true;
	}

	@Override
	public boolean isTypeAllowed()
	{
		return true;
	}
}
