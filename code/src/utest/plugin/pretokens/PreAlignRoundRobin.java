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

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.testsupport.TestSupport;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreAlignParser;
import plugin.pretokens.writer.PreAlignWriter;

public class PreAlignRoundRobin extends AbstractAlignRoundRobin
{
	public static void main(String args[])
	{
		TestRunner.run(PreAlignRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreAlignRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreAlignParser());
		TokenRegistration.register(new PreAlignWriter());
		TestSupport.createAllAlignments();
	}

	public void testDeity()
	{
		runRoundRobin("PRE" + getBaseString() + ":Deity");
	}

	@Override
	public String getBaseString()
	{
		return "ALIGN";
	}

}