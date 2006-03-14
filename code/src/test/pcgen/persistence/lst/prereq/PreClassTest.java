/*
 * PreClassTest.java
 *
 * Copyright 2004 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 22-Jan-2004
 *
 * Current Ver: $Revision$
 * 
 * Last Editor: $Author$
 * 
 * Last Edited: $Date$
 *
 */
package pcgen.persistence.lst.prereq;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;
import pcgen.core.prereq.Prerequisite;
import plugin.pretokens.parser.PreClassParser;

/**
 * @author wardc
 *
 */
public class PreClassTest extends TestCase
{
	public static void main(String[] args)
	{
		TestRunner.run(PreClassTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreClassTest.class);
	}
	

	public void testNoClassLevels() throws Exception
	{
		PreClassParser parser = new PreClassParser();
		Prerequisite prereq = parser.parse("class", "1,Monk=1", true, false);
		
		assertEquals("<prereq kind=\"class\" key=\"Monk\" operator=\"lt\" operand=\"1\" >\n"+
				"</prereq>\n", prereq.toString());
		
	}
}
