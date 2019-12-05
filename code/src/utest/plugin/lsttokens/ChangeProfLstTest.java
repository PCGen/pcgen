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
package plugin.lsttokens;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.PCTemplate;
import pcgen.core.WeaponProf;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

public class ChangeProfLstTest extends AbstractGlobalTokenTestCase
{

    static CDOMPrimaryToken<CDOMObject> token = new ChangeprofLst();
    static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<>();

    @Override
    public CDOMLoader<PCTemplate> getLoader()
    {
        return loader;
    }

    @Override
    public Class<PCTemplate> getCDOMClass()
    {
        return PCTemplate.class;
    }

    @Override
    public CDOMPrimaryToken<CDOMObject> getReadToken()
    {
        return token;
    }

    @Override
    public CDOMPrimaryToken<CDOMObject> getWriteToken()
    {
        return token;
    }

    @Test
    public void testInvalidEmpty()
    {
        assertFalse(parse(""));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidSourceOnly()
    {
        assertFalse(parse("Hammer"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidSourceEqualOnly()
    {
        assertFalse(parse("Hammer="));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidSourceEqualOnlyTypeTwo()
    {
        assertFalse(parse("Hammer=Martial|Pipe="));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptySource()
    {
        assertFalse(parse("=Martial"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidTwoEquals()
    {
        assertFalse(parse("Hammer==Martial"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidTwoEqualsTypeTwo()
    {
        assertFalse(parse("Hammer=TYPE.Heavy=Martial"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidBarEnding()
    {
        assertFalse(parse("Hammer=Martial|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidBarStarting()
    {
        assertFalse(parse("|Hammer=Martial"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidDoublePipe()
    {
        assertFalse(parse("Hammer=Martial||Pipe=Exotic"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidReversed()
    {
        assertTrue(parse("Martial=Hammer"));
        assertConstructionError();
    }

    @Test
    public void testInvalidResultPrimitive()
    {
        assertTrue(parse("Hammer=Pipe"));
        assertConstructionError();
    }

    @Test
    public void testInvalidResultType()
    {
        try
        {
            assertFalse(parse("Hammer=TYPE.Heavy"));
        } catch (IllegalArgumentException e)
        {
            // This is okay too
        }
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinSimple() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Hammer");
        secondaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Hammer");
        WeaponProf a = primaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Longsword");
        a.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
        WeaponProf b = secondaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Longsword");
        b.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
        runRoundRobin("Hammer=Martial");
    }

    @Test
    public void testRoundRobinTwo() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Hammer");
        secondaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Hammer");
        primaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Pipe");
        secondaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Pipe");
        WeaponProf a = primaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Longsword");
        a.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
        WeaponProf b = secondaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Longsword");
        b.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
        runRoundRobin("Hammer,Pipe=Martial");
    }

    @Test
    public void testRoundRobinType() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Hammer");
        secondaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Hammer");
        WeaponProf a = primaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Longsword");
        a.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
        WeaponProf b = secondaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Longsword");
        b.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
        WeaponProf c = primaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Sledgehammer");
        c.addToListFor(ListKey.TYPE, Type.getConstant("Heavy"));
        WeaponProf d = secondaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Sledgehammer");
        d.addToListFor(ListKey.TYPE, Type.getConstant("Heavy"));
        runRoundRobin("Hammer,TYPE.Heavy=Martial");
    }

    @Test
    public void testRoundRobinTwoResult() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Hammer");
        secondaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Hammer");
        primaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Pipe");
        secondaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Pipe");
        WeaponProf a = primaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Longsword");
        a.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
        WeaponProf b = secondaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Longsword");
        b.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
        WeaponProf c = primaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Bolas");
        c.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
        WeaponProf d = secondaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Bolas");
        d.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
        runRoundRobin("Hammer=Martial|Pipe=Exotic");
    }

    @Test
    public void testRoundRobinComplex() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Hammer");
        secondaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Hammer");
        primaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Nail");
        secondaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Nail");
        WeaponProf a = primaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Longsword");
        a.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
        WeaponProf b = secondaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Longsword");
        b.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
        WeaponProf c = primaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Bolas");
        c.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
        WeaponProf d = secondaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Bolas");
        d.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
        WeaponProf e = primaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Sledgehammer");
        e.addToListFor(ListKey.TYPE, Type.getConstant("Heavy"));
        WeaponProf f = secondaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Sledgehammer");
        f.addToListFor(ListKey.TYPE, Type.getConstant("Heavy"));
        WeaponProf g = primaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Average Weapon");
        g.addToListFor(ListKey.TYPE, Type.getConstant("Medium"));
        WeaponProf h = secondaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Average Weapon");
        h.addToListFor(ListKey.TYPE, Type.getConstant("Medium"));
        WeaponProf k = primaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Loaded Diaper");
        k.addToListFor(ListKey.TYPE, Type.getConstant("Disposable"));
        k.addToListFor(ListKey.TYPE, Type.getConstant("Crazy"));
        WeaponProf l = secondaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Loaded Diaper");
        l.addToListFor(ListKey.TYPE, Type.getConstant("Crazy"));
        l.addToListFor(ListKey.TYPE, Type.getConstant("Disposable"));
        runRoundRobin("Hammer,TYPE.Heavy,TYPE.Medium=Martial|Nail,TYPE.Crazy,TYPE.Disposable=Exotic");
    }

    @Override
    protected String getLegalValue()
    {
        // TODO What happens in consolidation of ChangeProf if Wand is reused?
        // What "wins"?
        return "Pipe=Martial"; // |Wand=Exotic";
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "Hammer,Wand,TYPE.Heavy,TYPE.Medium=Martial|Nail,TYPE.Crazy,TYPE.Disposable=Exotic";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return strings -> new String[]{
                "Hammer,Pipe,Wand,TYPE.Heavy,TYPE.Medium=Martial|Nail,TYPE.Crazy,TYPE.Disposable=Exotic"};
    }
}
