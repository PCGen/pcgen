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

import pcgen.core.Globals;
import pcgen.rules.context.AbstractReferenceContext;

import plugin.lsttokens.testsupport.BuildUtilities;

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

	public void testNoCompress()
	{
		runRoundRobin("PREMULT:2,[PRE" + getBaseString() + ":NG],[PRE"
				+ getBaseString() + ":LG]");
	}

	static void createAllAlignments()
	{
		AbstractReferenceContext ref = Globals.getContext().getReferenceContext();
		ref.importObject(BuildUtilities.createAlignment("Lawful Good", "LG"));
		ref.importObject(BuildUtilities.createAlignment("Lawful Neutral", "LN"));
		ref.importObject(BuildUtilities.createAlignment("Lawful Evil", "LE"));
		ref.importObject(BuildUtilities.createAlignment("Neutral Good", "NG"));
		ref.importObject(BuildUtilities.createAlignment("True Neutral", "TN"));
		ref.importObject(BuildUtilities.createAlignment("Neutral Evil", "NE"));
		ref.importObject(BuildUtilities.createAlignment("Chaotic Good", "CG"));
		ref.importObject(BuildUtilities.createAlignment("Chaotic Neutral", "CN"));
		ref.importObject(BuildUtilities.createAlignment("Chaotic Evil", "CE"));
		ref.importObject(BuildUtilities.createAlignment("None", "NONE"));
		ref.importObject(BuildUtilities.createAlignment("Deity's", "Deity"));
	}


}
