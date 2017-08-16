/*
 * Copyright 2008 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreAgeSetParser;
import plugin.pretokens.writer.PreAgeSetWriter;

/**
 * {@code PreAgeSetRoundRobin} verifies that preageset tags can be
 * read and written.
 */
public class PreAgeSetRoundRobin extends AbstractBasicRoundRobin
{
	
	/**
	 * The main method.
	 * 
	 * @param args the arguments
	 */
	public static void main(String args[])
	{
		TestRunner.run(PreAgeSetRoundRobin.class);
	}

	/**
	 * Suite.
	 * 
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreAgeSetRoundRobin.class);
	}

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreAgeSetParser());
		TokenRegistration.register(new PreAgeSetWriter());
	}

	/**
	 * @see plugin.pretokens.AbstractBasicRoundRobin#getBaseString()
	 */
	@Override
	public String getBaseString()
	{
		return "AGESET";
	}

	/**
	 * @see plugin.pretokens.AbstractBasicRoundRobin#isTypeAllowed()
	 */
	@Override
	public boolean isTypeAllowed()
	{
		return false;
	}

}
