/*
 * PreVariableParserTest.java
 *
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
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
 * Created on 18-Dec-2003
 *
 * Current Ver: $Revision: 1.3 $
 *
 * Last Editor: $Author: karianna $
 *
 * Last Edited: $Date: 2005/09/12 11:48:36 $
 *
 */
package pcgen.persistence.lst.prereq;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;
import pcgen.core.prereq.Prerequisite;

/**
 * @author wardc
 *
 */
public class PreVariableParserTest extends TestCase
{
	public static void main(String[] args)
	{
		TestRunner.run(PreVariableParserTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreVariableParserTest.class);
	}

	/**
	 * @throws Exception
	 */
	public void testNotEqual() throws Exception
	{
		PreVariableParser parser = new PreVariableParser();

		Prerequisite prereq = parser.parse("VARNEQ","Enraged,1", false, false);

		assertEquals("<prereq kind=\"var\" key=\"Enraged\" operator=\"neq\" operand=\"1\" >\n</prereq>\n", prereq.toString());
	}
}
