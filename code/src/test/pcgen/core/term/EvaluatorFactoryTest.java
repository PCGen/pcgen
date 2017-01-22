package pcgen.core.term;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pcgen.PCGenTestCase;
import pcgen.cdom.base.Constants;
import pcgen.cdom.list.ClassSpellList;
import pcgen.core.Globals;
import pcgen.core.PCStat;
import pcgen.core.SettingsHandler;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.util.TestHelper;

/**
 * EvaluatorFactory Tester.
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
 *
 * Created 10/04/2008
 *
 */

public class EvaluatorFactoryTest extends PCGenTestCase
{

	public EvaluatorFactoryTest(String name) {
		super(name);
	}

    @Override
	public void setUp() throws Exception {
		super.setUp();
	}

    @Override
	public void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Method: constructor()
	 */
	public void testConstructor001() {

		EvaluatorFactoryTest.loadAll();
		Class<?> uClass = pcgen.core.term.EvaluatorFactory.class;

		Field pF = (Field) TestHelper.findField(uClass, "internalVarPattern");
		Field sF = (Field) TestHelper.findField(uClass, "BuilderStore");

		boolean ok;
		try
		{
			ok = true;
			//is(pF.get(null), eqnull(), "");
			//is(sF.get(null), eqnull(), "");


			Pattern iVP = (Pattern) pF.get(EvaluatorFactory.PC);
			// noinspection unchecked
			Map<String, TermEvaluatorBuilderPCVar> eS =
					(Map<String, TermEvaluatorBuilderPCVar>) sF.get(EvaluatorFactory.PC);

			// don't need instanceof, would throw ClassCastException
			is(iVP != null, eq(true), "Pattern is now instantiated");
			is(eS != null, eq(true), "Map is now instantiated");
		}
		catch (ClassCastException e)
		{
			ok = false;			
		}
		catch (IllegalAccessException e)
		{
			ok = false;			
		}

		is(ok, eq(true), "No illegal access in Constructor001");
	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor002");


		String term = "ACCHECK";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor002 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor002 pattern matches all of " + term);

		// "ACCHECK",			   COMPLETE_ACCHECK			    

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor003");

		String term = "ACHECK";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor003 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor003 pattern matches all of " + term);

		// "ACHECK",			   COMPLETE_ACCHECK

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor004");

		String term = "ARMORACCHECK";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor004 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor004 pattern matches all of " + term);

		// "ARMORACCHECK",		   COMPLETE_ARMORACCHECK

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor005");

		String term = "ARMORACHECK";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor005 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor005 pattern matches all of " + term);

		// "ARMORACHECK",		   COMPLETE_ARMORACCHECK

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor006");

		String term = "BAB";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor006 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor006 pattern matches all of " + term);

		// "BAB",			   COMPLETE_BAB

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor007");

		String term = "BASESPELLSTAT";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor007 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor007 pattern matches all of " + term);

		// "BASESPELLSTAT",		   COMPLETE_BASESPELLSTAT

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor008");

		String term = "BL";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor008 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor008 pattern matches start of " + term);

		// "BL",			   START_BL

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor009");

		String term = "BL.Wizard";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor009 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor009 pattern matches start of " + term);

		// "BL.Wizard",				 START_BL

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor010");

		String term = "BL=Cleric";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor010 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor010 pattern matches start of " + term);

		// "BL=Cleric",				 START_BL

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor011");

		String term = "CASTERLEVEL";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor011 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor011 pattern matches all of " + term);

		// "CASTERLEVEL",		   COMPLETE_CASTERLEVEL

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor012");

		String term = "CASTERLEVEL.TOTAL";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor012 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor012 pattern matches all of " + term);

		// "CASTERLEVEL.TOTAL",		   COMPLETE_CASTERLEVEL

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor013");

		String term = "CL";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor013 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor013 pattern matches start of " + term);

		// "CL",			   START_CL

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor014");

		String term = "CL.Bard";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor014 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor014 pattern matches start of " + term);

		// "CL.Bard",			       START_CL

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor015");

		String term = "CL;BEFORELEVEL.10";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor015 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor015 pattern matches start of " + term);

		// "CL;BEFORELEVEL.10",		     START_CL_BEFORELEVEL

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor016");

		String term = "CL;BEFORELEVEL=15";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor016 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor016 pattern matches start of " + term);

		// "CL;BEFORELEVEL=15",		     START_CL_BEFORELEVEL

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor017");

		String term = "CL=Rogue";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor017 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor017 pattern matches start of " + term);

		// "CL=Rogue",				START_CL

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor018");

		String term = "CLASS.Druid";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor018 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor018 pattern matches start of " + term);

		// "CLASS.Druid",			START_CLASS

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor019");

		String term = "CLASS=Paladin";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor019 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor019 pattern matches start of " + term);

		// "CLASS=Paladin",			  START_CLASS

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor020");

		String term = "CLASSLEVEL.Bard";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor020 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor020 pattern matches start of " + term);

		// "CLASSLEVEL.Bard",		       START_CLASSLEVEL

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor021");

		String term = "CLASSLEVEL=Rogue";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor021 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor021 pattern matches start of " + term);

		// "CLASSLEVEL=Rogue",			START_CLASSLEVEL

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor022");

		String term = "COUNT[ATTACKS]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor022 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor022 pattern matches all of " + term);

		// "COUNT[ATTACKS]",		   COMPLETE_COUNT_ATTACKS

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor023");

