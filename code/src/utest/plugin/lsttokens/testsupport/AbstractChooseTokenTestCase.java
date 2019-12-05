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
package plugin.lsttokens.testsupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URISyntaxException;

import pcgen.cdom.base.BasicChooseInformation;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.choiceset.CollectionToChoiceSet;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.QualifierToken;
import plugin.qualifier.pobject.AnyToken;
import plugin.qualifier.pobject.QualifiedToken;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractChooseTokenTestCase<T extends CDOMObject, TC extends CDOMObject>
        extends AbstractCDOMTokenTestCase<T>
{

    private static QualifierToken<CDOMObject> qual = new QualifiedToken<>();

    private static QualifierToken<CDOMObject> anyqualifier = new AnyToken<>();

    public abstract CDOMSecondaryToken<?> getSubToken();

    private static boolean allowsPCQualifier;

    public String getSubTokenName()
    {
        return getSubToken().getTokenName();
    }

    public abstract Class<TC> getTargetClass();

    @BeforeEach
    @Override
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        QualifierToken<? extends CDOMObject> pcqual = getPCQualifier();
        allowsPCQualifier = pcqual != null;
        TokenRegistration.register(getSubToken());
        TokenRegistration.register(qual);
        TokenRegistration.register(anyqualifier);
        if (allowsPCQualifier)
        {
            TokenRegistration.register(pcqual);
        }
    }

    protected Loadable construct(LoadContext loadContext, String one)
    {
        return construct(loadContext, getTargetClass(), one);
    }

    protected static CDOMObject construct(LoadContext loadContext,
            Class<? extends CDOMObject> cl, String one)
    {
        return loadContext.getReferenceContext().constructCDOMObject(cl, one);
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return getSubTokenName() + '|' + "TestWP1|TestWP2|TestWP3";
    }

    @Override
    protected String getLegalValue()
    {
        return getSubTokenName() + '|' + "TestWP1|TestWP2";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }

    protected abstract boolean allowsQualifier();

    protected abstract boolean isTypeLegal();

    protected abstract boolean isAllLegal();

    protected abstract QualifierToken<? extends CDOMObject> getPCQualifier();

    @Test
    public void testInvalidInputEmptyString()
    {
        assertFalse(parse(""));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputOnlySubToken()
    {
        assertFalse(parse(getSubTokenName()));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputOnlySubTokenPipe()
    {
        assertFalse(parse(getSubTokenName() + '|'));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputJoinOnly()
    {
        assertEquals(!usesComma(), parse(getSubTokenName() + '|'
                + ","));
        if (usesComma())
        {
            assertNoSideEffects();
        } else
        {
            assertConstructionError();
        }
    }

    @Test
    public void testInvalidInputString()
    {
        assertEquals(!requiresLiteral(), parse(getSubTokenName() + '|'
                + "String"));
        if (requiresLiteral())
        {
            assertNoSideEffects();
        } else
        {
            assertConstructionError();
        }
    }

    private static boolean requiresLiteral()
    {
        return false;
    }

    @Test
    public void testInvalidInputJoinedDot()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        assertEquals(!requiresLiteral(), parse(getSubTokenName() + '|'
                + "TestWP1.TestWP2"));
        if (requiresLiteral())
        {
            assertNoSideEffects();
        } else
        {
            assertConstructionError();
        }
    }

    @Test
    public void testInvalidInputTypeEmpty()
    {
        assertFalse(parse(getSubTokenName() + '|' + "TYPE="));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputNotTypeEmpty()
    {
        assertFalse(parse(getSubTokenName() + '|' + "!TYPE="));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputTypeUnterminated()
    {
        assertFalse(parse(getSubTokenName() + '|' + "TYPE=One."));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputNotTypeUnterminated()
    {
        assertFalse(parse(getSubTokenName() + '|' + "!TYPE=One."));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputTypeDoubleSeparator()
    {
        assertFalse(parse(getSubTokenName() + '|' + "TYPE=One..Two"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputNotTypeDoubleSeparator()
    {
        assertFalse(parse(getSubTokenName() + '|' + "!TYPE=One..Two"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputTypeFalseStart()
    {
        assertFalse(parse(getSubTokenName() + '|' + "TYPE=.One"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputNotTypeFalseStart()
    {
        assertFalse(parse(getSubTokenName() + '|' + "!TYPE=.One"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidListEndPipe()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse(getSubTokenName() + '|' + "TestWP1|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidListEndComma()
    {
        construct(primaryContext, "TestWP1");
        assertEquals(!usesComma(), parse(getSubTokenName() + '|'
                + "TestWP1,"));
        if (usesComma())
        {
            assertNoSideEffects();
        } else
        {
            assertConstructionError();
        }
    }

    @Test
    public void testInvalidListStartPipe()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse(getSubTokenName() + '|' + "|TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidListStartComma()
    {
        construct(primaryContext, "TestWP1");
        assertEquals(!usesComma(), parse(getSubTokenName() + '|'
                + ",TestWP1"));
        if (usesComma())
        {
            assertNoSideEffects();
        } else
        {
            assertConstructionError();
        }
    }

    @Test
    public void testInvalidListDoubleJoinPipe()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        assertFalse(parse(getSubTokenName() + '|' + "TestWP2||TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidTitle()
    {
        construct(primaryContext, "TestWP1");
        boolean ret = parse(getSubTokenName() + '|' + "TestWP1|TITLE=");
        if (ret)
        {
            assertConstructionError();
        } else
        {
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidListDoubleJoinComma()
    {
        try
        {
            assertFalse(parse(getSubTokenName() + '|' + "TYPE=Foo,,!TYPE=Bar"));
        } catch (IllegalArgumentException e)
        {
            //OK too :)
        }
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputNotBuilt()
    {
        // Explicitly do NOT build TestWP2
        construct(primaryContext, "TestWP1");
        assertEquals(!requiresLiteral(), parse(getSubTokenName() + '|'
                + "TestWP1|TestWP2"));
        if (requiresLiteral())
        {
            assertNoSideEffects();
        } else
        {
            assertConstructionError();
        }
    }

    @Test
    public void testInvalidInputCheckTypeEqualLengthBar()
    {
        if (isTypeLegal())
        {
            /*
             * Explicitly do NOT build TestWP2 (this checks that the TYPE=
             * doesn't consume the |
             */
            construct(primaryContext, "TestWP1");
            CDOMObject a = (CDOMObject) construct(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            assertEquals(!requiresLiteral(), parse(getSubTokenName() + '|'
                    + "TestWP1|TYPE=TestType|TestWP2"));
            if (requiresLiteral())
            {
                assertNoSideEffects();
            } else
            {
                assertConstructionError();
            }
        }
    }

    @Test
    public void testInvalidInputCheckTypeDotLengthPipe()
    {
        /*
         * Explicitly do NOT build TestWP2 (this checks that the TYPE= doesn't
         * consume the |
         */
        construct(primaryContext, "TestWP1");
        if (CDOMObject.class.isAssignableFrom(getTargetClass()))
        {
            CDOMObject a = (CDOMObject) construct(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            CDOMObject b = (CDOMObject) construct(primaryContext, "Typed2");
            b.addToListFor(ListKey.TYPE, Type.getConstant("OtherTestType"));
        }
        assertEquals(!requiresLiteral(), parse(getSubTokenName() + '|'
                + "TestWP1|" + "TYPE.TestType.OtherTestType|TestWP2"));
        if (requiresLiteral())
        {
            assertNoSideEffects();
        } else
        {
            assertConstructionError();
        }
    }

    @Test
    public void testValidInputsTypeDot()
    {
        if (isTypeLegal())
        {
            CDOMObject a = (CDOMObject) construct(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            assertTrue(parse(getSubTokenName() + '|' + "TYPE.TestType"));
            assertCleanConstruction();
        }
    }

    @Test
    public void testRoundRobinOne() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        runRoundRobin(getSubTokenName() + '|' + "TestWP1");
    }

    @Test
    public void testRoundRobinOneTitle() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        runRoundRobin(getSubTokenName() + '|' + "TestWP1|TITLE=Test Title");
    }

    @Test
    public void testRoundRobinOnePreFooler() throws PersistenceLayerException
    {
        construct(primaryContext, "Prefool");
        construct(secondaryContext, "Prefool");
        runRoundRobin(getSubTokenName() + '|' + "Prefool");
    }

    @Test
    public void testRoundRobinParen() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1 (Test)");
        construct(secondaryContext, "TestWP1 (Test)");
        runRoundRobin(getSubTokenName() + '|' + "TestWP1 (Test)");
    }

    @Test
    public void testRoundRobinTitle() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1 (Test)");
        construct(secondaryContext, "TestWP1 (Test)");
        runRoundRobin(getSubTokenName() + '|' + "TestWP1 (Test)|TITLE=Foo Title");
    }

    @Test
    public void testRoundRobinThree() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(primaryContext, "TestWP3");
        construct(secondaryContext, "TestWP1");
        construct(secondaryContext, "TestWP2");
        construct(secondaryContext, "TestWP3");
        runRoundRobin(getSubTokenName() + '|' + "TestWP1|TestWP2|TestWP3");
    }

    @Test
    public void testRoundRobinTitleThree() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(primaryContext, "TestWP3");
        construct(secondaryContext, "TestWP1");
        construct(secondaryContext, "TestWP2");
        construct(secondaryContext, "TestWP3");
        runRoundRobin(getSubTokenName() + '|' + "TestWP1|TestWP2|TestWP3|TITLE=New Title");
    }

    @Test
    public void testRoundRobinWithEqualType() throws PersistenceLayerException
    {
        if (isTypeLegal())
        {
            construct(primaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            construct(secondaryContext, "TestWP1");
            construct(secondaryContext, "TestWP2");
            CDOMObject a = (CDOMObject) construct(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            CDOMObject b = (CDOMObject) construct(primaryContext, "Typed2");
            b.addToListFor(ListKey.TYPE, Type.getConstant("OtherTestType"));
            CDOMObject c = (CDOMObject) construct(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            CDOMObject d = (CDOMObject) construct(secondaryContext, "Typed2");
            d.addToListFor(ListKey.TYPE, Type.getConstant("OtherTestType"));
            runRoundRobin(getSubTokenName() + '|'
                    + "TestWP1|TestWP2|TYPE=OtherTestType|TYPE=TestType");
        }
    }

    @Test
    public void testRoundRobinTestEquals() throws PersistenceLayerException
    {
        if (isTypeLegal())
        {
            CDOMObject a = (CDOMObject) construct(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            CDOMObject c = (CDOMObject) construct(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            runRoundRobin(getSubTokenName() + '|' + "TYPE=TestType");
        }
    }

    @Test
    public void testRoundRobinTestEqualThree() throws PersistenceLayerException
    {
        if (isTypeLegal())
        {
            CDOMObject a = (CDOMObject) construct(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestThirdType"));
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestAltType"));
            CDOMObject c = (CDOMObject) construct(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            c.addToListFor(ListKey.TYPE, Type.getConstant("TestThirdType"));
            c.addToListFor(ListKey.TYPE, Type.getConstant("TestAltType"));
            runRoundRobin(getSubTokenName() + '|'
                    + "TYPE=TestAltType.TestThirdType.TestType");
        }
    }

    @Test
    public void testInvalidInputAnyItem()
    {
        if (isAllLegal())
        {
            construct(primaryContext, "TestWP1");
            assertFalse(parse(getSubTokenName() + "|ALL|TestWP1"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputItemAny()
    {
        if (isAllLegal())
        {
            construct(primaryContext, "TestWP1");
            assertFalse(parse(getSubTokenName() + '|' + "TestWP1|ALL"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputAnyType()
    {
        if (isTypeLegal())
        {
            assertFalse(parse(getSubTokenName() + '|' + "ALL|TYPE=TestType"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputTypeAny()
    {
        if (isTypeLegal())
        {
            assertFalse(parse(getSubTokenName() + '|' + "TYPE=TestType|ALL"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInputInvalidAddsTypeNoSideEffect()
    {
        if (isTypeLegal())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            construct(secondaryContext, "TestWP2");
            construct(primaryContext, "TestWP3");
            construct(secondaryContext, "TestWP3");
            assertTrue(parse(getSubTokenName() + '|' + "TestWP1|TestWP2"));
            assertTrue(parseSecondary(getSubTokenName() + '|'
                    + "TestWP1|TestWP2"));
            assertFalse(parse(getSubTokenName() + '|' + "TestWP3|TYPE="));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInputInvalidAddsBasicNoSideEffect()
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(secondaryContext, "TestWP2");
        construct(primaryContext, "TestWP3");
        construct(secondaryContext, "TestWP3");
        construct(primaryContext, "TestWP4");
        construct(secondaryContext, "TestWP4");
        assertTrue(parse(getSubTokenName() + '|' + "TestWP1|TestWP2"));
        assertTrue(parseSecondary(getSubTokenName() + '|' + "TestWP1|TestWP2"));
        assertFalse(parse(getSubTokenName() + '|' + "TestWP3||TestWP4"));
        assertNoSideEffects();
    }

    @Test
    public void testInputInvalidAddsAllNoSideEffect()
    {
        if (isAllLegal())
        {

            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            construct(secondaryContext, "TestWP2");
            construct(primaryContext, "TestWP3");
            construct(secondaryContext, "TestWP3");
            assertTrue(parse(getSubTokenName() + '|' + "TestWP1|TestWP2"));
            assertTrue(parseSecondary(getSubTokenName() + '|'
                    + "TestWP1|TestWP2"));
            assertFalse(parse(getSubTokenName() + '|' + "TestWP3|ALL"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testRoundRobinTestAll() throws PersistenceLayerException
    {
        if (isAllLegal())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            runRoundRobin(getSubTokenName() + "|ALL");
        }
    }

    @Test
    public void testInvalidInputJoinedDotQualifier()
    {
        assertEquals(!requiresLiteral(), parse(getSubTokenName() + '|'
                + "PC.QUALIFIED"));
        if (requiresLiteral())
        {
            assertNoSideEffects();
        } else
        {
            assertConstructionError();
        }
    }

    @Test
    public void testInvalidInputQualifierOpenBracket()
    {
        if (allowsQualifier())
        {
            assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED["));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputQualifierCloseBracket()
    {
        if (allowsQualifier())
        {
            assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputQualifierEmptyBracket()
    {
        if (allowsQualifier())
        {
            assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputQualifierQualifier()
    {
        if (allowsQualifier())
        {
            assertTrue(parse(getSubTokenName() + '|' + "QUALIFIED[QUALIFIED]"));
            assertConstructionError();
        }
    }

    @Test
    public void testInvalidInputJoinQualifiedOnlyPipe()
    {
        if (allowsQualifier())
        {
            assertFalse(parse(getSubTokenName() + "|QUALIFIED[|]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputJoinQualifiedOnlyComma()
    {
        if (allowsQualifier())
        {
            assertFalse(parse(getSubTokenName() + "|QUALIFIED[,]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputStringQualified()
    {
        if (allowsQualifier())
        {
            assertTrue(parse(getSubTokenName() + '|' + "QUALIFIED[String]"));
            assertConstructionError();
        }
    }

    @Test
    public void testInvalidInputJoinedDotQualified()
    {
        if (allowsQualifier())
        {
            construct(primaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            assertTrue(parse(getSubTokenName() + '|'
                    + "QUALIFIED[TestWP1.TestWP2]"));
            assertConstructionError();
        }
    }

    @Test
    public void testInvalidInputQualifiedTypeEmpty()
    {
        if (allowsQualifier())
        {
            assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[TYPE=]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputQualifiedNotTypeEmpty()
    {
        if (allowsQualifier())
        {
            assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[!TYPE=]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputQualifiedTypeUnterminated()
    {
        if (allowsQualifier())
        {
            assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[TYPE=One.]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputQualifiedNotTypeUnterminated()
    {
        if (allowsQualifier())
        {
            assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[!TYPE=One.]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputQualifiedTypeDoubleSeparator()
    {
        if (allowsQualifier())
        {
            assertFalse(parse(getSubTokenName() + '|'
                    + "QUALIFIED[TYPE=One..Two]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputQualifiedNotTypeDoubleSeparator()
    {
        if (allowsQualifier())
        {
            assertFalse(parse(getSubTokenName() + '|'
                    + "QUALIFIED[!TYPE=One..Two]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputQualifiedTypeFalseStart()
    {
        if (allowsQualifier())
        {
            assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[TYPE=.One]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputQualifiedNotTypeFalseStart()
    {
        if (allowsQualifier())
        {
            assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[!TYPE=.One]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidQualifiedListEndPipe()
    {
        if (allowsQualifier())
        {
            construct(primaryContext, "TestWP1");
            assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[TestWP1|]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidQualifiedListEndComma()
    {
        if (allowsQualifier())
        {
            construct(primaryContext, "TestWP1");
            assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[TestWP1,]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidQualifiedListStartPipe()
    {
        if (allowsQualifier())
        {
            construct(primaryContext, "TestWP1");
            assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[|TestWP1]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidQualifiedListStartComma()
    {
        if (allowsQualifier())
        {
            construct(primaryContext, "TestWP1");
            assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[,TestWP1]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidQualifiedListDoubleJoinPipe()
    {
        if (allowsQualifier())
        {
            construct(primaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            assertFalse(parse(getSubTokenName() + '|'
                    + "QUALIFIED[TestWP2||TestWP1]]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidQualifiedListDoubleJoinComma()
    {
        if (allowsQualifier())
        {
            assertFalse(parse(getSubTokenName() + '|'
                    + "QUALIFIED[TYPE=Foo,,!TYPE=Bar]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidQualifiedInputNotBuilt()
    {
        if (allowsQualifier())
        {
            // Explicitly do NOT build TestWP2
            construct(primaryContext, "TestWP1");
            assertTrue(parse(getSubTokenName() + '|'
                    + "QUALIFIED[TestWP1|TestWP2]"));
            assertConstructionError();
        }
    }

    @Test
    public void testInvalidQualifiedDanglingType()
    {
        if (allowsQualifier())
        {
            construct(primaryContext, "TestWP1");
            assertFalse(parse(getSubTokenName() + '|'
                    + "QUALIFIED[TestWP1]TYPE=Foo"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidQualifiedDanglingPrimitive()
    {
        if (allowsQualifier())
        {
            construct(primaryContext, "TestWP1");
            assertFalse(parse(getSubTokenName() + '|'
                    + "QUALIFIED[TYPE=Foo]TestWP1"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidQualifiedDanglingTypePipe()
    {
        if (allowsQualifier())
        {
            construct(primaryContext, "TestWP1");
            assertFalse(parse(getSubTokenName() + '|'
                    + "QUALIFIED[TestWP1]TYPE=Foo|TYPE=Bar"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidQualifiedDanglingPrimitiveComma()
    {
        if (allowsQualifier())
        {
            construct(primaryContext, "TestWP1");
            assertFalse(parse(getSubTokenName() + '|'
                    + "QUALIFIED[TYPE=Foo]TestWP1,TYPE=Bar"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testValidQualifiedInputLotsOr()
            throws PersistenceLayerException
    {
        if (allowsQualifier())
        {
            CDOMObject a = (CDOMObject) construct(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("Foo"));
            CDOMObject b = (CDOMObject) construct(primaryContext, "Typed2");
            b.addToListFor(ListKey.TYPE, Type.getConstant("Yea"));
            CDOMObject c = (CDOMObject) construct(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("Foo"));
            CDOMObject d = (CDOMObject) construct(secondaryContext, "Typed2");
            d.addToListFor(ListKey.TYPE, Type.getConstant("Yea"));
            CDOMObject e = (CDOMObject) construct(primaryContext, "Typed3");
            e.addToListFor(ListKey.TYPE, Type.getConstant("Bar"));
            CDOMObject f = (CDOMObject) construct(primaryContext, "Typed4");
            f.addToListFor(ListKey.TYPE, Type.getConstant("Goo"));
            CDOMObject g = (CDOMObject) construct(secondaryContext, "Typed3");
            g.addToListFor(ListKey.TYPE, Type.getConstant("Bar"));
            CDOMObject h = (CDOMObject) construct(secondaryContext, "Typed4");
            h.addToListFor(ListKey.TYPE, Type.getConstant("Goo"));
            runRoundRobin(getSubTokenName()
                    + '|'
                    + "QUALIFIED[TYPE=Bar|TYPE=Goo]|QUALIFIED[TYPE=Foo|TYPE=Yea]");
        }
    }

    @Test
    public void testValidQualifiedInputLotsAnd()
            throws PersistenceLayerException
    {
        if (allowsQualifier())
        {
            CDOMObject a = (CDOMObject) construct(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("Foo"));
            CDOMObject b = (CDOMObject) construct(primaryContext, "Typed2");
            b.addToListFor(ListKey.TYPE, Type.getConstant("Yea"));
            CDOMObject c = (CDOMObject) construct(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("Foo"));
            CDOMObject d = (CDOMObject) construct(secondaryContext, "Typed2");
            d.addToListFor(ListKey.TYPE, Type.getConstant("Yea"));
            CDOMObject e = (CDOMObject) construct(primaryContext, "Typed3");
            e.addToListFor(ListKey.TYPE, Type.getConstant("Bar"));
            CDOMObject f = (CDOMObject) construct(primaryContext, "Typed4");
            f.addToListFor(ListKey.TYPE, Type.getConstant("Goo"));
            CDOMObject g = (CDOMObject) construct(secondaryContext, "Typed3");
            g.addToListFor(ListKey.TYPE, Type.getConstant("Bar"));
            CDOMObject h = (CDOMObject) construct(secondaryContext, "Typed4");
            h.addToListFor(ListKey.TYPE, Type.getConstant("Goo"));
            runRoundRobin(getSubTokenName()
                    + '|'
                    + "QUALIFIED[TYPE=Bar,TYPE=Goo],QUALIFIED[TYPE=Foo,TYPE=Yea]");
        }
    }

    @Test
    public void testInvalidQualifiedInputCheckTypeEqualLengthBar()
    {
        if (allowsQualifier())
        {
            /*
             * Explicitly do NOT build TestWP2 (this checks that the TYPE=
             * doesn't consume the |
             */
            construct(primaryContext, "TestWP1");
            assertTrue(parse(getSubTokenName() + '|'
                    + "QUALIFIED[TestWP1|TYPE=TestType|TestWP2]"));
            assertConstructionError();
        }
    }

    @Test
    public void testInvalidQualifiedInputCheckTypeDotLengthPipe()
    {
        if (allowsQualifier())
        {
            /*
             * Explicitly do NOT build TestWP2 (this checks that the TYPE=
             * doesn't consume the |
             */
            construct(primaryContext, "TestWP1");
            assertTrue(parse(getSubTokenName() + '|' + "QUALIFIED[TestWP1|"
                    + "TYPE.TestType.OtherTestType|TestWP2]"));
            assertConstructionError();
        }
    }

    @Test
    public void testRoundRobinQualifiedOne() throws PersistenceLayerException
    {
        if (allowsQualifier())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            runRoundRobin(getSubTokenName() + '|' + "QUALIFIED[TestWP1]");
        }
    }

    @Test
    public void testRoundRobinQualifiedParen() throws PersistenceLayerException
    {
        if (allowsQualifier())
        {
            construct(primaryContext, "TestWP1 (Test)");
            construct(secondaryContext, "TestWP1 (Test)");
            runRoundRobin(getSubTokenName() + '|' + "QUALIFIED[TestWP1 (Test)]");
        }
    }

    @Test
    public void testRoundRobinQualifiedThreeOr()
            throws PersistenceLayerException
    {
        if (allowsQualifier())
        {
            construct(primaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            construct(primaryContext, "TestWP3");
            construct(secondaryContext, "TestWP1");
            construct(secondaryContext, "TestWP2");
            construct(secondaryContext, "TestWP3");
            runRoundRobin(getSubTokenName() + '|'
                    + "QUALIFIED[TestWP1|TestWP2|TestWP3]");
        }
    }

    @Test
    public void testRoundRobinQualifiedThreeAnd()
            throws PersistenceLayerException
    {
        if (allowsQualifier())
        {
            CDOMObject a = (CDOMObject) construct(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("Type1"));
            CDOMObject b = (CDOMObject) construct(primaryContext, "Typed2");
            b.addToListFor(ListKey.TYPE, Type.getConstant("Type2"));
            CDOMObject c = (CDOMObject) construct(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("Type1"));
            CDOMObject d = (CDOMObject) construct(secondaryContext, "Typed2");
            d.addToListFor(ListKey.TYPE, Type.getConstant("Type2"));
            CDOMObject e = (CDOMObject) construct(primaryContext, "Typed3");
            e.addToListFor(ListKey.TYPE, Type.getConstant("Type3"));
            CDOMObject g = (CDOMObject) construct(secondaryContext, "Typed3");
            g.addToListFor(ListKey.TYPE, Type.getConstant("Type3"));
            runRoundRobin(getSubTokenName() + '|'
                    + "QUALIFIED[!TYPE=Type1,TYPE=Type2,TYPE=Type3]");
        }
    }

    @Test
    public void testRoundRobinQualifiedFourAndOr()
            throws PersistenceLayerException
    {
        if (allowsQualifier())
        {
            CDOMObject a = (CDOMObject) construct(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("Type1"));
            CDOMObject b = (CDOMObject) construct(primaryContext, "Typed2");
            b.addToListFor(ListKey.TYPE, Type.getConstant("Type2"));
            CDOMObject c = (CDOMObject) construct(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("Type1"));
            CDOMObject d = (CDOMObject) construct(secondaryContext, "Typed2");
            d.addToListFor(ListKey.TYPE, Type.getConstant("Type2"));
            CDOMObject e = (CDOMObject) construct(primaryContext, "Typed3");
            e.addToListFor(ListKey.TYPE, Type.getConstant("Type3"));
            CDOMObject f = (CDOMObject) construct(primaryContext, "Typed4");
            f.addToListFor(ListKey.TYPE, Type.getConstant("Type4"));
            CDOMObject g = (CDOMObject) construct(secondaryContext, "Typed3");
            g.addToListFor(ListKey.TYPE, Type.getConstant("Type3"));
            CDOMObject h = (CDOMObject) construct(secondaryContext, "Typed4");
            h.addToListFor(ListKey.TYPE, Type.getConstant("Type4"));
            runRoundRobin(getSubTokenName()
                    + '|'
                    + "QUALIFIED[!TYPE=Type1,TYPE=Type2|!TYPE=Type3,TYPE=Type4]");
        }
    }

    @Test
    public void testRoundRobinQualifiedWithEqualType()
            throws PersistenceLayerException
    {
        if (allowsQualifier())
        {
            construct(primaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            construct(secondaryContext, "TestWP1");
            construct(secondaryContext, "TestWP2");
            CDOMObject a = (CDOMObject) construct(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("OtherTestType"));
            CDOMObject b = (CDOMObject) construct(primaryContext, "Typed2");
            b.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            CDOMObject c = (CDOMObject) construct(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("OtherTestType"));
            CDOMObject d = (CDOMObject) construct(secondaryContext, "Typed2");
            d.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            runRoundRobin(getSubTokenName()
                    + '|'
                    + "QUALIFIED[TestWP1|TestWP2|TYPE=OtherTestType|TYPE=TestType]");
        }
    }

    @Test
    public void testRoundRobinQualifiedTestEquals()
            throws PersistenceLayerException
    {
        if (allowsQualifier())
        {
            CDOMObject b = (CDOMObject) construct(primaryContext, "Typed2");
            b.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            CDOMObject d = (CDOMObject) construct(secondaryContext, "Typed2");
            d.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            runRoundRobin(getSubTokenName() + '|' + "QUALIFIED[TYPE=TestType]");
        }
    }

    @Test
    public void testRoundRobinQualifiedTestEqualThree()
            throws PersistenceLayerException
    {
        if (allowsQualifier())
        {
            CDOMObject a = (CDOMObject) construct(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestThirdType"));
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestAltType"));
            CDOMObject c = (CDOMObject) construct(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("TestThirdType"));
            c.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            c.addToListFor(ListKey.TYPE, Type.getConstant("TestAltType"));
            runRoundRobin(getSubTokenName() + '|'
                    + "QUALIFIED[TYPE=TestAltType.TestThirdType.TestType]");
        }
    }

    @Test
    public void testInvalidQualifiedInputAnyItem()
    {
        if (allowsQualifier())
        {
            construct(primaryContext, "TestWP1");
            assertFalse(parse(getSubTokenName() + "|QUALIFIED[ALL|TestWP1]"));
            assertNoSideEffects();
        }

    }

    @Test
    public void testInvalidQualifiedInputItemAny()
    {
        if (allowsQualifier())
        {
            construct(primaryContext, "TestWP1");
            assertFalse(parse(getSubTokenName() + '|'
                    + "QUALIFIED[TestWP1|ALL]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidQualifiedInputAnyType()
    {
        if (allowsQualifier())
        {
            assertFalse(parse(getSubTokenName() + '|'
                    + "QUALIFIED[ALL|TYPE=TestType]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidQualifiedInputTypeAny()
    {
        if (allowsQualifier())
        {
            assertFalse(parse(getSubTokenName() + '|'
                    + "QUALIFIED[TYPE=TestType|ALL]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInputInvalidQualifiedAddsTypeNoSideEffect()
    {
        if (allowsQualifier())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            construct(secondaryContext, "TestWP2");
            construct(primaryContext, "TestWP3");
            construct(secondaryContext, "TestWP3");
            assertTrue(parse(getSubTokenName() + '|'
                    + "QUALIFIED[TestWP1|TestWP2]"));
            assertTrue(parseSecondary(getSubTokenName() + '|'
                    + "QUALIFIED[TestWP1|TestWP2]"));
            assertFalse(parse(getSubTokenName() + '|'
                    + "QUALIFIED[TestWP3|TYPE=]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInputInvalidQualifiedAddsBasicNoSideEffect()
    {
        if (allowsQualifier())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            construct(secondaryContext, "TestWP2");
            construct(primaryContext, "TestWP3");
            construct(secondaryContext, "TestWP3");
            construct(primaryContext, "TestWP4");
            construct(secondaryContext, "TestWP4");
            assertTrue(parse(getSubTokenName() + '|'
                    + "QUALIFIED[TestWP1|TestWP2]"));
            assertTrue(parseSecondary(getSubTokenName() + '|'
                    + "QUALIFIED[TestWP1|TestWP2]"));
            assertFalse(parse(getSubTokenName() + '|'
                    + "QUALIFIED[TestWP3||TestWP4]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInputInvalidQualifiedAddsAllNoSideEffect()
    {
        if (allowsQualifier())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            construct(secondaryContext, "TestWP2");
            construct(primaryContext, "TestWP3");
            construct(secondaryContext, "TestWP3");
            assertTrue(parse(getSubTokenName() + '|'
                    + "QUALIFIED[TestWP1|TestWP2]"));
            assertTrue(parseSecondary(getSubTokenName() + '|'
                    + "QUALIFIED[TestWP1|TestWP2]"));
            assertFalse(parse(getSubTokenName() + '|'
                    + "QUALIFIED[TestWP3|ALL]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testRoundRobinTestQualifiedAll()
            throws PersistenceLayerException
    {
        if (allowsQualifier())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            runRoundRobin(getSubTokenName() + "|QUALIFIED[ALL]");
        }
    }

    //TODO: These tests fail for CHOOSE:SPELLS - but the code works under normal use.
//	@Test
//	public void testRoundRobinTestQualifiedAll2()
//			throws PersistenceLayerException
//	{
//		if (allowsQualifier())
//		{
//			runRoundRobin(getSubTokenName() + "|ANY");
//		}
//	}
//
//	@Test
//	public void testRoundRobinTestQualifiedAll3()
//			throws PersistenceLayerException
//	{
//		if (allowsQualifier())
//		{
//			runRoundRobin(getSubTokenName() + "|ANY[ALL]");
//		}
//	}

    @Test
    public void testUnparseNull()
    {
        primaryProf.put(getObjectKey(), null);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    private static ObjectKey<ChooseInformation<?>> getObjectKey()
    {
        return ObjectKey.CHOOSE_INFO;
    }

    @Test
    public void testUnparseIllegalAllItem()
    {
        if (isAllLegal())
        {
            assertBadChoose("ALL|TestWP1");
        }
    }

    private void assertBadChoose(String value)
    {
        parseForUnparse(value, false);
        assertBadUnparse();
    }

    @Test
    public void testUnparseIllegalItemAll()
    {
        if (isAllLegal())
        {
            assertBadChoose("ALL|TestWP1");
        }
    }

    @Test
    public void testUnparseIllegalAllType()
    {
        if (isAllLegal())
        {
            assertBadChoose("ALL|TestWP1");
        }
    }

    @Test
    public void testUnparseIllegalTypeAll()
    {
        if (isAllLegal())
        {
            assertBadChoose("ALL|TestWP1");
        }
    }

    @Test
    public void testUnparseLegal()
    {
        assertGoodChoose("TestWP1|TestWP2");
    }

    private void assertGoodChoose(String value)
    {
        parseForUnparse(value, true);
        String[] unparse = getToken().unparse(primaryContext, primaryProf);
        assertNotNull(unparse);
        assertEquals(1, unparse.length);
        assertEquals(unparse[0], getSubToken().getTokenName() + "|" + value);
    }

    private void parseForUnparse(String value, boolean valid)
    {
        PrimitiveChoiceSet<TC> pcs = new CollectionToChoiceSet<>(
                primaryContext.getChoiceSet(getManufacturer(), value));
        assertNotNull(pcs);
        assertEquals(valid, pcs.getGroupingState().isValid());
        BasicChooseInformation<TC> cs = new BasicChooseInformation<>(
                getSubToken().getTokenName(), pcs, getManufacturer().getIdentifierType());
        cs.setTitle(getChoiceTitle());
        primaryProf.put(ObjectKey.CHOOSE_INFO, cs);
    }

    protected ReferenceManufacturer<TC> getManufacturer()
    {
        return primaryContext.getReferenceContext().getManufacturer(getTargetClass());
    }

    protected abstract String getChoiceTitle();

    @SuppressWarnings("unchecked")
    @Test
    public void testUnparseGenericsFail()
    {
        ObjectKey objectKey = getObjectKey();
        primaryProf.put(objectKey, new Object());
        try
        {
            getToken().unparse(primaryContext, primaryProf);
            fail();
        } catch (ClassCastException e)
        {
            // Yep!
        }
    }

    @Test
    public void testValidQualifiedPCInputLotsOr()
            throws PersistenceLayerException
    {
        if (allowsPCQualifier)
        {
            CDOMObject a = (CDOMObject) construct(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("Foo"));
            CDOMObject b = (CDOMObject) construct(primaryContext, "Typed2");
            b.addToListFor(ListKey.TYPE, Type.getConstant("Yea"));
            CDOMObject c = (CDOMObject) construct(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("Foo"));
            CDOMObject d = (CDOMObject) construct(secondaryContext, "Typed2");
            d.addToListFor(ListKey.TYPE, Type.getConstant("Yea"));
            CDOMObject e = (CDOMObject) construct(primaryContext, "Typed3");
            e.addToListFor(ListKey.TYPE, Type.getConstant("Bar"));
            CDOMObject f = (CDOMObject) construct(primaryContext, "Typed4");
            f.addToListFor(ListKey.TYPE, Type.getConstant("Goo"));
            CDOMObject g = (CDOMObject) construct(secondaryContext, "Typed3");
            g.addToListFor(ListKey.TYPE, Type.getConstant("Bar"));
            CDOMObject h = (CDOMObject) construct(secondaryContext, "Typed4");
            h.addToListFor(ListKey.TYPE, Type.getConstant("Goo"));
            runRoundRobin(getSubTokenName() + '|'
                    + "PC[TYPE=Bar|TYPE=Goo]|PC[TYPE=Foo|TYPE=Yea]");
        }
    }

    @Test
    public void testValidQualifiedPCInputLotsAnd()
            throws PersistenceLayerException
    {
        if (allowsPCQualifier)
        {
            CDOMObject a = (CDOMObject) construct(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("Foo"));
            CDOMObject b = (CDOMObject) construct(primaryContext, "Typed2");
            b.addToListFor(ListKey.TYPE, Type.getConstant("Yea"));
            CDOMObject c = (CDOMObject) construct(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("Foo"));
            CDOMObject d = (CDOMObject) construct(secondaryContext, "Typed2");
            d.addToListFor(ListKey.TYPE, Type.getConstant("Yea"));
            CDOMObject e = (CDOMObject) construct(primaryContext, "Typed3");
            e.addToListFor(ListKey.TYPE, Type.getConstant("Bar"));
            CDOMObject f = (CDOMObject) construct(primaryContext, "Typed4");
            f.addToListFor(ListKey.TYPE, Type.getConstant("Goo"));
            CDOMObject g = (CDOMObject) construct(secondaryContext, "Typed3");
            g.addToListFor(ListKey.TYPE, Type.getConstant("Bar"));
            CDOMObject h = (CDOMObject) construct(secondaryContext, "Typed4");
            h.addToListFor(ListKey.TYPE, Type.getConstant("Goo"));
            runRoundRobin(getSubTokenName() + '|'
                    + "PC[TYPE=Bar,TYPE=Goo],PC[TYPE=Foo,TYPE=Yea]");
        }
    }

    @Test
    public void testRoundRobinQualifiedPCOne() throws PersistenceLayerException
    {
        if (allowsPCQualifier)
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            runRoundRobin(getSubTokenName() + '|' + "PC[TestWP1]");
        }
    }

    @Test
    public void testRoundRobinQualifiedPCParen()
            throws PersistenceLayerException
    {
        if (allowsPCQualifier)
        {
            construct(primaryContext, "TestWP1 (Test)");
            construct(secondaryContext, "TestWP1 (Test)");
            runRoundRobin(getSubTokenName() + '|' + "PC[TestWP1 (Test)]");
        }
    }

    @Test
    public void testRoundRobinQualifiedPCThreeOr()
            throws PersistenceLayerException
    {
        if (allowsPCQualifier)
        {
            construct(primaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            construct(primaryContext, "TestWP3");
            construct(secondaryContext, "TestWP1");
            construct(secondaryContext, "TestWP2");
            construct(secondaryContext, "TestWP3");
            runRoundRobin(getSubTokenName() + '|'
                    + "PC[TestWP1|TestWP2|TestWP3]");
        }
    }

    @Test
    public void testRoundRobinQualifiedPCThreeAnd()
            throws PersistenceLayerException
    {
        if (allowsPCQualifier)
        {
            CDOMObject a = (CDOMObject) construct(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("Type1"));
            CDOMObject b = (CDOMObject) construct(primaryContext, "Typed2");
            b.addToListFor(ListKey.TYPE, Type.getConstant("Type2"));
            CDOMObject c = (CDOMObject) construct(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("Type1"));
            CDOMObject d = (CDOMObject) construct(secondaryContext, "Typed2");
            d.addToListFor(ListKey.TYPE, Type.getConstant("Type2"));
            CDOMObject e = (CDOMObject) construct(primaryContext, "Typed3");
            e.addToListFor(ListKey.TYPE, Type.getConstant("Type3"));
            CDOMObject g = (CDOMObject) construct(secondaryContext, "Typed3");
            g.addToListFor(ListKey.TYPE, Type.getConstant("Type3"));
            runRoundRobin(getSubTokenName() + '|'
                    + "PC[!TYPE=Type1,TYPE=Type2,TYPE=Type3]");
        }
    }

    @Test
    public void testRoundRobinQualifiedPCFourAndOr()
            throws PersistenceLayerException
    {
        if (allowsPCQualifier)
        {
            CDOMObject a = (CDOMObject) construct(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("Type1"));
            CDOMObject b = (CDOMObject) construct(primaryContext, "Typed2");
            b.addToListFor(ListKey.TYPE, Type.getConstant("Type2"));
            CDOMObject c = (CDOMObject) construct(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("Type1"));
            CDOMObject d = (CDOMObject) construct(secondaryContext, "Typed2");
            d.addToListFor(ListKey.TYPE, Type.getConstant("Type2"));
            CDOMObject e = (CDOMObject) construct(primaryContext, "Typed3");
            e.addToListFor(ListKey.TYPE, Type.getConstant("Type3"));
            CDOMObject f = (CDOMObject) construct(primaryContext, "Typed4");
            f.addToListFor(ListKey.TYPE, Type.getConstant("Type4"));
            CDOMObject g = (CDOMObject) construct(secondaryContext, "Typed3");
            g.addToListFor(ListKey.TYPE, Type.getConstant("Type3"));
            CDOMObject h = (CDOMObject) construct(secondaryContext, "Typed4");
            h.addToListFor(ListKey.TYPE, Type.getConstant("Type4"));
            runRoundRobin(getSubTokenName() + '|'
                    + "PC[!TYPE=Type1,TYPE=Type2|!TYPE=Type3,TYPE=Type4]");
        }
    }

    @Test
    public void testRoundRobinQualifiedPCNegatedFourAndOr()
            throws PersistenceLayerException
    {
        if (allowsPCQualifier)
        {
            CDOMObject a = (CDOMObject) construct(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("Type1"));
            CDOMObject b = (CDOMObject) construct(primaryContext, "Typed2");
            b.addToListFor(ListKey.TYPE, Type.getConstant("Type2"));
            CDOMObject c = (CDOMObject) construct(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("Type1"));
            CDOMObject d = (CDOMObject) construct(secondaryContext, "Typed2");
            d.addToListFor(ListKey.TYPE, Type.getConstant("Type2"));
            CDOMObject e = (CDOMObject) construct(primaryContext, "Typed3");
            e.addToListFor(ListKey.TYPE, Type.getConstant("Type3"));
            CDOMObject f = (CDOMObject) construct(primaryContext, "Typed4");
            f.addToListFor(ListKey.TYPE, Type.getConstant("Type4"));
            CDOMObject g = (CDOMObject) construct(secondaryContext, "Typed3");
            g.addToListFor(ListKey.TYPE, Type.getConstant("Type3"));
            CDOMObject h = (CDOMObject) construct(secondaryContext, "Typed4");
            h.addToListFor(ListKey.TYPE, Type.getConstant("Type4"));
            runRoundRobin(getSubTokenName() + '|'
                    + "!PC[!TYPE=Type1,TYPE=Type2|!TYPE=Type3,TYPE=Type4]");
        }
    }

    @Test
    public void testRoundRobinQualifiedPCWithEqualType()
            throws PersistenceLayerException
    {
        if (allowsPCQualifier)
        {
            construct(primaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            construct(secondaryContext, "TestWP1");
            construct(secondaryContext, "TestWP2");
            CDOMObject a = (CDOMObject) construct(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            CDOMObject b = (CDOMObject) construct(primaryContext, "Typed2");
            b.addToListFor(ListKey.TYPE, Type.getConstant("OtherTestType"));
            CDOMObject c = (CDOMObject) construct(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            CDOMObject d = (CDOMObject) construct(secondaryContext, "Typed2");
            d.addToListFor(ListKey.TYPE, Type.getConstant("OtherTestType"));
            runRoundRobin(getSubTokenName() + '|'
                    + "PC[TestWP1|TestWP2|TYPE=OtherTestType|TYPE=TestType]");
        }
    }

    @Test
    public void testRoundRobinQualifiedPCTestEquals()
            throws PersistenceLayerException
    {
        if (CDOMObject.class.isAssignableFrom(getTargetClass()))
        {
            CDOMObject a = (CDOMObject) construct(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            CDOMObject c = (CDOMObject) construct(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
        }
        if (allowsPCQualifier)
        {
            runRoundRobin(getSubTokenName() + '|' + "PC[TYPE=TestType]");
        } else
        {
            assertFalse(parse(getSubTokenName() + '|' + "PC[TYPE=TestType]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testRoundRobinQualifiedPCTestEqualThree()
            throws PersistenceLayerException
    {
        if (allowsPCQualifier)
        {
            CDOMObject a = (CDOMObject) construct(primaryContext, "Typed1");
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestThirdType"));
            a.addToListFor(ListKey.TYPE, Type.getConstant("TestAltType"));
            CDOMObject c = (CDOMObject) construct(secondaryContext, "Typed1");
            c.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            c.addToListFor(ListKey.TYPE, Type.getConstant("TestThirdType"));
            c.addToListFor(ListKey.TYPE, Type.getConstant("TestAltType"));
            runRoundRobin(getSubTokenName() + '|'
                    + "PC[TYPE=TestAltType.TestThirdType.TestType]");
        }
    }

    @Test
    public void testRoundRobinTestQualifiedPCAll()
            throws PersistenceLayerException
    {
        if (allowsPCQualifier)
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            runRoundRobin(getSubTokenName() + "|PC[ALL]");
        }
    }

    protected boolean usesComma()
    {
        return true;
    }
}
