/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens.race;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.net.URISyntaxException;

import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.formula.FixedSizeFormula;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Race;
import pcgen.core.SizeAdjustment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SizeTokenTest extends AbstractCDOMTokenTestCase<Race>
{

    static SizeToken token = new SizeToken();
    static CDOMTokenLoader<Race> loader = new CDOMTokenLoader<>();
    private SizeAdjustment ps;

    @Override
    public Class<Race> getCDOMClass()
    {
        return Race.class;
    }

    @Override
    public CDOMLoader<Race> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<Race> getToken()
    {
        return token;
    }

    @Override
    @BeforeEach
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        ps = BuildUtilities.createSize("S", 0);
        primaryContext.getReferenceContext().importObject(ps);
        SizeAdjustment pm = BuildUtilities.createSize("M", 1);
        primaryContext.getReferenceContext().importObject(pm);
        SizeAdjustment ss = BuildUtilities.createSize("S", 0);
        secondaryContext.getReferenceContext().importObject(ss);
        SizeAdjustment sm = BuildUtilities.createSize("M", 1);
        secondaryContext.getReferenceContext().importObject(sm);
    }

    @Test
    public void testInvalidNotASize()
    {
        if (token.parseToken(primaryContext, primaryProf, "W").passed())
        {
            assertFalse(primaryContext.getReferenceContext().resolveReferences(null));
        } else
        {
            assertNoSideEffects();
        }
    }

    @Test
    public void testRoundRobinS() throws PersistenceLayerException
    {
        runRoundRobin("S");
    }

    @Test
    public void testRoundRobinM() throws PersistenceLayerException
    {
        runRoundRobin("M");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "S";
    }

    @Override
    protected String getLegalValue()
    {
        return "M";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }

    @Test
    public void testUnparseNull()
    {
        primaryProf.put(FormulaKey.SIZE, null);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    @Test
    public void testUnparseLegal()
    {
        FixedSizeFormula fsf = new FixedSizeFormula(CDOMDirectSingleRef.getRef(ps));
        primaryProf.put(FormulaKey.SIZE, fsf);
        expectSingle(getToken().unparse(primaryContext, primaryProf), ps
                .getKeyName());
    }

    /*
     * TODO Need to have this as someone's responsibility to check...
     */
    // @Test
    // public void testUnparseIllegal() throws PersistenceLayerException
    // {
    // Formula f = FormulaFactory.getFormulaFor(1);
    // primaryProf.put(FormulaKey.SIZE, f);
    // assertBadUnparse();
    // }
}
