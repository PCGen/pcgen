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
package plugin.lsttokens.deity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URISyntaxException;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Deity;
import pcgen.core.PCAlignment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AlignTokenTest extends AbstractCDOMTokenTestCase<Deity>
{
    static AlignToken token = new AlignToken();
    static CDOMTokenLoader<Deity> loader = new CDOMTokenLoader<>();
    private PCAlignment lg;

    @Override
    @BeforeEach
    public final void setUp() throws PersistenceLayerException,
            URISyntaxException
    {
        super.setUp();
        lg = BuildUtilities.createAlignment("Lawful Good", "LG");
        primaryContext.getReferenceContext().importObject(lg);
        PCAlignment ln = BuildUtilities.createAlignment("Lawful Neutral", "LN");
        primaryContext.getReferenceContext().importObject(ln);
        PCAlignment slg = BuildUtilities.createAlignment("Lawful Good", "LG");
        secondaryContext.getReferenceContext().importObject(slg);
        PCAlignment sln = BuildUtilities.createAlignment("Lawful Neutral", "LN");
        secondaryContext.getReferenceContext().importObject(sln);
    }

    @Override
    public Class<Deity> getCDOMClass()
    {
        return Deity.class;
    }

    @Override
    public CDOMLoader<Deity> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<Deity> getToken()
    {
        return token;
    }

    public static ObjectKey<?> getObjectKey()
    {
        return ObjectKey.ALIGNMENT;
    }

    @Test
    public void testInvalidEmpty()
    {
        assertFalse(parse(""));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidFormula()
    {
        if (parse("1+3"))
        {
            assertFalse(primaryContext.getReferenceContext().resolveReferences(null));
        } else
        {
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInteger()
    {
        assertFalse(parse("4"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidString()
    {
        if (parse("String"))
        {
            assertFalse(primaryContext.getReferenceContext().resolveReferences(null));
        } else
        {
            assertNoSideEffects();
        }
    }

    @Test
    public void testRoundRobinLG() throws PersistenceLayerException
    {
        runRoundRobin("LG");
    }

    @Test
    public void testRoundRobinLN() throws PersistenceLayerException
    {
        runRoundRobin("LN");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "LG";
    }

    @Override
    protected String getLegalValue()
    {
        return "LN";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }

    @Test
    public void testUnparseNull()
    {
        primaryProf.put(ObjectKey.ALIGNMENT, null);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    @Test
    public void testUnparseLegal()
    {
        primaryProf.put(ObjectKey.ALIGNMENT, CDOMDirectSingleRef.getRef(lg));
        expectSingle(getToken().unparse(primaryContext, primaryProf), lg
                .getKeyName());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUnparseGenericsFail()
    {
        ObjectKey objectKey = ObjectKey.ALIGNMENT;
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
}
