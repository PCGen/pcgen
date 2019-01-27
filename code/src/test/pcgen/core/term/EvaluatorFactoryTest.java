package pcgen.core.term;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.MovementType;
import pcgen.cdom.list.ClassSpellList;
import pcgen.core.Globals;
import pcgen.core.PCStat;
import pcgen.core.SettingsHandler;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.util.TestHelper;

import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

/**
 * Copyright (c) 2008 Andrew Wilson <nuance@users.sourceforge.net>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

public class EvaluatorFactoryTest
{

	@Test
	public void testConstructor001()
	{

		EvaluatorFactoryTest.loadAll();
		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");
		Field sF = (Field) TestHelper.findField(uClass, "BuilderStore");

		boolean ok;
		try
		{
			ok = true;
			//assertNull("", pF.get(null));
			//assertNull("", sF.get(null));

			Pattern iVP = (Pattern) pF.get(EvaluatorFactory.PC);
			// noinspection unchecked
			Map<String, TermEvaluatorBuilderPCVar> eS =
					(Map<String, TermEvaluatorBuilderPCVar>) sF.get(EvaluatorFactory.PC);

			// don't need instanceof, would throw ClassCastException
			assertNotNull("Pattern is now instantiated", iVP);
			assertNotNull("Map is now instantiated", eS);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor001", ok);
	}


	@Test
	public void testConstructor002()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor002", ok);

		String term = "ACCHECK";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor002 pattern matches for " + term, mat.find());
		assertEquals("Constructor002 pattern matches all of " + term, term, mat.group(1));

		// "ACCHECK",			   COMPLETE_ACCHECK

	}

	@Test
	public void testConstructor003()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor003", ok);

		String term = "ACHECK";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor003 pattern matches for " + term, mat.find());
		assertEquals("Constructor003 pattern matches all of " + term, term, mat.group(1));

		// "ACHECK",			   COMPLETE_ACCHECK

	}


	@Test
	public void testConstructor004()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor004", ok);

		String term = "ARMORACCHECK";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor004 pattern matches for " + term, mat.find());
		assertEquals("Constructor004 pattern matches all of " + term, term, mat.group(1));

		// "ARMORACCHECK",		   COMPLETE_ARMORACCHECK

	}


	@Test
	public void testConstructor005()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor005", ok);

		String term = "ARMORACHECK";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor005 pattern matches for " + term, mat.find());
		assertEquals("Constructor005 pattern matches all of " + term, term, mat.group(1));

		// "ARMORACHECK",		   COMPLETE_ARMORACCHECK

	}


	@Test
	public void testConstructor006()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor006", ok);

		String term = "BAB";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor006 pattern matches for " + term, mat.find());
		assertEquals("Constructor006 pattern matches all of " + term, term, mat.group(1));

		// "BAB",			   COMPLETE_BAB

	}


	@Test
	public void testConstructor007()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor007", ok);

		String term = "BASESPELLSTAT";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor007 pattern matches for " + term, mat.find());
		assertEquals("Constructor007 pattern matches all of " + term, term, mat.group(1));

		// "BASESPELLSTAT",		   COMPLETE_BASESPELLSTAT

	}


	@Test
	public void testConstructor008()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor008", ok);

		String term = "BL";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor008 pattern matches for " + term, mat.find());
		assertTrue("Constructor008 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "BL",			   START_BL

	}


	@Test
	public void testConstructor009()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor009", ok);

		String term = "BL.Wizard";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor009 pattern matches for " + term, mat.find());
		assertTrue("Constructor009 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "BL.Wizard",				 START_BL

	}


	@Test
	public void testConstructor010()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor010", ok);

		String term = "BL=Cleric";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor010 pattern matches for " + term, mat.find());
		assertTrue("Constructor010 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "BL=Cleric",				 START_BL

	}


	@Test
	public void testConstructor011()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor011", ok);

		String term = "CASTERLEVEL";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor011 pattern matches for " + term, mat.find());
		assertEquals("Constructor011 pattern matches all of " + term, term, mat.group(1));

		// "CASTERLEVEL",		   COMPLETE_CASTERLEVEL

	}


	@Test
	public void testConstructor012()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor012", ok);

		String term = "CASTERLEVEL.TOTAL";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor012 pattern matches for " + term, mat.find());
		assertEquals("Constructor012 pattern matches all of " + term, term, mat.group(1));

		// "CASTERLEVEL.TOTAL",		   COMPLETE_CASTERLEVEL

	}


	@Test
	public void testConstructor013()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor013", ok);

		String term = "CL";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor013 pattern matches for " + term, mat.find());
		assertTrue("Constructor013 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "CL",			   START_CL

	}


	@Test
	public void testConstructor014()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor014", ok);

		String term = "CL.Bard";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor014 pattern matches for " + term, mat.find());
		assertTrue("Constructor014 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "CL.Bard",			       START_CL

	}


	@Test
	public void testConstructor015()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor015", ok);

		String term = "CL;BEFORELEVEL.10";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor015 pattern matches for " + term, mat.find());
		assertTrue("Constructor015 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "CL;BEFORELEVEL.10",		     START_CL_BEFORELEVEL

	}


	@Test
	public void testConstructor016()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor016", ok);

		String term = "CL;BEFORELEVEL=15";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor016 pattern matches for " + term, mat.find());
		assertTrue("Constructor016 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "CL;BEFORELEVEL=15",		     START_CL_BEFORELEVEL

	}


	@Test
	public void testConstructor017()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor017", ok);

		String term = "CL=Rogue";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor017 pattern matches for " + term, mat.find());
		assertTrue("Constructor017 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "CL=Rogue",				START_CL

	}


	@Test
	public void testConstructor018()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor018", ok);

		String term = "CLASS.Druid";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor018 pattern matches for " + term, mat.find());
		assertTrue("Constructor018 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "CLASS.Druid",			START_CLASS

	}


	@Test
	public void testConstructor019()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor019", ok);

		String term = "CLASS=Paladin";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor019 pattern matches for " + term, mat.find());
		assertTrue("Constructor019 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "CLASS=Paladin",			  START_CLASS

	}


	@Test
	public void testConstructor020()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor020", ok);

		String term = "CLASSLEVEL.Bard";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor020 pattern matches for " + term, mat.find());
		assertTrue("Constructor020 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "CLASSLEVEL.Bard",		       START_CLASSLEVEL

	}


	@Test
	public void testConstructor021()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor021", ok);

		String term = "CLASSLEVEL=Rogue";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor021 pattern matches for " + term, mat.find());
		assertTrue("Constructor021 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "CLASSLEVEL=Rogue",			START_CLASSLEVEL

	}


	@Test
	public void testConstructor022()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor022", ok);

		String term = "COUNT[ATTACKS]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor022 pattern matches for " + term, mat.find());
		assertEquals("Constructor022 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[ATTACKS]",		   COMPLETE_COUNT_ATTACKS

	}


	@Test
	public void testConstructor023()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor023", ok);

		String term = "COUNT[CHECKS]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor023 pattern matches for " + term, mat.find());
		assertEquals("Constructor023 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[CHECKS]",		   COMPLETE_COUNT_CHECKS

	}


	@Test
	public void testConstructor024()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor024", ok);

		String term = "COUNT[CLASSES]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor024 pattern matches for " + term, mat.find());
		assertEquals("Constructor024 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[CLASSES]",		   COMPLETE_COUNT_CLASSES

	}


	@Test
	public void testConstructor025()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor025", ok);

		String term = "COUNT[CONTAINERS]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor025 pattern matches for " + term, mat.find());
		assertEquals("Constructor025 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[CONTAINERS]",		   COMPLETE_COUNT_CONTAINERS

	}


	@Test
	public void testConstructor026()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor026", ok);

		String term = "COUNT[DOMAINS]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor026 pattern matches for " + term, mat.find());
		assertEquals("Constructor026 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[DOMAINS]",		   COMPLETE_COUNT_DOMAINS

	}


	@Test
	public void testConstructor027()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor027", ok);

		String term = "COUNT[EQTYPE.MERGENONE.IS.FOO]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor027 pattern matches for " + term, mat.find());
		assertTrue("Constructor027 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "COUNT[EQTYPE.MERGENONE.IS.FOO]",		    START_COUNT_EQTYPE

	}


	@Test
	public void testConstructor028()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor028", ok);

		String term = "COUNT[EQUIPMENT.MERGENONE.NOT.FOO]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor028 pattern matches for " + term, mat.find());
		assertTrue("Constructor028 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "COUNT[EQUIPMENT.MERGENONE.NOT.FOO]",	      START_COUNT_EQUIPMENT

	}


	@Test
	public void testConstructor029()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor029", ok);

		String term = "COUNT[FEATAUTOTYPE.HIDDEN]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor029 pattern matches for " + term, mat.find());
		assertTrue("Constructor029 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "COUNT[FEATAUTOTYPE.HIDDEN]",	  START_COUNT_FEATTYPE

	}


	@Test
	public void testConstructor030()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor030", ok);

		String term = "COUNT[FEATAUTOTYPE=VISIBLE]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor030 pattern matches for " + term, mat.find());
		assertTrue("Constructor030 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "COUNT[FEATAUTOTYPE=VISIBLE]",	   START_COUNT_FEATTYPE

	}


	@Test
	public void testConstructor031()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor031", ok);

		String term = "COUNT[FEATNAME.Jack of all trades]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor031 pattern matches for " + term, mat.find());
		assertTrue("Constructor031 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "COUNT[FEATNAME.Jack of all trades]",	      START_COUNT_FEATTYPE

	}


	@Test
	public void testConstructor032()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor032", ok);

		String term = "COUNT[FEATNAME=Improved Initiative]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor032 pattern matches for " + term, mat.find());
		assertTrue("Constructor032 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "COUNT[FEATNAME=Improved Initiative]",	       START_COUNT_FEATTYPE

	}


	@Test
	public void testConstructor033()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor033", ok);

		String term = "COUNT[FEATS.ALL]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor033 pattern matches for " + term, mat.find());
		assertEquals("Constructor033 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[FEATS.ALL]",		   COMPLETE_COUNT_FEATSNATURENORMAL

	}


	@Test
	public void testConstructor034()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor034", ok);

		String term = "COUNT[FEATS.HIDDEN]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor034 pattern matches for " + term, mat.find());
		assertEquals("Constructor034 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[FEATS.HIDDEN]",	   COMPLETE_COUNT_FEATSNATURENORMAL

	}


	@Test
	public void testConstructor035()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor035", ok);

		String term = "COUNT[FEATS.VISIBLE]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor035 pattern matches for " + term, mat.find());
		assertEquals("Constructor035 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[FEATS.VISIBLE]",	   COMPLETE_COUNT_FEATSNATURENORMAL

	}


	@Test
	public void testConstructor036()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor036", ok);

		String term = "COUNT[FEATSALL.ALL]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor036 pattern matches for " + term, mat.find());
		assertEquals("Constructor036 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[FEATSALL.ALL]",	   COMPLETE_COUNT_FEATSNATUREALL

	}


	@Test
	public void testConstructor037()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor037", ok);

		String term = "COUNT[FEATSALL.HIDDEN]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor037 pattern matches for " + term, mat.find());
		assertEquals("Constructor037 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[FEATSALL.HIDDEN]",	   COMPLETE_COUNT_FEATSNATUREALL

	}


	@Test
	public void testConstructor038()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor038", ok);

		String term = "COUNT[FEATSALL.VISIBLE]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor038 pattern matches for " + term, mat.find());
		assertEquals("Constructor038 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[FEATSALL.VISIBLE]",	   COMPLETE_COUNT_FEATSNATUREALL

	}


	@Test
	public void testConstructor039()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor039", ok);

		String term = "COUNT[FEATSALL]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor039 pattern matches for " + term, mat.find());
		assertEquals("Constructor039 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[FEATSALL]",		   COMPLETE_COUNT_FEATSNATUREALL

	}


	@Test
	public void testConstructor040()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor040", ok);

		String term = "COUNT[FEATSAUTO.ALL]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor040 pattern matches for " + term, mat.find());
		assertEquals("Constructor040 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[FEATSAUTO.ALL]",	   COMPLETE_COUNT_FEATSNATUREAUTO

	}


	@Test
	public void testConstructor041()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor041", ok);

		String term = "COUNT[FEATSAUTO.HIDDEN]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor041 pattern matches for " + term, mat.find());
		assertEquals("Constructor041 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[FEATSAUTO.HIDDEN]",	   COMPLETE_COUNT_FEATSNATUREAUTO

	}


	@Test
	public void testConstructor042()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor042", ok);

		String term = "COUNT[FEATSAUTO.VISIBLE]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor042 pattern matches for " + term, mat.find());
		assertEquals("Constructor042 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[FEATSAUTO.VISIBLE]",	   COMPLETE_COUNT_FEATSNATUREAUTO

	}


	@Test
	public void testConstructor043()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor043", ok);

		String term = "COUNT[FEATSAUTO]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor043 pattern matches for " + term, mat.find());
		assertEquals("Constructor043 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[FEATSAUTO]",		   COMPLETE_COUNT_FEATSNATUREAUTO

	}


	@Test
	public void testConstructor044()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor044", ok);

		String term = "COUNT[FEATS]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor044 pattern matches for " + term, mat.find());
		assertEquals("Constructor044 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[FEATS]",		   COMPLETE_COUNT_FEATSNATURENORMAL

	}


	@Test
	public void testConstructor045()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor045", ok);

		String term = "COUNT[FEATTYPE.BAR]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor045 pattern matches for " + term, mat.find());
		assertTrue("Constructor045 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "COUNT[FEATTYPE.BAR]",	       START_COUNT_FEATTYPE

	}


	@Test
	public void testConstructor046()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor046", ok);

		String term = "COUNT[FEATTYPE.BAZ]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor046 pattern matches for " + term, mat.find());
		assertTrue("Constructor046 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "COUNT[FEATTYPE.BAZ]",	       START_COUNT_FEATTYPE

	}


	@Test
	public void testConstructor047()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor047", ok);

		String term = "COUNT[FOLLOWERS]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor047 pattern matches for " + term, mat.find());
		assertEquals("Constructor047 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[FOLLOWERS]",		   COMPLETE_COUNT_FOLLOWERS

	}


	@Test
	public void testConstructor048()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor048", ok);

		String term = "COUNT[FOLLOWERTYPE.MOO]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor048 pattern matches for " + term, mat.find());
		assertTrue("Constructor048 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "COUNT[FOLLOWERTYPE.MOO]",	       START_COUNT_FOLLOWERTYPE

	}


	@Test
	public void testConstructor049()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor049", ok);

		String term = "COUNT[LANGUAGES]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor049 pattern matches for " + term, mat.find());
		assertEquals("Constructor049 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[LANGUAGES]",		   COMPLETE_COUNT_LANGUAGES

	}


	@Test
	public void testConstructor050()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor050", ok);

		String term = "COUNT[MISC.COMPANIONS]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor050 pattern matches for " + term, mat.find());
		assertEquals("Constructor050 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[MISC.COMPANIONS]",	   COMPLETE_COUNT_MISC_COMPANIONS

	}


	@Test
	public void testConstructor051()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor051", ok);

		String term = "COUNT[MISC.FUNDS]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor051 pattern matches for " + term, mat.find());
		assertEquals("Constructor051 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[MISC.FUNDS]",		   COMPLETE_COUNT_MISC_FUNDS

	}


	@Test
	public void testConstructor052()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor052", ok);

		String term = "COUNT[MISC.MAGIC]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor052 pattern matches for " + term, mat.find());
		assertEquals("Constructor052 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[MISC.MAGIC]",		   COMPLETE_COUNT_MISC_MAGIC

	}


	@Test
	public void testConstructor053()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor053", ok);

		String term = "COUNT[MOVE]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor053 pattern matches for " + term, mat.find());
		assertEquals("Constructor053 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[MOVE]",		   COMPLETE_COUNT_MOVE

	}


	@Test
	public void testConstructor054()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor054", ok);

		String term = "COUNT[NOTES]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor054 pattern matches for " + term, mat.find());
		assertEquals("Constructor054 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[NOTES]",		   COMPLETE_COUNT_NOTES

	}


	@Test
	public void testConstructor055()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor055", ok);

		String term = "COUNT[RACESUBTYPES]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor055 pattern matches for " + term, mat.find());
		assertEquals("Constructor055 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[RACESUBTYPES]",	   COMPLETE_COUNT_RACESUBTYPES

	}


	@Test
	public void testConstructor056()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor056", ok);

		String term = "COUNT[SA]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor056 pattern matches for " + term, mat.find());
		assertEquals("Constructor056 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[SA]",			   COMPLETE_COUNT_SA

	}


	@Test
	public void testConstructor057()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor057", ok);

		String term = "COUNT[SKILLS]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor057 pattern matches for " + term, mat.find());
		assertEquals("Constructor057 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[SKILLS]",		   COMPLETE_COUNT_SKILLS

	}


	@Test
	public void testConstructor058()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor058", ok);

		String term = "COUNT[SKILLTYPE.KNOWLEDGE]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor058 pattern matches for " + term, mat.find());
		assertTrue("Constructor058 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "COUNT[SKILLTYPE.KNOWLEDGE]",	     START_COUNT_SKILLTYPE

	}


	@Test
	public void testConstructor059()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor059", ok);

		String term = "COUNT[SKILLTYPE=PERFORM]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor059 pattern matches for " + term, mat.find());
		assertTrue("Constructor059 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "COUNT[SKILLTYPE=PERFORM]",		   START_COUNT_SKILLTYPE

	}


	@Test
	public void testConstructor060()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor060", ok);

		String term = "COUNT[SPELLBOOKS]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor060 pattern matches for " + term, mat.find());
		assertTrue("Constructor060 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "COUNT[SPELLBOOKS]",		    START_COUNT_SPELLBOOKS

	}


	@Test
	public void testConstructor061()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor061", ok);

		String term = "COUNT[SPELLCLASSES]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor061 pattern matches for " + term, mat.find());
		assertEquals("Constructor061 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[SPELLCLASSES]",	   COMPLETE_COUNT_SPELLCLASSES

	}


	@Test
	public void testConstructor062()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor062", ok);

		String term = "COUNT[SPELLRACE]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor062 pattern matches for " + term, mat.find());
		assertEquals("Constructor062 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[SPELLRACE]",		   COMPLETE_COUNT_SPELLRACE

	}


	@Test
	public void testConstructor063()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor063", ok);

		String term = "COUNT[SPELLSINBOOK]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor063 pattern matches for " + term, mat.find());
		assertTrue("Constructor063 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "COUNT[SPELLSINBOOK]",	    START_COUNT_SPELLSINBOOK

	}


	@Test
	public void testConstructor064()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor064", ok);

		String term = "COUNT[SPELLSKNOWN]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor064 pattern matches for " + term, mat.find());
		assertTrue("Constructor064 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "COUNT[SPELLSKNOWN]",	    START_COUNT_SPELLSKNOWN

	}


	@Test
	public void testConstructor065()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor065", ok);

		String term = "COUNT[SPELLSLEVELSINBOOK]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor065 pattern matches for " + term, mat.find());
		assertTrue("Constructor065 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "COUNT[SPELLSLEVELSINBOOK]",     START_COUNT_SPELLSLEVELSINBOOK

	}


	@Test
	public void testConstructor066()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor066", ok);

		String term = "COUNT[SPELLTIMES]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor066 pattern matches for " + term, mat.find());
		assertTrue("Constructor066 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "COUNT[SPELLTIMES]",		    START_COUNT_SPELLTIMES

	}


	@Test
	public void testConstructor067()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor067", ok);

		String term = "COUNT[STATS]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor067 pattern matches for " + term, mat.find());
		assertEquals("Constructor067 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[STATS]",		   COMPLETE_COUNT_STATS

	}


	@Test
	public void testConstructor068()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor068", ok);

		String term = "COUNT[TEMPBONUSNAMES]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor068 pattern matches for " + term, mat.find());
		assertEquals("Constructor068 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[TEMPBONUSNAMES]",	   COMPLETE_COUNT_TEMPBONUSNAMES

	}


	@Test
	public void testConstructor069()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor069", ok);

		String term = "COUNT[TEMPLATES]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor069 pattern matches for " + term, mat.find());
		assertEquals("Constructor069 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[TEMPLATES]",		   COMPLETE_COUNT_TEMPLATES

	}


	@Test
	public void testConstructor070()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor070", ok);

		String term = "COUNT[VFEATS.ALL]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor070 pattern matches for " + term, mat.find());
		assertEquals("Constructor070 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[VFEATS.ALL]",		   COMPLETE_COUNT_FEATSNATUREVIRTUAL

	}


	@Test
	public void testConstructor071()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor071", ok);

		String term = "COUNT[VFEATS.HIDDEN]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor071 pattern matches for " + term, mat.find());
		assertEquals("Constructor071 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[VFEATS.HIDDEN]",	   COMPLETE_COUNT_FEATSNATUREVIRTUAL

	}


	@Test
	public void testConstructor072()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor072", ok);

		String term = "COUNT[VFEATS.VISIBLE]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor072 pattern matches for " + term, mat.find());
		assertEquals("Constructor072 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[VFEATS.VISIBLE]",	   COMPLETE_COUNT_FEATSNATUREVIRTUAL

	}


	@Test
	public void testConstructor073()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor073", ok);

		String term = "COUNT[VFEATS]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor073 pattern matches for " + term, mat.find());
		assertEquals("Constructor073 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[VFEATS]",		   COMPLETE_COUNT_FEATSNATUREVIRTUAL

	}


	@Test
	public void testConstructor074()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor074", ok);

		String term = "COUNT[VFEATTYPE.HIDDEN]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor074 pattern matches for " + term, mat.find());
		assertTrue("Constructor074 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "COUNT[VFEATTYPE.HIDDEN]",		  START_COUNT_FEATTYPE

	}


	@Test
	public void testConstructor075()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor075", ok);

		String term = "COUNT[VFEATTYPE=ALL]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor075 pattern matches for " + term, mat.find());
		assertTrue("Constructor075 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "COUNT[VFEATTYPE=ALL]",	       START_COUNT_FEATTYPE

	}


	@Test
	public void testConstructor076()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor076", ok);

		String term = "COUNT[VISIBLETEMPLATES]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor076 pattern matches for " + term, mat.find());
		assertEquals("Constructor076 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[VISIBLETEMPLATES]",	   COMPLETE_COUNT_VISIBLETEMPLATES

	}


	@Test
	public void testConstructor077()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor077", ok);

		String term = "COUNT[VISION]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor077 pattern matches for " + term, mat.find());
		assertEquals("Constructor077 pattern matches all of " + term, term, mat.group(1));

		// "COUNT[VISION]",		   COMPLETE_COUNT_VISION

	}


	@Test
	public void testConstructor078()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor078", ok);

		String term = "ENCUMBERANCE";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor078 pattern matches for " + term, mat.find());
		assertEquals("Constructor078 pattern matches all of " + term, term, mat.group(1));

		// "ENCUMBERANCE",		   COMPLETE_ENCUMBERANCE

	}


	@Test
	public void testConstructor079()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor079", ok);

		String term = "EQTYPE.EQUIPPED.IS.FOO";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor079 pattern matches for " + term, mat.find());
		assertTrue("Constructor079 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "EQTYPE.EQUIPPED.IS.FOO",			   START_EQTYPE

	}


	@Test
	public void testConstructor080()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor080", ok);

		String term = "HASDEITY:Bane";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor080 pattern matches for " + term, mat.find());
		assertTrue("Constructor080 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "HASDEITY:Bane",		       START_HASDEITY

	}


	@Test
	public void testConstructor081()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor081", ok);

		String term = "HASFEAT:Endurance";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor081 pattern matches for " + term, mat.find());
		assertTrue("Constructor081 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "HASFEAT:Endurance",			    START_HASFEAT

	}


	@Test
	public void testConstructor082()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor082", ok);

		String term = "HD";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor082 pattern matches for " + term, mat.find());
		assertEquals("Constructor082 pattern matches all of " + term, term, mat.group(1));

		// "HD",			   COMPLETE_HD

	}


	@Test
	public void testConstructor083()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor083", ok);

		String term = "MAXCASTABLE";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor083 pattern matches for " + term, mat.find());
		assertEquals("Constructor083 pattern matches all of " + term, term, mat.group(1));

		// "MAXCASTABLE",		   COMPLETE_MAXCASTABLE

	}


	@Test
	public void testConstructor084()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor084", ok);

		String term = "MODEQUIPSPELLFAILURE";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor084 pattern matches for " + term, mat.find());
		assertTrue("Constructor084 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "MODEQUIPSPELLFAILURE",		       START_MODEQUIP

	}


	@Test
	public void testConstructor085()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor085", ok);

		String term = "MOVEBASE";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor085 pattern matches for " + term, mat.find());
		assertEquals("Constructor085 pattern matches all of " + term, term, mat.group(1));

		// "MOVEBASE",			   COMPLETE_MOVEBASE

	}


	@Test
	public void testConstructor086()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor086", ok);

		String term = "MOVE[Walk]";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor086 pattern matches for " + term, mat.find());
		assertTrue("Constructor086 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "MOVE[Walk]",			START_MOVE

	}


	@Test
	public void testConstructor087()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor087", ok);

		String term = "PC.HEIGHT";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor087 pattern matches for " + term, mat.find());
		assertEquals("Constructor087 pattern matches all of " + term, term, mat.group(1));

		// "PC.HEIGHT",			   COMPLETE_PC_HEIGHT

	}


	@Test
	public void testConstructor088()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor088", ok);

		String term = "PC.SIZEINT";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor088 pattern matches for " + term, mat.find());
		assertTrue("Constructor088 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "PC.SIZEINT",		      START_PC_SIZE

	}


	@Test
	public void testConstructor089()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor089", ok);

		String term = "PC.WEIGHT";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor089 pattern matches for " + term, mat.find());
		assertEquals("Constructor089 pattern matches all of " + term, term, mat.group(1));

		// "PC.WEIGHT",			   COMPLETE_PC_WEIGHT

	}


	@Test
	public void testConstructor090()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor090", ok);

		String term = "PROFACCHECK";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor090 pattern matches for " + term, mat.find());
		assertEquals("Constructor090 pattern matches all of " + term, term, mat.group(1));

		// "PROFACCHECK",		   COMPLETE_PROFACCHECK

	}


	@Test
	public void testConstructor091()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor091", ok);

		String term = "RACESIZE";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor091 pattern matches for " + term, mat.find());
		assertEquals("Constructor091 pattern matches all of " + term, term, mat.group(1));

		// "RACESIZE",			   COMPLETE_RACESIZE

	}


	@Test
	public void testConstructor092()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor092", ok);

		String term = "SCORE";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor092 pattern matches for " + term, mat.find());
		assertEquals("Constructor092 pattern matches all of " + term, term, mat.group(1));

		// "SCORE",			   COMPLETE_SCORE

	}


	@Test
	public void testConstructor093()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor093", ok);

		String term = "SHIELDACCHECK";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor093 pattern matches for " + term, mat.find());
		assertEquals("Constructor093 pattern matches all of " + term, term, mat.group(1));

		// "SHIELDACCHECK",		   COMPLETE_SHIELDACCHECK

	}


	@Test
	public void testConstructor094()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor094", ok);

		String term = "SHIELDACHECK";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor094 pattern matches for " + term, mat.find());
		assertEquals("Constructor094 pattern matches all of " + term, term, mat.group(1));

		// "SHIELDACHECK",		   COMPLETE_SHIELDACCHECK

	}


	@Test
	public void testConstructor095()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor095", ok);

		String term = "SIZE";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor095 pattern matches for " + term, mat.find());
		assertEquals("Constructor095 pattern matches all of " + term, term, mat.group(1));

		// "SIZE",			   COMPLETE_SIZEMOD

	}


	@Test
	public void testConstructor096()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor096", ok);

		String term = "SIZEMOD";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor096 pattern matches for " + term, mat.find());
		assertEquals("Constructor096 pattern matches all of " + term, term, mat.group(1));

		// "SIZEMOD",			   COMPLETE_SIZEMOD

	}


	@Test
	public void testConstructor097()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor097", ok);

		String term = "SKILLRANK.Tumble";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor097 pattern matches for " + term, mat.find());
		assertTrue("Constructor097 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "SKILLRANK.Tumble",			 START_SKILLRANK

	}


	@Test
	public void testConstructor098()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor098", ok);

		String term = "SKILLRANK=Perform (Dance)";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor098 pattern matches for " + term, mat.find());
		assertTrue("Constructor098 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "SKILLRANK=Perform (Dance)",			  START_SKILLRANK

	}


	@Test
	public void testConstructor099()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor099", ok);

		String term = "SKILLTOTAL.Tumble";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor099 pattern matches for " + term, mat.find());
		assertTrue("Constructor099 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "SKILLTOTAL.Tumble",			 START_SKILLTOTAL

	}


	@Test
	public void testConstructor100()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor100", ok);

		String term = "SKILLTOTAL=Perform (Dance)";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor100 pattern matches for " + term, mat.find());
		assertTrue("Constructor100 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "SKILLTOTAL=Perform (Dance)",		  START_SKILLTOTAL

	}


	@Test
	public void testConstructor101()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor101", ok);

		String term = "SPELLBASESTAT";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor101 pattern matches for " + term, mat.find());
		assertEquals("Constructor101 pattern matches all of " + term, term, mat.group(1));

		// "SPELLBASESTAT",		   COMPLETE_SPELLBASESTAT

	}


	@Test
	public void testConstructor102()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor102", ok);

		String term = "SPELLBASESTATSCORE";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor102 pattern matches for " + term, mat.find());
		assertEquals("Constructor102 pattern matches all of " + term, term, mat.group(1));

		// "SPELLBASESTATSCORE",	   COMPLETE_SPELLBASESTAT

	}


	@Test
	public void testConstructor103()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor103", ok);

		String term = "SPELLLEVEL";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor103 pattern matches for " + term, mat.find());
		assertEquals("Constructor103 pattern matches all of " + term, term, mat.group(1));

		// "SPELLLEVEL",		   COMPLETE_SPELLLEVEL

	}


	@Test
	public void testConstructor104()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor104", ok);

		String term = "TL";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor104 pattern matches for " + term, mat.find());
		assertEquals("Constructor104 pattern matches all of " + term, term, mat.group(1));

		// "TL",			   COMPLETE_TL

	}


	@Test
	public void testConstructor105()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor105", ok);

		String term = "VARDEFINED:MilkyBarsEaten";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor105 pattern matches for " + term, mat.find());
		assertTrue("Constructor105 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "VARDEFINED:MilkyBarsEaten",			 START_VARDEFINED

	}


	@Test
	public void testConstructor106()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor106", ok);

		String term = "WEIGHT.CARRIED";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor106 pattern matches for " + term, mat.find());
		assertTrue("Constructor106 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "WEIGHT.CARRIED",			  START_WEIGHT

	}


	@Test
	public void testConstructor107()
	{
		EvaluatorFactoryTest.loadAll();

		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");

		boolean ok;
		Pattern iVP = Pattern.compile("foo");
		try
		{
			ok = true;
			iVP = (Pattern) pF.get(EvaluatorFactory.PC);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in Constructor107", ok);

		String term = "DEXSCORE";
		Matcher mat = iVP.matcher(term);
		assertTrue("Constructor107 pattern matches for " + term, mat.find());
		assertTrue("Constructor107 pattern matches start of " + term, term.startsWith(mat.group(1)));

		// "DEXSCORE",			      START_STAT

	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator001()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "ACCHECK";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator001 evaluator correct for " + term, t instanceof PCACcheckTermEvaluator);

		Class<?> uClass = PCACcheckTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator001", ok);

		assertEquals("GetTermEvaluator001 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator002()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "ACHECK";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator002 evaluator correct for " + term, t instanceof PCACcheckTermEvaluator);

		Class<?> uClass = PCACcheckTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator002", ok);

		assertEquals("GetTermEvaluator002 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator003()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "ARMORACCHECK";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator003 evaluator correct for " + term, t instanceof PCArmourACcheckTermEvaluator);

		Class<?> uClass = PCArmourACcheckTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator003", ok);

		assertEquals("GetTermEvaluator003 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator004()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "ARMORACHECK";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator004 evaluator correct for " + term, t instanceof PCArmourACcheckTermEvaluator);

		Class<?> uClass = PCArmourACcheckTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator004", ok);

		assertEquals("GetTermEvaluator004 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator005()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "BAB";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator005 evaluator correct for " + term, t instanceof PCBABTermEvaluator);

		Class<?> uClass = PCBABTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator005", ok);

		assertEquals("GetTermEvaluator005 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator006()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "BASESPELLSTAT";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "CLASS:Foo");

		assertTrue("GetTermEvaluator006 evaluator correct for " + term, t instanceof PCBaseSpellStatTermEvaluator);

		Class<?> uClass = PCBaseSpellStatTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "source");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator006", ok);

		assertEquals("GetTermEvaluator006 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator006 field source is correct ", "Foo", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator007()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "BASESPELLSTAT";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "RACE:Bar");

		assertTrue("GetTermEvaluator007 evaluator correct for " + term, t instanceof PCBaseSpellStatTermEvaluator);

		Class<?> uClass = PCBaseSpellStatTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "source");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator007", ok);

		assertEquals("GetTermEvaluator007 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator007 field source is correct ", "", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator008()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "CASTERLEVEL";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "RACE:Bar");

		assertTrue("GetTermEvaluator008 evaluator correct for " + term, t instanceof PCCasterLevelRaceTermEvaluator);

		Class<?> uClass = PCCasterLevelRaceTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "source");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator008", ok);

		assertEquals("GetTermEvaluator008 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator008 field source is correct ", "RACE.Bar", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator009()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "CASTERLEVEL";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "CLASS:Foo");

		assertTrue("GetTermEvaluator009 evaluator correct for " + term, t instanceof PCCasterLevelClassTermEvaluator);

		Class<?> uClass = PCCasterLevelClassTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "source");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator009", ok);

		assertEquals("GetTermEvaluator009 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator009 field source is correct ", "Foo", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator010()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "CASTERLEVEL";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "SPELL:Baz");

		assertTrue("GetTermEvaluator010 evaluator correct for " + term, t instanceof PCCasterLevelTotalTermEvaluator);

		Class<?> uClass = PCCasterLevelTotalTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator010", ok);

		assertEquals("GetTermEvaluator010 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator011()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "CASTERLEVEL.TOTAL";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "RACE:Bar");

		assertTrue("GetTermEvaluator011 evaluator correct for " + term, t instanceof PCCasterLevelTotalTermEvaluator);

		Class<?> uClass = PCCasterLevelTotalTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator011", ok);

		assertEquals("GetTermEvaluator011 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator012()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[ATTACKS]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator012 evaluator correct for " + term, t instanceof PCCountAttacksTermEvaluator);

		Class<?> uClass = PCCountAttacksTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator012", ok);

		assertEquals("GetTermEvaluator012 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator013()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[CHECKS]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator013 evaluator correct for " + term, t instanceof PCCountChecksTermEvaluator);

		Class<?> uClass = PCCountChecksTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator013", ok);

		assertEquals("GetTermEvaluator013 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator014()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[CLASSES]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator014 evaluator correct for " + term, t instanceof PCCountClassesTermEvaluator);

		Class<?> uClass = PCCountClassesTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator014", ok);

		assertEquals("GetTermEvaluator014 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator015()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[CONTAINERS]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator015 evaluator correct for " + term, t instanceof PCCountContainersTermEvaluator);

		Class<?> uClass = PCCountContainersTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator015", ok);

		assertEquals("GetTermEvaluator015 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator016()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[DOMAINS]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator016 evaluator correct for " + term, t instanceof PCCountDomainsTermEvaluator);

		Class<?> uClass = PCCountDomainsTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator016", ok);

		assertEquals("GetTermEvaluator016 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator017()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATSALL.ALL]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue(
				"GetTermEvaluator017 evaluator correct for " + term,
				t instanceof PCCountAbilitiesNatureAllTermEvaluator
		);

		Class<?> uClass = PCCountAbilitiesNatureAllTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF2 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		Boolean field1 = false;
		Boolean field2 = false;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (Boolean) pF1.get(t);
			field2 = (Boolean) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator017", ok);

		assertEquals("GetTermEvaluator017 stored term is correct " + term, term, field0);
		assertTrue("GetTermEvaluator017 field hidden is correct ", field1);
		assertTrue("GetTermEvaluator017 field visible is correct ", field2);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator018()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATSALL.HIDDEN]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue(
				"GetTermEvaluator018 evaluator correct for " + term,
				t instanceof PCCountAbilitiesNatureAllTermEvaluator
		);

		Class<?> uClass = PCCountAbilitiesNatureAllTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF2 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		Boolean field1 = false;
		Boolean field2 = false;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (Boolean) pF1.get(t);
			field2 = (Boolean) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator018", ok);

		assertEquals("GetTermEvaluator018 stored term is correct " + term, term, field0);
		assertTrue("GetTermEvaluator018 field hidden is correct ", field1);
		assertFalse("GetTermEvaluator018 field visible is correct ", field2);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator019()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATSALL.VISIBLE]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertThat("GetTermEvaluator019 evaluator correct for " + term, t,
			instanceOf(PCCountAbilitiesNatureAllTermEvaluator.class));

		Class<?> uClass = PCCountAbilitiesNatureAllTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF2 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		Boolean field1 = false;
		Boolean field2 = false;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (Boolean) pF1.get(t);
			field2 = (Boolean) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator019", ok);

		assertEquals("GetTermEvaluator019 stored term is correct " + term, term, field0);
		assertFalse("GetTermEvaluator019 field hidden is correct ", field1);
		assertTrue("GetTermEvaluator019 field visible is correct ", field2);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator020()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATSALL]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue(
				"GetTermEvaluator020 evaluator correct for " + term,
				t instanceof PCCountAbilitiesNatureAllTermEvaluator
		);

		Class<?> uClass = PCCountAbilitiesNatureAllTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF2 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		Boolean field1 = false;
		Boolean field2 = false;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (Boolean) pF1.get(t);
			field2 = (Boolean) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator020", ok);

		assertEquals("GetTermEvaluator020 stored term is correct " + term, term, field0);
		assertFalse("GetTermEvaluator020 field hidden is correct ", field1);
		assertTrue("GetTermEvaluator020 field visible is correct ", field2);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator021()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATSAUTO.ALL]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue(
				"GetTermEvaluator021 evaluator correct for " + term,
				t instanceof PCCountAbilitiesNatureAutoTermEvaluator
		);

		Class<?> uClass = PCCountAbilitiesNatureAutoTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF2 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		Boolean field1 = false;
		Boolean field2 = false;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (Boolean) pF1.get(t);
			field2 = (Boolean) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator021", ok);

		assertEquals("GetTermEvaluator021 stored term is correct " + term, term, field0);
		assertTrue("GetTermEvaluator021 field hidden is correct ", field1);
		assertTrue("GetTermEvaluator021 field visible is correct ", field2);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator022()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATSAUTO.HIDDEN]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue(
				"GetTermEvaluator022 evaluator correct for " + term,
				t instanceof PCCountAbilitiesNatureAutoTermEvaluator
		);

		Class<?> uClass = PCCountAbilitiesNatureAutoTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF2 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		Boolean field1 = false;
		Boolean field2 = false;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (Boolean) pF1.get(t);
			field2 = (Boolean) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator022", ok);

		assertEquals("GetTermEvaluator022 stored term is correct " + term, term, field0);
		assertTrue("GetTermEvaluator022 field hidden is correct ", field1);
		assertFalse("GetTermEvaluator022 field visible is correct ", field2);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator023()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATSAUTO.VISIBLE]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue(
				"GetTermEvaluator023 evaluator correct for " + term,
				t instanceof PCCountAbilitiesNatureAutoTermEvaluator
		);

		Class<?> uClass = PCCountAbilitiesNatureAutoTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF2 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		Boolean field1 = false;
		Boolean field2 = false;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (Boolean) pF1.get(t);
			field2 = (Boolean) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator023", ok);

		assertEquals("GetTermEvaluator023 stored term is correct " + term, term, field0);
		assertFalse("GetTermEvaluator023 field hidden is correct ", field1);
		assertTrue("GetTermEvaluator023 field visible is correct ", field2);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator024()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATSAUTO]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue(
				"GetTermEvaluator024 evaluator correct for " + term,
				t instanceof PCCountAbilitiesNatureAutoTermEvaluator
		);

		Class<?> uClass = PCCountAbilitiesNatureAutoTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF2 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		Boolean field1 = false;
		Boolean field2 = false;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (Boolean) pF1.get(t);
			field2 = (Boolean) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator024", ok);

		assertEquals("GetTermEvaluator024 stored term is correct " + term, term, field0);
		assertFalse("GetTermEvaluator024 field hidden is correct ", field1);
		assertTrue("GetTermEvaluator024 field visible is correct ", field2);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator025()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATS.ALL]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue(
				"GetTermEvaluator025 evaluator correct for " + term,
				t instanceof PCCountAbilitiesNatureNormalTermEvaluator
		);

		Class<?> uClass = PCCountAbilitiesNatureNormalTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF2 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		Boolean field1 = false;
		Boolean field2 = false;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (Boolean) pF1.get(t);
			field2 = (Boolean) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator025", ok);

		assertEquals("GetTermEvaluator025 stored term is correct " + term, term, field0);
		assertTrue("GetTermEvaluator025 field hidden is correct ", field1);
		assertTrue("GetTermEvaluator025 field visible is correct ", field2);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator026()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATS.HIDDEN]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue(
				"GetTermEvaluator026 evaluator correct for " + term,
				t instanceof PCCountAbilitiesNatureNormalTermEvaluator
		);

		Class<?> uClass = PCCountAbilitiesNatureNormalTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF2 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		Boolean field1 = false;
		Boolean field2 = false;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (Boolean) pF1.get(t);
			field2 = (Boolean) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator026", ok);

		assertEquals("GetTermEvaluator026 stored term is correct " + term, term, field0);
		assertTrue("GetTermEvaluator026 field hidden is correct ", field1);
		assertFalse("GetTermEvaluator026 field visible is correct ", field2);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator027()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATS.VISIBLE]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue(
				"GetTermEvaluator027 evaluator correct for " + term,
				t instanceof PCCountAbilitiesNatureNormalTermEvaluator
		);

		Class<?> uClass = PCCountAbilitiesNatureNormalTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF2 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		Boolean field1 = false;
		Boolean field2 = false;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (Boolean) pF1.get(t);
			field2 = (Boolean) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator027", ok);

		assertEquals("GetTermEvaluator027 stored term is correct " + term, term, field0);
		assertFalse("GetTermEvaluator027 field hidden is correct ", field1);
		assertTrue("GetTermEvaluator027 field visible is correct ", field2);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator028()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATS]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue(
				"GetTermEvaluator028 evaluator correct for " + term,
				t instanceof PCCountAbilitiesNatureNormalTermEvaluator
		);

		Class<?> uClass = PCCountAbilitiesNatureNormalTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF2 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		Boolean field1 = false;
		Boolean field2 = false;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (Boolean) pF1.get(t);
			field2 = (Boolean) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator028", ok);

		assertEquals("GetTermEvaluator028 stored term is correct " + term, term, field0);
		assertFalse("GetTermEvaluator028 field hidden is correct ", field1);
		assertTrue("GetTermEvaluator028 field visible is correct ", field2);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator029()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[VFEATS.ALL]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue(
				"GetTermEvaluator029 evaluator correct for " + term,
				t instanceof PCCountAbilitiesNatureVirtualTermEvaluator
		);

		Class<?> uClass = PCCountAbilitiesNatureVirtualTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF2 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		Boolean field1 = false;
		Boolean field2 = false;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (Boolean) pF1.get(t);
			field2 = (Boolean) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator029", ok);

		assertEquals("GetTermEvaluator029 stored term is correct " + term, term, field0);
		assertTrue("GetTermEvaluator029 field hidden is correct ", field1);
		assertTrue("GetTermEvaluator029 field visible is correct ", field2);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator030()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[VFEATS.HIDDEN]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue(
				"GetTermEvaluator030 evaluator correct for " + term,
				t instanceof PCCountAbilitiesNatureVirtualTermEvaluator
		);

		Class<?> uClass = PCCountAbilitiesNatureVirtualTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF2 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		Boolean field1 = false;
		Boolean field2 = false;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (Boolean) pF1.get(t);
			field2 = (Boolean) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator030", ok);

		assertEquals("GetTermEvaluator030 stored term is correct " + term, term, field0);
		assertTrue("GetTermEvaluator030 field hidden is correct ", field1);
		assertFalse("GetTermEvaluator030 field visible is correct ", field2);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator031()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[VFEATS.VISIBLE]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue(
				"GetTermEvaluator031 evaluator correct for " + term,
				t instanceof PCCountAbilitiesNatureVirtualTermEvaluator
		);

		Class<?> uClass = PCCountAbilitiesNatureVirtualTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF2 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		Boolean field1 = false;
		Boolean field2 = false;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (Boolean) pF1.get(t);
			field2 = (Boolean) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator031", ok);

		assertEquals("GetTermEvaluator031 stored term is correct " + term, term, field0);
		assertFalse("GetTermEvaluator031 field hidden is correct ", field1);
		assertTrue("GetTermEvaluator031 field visible is correct ", field2);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator032()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[VFEATS]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue(
				"GetTermEvaluator032 evaluator correct for " + term,
				t instanceof PCCountAbilitiesNatureVirtualTermEvaluator
		);

		Class<?> uClass = PCCountAbilitiesNatureVirtualTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF2 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		Boolean field1 = false;
		Boolean field2 = false;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (Boolean) pF1.get(t);
			field2 = (Boolean) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator032", ok);

		assertEquals("GetTermEvaluator032 stored term is correct " + term, term, field0);
		assertFalse("GetTermEvaluator032 field hidden is correct ", field1);
		assertTrue("GetTermEvaluator032 field visible is correct ", field2);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator033()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FOLLOWERS]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator033 evaluator correct for " + term, t instanceof PCCountFollowersTermEvaluator);

		Class<?> uClass = PCCountFollowersTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator033", ok);

		assertEquals("GetTermEvaluator033 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator034()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[LANGUAGES]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator034 evaluator correct for " + term, t instanceof PCCountLanguagesTermEvaluator);

		Class<?> uClass = PCCountLanguagesTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator034", ok);

		assertEquals("GetTermEvaluator034 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator035()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[MISC.COMPANIONS]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue(
				"GetTermEvaluator035 evaluator correct for " + term,
				t instanceof PCCountMiscCompanionsTermEvaluator
		);

		Class<?> uClass = PCCountMiscCompanionsTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator035", ok);

		assertEquals("GetTermEvaluator035 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator036()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[MISC.FUNDS]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator036 evaluator correct for " + term, t instanceof PCCountMiscFundsTermEvaluator);

		Class<?> uClass = PCCountMiscFundsTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator036", ok);

		assertEquals("GetTermEvaluator036 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator037()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[MISC.MAGIC]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator037 evaluator correct for " + term, t instanceof PCCountMiscMagicTermEvaluator);

		Class<?> uClass = PCCountMiscMagicTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator037", ok);

		assertEquals("GetTermEvaluator037 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator038()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[MOVE]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator038 evaluator correct for " + term, t instanceof PCCountMoveTermEvaluator);

		Class<?> uClass = PCCountMoveTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator038", ok);

		assertEquals("GetTermEvaluator038 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator039()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[NOTES]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator039 evaluator correct for " + term, t instanceof PCCountNotesTermEvaluator);

		Class<?> uClass = PCCountNotesTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator039", ok);

		assertEquals("GetTermEvaluator039 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator040()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[RACESUBTYPES]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator040 evaluator correct for " + term, t instanceof PCCountRaceSubTypesTermEvaluator);

		Class<?> uClass = PCCountRaceSubTypesTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator040", ok);

		assertEquals("GetTermEvaluator040 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator041()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SA]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator041 evaluator correct for " + term, t instanceof PCCountSABTermEvaluator);

		Class<?> uClass = PCCountSABTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator041", ok);

		assertEquals("GetTermEvaluator041 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator042()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SKILLS]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator042 evaluator correct for " + term, t instanceof PCCountSkillsTermEvaluator);

		Class<?> uClass = PCCountSkillsTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator042", ok);

		assertEquals("GetTermEvaluator042 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator043()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLCLASSES]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator043 evaluator correct for " + term, t instanceof PCCountSpellClassesTermEvaluator);

		Class<?> uClass = PCCountSpellClassesTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator043", ok);

		assertEquals("GetTermEvaluator043 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator044()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLRACE]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator044 evaluator correct for " + term, t instanceof PCCountSpellRaceTermEvaluator);

		Class<?> uClass = PCCountSpellRaceTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator044", ok);

		assertEquals("GetTermEvaluator044 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator045()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[STATS]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator045 evaluator correct for " + term, t instanceof PCCountStatsTermEvaluator);

		Class<?> uClass = PCCountStatsTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator045", ok);

		assertEquals("GetTermEvaluator045 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator046()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[TEMPBONUSNAMES]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue(
				"GetTermEvaluator046 evaluator correct for " + term,
				t instanceof PCCountTempBonusNamesTermEvaluator
		);

		Class<?> uClass = PCCountTempBonusNamesTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator046", ok);

		assertEquals("GetTermEvaluator046 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator047()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[TEMPLATES]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator047 evaluator correct for " + term, t instanceof PCCountTemplatesTermEvaluator);

		Class<?> uClass = PCCountTemplatesTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator047", ok);

		assertEquals("GetTermEvaluator047 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator048()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[VISIBLETEMPLATES]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue(
				"GetTermEvaluator048 evaluator correct for " + term,
				t instanceof PCCountVisibleTemplatesTermEvaluator
		);

		Class<?> uClass = PCCountVisibleTemplatesTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator048", ok);

		assertEquals("GetTermEvaluator048 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator049()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[VISION]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator049 evaluator correct for " + term, t instanceof PCCountVisionTermEvaluator);

		Class<?> uClass = PCCountVisionTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator049", ok);

		assertEquals("GetTermEvaluator049 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator050()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "ENCUMBERANCE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator050 evaluator correct for " + term, t instanceof PCEncumberanceTermEvaluator);

		Class<?> uClass = PCEncumberanceTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator050", ok);

		assertEquals("GetTermEvaluator050 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator051()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "HD";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator051 evaluator correct for " + term, t instanceof PCHDTermEvaluator);

		Class<?> uClass = PCHDTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator051", ok);

		assertEquals("GetTermEvaluator051 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluatorHp()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "HP";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("testGetTermEvaluatorHp evaluator correct for " + term, t instanceof PCHPTermEvaluator);

		Class<?> uClass = PCHPTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in testGetTermEvaluatorHp", ok);

		assertEquals("testGetTermEvaluatorHp stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator052()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "MAXCASTABLE";

		Globals.getContext().getReferenceContext().constructCDOMObject(ClassSpellList.class, "Bard");
		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "CLASS:Bard");

		assertTrue("GetTermEvaluator052 evaluator correct for " + term, t instanceof PCMaxCastableClassTermEvaluator);

		Class<?> uClass = PCMaxCastableClassTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "spellList");

		String field0 = "";
		ClassSpellList field1 = null;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (ClassSpellList) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator052", ok);

		assertEquals("GetTermEvaluator052 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator052 field spellList is correct ", "Bard", field1.getKeyName());
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator053()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "MAXCASTABLE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "DOMAIN:Fire");

		assertTrue("GetTermEvaluator053 evaluator correct for " + term, t instanceof PCMaxCastableDomainTermEvaluator);

		Class<?> uClass = PCMaxCastableDomainTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "domainKey");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator053", ok);

		assertEquals("GetTermEvaluator053 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator053 field domainKey is correct ", "Fire", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator054()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "MAXCASTABLE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "SPELLTYPE:Arcane");

		assertTrue(
				"GetTermEvaluator054 evaluator correct for " + term,
				t instanceof PCMaxCastableSpellTypeTermEvaluator
		);

		Class<?> uClass = PCMaxCastableSpellTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "typeKey");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator054", ok);

		assertEquals("GetTermEvaluator054 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator054 field typeKey is correct ", "Arcane", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator055()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "MAXCASTABLE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "ANY");

		assertTrue("GetTermEvaluator055 evaluator correct for " + term, t instanceof PCMaxCastableAnyTermEvaluator);

		Class<?> uClass = PCMaxCastableAnyTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator055", ok);

		assertEquals("GetTermEvaluator055 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator056()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "MAXCASTABLE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator056 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator057()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "MOVEBASE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator057 evaluator correct for " + term, t instanceof PCMoveBaseTermEvaluator);

		Class<?> uClass = PCMoveBaseTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator057", ok);

		assertEquals("GetTermEvaluator057 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator058()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "PC.HEIGHT";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator058 evaluator correct for " + term, t instanceof PCHeightTermEvaluator);

		Class<?> uClass = PCHeightTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator058", ok);

		assertEquals("GetTermEvaluator058 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator059()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "PC.WEIGHT";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator059 evaluator correct for " + term, t instanceof PCWeightTermEvaluator);

		Class<?> uClass = PCWeightTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator059", ok);

		assertEquals("GetTermEvaluator059 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator060()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "PROFACCHECK";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "EQ:Dagger");

		assertTrue("GetTermEvaluator060 evaluator correct for " + term, t instanceof PCProfACCheckTermEvaluator);

		Class<?> uClass = PCProfACCheckTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "eqKey");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator060", ok);

		assertEquals("GetTermEvaluator060 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator060 field eqKey is correct ", "Dagger", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator061()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "PROFACCHECK";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator061 evaluator correct for " + term, t instanceof PCProfACCheckTermEvaluator);

		Class<?> uClass = PCProfACCheckTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "eqKey");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator061", ok);

		assertEquals("GetTermEvaluator061 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator061 field eqKey is correct ", "", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator062()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "RACESIZE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator062 evaluator correct for " + term, t instanceof PCRaceSizeTermEvaluator);

		Class<?> uClass = PCRaceSizeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator062", ok);

		assertEquals("GetTermEvaluator062 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator063()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "SCORE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "STAT:INT");

		assertTrue("GetTermEvaluator063 evaluator correct for " + term, t instanceof PCScoreTermEvaluator);

		Class<?> uClass = PCScoreTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "stat");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator063", ok);

		assertEquals("GetTermEvaluator063 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator063 field stat is correct ", "INT", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator064()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "SCORE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator064 evaluator correct for " + term, t instanceof PCScoreTermEvaluator);

		Class<?> uClass = PCScoreTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "stat");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator064", ok);

		assertEquals("GetTermEvaluator064 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator064 field stat is correct ", "", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator065()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "SHIELDACCHECK";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator065 evaluator correct for " + term, t instanceof PCShieldACcheckTermEvaluator);

		Class<?> uClass = PCShieldACcheckTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator065", ok);

		assertEquals("GetTermEvaluator065 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator066()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "SHIELDACHECK";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator066 evaluator correct for " + term, t instanceof PCShieldACcheckTermEvaluator);

		Class<?> uClass = PCShieldACcheckTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator066", ok);

		assertEquals("GetTermEvaluator066 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator067()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "SIZE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator067 evaluator correct for " + term, t instanceof PCSizeTermEvaluator);

		Class<?> uClass = PCSizeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator067", ok);

		assertEquals("GetTermEvaluator067 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator068()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "SIZEMOD";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator068 evaluator correct for " + term, t instanceof PCSizeModEvaluatorTermEvaluator);

		Class<?> uClass = PCSizeModEvaluatorTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator068", ok);

		assertEquals("GetTermEvaluator068 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator069()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "SPELLBASESTAT";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "CLASS:Cleric");

		assertTrue("GetTermEvaluator069 evaluator correct for " + term, t instanceof PCSPellBaseStatTermEvaluator);

		Class<?> uClass = PCSPellBaseStatTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "classKey");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator069", ok);

		assertEquals("GetTermEvaluator069 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator069 field classKey is correct ", "Cleric", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator070()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "SPELLBASESTATSCORE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "CLASS:Cleric");

		assertTrue(
				"GetTermEvaluator070 evaluator correct for " + term,
				t instanceof PCSPellBaseStatScoreEvaluatorTermEvaluator
		);

		Class<?> uClass = PCSPellBaseStatScoreEvaluatorTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "classKey");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator070", ok);

		assertEquals("GetTermEvaluator070 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator070 field classKey is correct ", "Cleric", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator071()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "SPELLBASESTAT";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "RACE:Gnu");

		assertTrue("GetTermEvaluator071 evaluator correct for " + term, t instanceof PCSPellBaseStatTermEvaluator);

		Class<?> uClass = PCSPellBaseStatTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "classKey");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator071", ok);

		assertEquals("GetTermEvaluator071 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator071 field classKey is correct ", "", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator072()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "SPELLBASESTATSCORE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "RACE:Gnu");

		assertThat("GetTermEvaluator072 evaluator correct for " + term, t,
				instanceOf((PCSPellBaseStatScoreEvaluatorTermEvaluator.class)));

		Class<?> uClass = PCSPellBaseStatScoreEvaluatorTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "classKey");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator072", ok);

		assertEquals("GetTermEvaluator072 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator072 field classKey is correct ", "", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator073()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "SPELLLEVEL";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator073 evaluator correct for " + term, t instanceof PCSpellLevelTermEvaluator);

		Class<?> uClass = PCSpellLevelTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator073", ok);

		assertEquals("GetTermEvaluator073 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator074()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "TL";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator074 evaluator correct for " + term, t instanceof PCTLTermEvaluator);

		Class<?> uClass = PCTLTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator074", ok);

		assertEquals("GetTermEvaluator074 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator075()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "BL";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "CLASS:Ranger");

		assertTrue("GetTermEvaluator075 evaluator correct for " + term, t instanceof PCBLTermEvaluator);

		Class<?> uClass = PCBLTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "source");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator075", ok);

		assertEquals("GetTermEvaluator075 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator075 field source is correct ", "Ranger", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator076()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "BL";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "RACE:Gnome");

		assertTrue("GetTermEvaluator076 evaluator correct for " + term, t instanceof PCBLTermEvaluator);

		Class<?> uClass = PCBLTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "source");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator076", ok);

		assertEquals("GetTermEvaluator076 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator076 field source is correct ", "", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator077()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "BL.Wizard";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "DOMAIN:Ice");

		assertTrue("GetTermEvaluator077 evaluator correct for " + term, t instanceof PCBLTermEvaluator);

		Class<?> uClass = PCBLTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "source");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator077", ok);

		assertEquals("GetTermEvaluator077 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator077 field source is correct ", "Wizard", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator078()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "BL=Cleric";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "DOMAIN:Law");

		assertTrue("GetTermEvaluator078 evaluator correct for " + term, t instanceof PCBLTermEvaluator);

		Class<?> uClass = PCBLTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "source");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator078", ok);

		assertEquals("GetTermEvaluator078 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator078 field source is correct ", "Cleric", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator079()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "CL;BEFORELEVEL.10";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "CLASS:Ranger");

		assertTrue("GetTermEvaluator079 evaluator correct for " + term, t instanceof PCCLBeforeLevelTermEvaluator);

		Class<?> uClass = PCCLBeforeLevelTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "source");
		Field pF2 = (Field) TestHelper.findField(uClass, "level");

		String field0 = "";
		String field1 = "";
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator079", ok);

		assertEquals("GetTermEvaluator079 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator079 field source is correct ", "Ranger", field1);
		assertEquals("GetTermEvaluator079 field level is correct ",
			10,
				field2.intValue());
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator080()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "CL;BEFORELEVEL=15";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "CLASS:Druid");

		assertTrue("GetTermEvaluator080 evaluator correct for " + term, t instanceof PCCLBeforeLevelTermEvaluator);

		Class<?> uClass = PCCLBeforeLevelTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "source");
		Field pF2 = (Field) TestHelper.findField(uClass, "level");

		String field0 = "";
		String field1 = "";
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator080", ok);

		assertEquals("GetTermEvaluator080 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator080 field source is correct ", "Druid", field1);
		assertEquals("GetTermEvaluator080 field level is correct ",
			15,
			field2.intValue());
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator081()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "CL;BEFORELEVEL=Fighter";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator081 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator082()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "CLASSLEVEL.Bard (Bardiliscious)";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator082 evaluator correct for " + term, t instanceof PCCLTermEvaluator);

		Class<?> uClass = PCCLTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "classKey");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator082", ok);

		assertEquals("GetTermEvaluator082 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator082 field classKey is correct ", "Bard (Bardiliscious)", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator083()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "CLASSLEVEL=Rogue {Sneaky}";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator083 evaluator correct for " + term, t instanceof PCCLTermEvaluator);

		Class<?> uClass = PCCLTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "classKey");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator083", ok);

		assertEquals("GetTermEvaluator083 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator083 field classKey is correct ", "Rogue (Sneaky)", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator084()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "CLASS.Druid";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "RACE:Human");

		assertTrue("GetTermEvaluator084 evaluator correct for " + term, t instanceof PCHasClassTermEvaluator);

		Class<?> uClass = PCHasClassTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "source");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator084", ok);

		assertEquals("GetTermEvaluator084 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator084 field source is correct ", "Druid", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator085()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "CLASS=Paladin";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "RACE:Dwarf");

		assertTrue("GetTermEvaluator085 evaluator correct for " + term, t instanceof PCHasClassTermEvaluator);

		Class<?> uClass = PCHasClassTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "source");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator085", ok);

		assertEquals("GetTermEvaluator085 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator085 field source is correct ", "Paladin", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator086()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "CL";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "CLASS:Ranger");

		assertTrue("GetTermEvaluator086 evaluator correct for " + term, t instanceof PCCLTermEvaluator);

		Class<?> uClass = PCCLTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "classKey");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator086", ok);

		assertEquals("GetTermEvaluator086 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator086 field classKey is correct ", "Ranger", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator087()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "CL";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator087 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator088()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "CL.Bard";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "RACE:Elf");

		assertTrue("GetTermEvaluator088 evaluator correct for " + term, t instanceof PCCLTermEvaluator);

		Class<?> uClass = PCCLTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "classKey");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator088", ok);

		assertEquals("GetTermEvaluator088 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator088 field classKey is correct ", "Bard", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator089()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "CL=Rogue";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "RACE:Elf");

		assertTrue("GetTermEvaluator089 evaluator correct for " + term, t instanceof PCCLTermEvaluator);

		Class<?> uClass = PCCLTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "classKey");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator089", ok);

		assertEquals("GetTermEvaluator089 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator089 field classKey is correct ", "Rogue", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator090()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.MERGENONE]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator090 evaluator correct for " + term, t instanceof PCCountEqTypeTermEvaluator);

		Class<?> uClass = PCCountEqTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[]{};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator090", ok);

		assertEquals("GetTermEvaluator090 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator090 field types[0] is correct ", "", field1[0]);
		assertEquals("GetTermEvaluator090 field merge is correct ",
			Constants.MERGE_NONE,
			field2.intValue());
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator091()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.MERGELOC]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator091 evaluator correct for " + term, t instanceof PCCountEqTypeTermEvaluator);

		Class<?> uClass = PCCountEqTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[]{};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator091", ok);

		assertEquals("GetTermEvaluator091 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator091 field types[0] is correct ", "", field1[0]);
		assertEquals("GetTermEvaluator091 field merge is correct ",
			Constants.MERGE_LOCATION,
			field2.intValue());
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator092()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator092 evaluator correct for " + term, t instanceof PCCountEqTypeTermEvaluator);

		Class<?> uClass = PCCountEqTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[]{};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator092", ok);

		assertEquals("GetTermEvaluator092 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator092 field types[0] is correct ", "", field1[0]);
		assertEquals("GetTermEvaluator092 field merge is correct ",
			Constants.MERGE_ALL,
			field2.intValue());
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator093()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.EQUIPPED]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator093 evaluator correct for " + term, t instanceof PCCountEqTypeTermEvaluator);

		Class<?> uClass = PCCountEqTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[]{};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator093", ok);

		assertEquals("GetTermEvaluator093 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator093 field types[0] is correct ", "EQUIPPED", field1[0]);
		assertEquals("GetTermEvaluator093 field merge is correct ",
			Constants.MERGE_ALL,
			field2.intValue());
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator094()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.NOTEQUIPPED]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator094 evaluator correct for " + term, t instanceof PCCountEqTypeTermEvaluator);

		Class<?> uClass = PCCountEqTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[]{};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator094", ok);

		assertEquals("GetTermEvaluator094 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator094 field types[0] is correct ", "NOTEQUIPPED", field1[0]);
		assertEquals("GetTermEvaluator094 field merge is correct ",
			Constants.MERGE_ALL,
			field2.intValue());
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator095()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.CONTAINER]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator095 evaluator correct for " + term, t instanceof PCCountEqTypeTermEvaluator);

		Class<?> uClass = PCCountEqTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[]{};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator095", ok);

		assertEquals("GetTermEvaluator095 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator095 field types[0] is correct ", "CONTAINER", field1[0]);
		assertEquals("GetTermEvaluator095 field merge is correct ",
			Constants.MERGE_ALL,
			field2.intValue());
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator096()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.WEAPON]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator096 evaluator correct for " + term, t instanceof PCCountEqTypeTermEvaluator);

		Class<?> uClass = PCCountEqTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[]{};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator096", ok);

		assertEquals("GetTermEvaluator096 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator096 field types[0] is correct ", "WEAPON", field1[0]);
		assertEquals("GetTermEvaluator096 field merge is correct ",
			Constants.MERGE_ALL,
			field2.intValue());
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator097()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.ACITEM]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator097 evaluator correct for " + term, t instanceof PCCountEqTypeTermEvaluator);

		Class<?> uClass = PCCountEqTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[]{};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator097", ok);

		assertEquals("GetTermEvaluator097 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator097 field types[0] is correct ", "ACITEM", field1[0]);
		assertEquals("GetTermEvaluator097 field merge is correct ",
			Constants.MERGE_ALL,
			field2.intValue());
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator098()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.MERGENONE.ARMOR.IS.FOO]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator098 evaluator correct for " + term, t instanceof PCCountEqTypeTermEvaluator);

		Class<?> uClass = PCCountEqTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[]{};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator098", ok);

		assertEquals("GetTermEvaluator098 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator098 field types[0] is correct ", "ARMOR", field1[0]);
		assertEquals("GetTermEvaluator098 field types[1] is correct ", "IS", field1[1]);
		assertEquals("GetTermEvaluator098 field types[2] is correct ", "FOO", field1[2]);
		assertEquals("GetTermEvaluator098 field merge is correct ",
			Constants.MERGE_NONE,
			field2.intValue());
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator099()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.MERGELOC.QUX.NOT.BAR]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator099 evaluator correct for " + term, t instanceof PCCountEqTypeTermEvaluator);

		Class<?> uClass = PCCountEqTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[]{};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator099", ok);

		assertEquals("GetTermEvaluator099 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator099 field types[0] is correct ", "QUX", field1[0]);
		assertEquals("GetTermEvaluator099 field types[1] is correct ", "NOT", field1[1]);
		assertEquals("GetTermEvaluator099 field types[2] is correct ", "BAR", field1[2]);
		assertEquals("GetTermEvaluator099 field merge is correct ",
			Constants.MERGE_LOCATION,
			field2.intValue());
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator100()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.QUUX.ADD.BAZ]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator100 evaluator correct for " + term, t instanceof PCCountEqTypeTermEvaluator);

		Class<?> uClass = PCCountEqTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[]{};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator100", ok);

		assertEquals("GetTermEvaluator100 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator100 field types[0] is correct ", "QUUX", field1[0]);
		assertEquals("GetTermEvaluator100 field types[1] is correct ", "ADD", field1[1]);
		assertEquals("GetTermEvaluator100 field types[2] is correct ", "BAZ", field1[2]);
		assertEquals("GetTermEvaluator100 field merge is correct ",
			Constants.MERGE_ALL,
			field2.intValue());
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator101()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.MERGENONE.WEAPON.IS.FOO]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator101 evaluator correct for " + term, t instanceof PCCountEqTypeTermEvaluator);

		Class<?> uClass = PCCountEqTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[]{};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator101", ok);

		assertEquals("GetTermEvaluator101 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator101 field types[0] is correct ", "WEAPON", field1[0]);
		assertEquals("GetTermEvaluator101 field types[1] is correct ", "IS", field1[1]);
		assertEquals("GetTermEvaluator101 field types[2] is correct ", "FOO", field1[2]);
		assertEquals("GetTermEvaluator101 field merge is correct ",
			Constants.MERGE_NONE,
			field2.intValue());
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator102()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.WEAPON.IS.FOO.EQUIPPED.ADD.BAR]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator102 evaluator correct for " + term, t instanceof PCCountEqTypeTermEvaluator);

		Class<?> uClass = PCCountEqTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[]{};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator102", ok);

		assertEquals("GetTermEvaluator102 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator102 field types[0] is correct ", "WEAPON", field1[0]);
		assertEquals("GetTermEvaluator102 field types[1] is correct ", "IS", field1[1]);
		assertEquals("GetTermEvaluator102 field types[2] is correct ", "FOO", field1[2]);
		assertEquals("GetTermEvaluator102 field types[3] is correct ", "EQUIPPED", field1[3]);
		assertEquals("GetTermEvaluator102 field types[4] is correct ", "ADD", field1[4]);
		assertEquals("GetTermEvaluator102 field types[5] is correct ", "BAR", field1[5]);
		assertEquals("GetTermEvaluator102 field merge is correct ",
			Constants.MERGE_ALL,
			field2.intValue());
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator103()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.FOO]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator103 evaluator correct for " + term, t instanceof PCCountEqTypeTermEvaluator);

		Class<?> uClass = PCCountEqTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[]{};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator103", ok);

		assertEquals("GetTermEvaluator103 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator103 field types[0] is correct ", "FOO", field1[0]);
		assertEquals("GetTermEvaluator103 field merge is correct ",
			Constants.MERGE_ALL,
			field2.intValue());
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator104()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.MERGENONE.IS.FOO.QUX]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator104 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator105()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.MERGELOC.NOT.BAR.QUX]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator105 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator106()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.ADD.BAZ.QUX]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator106 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator107()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQUIPMENT.MERGENONE]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator107 evaluator correct for " + term, t instanceof PCCountEquipmentTermEvaluator);

		Class<?> uClass = PCCountEquipmentTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[]{};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator107", ok);

		assertEquals("GetTermEvaluator107 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator107 field types[0] is correct ", "", field1[0]);
		assertEquals("GetTermEvaluator107 field merge is correct ",
			Constants.MERGE_NONE,
			field2.intValue());
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator108()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQUIPMENT.MERGELOC]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator108 evaluator correct for " + term, t instanceof PCCountEquipmentTermEvaluator);

		Class<?> uClass = PCCountEquipmentTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[]{};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator108", ok);

		assertEquals("GetTermEvaluator108 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator108 field types[0] is correct ", "", field1[0]);
		assertEquals("GetTermEvaluator108 field merge is correct ",
			Constants.MERGE_LOCATION,
			field2.intValue());
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator109()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQUIPMENT]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator109 evaluator correct for " + term, t instanceof PCCountEquipmentTermEvaluator);

		Class<?> uClass = PCCountEquipmentTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[]{};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator109", ok);

		assertEquals("GetTermEvaluator109 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator109 field types[0] is correct ", "", field1[0]);
		assertEquals("GetTermEvaluator109 field merge is correct ",
			Constants.MERGE_ALL,
			field2.intValue());
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator110()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQUIPMENT.EQUIPPED]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator110 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator111()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQUIPMENT.NOTEQUIPPED]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator111 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator112()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQUIPMENT.CONTAINER]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator112 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator113()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQUIPMENT.WEAPON]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator113 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator114()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQUIPMENT.ACITEM]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator114 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator115()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQUIPMENT.MERGENONE.IS.FOO]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator115 evaluator correct for " + term, t instanceof PCCountEquipmentTermEvaluator);

		Class<?> uClass = PCCountEquipmentTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[]{};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator115", ok);

		assertEquals("GetTermEvaluator115 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator115 field types[0] is correct ", "IS", field1[0]);
		assertEquals("GetTermEvaluator115 field types[1] is correct ", "FOO", field1[1]);
		assertEquals("GetTermEvaluator115 field merge is correct ",
			Constants.MERGE_NONE,
			field2.intValue());
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator116()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQUIPMENT.MERGELOC.NOT.BAR]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator116 evaluator correct for " + term, t instanceof PCCountEquipmentTermEvaluator);

		Class<?> uClass = PCCountEquipmentTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[]{};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator116", ok);

		assertEquals("GetTermEvaluator116 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator116 field types[0] is correct ", "NOT", field1[0]);
		assertEquals("GetTermEvaluator116 field types[1] is correct ", "BAR", field1[1]);
		assertEquals("GetTermEvaluator116 field merge is correct ",
			Constants.MERGE_LOCATION,
			field2.intValue());
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator117()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQUIPMENT.ADD.BAZ]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator117 evaluator correct for " + term, t instanceof PCCountEquipmentTermEvaluator);

		Class<?> uClass = PCCountEquipmentTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[]{};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator117", ok);

		assertEquals("GetTermEvaluator117 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator117 field types[0] is correct ", "ADD", field1[0]);
		assertEquals("GetTermEvaluator117 field types[1] is correct ", "BAZ", field1[1]);
		assertEquals("GetTermEvaluator117 field merge is correct ",
			Constants.MERGE_ALL,
			field2.intValue());
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator118()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQUIPMENT.IS.FOO.EQUIPPED.ADD.BAR]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator118 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator120()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQUIPMENT.MERGENONE.BAR.IS.FOO.QUX]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator120 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator121()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQUIPMENT.MERGELOC.BAR.NOT.BAZ.QUX]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator121 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator122()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQUIPMENT.BAR.ADD.BAZ.QUX]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator122 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator123()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATAUTOTYPE.HIDDEN]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue(
				"GetTermEvaluator123 evaluator correct for " + term,
				t instanceof PCCountAbilitiesTypeNatureAutoTermEvaluator
		);

		Class<?> uClass = PCCountAbilitiesTypeNatureAutoTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF3 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		String[] field1 = new String[]{};
		Boolean field2 = false;
		Boolean field3 = false;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Boolean) pF2.get(t);
			field3 = (Boolean) pF3.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator123", ok);

		assertEquals("GetTermEvaluator123 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator123 field types[0] is correct ", "", field1[0]);
		assertTrue("GetTermEvaluator123 field hidden is correct ", field2);
		assertFalse("GetTermEvaluator123 field visible is correct ", field3);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator124()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATAUTOTYPE=VISIBLE]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue(
				"GetTermEvaluator124 evaluator correct for " + term,
				t instanceof PCCountAbilitiesTypeNatureAutoTermEvaluator
		);

		Class<?> uClass = PCCountAbilitiesTypeNatureAutoTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF3 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		String[] field1 = new String[]{};
		Boolean field2 = false;
		Boolean field3 = false;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Boolean) pF2.get(t);
			field3 = (Boolean) pF3.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator124", ok);

		assertEquals("GetTermEvaluator124 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator124 field types[0] is correct ", "", field1[0]);
		assertFalse("GetTermEvaluator124 field hidden is correct ", field2);
		assertTrue("GetTermEvaluator124 field visible is correct ", field3);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator125()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATNAME.Jack of all trades]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator125 evaluator correct for " + term, t instanceof PCCountAbilityNameTermEvaluator);

		Class<?> uClass = PCCountAbilityNameTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "key");
		Field pF2 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF3 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		String field1 = "";
		Boolean field2 = false;
		Boolean field3 = false;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
			field2 = (Boolean) pF2.get(t);
			field3 = (Boolean) pF3.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator125", ok);

		assertEquals("GetTermEvaluator125 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator125 field key is correct ", "Jack of all trades", field1);
		assertFalse("GetTermEvaluator125 field hidden is correct ", field2);
		assertTrue("GetTermEvaluator125 field visible is correct ", field3);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator126()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATNAME=Weapon Focus (Dagger)]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator126 evaluator correct for " + term, t instanceof PCCountAbilityNameTermEvaluator);

		Class<?> uClass = PCCountAbilityNameTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "key");
		Field pF2 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF3 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		String field1 = "";
		Boolean field2 = false;
		Boolean field3 = false;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
			field2 = (Boolean) pF2.get(t);
			field3 = (Boolean) pF3.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator126", ok);

		assertEquals("GetTermEvaluator126 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator126 field key is correct ", "Weapon Focus (Dagger)", field1);
		assertFalse("GetTermEvaluator126 field hidden is correct ", field2);
		assertTrue("GetTermEvaluator126 field visible is correct ", field3);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator127()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATTYPE.BAR]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue(
				"GetTermEvaluator127 evaluator correct for " + term,
				t instanceof PCCountAbilitiesTypeNatureAllTermEvaluator
		);

		Class<?> uClass = PCCountAbilitiesTypeNatureAllTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF3 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		String[] field1 = new String[]{};
		Boolean field2 = false;
		Boolean field3 = false;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Boolean) pF2.get(t);
			field3 = (Boolean) pF3.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator127", ok);

		assertEquals("GetTermEvaluator127 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator127 field types[0] is correct ", "BAR", field1[0]);
		assertFalse("GetTermEvaluator127 field hidden is correct ", field2);
		assertTrue("GetTermEvaluator127 field visible is correct ", field3);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator128()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATTYPE.BAZ]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue(
				"GetTermEvaluator128 evaluator correct for " + term,
				t instanceof PCCountAbilitiesTypeNatureAllTermEvaluator
		);

		Class<?> uClass = PCCountAbilitiesTypeNatureAllTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF3 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		String[] field1 = new String[]{};
		Boolean field2 = false;
		Boolean field3 = false;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Boolean) pF2.get(t);
			field3 = (Boolean) pF3.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator128", ok);

		assertEquals("GetTermEvaluator128 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator128 field types[0] is correct ", "BAZ", field1[0]);
		assertFalse("GetTermEvaluator128 field hidden is correct ", field2);
		assertTrue("GetTermEvaluator128 field visible is correct ", field3);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator129()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[VFEATTYPE.HIDDEN]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue(
				"GetTermEvaluator129 evaluator correct for " + term,
				t instanceof PCCountAbilitiesTypeNatureVirtualTermEvaluator
		);

		Class<?> uClass = PCCountAbilitiesTypeNatureVirtualTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF3 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		String[] field1 = new String[]{};
		Boolean field2 = false;
		Boolean field3 = false;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Boolean) pF2.get(t);
			field3 = (Boolean) pF3.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator129", ok);

		assertEquals("GetTermEvaluator129 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator129 field types[0] is correct ", "", field1[0]);
		assertTrue("GetTermEvaluator129 field hidden is correct ", field2);
		assertFalse("GetTermEvaluator129 field visible is correct ", field3);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator130()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[VFEATTYPE=ALL]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue(
				"GetTermEvaluator130 evaluator correct for " + term,
				t instanceof PCCountAbilitiesTypeNatureVirtualTermEvaluator
		);

		Class<?> uClass = PCCountAbilitiesTypeNatureVirtualTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF3 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		String[] field1 = new String[]{};
		Boolean field2 = false;
		Boolean field3 = false;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Boolean) pF2.get(t);
			field3 = (Boolean) pF3.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator130", ok);

		assertEquals("GetTermEvaluator130 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator130 field types[0] is correct ", "", field1[0]);
		assertTrue("GetTermEvaluator130 field hidden is correct ", field2);
		assertTrue("GetTermEvaluator130 field visible is correct ", field3);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator131()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATTYPE.FOO.BAR.BAZ.QUX]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue(
				"GetTermEvaluator131 evaluator correct for " + term,
				t instanceof PCCountAbilitiesTypeNatureAllTermEvaluator
		);

		Class<?> uClass = PCCountAbilitiesTypeNatureAllTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF3 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		String[] field1 = new String[]{};
		Boolean field2 = false;
		Boolean field3 = false;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Boolean) pF2.get(t);
			field3 = (Boolean) pF3.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator131", ok);

		assertEquals("GetTermEvaluator131 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator131 field types[0] is correct ", "FOO", field1[0]);
		assertEquals("GetTermEvaluator131 field types[1] is correct ", "BAR", field1[1]);
		assertEquals("GetTermEvaluator131 field types[2] is correct ", "BAZ", field1[2]);
		assertEquals("GetTermEvaluator131 field types[3] is correct ", "QUX", field1[3]);
		assertFalse("GetTermEvaluator131 field hidden is correct ", field2);
		assertTrue("GetTermEvaluator131 field visible is correct ", field3);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator132()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FOLLOWERTYPE.MOO]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator132 evaluator correct for " + term, t instanceof PCCountFollowerTypeTermEvaluator);

		Class<?> uClass = PCCountFollowerTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "type");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator132", ok);

		assertEquals("GetTermEvaluator132 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator132 field type is correct ", "MOO", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator133()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FOLLOWERTYPE.MOO.0.EQTYPE]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue(
				"GetTermEvaluator133 evaluator correct for " + term,
				t instanceof PCCountFollowerTypeTransitiveTermEvaluator
		);

		Class<?> uClass = PCCountFollowerTypeTransitiveTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "index");
		Field pF2 = (Field) TestHelper.findField(uClass, "newCount");
		Field pF3 = (Field) TestHelper.findField(uClass, "type");

		String field0 = "";
		Integer field1 = 0;
		String field2 = "";
		String field3 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (Integer) pF1.get(t);
			field2 = (String) pF2.get(t);
			field3 = (String) pF3.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator133", ok);

		assertEquals("GetTermEvaluator133 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator133 field index is correct ",
			0,
			field1.intValue());
		assertEquals("GetTermEvaluator133 field newCount is correct ", "COUNT[EQTYPE]", field2);
		assertEquals("GetTermEvaluator133 field type is correct ", "MOO", field3);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator134()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FOLLOWERTYPE.MOO.0]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator134 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator135()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FOLLOWERTYPE.MOO.FOO.0]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator135 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator136()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SKILLTYPE.KNOWLEDGE]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator136 evaluator correct for " + term, t instanceof PCSkillTypeTermEvaluator);

		Class<?> uClass = PCSkillTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "type");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator136", ok);

		assertEquals("GetTermEvaluator136 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator136 field type is correct ", "KNOWLEDGE", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator137()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SKILLTYPE=PERFORM]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator137 evaluator correct for " + term, t instanceof PCSkillTypeTermEvaluator);

		Class<?> uClass = PCSkillTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "type");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator137", ok);

		assertEquals("GetTermEvaluator137 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator137 field type is correct ", "PERFORM", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator138()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLBOOKS]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator138 evaluator correct for " + term, t instanceof PCCountSpellbookTermEvaluator);

		Class<?> uClass = PCCountSpellbookTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator138", ok);

		assertEquals("GetTermEvaluator138 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator139()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLBOOKS.broken";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator139 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator140()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLSINBOOK.1.0]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator140 evaluator correct for " + term, t instanceof PCCountSpellsInbookTermEvaluator);

		Class<?> uClass = PCCountSpellsInbookTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "book");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator140", ok);

		assertEquals("GetTermEvaluator140 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator140 field book is correct ", "1.0", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator141()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLSINBOOK.1.0";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator141 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator142()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLSKNOWN.0.0]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator142 evaluator correct for " + term, t instanceof PCCountSpellsKnownTermEvaluator);

		Class<?> uClass = PCCountSpellsKnownTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "nums");

		String field0 = "";
		int[] field1 = new int[]{};
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (int[]) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator142", ok);

		assertEquals("GetTermEvaluator142 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator142 field nums[0] is correct ",
			0,
			field1[0]);
		assertEquals("GetTermEvaluator142 field nums[1] is correct ",
			0,
			field1[1]);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator143()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLSKNOWN.0]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator143 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator144()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLSKNOWN.FOO]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator144 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator145()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLSKNOWN.FOO";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator145 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator146()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLTIMES.1.2.3.4]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator146 evaluator correct for " + term, t instanceof PCCountSpellTimesTermEvaluator);

		Class<?> uClass = PCCountSpellTimesTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "classNum");
		Field pF2 = (Field) TestHelper.findField(uClass, "bookNum");
		Field pF3 = (Field) TestHelper.findField(uClass, "spellLevel");
		Field pF4 = (Field) TestHelper.findField(uClass, "spellNumber");

		String field0 = "";
		Integer field1 = 0;
		Integer field2 = 0;
		Integer field3 = 0;
		Integer field4 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (Integer) pF1.get(t);
			field2 = (Integer) pF2.get(t);
			field3 = (Integer) pF3.get(t);
			field4 = (Integer) pF4.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator146", ok);

		assertEquals("GetTermEvaluator146 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator146 field classNum is correct ",
			1,
			field1.intValue());
		assertEquals("GetTermEvaluator146 field bookNum is correct ",
			2,
			field2.intValue());
		assertEquals("GetTermEvaluator146 field spellLevel is correct ",
			3,
			field3.intValue());
		assertEquals("GetTermEvaluator146 field spellNumber is correct ",
			4,
			field4.intValue());
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator147()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLSLEVELSINBOOK.1.2]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue(
				"GetTermEvaluator147 evaluator correct for " + term,
				t instanceof PCCountSpellsLevelsInBookTermEvaluator
		);

		Class<?> uClass = PCCountSpellsLevelsInBookTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "classNum");
		Field pF2 = (Field) TestHelper.findField(uClass, "sbookNum");

		String field0 = "";
		Integer field1 = 0;
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (Integer) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator147", ok);

		assertEquals("GetTermEvaluator147 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator147 field classNum is correct ",
			1,
			field1.intValue());
		assertEquals("GetTermEvaluator147 field sbookNum is correct ",
			2,
			field2.intValue());
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator148()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLSLEVELSINBOOK.1.2";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator148 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator149()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLSLEVELSINBOOK.0]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator149 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator150()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLTIMES.1.2.3.4";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator150 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator151()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLTIMES.1.2.3]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator151 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator152()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "EQTYPE.EQUIPPED.IS.FOO";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator152 evaluator correct for " + term, t instanceof PCEqTypeTermEvaluator);

		Class<?> uClass = PCEqTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator152", ok);

		assertEquals("GetTermEvaluator152 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator153()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "HASDEITY:Bane";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator153 evaluator correct for " + term, t instanceof PCHasDeityTermEvaluator);

		Class<?> uClass = PCHasDeityTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "deity");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator153", ok);

		assertEquals("GetTermEvaluator153 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator153 field deity is correct ", "Bane", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator154()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "HASFEAT:Endurance";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator154 evaluator correct for " + term, t instanceof PCHasFeatTermEvaluator);

		Class<?> uClass = PCHasFeatTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "feat");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator154", ok);

		assertEquals("GetTermEvaluator154 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator154 field feat is correct ", "Endurance", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator155()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "MODEQUIPSPELLFAILURE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator155 evaluator correct for " + term, t instanceof PCModEquipTermEvaluator);

		Class<?> uClass = PCModEquipTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "modEq");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator155", ok);

		assertEquals("GetTermEvaluator155 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator155 field modEq is correct ", "SPELLFAILURE", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator156()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "MOVE[Walk]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator156 evaluator correct for " + term, t instanceof PCMovementTermEvaluator);

		Class<?> uClass = PCMovementTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "movement");

		String field0 = "";
		MovementType field1 = null;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (MovementType) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator156", ok);

		assertEquals("GetTermEvaluator156 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator156 field movement is correct ", MovementType.getConstant("Walk"), field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator157()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "MOVE[Walk";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator157 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator158()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "PC.SIZE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator158 evaluator correct for " + term, t instanceof PCSizeTermEvaluator);

		Class<?> uClass = PCSizeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator158", ok);

		assertEquals("GetTermEvaluator158 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator159()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "SKILLRANK.Tumble";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator159 evaluator correct for " + term, t instanceof PCSkillRankTermEvaluator);

		Class<?> uClass = PCSkillRankTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "rank");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator159", ok);

		assertEquals("GetTermEvaluator159 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator159 field rank is correct ", "Tumble", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator160()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "SKILLRANK=Perform (Dance)";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator160 evaluator correct for " + term, t instanceof PCSkillRankTermEvaluator);

		Class<?> uClass = PCSkillRankTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "rank");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator160", ok);

		assertEquals("GetTermEvaluator160 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator160 field rank is correct ", "Perform (Dance)", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator161()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "SKILLRANK=Perform {Sing}";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator161 evaluator correct for " + term, t instanceof PCSkillRankTermEvaluator);

		Class<?> uClass = PCSkillRankTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "rank");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator161", ok);

		assertEquals("GetTermEvaluator161 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator161 field rank is correct ", "Perform (Sing)", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator162()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "SKILLTOTAL.Tumble";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator162 evaluator correct for " + term, t instanceof PCSkillTotalTermEvaluator);

		Class<?> uClass = PCSkillTotalTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "total");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator162", ok);

		assertEquals("GetTermEvaluator162 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator162 field total is correct ", "Tumble", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator163()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "SKILLTOTAL=Perform (Dance)";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator163 evaluator correct for " + term, t instanceof PCSkillTotalTermEvaluator);

		Class<?> uClass = PCSkillTotalTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "total");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator163", ok);

		assertEquals("GetTermEvaluator163 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator163 field total is correct ", "Perform (Dance)", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator164()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "SKILLTOTAL=Perform {Sing}";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator164 evaluator correct for " + term, t instanceof PCSkillTotalTermEvaluator);

		Class<?> uClass = PCSkillTotalTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "total");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator164", ok);

		assertEquals("GetTermEvaluator164 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator164 field total is correct ", "Perform (Sing)", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator165()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "VARDEFINED:MilkyBarsEaten";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator165 evaluator correct for " + term, t instanceof PCVarDefinedTermEvaluator);

		Class<?> uClass = PCVarDefinedTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "var");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator165", ok);

		assertEquals("GetTermEvaluator165 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator165 field var is correct ", "MilkyBarsEaten", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator166()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "WEIGHT.CARRIED";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator166 evaluator correct for " + term, t instanceof PCCarriedWeightTermEvaluator);

		Class<?> uClass = PCCarriedWeightTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator166", ok);

		assertEquals("GetTermEvaluator166 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator167()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "WEIGHT.EQUIPPED";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator167 evaluator correct for " + term, t instanceof PCCarriedWeightTermEvaluator);

		Class<?> uClass = PCCarriedWeightTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator167", ok);

		assertEquals("GetTermEvaluator167 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator168()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "WEIGHT.PC";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator168 evaluator correct for " + term, t instanceof PCWeightTermEvaluator);

		Class<?> uClass = PCWeightTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator168", ok);

		assertEquals("GetTermEvaluator168 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator169()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "WEIGHT.TOTAL";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator169 evaluator correct for " + term, t instanceof PCTotalWeightTermEvaluator);

		Class<?> uClass = PCTotalWeightTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator169", ok);

		assertEquals("GetTermEvaluator169 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator170()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "WEIGHT.NONSENSE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator170 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator171()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "STR";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		Assert.assertThat(
				"GetTermEvaluator171 evaluator correct for " + term,
				t,
				instanceOf(PCStatModTermEvaluator.class)
		);

		Class<?> uClass = PCStatModTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "statAbbrev");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator171", ok);

		assertEquals("GetTermEvaluator171 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator171 field statAbbrev is correct ", "STR", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator172()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "INT";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator172 evaluator correct for " + term, t instanceof PCStatModTermEvaluator);

		Class<?> uClass = PCStatModTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "statAbbrev");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator172", ok);

		assertEquals("GetTermEvaluator172 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator172 field statAbbrev is correct ", "INT", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator173()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "DEX";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator173 evaluator correct for " + term, t instanceof PCStatModTermEvaluator);

		Class<?> uClass = PCStatModTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "statAbbrev");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator173", ok);

		assertEquals("GetTermEvaluator173 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator173 field statAbbrev is correct ", "DEX", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator174()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "WIS";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator174 evaluator correct for " + term, t instanceof PCStatModTermEvaluator);

		Class<?> uClass = PCStatModTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "statAbbrev");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator174", ok);

		assertEquals("GetTermEvaluator174 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator174 field statAbbrev is correct ", "WIS", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator175()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "CON";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator175 evaluator correct for " + term, t instanceof PCStatModTermEvaluator);

		Class<?> uClass = PCStatModTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "statAbbrev");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator175", ok);

		assertEquals("GetTermEvaluator175 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator175 field statAbbrev is correct ", "CON", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator176()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "CHA";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator176 evaluator correct for " + term, t instanceof PCStatModTermEvaluator);

		Class<?> uClass = PCStatModTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "statAbbrev");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator176", ok);

		assertEquals("GetTermEvaluator176 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator176 field statAbbrev is correct ", "CHA", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator177()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "STRSCORE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator177 evaluator correct for " + term, t instanceof PCStatTotalTermEvaluator);

		Class<?> uClass = PCStatTotalTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "statAbbrev");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator177", ok);

		assertEquals("GetTermEvaluator177 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator177 field statAbbrev is correct ", "STR", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator178()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "INTSCORE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator178 evaluator correct for " + term, t instanceof PCStatTotalTermEvaluator);

		Class<?> uClass = PCStatTotalTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "statAbbrev");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator178", ok);

		assertEquals("GetTermEvaluator178 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator178 field statAbbrev is correct ", "INT", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator179()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "DEXSCORE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator179 evaluator correct for " + term, t instanceof PCStatTotalTermEvaluator);

		Class<?> uClass = PCStatTotalTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "statAbbrev");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator179", ok);

		assertEquals("GetTermEvaluator179 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator179 field statAbbrev is correct ", "DEX", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator180()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "WISSCORE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator180 evaluator correct for " + term, t instanceof PCStatTotalTermEvaluator);

		Class<?> uClass = PCStatTotalTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "statAbbrev");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator180", ok);

		assertEquals("GetTermEvaluator180 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator180 field statAbbrev is correct ", "WIS", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator181()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "CONSCORE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator181 evaluator correct for " + term, t instanceof PCStatTotalTermEvaluator);

		Class<?> uClass = PCStatTotalTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "statAbbrev");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator181", ok);

		assertEquals("GetTermEvaluator181 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator181 field statAbbrev is correct ", "CON", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator182()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "CHASCORE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator182 evaluator correct for " + term, t instanceof PCStatTotalTermEvaluator);

		Class<?> uClass = PCStatTotalTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "statAbbrev");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator182", ok);

		assertEquals("GetTermEvaluator182 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator182 field statAbbrev is correct ", "CHA", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator183()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "STR.BASE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator183 evaluator correct for " + term, t instanceof PCStatBaseTermEvaluator);

		Class<?> uClass = PCStatBaseTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "statAbbrev");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator183", ok);

		assertEquals("GetTermEvaluator183 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator183 field statAbbrev is correct ", "STR", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator184()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "INT.BASE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator184 evaluator correct for " + term, t instanceof PCStatBaseTermEvaluator);

		Class<?> uClass = PCStatBaseTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "statAbbrev");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator184", ok);

		assertEquals("GetTermEvaluator184 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator184 field statAbbrev is correct ", "INT", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator185()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "DEX.BASE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator185 evaluator correct for " + term, t instanceof PCStatBaseTermEvaluator);

		Class<?> uClass = PCStatBaseTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "statAbbrev");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator185", ok);

		assertEquals("GetTermEvaluator185 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator185 field statAbbrev is correct ", "DEX", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator186()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "WIS.BASE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator186 evaluator correct for " + term, t instanceof PCStatBaseTermEvaluator);

		Class<?> uClass = PCStatBaseTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "statAbbrev");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator186", ok);

		assertEquals("GetTermEvaluator186 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator186 field statAbbrev is correct ", "WIS", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator187()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "CON.BASE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator187 evaluator correct for " + term, t instanceof PCStatBaseTermEvaluator);

		Class<?> uClass = PCStatBaseTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "statAbbrev");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator187", ok);

		assertEquals("GetTermEvaluator187 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator187 field statAbbrev is correct ", "CON", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator188()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "CHA.BASE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator188 evaluator correct for " + term, t instanceof PCStatBaseTermEvaluator);

		Class<?> uClass = PCStatBaseTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "statAbbrev");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator188", ok);

		assertEquals("GetTermEvaluator188 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator188 field statAbbrev is correct ", "CHA", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator189()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "STRSCORE.BASE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator189 evaluator correct for " + term, t instanceof PCStatBaseTermEvaluator);

		Class<?> uClass = PCStatBaseTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "statAbbrev");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator189", ok);

		assertEquals("GetTermEvaluator189 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator189 field statAbbrev is correct ", "STR", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator190()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "INTSCORE.BASE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator190 evaluator correct for " + term, t instanceof PCStatBaseTermEvaluator);

		Class<?> uClass = PCStatBaseTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "statAbbrev");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator190", ok);

		assertEquals("GetTermEvaluator190 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator190 field statAbbrev is correct ", "INT", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator191()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "DEXSCORE.BASE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator191 evaluator correct for " + term, t instanceof PCStatBaseTermEvaluator);

		Class<?> uClass = PCStatBaseTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "statAbbrev");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator191", ok);

		assertEquals("GetTermEvaluator191 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator191 field statAbbrev is correct ", "DEX", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator192()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "WISSCORE.BASE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator192 evaluator correct for " + term, t instanceof PCStatBaseTermEvaluator);

		Class<?> uClass = PCStatBaseTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "statAbbrev");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator192", ok);

		assertEquals("GetTermEvaluator192 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator192 field statAbbrev is correct ", "WIS", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator193()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "CONSCORE.BASE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator193 evaluator correct for " + term, t instanceof PCStatBaseTermEvaluator);

		Class<?> uClass = PCStatBaseTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "statAbbrev");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator193", ok);

		assertEquals("GetTermEvaluator193 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator193 field statAbbrev is correct ", "CON", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator194()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "CHASCORE.BASE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator194 evaluator correct for " + term, t instanceof PCStatBaseTermEvaluator);

		Class<?> uClass = PCStatBaseTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "statAbbrev");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator194", ok);

		assertEquals("GetTermEvaluator194 stored term is correct " + term, term, field0);
		assertEquals("GetTermEvaluator194 field statAbbrev is correct ", "CHA", field1);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator195()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "STRENGTH";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator195 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator196()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "INTELLIGENCE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator196 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator197()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "DEXTERITY";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator197 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator198()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "WISDOM";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator198 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator199()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "CONSTITUTION";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator199 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator200()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "CHARMING";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		assertNull("GetTermEvaluator200 evaluator is null", t);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator201()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "BASECOST";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator201 evaluator correct for " + term, t instanceof EQBaseCostTermEvaluator);

		Class<?> uClass = EQBaseCostTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator201", ok);

		assertEquals("GetTermEvaluator201 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator202()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "CRITMULT";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator202 evaluator correct for " + term, t instanceof EQCritMultTermEvaluator);

		Class<?> uClass = EQCritMultTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator202", ok);

		assertEquals("GetTermEvaluator202 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator203()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "DMGDICE";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator203 evaluator correct for " + term, t instanceof EQDamageDiceTermEvaluator);

		Class<?> uClass = EQDamageDiceTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator203", ok);

		assertEquals("GetTermEvaluator203 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator204()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "DMGDIE";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator204 evaluator correct for " + term, t instanceof EQDamageDieTermEvaluator);

		Class<?> uClass = EQDamageDieTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator204", ok);

		assertEquals("GetTermEvaluator204 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator205()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "EQACCHECK";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator205 evaluator correct for " + term, t instanceof EQACCheckTermEvaluator);

		Class<?> uClass = EQACCheckTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator205", ok);

		assertEquals("GetTermEvaluator205 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator206()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "EQHANDS";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator206 evaluator correct for " + term, t instanceof EQHandsTermEvaluator);

		Class<?> uClass = EQHandsTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator206", ok);

		assertEquals("GetTermEvaluator206 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator207()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "EQSPELLFAIL";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator207 evaluator correct for " + term, t instanceof EQSpellFailureTermEvaluator);

		Class<?> uClass = EQSpellFailureTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator207", ok);

		assertEquals("GetTermEvaluator207 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator208()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "EQUIP.SIZE";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator208 evaluator correct for " + term, t instanceof EQEquipSizeTermEvaluator);

		Class<?> uClass = EQEquipSizeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator208", ok);

		assertEquals("GetTermEvaluator208 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator209()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "EQUIP.SIZE.INT";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator209 evaluator correct for " + term, t instanceof EQSizeTermEvaluator);

		Class<?> uClass = EQSizeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator209", ok);

		assertEquals("GetTermEvaluator209 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluatorAltPlusTotal()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "ALTPLUSTOTAL";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		assertTrue(
				"EQAltPlusTotalTermEvaluator evaluator correct for " + term,
				t instanceof EQAltPlusTotalTermEvaluator
		);

		Class<?> uClass = EQAltPlusTotalTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in testGetTermEvaluatorAltPlusTotal", ok);

		assertEquals("testGetTermEvaluatorAltPlusTotal stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluatorPlusTotal()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "PLUSTOTAL";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		assertTrue("EQPlusTotalTermEvaluator evaluator correct for " + term, t instanceof EQPlusTotalTermEvaluator);

		Class<?> uClass = EQPlusTotalTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in testGetTermEvaluatorPlusTotal", ok);

		assertEquals("testGetTermEvaluatorPlusTotal stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator210()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "RACEREACH";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "RACE:Gnome");

		assertTrue("GetTermEvaluator210 evaluator correct for " + term, t instanceof EQRaceReachTermEvaluator);

		Class<?> uClass = EQRaceReachTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator210", ok);

		assertEquals("GetTermEvaluator210 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator211()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "RANGE";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator211 evaluator correct for " + term, t instanceof EQRangeTermEvaluator);

		Class<?> uClass = EQRangeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator211", ok);

		assertEquals("GetTermEvaluator211 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator212()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "REACH";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator212 evaluator correct for " + term, t instanceof EQReachTermEvaluator);

		Class<?> uClass = EQReachTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator212", ok);

		assertEquals("GetTermEvaluator212 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator213()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "REACHMULT";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator213 evaluator correct for " + term, t instanceof EQReachMultTermEvaluator);

		Class<?> uClass = EQReachMultTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator213", ok);

		assertEquals("GetTermEvaluator213 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator214()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "SIZE";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator214 evaluator correct for " + term, t instanceof EQSizeTermEvaluator);

		Class<?> uClass = EQSizeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator214", ok);

		assertEquals("GetTermEvaluator214 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator215()
	{
		EvaluatorFactoryTest.loadAll();

		String term = "WT";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		assertTrue("GetTermEvaluator215 evaluator correct for " + term, t instanceof EQWeightTermEvaluator);

		Class<?> uClass = EQWeightTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException | IllegalAccessException e)
		{
			ok = false;
		}

		assertTrue("No illegal access in getTermEvaluator215", ok);

		assertEquals("GetTermEvaluator215 stored term is correct " + term, term, field0);
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	@Test
	public void testGetTermEvaluator216()
	{
		EvaluatorFactoryTest.loadAll();

		String term1 = "CL;BEFORELEVEL=15";

		TermEvaluator t1 = EvaluatorFactory.PC.getTermEvaluator(term1, "CLASS:Druid");
		TermEvaluator t2 = EvaluatorFactory.PC.getTermEvaluator(term1, "CLASS:Ranger");
		TermEvaluator t3 = EvaluatorFactory.PC.getTermEvaluator(term1, "CLASS:Druid");

		assertTrue("GetTermEvaluator215 t1 evaluator correct for " + term1, t1 instanceof PCCLBeforeLevelTermEvaluator);
		assertTrue("GetTermEvaluator215 t2 evaluator correct for " + term1, t2 instanceof PCCLBeforeLevelTermEvaluator);
		assertTrue("GetTermEvaluator215 t3 evaluator correct for " + term1, t3 instanceof PCCLBeforeLevelTermEvaluator);

		assertNotEquals("t1 and t2 are different objects", t1, t2);
		assertEquals("t1 and t3 are the Same object", t1, t3);
		assertNotEquals("t2 and t3 are different objects", t2, t3);

		String term2 = "CL;BEFORELEVEL=14";

		TermEvaluator t4 = EvaluatorFactory.PC.getTermEvaluator(term2, "CLASS:Druid");
		TermEvaluator t5 = EvaluatorFactory.PC.getTermEvaluator(term2, "CLASS:Ranger");
		TermEvaluator t6 = EvaluatorFactory.PC.getTermEvaluator(term2, "CLASS:Druid");

		assertTrue("GetTermEvaluator215 t4 evaluator correct for " + term2, t4 instanceof PCCLBeforeLevelTermEvaluator);
		assertTrue("GetTermEvaluator215 t5 evaluator correct for " + term2, t6 instanceof PCCLBeforeLevelTermEvaluator);
		assertTrue("GetTermEvaluator215 t6 evaluator correct for " + term2, t5 instanceof PCCLBeforeLevelTermEvaluator);

		assertEquals("t4 and t6 are the Same object", t4, t6);
		assertNotEquals("t4 and t5 are different objects", t4, t5);
		assertNotEquals("t6 and t5 are different objects", t6, t5);

		assertNotEquals("t1 and t4 are diffferent objects", t1, t4);
	}

	/**
	 * Initialise the data and game modes - does the parsing of the data, so 
	 * the plugins must be loaded before this method is called.
	 * 
	 * @deprecated This is calling Persistence Manager, so it should no longer be used
	 * CODE-1888
	 */
	@Deprecated
	public static void initGameModes()
	{
		SettingsHandler.setGame(SettingsHandler.getPCGenOption("game", "35e")); //$NON-NLS-1$
		SettingsHandler.game.clearLoadContext();

		BuildUtilities.buildUnselectedRace(Globals.getContext());
		AbstractReferenceContext rc = Globals.getContext().getReferenceContext();
		PCStat str = rc.constructCDOMObject(PCStat.class, "Strength");
		str.setKeyName("STR");
		rc.reassociateKey("STR", str);
		PCStat intel = rc.constructCDOMObject(PCStat.class, "Intelligence");
		intel.setKeyName("INT");
		rc.reassociateKey("INT", intel);
		PCStat dex = rc.constructCDOMObject(PCStat.class, "Dexterity");
		dex.setKeyName("DEX");
		rc.reassociateKey("DEX", dex);
		PCStat wis = rc.constructCDOMObject(PCStat.class, "Wisdom");
		wis.setKeyName("WIS");
		rc.reassociateKey("WIS", wis);
		PCStat con = rc.constructCDOMObject(PCStat.class, "Constitution");
		con.setKeyName("CON");
		rc.reassociateKey("CON", con);
		PCStat cha = rc.constructCDOMObject(PCStat.class, "Charisma");
		cha.setKeyName("CHA");
		rc.reassociateKey("CHA", cha);
	}

	/**
	 * Load and initialise the properties, plugins and GameModes
	 */
	private static void loadAll()
	{
		SettingsHandler.readOptionsProperties();
		SettingsHandler.getOptionsFromProperties(null);

		TestHelper.loadPlugins();
		EvaluatorFactoryTest.initGameModes();
	}


}
