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
public abstract class AbstractAlignRoundRobin extends AbstractPreRoundRobin
{
	public abstract String getBaseString();

	public void testSimple()
	{
		runRoundRobin("PRE" + getBaseString() + ":LG");
	}

	public void testMultiple()
	{
		runRoundRobin("PRE" + getBaseString() + ":LG,LN,LE");
	}

	public void testNumber()
	{
		runSimpleRoundRobin("PRE" + getBaseString() + ":3", "PRE"
				+ getBaseString() + ":NG");
		runSimpleRoundRobin("!PRE" + getBaseString() + ":3", "!PRE"
				+ getBaseString() + ":NG");
	}

	public void testNumberMultiple()
	{
		runSimpleRoundRobin("PRE" + getBaseString() + ":3,4,5", "PRE"
				+ getBaseString() + ":NG,TN,NE");
		runSimpleRoundRobin("!PRE" + getBaseString() + ":3,4,5", "!PRE"
				+ getBaseString() + ":NG,TN,NE");
	}

	public void testNoCompress()
	{
		runRoundRobin("PREMULT:2,[PRE" + getBaseString() + ":NG],[PRE"
				+ getBaseString() + ":LG]");
	}

}