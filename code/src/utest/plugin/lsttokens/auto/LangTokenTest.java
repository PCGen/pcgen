/*
 *
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
package plugin.lsttokens.auto;

import static org.junit.jupiter.api.Assertions.fail;

import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Language;
import pcgen.core.QualifiedObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import plugin.lsttokens.testsupport.AbstractAutoTokenTestCase;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

public class LangTokenTest extends AbstractAutoTokenTestCase<Language>
{

    static LangToken subtoken = new LangToken();

    @Override
    public CDOMSecondaryToken<?> getSubToken()
    {
        return subtoken;
    }

    @Override
    public Class<Language> getTargetClass()
    {
        return Language.class;
    }

    @Override
    public boolean isAllLegal()
    {
        return true;
    }

    @Override
    protected ChooseSelectionActor<Language> getActor()
    {
        return subtoken;
    }

    @Override
    protected void loadAllReference()
    {
        CDOMGroupRef<Language> ref = primaryContext.getReferenceContext().getCDOMAllReference(Language.class);
        primaryProf.addToListFor(ListKey.AUTO_LANGUAGE, new QualifiedObject<>(ref));
    }

    @Override
    protected void loadProf(CDOMSingleRef<Language> ref)
    {
        primaryProf.addToListFor(ListKey.AUTO_LANGUAGE, new QualifiedObject<>(ref));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testUnparseGenericsFail()
    {
        ListKey listKey = ListKey.AUTO_LANGUAGE;
        primaryProf.addToListFor(listKey, new Object());
        try
        {
            getToken().unparse(primaryContext, primaryProf);
            fail();
        } catch (ClassCastException e)
        {
            // Yep!
        }
    }

    @Override
    protected void loadTypeProf(String... types)
    {
        CDOMGroupRef<Language> ref = primaryContext.getReferenceContext().getCDOMTypeReference(Language.class, types);
        primaryProf.addToListFor(ListKey.AUTO_LANGUAGE, new QualifiedObject<>(ref));
    }

    @Override
    protected boolean allowsPrerequisite()
    {
        return false;
    }

    /*
     * TODO This doesn't alphabetize, which is probably a bad thing... should
     * fix.
     */
    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return strings -> new String[]{"LANG|TestWP1|TestWP2|TestWP1|TestWP2|TestWP3"};
    }

    /**
     * Check the parsing and unparsing of AUTO:LANG with multiple languages and
     * prereqs.
     *
     * @throws PersistenceLayerException Not expected.
     */
    public void testRounfRobinMultWithPrereq() throws PersistenceLayerException
    {
        construct(primaryContext, "Infernal");
        construct(primaryContext, "Celestial");
        construct(secondaryContext, "Infernal");
        construct(secondaryContext, "Celestial");
        runRoundRobin(getSubTokenName() + '|'
                + "Infernal|Celestial|PRERACE:1,Human");
    }
}
