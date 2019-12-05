/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.pcclass.level;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.base.formula.DividingFormula;
import pcgen.cdom.content.HitDie;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.processor.HitDieFormula;
import pcgen.cdom.processor.HitDieLock;
import pcgen.cdom.processor.HitDieStep;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;


public class HitDieTokenTest extends AbstractPCClassLevelTokenTestCase
{
    private static final CDOMPrimaryToken<PCClassLevel> token = new HitdieLst();

    @Override
    public CDOMPrimaryToken<PCClassLevel> getToken()
    {
        return token;
    }

    @Test
    public void testInvalidInputTooManyLimits()
    {
        assertFalse(parse("15|CLASS=Fighter|CLASS.TYPE=Base", 2));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputNotALimit()
    {
        assertFalse(parse("15|PRECLASS:1,Fighter", 2));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputEmptyLimit()
    {
        assertFalse(parse("15|CLASS=", 2));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputEmptyTypeLimit()
    {
        assertFalse(parse("15|CLASS.TYPE=", 2));
        assertNoSideEffects();
    }

    @Test
    public void testValidInputDivideNegative()
    {
        assertFalse(parse("%/-2", 2));
        assertNoSideEffects();
    }

    @Test
    public void testValidInputDivideZero()
    {
        assertFalse(parse("%/0", 2));
        assertNoSideEffects();
    }

    @Test
    public void testValidInputDivide()
    {
        assertTrue(parse("%/4", 2));
    }

    @Test
    public void testInvalidInputAddNegative()
    {
        assertFalse(parse("%+-3", 2));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputAddZero()
    {
        assertFalse(parse("%+0", 2));
        assertNoSideEffects();
    }

    @Test
    public void testValidInputAdd()
    {
        assertTrue(parse("%+4", 2));
    }

    @Test
    public void testInvalidInputMultiplyNegative()
    {
        assertFalse(parse("%*-3", 2));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputMultiplyZero()
    {
        assertFalse(parse("%*0", 2));
        assertNoSideEffects();
    }

    @Test
    public void testValidInputMultiply()
    {
        assertTrue(parse("%*4", 2));
    }

    @Test
    public void testInvalidInputSubtractNegative()
    {
        assertFalse(parse("%--3", 2));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputSubtractZero()
    {
        assertFalse(parse("%-0", 2));
        assertNoSideEffects();
    }

    @Test
    public void testValidInputSubtract()
    {
        assertTrue(parse("%-4", 2));
    }

    @Test
    public void testInvalidInputUpNegative()
    {
        assertFalse(parse("%up-3", 2));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputUpZero()
    {
        assertFalse(parse("%up0", 2));
        assertNoSideEffects();
    }

    @Test
    public void testValidInputUp()
    {
        assertTrue(parse("%up4", 2));
    }

    @Test
    public void testInvalidInputUpTooBig()
    {
        assertFalse(parse("%up5", 2));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputUpReallyTooBig()
    {
        assertFalse(parse("%up15", 2));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputHUpNegative()
    {
        assertFalse(parse("%Hup-3", 2));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputHUpZero()
    {
        assertFalse(parse("%Hup0", 2));
        assertNoSideEffects();
    }

    @Test
    public void testValidInputHUp()
    {
        assertTrue(parse("%Hup4", 2));
    }

    @Test
    public void testInvalidInputDownNegative()
    {
        assertFalse(parse("%down-3", 2));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputDownZero()
    {
        assertFalse(parse("%down0", 2));
        assertNoSideEffects();
    }

    @Test
    public void testValidInputDown()
    {
        assertTrue(parse("%down4", 2));
    }

    @Test
    public void testInvalidInputDownTooBig()
    {
        assertFalse(parse("%down5", 3));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputDownReallyTooBig()
    {
        assertFalse(parse("%down15", 3));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputHdownNegative()
    {
        assertFalse(parse("%Hdown-3", 2));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputHdownZero()
    {
        assertFalse(parse("%Hdown0", 2));
        assertNoSideEffects();
    }

    @Test
    public void testValidInputHdown()
    {
        assertTrue(parse("%Hdown4", 2));
    }

    @Test
    public void testInvalidInputNegative()
    {
        assertFalse(parse("-3", 2));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputZero()
    {
        assertFalse(parse("0", 2));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputDecimal()
    {
        assertFalse(parse("3.5", 2));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputMisspell()
    {
        assertFalse(parse("%upn5", 2));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinInteger() throws PersistenceLayerException
    {
        runRoundRobin("2");
    }

    @Test
    public void testRoundRobinAdd() throws PersistenceLayerException
    {
        runRoundRobin("%+2");
    }

    @Test
    public void testRoundRobinSubtract() throws PersistenceLayerException
    {
        runRoundRobin("%-2");
    }

    @Test
    public void testRoundRobinMultiply() throws PersistenceLayerException
    {
        runRoundRobin("%*2");
    }

    @Test
    public void testRoundRobinDivide() throws PersistenceLayerException
    {
        runRoundRobin("%/2");
    }

    @Test
    public void testRoundRobinUp() throws PersistenceLayerException
    {
        runRoundRobin("%up2");
    }

    @Test
    public void testRoundRobinHup() throws PersistenceLayerException
    {
        runRoundRobin("%Hup2");
    }

    @Test
    public void testRoundRobinDown() throws PersistenceLayerException
    {
        runRoundRobin("%down2");
    }

    @Test
    public void testRoundRobinHdown() throws PersistenceLayerException
    {
        runRoundRobin("%Hdown2");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "%down2";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }

    @Override
    protected String getLegalValue()
    {
        return "%Hup2";
    }

    @Test
    public void testUnparseNull()
    {
        primaryProf1.put(ObjectKey.HITDIE, null);
        assertNull(getToken().unparse(primaryContext, primaryProf1));
    }

    @Test
    public void testUnparseLegal()
    {
        primaryProf1.put(ObjectKey.HITDIE, new HitDieLock(new HitDie(1)));
        assertArrayEquals(new String[]{"1"}, getToken().unparse(primaryContext, primaryProf1));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUnparseGenericsFail()
    {
        ObjectKey objectKey = ObjectKey.HITDIE;
        primaryProf1.put(objectKey, new Object());
        assertThrows(ClassCastException.class,
                () -> getToken().unparse(primaryContext, primaryProf1)
        );
    }

    @Test
    public void testUnparseZeroSteps()
    {
        assertThrows(IllegalArgumentException.class,
                () -> primaryProf1.put(ObjectKey.HITDIE,
                        new HitDieStep(0, new HitDie(12))));
        //			assertBadUnparse();
    }

    @Test
    public void testUnparseNegativeLevel()
    {
        try
        {
            primaryProf1.put(ObjectKey.HITDIE, new HitDieLock(new HitDie(-1)));
            assertBadUnparse();
        } catch (IllegalArgumentException e)
        {
            // Good here too :)
        }
    }

    @Test
    public void testUnparseZeroDivide()
    {
        try
        {
            primaryProf1.put(ObjectKey.HITDIE, new HitDieFormula(
                    new DividingFormula(0)));
            assertBadUnparse();
        } catch (IllegalArgumentException e)
        {
            // Good here too :)
        }
    }

    /*
     * TODO Need to find owner for this responsibility
     */
    // @Test
    // public void testUnparseNegativeDivide() throws PersistenceLayerException
    // {
    // try
    // {
    // primaryProf1.put(ObjectKey.HITDIE, new HitDieFormula(new
    // DividingFormula(-3)));
    // assertBadUnparse();
    // }
    // catch (IllegalArgumentException e)
    // {
    // //Good here too :)
    // }
    // }
    //
    // @Test
    // public void testUnparseZeroMult() throws PersistenceLayerException
    // {
    // try
    // {
    // primaryProf1.put(ObjectKey.HITDIE, new HitDieFormula(new
    // MultiplyingFormula(0)));
    // assertBadUnparse();
    // }
    // catch (IllegalArgumentException e)
    // {
    // //Good here too :)
    // }
    // }
    //
    // @Test
    // public void testUnparseNegativeMult() throws PersistenceLayerException
    // {
    // try
    // {
    // primaryProf1.put(ObjectKey.HITDIE, new HitDieFormula(new
    // MultiplyingFormula(-3)));
    // assertBadUnparse();
    // }
    // catch (IllegalArgumentException e)
    // {
    // //Good here too :)
    // }
    // }
    //
    // @Test
    // public void testUnparseZeroAdd() throws PersistenceLayerException
    // {
    // try
    // {
    // primaryProf1.put(ObjectKey.HITDIE, new HitDieFormula(new
    // AddingFormula(0)));
    // assertBadUnparse();
    // }
    // catch (IllegalArgumentException e)
    // {
    // //Good here too :)
    // }
    // }
    //
    // @Test
    // public void testUnparseNegativeAdd() throws PersistenceLayerException
    // {
    // try
    // {
    // primaryProf1.put(ObjectKey.HITDIE, new HitDieFormula(new
    // AddingFormula(-3)));
    // assertBadUnparse();
    // }
    // catch (IllegalArgumentException e)
    // {
    // //Good here too :)
    // }
    // }
    //
    // @Test
    // public void testUnparseZeroSub() throws PersistenceLayerException
    // {
    // try
    // {
    // primaryProf1.put(ObjectKey.HITDIE, new HitDieFormula(new
    // SubtractingFormula(0)));
    // assertBadUnparse();
    // }
    // catch (IllegalArgumentException e)
    // {
    // //Good here too :)
    // }
    // }
    //
    // @Test
    // public void testUnparseNegativeSub() throws PersistenceLayerException
    // {
    // try
    // {
    // primaryProf1.put(ObjectKey.HITDIE, new HitDieFormula(new
    // SubtractingFormula(-3)));
    // assertBadUnparse();
    // }
    // catch (IllegalArgumentException e)
    // {
    // //Good here too :)
    // }
    // }
    //
    // @Test
    // public void testUnparseBigSteps() throws PersistenceLayerException
    // {
    // try
    // {
    // primaryProf1.put(ObjectKey.HITDIE, new HitDieStep(8, new HitDie(12)));
    // assertBadUnparse();
    // }
    // catch (IllegalArgumentException e)
    // {
    // //Good here too :)
    // }
    // }
    //
    // @Test
    // public void testUnparseBigNegativeSteps() throws
    // PersistenceLayerException
    // {
    // try
    // {
    // primaryProf1.put(ObjectKey.HITDIE, new HitDieStep(-8, new HitDie(12)));
    // assertBadUnparse();
    // }
    // catch (IllegalArgumentException e)
    // {
    // //Good here too :)
    // }
    // }
    //
    // @Test
    // public void testUnparseBadBase() throws PersistenceLayerException
    // {
    // try
    // {
    // primaryProf1.put(ObjectKey.HITDIE, new HitDieStep(1, new HitDie(6)));
    //			assertBadUnparse();
    //		}
    //		catch (IllegalArgumentException e)
    //		{
    //			//Good here too :)
    //		}
    //	}

    private void assertBadUnparse()
    {
        assertNull(getToken().unparse(primaryContext, primaryProf1));
        assertTrue(primaryContext.getWriteMessageCount() > 0);
    }

}
