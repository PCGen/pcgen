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
import plugin.pretokens.parser.PreVariableParser;
import plugin.pretokens.writer.PreVariableWriter;

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
public class PreVarRoundRobin extends AbstractComparatorRoundRobin
{
	public static void main(String args[])
	{
		TestRunner.run(PreVarRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreVarRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreVariableParser());
		TokenRegistration.register(new PreVariableWriter());
	}

	public void testSimpleFormula()
	{
		runRoundRobin("INT,5");
	}

	public void testSimpleCompare()
	{
		runRoundRobin("abs(STR),4");
	}

	@Override
	public String getBaseString()
	{
		return "VAR";
	}

	public void testMultipleCompare()
	{
		runRoundRobin("abs(STR),4,abs(INT),3");
	}

	public void testDiffCompare()
	{
		runSimpleRoundRobin(
				"PREMULT:2,[PREVARGT:abs(STR),4],[PREVARLT:abs(INT),3]",
				"PREMULT:2,[PREVARGT:abs(STR),4],[PREVARLT:abs(INT),3]");
	}

	public void testCloseCompare()
	{
		runSimpleRoundRobin(
				"PREMULT:2,[PREVARGT:abs(STR),4],[PREVARGTEQ:abs(INT),3]",
				"PREMULT:2,[PREVARGT:abs(STR),4],[PREVARGTEQ:abs(INT),3]");
	}

	public void testCountOne()
	{
		runSimpleRoundRobin(
				"PREMULT:1,[PREVARGT:abs(STR),4],[PREVARGT:abs(INT),3]",
				"PREMULT:1,[PREVARGT:abs(STR),4],[PREVARGT:abs(INT),3]");
	}

	public void testFunConsolidation()
	{
		runSimpleRoundRobin(
				"PREMULT:2,[PREVARGT:abs(STR),4],[!PREVARLTEQ:abs(INT),3]",
				"PREVARGT:abs(STR),4,abs(INT),3");
	}

	@Override
	public boolean isBaseAllowed()
	{
		return true;
	}

}