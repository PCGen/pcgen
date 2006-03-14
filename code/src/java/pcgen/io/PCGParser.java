/*
 * PCGParser.java
 * Copyright 2002 (C) Thomas Behr <ravenlock@gmx.de>
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
 *
 * Created on September 07, 2002, 11:30 AM
 */
package pcgen.io;

import java.util.List;

/**
 * <code>PCGParser</code><br>
 * @author Thomas Behr 07-09-02
 * @version $Revision$
 */
interface PCGParser
{
	String s_CHECKLOADEDCAMPAIGNS = "Check loaded campaigns.";

	/**
	 * Selector
	 *
	 * <br>author: Thomas Behr 07-09-02
	 *
	 * @return a list of warning messages
	 */
	List getWarnings();

	/**
	 * parse a String in PCG format
	 *
	 * <br>author: Thomas Behr 07-09-02
	 *
	 * @param lines   the String to parse
	 * @throws PCGParseException
	 */
	void parsePCG(String[] lines) throws PCGParseException;
}
