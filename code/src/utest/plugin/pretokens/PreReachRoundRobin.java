/*
 * PreAbilityParserTest.java
 * Copyright 2007 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on January 23, 2006
 *
 * Current Ver: $Revision: 1777 $
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2006-12-17 15:36:01 +1100 (Sun, 17 Dec 2006) $
 *
 */
package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.pretokens.parser.PreReachParser;
import plugin.pretokens.writer.PreReachWriter;

/**
 * <code>PreAbilityParserTest</code> tests the function of the PREABILITY
 * parser.
 * 
 * Last Editor: $Author: jdempsey $ Last Edited: $Date: 2006-12-17 15:36:01
 * +1100 (Sun, 17 Dec 2006) $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 1777 $
 */
public class PreReachRoundRobin extends AbstractComparatorRoundRobin
{
	public static void main(String args[])
	{
		TestRunner.run(PreReachRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreReachRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreReachParser());
		TokenRegistration.register(new PreReachWriter());
	}

	public void testSimpleInteger()
	{
		runRoundRobin("1");
	}

	@Override
	public String getBaseString()
	{
		return "REACH";
	}

	@Override
	public boolean isBaseAllowed()
	{
		return true;
	}

}