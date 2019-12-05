package pcgen.core.term;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import org.junit.jupiter.api.Test;

/**
 * Copyright (c) 2008 Andrew Wilson <nuance@users.sourceforge.net>.
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

class EvaluatorFactoryTest
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
            //assertNull(pF.get(null), "");
            //assertNull(sF.get(null), "");

            Pattern iVP = (Pattern) pF.get(EvaluatorFactory.PC);
            // noinspection unchecked
            Map<String, TermEvaluatorBuilderPCVar> eS =
                    (Map<String, TermEvaluatorBuilderPCVar>) sF.get(EvaluatorFactory.PC);

            // don't need instanceof, would throw ClassCastException
            assertNotNull(iVP, "Pattern is now instantiated");
            assertNotNull(eS, "Map is now instantiated");
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor001");
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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor002");

        String term = "ACCHECK";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor002 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor002 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor003");

        String term = "ACHECK";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor003 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor003 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor004");

        String term = "ARMORACCHECK";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor004 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor004 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor005");

        String term = "ARMORACHECK";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor005 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor005 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor006");

        String term = "BAB";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor006 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor006 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor007");

        String term = "BASESPELLSTAT";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor007 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor007 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor008");

        String term = "BL";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor008 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor008 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor009");

        String term = "BL.Wizard";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor009 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor009 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor010");

        String term = "BL=Cleric";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor010 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor010 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor011");

        String term = "CASTERLEVEL";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor011 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor011 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor012");

        String term = "CASTERLEVEL.TOTAL";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor012 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor012 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor013");

        String term = "CL";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor013 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor013 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor014");

        String term = "CL.Bard";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor014 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor014 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor015");

        String term = "CL;BEFORELEVEL.10";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor015 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor015 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor016");

        String term = "CL;BEFORELEVEL=15";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor016 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor016 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor017");

        String term = "CL=Rogue";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor017 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor017 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor018");

        String term = "CLASS.Druid";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor018 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor018 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor019");

        String term = "CLASS=Paladin";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor019 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor019 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor020");

        String term = "CLASSLEVEL.Bard";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor020 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor020 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor021");

        String term = "CLASSLEVEL=Rogue";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor021 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor021 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor022");

        String term = "COUNT[ATTACKS]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor022 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor022 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor023");

        String term = "COUNT[CHECKS]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor023 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor023 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor024");

        String term = "COUNT[CLASSES]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor024 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor024 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor025");

        String term = "COUNT[CONTAINERS]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor025 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor025 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor026");

        String term = "COUNT[DOMAINS]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor026 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor026 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor027");

        String term = "COUNT[EQTYPE.MERGENONE.IS.FOO]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor027 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor027 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor028");

        String term = "COUNT[EQUIPMENT.MERGENONE.NOT.FOO]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor028 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor028 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor029");

        String term = "COUNT[FEATAUTOTYPE.HIDDEN]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor029 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor029 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor030");

        String term = "COUNT[FEATAUTOTYPE=VISIBLE]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor030 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor030 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor031");

        String term = "COUNT[FEATNAME.Jack of all trades]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor031 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor031 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor032");

        String term = "COUNT[FEATNAME=Improved Initiative]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor032 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor032 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor033");

        String term = "COUNT[FEATS.ALL]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor033 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor033 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor034");

        String term = "COUNT[FEATS.HIDDEN]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor034 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor034 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor035");

        String term = "COUNT[FEATS.VISIBLE]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor035 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor035 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor036");

        String term = "COUNT[FEATSALL.ALL]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor036 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor036 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor037");

        String term = "COUNT[FEATSALL.HIDDEN]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor037 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor037 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor038");

        String term = "COUNT[FEATSALL.VISIBLE]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor038 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor038 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor039");

        String term = "COUNT[FEATSALL]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor039 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor039 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor040");

        String term = "COUNT[FEATSAUTO.ALL]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor040 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor040 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor041");

        String term = "COUNT[FEATSAUTO.HIDDEN]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor041 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor041 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor042");

        String term = "COUNT[FEATSAUTO.VISIBLE]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor042 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor042 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor043");

        String term = "COUNT[FEATSAUTO]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor043 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor043 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor044");

        String term = "COUNT[FEATS]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor044 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor044 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor045");

        String term = "COUNT[FEATTYPE.BAR]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor045 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor045 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor046");

        String term = "COUNT[FEATTYPE.BAZ]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor046 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor046 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor047");

        String term = "COUNT[FOLLOWERS]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor047 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor047 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor048");

        String term = "COUNT[FOLLOWERTYPE.MOO]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor048 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor048 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor049");

        String term = "COUNT[LANGUAGES]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor049 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor049 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor050");

        String term = "COUNT[MISC.COMPANIONS]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor050 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor050 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor051");

        String term = "COUNT[MISC.FUNDS]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor051 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor051 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor052");

        String term = "COUNT[MISC.MAGIC]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor052 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor052 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor053");

        String term = "COUNT[MOVE]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor053 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor053 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor054");

        String term = "COUNT[NOTES]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor054 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor054 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor055");

        String term = "COUNT[RACESUBTYPES]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor055 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor055 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor056");

        String term = "COUNT[SA]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor056 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor056 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor057");

        String term = "COUNT[SKILLS]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor057 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor057 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor058");

        String term = "COUNT[SKILLTYPE.KNOWLEDGE]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor058 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor058 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor059");

        String term = "COUNT[SKILLTYPE=PERFORM]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor059 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor059 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor060");

        String term = "COUNT[SPELLBOOKS]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor060 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor060 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor061");

        String term = "COUNT[SPELLCLASSES]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor061 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor061 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor062");

        String term = "COUNT[SPELLRACE]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor062 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor062 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor063");

        String term = "COUNT[SPELLSINBOOK]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor063 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor063 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor064");

        String term = "COUNT[SPELLSKNOWN]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor064 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor064 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor065");

        String term = "COUNT[SPELLSLEVELSINBOOK]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor065 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor065 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor066");

        String term = "COUNT[SPELLTIMES]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor066 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor066 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor067");

        String term = "COUNT[STATS]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor067 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor067 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor068");

        String term = "COUNT[TEMPBONUSNAMES]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor068 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor068 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor069");

        String term = "COUNT[TEMPLATES]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor069 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor069 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor070");

        String term = "COUNT[VFEATS.ALL]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor070 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor070 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor071");

        String term = "COUNT[VFEATS.HIDDEN]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor071 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor071 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor072");

        String term = "COUNT[VFEATS.VISIBLE]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor072 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor072 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor073");

        String term = "COUNT[VFEATS]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor073 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor073 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor074");

        String term = "COUNT[VFEATTYPE.HIDDEN]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor074 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor074 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor075");

        String term = "COUNT[VFEATTYPE=ALL]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor075 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor075 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor076");

        String term = "COUNT[VISIBLETEMPLATES]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor076 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor076 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor077");

        String term = "COUNT[VISION]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor077 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor077 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor078");

        String term = "ENCUMBERANCE";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor078 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor078 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor079");

        String term = "EQTYPE.EQUIPPED.IS.FOO";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor079 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor079 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor080");

        String term = "HASDEITY:Bane";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor080 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor080 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor081");

        String term = "HASFEAT:Endurance";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor081 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor081 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor082");

        String term = "HD";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor082 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor082 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor083");

        String term = "MAXCASTABLE";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor083 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor083 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor084");

        String term = "MODEQUIPSPELLFAILURE";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor084 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor084 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor085");

        String term = "MOVEBASE";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor085 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor085 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor086");

        String term = "MOVE[Walk]";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor086 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor086 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor087");

        String term = "PC.HEIGHT";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor087 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor087 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor088");

        String term = "PC.SIZEINT";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor088 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor088 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor089");

        String term = "PC.WEIGHT";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor089 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor089 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor090");

        String term = "PROFACCHECK";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor090 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor090 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor091");

        String term = "RACESIZE";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor091 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor091 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor092");

        String term = "SCORE";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor092 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor092 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor093");

        String term = "SHIELDACCHECK";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor093 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor093 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor094");

        String term = "SHIELDACHECK";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor094 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor094 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor095");

        String term = "SIZE";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor095 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor095 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor096");

        String term = "SIZEMOD";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor096 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor096 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor097");

        String term = "SKILLRANK.Tumble";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor097 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor097 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor098");

        String term = "SKILLRANK=Perform (Dance)";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor098 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor098 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor099");

        String term = "SKILLTOTAL.Tumble";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor099 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor099 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor100");

        String term = "SKILLTOTAL=Perform (Dance)";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor100 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor100 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor101");

        String term = "SPELLBASESTAT";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor101 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor101 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor102");

        String term = "SPELLBASESTATSCORE";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor102 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor102 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor103");

        String term = "SPELLLEVEL";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor103 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor103 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor104");

        String term = "TL";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor104 pattern matches for " + term);
        assertEquals(term, mat.group(1), () -> "Constructor104 pattern matches all of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor105");

        String term = "VARDEFINED:MilkyBarsEaten";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor105 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor105 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor106");

        String term = "WEIGHT.CARRIED";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor106 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor106 pattern matches start of " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in Constructor107");

        String term = "DEXSCORE";
        Matcher mat = iVP.matcher(term);
        assertTrue(mat.find(), () -> "Constructor107 pattern matches for " + term);
        assertTrue(term.startsWith(mat.group(1)), () -> "Constructor107 pattern matches start of " + term);

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

        assertTrue(t instanceof PCACcheckTermEvaluator, () -> "GetTermEvaluator001 evaluator correct for " + term);

        Class<?> uClass = PCACcheckTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator001");

        assertEquals(term, field0, () -> "GetTermEvaluator001 stored term is correct " + term);
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

        assertTrue(t instanceof PCACcheckTermEvaluator, () -> "GetTermEvaluator002 evaluator correct for " + term);

        Class<?> uClass = PCACcheckTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator002");

        assertEquals(term, field0, () -> "GetTermEvaluator002 stored term is correct " + term);
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

        assertTrue(t instanceof PCArmourACcheckTermEvaluator, () -> "GetTermEvaluator003 evaluator correct for " + term);

        Class<?> uClass = PCArmourACcheckTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator003");

        assertEquals(term, field0, () -> "GetTermEvaluator003 stored term is correct " + term);
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

        assertTrue(t instanceof PCArmourACcheckTermEvaluator, () -> "GetTermEvaluator004 evaluator correct for " + term);

        Class<?> uClass = PCArmourACcheckTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator004");

        assertEquals(term, field0, () -> "GetTermEvaluator004 stored term is correct " + term);
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

        assertTrue(t instanceof PCBABTermEvaluator, () -> "GetTermEvaluator005 evaluator correct for " + term);

        Class<?> uClass = PCBABTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator005");

        assertEquals(term, field0, () -> "GetTermEvaluator005 stored term is correct " + term);
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

        assertTrue(t instanceof PCBaseSpellStatTermEvaluator, () -> "GetTermEvaluator006 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator006");

        assertEquals(term, field0, () -> "GetTermEvaluator006 stored term is correct " + term);
        assertEquals("Foo", field1, "GetTermEvaluator006 field source is correct ");
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

        assertTrue(t instanceof PCBaseSpellStatTermEvaluator, () -> "GetTermEvaluator007 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator007");

        assertEquals(term, field0, () -> "GetTermEvaluator007 stored term is correct " + term);
        assertEquals("", field1, "GetTermEvaluator007 field source is correct ");
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

        assertTrue(t instanceof PCCasterLevelRaceTermEvaluator,
                () -> "GetTermEvaluator008 evaluator correct for " + term
        );

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator008");

        assertEquals(term, field0, () -> "GetTermEvaluator008 stored term is correct " + term);
        assertEquals("RACE.Bar", field1, "GetTermEvaluator008 field source is correct ");
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

        assertTrue(t instanceof PCCasterLevelClassTermEvaluator,
                () -> "GetTermEvaluator009 evaluator correct for " + term
        );

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator009");

        assertEquals(term, field0, () -> "GetTermEvaluator009 stored term is correct " + term);
        assertEquals("Foo", field1, "GetTermEvaluator009 field source is correct ");
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

        assertTrue(t instanceof PCCasterLevelTotalTermEvaluator,
                () -> "GetTermEvaluator010 evaluator correct for " + term
        );

        Class<?> uClass = PCCasterLevelTotalTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator010");

        assertEquals(term, field0, () -> "GetTermEvaluator010 stored term is correct " + term);
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

        assertTrue(t instanceof PCCasterLevelTotalTermEvaluator,
                () -> "GetTermEvaluator011 evaluator correct for " + term
        );

        Class<?> uClass = PCCasterLevelTotalTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator011");

        assertEquals(term, field0, () -> "GetTermEvaluator011 stored term is correct " + term);
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

        assertTrue(t instanceof PCCountAttacksTermEvaluator, () -> "GetTermEvaluator012 evaluator correct for " + term);

        Class<?> uClass = PCCountAttacksTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator012");

        assertEquals(term, field0, () -> "GetTermEvaluator012 stored term is correct " + term);
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

        assertTrue(t instanceof PCCountChecksTermEvaluator, () -> "GetTermEvaluator013 evaluator correct for " + term);

        Class<?> uClass = PCCountChecksTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator013");

        assertEquals(term, field0, () -> "GetTermEvaluator013 stored term is correct " + term);
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

        assertTrue(t instanceof PCCountClassesTermEvaluator, () -> "GetTermEvaluator014 evaluator correct for " + term);

        Class<?> uClass = PCCountClassesTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator014");

        assertEquals(term, field0, () -> "GetTermEvaluator014 stored term is correct " + term);
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

        assertTrue(t instanceof PCCountContainersTermEvaluator,
                () -> "GetTermEvaluator015 evaluator correct for " + term
        );

        Class<?> uClass = PCCountContainersTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator015");

        assertEquals(term, field0, () -> "GetTermEvaluator015 stored term is correct " + term);
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

        assertTrue(t instanceof PCCountDomainsTermEvaluator, () -> "GetTermEvaluator016 evaluator correct for " + term);

        Class<?> uClass = PCCountDomainsTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator016");

        assertEquals(term, field0, () -> "GetTermEvaluator016 stored term is correct " + term);
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
                t instanceof PCCountAbilitiesNatureAllTermEvaluator,
                () -> "GetTermEvaluator017 evaluator correct for " + term
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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator017");

        assertEquals(term, field0, () -> "GetTermEvaluator017 stored term is correct " + term);
        assertTrue(field1, "GetTermEvaluator017 field hidden is correct ");
        assertTrue(field2, "GetTermEvaluator017 field visible is correct ");
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
                t instanceof PCCountAbilitiesNatureAllTermEvaluator,
                () -> "GetTermEvaluator018 evaluator correct for " + term
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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator018");

        assertEquals(term, field0, () -> "GetTermEvaluator018 stored term is correct " + term);
        assertTrue(field1, "GetTermEvaluator018 field hidden is correct ");
        assertFalse(field2, "GetTermEvaluator018 field visible is correct ");
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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator019");

        assertEquals(term, field0, () -> "GetTermEvaluator019 stored term is correct " + term);
        assertFalse(field1, "GetTermEvaluator019 field hidden is correct ");
        assertTrue(field2, "GetTermEvaluator019 field visible is correct ");
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
                t instanceof PCCountAbilitiesNatureAllTermEvaluator,
                () -> "GetTermEvaluator020 evaluator correct for " + term
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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator020");

        assertEquals(term, field0, () -> "GetTermEvaluator020 stored term is correct " + term);
        assertFalse(field1, "GetTermEvaluator020 field hidden is correct ");
        assertTrue(field2, "GetTermEvaluator020 field visible is correct ");
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
                t instanceof PCCountAbilitiesNatureAutoTermEvaluator,
                () -> "GetTermEvaluator021 evaluator correct for " + term
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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator021");

        assertEquals(term, field0, () -> "GetTermEvaluator021 stored term is correct " + term);
        assertTrue(field1, "GetTermEvaluator021 field hidden is correct ");
        assertTrue(field2, "GetTermEvaluator021 field visible is correct ");
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
                t instanceof PCCountAbilitiesNatureAutoTermEvaluator,
                () -> "GetTermEvaluator022 evaluator correct for " + term
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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator022");

        assertEquals(term, field0, () -> "GetTermEvaluator022 stored term is correct " + term);
        assertTrue(field1, "GetTermEvaluator022 field hidden is correct ");
        assertFalse(field2, "GetTermEvaluator022 field visible is correct ");
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
                t instanceof PCCountAbilitiesNatureAutoTermEvaluator,
                () -> "GetTermEvaluator023 evaluator correct for " + term
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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator023");

        assertEquals(term, field0, () -> "GetTermEvaluator023 stored term is correct " + term);
        assertFalse(field1, "GetTermEvaluator023 field hidden is correct ");
        assertTrue(field2, "GetTermEvaluator023 field visible is correct ");
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
                t instanceof PCCountAbilitiesNatureAutoTermEvaluator,
                () -> "GetTermEvaluator024 evaluator correct for " + term
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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator024");

        assertEquals(term, field0, () -> "GetTermEvaluator024 stored term is correct " + term);
        assertFalse(field1, "GetTermEvaluator024 field hidden is correct ");
        assertTrue(field2, "GetTermEvaluator024 field visible is correct ");
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
                t instanceof PCCountAbilitiesNatureNormalTermEvaluator,
                () -> "GetTermEvaluator025 evaluator correct for " + term
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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator025");

        assertEquals(term, field0, () -> "GetTermEvaluator025 stored term is correct " + term);
        assertTrue(field1, "GetTermEvaluator025 field hidden is correct ");
        assertTrue(field2, "GetTermEvaluator025 field visible is correct ");
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
                t instanceof PCCountAbilitiesNatureNormalTermEvaluator,
                () -> "GetTermEvaluator026 evaluator correct for " + term
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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator026");

        assertEquals(term, field0, () -> "GetTermEvaluator026 stored term is correct " + term);
        assertTrue(field1, "GetTermEvaluator026 field hidden is correct ");
        assertFalse(field2, "GetTermEvaluator026 field visible is correct ");
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
                t instanceof PCCountAbilitiesNatureNormalTermEvaluator,
                () -> "GetTermEvaluator027 evaluator correct for " + term
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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator027");

        assertEquals(term, field0, () -> "GetTermEvaluator027 stored term is correct " + term);
        assertFalse(field1, "GetTermEvaluator027 field hidden is correct ");
        assertTrue(field2, "GetTermEvaluator027 field visible is correct ");
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
                t instanceof PCCountAbilitiesNatureNormalTermEvaluator,
                () -> "GetTermEvaluator028 evaluator correct for " + term
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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator028");

        assertEquals(term, field0, () -> "GetTermEvaluator028 stored term is correct " + term);
        assertFalse(field1, "GetTermEvaluator028 field hidden is correct ");
        assertTrue(field2, "GetTermEvaluator028 field visible is correct ");
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
                t instanceof PCCountAbilitiesNatureVirtualTermEvaluator,
                () -> "GetTermEvaluator029 evaluator correct for " + term
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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator029");

        assertEquals(term, field0, () -> "GetTermEvaluator029 stored term is correct " + term);
        assertTrue(field1, "GetTermEvaluator029 field hidden is correct ");
        assertTrue(field2, "GetTermEvaluator029 field visible is correct ");
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
                t instanceof PCCountAbilitiesNatureVirtualTermEvaluator,
                () -> "GetTermEvaluator030 evaluator correct for " + term
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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator030");

        assertEquals(term, field0, () -> "GetTermEvaluator030 stored term is correct " + term);
        assertTrue(field1, "GetTermEvaluator030 field hidden is correct ");
        assertFalse(field2, "GetTermEvaluator030 field visible is correct ");
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
                t instanceof PCCountAbilitiesNatureVirtualTermEvaluator,
                () -> "GetTermEvaluator031 evaluator correct for " + term
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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator031");

        assertEquals(term, field0, () -> "GetTermEvaluator031 stored term is correct " + term);
        assertFalse(field1, "GetTermEvaluator031 field hidden is correct ");
        assertTrue(field2, "GetTermEvaluator031 field visible is correct ");
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
                t instanceof PCCountAbilitiesNatureVirtualTermEvaluator,
                () -> "GetTermEvaluator032 evaluator correct for " + term
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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator032");

        assertEquals(term, field0, () -> "GetTermEvaluator032 stored term is correct " + term);
        assertFalse(field1, "GetTermEvaluator032 field hidden is correct ");
        assertTrue(field2, "GetTermEvaluator032 field visible is correct ");
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

        assertTrue(t instanceof PCCountFollowersTermEvaluator,
                () -> "GetTermEvaluator033 evaluator correct for " + term
        );

        Class<?> uClass = PCCountFollowersTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator033");

        assertEquals(term, field0, () -> "GetTermEvaluator033 stored term is correct " + term);
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

        assertTrue(t instanceof PCCountLanguagesTermEvaluator,
                () -> "GetTermEvaluator034 evaluator correct for " + term
        );

        Class<?> uClass = PCCountLanguagesTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator034");

        assertEquals(term, field0, () -> "GetTermEvaluator034 stored term is correct " + term);
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
                t instanceof PCCountMiscCompanionsTermEvaluator,
                () -> "GetTermEvaluator035 evaluator correct for " + term
        );

        Class<?> uClass = PCCountMiscCompanionsTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator035");

        assertEquals(term, field0, () -> "GetTermEvaluator035 stored term is correct " + term);
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

        assertTrue(t instanceof PCCountMiscFundsTermEvaluator,
                () -> "GetTermEvaluator036 evaluator correct for " + term
        );

        Class<?> uClass = PCCountMiscFundsTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator036");

        assertEquals(term, field0, () -> "GetTermEvaluator036 stored term is correct " + term);
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

        assertTrue(t instanceof PCCountMiscMagicTermEvaluator,
                () -> "GetTermEvaluator037 evaluator correct for " + term
        );

        Class<?> uClass = PCCountMiscMagicTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator037");

        assertEquals(term, field0, () -> "GetTermEvaluator037 stored term is correct " + term);
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

        assertTrue(t instanceof PCCountMoveTermEvaluator, () -> "GetTermEvaluator038 evaluator correct for " + term);

        Class<?> uClass = PCCountMoveTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator038");

        assertEquals(term, field0, () -> "GetTermEvaluator038 stored term is correct " + term);
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

        assertTrue(t instanceof PCCountNotesTermEvaluator, () -> "GetTermEvaluator039 evaluator correct for " + term);

        Class<?> uClass = PCCountNotesTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator039");

        assertEquals(term, field0, () -> "GetTermEvaluator039 stored term is correct " + term);
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

        assertTrue(t instanceof PCCountRaceSubTypesTermEvaluator,
                () -> "GetTermEvaluator040 evaluator correct for " + term
        );

        Class<?> uClass = PCCountRaceSubTypesTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator040");

        assertEquals(term, field0, () -> "GetTermEvaluator040 stored term is correct " + term);
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

        assertTrue(t instanceof PCCountSABTermEvaluator, () -> "GetTermEvaluator041 evaluator correct for " + term);

        Class<?> uClass = PCCountSABTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator041");

        assertEquals(term, field0, () -> "GetTermEvaluator041 stored term is correct " + term);
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

        assertTrue(t instanceof PCCountSkillsTermEvaluator, () -> "GetTermEvaluator042 evaluator correct for " + term);

        Class<?> uClass = PCCountSkillsTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator042");

        assertEquals(term, field0, () -> "GetTermEvaluator042 stored term is correct " + term);
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

        assertTrue(t instanceof PCCountSpellClassesTermEvaluator,
                () -> "GetTermEvaluator043 evaluator correct for " + term
        );

        Class<?> uClass = PCCountSpellClassesTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator043");

        assertEquals(term, field0, () -> "GetTermEvaluator043 stored term is correct " + term);
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

        assertTrue(t instanceof PCCountSpellRaceTermEvaluator,
                () -> "GetTermEvaluator044 evaluator correct for " + term
        );

        Class<?> uClass = PCCountSpellRaceTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator044");

        assertEquals(term, field0, () -> "GetTermEvaluator044 stored term is correct " + term);
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

        assertTrue(t instanceof PCCountStatsTermEvaluator, () -> "GetTermEvaluator045 evaluator correct for " + term);

        Class<?> uClass = PCCountStatsTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator045");

        assertEquals(term, field0, () -> "GetTermEvaluator045 stored term is correct " + term);
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
                t instanceof PCCountTempBonusNamesTermEvaluator,
                () -> "GetTermEvaluator046 evaluator correct for " + term
        );

        Class<?> uClass = PCCountTempBonusNamesTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator046");

        assertEquals(term, field0, () -> "GetTermEvaluator046 stored term is correct " + term);
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

        assertTrue(t instanceof PCCountTemplatesTermEvaluator,
                () -> "GetTermEvaluator047 evaluator correct for " + term
        );

        Class<?> uClass = PCCountTemplatesTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator047");

        assertEquals(term, field0, () -> "GetTermEvaluator047 stored term is correct " + term);
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
                t instanceof PCCountVisibleTemplatesTermEvaluator,
                () -> "GetTermEvaluator048 evaluator correct for " + term
        );

        Class<?> uClass = PCCountVisibleTemplatesTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator048");

        assertEquals(term, field0, () -> "GetTermEvaluator048 stored term is correct " + term);
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

        assertTrue(t instanceof PCCountVisionTermEvaluator, () -> "GetTermEvaluator049 evaluator correct for " + term);

        Class<?> uClass = PCCountVisionTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator049");

        assertEquals(term, field0, () -> "GetTermEvaluator049 stored term is correct " + term);
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

        assertTrue(t instanceof PCEncumberanceTermEvaluator, () -> "GetTermEvaluator050 evaluator correct for " + term);

        Class<?> uClass = PCEncumberanceTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator050");

        assertEquals(term, field0, () -> "GetTermEvaluator050 stored term is correct " + term);
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

        assertTrue(t instanceof PCHDTermEvaluator, () -> "GetTermEvaluator051 evaluator correct for " + term);

        Class<?> uClass = PCHDTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator051");

        assertEquals(term, field0, () -> "GetTermEvaluator051 stored term is correct " + term);
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

        assertTrue(t instanceof PCHPTermEvaluator, () -> "testGetTermEvaluatorHp evaluator correct for " + term);

        Class<?> uClass = PCHPTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in testGetTermEvaluatorHp");

        assertEquals(term, field0, () -> "testGetTermEvaluatorHp stored term is correct " + term);
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

        assertTrue(t instanceof PCMaxCastableClassTermEvaluator,
                () -> "GetTermEvaluator052 evaluator correct for " + term
        );

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator052");

        assertEquals(term, field0, () -> "GetTermEvaluator052 stored term is correct " + term);
        assertEquals("Bard", field1.getKeyName(), "GetTermEvaluator052 field spellList is correct ");
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

        assertTrue(t instanceof PCMaxCastableDomainTermEvaluator,
                () -> "GetTermEvaluator053 evaluator correct for " + term
        );

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator053");

        assertEquals(term, field0, () -> "GetTermEvaluator053 stored term is correct " + term);
        assertEquals("Fire", field1, "GetTermEvaluator053 field domainKey is correct ");
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
                t instanceof PCMaxCastableSpellTypeTermEvaluator,
                () -> "GetTermEvaluator054 evaluator correct for " + term
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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator054");

        assertEquals(term, field0, () -> "GetTermEvaluator054 stored term is correct " + term);
        assertEquals("Arcane", field1, "GetTermEvaluator054 field typeKey is correct ");
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

        assertTrue(t instanceof PCMaxCastableAnyTermEvaluator,
                () -> "GetTermEvaluator055 evaluator correct for " + term
        );

        Class<?> uClass = PCMaxCastableAnyTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator055");

        assertEquals(term, field0, () -> "GetTermEvaluator055 stored term is correct " + term);
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

        assertNull(t, "GetTermEvaluator056 evaluator is null");
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

        assertTrue(t instanceof PCMoveBaseTermEvaluator, () -> "GetTermEvaluator057 evaluator correct for " + term);

        Class<?> uClass = PCMoveBaseTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator057");

        assertEquals(term, field0, () -> "GetTermEvaluator057 stored term is correct " + term);
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

        assertTrue(t instanceof PCHeightTermEvaluator, () -> "GetTermEvaluator058 evaluator correct for " + term);

        Class<?> uClass = PCHeightTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator058");

        assertEquals(term, field0, () -> "GetTermEvaluator058 stored term is correct " + term);
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

        assertTrue(t instanceof PCWeightTermEvaluator, () -> "GetTermEvaluator059 evaluator correct for " + term);

        Class<?> uClass = PCWeightTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator059");

        assertEquals(term, field0, () -> "GetTermEvaluator059 stored term is correct " + term);
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

        assertTrue(t instanceof PCProfACCheckTermEvaluator, () -> "GetTermEvaluator060 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator060");

        assertEquals(term, field0, () -> "GetTermEvaluator060 stored term is correct " + term);
        assertEquals("Dagger", field1, "GetTermEvaluator060 field eqKey is correct ");
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

        assertTrue(t instanceof PCProfACCheckTermEvaluator, () -> "GetTermEvaluator061 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator061");

        assertEquals(term, field0, () -> "GetTermEvaluator061 stored term is correct " + term);
        assertEquals("", field1, "GetTermEvaluator061 field eqKey is correct ");
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

        assertTrue(t instanceof PCRaceSizeTermEvaluator, () -> "GetTermEvaluator062 evaluator correct for " + term);

        Class<?> uClass = PCRaceSizeTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator062");

        assertEquals(term, field0, () -> "GetTermEvaluator062 stored term is correct " + term);
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

        assertTrue(t instanceof PCScoreTermEvaluator, () -> "GetTermEvaluator063 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator063");

        assertEquals(term, field0, () -> "GetTermEvaluator063 stored term is correct " + term);
        assertEquals("INT", field1, "GetTermEvaluator063 field stat is correct ");
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

        assertTrue(t instanceof PCScoreTermEvaluator, () -> "GetTermEvaluator064 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator064");

        assertEquals(term, field0, () -> "GetTermEvaluator064 stored term is correct " + term);
        assertEquals("", field1, "GetTermEvaluator064 field stat is correct ");
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

        assertTrue(t instanceof PCShieldACcheckTermEvaluator, () -> "GetTermEvaluator065 evaluator correct for " + term);

        Class<?> uClass = PCShieldACcheckTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator065");

        assertEquals(term, field0, () -> "GetTermEvaluator065 stored term is correct " + term);
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

        assertTrue(t instanceof PCShieldACcheckTermEvaluator, () -> "GetTermEvaluator066 evaluator correct for " + term);

        Class<?> uClass = PCShieldACcheckTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator066");

        assertEquals(term, field0, () -> "GetTermEvaluator066 stored term is correct " + term);
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

        assertTrue(t instanceof PCSizeTermEvaluator, () -> "GetTermEvaluator067 evaluator correct for " + term);

        Class<?> uClass = PCSizeTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator067");

        assertEquals(term, field0, () -> "GetTermEvaluator067 stored term is correct " + term);
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

        assertTrue(t instanceof PCSizeModEvaluatorTermEvaluator,
                () -> "GetTermEvaluator068 evaluator correct for " + term
        );

        Class<?> uClass = PCSizeModEvaluatorTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator068");

        assertEquals(term, field0, () -> "GetTermEvaluator068 stored term is correct " + term);
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

        assertTrue(t instanceof PCSPellBaseStatTermEvaluator, () -> "GetTermEvaluator069 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator069");

        assertEquals(term, field0, () -> "GetTermEvaluator069 stored term is correct " + term);
        assertEquals("Cleric", field1, "GetTermEvaluator069 field classKey is correct ");
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
                t instanceof PCSPellBaseStatScoreEvaluatorTermEvaluator,
                () -> "GetTermEvaluator070 evaluator correct for " + term
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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator070");

        assertEquals(term, field0, () -> "GetTermEvaluator070 stored term is correct " + term);
        assertEquals("Cleric", field1, "GetTermEvaluator070 field classKey is correct ");
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

        assertTrue(t instanceof PCSPellBaseStatTermEvaluator, () -> "GetTermEvaluator071 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator071");

        assertEquals(term, field0, () -> "GetTermEvaluator071 stored term is correct " + term);
        assertEquals("", field1, "GetTermEvaluator071 field classKey is correct ");
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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator072");

        assertEquals(term, field0, () -> "GetTermEvaluator072 stored term is correct " + term);
        assertEquals("", field1, "GetTermEvaluator072 field classKey is correct ");
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

        assertTrue(t instanceof PCSpellLevelTermEvaluator, () -> "GetTermEvaluator073 evaluator correct for " + term);

        Class<?> uClass = PCSpellLevelTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator073");

        assertEquals(term, field0, () -> "GetTermEvaluator073 stored term is correct " + term);
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

        assertTrue(t instanceof PCTLTermEvaluator, () -> "GetTermEvaluator074 evaluator correct for " + term);

        Class<?> uClass = PCTLTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator074");

        assertEquals(term, field0, () -> "GetTermEvaluator074 stored term is correct " + term);
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

        assertTrue(t instanceof PCBLTermEvaluator, () -> "GetTermEvaluator075 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator075");

        assertEquals(term, field0, () -> "GetTermEvaluator075 stored term is correct " + term);
        assertEquals("Ranger", field1, "GetTermEvaluator075 field source is correct ");
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

        assertTrue(t instanceof PCBLTermEvaluator, () -> "GetTermEvaluator076 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator076");

        assertEquals(term, field0, () -> "GetTermEvaluator076 stored term is correct " + term);
        assertEquals("", field1, "GetTermEvaluator076 field source is correct ");
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

        assertTrue(t instanceof PCBLTermEvaluator, () -> "GetTermEvaluator077 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator077");

        assertEquals(term, field0, () -> "GetTermEvaluator077 stored term is correct " + term);
        assertEquals("Wizard", field1, "GetTermEvaluator077 field source is correct ");
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

        assertTrue(t instanceof PCBLTermEvaluator, () -> "GetTermEvaluator078 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator078");

        assertEquals(term, field0, () -> "GetTermEvaluator078 stored term is correct " + term);
        assertEquals("Cleric", field1, "GetTermEvaluator078 field source is correct ");
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

        assertTrue(t instanceof PCCLBeforeLevelTermEvaluator, () -> "GetTermEvaluator079 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator079");

        assertEquals(term, field0, () -> "GetTermEvaluator079 stored term is correct " + term);
        assertEquals("Ranger", field1, "GetTermEvaluator079 field source is correct ");
        assertEquals(10, field2.intValue(), "GetTermEvaluator079 field level is correct ");
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

        assertTrue(t instanceof PCCLBeforeLevelTermEvaluator, () -> "GetTermEvaluator080 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator080");

        assertEquals(term, field0, () -> "GetTermEvaluator080 stored term is correct " + term);
        assertEquals("Druid", field1, "GetTermEvaluator080 field source is correct ");
        assertEquals(15, field2.intValue(), "GetTermEvaluator080 field level is correct ");
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

        assertNull(t, "GetTermEvaluator081 evaluator is null");
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

        assertTrue(t instanceof PCCLTermEvaluator, () -> "GetTermEvaluator082 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator082");

        assertEquals(term, field0, () -> "GetTermEvaluator082 stored term is correct " + term);
        assertEquals("Bard (Bardiliscious)", field1, "GetTermEvaluator082 field classKey is correct ");
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

        assertTrue(t instanceof PCCLTermEvaluator, () -> "GetTermEvaluator083 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator083");

        assertEquals(term, field0, () -> "GetTermEvaluator083 stored term is correct " + term);
        assertEquals("Rogue (Sneaky)", field1, "GetTermEvaluator083 field classKey is correct ");
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

        assertTrue(t instanceof PCHasClassTermEvaluator, () -> "GetTermEvaluator084 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator084");

        assertEquals(term, field0, () -> "GetTermEvaluator084 stored term is correct " + term);
        assertEquals("Druid", field1, "GetTermEvaluator084 field source is correct ");
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

        assertTrue(t instanceof PCHasClassTermEvaluator, () -> "GetTermEvaluator085 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator085");

        assertEquals(term, field0, () -> "GetTermEvaluator085 stored term is correct " + term);
        assertEquals("Paladin", field1, "GetTermEvaluator085 field source is correct ");
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

        assertTrue(t instanceof PCCLTermEvaluator, () -> "GetTermEvaluator086 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator086");

        assertEquals(term, field0, () -> "GetTermEvaluator086 stored term is correct " + term);
        assertEquals("Ranger", field1, "GetTermEvaluator086 field classKey is correct ");
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

        assertNull(t, "GetTermEvaluator087 evaluator is null");
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

        assertTrue(t instanceof PCCLTermEvaluator, () -> "GetTermEvaluator088 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator088");

        assertEquals(term, field0, () -> "GetTermEvaluator088 stored term is correct " + term);
        assertEquals("Bard", field1, "GetTermEvaluator088 field classKey is correct ");
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

        assertTrue(t instanceof PCCLTermEvaluator, () -> "GetTermEvaluator089 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator089");

        assertEquals(term, field0, () -> "GetTermEvaluator089 stored term is correct " + term);
        assertEquals("Rogue", field1, "GetTermEvaluator089 field classKey is correct ");
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

        assertTrue(t instanceof PCCountEqTypeTermEvaluator, () -> "GetTermEvaluator090 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator090");

        assertEquals(term, field0, () -> "GetTermEvaluator090 stored term is correct " + term);
        assertEquals("", field1[0], "GetTermEvaluator090 field types[0] is correct ");
        assertEquals(Constants.MERGE_NONE, (int) field2, "GetTermEvaluator090 field merge is correct ");
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

        assertTrue(t instanceof PCCountEqTypeTermEvaluator, () -> "GetTermEvaluator091 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator091");

        assertEquals(term, field0, () -> "GetTermEvaluator091 stored term is correct " + term);
        assertEquals("", field1[0], "GetTermEvaluator091 field types[0] is correct ");
        assertEquals(Constants.MERGE_LOCATION, (int) field2, "GetTermEvaluator091 field merge is correct ");
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

        assertTrue(t instanceof PCCountEqTypeTermEvaluator, () -> "GetTermEvaluator092 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator092");

        assertEquals(term, field0, () -> "GetTermEvaluator092 stored term is correct " + term);
        assertEquals("", field1[0], "GetTermEvaluator092 field types[0] is correct ");
        assertEquals(Constants.MERGE_ALL, (int) field2, "GetTermEvaluator092 field merge is correct ");
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

        assertTrue(t instanceof PCCountEqTypeTermEvaluator, () -> "GetTermEvaluator093 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator093");

        assertEquals(term, field0, () -> "GetTermEvaluator093 stored term is correct " + term);
        assertEquals("EQUIPPED", field1[0], "GetTermEvaluator093 field types[0] is correct ");
        assertEquals(Constants.MERGE_ALL, (int) field2, "GetTermEvaluator093 field merge is correct ");
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

        assertTrue(t instanceof PCCountEqTypeTermEvaluator, () -> "GetTermEvaluator094 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator094");

        assertEquals(term, field0, () -> "GetTermEvaluator094 stored term is correct " + term);
        assertEquals("NOTEQUIPPED", field1[0], "GetTermEvaluator094 field types[0] is correct ");
        assertEquals(Constants.MERGE_ALL, (int) field2, "GetTermEvaluator094 field merge is correct ");
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

        assertTrue(t instanceof PCCountEqTypeTermEvaluator, () -> "GetTermEvaluator095 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator095");

        assertEquals(term, field0, () -> "GetTermEvaluator095 stored term is correct " + term);
        assertEquals("CONTAINER", field1[0], "GetTermEvaluator095 field types[0] is correct ");
        assertEquals(Constants.MERGE_ALL, (int) field2, "GetTermEvaluator095 field merge is correct ");
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

        assertTrue(t instanceof PCCountEqTypeTermEvaluator, () -> "GetTermEvaluator096 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator096");

        assertEquals(term, field0, () -> "GetTermEvaluator096 stored term is correct " + term);
        assertEquals("WEAPON", field1[0], "GetTermEvaluator096 field types[0] is correct ");
        assertEquals(Constants.MERGE_ALL, (int) field2, "GetTermEvaluator096 field merge is correct ");
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

        assertTrue(t instanceof PCCountEqTypeTermEvaluator, () -> "GetTermEvaluator097 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator097");

        assertEquals(term, field0, () -> "GetTermEvaluator097 stored term is correct " + term);
        assertEquals("ACITEM", field1[0], "GetTermEvaluator097 field types[0] is correct ");
        assertEquals(Constants.MERGE_ALL, (int) field2, "GetTermEvaluator097 field merge is correct ");
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

        assertTrue(t instanceof PCCountEqTypeTermEvaluator, () -> "GetTermEvaluator098 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator098");

        assertEquals(term, field0, () -> "GetTermEvaluator098 stored term is correct " + term);
        assertEquals("ARMOR", field1[0], "GetTermEvaluator098 field types[0] is correct ");
        assertEquals("IS", field1[1], "GetTermEvaluator098 field types[1] is correct ");
        assertEquals("FOO", field1[2], "GetTermEvaluator098 field types[2] is correct ");
        assertEquals(Constants.MERGE_NONE, (int) field2, "GetTermEvaluator098 field merge is correct ");
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

        assertTrue(t instanceof PCCountEqTypeTermEvaluator, () -> "GetTermEvaluator099 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator099");

        assertEquals(term, field0, () -> "GetTermEvaluator099 stored term is correct " + term);
        assertEquals("QUX", field1[0], "GetTermEvaluator099 field types[0] is correct ");
        assertEquals("NOT", field1[1], "GetTermEvaluator099 field types[1] is correct ");
        assertEquals("BAR", field1[2], "GetTermEvaluator099 field types[2] is correct ");
        assertEquals(Constants.MERGE_LOCATION, (int) field2, "GetTermEvaluator099 field merge is correct ");
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

        assertTrue(t instanceof PCCountEqTypeTermEvaluator, () -> "GetTermEvaluator100 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator100");

        assertEquals(term, field0, () -> "GetTermEvaluator100 stored term is correct " + term);
        assertEquals("QUUX", field1[0], "GetTermEvaluator100 field types[0] is correct ");
        assertEquals("ADD", field1[1], "GetTermEvaluator100 field types[1] is correct ");
        assertEquals("BAZ", field1[2], "GetTermEvaluator100 field types[2] is correct ");
        assertEquals(Constants.MERGE_ALL, (int) field2, "GetTermEvaluator100 field merge is correct ");
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

        assertTrue(t instanceof PCCountEqTypeTermEvaluator, () -> "GetTermEvaluator101 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator101");

        assertEquals(term, field0, () -> "GetTermEvaluator101 stored term is correct " + term);
        assertEquals("WEAPON", field1[0], "GetTermEvaluator101 field types[0] is correct ");
        assertEquals("IS", field1[1], "GetTermEvaluator101 field types[1] is correct ");
        assertEquals("FOO", field1[2], "GetTermEvaluator101 field types[2] is correct ");
        assertEquals(Constants.MERGE_NONE, (int) field2, "GetTermEvaluator101 field merge is correct ");
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

        assertTrue(t instanceof PCCountEqTypeTermEvaluator, () -> "GetTermEvaluator102 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator102");

        assertEquals(term, field0, () -> "GetTermEvaluator102 stored term is correct " + term);
        assertEquals("WEAPON", field1[0], "GetTermEvaluator102 field types[0] is correct ");
        assertEquals("IS", field1[1], "GetTermEvaluator102 field types[1] is correct ");
        assertEquals("FOO", field1[2], "GetTermEvaluator102 field types[2] is correct ");
        assertEquals("EQUIPPED", field1[3], "GetTermEvaluator102 field types[3] is correct ");
        assertEquals("ADD", field1[4], "GetTermEvaluator102 field types[4] is correct ");
        assertEquals("BAR", field1[5], "GetTermEvaluator102 field types[5] is correct ");
        assertEquals(Constants.MERGE_ALL, (int) field2, "GetTermEvaluator102 field merge is correct ");
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

        assertTrue(t instanceof PCCountEqTypeTermEvaluator, () -> "GetTermEvaluator103 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator103");

        assertEquals(term, field0, () -> "GetTermEvaluator103 stored term is correct " + term);
        assertEquals("FOO", field1[0], "GetTermEvaluator103 field types[0] is correct ");
        assertEquals(Constants.MERGE_ALL, (int) field2, "GetTermEvaluator103 field merge is correct ");
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

        assertNull(t, "GetTermEvaluator104 evaluator is null");
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

        assertNull(t, "GetTermEvaluator105 evaluator is null");
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

        assertNull(t, "GetTermEvaluator106 evaluator is null");
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

        assertTrue(t instanceof PCCountEquipmentTermEvaluator,
                () -> "GetTermEvaluator107 evaluator correct for " + term
        );

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator107");

        assertEquals(term, field0, () -> "GetTermEvaluator107 stored term is correct " + term);
        assertEquals("", field1[0], "GetTermEvaluator107 field types[0] is correct ");
        assertEquals(Constants.MERGE_NONE, (int) field2, "GetTermEvaluator107 field merge is correct ");
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

        assertTrue(t instanceof PCCountEquipmentTermEvaluator,
                () -> "GetTermEvaluator108 evaluator correct for " + term
        );

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator108");

        assertEquals(term, field0, () -> "GetTermEvaluator108 stored term is correct " + term);
        assertEquals("", field1[0], "GetTermEvaluator108 field types[0] is correct ");
        assertEquals(Constants.MERGE_LOCATION, (int) field2, "GetTermEvaluator108 field merge is correct ");
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

        assertTrue(t instanceof PCCountEquipmentTermEvaluator,
                () -> "GetTermEvaluator109 evaluator correct for " + term
        );

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator109");

        assertEquals(term, field0, () -> "GetTermEvaluator109 stored term is correct " + term);
        assertEquals("", field1[0], "GetTermEvaluator109 field types[0] is correct ");
        assertEquals(Constants.MERGE_ALL, (int) field2, "GetTermEvaluator109 field merge is correct ");
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

        assertNull(t, "GetTermEvaluator110 evaluator is null");
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

        assertNull(t, "GetTermEvaluator111 evaluator is null");
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

        assertNull(t, "GetTermEvaluator112 evaluator is null");
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

        assertNull(t, "GetTermEvaluator113 evaluator is null");
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

        assertNull(t, "GetTermEvaluator114 evaluator is null");
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

        assertTrue(t instanceof PCCountEquipmentTermEvaluator,
                () -> "GetTermEvaluator115 evaluator correct for " + term
        );

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator115");

        assertEquals(term, field0, () -> "GetTermEvaluator115 stored term is correct " + term);
        assertEquals("IS", field1[0], "GetTermEvaluator115 field types[0] is correct ");
        assertEquals("FOO", field1[1], "GetTermEvaluator115 field types[1] is correct ");
        assertEquals(Constants.MERGE_NONE, (int) field2, "GetTermEvaluator115 field merge is correct ");
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

        assertTrue(t instanceof PCCountEquipmentTermEvaluator,
                () -> "GetTermEvaluator116 evaluator correct for " + term
        );

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator116");

        assertEquals(term, field0, () -> "GetTermEvaluator116 stored term is correct " + term);
        assertEquals("NOT", field1[0], "GetTermEvaluator116 field types[0] is correct ");
        assertEquals("BAR", field1[1], "GetTermEvaluator116 field types[1] is correct ");
        assertEquals(
                Constants.MERGE_LOCATION,
                (int) field2,
                "GetTermEvaluator116 field merge is correct "
        );
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

        assertTrue(t instanceof PCCountEquipmentTermEvaluator,
                () -> "GetTermEvaluator117 evaluator correct for " + term
        );

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator117");

        assertEquals(term, field0, () -> "GetTermEvaluator117 stored term is correct " + term);
        assertEquals("ADD", field1[0], "GetTermEvaluator117 field types[0] is correct ");
        assertEquals("BAZ", field1[1], "GetTermEvaluator117 field types[1] is correct ");
        assertEquals(
                Constants.MERGE_ALL,
                (int) field2,
                "GetTermEvaluator117 field merge is correct "
        );
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

        assertNull(t, "GetTermEvaluator118 evaluator is null");
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

        assertNull(t, "GetTermEvaluator120 evaluator is null");
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

        assertNull(t, "GetTermEvaluator121 evaluator is null");
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

        assertNull(t, "GetTermEvaluator122 evaluator is null");
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
                t instanceof PCCountAbilitiesTypeNatureAutoTermEvaluator,
                () -> "GetTermEvaluator123 evaluator correct for " + term
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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator123");

        assertEquals(term, field0, () -> "GetTermEvaluator123 stored term is correct " + term);
        assertEquals("", field1[0], "GetTermEvaluator123 field types[0] is correct ");
        assertTrue(field2, "GetTermEvaluator123 field hidden is correct ");
        assertFalse(field3, "GetTermEvaluator123 field visible is correct ");
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
                t instanceof PCCountAbilitiesTypeNatureAutoTermEvaluator,
                () -> "GetTermEvaluator124 evaluator correct for " + term
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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator124");

        assertEquals(term, field0, () -> "GetTermEvaluator124 stored term is correct " + term);
        assertEquals("", field1[0], "GetTermEvaluator124 field types[0] is correct ");
        assertFalse(field2, "GetTermEvaluator124 field hidden is correct ");
        assertTrue(field3, "GetTermEvaluator124 field visible is correct ");
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

        assertTrue(t instanceof PCCountAbilityNameTermEvaluator,
                () -> "GetTermEvaluator125 evaluator correct for " + term
        );

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator125");

        assertEquals(term, field0, () -> "GetTermEvaluator125 stored term is correct " + term);
        assertEquals("Jack of all trades", field1, "GetTermEvaluator125 field key is correct ");
        assertFalse(field2, "GetTermEvaluator125 field hidden is correct ");
        assertTrue(field3, "GetTermEvaluator125 field visible is correct ");
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

        assertTrue(t instanceof PCCountAbilityNameTermEvaluator,
                () -> "GetTermEvaluator126 evaluator correct for " + term
        );

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator126");

        assertEquals(term, field0, () -> "GetTermEvaluator126 stored term is correct " + term);
        assertEquals("Weapon Focus (Dagger)", field1, "GetTermEvaluator126 field key is correct ");
        assertFalse(field2, "GetTermEvaluator126 field hidden is correct ");
        assertTrue(field3, "GetTermEvaluator126 field visible is correct ");
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
                t instanceof PCCountAbilitiesTypeNatureAllTermEvaluator,
                () -> "GetTermEvaluator127 evaluator correct for " + term
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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator127");

        assertEquals(term, field0, () -> "GetTermEvaluator127 stored term is correct " + term);
        assertEquals("BAR", field1[0], "GetTermEvaluator127 field types[0] is correct ");
        assertFalse(field2, "GetTermEvaluator127 field hidden is correct ");
        assertTrue(field3, "GetTermEvaluator127 field visible is correct ");
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
                t instanceof PCCountAbilitiesTypeNatureAllTermEvaluator,
                () -> "GetTermEvaluator128 evaluator correct for " + term
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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator128");

        assertEquals(term, field0, () -> "GetTermEvaluator128 stored term is correct " + term);
        assertEquals("BAZ", field1[0], "GetTermEvaluator128 field types[0] is correct ");
        assertFalse(field2, "GetTermEvaluator128 field hidden is correct ");
        assertTrue(field3, "GetTermEvaluator128 field visible is correct ");
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
                t instanceof PCCountAbilitiesTypeNatureVirtualTermEvaluator,
                () -> "GetTermEvaluator129 evaluator correct for " + term
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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator129");

        assertEquals(term, field0, () -> "GetTermEvaluator129 stored term is correct " + term);
        assertEquals("", field1[0], "GetTermEvaluator129 field types[0] is correct ");
        assertTrue(field2, "GetTermEvaluator129 field hidden is correct ");
        assertFalse(field3, "GetTermEvaluator129 field visible is correct ");
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
                t instanceof PCCountAbilitiesTypeNatureVirtualTermEvaluator,
                () -> "GetTermEvaluator130 evaluator correct for " + term
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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator130");

        assertEquals(term, field0, () -> "GetTermEvaluator130 stored term is correct " + term);
        assertEquals("", field1[0], "GetTermEvaluator130 field types[0] is correct ");
        assertTrue(field2, "GetTermEvaluator130 field hidden is correct ");
        assertTrue(field3, "GetTermEvaluator130 field visible is correct ");
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
                t instanceof PCCountAbilitiesTypeNatureAllTermEvaluator,
                () -> "GetTermEvaluator131 evaluator correct for " + term
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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator131");

        assertEquals(term, field0, () -> "GetTermEvaluator131 stored term is correct " + term);
        assertEquals("FOO", field1[0], "GetTermEvaluator131 field types[0] is correct ");
        assertEquals("BAR", field1[1], "GetTermEvaluator131 field types[1] is correct ");
        assertEquals("BAZ", field1[2], "GetTermEvaluator131 field types[2] is correct ");
        assertEquals("QUX", field1[3], "GetTermEvaluator131 field types[3] is correct ");
        assertFalse(field2, "GetTermEvaluator131 field hidden is correct ");
        assertTrue(field3, "GetTermEvaluator131 field visible is correct ");
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

        assertTrue(t instanceof PCCountFollowerTypeTermEvaluator,
                () -> "GetTermEvaluator132 evaluator correct for " + term
        );

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator132");

        assertEquals(term, field0, () -> "GetTermEvaluator132 stored term is correct " + term);
        assertEquals("MOO", field1, "GetTermEvaluator132 field type is correct ");
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
                t instanceof PCCountFollowerTypeTransitiveTermEvaluator,
                () -> "GetTermEvaluator133 evaluator correct for " + term
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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator133");

        assertEquals(term, field0, () -> "GetTermEvaluator133 stored term is correct " + term);
        assertEquals(0, (int) field1, "GetTermEvaluator133 field index is correct ");
        assertEquals("COUNT[EQTYPE]", field2, "GetTermEvaluator133 field newCount is correct ");
        assertEquals("MOO", field3, "GetTermEvaluator133 field type is correct ");
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

        assertNull(t, "GetTermEvaluator134 evaluator is null");
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

        assertNull(t, "GetTermEvaluator135 evaluator is null");
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

        assertTrue(t instanceof PCSkillTypeTermEvaluator, () -> "GetTermEvaluator136 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator136");

        assertEquals(term, field0, () -> "GetTermEvaluator136 stored term is correct " + term);
        assertEquals("KNOWLEDGE", field1, "GetTermEvaluator136 field type is correct ");
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

        assertTrue(t instanceof PCSkillTypeTermEvaluator, () -> "GetTermEvaluator137 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator137");

        assertEquals(term, field0, () -> "GetTermEvaluator137 stored term is correct " + term);
        assertEquals("PERFORM", field1, "GetTermEvaluator137 field type is correct ");
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

        assertTrue(t instanceof PCCountSpellbookTermEvaluator,
                () -> "GetTermEvaluator138 evaluator correct for " + term
        );

        Class<?> uClass = PCCountSpellbookTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator138");

        assertEquals(term, field0, () -> "GetTermEvaluator138 stored term is correct " + term);
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

        assertNull(t, "GetTermEvaluator139 evaluator is null");
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

        assertTrue(t instanceof PCCountSpellsInbookTermEvaluator,
                () -> "GetTermEvaluator140 evaluator correct for " + term
        );

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator140");

        assertEquals(term, field0, () -> "GetTermEvaluator140 stored term is correct " + term);
        assertEquals("1.0", field1, "GetTermEvaluator140 field book is correct ");
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

        assertNull(t, "GetTermEvaluator141 evaluator is null");
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

        assertTrue(t instanceof PCCountSpellsKnownTermEvaluator,
                () -> "GetTermEvaluator142 evaluator correct for " + term
        );

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator142");

        assertEquals(term, field0, () -> "GetTermEvaluator142 stored term is correct " + term);
        assertEquals(0, field1[0], "GetTermEvaluator142 field nums[0] is correct ");
        assertEquals(0, field1[1], "GetTermEvaluator142 field nums[1] is correct ");
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

        assertNull(t, "GetTermEvaluator143 evaluator is null");
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

        assertNull(t, "GetTermEvaluator144 evaluator is null");
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

        assertNull(t, "GetTermEvaluator145 evaluator is null");
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

        assertTrue(t instanceof PCCountSpellTimesTermEvaluator,
                () -> "GetTermEvaluator146 evaluator correct for " + term
        );

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator146");

        assertEquals(term, field0, () -> "GetTermEvaluator146 stored term is correct " + term);
        assertEquals(1, (int) field1, "GetTermEvaluator146 field classNum is correct ");
        assertEquals(2, (int) field2, "GetTermEvaluator146 field bookNum is correct ");
        assertEquals(3, (int) field3, "GetTermEvaluator146 field spellLevel is correct ");
        assertEquals(4, (int) field4, "GetTermEvaluator146 field spellNumber is correct ");
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
                t instanceof PCCountSpellsLevelsInBookTermEvaluator,
                () -> "GetTermEvaluator147 evaluator correct for " + term
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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator147");

        assertEquals(term, field0, () -> "GetTermEvaluator147 stored term is correct " + term);
        assertEquals(1, (int) field1, "GetTermEvaluator147 field classNum is correct ");
        assertEquals(2, (int) field2, "GetTermEvaluator147 field sbookNum is correct ");
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

        assertNull(t, "GetTermEvaluator148 evaluator is null");
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

        assertNull(t, "GetTermEvaluator149 evaluator is null");
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

        assertNull(t, "GetTermEvaluator150 evaluator is null");
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

        assertNull(t, "GetTermEvaluator151 evaluator is null");
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

        assertTrue(t instanceof PCEqTypeTermEvaluator, () -> "GetTermEvaluator152 evaluator correct for " + term);

        Class<?> uClass = PCEqTypeTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator152");

        assertEquals(term, field0, () -> "GetTermEvaluator152 stored term is correct " + term);
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

        assertTrue(t instanceof PCHasDeityTermEvaluator, () -> "GetTermEvaluator153 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator153");

        assertEquals(term, field0, () -> "GetTermEvaluator153 stored term is correct " + term);
        assertEquals("Bane", field1, "GetTermEvaluator153 field deity is correct ");
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

        assertTrue(t instanceof PCHasFeatTermEvaluator, () -> "GetTermEvaluator154 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator154");

        assertEquals(term, field0, () -> "GetTermEvaluator154 stored term is correct " + term);
        assertEquals("Endurance", field1, "GetTermEvaluator154 field feat is correct ");
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

        assertTrue(t instanceof PCModEquipTermEvaluator, () -> "GetTermEvaluator155 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator155");

        assertEquals(term, field0, () -> "GetTermEvaluator155 stored term is correct " + term);
        assertEquals("SPELLFAILURE", field1, "GetTermEvaluator155 field modEq is correct ");
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

        assertTrue(t instanceof PCMovementTermEvaluator, () -> "GetTermEvaluator156 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator156");

        assertEquals(term, field0, () -> "GetTermEvaluator156 stored term is correct " + term);
        assertEquals(MovementType.getConstant("Walk"), field1, "GetTermEvaluator156 field movement is correct ");
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

        assertNull(t, "GetTermEvaluator157 evaluator is null");
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

        assertTrue(t instanceof PCSizeTermEvaluator, () -> "GetTermEvaluator158 evaluator correct for " + term);

        Class<?> uClass = PCSizeTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator158");

        assertEquals(term, field0, () -> "GetTermEvaluator158 stored term is correct " + term);
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

        assertTrue(t instanceof PCSkillRankTermEvaluator, () -> "GetTermEvaluator159 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator159");

        assertEquals(term, field0, () -> "GetTermEvaluator159 stored term is correct " + term);
        assertEquals("Tumble", field1, "GetTermEvaluator159 field rank is correct ");
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

        assertTrue(t instanceof PCSkillRankTermEvaluator, () -> "GetTermEvaluator160 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator160");

        assertEquals(term, field0, () -> "GetTermEvaluator160 stored term is correct " + term);
        assertEquals("Perform (Dance)", field1, "GetTermEvaluator160 field rank is correct ");
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

        assertTrue(t instanceof PCSkillRankTermEvaluator, () -> "GetTermEvaluator161 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator161");

        assertEquals(term, field0, () -> "GetTermEvaluator161 stored term is correct " + term);
        assertEquals("Perform (Sing)", field1, "GetTermEvaluator161 field rank is correct ");
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

        assertTrue(t instanceof PCSkillTotalTermEvaluator, () -> "GetTermEvaluator162 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator162");

        assertEquals(term, field0, () -> "GetTermEvaluator162 stored term is correct " + term);
        assertEquals("Tumble", field1, "GetTermEvaluator162 field total is correct ");
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

        assertTrue(t instanceof PCSkillTotalTermEvaluator, () -> "GetTermEvaluator163 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator163");

        assertEquals(term, field0, () -> "GetTermEvaluator163 stored term is correct " + term);
        assertEquals("Perform (Dance)", field1, "GetTermEvaluator163 field total is correct ");
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

        assertTrue(t instanceof PCSkillTotalTermEvaluator, () -> "GetTermEvaluator164 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator164");

        assertEquals(term, field0, () -> "GetTermEvaluator164 stored term is correct " + term);
        assertEquals("Perform (Sing)", field1, "GetTermEvaluator164 field total is correct ");
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

        assertTrue(t instanceof PCVarDefinedTermEvaluator, () -> "GetTermEvaluator165 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator165");

        assertEquals(term, field0, () -> "GetTermEvaluator165 stored term is correct " + term);
        assertEquals("MilkyBarsEaten", field1, "GetTermEvaluator165 field var is correct ");
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

        assertTrue(t instanceof PCCarriedWeightTermEvaluator, () -> "GetTermEvaluator166 evaluator correct for " + term);

        Class<?> uClass = PCCarriedWeightTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator166");

        assertEquals(term, field0, () -> "GetTermEvaluator166 stored term is correct " + term);
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

        assertTrue(t instanceof PCCarriedWeightTermEvaluator, () -> "GetTermEvaluator167 evaluator correct for " + term);

        Class<?> uClass = PCCarriedWeightTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator167");

        assertEquals(term, field0, () -> "GetTermEvaluator167 stored term is correct " + term);
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

        assertTrue(t instanceof PCWeightTermEvaluator, () -> "GetTermEvaluator168 evaluator correct for " + term);

        Class<?> uClass = PCWeightTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator168");

        assertEquals(term, field0, () -> "GetTermEvaluator168 stored term is correct " + term);
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

        assertTrue(t instanceof PCTotalWeightTermEvaluator, () -> "GetTermEvaluator169 evaluator correct for " + term);

        Class<?> uClass = PCTotalWeightTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator169");

        assertEquals(term, field0, () -> "GetTermEvaluator169 stored term is correct " + term);
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

        assertNull(t, "GetTermEvaluator170 evaluator is null");
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

        assertThat(
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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator171");

        assertEquals(term, field0, () -> "GetTermEvaluator171 stored term is correct " + term);
        assertEquals("STR", field1, "GetTermEvaluator171 field statAbbrev is correct ");
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

        assertTrue(t instanceof PCStatModTermEvaluator, () -> "GetTermEvaluator172 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator172");

        assertEquals(term, field0, () -> "GetTermEvaluator172 stored term is correct " + term);
        assertEquals("INT", field1, "GetTermEvaluator172 field statAbbrev is correct ");
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

        assertTrue(t instanceof PCStatModTermEvaluator, () -> "GetTermEvaluator173 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator173");

        assertEquals(term, field0, () -> "GetTermEvaluator173 stored term is correct " + term);
        assertEquals("DEX", field1, "GetTermEvaluator173 field statAbbrev is correct ");
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

        assertTrue(t instanceof PCStatModTermEvaluator, () -> "GetTermEvaluator174 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator174");

        assertEquals(term, field0, () -> "GetTermEvaluator174 stored term is correct " + term);
        assertEquals("WIS", field1, "GetTermEvaluator174 field statAbbrev is correct ");
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

        assertTrue(t instanceof PCStatModTermEvaluator, () -> "GetTermEvaluator175 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator175");

        assertEquals(term, field0, () -> "GetTermEvaluator175 stored term is correct " + term);
        assertEquals("CON", field1, "GetTermEvaluator175 field statAbbrev is correct ");
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

        assertTrue(t instanceof PCStatModTermEvaluator, () -> "GetTermEvaluator176 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator176");

        assertEquals(term, field0, () -> "GetTermEvaluator176 stored term is correct " + term);
        assertEquals("CHA", field1, "GetTermEvaluator176 field statAbbrev is correct ");
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

        assertTrue(t instanceof PCStatTotalTermEvaluator, () -> "GetTermEvaluator177 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator177");

        assertEquals(term, field0, () -> "GetTermEvaluator177 stored term is correct " + term);
        assertEquals("STR", field1, "GetTermEvaluator177 field statAbbrev is correct ");
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

        assertTrue(t instanceof PCStatTotalTermEvaluator, () -> "GetTermEvaluator178 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator178");

        assertEquals(term, field0, () -> "GetTermEvaluator178 stored term is correct " + term);
        assertEquals("INT", field1, "GetTermEvaluator178 field statAbbrev is correct ");
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

        assertTrue(t instanceof PCStatTotalTermEvaluator, () -> "GetTermEvaluator179 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator179");

        assertEquals(term, field0, () -> "GetTermEvaluator179 stored term is correct " + term);
        assertEquals("DEX", field1, "GetTermEvaluator179 field statAbbrev is correct ");
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

        assertTrue(t instanceof PCStatTotalTermEvaluator, () -> "GetTermEvaluator180 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator180");

        assertEquals(term, field0, () -> "GetTermEvaluator180 stored term is correct " + term);
        assertEquals("WIS", field1, "GetTermEvaluator180 field statAbbrev is correct ");
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

        assertTrue(t instanceof PCStatTotalTermEvaluator, () -> "GetTermEvaluator181 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator181");

        assertEquals(term, field0, () -> "GetTermEvaluator181 stored term is correct " + term);
        assertEquals("CON", field1, "GetTermEvaluator181 field statAbbrev is correct ");
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

        assertTrue(t instanceof PCStatTotalTermEvaluator, () -> "GetTermEvaluator182 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator182");

        assertEquals(term, field0, () -> "GetTermEvaluator182 stored term is correct " + term);
        assertEquals("CHA", field1, "GetTermEvaluator182 field statAbbrev is correct ");
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

        assertTrue(t instanceof PCStatBaseTermEvaluator, () -> "GetTermEvaluator183 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator183");

        assertEquals(term, field0, () -> "GetTermEvaluator183 stored term is correct " + term);
        assertEquals("STR", field1, "GetTermEvaluator183 field statAbbrev is correct ");
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

        assertTrue(t instanceof PCStatBaseTermEvaluator, () -> "GetTermEvaluator184 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator184");

        assertEquals(term, field0, () -> "GetTermEvaluator184 stored term is correct " + term);
        assertEquals("INT", field1, "GetTermEvaluator184 field statAbbrev is correct ");
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

        assertTrue(t instanceof PCStatBaseTermEvaluator, () -> "GetTermEvaluator185 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator185");

        assertEquals(term, field0, () -> "GetTermEvaluator185 stored term is correct " + term);
        assertEquals("DEX", field1, "GetTermEvaluator185 field statAbbrev is correct ");
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

        assertTrue(t instanceof PCStatBaseTermEvaluator, () -> "GetTermEvaluator186 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator186");

        assertEquals(term, field0, () -> "GetTermEvaluator186 stored term is correct " + term);
        assertEquals("WIS", field1, "GetTermEvaluator186 field statAbbrev is correct ");
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

        assertTrue(t instanceof PCStatBaseTermEvaluator, () -> "GetTermEvaluator187 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator187");

        assertEquals(term, field0, () -> "GetTermEvaluator187 stored term is correct " + term);
        assertEquals("CON", field1, "GetTermEvaluator187 field statAbbrev is correct ");
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

        assertTrue(t instanceof PCStatBaseTermEvaluator, () -> "GetTermEvaluator188 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator188");

        assertEquals(term, field0, () -> "GetTermEvaluator188 stored term is correct " + term);
        assertEquals("CHA", field1, "GetTermEvaluator188 field statAbbrev is correct ");
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

        assertTrue(t instanceof PCStatBaseTermEvaluator, () -> "GetTermEvaluator189 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator189");

        assertEquals(term, field0, () -> "GetTermEvaluator189 stored term is correct " + term);
        assertEquals("STR", field1, "GetTermEvaluator189 field statAbbrev is correct ");
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

        assertTrue(t instanceof PCStatBaseTermEvaluator, () -> "GetTermEvaluator190 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator190");

        assertEquals(term, field0, () -> "GetTermEvaluator190 stored term is correct " + term);
        assertEquals("INT", field1, "GetTermEvaluator190 field statAbbrev is correct ");
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

        assertTrue(t instanceof PCStatBaseTermEvaluator, () -> "GetTermEvaluator191 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator191");

        assertEquals(term, field0, () -> "GetTermEvaluator191 stored term is correct " + term);
        assertEquals("DEX", field1, "GetTermEvaluator191 field statAbbrev is correct ");
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

        assertTrue(t instanceof PCStatBaseTermEvaluator, () -> "GetTermEvaluator192 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator192");

        assertEquals(term, field0, () -> "GetTermEvaluator192 stored term is correct " + term);
        assertEquals("WIS", field1, "GetTermEvaluator192 field statAbbrev is correct ");
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

        assertTrue(t instanceof PCStatBaseTermEvaluator, () -> "GetTermEvaluator193 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator193");

        assertEquals(term, field0, () -> "GetTermEvaluator193 stored term is correct " + term);
        assertEquals("CON", field1, "GetTermEvaluator193 field statAbbrev is correct ");
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

        assertTrue(t instanceof PCStatBaseTermEvaluator, () -> "GetTermEvaluator194 evaluator correct for " + term);

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
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator194");

        assertEquals(term, field0, () -> "GetTermEvaluator194 stored term is correct " + term);
        assertEquals("CHA", field1, "GetTermEvaluator194 field statAbbrev is correct ");
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

        assertNull(t, "GetTermEvaluator195 evaluator is null");
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

        assertNull(t, "GetTermEvaluator196 evaluator is null");
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

        assertNull(t, "GetTermEvaluator197 evaluator is null");
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

        assertNull(t, "GetTermEvaluator198 evaluator is null");
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

        assertNull(t, "GetTermEvaluator199 evaluator is null");
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

        assertNull(t, "GetTermEvaluator200 evaluator is null");
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

        assertTrue(t instanceof EQBaseCostTermEvaluator, () -> "GetTermEvaluator201 evaluator correct for " + term);

        Class<?> uClass = EQBaseCostTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator201");

        assertEquals(term, field0, () -> "GetTermEvaluator201 stored term is correct " + term);
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

        assertTrue(t instanceof EQCritMultTermEvaluator, () -> "GetTermEvaluator202 evaluator correct for " + term);

        Class<?> uClass = EQCritMultTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator202");

        assertEquals(term, field0, () -> "GetTermEvaluator202 stored term is correct " + term);
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

        assertTrue(t instanceof EQDamageDiceTermEvaluator, () -> "GetTermEvaluator203 evaluator correct for " + term);

        Class<?> uClass = EQDamageDiceTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator203");

        assertEquals(term, field0, () -> "GetTermEvaluator203 stored term is correct " + term);
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

        assertTrue(t instanceof EQDamageDieTermEvaluator, () -> "GetTermEvaluator204 evaluator correct for " + term);

        Class<?> uClass = EQDamageDieTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator204");

        assertEquals(term, field0, () -> "GetTermEvaluator204 stored term is correct " + term);
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

        assertTrue(t instanceof EQACCheckTermEvaluator, () -> "GetTermEvaluator205 evaluator correct for " + term);

        Class<?> uClass = EQACCheckTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator205");

        assertEquals(term, field0, () -> "GetTermEvaluator205 stored term is correct " + term);
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

        assertTrue(t instanceof EQHandsTermEvaluator, () -> "GetTermEvaluator206 evaluator correct for " + term);

        Class<?> uClass = EQHandsTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator206");

        assertEquals(term, field0, () -> "GetTermEvaluator206 stored term is correct " + term);
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

        assertTrue(t instanceof EQSpellFailureTermEvaluator, () -> "GetTermEvaluator207 evaluator correct for " + term);

        Class<?> uClass = EQSpellFailureTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator207");

        assertEquals(term, field0, () -> "GetTermEvaluator207 stored term is correct " + term);
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

        assertTrue(t instanceof EQEquipSizeTermEvaluator, () -> "GetTermEvaluator208 evaluator correct for " + term);

        Class<?> uClass = EQEquipSizeTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator208");

        assertEquals(term, field0, () -> "GetTermEvaluator208 stored term is correct " + term);
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

        assertTrue(t instanceof EQSizeTermEvaluator, () -> "GetTermEvaluator209 evaluator correct for " + term);

        Class<?> uClass = EQSizeTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator209");

        assertEquals(term, field0, () -> "GetTermEvaluator209 stored term is correct " + term);
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
                t instanceof EQAltPlusTotalTermEvaluator,
                () -> "EQAltPlusTotalTermEvaluator evaluator correct for " + term
        );

        Class<?> uClass = EQAltPlusTotalTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in testGetTermEvaluatorAltPlusTotal");

        assertEquals(term, field0, () -> "testGetTermEvaluatorAltPlusTotal stored term is correct " + term);
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

        assertTrue(t instanceof EQPlusTotalTermEvaluator,
                () -> "EQPlusTotalTermEvaluator evaluator correct for " + term
        );

        Class<?> uClass = EQPlusTotalTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in testGetTermEvaluatorPlusTotal");

        assertEquals(term, field0, () -> "testGetTermEvaluatorPlusTotal stored term is correct " + term);
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

        assertTrue(t instanceof EQRaceReachTermEvaluator, () -> "GetTermEvaluator210 evaluator correct for " + term);

        Class<?> uClass = EQRaceReachTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator210");

        assertEquals(term, field0, () -> "GetTermEvaluator210 stored term is correct " + term);
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

        assertTrue(t instanceof EQRangeTermEvaluator, () -> "GetTermEvaluator211 evaluator correct for " + term);

        Class<?> uClass = EQRangeTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator211");

        assertEquals(term, field0, () -> "GetTermEvaluator211 stored term is correct " + term);
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

        assertTrue(t instanceof EQReachTermEvaluator, () -> "GetTermEvaluator212 evaluator correct for " + term);

        Class<?> uClass = EQReachTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator212");

        assertEquals(term, field0, () -> "GetTermEvaluator212 stored term is correct " + term);
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

        assertTrue(t instanceof EQReachMultTermEvaluator, () -> "GetTermEvaluator213 evaluator correct for " + term);

        Class<?> uClass = EQReachMultTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator213");

        assertEquals(term, field0, () -> "GetTermEvaluator213 stored term is correct " + term);
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

        assertTrue(t instanceof EQSizeTermEvaluator, () -> "GetTermEvaluator214 evaluator correct for " + term);

        Class<?> uClass = EQSizeTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator214");

        assertEquals(term, field0, () -> "GetTermEvaluator214 stored term is correct " + term);
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

        assertTrue(t instanceof EQWeightTermEvaluator, () -> "GetTermEvaluator215 evaluator correct for " + term);

        Class<?> uClass = EQWeightTermEvaluator.class;

        Field pF0 = (Field) TestHelper.findField(uClass, "originalText");

        String field0 = "";
        boolean ok;
        try
        {
            ok = true;
            field0 = (String) pF0.get(t);
        } catch (ClassCastException | IllegalAccessException e)
        {
            ok = false;
        }

        assertTrue(ok, "No illegal access in getTermEvaluator215");

        assertEquals(term, field0, () -> "GetTermEvaluator215 stored term is correct " + term);
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

        assertTrue(t1 instanceof PCCLBeforeLevelTermEvaluator,
                () -> "GetTermEvaluator215 t1 evaluator correct for " + term1
        );
        assertTrue(t2 instanceof PCCLBeforeLevelTermEvaluator,
                () -> "GetTermEvaluator215 t2 evaluator correct for " + term1
        );
        assertTrue(t3 instanceof PCCLBeforeLevelTermEvaluator,
                () -> "GetTermEvaluator215 t3 evaluator correct for " + term1
        );

        assertNotEquals(t1, t2, "t1 and t2 are different objects");
        assertEquals(t1, t3, "t1 and t3 are the Same object");
        assertNotEquals(t2, t3, "t2 and t3 are different objects");

        String term2 = "CL;BEFORELEVEL=14";

        TermEvaluator t4 = EvaluatorFactory.PC.getTermEvaluator(term2, "CLASS:Druid");
        TermEvaluator t5 = EvaluatorFactory.PC.getTermEvaluator(term2, "CLASS:Ranger");
        TermEvaluator t6 = EvaluatorFactory.PC.getTermEvaluator(term2, "CLASS:Druid");

        assertTrue(t4 instanceof PCCLBeforeLevelTermEvaluator,
                () -> "GetTermEvaluator215 t4 evaluator correct for " + term2
        );
        assertTrue(t6 instanceof PCCLBeforeLevelTermEvaluator,
                () -> "GetTermEvaluator215 t5 evaluator correct for " + term2
        );
        assertTrue(t5 instanceof PCCLBeforeLevelTermEvaluator,
                () -> "GetTermEvaluator215 t6 evaluator correct for " + term2
        );

        assertEquals(t4, t6, "t4 and t6 are the Same object");
        assertNotEquals(t4, t5, "t4 and t5 are different objects");
        assertNotEquals(t6, t5, "t6 and t5 are different objects");

        assertNotEquals(t1, t4, "t1 and t4 are diffferent objects");
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
        SettingsHandler.getGameAsProperty().get().clearLoadContext();

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
