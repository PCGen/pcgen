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

import java.io.StringWriter;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterFactory;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface;
import pcgen.persistence.lst.prereq.PreParserFactory;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreFeatParser;
import plugin.pretokens.writer.PreFeatWriter;

public class PreFeatRoundRobin extends AbstractBasicRoundRobin
{
	public static void main(String args[])
	{
		TestRunner.run(PreFeatRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreFeatRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreFeatParser());
		TokenRegistration.register(new PreFeatWriter());
	}

	@Override
	public String getBaseString()
	{
		return "FEAT";
	}

	@Override
	public boolean isTypeAllowed()
	{
		return true;
	}

	public void testBasicCheckMult()
	{
		runRoundRobin("PRE" + getBaseString() + ":1," + "CHECKMULT,"
				+ getPrefix() + "Foo");
	}

	public void testMultipleCheckMult()
	{
		runRoundRobin("PRE" + getBaseString() + ":1," + "CHECKMULT,"
				+ getPrefix() + "Spot,Listen");
	}

	public void testNoCombineSubCheckMult()
	{
		runRoundRobin("PREMULT:1,[PRE" + getBaseString() + ":1," + "CHECKMULT,"
				+ getPrefix() + "Foo,Bar],[PRE" + getBaseString() + ":2,"
				+ "CHECKMULT," + getPrefix() + "Spot,Listen]");
	}

	public void testNoCombineSubNegativeCheckMult()
	{
		runRoundRobin("PREMULT:1,[!PRE" + getBaseString() + ":1,"
				+ "CHECKMULT," + getPrefix() + "Foo],[!PRE" + getBaseString()
				+ ":1," + "CHECKMULT," + getPrefix() + "Spot]");
	}

	public void testPossiblyCombineSubCheckMult()
	{
		// runSimpleRoundRobin("PREMULT:2,[!PRE" + getBaseString() + ":1,"
		// + "CHECKMULT," + getPrefix() + "Foo],[!PRE" + getBaseString()
		// + ":1," + "CHECKMULT," + getPrefix() + "Spot]", "!PRE"
		// + getBaseString() + ":1," + "CHECKMULT," + getPrefix()
		// + "Foo,Spot");
		try
		{
			String original = "PREMULT:2,[!PRE" + getBaseString() + ":1,"
					+ "CHECKMULT," + getPrefix() + "Foo],[!PRE"
					+ getBaseString() + ":1," + "CHECKMULT," + getPrefix()
					+ "Spot]";
			Prerequisite p = PreParserFactory.getInstance().parse(original);
			PrerequisiteWriterInterface writer = PrerequisiteWriterFactory
					.getInstance().getWriter(p.getKind());
			if (writer == null)
			{
				fail("Could not find Writer for: " + p.getKind());
			}
			StringWriter w = new StringWriter();
			writer.write(w, p);
			boolean consolidated = w.toString().equals(
					"!PRE" + getBaseString() + ":1," + "CHECKMULT,"
							+ getPrefix() + "Foo,Spot");
			boolean separate = w.toString().equals(original);
			assertTrue(consolidated || separate);
		}
		catch (PersistenceLayerException e)
		{
			fail(e.getLocalizedMessage());
		}
	}

	public void testPossiblyCombineSubNegativeCheckMult()
	{
		// runSimpleRoundRobin("!PREMULT:2,[!PRE" + getBaseString() + ":1,"
		// + "CHECKMULT," + getPrefix() + "Foo],[!PRE" + getBaseString()
		// + ":1," + "CHECKMULT," + getPrefix() + "Spot]", "PRE"
		// + getBaseString() + ":1," + "CHECKMULT," + getPrefix()
		// + "Foo,Spot");
		try
		{
			String original = "!PREMULT:2,[!PRE" + getBaseString() + ":1,"
					+ "CHECKMULT," + getPrefix() + "Foo],[!PRE"
					+ getBaseString() + ":1," + "CHECKMULT," + getPrefix()
					+ "Spot]";
			Prerequisite p = PreParserFactory.getInstance().parse(original);
			PrerequisiteWriterInterface writer = PrerequisiteWriterFactory
					.getInstance().getWriter(p.getKind());
			if (writer == null)
			{
				fail("Could not find Writer for: " + p.getKind());
			}
			StringWriter w = new StringWriter();
			writer.write(w, p);
			boolean consolidated = w.toString().equals(
					"PRE" + getBaseString() + ":1," + "CHECKMULT,"
							+ getPrefix() + "Foo,Spot");
			boolean separate = w.toString().equals(original);
			assertTrue(consolidated || separate);
		}
		catch (PersistenceLayerException e)
		{
			fail(e.getLocalizedMessage());
		}
	}

	public void testNoCombineMultCheckMult()
	{
		runRoundRobin("PREMULT:2,[PRE" + getBaseString() + ":1," + "CHECKMULT,"
				+ getPrefix() + "Foo,Bar],[PRE" + getBaseString() + ":1,"
				+ "CHECKMULT," + getPrefix() + "Spot,Listen]");
	}

	public void testMultipleCountCheckMult()
	{
		runRoundRobin("PRE" + getBaseString() + ":2," + "CHECKMULT,"
				+ getPrefix() + "Foo,Bar");
	}

	public void testTypeCheckMult()
	{
		if (isTypeAllowed())
		{
			runRoundRobin("PRE" + getBaseString() + ":1," + "CHECKMULT,"
					+ getPrefix() + "TYPE=Foo");
		}
	}

	public void testTypeMultipleCountCheckMult()
	{
		if (isTypeAllowed())
		{
			runRoundRobin("PRE" + getBaseString() + ":2," + "CHECKMULT,"
					+ getPrefix() + "TYPE=Foo");
		}
	}

	public void testMultipleTypeCheckMult()
	{
		if (isTypeAllowed())
		{
			runRoundRobin("PRE" + getBaseString() + ":1," + "CHECKMULT,"
					+ getPrefix() + "TYPE=Bar,TYPE=Foo");
		}
	}

	public void testTypeAndCheckMult()
	{
		if (isTypeAllowed())
		{
			runRoundRobin("PRE" + getBaseString() + ":1," + "CHECKMULT,"
					+ getPrefix() + "TYPE=Foo.Bar");
		}
	}

	public void testComplexCheckMult()
	{
		if (isTypeAllowed())
		{
			runRoundRobin("PRE" + getBaseString() + ":3," + "CHECKMULT,"
					+ getPrefix() + "Foo,TYPE=Bar");
		}
	}

	public void testNoCombineCheckMultSubNegative()
	{
		runRoundRobin("PREMULT:2,[!PRE" + getBaseString() + ":1," + getPrefix()
				+ "Foo],[!PRE" + getBaseString() + ":1,CHECKMULT,Spot]");
	}
}