		String term = "COUNT[CHECKS]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor023 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor023 pattern matches all of " + term);

		// "COUNT[CHECKS]",		   COMPLETE_COUNT_CHECKS

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor024");

		String term = "COUNT[CLASSES]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor024 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor024 pattern matches all of " + term);

		// "COUNT[CLASSES]",		   COMPLETE_COUNT_CLASSES

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor025");

		String term = "COUNT[CONTAINERS]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor025 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor025 pattern matches all of " + term);

		// "COUNT[CONTAINERS]",		   COMPLETE_COUNT_CONTAINERS

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor026");

		String term = "COUNT[DOMAINS]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor026 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor026 pattern matches all of " + term);

		// "COUNT[DOMAINS]",		   COMPLETE_COUNT_DOMAINS

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor027");

		String term = "COUNT[EQTYPE.MERGENONE.IS.FOO]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor027 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor027 pattern matches start of " + term);

		// "COUNT[EQTYPE.MERGENONE.IS.FOO]",		    START_COUNT_EQTYPE

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor028");

		String term = "COUNT[EQUIPMENT.MERGENONE.NOT.FOO]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor028 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor028 pattern matches start of " + term);

		// "COUNT[EQUIPMENT.MERGENONE.NOT.FOO]",	      START_COUNT_EQUIPMENT

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor029");

		String term = "COUNT[FEATAUTOTYPE.HIDDEN]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor029 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor029 pattern matches start of " + term);

		// "COUNT[FEATAUTOTYPE.HIDDEN]",	  START_COUNT_FEATTYPE

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor030");

		String term = "COUNT[FEATAUTOTYPE=VISIBLE]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor030 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor030 pattern matches start of " + term);

		// "COUNT[FEATAUTOTYPE=VISIBLE]",	   START_COUNT_FEATTYPE

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor031");

		String term = "COUNT[FEATNAME.Jack of all trades]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor031 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor031 pattern matches start of " + term);

		// "COUNT[FEATNAME.Jack of all trades]",	      START_COUNT_FEATTYPE

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor032");

		String term = "COUNT[FEATNAME=Improved Initiative]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor032 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor032 pattern matches start of " + term);

		// "COUNT[FEATNAME=Improved Initiative]",	       START_COUNT_FEATTYPE

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor033");

		String term = "COUNT[FEATS.ALL]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor033 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor033 pattern matches all of " + term);

		// "COUNT[FEATS.ALL]",		   COMPLETE_COUNT_FEATSNATURENORMAL

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor034");

		String term = "COUNT[FEATS.HIDDEN]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor034 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor034 pattern matches all of " + term);

		// "COUNT[FEATS.HIDDEN]",	   COMPLETE_COUNT_FEATSNATURENORMAL

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor035");

		String term = "COUNT[FEATS.VISIBLE]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor035 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor035 pattern matches all of " + term);

		// "COUNT[FEATS.VISIBLE]",	   COMPLETE_COUNT_FEATSNATURENORMAL

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor036");

		String term = "COUNT[FEATSALL.ALL]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor036 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor036 pattern matches all of " + term);

		// "COUNT[FEATSALL.ALL]",	   COMPLETE_COUNT_FEATSNATUREALL

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor037");

		String term = "COUNT[FEATSALL.HIDDEN]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor037 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor037 pattern matches all of " + term);

		// "COUNT[FEATSALL.HIDDEN]",	   COMPLETE_COUNT_FEATSNATUREALL

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor038");

		String term = "COUNT[FEATSALL.VISIBLE]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor038 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor038 pattern matches all of " + term);

		// "COUNT[FEATSALL.VISIBLE]",	   COMPLETE_COUNT_FEATSNATUREALL

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor039");

		String term = "COUNT[FEATSALL]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor039 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor039 pattern matches all of " + term);

		// "COUNT[FEATSALL]",		   COMPLETE_COUNT_FEATSNATUREALL

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor040");

		String term = "COUNT[FEATSAUTO.ALL]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor040 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor040 pattern matches all of " + term);

		// "COUNT[FEATSAUTO.ALL]",	   COMPLETE_COUNT_FEATSNATUREAUTO

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor041");

		String term = "COUNT[FEATSAUTO.HIDDEN]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor041 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor041 pattern matches all of " + term);

		// "COUNT[FEATSAUTO.HIDDEN]",	   COMPLETE_COUNT_FEATSNATUREAUTO

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor042");

		String term = "COUNT[FEATSAUTO.VISIBLE]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor042 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor042 pattern matches all of " + term);

		// "COUNT[FEATSAUTO.VISIBLE]",	   COMPLETE_COUNT_FEATSNATUREAUTO

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor043");

		String term = "COUNT[FEATSAUTO]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor043 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor043 pattern matches all of " + term);

		// "COUNT[FEATSAUTO]",		   COMPLETE_COUNT_FEATSNATUREAUTO

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor044");

		String term = "COUNT[FEATS]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor044 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor044 pattern matches all of " + term);

		// "COUNT[FEATS]",		   COMPLETE_COUNT_FEATSNATURENORMAL

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor045");

		String term = "COUNT[FEATTYPE.BAR]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor045 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor045 pattern matches start of " + term);

		// "COUNT[FEATTYPE.BAR]",	       START_COUNT_FEATTYPE

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor046");

		String term = "COUNT[FEATTYPE.BAZ]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor046 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor046 pattern matches start of " + term);

		// "COUNT[FEATTYPE.BAZ]",	       START_COUNT_FEATTYPE

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor047");

		String term = "COUNT[FOLLOWERS]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor047 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor047 pattern matches all of " + term);

		// "COUNT[FOLLOWERS]",		   COMPLETE_COUNT_FOLLOWERS

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor048");

		String term = "COUNT[FOLLOWERTYPE.MOO]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor048 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor048 pattern matches start of " + term);

		// "COUNT[FOLLOWERTYPE.MOO]",	       START_COUNT_FOLLOWERTYPE

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor049");

		String term = "COUNT[LANGUAGES]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor049 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor049 pattern matches all of " + term);

		// "COUNT[LANGUAGES]",		   COMPLETE_COUNT_LANGUAGES

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor050");

		String term = "COUNT[MISC.COMPANIONS]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor050 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor050 pattern matches all of " + term);

		// "COUNT[MISC.COMPANIONS]",	   COMPLETE_COUNT_MISC_COMPANIONS

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor051");

		String term = "COUNT[MISC.FUNDS]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor051 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor051 pattern matches all of " + term);

		// "COUNT[MISC.FUNDS]",		   COMPLETE_COUNT_MISC_FUNDS

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor052");

		String term = "COUNT[MISC.MAGIC]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor052 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor052 pattern matches all of " + term);

		// "COUNT[MISC.MAGIC]",		   COMPLETE_COUNT_MISC_MAGIC

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor053");

		String term = "COUNT[MOVE]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor053 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor053 pattern matches all of " + term);

		// "COUNT[MOVE]",		   COMPLETE_COUNT_MOVE

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor054");

		String term = "COUNT[NOTES]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor054 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor054 pattern matches all of " + term);

		// "COUNT[NOTES]",		   COMPLETE_COUNT_NOTES

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor055");

		String term = "COUNT[RACESUBTYPES]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor055 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor055 pattern matches all of " + term);

		// "COUNT[RACESUBTYPES]",	   COMPLETE_COUNT_RACESUBTYPES

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor056");

		String term = "COUNT[SA]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor056 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor056 pattern matches all of " + term);

		// "COUNT[SA]",			   COMPLETE_COUNT_SA

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor057");

		String term = "COUNT[SKILLS]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor057 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor057 pattern matches all of " + term);

		// "COUNT[SKILLS]",		   COMPLETE_COUNT_SKILLS

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor058");

		String term = "COUNT[SKILLTYPE.KNOWLEDGE]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor058 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor058 pattern matches start of " + term);

		// "COUNT[SKILLTYPE.KNOWLEDGE]",	     START_COUNT_SKILLTYPE

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor059");

		String term = "COUNT[SKILLTYPE=PERFORM]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor059 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor059 pattern matches start of " + term);

		// "COUNT[SKILLTYPE=PERFORM]",		   START_COUNT_SKILLTYPE

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor060");

		String term = "COUNT[SPELLBOOKS]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor060 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor060 pattern matches start of " + term);

		// "COUNT[SPELLBOOKS]",		    START_COUNT_SPELLBOOKS

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor061");

		String term = "COUNT[SPELLCLASSES]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor061 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor061 pattern matches all of " + term);

		// "COUNT[SPELLCLASSES]",	   COMPLETE_COUNT_SPELLCLASSES

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor062");

		String term = "COUNT[SPELLRACE]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor062 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor062 pattern matches all of " + term);

		// "COUNT[SPELLRACE]",		   COMPLETE_COUNT_SPELLRACE

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor063");

		String term = "COUNT[SPELLSINBOOK]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor063 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor063 pattern matches start of " + term);

		// "COUNT[SPELLSINBOOK]",	    START_COUNT_SPELLSINBOOK

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor064");

		String term = "COUNT[SPELLSKNOWN]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor064 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor064 pattern matches start of " + term);

		// "COUNT[SPELLSKNOWN]",	    START_COUNT_SPELLSKNOWN

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor065");

		String term = "COUNT[SPELLSLEVELSINBOOK]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor065 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor065 pattern matches start of " + term);

		// "COUNT[SPELLSLEVELSINBOOK]",     START_COUNT_SPELLSLEVELSINBOOK

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor066");

		String term = "COUNT[SPELLTIMES]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor066 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor066 pattern matches start of " + term);

		// "COUNT[SPELLTIMES]",		    START_COUNT_SPELLTIMES

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor067");

		String term = "COUNT[STATS]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor067 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor067 pattern matches all of " + term);

		// "COUNT[STATS]",		   COMPLETE_COUNT_STATS

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor068");

		String term = "COUNT[TEMPBONUSNAMES]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor068 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor068 pattern matches all of " + term);

		// "COUNT[TEMPBONUSNAMES]",	   COMPLETE_COUNT_TEMPBONUSNAMES

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor069");

		String term = "COUNT[TEMPLATES]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor069 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor069 pattern matches all of " + term);

		// "COUNT[TEMPLATES]",		   COMPLETE_COUNT_TEMPLATES

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor070");

		String term = "COUNT[VFEATS.ALL]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor070 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor070 pattern matches all of " + term);

		// "COUNT[VFEATS.ALL]",		   COMPLETE_COUNT_FEATSNATUREVIRTUAL

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor071");

		String term = "COUNT[VFEATS.HIDDEN]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor071 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor071 pattern matches all of " + term);

		// "COUNT[VFEATS.HIDDEN]",	   COMPLETE_COUNT_FEATSNATUREVIRTUAL

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor072");

		String term = "COUNT[VFEATS.VISIBLE]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor072 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor072 pattern matches all of " + term);

		// "COUNT[VFEATS.VISIBLE]",	   COMPLETE_COUNT_FEATSNATUREVIRTUAL

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor073");

		String term = "COUNT[VFEATS]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor073 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor073 pattern matches all of " + term);

		// "COUNT[VFEATS]",		   COMPLETE_COUNT_FEATSNATUREVIRTUAL

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor074");

		String term = "COUNT[VFEATTYPE.HIDDEN]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor074 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor074 pattern matches start of " + term);

		// "COUNT[VFEATTYPE.HIDDEN]",		  START_COUNT_FEATTYPE

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor075");

		String term = "COUNT[VFEATTYPE=ALL]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor075 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor075 pattern matches start of " + term);

		// "COUNT[VFEATTYPE=ALL]",	       START_COUNT_FEATTYPE

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor076");

		String term = "COUNT[VISIBLETEMPLATES]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor076 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor076 pattern matches all of " + term);

		// "COUNT[VISIBLETEMPLATES]",	   COMPLETE_COUNT_VISIBLETEMPLATES

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor077");

		String term = "COUNT[VISION]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor077 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor077 pattern matches all of " + term);

		// "COUNT[VISION]",		   COMPLETE_COUNT_VISION

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor078");

		String term = "ENCUMBERANCE";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor078 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor078 pattern matches all of " + term);

		// "ENCUMBERANCE",		   COMPLETE_ENCUMBERANCE

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor079");

		String term = "EQTYPE.EQUIPPED.IS.FOO";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor079 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor079 pattern matches start of " + term);

		// "EQTYPE.EQUIPPED.IS.FOO",			   START_EQTYPE

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor080");

		String term = "HASDEITY:Bane";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor080 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor080 pattern matches start of " + term);

		// "HASDEITY:Bane",		       START_HASDEITY

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor081");

		String term = "HASFEAT:Endurance";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor081 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor081 pattern matches start of " + term);

		// "HASFEAT:Endurance",			    START_HASFEAT

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor082");

		String term = "HD";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor082 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor082 pattern matches all of " + term);

		// "HD",			   COMPLETE_HD

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor083");

		String term = "MAXCASTABLE";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor083 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor083 pattern matches all of " + term);

		// "MAXCASTABLE",		   COMPLETE_MAXCASTABLE

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor084");

		String term = "MODEQUIPSPELLFAILURE";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor084 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor084 pattern matches start of " + term);

		// "MODEQUIPSPELLFAILURE",		       START_MODEQUIP

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor085");

		String term = "MOVEBASE";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor085 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor085 pattern matches all of " + term);

		// "MOVEBASE",			   COMPLETE_MOVEBASE

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor086");

		String term = "MOVE[Walk]";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor086 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor086 pattern matches start of " + term);

		// "MOVE[Walk]",			START_MOVE

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor087");

		String term = "PC.HEIGHT";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor087 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor087 pattern matches all of " + term);

		// "PC.HEIGHT",			   COMPLETE_PC_HEIGHT

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor088");

		String term = "PC.SIZEINT";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor088 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor088 pattern matches start of " + term);

		// "PC.SIZEINT",		      START_PC_SIZE

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor089");

		String term = "PC.WEIGHT";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor089 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor089 pattern matches all of " + term);

		// "PC.WEIGHT",			   COMPLETE_PC_WEIGHT

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor090");

		String term = "PROFACCHECK";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor090 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor090 pattern matches all of " + term);

		// "PROFACCHECK",		   COMPLETE_PROFACCHECK

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor091");

		String term = "RACESIZE";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor091 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor091 pattern matches all of " + term);

		// "RACESIZE",			   COMPLETE_RACESIZE

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor092");

		String term = "SCORE";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor092 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor092 pattern matches all of " + term);

		// "SCORE",			   COMPLETE_SCORE

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor093");

		String term = "SHIELDACCHECK";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor093 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor093 pattern matches all of " + term);

		// "SHIELDACCHECK",		   COMPLETE_SHIELDACCHECK

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor094");

		String term = "SHIELDACHECK";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor094 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor094 pattern matches all of " + term);

		// "SHIELDACHECK",		   COMPLETE_SHIELDACCHECK

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor095");

		String term = "SIZE";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor095 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor095 pattern matches all of " + term);

		// "SIZE",			   COMPLETE_SIZEMOD

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor096");

		String term = "SIZEMOD";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor096 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor096 pattern matches all of " + term);

		// "SIZEMOD",			   COMPLETE_SIZEMOD

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor097");

		String term = "SKILLRANK.Tumble";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor097 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor097 pattern matches start of " + term);

		// "SKILLRANK.Tumble",			 START_SKILLRANK

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor098");

		String term = "SKILLRANK=Perform (Dance)";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor098 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor098 pattern matches start of " + term);

		// "SKILLRANK=Perform (Dance)",			  START_SKILLRANK

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor099");

		String term = "SKILLTOTAL.Tumble";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor099 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor099 pattern matches start of " + term);

		// "SKILLTOTAL.Tumble",			 START_SKILLTOTAL

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor100");

		String term = "SKILLTOTAL=Perform (Dance)";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor100 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor100 pattern matches start of " + term);

		// "SKILLTOTAL=Perform (Dance)",		  START_SKILLTOTAL

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor101");

		String term = "SPELLBASESTAT";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor101 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor101 pattern matches all of " + term);

		// "SPELLBASESTAT",		   COMPLETE_SPELLBASESTAT

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor102");

		String term = "SPELLBASESTATSCORE";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor102 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor102 pattern matches all of " + term);

		// "SPELLBASESTATSCORE",	   COMPLETE_SPELLBASESTAT

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor103");

		String term = "SPELLLEVEL";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor103 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor103 pattern matches all of " + term);

		// "SPELLLEVEL",		   COMPLETE_SPELLLEVEL

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor104");

		String term = "TL";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor104 pattern matches for " + term);
		is(mat.group(1), strEq(term), "Constructor104 pattern matches all of " + term);

		// "TL",			   COMPLETE_TL

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor105");

		String term = "VARDEFINED:MilkyBarsEaten";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor105 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor105 pattern matches start of " + term);

		// "VARDEFINED:MilkyBarsEaten",			 START_VARDEFINED

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor106");

		String term = "WEIGHT.CARRIED";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor106 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor106 pattern matches start of " + term);

		// "WEIGHT.CARRIED",			  START_WEIGHT

	}

	/**
	 * Method: constructor()
	 */
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in Constructor107");

		String term = "DEXSCORE";
		Matcher mat = iVP.matcher(term); 
		is(mat.find(), eq(true), "Constructor107 pattern matches for " + term);
		is(term.startsWith(mat.group(1)), eq(true), "Constructor107 pattern matches start of " + term);

		// "DEXSCORE",			      START_STAT			       

	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator001() {
		EvaluatorFactoryTest.loadAll();

		String term = "ACCHECK";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCACcheckTermEvaluator, eq(true),
		   "GetTermEvaluator001 evaluator correct for " + term);

		Class<?> uClass = PCACcheckTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator001");

		is(field0, strEq(term), "GetTermEvaluator001 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator002() {
		EvaluatorFactoryTest.loadAll();

		String term = "ACHECK";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCACcheckTermEvaluator, eq(true),
		   "GetTermEvaluator002 evaluator correct for " + term);

		Class<?> uClass = PCACcheckTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator002");

		is(field0, strEq(term), "GetTermEvaluator002 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator003() {
		EvaluatorFactoryTest.loadAll();

		String term = "ARMORACCHECK";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCArmourACcheckTermEvaluator, eq(true),
		   "GetTermEvaluator003 evaluator correct for " + term);

		Class<?> uClass = PCArmourACcheckTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator003");

		is(field0, strEq(term), "GetTermEvaluator003 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator004() {
		EvaluatorFactoryTest.loadAll();

		String term = "ARMORACHECK";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCArmourACcheckTermEvaluator, eq(true),
		   "GetTermEvaluator004 evaluator correct for " + term);

		Class<?> uClass = PCArmourACcheckTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator004");

		is(field0, strEq(term), "GetTermEvaluator004 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator005() {
		EvaluatorFactoryTest.loadAll();

		String term = "BAB";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCBABTermEvaluator, eq(true),
		   "GetTermEvaluator005 evaluator correct for " + term);

		Class<?> uClass = PCBABTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator005");

		is(field0, strEq(term), "GetTermEvaluator005 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator006() {
		EvaluatorFactoryTest.loadAll();

		String term = "BASESPELLSTAT";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "CLASS:Foo");

		is(t instanceof PCBaseSpellStatTermEvaluator, eq(true),
		   "GetTermEvaluator006 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator006");

		is(field0, strEq(term), "GetTermEvaluator006 stored term is correct " + term);	       
		is(field1, strEq("Foo"), "GetTermEvaluator006 field source is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator007() {
		EvaluatorFactoryTest.loadAll();

		String term = "BASESPELLSTAT";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "RACE:Bar");

		is(t instanceof PCBaseSpellStatTermEvaluator, eq(true),
		   "GetTermEvaluator007 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator007");

		is(field0, strEq(term), "GetTermEvaluator007 stored term is correct " + term);	       
		is(field1, strEq(""), "GetTermEvaluator007 field source is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator008() {
		EvaluatorFactoryTest.loadAll();

		String term = "CASTERLEVEL";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "RACE:Bar");

		is(t instanceof PCCasterLevelRaceTermEvaluator, eq(true),
		   "GetTermEvaluator008 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator008");

		is(field0, strEq(term), "GetTermEvaluator008 stored term is correct " + term);	       
		is(field1, strEq("RACE.Bar"), "GetTermEvaluator008 field source is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator009() {
		EvaluatorFactoryTest.loadAll();

		String term = "CASTERLEVEL";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "CLASS:Foo");

		is(t instanceof PCCasterLevelClassTermEvaluator, eq(true),
		   "GetTermEvaluator009 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator009");

		is(field0, strEq(term), "GetTermEvaluator009 stored term is correct " + term);	       
		is(field1, strEq("Foo"), "GetTermEvaluator009 field source is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator010() {
		EvaluatorFactoryTest.loadAll();

		String term = "CASTERLEVEL";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "SPELL:Baz");

		is(t instanceof PCCasterLevelTotalTermEvaluator, eq(true),
		   "GetTermEvaluator010 evaluator correct for " + term);

		Class<?> uClass = PCCasterLevelTotalTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator010");

		is(field0, strEq(term), "GetTermEvaluator010 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator011() {
		EvaluatorFactoryTest.loadAll();

		String term = "CASTERLEVEL.TOTAL";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "RACE:Bar");

		is(t instanceof PCCasterLevelTotalTermEvaluator, eq(true),
		   "GetTermEvaluator011 evaluator correct for " + term);

		Class<?> uClass = PCCasterLevelTotalTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator011");

		is(field0, strEq(term), "GetTermEvaluator011 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator012() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[ATTACKS]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountAttacksTermEvaluator, eq(true),
		   "GetTermEvaluator012 evaluator correct for " + term);

		Class<?> uClass = PCCountAttacksTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator012");

		is(field0, strEq(term), "GetTermEvaluator012 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator013() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[CHECKS]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountChecksTermEvaluator, eq(true),
		   "GetTermEvaluator013 evaluator correct for " + term);

		Class<?> uClass = PCCountChecksTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator013");

		is(field0, strEq(term), "GetTermEvaluator013 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator014() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[CLASSES]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountClassesTermEvaluator, eq(true),
		   "GetTermEvaluator014 evaluator correct for " + term);

		Class<?> uClass = PCCountClassesTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator014");

		is(field0, strEq(term), "GetTermEvaluator014 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator015() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[CONTAINERS]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountContainersTermEvaluator, eq(true),
		   "GetTermEvaluator015 evaluator correct for " + term);

		Class<?> uClass = PCCountContainersTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator015");

		is(field0, strEq(term), "GetTermEvaluator015 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator016() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[DOMAINS]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountDomainsTermEvaluator, eq(true),
		   "GetTermEvaluator016 evaluator correct for " + term);

		Class<?> uClass = PCCountDomainsTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator016");

		is(field0, strEq(term), "GetTermEvaluator016 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator017() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATSALL.ALL]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountAbilitiesNatureAllTermEvaluator, eq(true),
		   "GetTermEvaluator017 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator017");

		is(field0, strEq(term), "GetTermEvaluator017 stored term is correct " + term);	       
		is(field1, eq(true), "GetTermEvaluator017 field hidden is correct ");	       
		is(field2, eq(true), "GetTermEvaluator017 field visible is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator018() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATSALL.HIDDEN]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountAbilitiesNatureAllTermEvaluator, eq(true),
		   "GetTermEvaluator018 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator018");

		is(field0, strEq(term), "GetTermEvaluator018 stored term is correct " + term);	       
		is(field1, eq(true), "GetTermEvaluator018 field hidden is correct ");	       
		is(field2, eq(false), "GetTermEvaluator018 field visible is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator019() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATSALL.VISIBLE]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountAbilitiesNatureAllTermEvaluator, eq(true),
		   "GetTermEvaluator019 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator019");

		is(field0, strEq(term), "GetTermEvaluator019 stored term is correct " + term);	       
		is(field1, eq(false), "GetTermEvaluator019 field hidden is correct ");	       
		is(field2, eq(true), "GetTermEvaluator019 field visible is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator020() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATSALL]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountAbilitiesNatureAllTermEvaluator, eq(true),
		   "GetTermEvaluator020 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator020");

		is(field0, strEq(term), "GetTermEvaluator020 stored term is correct " + term);	       
		is(field1, eq(false), "GetTermEvaluator020 field hidden is correct ");	       
		is(field2, eq(true), "GetTermEvaluator020 field visible is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator021() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATSAUTO.ALL]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountAbilitiesNatureAutoTermEvaluator, eq(true),
		   "GetTermEvaluator021 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator021");

		is(field0, strEq(term), "GetTermEvaluator021 stored term is correct " + term);	       
		is(field1, eq(true), "GetTermEvaluator021 field hidden is correct ");	       
		is(field2, eq(true), "GetTermEvaluator021 field visible is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator022() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATSAUTO.HIDDEN]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountAbilitiesNatureAutoTermEvaluator, eq(true),
		   "GetTermEvaluator022 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator022");

		is(field0, strEq(term), "GetTermEvaluator022 stored term is correct " + term);	       
		is(field1, eq(true), "GetTermEvaluator022 field hidden is correct ");	       
		is(field2, eq(false), "GetTermEvaluator022 field visible is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator023() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATSAUTO.VISIBLE]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountAbilitiesNatureAutoTermEvaluator, eq(true),
		   "GetTermEvaluator023 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator023");

		is(field0, strEq(term), "GetTermEvaluator023 stored term is correct " + term);	       
		is(field1, eq(false), "GetTermEvaluator023 field hidden is correct ");	       
		is(field2, eq(true), "GetTermEvaluator023 field visible is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator024() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATSAUTO]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountAbilitiesNatureAutoTermEvaluator, eq(true),
		   "GetTermEvaluator024 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator024");

		is(field0, strEq(term), "GetTermEvaluator024 stored term is correct " + term);	       
		is(field1, eq(false), "GetTermEvaluator024 field hidden is correct ");	       
		is(field2, eq(true), "GetTermEvaluator024 field visible is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator025() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATS.ALL]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountAbilitiesNatureNormalTermEvaluator, eq(true),
		   "GetTermEvaluator025 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator025");

		is(field0, strEq(term), "GetTermEvaluator025 stored term is correct " + term);	       
		is(field1, eq(true), "GetTermEvaluator025 field hidden is correct ");	       
		is(field2, eq(true), "GetTermEvaluator025 field visible is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator026() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATS.HIDDEN]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountAbilitiesNatureNormalTermEvaluator, eq(true),
		   "GetTermEvaluator026 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator026");

		is(field0, strEq(term), "GetTermEvaluator026 stored term is correct " + term);	       
		is(field1, eq(true), "GetTermEvaluator026 field hidden is correct ");	       
		is(field2, eq(false), "GetTermEvaluator026 field visible is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator027() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATS.VISIBLE]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountAbilitiesNatureNormalTermEvaluator, eq(true),
		   "GetTermEvaluator027 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator027");

		is(field0, strEq(term), "GetTermEvaluator027 stored term is correct " + term);	       
		is(field1, eq(false), "GetTermEvaluator027 field hidden is correct ");	       
		is(field2, eq(true), "GetTermEvaluator027 field visible is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator028() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATS]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountAbilitiesNatureNormalTermEvaluator, eq(true),
		   "GetTermEvaluator028 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator028");

		is(field0, strEq(term), "GetTermEvaluator028 stored term is correct " + term);	       
		is(field1, eq(false), "GetTermEvaluator028 field hidden is correct ");	       
		is(field2, eq(true), "GetTermEvaluator028 field visible is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator029() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[VFEATS.ALL]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountAbilitiesNatureVirtualTermEvaluator, eq(true),
		   "GetTermEvaluator029 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator029");

		is(field0, strEq(term), "GetTermEvaluator029 stored term is correct " + term);	       
		is(field1, eq(true), "GetTermEvaluator029 field hidden is correct ");	       
		is(field2, eq(true), "GetTermEvaluator029 field visible is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator030() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[VFEATS.HIDDEN]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountAbilitiesNatureVirtualTermEvaluator, eq(true),
		   "GetTermEvaluator030 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator030");

		is(field0, strEq(term), "GetTermEvaluator030 stored term is correct " + term);	       
		is(field1, eq(true), "GetTermEvaluator030 field hidden is correct ");	       
		is(field2, eq(false), "GetTermEvaluator030 field visible is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator031() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[VFEATS.VISIBLE]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountAbilitiesNatureVirtualTermEvaluator, eq(true),
		   "GetTermEvaluator031 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator031");

		is(field0, strEq(term), "GetTermEvaluator031 stored term is correct " + term);	       
		is(field1, eq(false), "GetTermEvaluator031 field hidden is correct ");	       
		is(field2, eq(true), "GetTermEvaluator031 field visible is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator032() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[VFEATS]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountAbilitiesNatureVirtualTermEvaluator, eq(true),
		   "GetTermEvaluator032 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator032");

		is(field0, strEq(term), "GetTermEvaluator032 stored term is correct " + term);	       
		is(field1, eq(false), "GetTermEvaluator032 field hidden is correct ");	       
		is(field2, eq(true), "GetTermEvaluator032 field visible is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator033() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FOLLOWERS]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountFollowersTermEvaluator, eq(true),
		   "GetTermEvaluator033 evaluator correct for " + term);

		Class<?> uClass = PCCountFollowersTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator033");

		is(field0, strEq(term), "GetTermEvaluator033 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator034() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[LANGUAGES]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountLanguagesTermEvaluator, eq(true),
		   "GetTermEvaluator034 evaluator correct for " + term);

		Class<?> uClass = PCCountLanguagesTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator034");

		is(field0, strEq(term), "GetTermEvaluator034 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator035() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[MISC.COMPANIONS]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountMiscCompanionsTermEvaluator, eq(true),
		   "GetTermEvaluator035 evaluator correct for " + term);

		Class<?> uClass = PCCountMiscCompanionsTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator035");

		is(field0, strEq(term), "GetTermEvaluator035 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator036() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[MISC.FUNDS]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountMiscFundsTermEvaluator, eq(true),
		   "GetTermEvaluator036 evaluator correct for " + term);

		Class<?> uClass = PCCountMiscFundsTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator036");

		is(field0, strEq(term), "GetTermEvaluator036 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator037() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[MISC.MAGIC]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountMiscMagicTermEvaluator, eq(true),
		   "GetTermEvaluator037 evaluator correct for " + term);

		Class<?> uClass = PCCountMiscMagicTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator037");

		is(field0, strEq(term), "GetTermEvaluator037 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator038() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[MOVE]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountMoveTermEvaluator, eq(true),
		   "GetTermEvaluator038 evaluator correct for " + term);

		Class<?> uClass = PCCountMoveTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator038");

		is(field0, strEq(term), "GetTermEvaluator038 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator039() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[NOTES]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountNotesTermEvaluator, eq(true),
		   "GetTermEvaluator039 evaluator correct for " + term);

		Class<?> uClass = PCCountNotesTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator039");

		is(field0, strEq(term), "GetTermEvaluator039 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator040() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[RACESUBTYPES]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountRaceSubTypesTermEvaluator, eq(true),
		   "GetTermEvaluator040 evaluator correct for " + term);

		Class<?> uClass = PCCountRaceSubTypesTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator040");

		is(field0, strEq(term), "GetTermEvaluator040 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator041() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SA]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountSABTermEvaluator, eq(true),
		   "GetTermEvaluator041 evaluator correct for " + term);

		Class<?> uClass = PCCountSABTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator041");

		is(field0, strEq(term), "GetTermEvaluator041 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator042() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SKILLS]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountSkillsTermEvaluator, eq(true),
		   "GetTermEvaluator042 evaluator correct for " + term);

		Class<?> uClass = PCCountSkillsTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator042");

		is(field0, strEq(term), "GetTermEvaluator042 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator043() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLCLASSES]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountSpellClassesTermEvaluator, eq(true),
		   "GetTermEvaluator043 evaluator correct for " + term);

		Class<?> uClass = PCCountSpellClassesTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator043");

		is(field0, strEq(term), "GetTermEvaluator043 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator044() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLRACE]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountSpellRaceTermEvaluator, eq(true),
		   "GetTermEvaluator044 evaluator correct for " + term);

		Class<?> uClass = PCCountSpellRaceTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator044");

		is(field0, strEq(term), "GetTermEvaluator044 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator045() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[STATS]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountStatsTermEvaluator, eq(true),
		   "GetTermEvaluator045 evaluator correct for " + term);

		Class<?> uClass = PCCountStatsTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator045");

		is(field0, strEq(term), "GetTermEvaluator045 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator046() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[TEMPBONUSNAMES]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountTempBonusNamesTermEvaluator, eq(true),
		   "GetTermEvaluator046 evaluator correct for " + term);

		Class<?> uClass = PCCountTempBonusNamesTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator046");

		is(field0, strEq(term), "GetTermEvaluator046 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator047() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[TEMPLATES]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountTemplatesTermEvaluator, eq(true),
		   "GetTermEvaluator047 evaluator correct for " + term);

		Class<?> uClass = PCCountTemplatesTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator047");

		is(field0, strEq(term), "GetTermEvaluator047 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator048() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[VISIBLETEMPLATES]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountVisibleTemplatesTermEvaluator, eq(true),
		   "GetTermEvaluator048 evaluator correct for " + term);

		Class<?> uClass = PCCountVisibleTemplatesTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator048");

		is(field0, strEq(term), "GetTermEvaluator048 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator049() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[VISION]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountVisionTermEvaluator, eq(true),
		   "GetTermEvaluator049 evaluator correct for " + term);

		Class<?> uClass = PCCountVisionTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator049");

		is(field0, strEq(term), "GetTermEvaluator049 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator050() {
		EvaluatorFactoryTest.loadAll();

		String term = "ENCUMBERANCE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCEncumberanceTermEvaluator, eq(true),
		   "GetTermEvaluator050 evaluator correct for " + term);

		Class<?> uClass = PCEncumberanceTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator050");

		is(field0, strEq(term), "GetTermEvaluator050 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator051() {
		EvaluatorFactoryTest.loadAll();

		String term = "HD";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCHDTermEvaluator, eq(true),
		   "GetTermEvaluator051 evaluator correct for " + term);

		Class<?> uClass = PCHDTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator051");

		is(field0, strEq(term), "GetTermEvaluator051 stored term is correct " + term);	       
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluatorHp() {
		EvaluatorFactoryTest.loadAll();

		String term = "HP";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCHPTermEvaluator, eq(true),
		   "testGetTermEvaluatorHp evaluator correct for " + term);

		Class<?> uClass = PCHPTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in testGetTermEvaluatorHp");

		is(field0, strEq(term), "testGetTermEvaluatorHp stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator052() {
		EvaluatorFactoryTest.loadAll();

		String term = "MAXCASTABLE";

		Globals.getContext().getReferenceContext().constructCDOMObject(ClassSpellList.class, "Bard");
		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "CLASS:Bard");

		is(t instanceof PCMaxCastableClassTermEvaluator, eq(true),
		   "GetTermEvaluator052 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator052");

		is(field0, strEq(term), "GetTermEvaluator052 stored term is correct " + term);	       
		is(field1.getKeyName(), strEq("Bard"), "GetTermEvaluator052 field spellList is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator053() {
		EvaluatorFactoryTest.loadAll();

		String term = "MAXCASTABLE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "DOMAIN:Fire");

		is(t instanceof PCMaxCastableDomainTermEvaluator, eq(true),
		   "GetTermEvaluator053 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator053");

		is(field0, strEq(term), "GetTermEvaluator053 stored term is correct " + term);	       
		is(field1, strEq("Fire"), "GetTermEvaluator053 field domainKey is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator054() {
		EvaluatorFactoryTest.loadAll();

		String term = "MAXCASTABLE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "SPELLTYPE:Arcane");

		is(t instanceof PCMaxCastableSpellTypeTermEvaluator, eq(true),
		   "GetTermEvaluator054 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator054");

		is(field0, strEq(term), "GetTermEvaluator054 stored term is correct " + term);	       
		is(field1, strEq("Arcane"), "GetTermEvaluator054 field typeKey is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator055() {
		EvaluatorFactoryTest.loadAll();

		String term = "MAXCASTABLE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "ANY");

		is(t instanceof PCMaxCastableAnyTermEvaluator, eq(true),
		   "GetTermEvaluator055 evaluator correct for " + term);

		Class<?> uClass = PCMaxCastableAnyTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator055");

		is(field0, strEq(term), "GetTermEvaluator055 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator056() {
		EvaluatorFactoryTest.loadAll();

		String term = "MAXCASTABLE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator056 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator057() {
		EvaluatorFactoryTest.loadAll();

		String term = "MOVEBASE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCMoveBaseTermEvaluator, eq(true),
		   "GetTermEvaluator057 evaluator correct for " + term);

		Class<?> uClass = PCMoveBaseTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator057");

		is(field0, strEq(term), "GetTermEvaluator057 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator058() {
		EvaluatorFactoryTest.loadAll();

		String term = "PC.HEIGHT";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCHeightTermEvaluator, eq(true),
		   "GetTermEvaluator058 evaluator correct for " + term);

		Class<?> uClass = PCHeightTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator058");

		is(field0, strEq(term), "GetTermEvaluator058 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator059() {
		EvaluatorFactoryTest.loadAll();

		String term = "PC.WEIGHT";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCWeightTermEvaluator, eq(true),
		   "GetTermEvaluator059 evaluator correct for " + term);

		Class<?> uClass = PCWeightTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator059");

		is(field0, strEq(term), "GetTermEvaluator059 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator060() {
		EvaluatorFactoryTest.loadAll();

		String term = "PROFACCHECK";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "EQ:Dagger");

		is(t instanceof PCProfACCheckTermEvaluator, eq(true),
		   "GetTermEvaluator060 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator060");

		is(field0, strEq(term), "GetTermEvaluator060 stored term is correct " + term);	       
		is(field1, strEq("Dagger"), "GetTermEvaluator060 field eqKey is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator061() {
		EvaluatorFactoryTest.loadAll();

		String term = "PROFACCHECK";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCProfACCheckTermEvaluator, eq(true),
		   "GetTermEvaluator061 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator061");

		is(field0, strEq(term), "GetTermEvaluator061 stored term is correct " + term);	       
		is(field1, strEq(""), "GetTermEvaluator061 field eqKey is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator062() {
		EvaluatorFactoryTest.loadAll();

		String term = "RACESIZE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCRaceSizeTermEvaluator, eq(true),
		   "GetTermEvaluator062 evaluator correct for " + term);

		Class<?> uClass = PCRaceSizeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator062");

		is(field0, strEq(term), "GetTermEvaluator062 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator063() {
		EvaluatorFactoryTest.loadAll();

		String term = "SCORE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "STAT:INT");

		is(t instanceof PCScoreTermEvaluator, eq(true),
		   "GetTermEvaluator063 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator063");

		is(field0, strEq(term), "GetTermEvaluator063 stored term is correct " + term);	       
		is(field1, strEq("INT"), "GetTermEvaluator063 field stat is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator064() {
		EvaluatorFactoryTest.loadAll();

		String term = "SCORE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCScoreTermEvaluator, eq(true),
		   "GetTermEvaluator064 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator064");

		is(field0, strEq(term), "GetTermEvaluator064 stored term is correct " + term);	       
		is(field1, strEq(""), "GetTermEvaluator064 field stat is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator065() {
		EvaluatorFactoryTest.loadAll();

		String term = "SHIELDACCHECK";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCShieldACcheckTermEvaluator, eq(true),
		   "GetTermEvaluator065 evaluator correct for " + term);

		Class<?> uClass = PCShieldACcheckTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator065");

		is(field0, strEq(term), "GetTermEvaluator065 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator066() {
		EvaluatorFactoryTest.loadAll();

		String term = "SHIELDACHECK";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCShieldACcheckTermEvaluator, eq(true),
		   "GetTermEvaluator066 evaluator correct for " + term);

		Class<?> uClass = PCShieldACcheckTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator066");

		is(field0, strEq(term), "GetTermEvaluator066 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator067() {
		EvaluatorFactoryTest.loadAll();

		String term = "SIZE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCSizeTermEvaluator, eq(true),
		   "GetTermEvaluator067 evaluator correct for " + term);

		Class<?> uClass = PCSizeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator067");

		is(field0, strEq(term), "GetTermEvaluator067 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator068() {
		EvaluatorFactoryTest.loadAll();

		String term = "SIZEMOD";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCSizeModEvaluatorTermEvaluator, eq(true),
		   "GetTermEvaluator068 evaluator correct for " + term);

		Class<?> uClass = PCSizeModEvaluatorTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator068");

		is(field0, strEq(term), "GetTermEvaluator068 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator069() {
		EvaluatorFactoryTest.loadAll();

		String term = "SPELLBASESTAT";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "CLASS:Cleric");

		is(t instanceof PCSPellBaseStatTermEvaluator, eq(true),
		   "GetTermEvaluator069 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator069");

		is(field0, strEq(term), "GetTermEvaluator069 stored term is correct " + term);	       
		is(field1, strEq("Cleric"), "GetTermEvaluator069 field classKey is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator070() {
		EvaluatorFactoryTest.loadAll();

		String term = "SPELLBASESTATSCORE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "CLASS:Cleric");

		is(t instanceof PCSPellBaseStatScoreEvaluatorTermEvaluator, eq(true),
		   "GetTermEvaluator070 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator070");

		is(field0, strEq(term), "GetTermEvaluator070 stored term is correct " + term);	       
		is(field1, strEq("Cleric"), "GetTermEvaluator070 field classKey is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator071() {
		EvaluatorFactoryTest.loadAll();

		String term = "SPELLBASESTAT";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "RACE:Gnu");

		is(t instanceof PCSPellBaseStatTermEvaluator, eq(true),
		   "GetTermEvaluator071 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator071");

		is(field0, strEq(term), "GetTermEvaluator071 stored term is correct " + term);	       
		is(field1, strEq(""), "GetTermEvaluator071 field classKey is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator072() {
		EvaluatorFactoryTest.loadAll();

		String term = "SPELLBASESTATSCORE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "RACE:Gnu");

		is(t instanceof PCSPellBaseStatScoreEvaluatorTermEvaluator, eq(true),
		   "GetTermEvaluator072 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator072");

		is(field0, strEq(term), "GetTermEvaluator072 stored term is correct " + term);	       
		is(field1, strEq(""), "GetTermEvaluator072 field classKey is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator073() {
		EvaluatorFactoryTest.loadAll();

		String term = "SPELLLEVEL";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCSpellLevelTermEvaluator, eq(true),
		   "GetTermEvaluator073 evaluator correct for " + term);

		Class<?> uClass = PCSpellLevelTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator073");

		is(field0, strEq(term), "GetTermEvaluator073 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator074() {
		EvaluatorFactoryTest.loadAll();

		String term = "TL";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCTLTermEvaluator, eq(true),
		   "GetTermEvaluator074 evaluator correct for " + term);

		Class<?> uClass = PCTLTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator074");

		is(field0, strEq(term), "GetTermEvaluator074 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator075() {
		EvaluatorFactoryTest.loadAll();

		String term = "BL";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "CLASS:Ranger");

		is(t instanceof PCBLTermEvaluator, eq(true),
		   "GetTermEvaluator075 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator075");

		is(field0, strEq(term), "GetTermEvaluator075 stored term is correct " + term);	       
		is(field1, strEq("Ranger"), "GetTermEvaluator075 field source is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator076() {
		EvaluatorFactoryTest.loadAll();

		String term = "BL";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "RACE:Gnome");

		is(t instanceof PCBLTermEvaluator, eq(true),
		   "GetTermEvaluator076 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator076");

		is(field0, strEq(term), "GetTermEvaluator076 stored term is correct " + term);	       
		is(field1, strEq(""), "GetTermEvaluator076 field source is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator077() {
		EvaluatorFactoryTest.loadAll();

		String term = "BL.Wizard";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "DOMAIN:Ice");

		is(t instanceof PCBLTermEvaluator, eq(true),
		   "GetTermEvaluator077 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator077");

		is(field0, strEq(term), "GetTermEvaluator077 stored term is correct " + term);	       
		is(field1, strEq("Wizard"), "GetTermEvaluator077 field source is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator078() {
		EvaluatorFactoryTest.loadAll();

		String term = "BL=Cleric";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "DOMAIN:Law");

		is(t instanceof PCBLTermEvaluator, eq(true),
		   "GetTermEvaluator078 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator078");

		is(field0, strEq(term), "GetTermEvaluator078 stored term is correct " + term);	       
		is(field1, strEq("Cleric"), "GetTermEvaluator078 field source is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator079() {
		EvaluatorFactoryTest.loadAll();

		String term = "CL;BEFORELEVEL.10";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "CLASS:Ranger");

		is(t instanceof PCCLBeforeLevelTermEvaluator, eq(true),
		   "GetTermEvaluator079 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator079");

		is(field0, strEq(term), "GetTermEvaluator079 stored term is correct " + term);	       
		is(field1, strEq("Ranger"), "GetTermEvaluator079 field source is correct ");	       
		is(field2, eq(10), "GetTermEvaluator079 field level is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator080() {
		EvaluatorFactoryTest.loadAll();

		String term = "CL;BEFORELEVEL=15";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "CLASS:Druid");

		is(t instanceof PCCLBeforeLevelTermEvaluator, eq(true),
		   "GetTermEvaluator080 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator080");

		is(field0, strEq(term), "GetTermEvaluator080 stored term is correct " + term);	       
		is(field1, strEq("Druid"), "GetTermEvaluator080 field source is correct ");	       
		is(field2, eq(15), "GetTermEvaluator080 field level is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator081() {
		EvaluatorFactoryTest.loadAll();

		String term = "CL;BEFORELEVEL=Fighter";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator081 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator082() {
		EvaluatorFactoryTest.loadAll();

		String term = "CLASSLEVEL.Bard (Bardiliscious)";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCLTermEvaluator, eq(true),
		   "GetTermEvaluator082 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator082");

		is(field0, strEq(term), "GetTermEvaluator082 stored term is correct " + term);	       
		is(field1, strEq("Bard (Bardiliscious)"), "GetTermEvaluator082 field classKey is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator083() {
		EvaluatorFactoryTest.loadAll();

		String term = "CLASSLEVEL=Rogue {Sneaky}";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCLTermEvaluator, eq(true),
		   "GetTermEvaluator083 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator083");

		is(field0, strEq(term), "GetTermEvaluator083 stored term is correct " + term);	       
		is(field1, strEq("Rogue (Sneaky)"), "GetTermEvaluator083 field classKey is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator084() {
		EvaluatorFactoryTest.loadAll();

		String term = "CLASS.Druid";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "RACE:Human");

		is(t instanceof PCHasClassTermEvaluator, eq(true),
		   "GetTermEvaluator084 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator084");

		is(field0, strEq(term), "GetTermEvaluator084 stored term is correct " + term);	       
		is(field1, strEq("Druid"), "GetTermEvaluator084 field source is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator085() {
		EvaluatorFactoryTest.loadAll();

		String term = "CLASS=Paladin";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "RACE:Dwarf");

		is(t instanceof PCHasClassTermEvaluator, eq(true),
		   "GetTermEvaluator085 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator085");

		is(field0, strEq(term), "GetTermEvaluator085 stored term is correct " + term);	       
		is(field1, strEq("Paladin"), "GetTermEvaluator085 field source is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator086() {
		EvaluatorFactoryTest.loadAll();

		String term = "CL";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "CLASS:Ranger");

		is(t instanceof PCCLTermEvaluator, eq(true),
		   "GetTermEvaluator086 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator086");

		is(field0, strEq(term), "GetTermEvaluator086 stored term is correct " + term);	       
		is(field1, strEq("Ranger"), "GetTermEvaluator086 field classKey is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator087() {
		EvaluatorFactoryTest.loadAll();

		String term = "CL";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator087 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator088() {
		EvaluatorFactoryTest.loadAll();

		String term = "CL.Bard";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "RACE:Elf");

		is(t instanceof PCCLTermEvaluator, eq(true),
		   "GetTermEvaluator088 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator088");

		is(field0, strEq(term), "GetTermEvaluator088 stored term is correct " + term);	       
		is(field1, strEq("Bard"), "GetTermEvaluator088 field classKey is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator089() {
		EvaluatorFactoryTest.loadAll();

		String term = "CL=Rogue";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "RACE:Elf");

		is(t instanceof PCCLTermEvaluator, eq(true),
		   "GetTermEvaluator089 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator089");

		is(field0, strEq(term), "GetTermEvaluator089 stored term is correct " + term);	       
		is(field1, strEq("Rogue"), "GetTermEvaluator089 field classKey is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator090() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.MERGENONE]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountEqTypeTermEvaluator, eq(true),
		   "GetTermEvaluator090 evaluator correct for " + term);

		Class<?> uClass = PCCountEqTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[] {};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator090");

		is(field0, strEq(term), "GetTermEvaluator090 stored term is correct " + term);	       
		is(field1[0], strEq(""), "GetTermEvaluator090 field types[0] is correct ");	       
		is(field2, eq(Constants.MERGE_NONE), "GetTermEvaluator090 field merge is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator091() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.MERGELOC]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountEqTypeTermEvaluator, eq(true),
		   "GetTermEvaluator091 evaluator correct for " + term);

		Class<?> uClass = PCCountEqTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[] {};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator091");

		is(field0, strEq(term), "GetTermEvaluator091 stored term is correct " + term);	       
		is(field1[0], strEq(""), "GetTermEvaluator091 field types[0] is correct ");	       
		is(field2, eq(Constants.MERGE_LOCATION), "GetTermEvaluator091 field merge is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator092() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountEqTypeTermEvaluator, eq(true),
		   "GetTermEvaluator092 evaluator correct for " + term);

		Class<?> uClass = PCCountEqTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[] {};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator092");

		is(field0, strEq(term), "GetTermEvaluator092 stored term is correct " + term);	       
		is(field1[0], strEq(""), "GetTermEvaluator092 field types[0] is correct ");	       
		is(field2, eq(Constants.MERGE_ALL), "GetTermEvaluator092 field merge is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator093() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.EQUIPPED]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountEqTypeTermEvaluator, eq(true),
		   "GetTermEvaluator093 evaluator correct for " + term);

		Class<?> uClass = PCCountEqTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[] {};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator093");

		is(field0, strEq(term), "GetTermEvaluator093 stored term is correct " + term);	       
		is(field1[0], strEq("EQUIPPED"), "GetTermEvaluator093 field types[0] is correct ");	       
		is(field2, eq(Constants.MERGE_ALL), "GetTermEvaluator093 field merge is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator094() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.NOTEQUIPPED]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountEqTypeTermEvaluator, eq(true),
		   "GetTermEvaluator094 evaluator correct for " + term);

		Class<?> uClass = PCCountEqTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[] {};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator094");

		is(field0, strEq(term), "GetTermEvaluator094 stored term is correct " + term);	       
		is(field1[0], strEq("NOTEQUIPPED"), "GetTermEvaluator094 field types[0] is correct ");	       
		is(field2, eq(Constants.MERGE_ALL), "GetTermEvaluator094 field merge is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator095() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.CONTAINER]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountEqTypeTermEvaluator, eq(true),
		   "GetTermEvaluator095 evaluator correct for " + term);

		Class<?> uClass = PCCountEqTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[] {};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator095");

		is(field0, strEq(term), "GetTermEvaluator095 stored term is correct " + term);	       
		is(field1[0], strEq("CONTAINER"), "GetTermEvaluator095 field types[0] is correct ");	       
		is(field2, eq(Constants.MERGE_ALL), "GetTermEvaluator095 field merge is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator096() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.WEAPON]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountEqTypeTermEvaluator, eq(true),
		   "GetTermEvaluator096 evaluator correct for " + term);

		Class<?> uClass = PCCountEqTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[] {};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator096");

		is(field0, strEq(term), "GetTermEvaluator096 stored term is correct " + term);	       
		is(field1[0], strEq("WEAPON"), "GetTermEvaluator096 field types[0] is correct ");	       
		is(field2, eq(Constants.MERGE_ALL), "GetTermEvaluator096 field merge is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator097() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.ACITEM]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountEqTypeTermEvaluator, eq(true),
		   "GetTermEvaluator097 evaluator correct for " + term);

		Class<?> uClass = PCCountEqTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[] {};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator097");

		is(field0, strEq(term), "GetTermEvaluator097 stored term is correct " + term);	       
		is(field1[0], strEq("ACITEM"), "GetTermEvaluator097 field types[0] is correct ");	       
		is(field2, eq(Constants.MERGE_ALL), "GetTermEvaluator097 field merge is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator098() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.MERGENONE.ARMOR.IS.FOO]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountEqTypeTermEvaluator, eq(true),
		   "GetTermEvaluator098 evaluator correct for " + term);

		Class<?> uClass = PCCountEqTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[] {};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator098");

		is(field0, strEq(term), "GetTermEvaluator098 stored term is correct " + term);	       
		is(field1[0], strEq("ARMOR"), "GetTermEvaluator098 field types[0] is correct ");	       
		is(field1[1], strEq("IS"), "GetTermEvaluator098 field types[1] is correct ");	       
		is(field1[2], strEq("FOO"), "GetTermEvaluator098 field types[2] is correct ");	       
		is(field2, eq(Constants.MERGE_NONE), "GetTermEvaluator098 field merge is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator099() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.MERGELOC.QUX.NOT.BAR]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountEqTypeTermEvaluator, eq(true),
		   "GetTermEvaluator099 evaluator correct for " + term);

		Class<?> uClass = PCCountEqTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[] {};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator099");

		is(field0, strEq(term), "GetTermEvaluator099 stored term is correct " + term);	       
		is(field1[0], strEq("QUX"), "GetTermEvaluator099 field types[0] is correct ");	       
		is(field1[1], strEq("NOT"), "GetTermEvaluator099 field types[1] is correct ");	       
		is(field1[2], strEq("BAR"), "GetTermEvaluator099 field types[2] is correct ");	       
		is(field2, eq(Constants.MERGE_LOCATION), "GetTermEvaluator099 field merge is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator100() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.QUUX.ADD.BAZ]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountEqTypeTermEvaluator, eq(true),
		   "GetTermEvaluator100 evaluator correct for " + term);

		Class<?> uClass = PCCountEqTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[] {};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator100");

		is(field0, strEq(term), "GetTermEvaluator100 stored term is correct " + term);	       
		is(field1[0], strEq("QUUX"), "GetTermEvaluator100 field types[0] is correct ");	       
		is(field1[1], strEq("ADD"), "GetTermEvaluator100 field types[1] is correct ");	       
		is(field1[2], strEq("BAZ"), "GetTermEvaluator100 field types[2] is correct ");	       
		is(field2, eq(Constants.MERGE_ALL), "GetTermEvaluator100 field merge is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator101() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.MERGENONE.WEAPON.IS.FOO]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountEqTypeTermEvaluator, eq(true),
		   "GetTermEvaluator101 evaluator correct for " + term);

		Class<?> uClass = PCCountEqTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[] {};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator101");

		is(field0, strEq(term), "GetTermEvaluator101 stored term is correct " + term);	       
		is(field1[0], strEq("WEAPON"), "GetTermEvaluator101 field types[0] is correct ");	       
		is(field1[1], strEq("IS"), "GetTermEvaluator101 field types[1] is correct ");	       
		is(field1[2], strEq("FOO"), "GetTermEvaluator101 field types[2] is correct ");	       
		is(field2, eq(Constants.MERGE_NONE), "GetTermEvaluator101 field merge is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator102() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.WEAPON.IS.FOO.EQUIPPED.ADD.BAR]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountEqTypeTermEvaluator, eq(true),
		   "GetTermEvaluator102 evaluator correct for " + term);

		Class<?> uClass = PCCountEqTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[] {};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator102");

		is(field0, strEq(term), "GetTermEvaluator102 stored term is correct " + term);	       
		is(field1[0], strEq("WEAPON"), "GetTermEvaluator102 field types[0] is correct ");	       
		is(field1[1], strEq("IS"), "GetTermEvaluator102 field types[1] is correct ");	       
		is(field1[2], strEq("FOO"), "GetTermEvaluator102 field types[2] is correct ");	       
		is(field1[3], strEq("EQUIPPED"), "GetTermEvaluator102 field types[3] is correct ");	       
		is(field1[4], strEq("ADD"), "GetTermEvaluator102 field types[4] is correct ");	       
		is(field1[5], strEq("BAR"), "GetTermEvaluator102 field types[5] is correct ");	       
		is(field2, eq(Constants.MERGE_ALL), "GetTermEvaluator102 field merge is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator103() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.FOO]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountEqTypeTermEvaluator, eq(true),
		   "GetTermEvaluator103 evaluator correct for " + term);

		Class<?> uClass = PCCountEqTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[] {};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator103");

		is(field0, strEq(term), "GetTermEvaluator103 stored term is correct " + term);	       
		is(field1[0], strEq("FOO"), "GetTermEvaluator103 field types[0] is correct ");	       
		is(field2, eq(Constants.MERGE_ALL), "GetTermEvaluator103 field merge is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator104() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.MERGENONE.IS.FOO.QUX]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator104 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator105() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.MERGELOC.NOT.BAR.QUX]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator105 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator106() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQTYPE.ADD.BAZ.QUX]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator106 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator107() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQUIPMENT.MERGENONE]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountEquipmentTermEvaluator, eq(true),
		   "GetTermEvaluator107 evaluator correct for " + term);

		Class<?> uClass = PCCountEquipmentTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[] {};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator107");

		is(field0, strEq(term), "GetTermEvaluator107 stored term is correct " + term);	       
		is(field1[0], strEq(""), "GetTermEvaluator107 field types[0] is correct ");	       
		is(field2, eq(Constants.MERGE_NONE), "GetTermEvaluator107 field merge is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator108() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQUIPMENT.MERGELOC]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountEquipmentTermEvaluator, eq(true),
		   "GetTermEvaluator108 evaluator correct for " + term);

		Class<?> uClass = PCCountEquipmentTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[] {};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator108");

		is(field0, strEq(term), "GetTermEvaluator108 stored term is correct " + term);	       
		is(field1[0], strEq(""), "GetTermEvaluator108 field types[0] is correct ");	       
		is(field2, eq(Constants.MERGE_LOCATION), "GetTermEvaluator108 field merge is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator109() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQUIPMENT]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountEquipmentTermEvaluator, eq(true),
		   "GetTermEvaluator109 evaluator correct for " + term);

		Class<?> uClass = PCCountEquipmentTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[] {};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator109");

		is(field0, strEq(term), "GetTermEvaluator109 stored term is correct " + term);	       
		is(field1[0], strEq(""), "GetTermEvaluator109 field types[0] is correct ");	       
		is(field2, eq(Constants.MERGE_ALL), "GetTermEvaluator109 field merge is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator110() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQUIPMENT.EQUIPPED]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator110 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator111() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQUIPMENT.NOTEQUIPPED]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator111 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator112() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQUIPMENT.CONTAINER]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator112 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator113() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQUIPMENT.WEAPON]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator113 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator114() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQUIPMENT.ACITEM]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator114 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator115() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQUIPMENT.MERGENONE.IS.FOO]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountEquipmentTermEvaluator, eq(true),
		   "GetTermEvaluator115 evaluator correct for " + term);

		Class<?> uClass = PCCountEquipmentTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[] {};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator115");

		is(field0, strEq(term), "GetTermEvaluator115 stored term is correct " + term);	       
		is(field1[0], strEq("IS"), "GetTermEvaluator115 field types[0] is correct ");	       
		is(field1[1], strEq("FOO"), "GetTermEvaluator115 field types[1] is correct ");	       
		is(field2, eq(Constants.MERGE_NONE), "GetTermEvaluator115 field merge is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator116() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQUIPMENT.MERGELOC.NOT.BAR]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountEquipmentTermEvaluator, eq(true),
		   "GetTermEvaluator116 evaluator correct for " + term);

		Class<?> uClass = PCCountEquipmentTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[] {};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator116");

		is(field0, strEq(term), "GetTermEvaluator116 stored term is correct " + term);	       
		is(field1[0], strEq("NOT"), "GetTermEvaluator116 field types[0] is correct ");	       
		is(field1[1], strEq("BAR"), "GetTermEvaluator116 field types[1] is correct ");	       
		is(field2, eq(Constants.MERGE_LOCATION), "GetTermEvaluator116 field merge is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator117() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQUIPMENT.ADD.BAZ]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountEquipmentTermEvaluator, eq(true),
		   "GetTermEvaluator117 evaluator correct for " + term);

		Class<?> uClass = PCCountEquipmentTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "merge");

		String field0 = "";
		String[] field1 = new String[] {};
		Integer field2 = 0;
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String[]) pF1.get(t);
			field2 = (Integer) pF2.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator117");

		is(field0, strEq(term), "GetTermEvaluator117 stored term is correct " + term);	       
		is(field1[0], strEq("ADD"), "GetTermEvaluator117 field types[0] is correct ");	       
		is(field1[1], strEq("BAZ"), "GetTermEvaluator117 field types[1] is correct ");	       
		is(field2, eq(Constants.MERGE_ALL), "GetTermEvaluator117 field merge is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator118() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQUIPMENT.IS.FOO.EQUIPPED.ADD.BAR]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator118 evaluator is null");
	}


	/**
	 * Test 119 is a duplicate of test 109
	 * Method: getTermEvaluator(String term, String source)
	 */
//	public void testGetTermEvaluator119() {
//		TestHelper.loadAll();
//
//		String term = "COUNT[EQUIPMENT]";
//
//		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");
//
//		is(t instanceof PCCountEquipmentTermEvaluator, eq(true),
//		   "GetTermEvaluator119 evaluator correct for " + term);
//
//		Class<?> uClass = PCCountEquipmentTermEvaluator.class;
//
//		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
//		Field pF1 = (Field) TestHelper.findField(uClass, "types");
//		Field pF2 = (Field) TestHelper.findField(uClass, "merge");
//
//		String field0 = "";
//		String[] field1 = new String[] {};
//		Integer field2 = 0;
//		boolean ok;
//		try
//		{
//			ok = true;
//			field0 = (String) pF0.get(t);
//			field1 = (String[]) pF1.get(t);
//			field2 = (Integer) pF2.get(t);
//		}
//		catch (ClassCastException e)
//		{
//			ok = false;
//		}
//		catch (IllegalAccessException e)
//		{
//			ok = false;
//		}
//
//		is(ok, eq(true), "No illegal access in getTermEvaluator119");
//
//		is(field0, strEq(term), "GetTermEvaluator119 stored term is correct " + term);	       
//		is(field1[0], strEq(""), "GetTermEvaluator119 field types[0] is correct ");	       
//		is(field2, eq(Constants.MERGE_ALL), "GetTermEvaluator119 field merge is correct ");	       
//	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator120() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQUIPMENT.MERGENONE.BAR.IS.FOO.QUX]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator120 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator121() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQUIPMENT.MERGELOC.BAR.NOT.BAZ.QUX]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator121 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator122() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[EQUIPMENT.BAR.ADD.BAZ.QUX]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator122 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator123() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATAUTOTYPE.HIDDEN]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountAbilitiesTypeNatureAutoTermEvaluator, eq(true),
		   "GetTermEvaluator123 evaluator correct for " + term);

		Class<?> uClass = PCCountAbilitiesTypeNatureAutoTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF3 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		String[] field1 = new String[] {};
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator123");

		is(field0, strEq(term), "GetTermEvaluator123 stored term is correct " + term);	       
		is(field1[0], strEq(""), "GetTermEvaluator123 field types[0] is correct ");	       
		is(field2, eq(true), "GetTermEvaluator123 field hidden is correct ");	       
		is(field3, eq(false), "GetTermEvaluator123 field visible is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator124() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATAUTOTYPE=VISIBLE]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountAbilitiesTypeNatureAutoTermEvaluator, eq(true),
		   "GetTermEvaluator124 evaluator correct for " + term);

		Class<?> uClass = PCCountAbilitiesTypeNatureAutoTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF3 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		String[] field1 = new String[] {};
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator124");

		is(field0, strEq(term), "GetTermEvaluator124 stored term is correct " + term);	       
		is(field1[0], strEq(""), "GetTermEvaluator124 field types[0] is correct ");	       
		is(field2, eq(false), "GetTermEvaluator124 field hidden is correct ");	       
		is(field3, eq(true), "GetTermEvaluator124 field visible is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator125() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATNAME.Jack of all trades]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountAbilityNameTermEvaluator, eq(true),
		   "GetTermEvaluator125 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator125");

		is(field0, strEq(term), "GetTermEvaluator125 stored term is correct " + term);	       
		is(field1, strEq("Jack of all trades"), "GetTermEvaluator125 field key is correct ");	       
		is(field2, eq(false), "GetTermEvaluator125 field hidden is correct ");	       
		is(field3, eq(true), "GetTermEvaluator125 field visible is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator126() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATNAME=Weapon Focus (Dagger)]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountAbilityNameTermEvaluator, eq(true),
		   "GetTermEvaluator126 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator126");

		is(field0, strEq(term), "GetTermEvaluator126 stored term is correct " + term);	       
		is(field1, strEq("Weapon Focus (Dagger)"), "GetTermEvaluator126 field key is correct ");	       
		is(field2, eq(false), "GetTermEvaluator126 field hidden is correct ");	       
		is(field3, eq(true), "GetTermEvaluator126 field visible is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator127() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATTYPE.BAR]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountAbilitiesTypeNatureAllTermEvaluator, eq(true),
		   "GetTermEvaluator127 evaluator correct for " + term);

		Class<?> uClass = PCCountAbilitiesTypeNatureAllTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF3 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		String[] field1 = new String[] {};
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator127");

		is(field0, strEq(term), "GetTermEvaluator127 stored term is correct " + term);	       
		is(field1[0], strEq("BAR"), "GetTermEvaluator127 field types[0] is correct ");	       
		is(field2, eq(false), "GetTermEvaluator127 field hidden is correct ");	       
		is(field3, eq(true), "GetTermEvaluator127 field visible is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator128() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATTYPE.BAZ]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountAbilitiesTypeNatureAllTermEvaluator, eq(true),
		   "GetTermEvaluator128 evaluator correct for " + term);

		Class<?> uClass = PCCountAbilitiesTypeNatureAllTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF3 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		String[] field1 = new String[] {};
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator128");

		is(field0, strEq(term), "GetTermEvaluator128 stored term is correct " + term);	       
		is(field1[0], strEq("BAZ"), "GetTermEvaluator128 field types[0] is correct ");	       
		is(field2, eq(false), "GetTermEvaluator128 field hidden is correct ");	       
		is(field3, eq(true), "GetTermEvaluator128 field visible is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator129() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[VFEATTYPE.HIDDEN]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountAbilitiesTypeNatureVirtualTermEvaluator, eq(true),
		   "GetTermEvaluator129 evaluator correct for " + term);

		Class<?> uClass = PCCountAbilitiesTypeNatureVirtualTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF3 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		String[] field1 = new String[] {};
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator129");

		is(field0, strEq(term), "GetTermEvaluator129 stored term is correct " + term);	       
		is(field1[0], strEq(""), "GetTermEvaluator129 field types[0] is correct ");	       
		is(field2, eq(true), "GetTermEvaluator129 field hidden is correct ");	       
		is(field3, eq(false), "GetTermEvaluator129 field visible is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator130() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[VFEATTYPE=ALL]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountAbilitiesTypeNatureVirtualTermEvaluator, eq(true),
		   "GetTermEvaluator130 evaluator correct for " + term);

		Class<?> uClass = PCCountAbilitiesTypeNatureVirtualTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF3 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		String[] field1 = new String[] {};
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator130");

		is(field0, strEq(term), "GetTermEvaluator130 stored term is correct " + term);	       
		is(field1[0], strEq(""), "GetTermEvaluator130 field types[0] is correct ");	       
		is(field2, eq(true), "GetTermEvaluator130 field hidden is correct ");	       
		is(field3, eq(true), "GetTermEvaluator130 field visible is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator131() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FEATTYPE.FOO.BAR.BAZ.QUX]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountAbilitiesTypeNatureAllTermEvaluator, eq(true),
		   "GetTermEvaluator131 evaluator correct for " + term);

		Class<?> uClass = PCCountAbilitiesTypeNatureAllTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "types");
		Field pF2 = (Field) TestHelper.findField(uClass, "hidden");
		Field pF3 = (Field) TestHelper.findField(uClass, "visible");

		String field0 = "";
		String[] field1 = new String[] {};
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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator131");

		is(field0, strEq(term), "GetTermEvaluator131 stored term is correct " + term);	       
		is(field1[0], strEq("FOO"), "GetTermEvaluator131 field types[0] is correct ");	       
		is(field1[1], strEq("BAR"), "GetTermEvaluator131 field types[1] is correct ");	       
		is(field1[2], strEq("BAZ"), "GetTermEvaluator131 field types[2] is correct ");	       
		is(field1[3], strEq("QUX"), "GetTermEvaluator131 field types[3] is correct ");	       
		is(field2, eq(false), "GetTermEvaluator131 field hidden is correct ");	       
		is(field3, eq(true), "GetTermEvaluator131 field visible is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator132() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FOLLOWERTYPE.MOO]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountFollowerTypeTermEvaluator, eq(true),
		   "GetTermEvaluator132 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator132");

		is(field0, strEq(term), "GetTermEvaluator132 stored term is correct " + term);	       
		is(field1, strEq("MOO"), "GetTermEvaluator132 field type is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator133() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FOLLOWERTYPE.MOO.0.EQTYPE]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountFollowerTypeTransitiveTermEvaluator, eq(true),
		   "GetTermEvaluator133 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator133");

		is(field0, strEq(term), "GetTermEvaluator133 stored term is correct " + term);	       
		is(field1, eq(0), "GetTermEvaluator133 field index is correct ");	       
		is(field2, strEq("COUNT[EQTYPE]"), "GetTermEvaluator133 field newCount is correct ");	       
		is(field3, strEq("MOO"), "GetTermEvaluator133 field type is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator134() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FOLLOWERTYPE.MOO.0]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator134 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator135() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[FOLLOWERTYPE.MOO.FOO.0]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator135 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator136() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SKILLTYPE.KNOWLEDGE]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCSkillTypeTermEvaluator, eq(true),
		   "GetTermEvaluator136 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator136");

		is(field0, strEq(term), "GetTermEvaluator136 stored term is correct " + term);	       
		is(field1, strEq("KNOWLEDGE"), "GetTermEvaluator136 field type is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator137() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SKILLTYPE=PERFORM]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCSkillTypeTermEvaluator, eq(true),
		   "GetTermEvaluator137 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator137");

		is(field0, strEq(term), "GetTermEvaluator137 stored term is correct " + term);	       
		is(field1, strEq("PERFORM"), "GetTermEvaluator137 field type is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator138() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLBOOKS]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountSpellbookTermEvaluator, eq(true),
		   "GetTermEvaluator138 evaluator correct for " + term);

		Class<?> uClass = PCCountSpellbookTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator138");

		is(field0, strEq(term), "GetTermEvaluator138 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator139() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLBOOKS.broken";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator139 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator140() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLSINBOOK.1.0]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountSpellsInbookTermEvaluator, eq(true),
		   "GetTermEvaluator140 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator140");

		is(field0, strEq(term), "GetTermEvaluator140 stored term is correct " + term);	       
		is(field1, strEq("1.0"), "GetTermEvaluator140 field book is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator141() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLSINBOOK.1.0";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator141 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator142() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLSKNOWN.0.0]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountSpellsKnownTermEvaluator, eq(true),
		   "GetTermEvaluator142 evaluator correct for " + term);

		Class<?> uClass = PCCountSpellsKnownTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "nums");

		String field0 = "";
		int[] field1 = new int[] {};
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (int[]) pF1.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator142");

		is(field0, strEq(term), "GetTermEvaluator142 stored term is correct " + term);	       
		is(field1[0], eq(0), "GetTermEvaluator142 field nums[0] is correct ");	       
		is(field1[1], eq(0), "GetTermEvaluator142 field nums[1] is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator143() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLSKNOWN.0]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator143 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator144() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLSKNOWN.FOO]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator144 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator145() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLSKNOWN.FOO";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator145 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator146() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLTIMES.1.2.3.4]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountSpellTimesTermEvaluator, eq(true),
		   "GetTermEvaluator146 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator146");

		is(field0, strEq(term), "GetTermEvaluator146 stored term is correct " + term);	       
		is(field1, eq(1), "GetTermEvaluator146 field classNum is correct ");	       
		is(field2, eq(2), "GetTermEvaluator146 field bookNum is correct ");	       
		is(field3, eq(3), "GetTermEvaluator146 field spellLevel is correct ");	       
		is(field4, eq(4), "GetTermEvaluator146 field spellNumber is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator147() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLSLEVELSINBOOK.1.2]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCountSpellsLevelsInBookTermEvaluator, eq(true),
		   "GetTermEvaluator147 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator147");

		is(field0, strEq(term), "GetTermEvaluator147 stored term is correct " + term);	       
		is(field1, eq(1), "GetTermEvaluator147 field classNum is correct ");	       
		is(field2, eq(2), "GetTermEvaluator147 field sbookNum is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator148() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLSLEVELSINBOOK.1.2";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator148 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator149() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLSLEVELSINBOOK.0]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator149 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator150() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLTIMES.1.2.3.4";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator150 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator151() {
		EvaluatorFactoryTest.loadAll();

		String term = "COUNT[SPELLTIMES.1.2.3]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator151 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator152() {
		EvaluatorFactoryTest.loadAll();

		String term = "EQTYPE.EQUIPPED.IS.FOO";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCEqTypeTermEvaluator, eq(true),
		   "GetTermEvaluator152 evaluator correct for " + term);

		Class<?> uClass = PCEqTypeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator152");

		is(field0, strEq(term), "GetTermEvaluator152 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator153() {
		EvaluatorFactoryTest.loadAll();

		String term = "HASDEITY:Bane";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCHasDeityTermEvaluator, eq(true),
		   "GetTermEvaluator153 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator153");

		is(field0, strEq(term), "GetTermEvaluator153 stored term is correct " + term);	       
		is(field1, strEq("Bane"), "GetTermEvaluator153 field deity is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator154() {
		EvaluatorFactoryTest.loadAll();

		String term = "HASFEAT:Endurance";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCHasFeatTermEvaluator, eq(true),
		   "GetTermEvaluator154 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator154");

		is(field0, strEq(term), "GetTermEvaluator154 stored term is correct " + term);	       
		is(field1, strEq("Endurance"), "GetTermEvaluator154 field feat is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator155() {
		EvaluatorFactoryTest.loadAll();

		String term = "MODEQUIPSPELLFAILURE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCModEquipTermEvaluator, eq(true),
		   "GetTermEvaluator155 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator155");

		is(field0, strEq(term), "GetTermEvaluator155 stored term is correct " + term);	       
		is(field1, strEq("SPELLFAILURE"), "GetTermEvaluator155 field modEq is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator156() {
		EvaluatorFactoryTest.loadAll();

		String term = "MOVE[Walk]";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCMovementTermEvaluator, eq(true),
		   "GetTermEvaluator156 evaluator correct for " + term);

		Class<?> uClass = PCMovementTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");
		Field pF1 = (Field) TestHelper.findField(uClass, "movement");

		String field0 = "";
		String field1 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
			field1 = (String) pF1.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator156");

		is(field0, strEq(term), "GetTermEvaluator156 stored term is correct " + term);	       
		is(field1, strEq("Walk"), "GetTermEvaluator156 field movement is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator157() {
		EvaluatorFactoryTest.loadAll();

		String term = "MOVE[Walk";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator157 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator158() {
		EvaluatorFactoryTest.loadAll();

		String term = "PC.SIZE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCSizeTermEvaluator, eq(true),
		   "GetTermEvaluator158 evaluator correct for " + term);

		Class<?> uClass = PCSizeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator158");

		is(field0, strEq(term), "GetTermEvaluator158 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator159() {
		EvaluatorFactoryTest.loadAll();

		String term = "SKILLRANK.Tumble";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCSkillRankTermEvaluator, eq(true),
		   "GetTermEvaluator159 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator159");

		is(field0, strEq(term), "GetTermEvaluator159 stored term is correct " + term);	       
		is(field1, strEq("Tumble"), "GetTermEvaluator159 field rank is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator160() {
		EvaluatorFactoryTest.loadAll();

		String term = "SKILLRANK=Perform (Dance)";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCSkillRankTermEvaluator, eq(true),
		   "GetTermEvaluator160 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator160");

		is(field0, strEq(term), "GetTermEvaluator160 stored term is correct " + term);	       
		is(field1, strEq("Perform (Dance)"), "GetTermEvaluator160 field rank is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator161() {
		EvaluatorFactoryTest.loadAll();

		String term = "SKILLRANK=Perform {Sing}";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCSkillRankTermEvaluator, eq(true),
		   "GetTermEvaluator161 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator161");

		is(field0, strEq(term), "GetTermEvaluator161 stored term is correct " + term);	       
		is(field1, strEq("Perform (Sing)"), "GetTermEvaluator161 field rank is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator162() {
		EvaluatorFactoryTest.loadAll();

		String term = "SKILLTOTAL.Tumble";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCSkillTotalTermEvaluator, eq(true),
		   "GetTermEvaluator162 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator162");

		is(field0, strEq(term), "GetTermEvaluator162 stored term is correct " + term);	       
		is(field1, strEq("Tumble"), "GetTermEvaluator162 field total is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator163() {
		EvaluatorFactoryTest.loadAll();

		String term = "SKILLTOTAL=Perform (Dance)";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCSkillTotalTermEvaluator, eq(true),
		   "GetTermEvaluator163 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator163");

		is(field0, strEq(term), "GetTermEvaluator163 stored term is correct " + term);	       
		is(field1, strEq("Perform (Dance)"), "GetTermEvaluator163 field total is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator164() {
		EvaluatorFactoryTest.loadAll();

		String term = "SKILLTOTAL=Perform {Sing}";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCSkillTotalTermEvaluator, eq(true),
		   "GetTermEvaluator164 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator164");

		is(field0, strEq(term), "GetTermEvaluator164 stored term is correct " + term);	       
		is(field1, strEq("Perform (Sing)"), "GetTermEvaluator164 field total is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator165() {
		EvaluatorFactoryTest.loadAll();

		String term = "VARDEFINED:MilkyBarsEaten";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCVarDefinedTermEvaluator, eq(true),
		   "GetTermEvaluator165 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator165");

		is(field0, strEq(term), "GetTermEvaluator165 stored term is correct " + term);	       
		is(field1, strEq("MilkyBarsEaten"), "GetTermEvaluator165 field var is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator166() {
		EvaluatorFactoryTest.loadAll();

		String term = "WEIGHT.CARRIED";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCarriedWeightTermEvaluator, eq(true),
		   "GetTermEvaluator166 evaluator correct for " + term);

		Class<?> uClass = PCCarriedWeightTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator166");

		is(field0, strEq(term), "GetTermEvaluator166 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator167() {
		EvaluatorFactoryTest.loadAll();

		String term = "WEIGHT.EQUIPPED";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCCarriedWeightTermEvaluator, eq(true),
		   "GetTermEvaluator167 evaluator correct for " + term);

		Class<?> uClass = PCCarriedWeightTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator167");

		is(field0, strEq(term), "GetTermEvaluator167 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator168() {
		EvaluatorFactoryTest.loadAll();

		String term = "WEIGHT.PC";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCWeightTermEvaluator, eq(true),
		   "GetTermEvaluator168 evaluator correct for " + term);

		Class<?> uClass = PCWeightTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator168");

		is(field0, strEq(term), "GetTermEvaluator168 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator169() {
		EvaluatorFactoryTest.loadAll();

		String term = "WEIGHT.TOTAL";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCTotalWeightTermEvaluator, eq(true),
		   "GetTermEvaluator169 evaluator correct for " + term);

		Class<?> uClass = PCTotalWeightTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator169");

		is(field0, strEq(term), "GetTermEvaluator169 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator170() {
		EvaluatorFactoryTest.loadAll();

		String term = "WEIGHT.NONSENSE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator170 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator171() {
		EvaluatorFactoryTest.loadAll();

		String term = "STR";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCStatModTermEvaluator, eq(true),
		   "GetTermEvaluator171 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator171");

		is(field0, strEq(term), "GetTermEvaluator171 stored term is correct " + term);	       
		is(field1, strEq("STR"), "GetTermEvaluator171 field statAbbrev is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator172() {
		EvaluatorFactoryTest.loadAll();

		String term = "INT";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCStatModTermEvaluator, eq(true),
		   "GetTermEvaluator172 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator172");

		is(field0, strEq(term), "GetTermEvaluator172 stored term is correct " + term);	       
		is(field1, strEq("INT"), "GetTermEvaluator172 field statAbbrev is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator173() {
		EvaluatorFactoryTest.loadAll();

		String term = "DEX";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCStatModTermEvaluator, eq(true),
		   "GetTermEvaluator173 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator173");

		is(field0, strEq(term), "GetTermEvaluator173 stored term is correct " + term);	       
		is(field1, strEq("DEX"), "GetTermEvaluator173 field statAbbrev is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator174() {
		EvaluatorFactoryTest.loadAll();

		String term = "WIS";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCStatModTermEvaluator, eq(true),
		   "GetTermEvaluator174 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator174");

		is(field0, strEq(term), "GetTermEvaluator174 stored term is correct " + term);	       
		is(field1, strEq("WIS"), "GetTermEvaluator174 field statAbbrev is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator175() {
		EvaluatorFactoryTest.loadAll();

		String term = "CON";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCStatModTermEvaluator, eq(true),
		   "GetTermEvaluator175 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator175");

		is(field0, strEq(term), "GetTermEvaluator175 stored term is correct " + term);	       
		is(field1, strEq("CON"), "GetTermEvaluator175 field statAbbrev is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator176() {
		EvaluatorFactoryTest.loadAll();

		String term = "CHA";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCStatModTermEvaluator, eq(true),
		   "GetTermEvaluator176 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator176");

		is(field0, strEq(term), "GetTermEvaluator176 stored term is correct " + term);	       
		is(field1, strEq("CHA"), "GetTermEvaluator176 field statAbbrev is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator177() {
		EvaluatorFactoryTest.loadAll();

		String term = "STRSCORE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCStatTotalTermEvaluator, eq(true),
		   "GetTermEvaluator177 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator177");

		is(field0, strEq(term), "GetTermEvaluator177 stored term is correct " + term);	       
		is(field1, strEq("STR"), "GetTermEvaluator177 field statAbbrev is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator178() {
		EvaluatorFactoryTest.loadAll();

		String term = "INTSCORE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCStatTotalTermEvaluator, eq(true),
		   "GetTermEvaluator178 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator178");

		is(field0, strEq(term), "GetTermEvaluator178 stored term is correct " + term);	       
		is(field1, strEq("INT"), "GetTermEvaluator178 field statAbbrev is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator179() {
		EvaluatorFactoryTest.loadAll();

		String term = "DEXSCORE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCStatTotalTermEvaluator, eq(true),
		   "GetTermEvaluator179 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator179");

		is(field0, strEq(term), "GetTermEvaluator179 stored term is correct " + term);	       
		is(field1, strEq("DEX"), "GetTermEvaluator179 field statAbbrev is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator180() {
		EvaluatorFactoryTest.loadAll();

		String term = "WISSCORE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCStatTotalTermEvaluator, eq(true),
		   "GetTermEvaluator180 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator180");

		is(field0, strEq(term), "GetTermEvaluator180 stored term is correct " + term);	       
		is(field1, strEq("WIS"), "GetTermEvaluator180 field statAbbrev is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator181() {
		EvaluatorFactoryTest.loadAll();

		String term = "CONSCORE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCStatTotalTermEvaluator, eq(true),
		   "GetTermEvaluator181 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator181");

		is(field0, strEq(term), "GetTermEvaluator181 stored term is correct " + term);	       
		is(field1, strEq("CON"), "GetTermEvaluator181 field statAbbrev is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator182() {
		EvaluatorFactoryTest.loadAll();

		String term = "CHASCORE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCStatTotalTermEvaluator, eq(true),
		   "GetTermEvaluator182 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator182");

		is(field0, strEq(term), "GetTermEvaluator182 stored term is correct " + term);	       
		is(field1, strEq("CHA"), "GetTermEvaluator182 field statAbbrev is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator183() {
		EvaluatorFactoryTest.loadAll();

		String term = "STR.BASE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCStatBaseTermEvaluator, eq(true),
		   "GetTermEvaluator183 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator183");

		is(field0, strEq(term), "GetTermEvaluator183 stored term is correct " + term);	       
		is(field1, strEq("STR"), "GetTermEvaluator183 field statAbbrev is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator184() {
		EvaluatorFactoryTest.loadAll();

		String term = "INT.BASE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCStatBaseTermEvaluator, eq(true),
		   "GetTermEvaluator184 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator184");

		is(field0, strEq(term), "GetTermEvaluator184 stored term is correct " + term);	       
		is(field1, strEq("INT"), "GetTermEvaluator184 field statAbbrev is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator185() {
		EvaluatorFactoryTest.loadAll();

		String term = "DEX.BASE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCStatBaseTermEvaluator, eq(true),
		   "GetTermEvaluator185 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator185");

		is(field0, strEq(term), "GetTermEvaluator185 stored term is correct " + term);	       
		is(field1, strEq("DEX"), "GetTermEvaluator185 field statAbbrev is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator186() {
		EvaluatorFactoryTest.loadAll();

		String term = "WIS.BASE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCStatBaseTermEvaluator, eq(true),
		   "GetTermEvaluator186 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator186");

		is(field0, strEq(term), "GetTermEvaluator186 stored term is correct " + term);	       
		is(field1, strEq("WIS"), "GetTermEvaluator186 field statAbbrev is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator187() {
		EvaluatorFactoryTest.loadAll();

		String term = "CON.BASE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCStatBaseTermEvaluator, eq(true),
		   "GetTermEvaluator187 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator187");

		is(field0, strEq(term), "GetTermEvaluator187 stored term is correct " + term);	       
		is(field1, strEq("CON"), "GetTermEvaluator187 field statAbbrev is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator188() {
		EvaluatorFactoryTest.loadAll();

		String term = "CHA.BASE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCStatBaseTermEvaluator, eq(true),
		   "GetTermEvaluator188 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator188");

		is(field0, strEq(term), "GetTermEvaluator188 stored term is correct " + term);	       
		is(field1, strEq("CHA"), "GetTermEvaluator188 field statAbbrev is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator189() {
		EvaluatorFactoryTest.loadAll();

		String term = "STRSCORE.BASE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCStatBaseTermEvaluator, eq(true),
		   "GetTermEvaluator189 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator189");

		is(field0, strEq(term), "GetTermEvaluator189 stored term is correct " + term);	       
		is(field1, strEq("STR"), "GetTermEvaluator189 field statAbbrev is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator190() {
		EvaluatorFactoryTest.loadAll();

		String term = "INTSCORE.BASE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCStatBaseTermEvaluator, eq(true),
		   "GetTermEvaluator190 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator190");

		is(field0, strEq(term), "GetTermEvaluator190 stored term is correct " + term);	       
		is(field1, strEq("INT"), "GetTermEvaluator190 field statAbbrev is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator191() {
		EvaluatorFactoryTest.loadAll();

		String term = "DEXSCORE.BASE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCStatBaseTermEvaluator, eq(true),
		   "GetTermEvaluator191 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator191");

		is(field0, strEq(term), "GetTermEvaluator191 stored term is correct " + term);	       
		is(field1, strEq("DEX"), "GetTermEvaluator191 field statAbbrev is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator192() {
		EvaluatorFactoryTest.loadAll();

		String term = "WISSCORE.BASE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCStatBaseTermEvaluator, eq(true),
		   "GetTermEvaluator192 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator192");

		is(field0, strEq(term), "GetTermEvaluator192 stored term is correct " + term);	       
		is(field1, strEq("WIS"), "GetTermEvaluator192 field statAbbrev is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator193() {
		EvaluatorFactoryTest.loadAll();

		String term = "CONSCORE.BASE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCStatBaseTermEvaluator, eq(true),
		   "GetTermEvaluator193 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator193");

		is(field0, strEq(term), "GetTermEvaluator193 stored term is correct " + term);	       
		is(field1, strEq("CON"), "GetTermEvaluator193 field statAbbrev is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator194() {
		EvaluatorFactoryTest.loadAll();

		String term = "CHASCORE.BASE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t instanceof PCStatBaseTermEvaluator, eq(true),
		   "GetTermEvaluator194 evaluator correct for " + term);

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
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator194");

		is(field0, strEq(term), "GetTermEvaluator194 stored term is correct " + term);	       
		is(field1, strEq("CHA"), "GetTermEvaluator194 field statAbbrev is correct ");	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator195() {
		EvaluatorFactoryTest.loadAll();

		String term = "STRENGTH";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator195 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator196() {
		EvaluatorFactoryTest.loadAll();

		String term = "INTELLIGENCE";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator196 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator197() {
		EvaluatorFactoryTest.loadAll();

		String term = "DEXTERITY";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator197 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator198() {
		EvaluatorFactoryTest.loadAll();

		String term = "WISDOM";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator198 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator199() {
		EvaluatorFactoryTest.loadAll();

		String term = "CONSTITUTION";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator199 evaluator is null");
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator200() {
		EvaluatorFactoryTest.loadAll();

		String term = "CHARMING";

		TermEvaluator t = EvaluatorFactory.PC.getTermEvaluator(term, "");

		is(t, eqnull(), "GetTermEvaluator200 evaluator is null");
	}

	
	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator201() {
		EvaluatorFactoryTest.loadAll();

		String term = "BASECOST";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		is(t instanceof EQBaseCostTermEvaluator, eq(true),
		   "GetTermEvaluator201 evaluator correct for " + term);

		Class<?> uClass = EQBaseCostTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator201");

		is(field0, strEq(term), "GetTermEvaluator201 stored term is correct " + term);	       
	}



	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator202() {
		EvaluatorFactoryTest.loadAll();

		String term = "CRITMULT";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		is(t instanceof EQCritMultTermEvaluator, eq(true),
		   "GetTermEvaluator202 evaluator correct for " + term);

		Class<?> uClass = EQCritMultTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator202");

		is(field0, strEq(term), "GetTermEvaluator202 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator203() {
		EvaluatorFactoryTest.loadAll();

		String term = "DMGDICE";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		is(t instanceof EQDamageDiceTermEvaluator, eq(true),
		   "GetTermEvaluator203 evaluator correct for " + term);

		Class<?> uClass = EQDamageDiceTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator203");

		is(field0, strEq(term), "GetTermEvaluator203 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator204() {
		EvaluatorFactoryTest.loadAll();

		String term = "DMGDIE";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		is(t instanceof EQDamageDieTermEvaluator, eq(true),
		   "GetTermEvaluator204 evaluator correct for " + term);

		Class<?> uClass = EQDamageDieTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator204");

		is(field0, strEq(term), "GetTermEvaluator204 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator205() {
		EvaluatorFactoryTest.loadAll();

		String term = "EQACCHECK";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		is(t instanceof EQACCheckTermEvaluator, eq(true),
		   "GetTermEvaluator205 evaluator correct for " + term);

		Class<?> uClass = EQACCheckTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator205");

		is(field0, strEq(term), "GetTermEvaluator205 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator206() {
		EvaluatorFactoryTest.loadAll();

		String term = "EQHANDS";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		is(t instanceof EQHandsTermEvaluator, eq(true),
		   "GetTermEvaluator206 evaluator correct for " + term);

		Class<?> uClass = EQHandsTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator206");

		is(field0, strEq(term), "GetTermEvaluator206 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator207() {
		EvaluatorFactoryTest.loadAll();

		String term = "EQSPELLFAIL";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		is(t instanceof EQSpellFailureTermEvaluator, eq(true),
		   "GetTermEvaluator207 evaluator correct for " + term);

		Class<?> uClass = EQSpellFailureTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator207");

		is(field0, strEq(term), "GetTermEvaluator207 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator208() {
		EvaluatorFactoryTest.loadAll();

		String term = "EQUIP.SIZE";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		is(t instanceof EQEquipSizeTermEvaluator, eq(true),
		   "GetTermEvaluator208 evaluator correct for " + term);

		Class<?> uClass = EQEquipSizeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator208");

		is(field0, strEq(term), "GetTermEvaluator208 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator209() {
		EvaluatorFactoryTest.loadAll();

		String term = "EQUIP.SIZE.INT";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		is(t instanceof EQSizeTermEvaluator, eq(true),
		   "GetTermEvaluator209 evaluator correct for " + term);

		Class<?> uClass = EQSizeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator209");

		is(field0, strEq(term), "GetTermEvaluator209 stored term is correct " + term);	       
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluatorAltPlusTotal() {
		EvaluatorFactoryTest.loadAll();

		String term = "ALTPLUSTOTAL";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		is(t instanceof EQAltPlusTotalTermEvaluator, eq(true),
		   "EQAltPlusTotalTermEvaluator evaluator correct for " + term);

		Class<?> uClass = EQAltPlusTotalTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in testGetTermEvaluatorAltPlusTotal");

		is(field0, strEq(term), "testGetTermEvaluatorAltPlusTotal stored term is correct " + term);	       
	}

	
	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluatorPlusTotal() {
		EvaluatorFactoryTest.loadAll();

		String term = "PLUSTOTAL";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		is(t instanceof EQPlusTotalTermEvaluator, eq(true),
		   "EQPlusTotalTermEvaluator evaluator correct for " + term);

		Class<?> uClass = EQPlusTotalTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in testGetTermEvaluatorPlusTotal");

		is(field0, strEq(term), "testGetTermEvaluatorPlusTotal stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator210() {
		EvaluatorFactoryTest.loadAll();

		String term = "RACEREACH";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "RACE:Gnome");

		is(t instanceof EQRaceReachTermEvaluator, eq(true),
		   "GetTermEvaluator210 evaluator correct for " + term);

		Class<?> uClass = EQRaceReachTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator210");

		is(field0, strEq(term), "GetTermEvaluator210 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator211() {
		EvaluatorFactoryTest.loadAll();

		String term = "RANGE";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		is(t instanceof EQRangeTermEvaluator, eq(true),
		   "GetTermEvaluator211 evaluator correct for " + term);

		Class<?> uClass = EQRangeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator211");

		is(field0, strEq(term), "GetTermEvaluator211 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator212() {
		EvaluatorFactoryTest.loadAll();

		String term = "REACH";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		is(t instanceof EQReachTermEvaluator, eq(true),
		   "GetTermEvaluator212 evaluator correct for " + term);

		Class<?> uClass = EQReachTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator212");

		is(field0, strEq(term), "GetTermEvaluator212 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator213() {
		EvaluatorFactoryTest.loadAll();

		String term = "REACHMULT";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		is(t instanceof EQReachMultTermEvaluator, eq(true),
		   "GetTermEvaluator213 evaluator correct for " + term);

		Class<?> uClass = EQReachMultTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator213");

		is(field0, strEq(term), "GetTermEvaluator213 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator214() {
		EvaluatorFactoryTest.loadAll();

		String term = "SIZE";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		is(t instanceof EQSizeTermEvaluator, eq(true),
		   "GetTermEvaluator214 evaluator correct for " + term);

		Class<?> uClass = EQSizeTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator214");

		is(field0, strEq(term), "GetTermEvaluator214 stored term is correct " + term);	       
	}


	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator215() {
		EvaluatorFactoryTest.loadAll();

		String term = "WT";

		TermEvaluator t = EvaluatorFactory.EQ.getTermEvaluator(term, "");

		is(t instanceof EQWeightTermEvaluator, eq(true),
		   "GetTermEvaluator215 evaluator correct for " + term);

		Class<?> uClass = EQWeightTermEvaluator.class;

		Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

		String field0 = "";
		boolean ok;
		try
		{
			ok = true;
			field0 = (String) pF0.get(t);
		}
		catch (ClassCastException e)
		{
			ok = false;
		}
		catch (IllegalAccessException e)
		{
			ok = false;
		}

		is(ok, eq(true), "No illegal access in getTermEvaluator215");

		is(field0, strEq(term), "GetTermEvaluator215 stored term is correct " + term);	       
	}

	/**
	 * Method: getTermEvaluator(String term, String source)
	 */
	public void testGetTermEvaluator216() {
		EvaluatorFactoryTest.loadAll();

		String term1 = "CL;BEFORELEVEL=15";

		TermEvaluator t1 = EvaluatorFactory.PC.getTermEvaluator(term1, "CLASS:Druid");
		TermEvaluator t2 = EvaluatorFactory.PC.getTermEvaluator(term1, "CLASS:Ranger");
		TermEvaluator t3 = EvaluatorFactory.PC.getTermEvaluator(term1, "CLASS:Druid");

		is(t1 instanceof PCCLBeforeLevelTermEvaluator, eq(true),
		   "GetTermEvaluator215 t1 evaluator correct for " + term1);
		is(t2 instanceof PCCLBeforeLevelTermEvaluator, eq(true),
		   "GetTermEvaluator215 t2 evaluator correct for " + term1);
		is(t3 instanceof PCCLBeforeLevelTermEvaluator, eq(true),
		   "GetTermEvaluator215 t3 evaluator correct for " + term1);

		is(t1.equals(t2), eq(false), "t1 and t2 are different objects");
		is(t1.equals(t3), eq(true),  "t1 and t3 are the Same object");
		is(t2.equals(t3), eq(false), "t2 and t3 are different objects");

		String term2 = "CL;BEFORELEVEL=14";

		TermEvaluator t4 = EvaluatorFactory.PC.getTermEvaluator(term2, "CLASS:Druid");
		TermEvaluator t5 = EvaluatorFactory.PC.getTermEvaluator(term2, "CLASS:Ranger");
		TermEvaluator t6 = EvaluatorFactory.PC.getTermEvaluator(term2, "CLASS:Druid");

		is(t4 instanceof PCCLBeforeLevelTermEvaluator, eq(true),
			"GetTermEvaluator215 t4 evaluator correct for " + term2);
		is(t6 instanceof PCCLBeforeLevelTermEvaluator, eq(true),
		   "GetTermEvaluator215 t5 evaluator correct for " + term2);
		is(t5 instanceof PCCLBeforeLevelTermEvaluator, eq(true),
		  "GetTermEvaluator215 t6 evaluator correct for " + term2);

		is(t4.equals(t6), eq(true),  "t4 and t6 are the Same object");
		is(t4.equals(t5), eq(false), "t4 and t5 are different objects");
		is(t6.equals(t5), eq(false), "t6 and t5 are different objects");
		
		is(t1.equals(t4), eq(false),  "t1 and t4 are diffferent objects");
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
	
		Globals.createEmptyRace();
	
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
		SettingsHandler.validateBonuses = SettingsHandler.getPCGenOption("validateBonuses", false); //$NON-NLS-1$
	}

	/**
	 * Load and initialise the properties, plugins and GameModes
	 */
	public static void loadAll()
	{
		SettingsHandler.readOptionsProperties();
		SettingsHandler.getOptionsFromProperties(null);
	
		TestHelper.loadPlugins();
		EvaluatorFactoryTest.initGameModes();
	}

}
