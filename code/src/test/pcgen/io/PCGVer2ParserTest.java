/*
 * PCGVer2ParserTest.java
 * Copyright 2004 (C) Chris Ward <frugal@purplewombat.co.uk>
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
 * Created on 19-Nov-2004
 */
package pcgen.io;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 */
public class PCGVer2ParserTest extends TestCase
{
	/**
	 * Quick test suite creation - adds all methods beginning with "test"
	 * @return The Test suite
	 */
	public static Test suite()
	{
		return new TestSuite(PCGVer2ParserTest.class);
	}

	/**
	 * Basic constructor, name only.
	 * @param name The name of the test class.
	 */
	public PCGVer2ParserTest(String name)
	{
		super(name);
	}

	public void test_1045596_1() throws PCGParseException
	{
		PCGVer2Parser parser = new PCGVer2Parser(null);

		parser.parseVersionLine("VERSION:5.7.1");

		int[] version = parser.getPcgenVersion();
		String suffix = parser.getPcgenVersionSuffix();

		assertEquals(3, version.length);
		assertEquals(5, version[0]);
		assertEquals(7, version[1]);
		assertEquals(1, version[2]);
		assertEquals(null, suffix);
	}

	public void test_1045596_2() throws PCGParseException
	{
		PCGVer2Parser parser = new PCGVer2Parser(null);

		parser.parseVersionLine("VERSION:5-7-1");

		int[] version = parser.getPcgenVersion();
		String suffix = parser.getPcgenVersionSuffix();

		assertEquals(3, version.length);
		assertEquals(5, version[0]);
		assertEquals(7, version[1]);
		assertEquals(1, version[2]);
		assertEquals(null, suffix);
	}

	public void test_1045596_3() throws PCGParseException
	{
		PCGVer2Parser parser = new PCGVer2Parser(null);

		parser.parseVersionLine("VERSION:5 7 1");

		int[] version = parser.getPcgenVersion();
		String suffix = parser.getPcgenVersionSuffix();

		assertEquals(3, version.length);
		assertEquals(5, version[0]);
		assertEquals(7, version[1]);
		assertEquals(1, version[2]);
		assertEquals(null, suffix);
	}

	public void test_1045596_4() throws PCGParseException
	{
		PCGVer2Parser parser = new PCGVer2Parser(null);

		parser.parseVersionLine("VERSION:5.7.1 RC1");

		int[] version = parser.getPcgenVersion();
		String suffix = parser.getPcgenVersionSuffix();

		assertEquals(3, version.length);
		assertEquals(5, version[0]);
		assertEquals(7, version[1]);
		assertEquals(1, version[2]);
		assertEquals("RC1", suffix);
	}

	public void test_1045596_5() throws PCGParseException
	{
		PCGVer2Parser parser = new PCGVer2Parser(null);

		parser.parseVersionLine("VERSION:5.7.1.autobuild.20041119-18:00");

		int[] version = parser.getPcgenVersion();
		String suffix = parser.getPcgenVersionSuffix();

		assertEquals(3, version.length);
		assertEquals(5, version[0]);
		assertEquals(7, version[1]);
		assertEquals(1, version[2]);
		assertEquals("autobuild.20041119-18:00", suffix);
	}

	public void test_1045596_6()
	{
		PCGVer2Parser parser = new PCGVer2Parser(null);

		try
		{
			parser.parseVersionLine("5.7.1");
			fail("Should have thrown an exception");
		}
		catch (PCGParseException e)
		{
			assertEquals("Not a Version Line.", e.getMessage());
		}
	}

	public void test_1045596_7()
	{
		PCGVer2Parser parser = new PCGVer2Parser(null);

		try
		{
			parser.parseVersionLine("VERSION:5.7.1RC1");
			fail("Should have thrown an exception");
		}
		catch (PCGParseException e)
		{
			assertEquals("Invalid PCGen version.", e.getMessage());
		}
	}
}
