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
package plugin.lsttokens.pcclass;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

public class ExClassTokenTest extends AbstractCDOMTokenTestCase<PCClass>
{

    static ExclassToken token = new ExclassToken();
    static CDOMTokenLoader<PCClass> loader =
            new CDOMTokenLoader<>();

    @Override
    public Class<PCClass> getCDOMClass()
    {
        return PCClass.class;
    }

    @Override
    public CDOMLoader<PCClass> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<PCClass> getToken()
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
    public void testRoundRobinBase() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Rheinhessen");
        secondaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Rheinhessen");
        runRoundRobin("Rheinhessen");
    }

    @Test
    public void testRoundRobinWithSpace() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Finger Lakes");
        secondaryContext.getReferenceContext()
                .constructCDOMObject(getCDOMClass(), "Finger Lakes");
        runRoundRobin("Finger Lakes");
    }

    @Test
    public void testRoundRobinNonEnglishAndN() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
                "Niederösterreich");
        secondaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
                "Niederösterreich");
        runRoundRobin("Niederösterreich");
    }

    @Test
    public void testRoundRobinHyphen() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
                "Languedoc-Roussillon");
        secondaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
                "Languedoc-Roussillon");
        runRoundRobin("Languedoc-Roussillon");
    }

    @Test
    public void testRoundRobinY() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Yarra Valley");
        secondaryContext.getReferenceContext()
                .constructCDOMObject(getCDOMClass(), "Yarra Valley");
        runRoundRobin("Yarra Valley");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "Languedoc-Roussillon";
    }

    @Override
    protected String getLegalValue()
    {
        return "Yarra Valley";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }

    @Test
    public void testUnparseNull()
    {
        primaryProf.put(getObjectKey(), null);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    private static ObjectKey<CDOMSingleRef<PCClass>> getObjectKey()
    {
        return ObjectKey.EX_CLASS;
    }

    @Test
    public void testUnparseLegal()
    {
        primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, getLegalValue());
        CDOMSingleRef<PCClass> cl = primaryContext.getReferenceContext().getCDOMReference(
                PCClass.class, getLegalValue());
        primaryProf.put(getObjectKey(), cl);
        expectSingle(getToken().unparse(primaryContext, primaryProf), getLegalValue());
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
}
