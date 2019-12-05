/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Race;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.QualifierToken;
import plugin.lsttokens.ChooseLst;

import org.junit.jupiter.api.Test;

public abstract class AbstractPCQualifierTokenTestCase<T extends CDOMObject>
        extends AbstractQualifierTokenTestCase<CDOMObject, T>
{

    private static ChooseLst token = new ChooseLst();
    private static CDOMTokenLoader<CDOMObject> loader =
            new CDOMTokenLoader<>();

    protected AbstractPCQualifierTokenTestCase()
    {
        super("PC", null);
    }

    @Override
    public Class<Race> getCDOMClass()
    {
        return Race.class;
    }

    @Override
    public CDOMLoader<CDOMObject> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<CDOMObject> getToken()
    {
        return token;
    }

    @Override
    protected boolean allowsNotQualifier()
    {
        return true;
    }

    private T wp1, wp2, wp3;

    @Test
    public void testGetSet()
    {
        setUpPC();
        initializeObjects();
        assertTrue(parse(getSubTokenName() + "|PC"));
        finishLoad();
        TransparentPlayerCharacter pc = new TransparentPlayerCharacter();

        ChooseInformation<?> info = primaryProf.get(ObjectKey.CHOOSE_INFO);
        Collection<?> set = info.getSet(pc);
        assertTrue(set.isEmpty());
        addToPCSet(pc, wp1);
        set = info.getSet(pc);
        assertFalse(set.isEmpty());
        assertEquals(1, set.size());
        assertEquals(wp1, set.iterator().next());
        addToPCSet(pc, wp2);
        set = info.getSet(pc);
        assertFalse(set.isEmpty());
        if (typeAllowsMult())
        {
            assertEquals(2, set.size());
            assertTrue(set.contains(wp1));
            assertTrue(set.contains(wp2));
        } else
        {
            assertEquals(1, set.size());
            assertTrue(set.contains(wp2));
        }
    }

    @Override
    public CDOMSecondaryToken<?> getSubToken()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<T> getTargetClass()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Class<? extends QualifierToken> getQualifierClass()
    {
        // TODO Auto-generated method stub
        return null;
    }

    protected boolean typeAllowsMult()
    {
        return true;
    }

    @Test
    public void testGetSetFiltered()
    {
        setUpPC();
        initializeObjects();
        assertTrue(parse(getSubTokenName() + "|PC[TYPE=Masterful]"));
        finishLoad();
        TransparentPlayerCharacter pc = new TransparentPlayerCharacter();

        ChooseInformation<?> info = primaryProf.get(ObjectKey.CHOOSE_INFO);
        Collection<?> set = info.getSet(pc);
        assertTrue(set.isEmpty());
        addToPCSet(pc, wp1);
        addToPCSet(pc, wp2);
        set = info.getSet(pc);
        assertFalse(set.isEmpty());
        assertEquals(1, set.size());
        assertEquals(wp2, set.iterator().next());
    }

    @Test
    public void testGetSetNegated()
    {
        setUpPC();
        initializeObjects();
        assertTrue(parse(getSubTokenName() + "|!PC[TYPE=Masterful]"));
        finishLoad();
        TransparentPlayerCharacter pc = new TransparentPlayerCharacter();

        ChooseInformation<?> info = primaryProf.get(ObjectKey.CHOOSE_INFO);
        Collection<?> set = info.getSet(pc);
        assertFalse(set.isEmpty());
        assertEquals(2, set.size());
        assertTrue(set.contains(wp2));
        assertTrue(set.contains(wp3));
        addToPCSet(pc, wp1);
        addToPCSet(pc, wp2);
        set = info.getSet(pc);
        assertFalse(set.isEmpty());
        assertEquals(1, set.size());
        assertTrue(set.contains(wp3));
    }

    protected abstract void addToPCSet(TransparentPlayerCharacter pc, T item);

    private void initializeObjects()
    {
        wp1 = (T) construct(primaryContext, "Eq1");
        primaryContext.unconditionallyProcess(wp1, "TYPE", "Boring");
        primaryContext.getReferenceContext().importObject(wp1);

        wp2 = (T) construct(primaryContext, "Wp2");
        primaryContext.unconditionallyProcess(wp2, "TYPE", "Masterful");
        primaryContext.getReferenceContext().importObject(wp2);

        wp3 = (T) construct(primaryContext, "Wp3");
        primaryContext.unconditionallyProcess(wp3, "TYPE", "Masterful");
        primaryContext.getReferenceContext().importObject(wp3);
    }
}
