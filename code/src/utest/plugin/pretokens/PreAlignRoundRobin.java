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
import pcgen.core.Globals;
import pcgen.rules.context.ReferenceContext;
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
		ReferenceContext ref = Globals.getContext().ref;
		ref.importObject(TestSupport.createAlignment("Lawful Good", "LG"));
		ref.importObject(TestSupport.createAlignment("Lawful Neutral", "LN"));
		ref.importObject(TestSupport.createAlignment("Lawful Evil", "LE"));
		ref.importObject(TestSupport.createAlignment("Neutral Good", "NG"));
		ref.importObject(TestSupport.createAlignment("True Neutral", "TN"));
		ref.importObject(TestSupport.createAlignment("Neutral Evil", "NE"));
		ref.importObject(TestSupport.createAlignment("Chaotic Good", "CG"));
		ref.importObject(TestSupport.createAlignment("Chaotic Neutral", "CN"));
		ref.importObject(TestSupport.createAlignment("Chaotic Evil", "CE"));
		ref.importObject(TestSupport.createAlignment("None", "NONE"));
		ref.importObject(TestSupport.createAlignment("Deity's", "Deity"));
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