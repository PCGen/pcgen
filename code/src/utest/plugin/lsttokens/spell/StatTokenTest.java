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
package plugin.lsttokens.spell;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URISyntaxException;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PCStat;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StatTokenTest extends AbstractCDOMTokenTestCase<Spell>
{

    static StatToken token = new StatToken();
    static CDOMTokenLoader<Spell> loader = new CDOMTokenLoader<>();
    private PCStat ps;

    @Override
    @BeforeEach
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        ps = BuildUtilities.createStat("Strength", "STR");
        primaryContext.getReferenceContext().importObject(ps);
        PCStat pi = BuildUtilities.createStat("Intelligence", "INT");
        primaryContext.getReferenceContext().importObject(pi);
        PCStat ss = BuildUtilities.createStat("Strength", "STR");
        secondaryContext.getReferenceContext().importObject(ss);
        PCStat si = BuildUtilities.createStat("Intelligence", "INT");
        secondaryContext.getReferenceContext().importObject(si);
    }

    @Override
    public Class<Spell> getCDOMClass()
    {
        return Spell.class;
    }

    @Override
    public CDOMLoader<Spell> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<Spell> getToken()
    {
        return token;
    }

    @Test
    public void testInvalidNotAStat()
    {
        if (parse("NAN"))
        {
            assertFalse(primaryContext.getReferenceContext().resolveReferences(null));
        } else
        {
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidMultipleStatComma()
    {
        if (parse("STR,INT"))
        {
            assertFalse(primaryContext.getReferenceContext().resolveReferences(null));
        } else
        {
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidMultipleStatBar()
    {
        if (parse("STR|INT"))
        {
            assertFalse(primaryContext.getReferenceContext().resolveReferences(null));
        } else
        {
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidMultipleStatDot()
    {
        if (parse("STR.INT"))
        {
            assertFalse(primaryContext.getReferenceContext().resolveReferences(null));
        } else
        {
            assertNoSideEffects();
        }
    }

    @Test
    public void testRoundRobinDisplay() throws PersistenceLayerException
    {
        runRoundRobin("STR");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "STR";
    }

    @Override
    protected String getLegalValue()
    {
        return "INT";
    }

    @Test
    public void testUnparseNull()
    {
        primaryProf.put(getObjectKey(), null);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    private static ObjectKey<CDOMSingleRef<PCStat>> getObjectKey()
    {
        return ObjectKey.SPELL_STAT;
    }

    @Test
    public void testUnparseLegal()
    {
        primaryProf.put(getObjectKey(), CDOMDirectSingleRef.getRef(ps));
        expectSingle(getToken().unparse(primaryContext, primaryProf), ps.getKeyName());
    }

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
            //Yep!
        }
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }
}
