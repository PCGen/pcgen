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

import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterFactory;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface;
import pcgen.persistence.lst.prereq.PreParserFactory;

import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreAbilityParser;
import plugin.pretokens.writer.PreAbilityWriter;

public class PreAbilityRoundRobin extends AbstractBasicRoundRobin
{

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreAbilityParser());
		TokenRegistration.register(new PreAbilityWriter());
	}

	@Override
	public String getBaseString()
	{
		return "ABILITY";
	}

	@Override
	public boolean isTypeAllowed()
	{
		return true;
	}

	@Override
	public String getPrefix()
	{
		return "CATEGORY=Mutation,";
	}

	public static String getAltPrefix()
	{
		return "CATEGORY=Special Ability,";
	}

	public static String getAnyPrefix()
	{
		return "CATEGORY=ANY,";
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

	public void testCombineSubCheckMult()
	{
		// runSimpleRoundRobin("PREMULT:2,[!PRE" + getBaseString() + ":1,"
		// + "CHECKMULT," + getPrefix() + "Foo],[!PRE" + getBaseString()
		// + ":1," + "CHECKMULT," + getPrefix() + "Spot]", "!PRE"
		// + getBaseString() + ":1," + "CHECKMULT," + getPrefix()
		// + "Foo,Spot");
		String original = "PREMULT:2,[!PRE" + getBaseString() + ":1,"
				+ "CHECKMULT," + getPrefix() + "Foo],[!PRE" + getBaseString()
				+ ":1," + "CHECKMULT," + getPrefix() + "Spot]";
		String consolidatedPre = "!PRE" + getBaseString() + ":1,"
				+ "CHECKMULT," + getPrefix() + "Foo,Spot";
		try
		{
			Prerequisite p = PreParserFactory.getInstance().parse(original);
			PrerequisiteWriterInterface writer = PrerequisiteWriterFactory
					.getInstance().getWriter(p.getKind());
			if (writer == null)
			{
				fail("Could not find Writer for: " + p.getKind());
			}
			StringWriter w = new StringWriter();
			writer.write(w, p);
			boolean consolidated = w.toString().equals(consolidatedPre);
			boolean separate = w.toString().equals(original);
			assertTrue(consolidated || separate);
		}
		catch (PersistenceLayerException e)
		{
			fail(e.getLocalizedMessage());
		}
	}

	public void testCombineSubNegativeCheckMult()
	{
		// runSimpleRoundRobin("!PREMULT:2,[!PRE" + getBaseString() + ":1,"
		// + "CHECKMULT," + getPrefix() + "Foo],[!PRE" + getBaseString()
		// + ":1," + "CHECKMULT," + getPrefix() + "Spot]", "PRE"
		// + getBaseString() + ":1," + "CHECKMULT," + getPrefix()
		// + "Foo,Spot");
		String original = "!PREMULT:2,[!PRE" + getBaseString() + ":1,"
				+ "CHECKMULT," + getPrefix() + "Foo],[!PRE" + getBaseString()
				+ ":1," + "CHECKMULT," + getPrefix() + "Spot]";
		String consolidatedPre = "PRE" + getBaseString() + ":1," + "CHECKMULT,"
				+ getPrefix() + "Foo,Spot";
		try
		{
			Prerequisite p = PreParserFactory.getInstance().parse(original);
			PrerequisiteWriterInterface writer = PrerequisiteWriterFactory
					.getInstance().getWriter(p.getKind());
			if (writer == null)
			{
				fail("Could not find Writer for: " + p.getKind());
			}
			StringWriter w = new StringWriter();
			writer.write(w, p);
			boolean consolidated = w.toString().equals(consolidatedPre);
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

	public void testNoCombineSubNegativeAltCategory()
	{
		runRoundRobin("PREMULT:2,[!PRE" + getBaseString() + ":1," + getPrefix()
				+ "Foo],[!PRE" + getBaseString() + ":1," + getAltPrefix()
				+ "Spot]");
	}

	public void testNoCombineCheckMultSubNegative()
	{
		runRoundRobin("PREMULT:2,[!PRE" + getBaseString() + ":1," + getPrefix()
				+ "Foo],[!PRE" + getBaseString() + ":1,CHECKMULT,"
				+ getPrefix() + "Spot]");
	}

	public void testBasicAnyCategory()
	{
		runRoundRobin("PRE" + getBaseString() + ":1," + getAnyPrefix() + "Foo");
	}

	public void testMultipleAnyCategory()
	{
		runRoundRobin("PRE" + getBaseString() + ":1," + getAnyPrefix()
				+ "Spot,Listen");
	}

	public void testNoCombineSubAnyCategory()
	{
		runRoundRobin("PREMULT:1,[PRE" + getBaseString() + ":1,"
				+ getAnyPrefix() + "Foo,Bar],[PRE" + getBaseString() + ":2,"
				+ getAnyPrefix() + "Spot,Listen]");
	}

	public void testNoCombineSubNegativeAnyCategory()
	{
		runRoundRobin("PREMULT:1,[!PRE" + getBaseString() + ":1,"
				+ getAnyPrefix() + "Foo],[!PRE" + getBaseString() + ":1,"
				+ getAnyPrefix() + "Spot]");
	}

	public void testCombineSubAnyCategory()
	{
		// runSimpleRoundRobin("PREMULT:2,[!PRE" + getBaseString() + ":1,"
		// + getAnyPrefix() + "Foo],[!PRE" + getBaseString() + ":1,"
		// + getAnyPrefix() + "Spot]", "!PRE" + getBaseString() + ":1,"
		// + getAnyPrefix() + "Foo,Spot");
		String original = "PREMULT:2,[!PRE" + getBaseString() + ":1,"
				+ getAnyPrefix() + "Foo],[!PRE" + getBaseString() + ":1,"
				+ getAnyPrefix() + "Spot]";
		String consolidatedPre = "!PRE" + getBaseString() + ":1,"
				+ getAnyPrefix() + "Foo,Spot";
		try
		{
			Prerequisite p = PreParserFactory.getInstance().parse(original);
			PrerequisiteWriterInterface writer = PrerequisiteWriterFactory
					.getInstance().getWriter(p.getKind());
			if (writer == null)
			{
				fail("Could not find Writer for: " + p.getKind());
			}
			StringWriter w = new StringWriter();
			writer.write(w, p);
			boolean consolidated = w.toString().equals(consolidatedPre);
			boolean separate = w.toString().equals(original);
			assertTrue(consolidated || separate);
		}
		catch (PersistenceLayerException e)
		{
			fail(e.getLocalizedMessage());
		}
	}

	public void testCombineSubNegativeAnyCategory()
	{
		// runSimpleRoundRobin("!PREMULT:2,[!PRE" + getBaseString() + ":1,"
		// + getAnyPrefix() + "Foo],[!PRE" + getBaseString() + ":1,"
		// + getAnyPrefix() + "Spot]", "PRE" + getBaseString() + ":1,"
		// + getAnyPrefix() + "Foo,Spot");
		String original = "!PREMULT:2,[!PRE" + getBaseString() + ":1,"
				+ getAnyPrefix() + "Foo],[!PRE" + getBaseString() + ":1,"
				+ getAnyPrefix() + "Spot]";
		String consolidatedPre = "PRE" + getBaseString() + ":1,"
				+ getAnyPrefix() + "Foo,Spot";
		try
		{
			Prerequisite p = PreParserFactory.getInstance().parse(original);
			PrerequisiteWriterInterface writer = PrerequisiteWriterFactory
					.getInstance().getWriter(p.getKind());
			if (writer == null)
			{
				fail("Could not find Writer for: " + p.getKind());
			}
			StringWriter w = new StringWriter();
			writer.write(w, p);
			boolean consolidated = w.toString().equals(consolidatedPre);
			boolean separate = w.toString().equals(original);
			assertTrue(consolidated || separate);
		}
		catch (PersistenceLayerException e)
		{
			fail(e.getLocalizedMessage());
		}
	}

	public void testNoCombineMultAnyCategory()
	{
		runRoundRobin("PREMULT:2,[PRE" + getBaseString() + ":1,"
				+ getAnyPrefix() + "Foo,Bar],[PRE" + getBaseString() + ":1,"
				+ getAnyPrefix() + "Spot,Listen]");
	}

	public void testMultipleCountAnyCategory()
	{
		runRoundRobin("PRE" + getBaseString() + ":2," + getAnyPrefix()
				+ "Foo,Bar");
	}

	public void testTypeAnyCategory()
	{
		if (isTypeAllowed())
		{
			runRoundRobin("PRE" + getBaseString() + ":1," + getAnyPrefix()
					+ "TYPE=Foo");
		}
	}

	public void testTypeMultipleCountAnyCategory()
	{
		if (isTypeAllowed())
		{
			runRoundRobin("PRE" + getBaseString() + ":2," + getAnyPrefix()
					+ "TYPE=Foo");
		}
	}

	public void testMultipleTypeAnyCategory()
	{
		if (isTypeAllowed())
		{
			runRoundRobin("PRE" + getBaseString() + ":1," + getAnyPrefix()
					+ "TYPE=Bar,TYPE=Foo");
		}
	}

	public void testTypeAndAnyCategory()
	{
		if (isTypeAllowed())
		{
			runRoundRobin("PRE" + getBaseString() + ":1," + getAnyPrefix()
					+ "TYPE=Foo.Bar");
		}
	}

	public void testComplexAnyCategory()
	{
		if (isTypeAllowed())
		{
			runRoundRobin("PRE" + getBaseString() + ":3," + getAnyPrefix()
					+ "Foo,TYPE=Bar");
		}
	}

	@Override
	public void testCombineSub()
	{
		String original = "PREMULT:2,[!PRE" + getBaseString() + ":1,"
				+ getPrefix() + "Foo],[!PRE" + getBaseString() + ":1,"
				+ getPrefix() + "Spot]";
		String consolidatedPre = "!PRE" + getBaseString() + ":1," + getPrefix()
				+ "Foo,Spot";
		try
		{
			Prerequisite p = PreParserFactory.getInstance().parse(original);
			PrerequisiteWriterInterface writer = PrerequisiteWriterFactory
					.getInstance().getWriter(p.getKind());
			if (writer == null)
			{
				fail("Could not find Writer for: " + p.getKind());
			}
			StringWriter w = new StringWriter();
			writer.write(w, p);
			boolean consolidated = w.toString().equals(consolidatedPre);
			boolean separate = w.toString().equals(original);
			assertTrue(consolidated || separate);
		}
		catch (PersistenceLayerException e)
		{
			fail(e.getLocalizedMessage());
		}
	}

	@Override
	public void testCombineSubNegative()
	{
		String original = "!PREMULT:2,[!PRE" + getBaseString() + ":1,"
				+ getPrefix() + "Foo],[!PRE" + getBaseString() + ":1,"
				+ getPrefix() + "Spot]";
		String consolidatedPre = "PRE" + getBaseString() + ":1," + getPrefix()
				+ "Foo,Spot";
		try
		{
			Prerequisite p = PreParserFactory.getInstance().parse(original);
			PrerequisiteWriterInterface writer = PrerequisiteWriterFactory
					.getInstance().getWriter(p.getKind());
			if (writer == null)
			{
				fail("Could not find Writer for: " + p.getKind());
			}
			StringWriter w = new StringWriter();
			writer.write(w, p);
			boolean consolidated = w.toString().equals(consolidatedPre);
			boolean separate = w.toString().equals(original);
			assertTrue(consolidated || separate);
		}
		catch (PersistenceLayerException e)
		{
			fail(e.getLocalizedMessage());
		}
	}

	@Override
	public void testCombineSubSub()
	{
		String original = "PREMULT:2,[!PRE" + getBaseString() + ":1,"
				+ getPrefix() + "Foo (Bar)],[!PRE" + getBaseString() + ":1,"
				+ getPrefix() + "Spot (Check)]";
		String consolidatedPre = "!PRE" + getBaseString() + ":1," + getPrefix()
				+ "Foo (Bar),Spot (Check)";
		try
		{
			Prerequisite p = PreParserFactory.getInstance().parse(original);
			PrerequisiteWriterInterface writer = PrerequisiteWriterFactory
					.getInstance().getWriter(p.getKind());
			if (writer == null)
			{
				fail("Could not find Writer for: " + p.getKind());
			}
			StringWriter w = new StringWriter();
			writer.write(w, p);
			boolean consolidated = w.toString().equals(consolidatedPre);
			boolean separate = w.toString().equals(original);
			assertTrue(consolidated || separate);
		}
		catch (PersistenceLayerException e)
		{
			fail(e.getLocalizedMessage());
		}
	}

	@Override
	public void testCombineSubNegativeSub()
	{
		String original = "!PREMULT:2,[!PRE" + getBaseString() + ":1,"
				+ getPrefix() + "Foo (Bar)],[!PRE" + getBaseString() + ":1,"
				+ getPrefix() + "Spot (Check)]";
		String consolidatedPre = "PRE" + getBaseString() + ":1," + getPrefix()
				+ "Foo (Bar),Spot (Check)";
		try
		{
			Prerequisite p = PreParserFactory.getInstance().parse(original);
			PrerequisiteWriterInterface writer = PrerequisiteWriterFactory
					.getInstance().getWriter(p.getKind());
			if (writer == null)
			{
				fail("Could not find Writer for: " + p.getKind());
			}
			StringWriter w = new StringWriter();
			writer.write(w, p);
			boolean consolidated = w.toString().equals(consolidatedPre);
			boolean separate = w.toString().equals(original);
			assertTrue(consolidated || separate);
		}
		catch (PersistenceLayerException e)
		{
			fail(e.getLocalizedMessage());
		}
	}

}
