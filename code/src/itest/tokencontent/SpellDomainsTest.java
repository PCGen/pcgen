/*
 * Copyright (c) 2012 Tom Parker <thpr@users.sourceforge.net>
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
package tokencontent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.facet.AvailableSpellFacet;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.input.DomainInputFacet;
import pcgen.cdom.helper.ClassSource;
import pcgen.core.Domain;
import pcgen.core.PCClass;
import pcgen.core.spell.Spell;
import pcgen.gui2.facade.MockUIDelegate;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.chooser.ChooserFactory;
import plugin.lsttokens.spell.DomainsToken;

import org.junit.jupiter.api.Test;
import tokenmodel.testsupport.AbstractTokenModelTest;
import util.TestURI;

public class SpellDomainsTest extends AbstractTokenModelTest
{

    private static DomainsToken token = new DomainsToken();
    private Spell sp;
    private PCClass dragon;
    protected AvailableSpellFacet availableSpellFacet = FacetLibrary
            .getFacet(AvailableSpellFacet.class);
    protected DomainInputFacet domainInputFacet = FacetLibrary
            .getFacet(DomainInputFacet.class);
    private Domain domain;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        sp = context.getReferenceContext().constructCDOMObject(Spell.class, "MySpell");
        dragon = context.getReferenceContext().constructCDOMObject(PCClass.class, "Dragon");
        dragon.addToListFor(ListKey.TYPE, Type.MONSTER);
        domain = create(Domain.class, "Source");
        ChooserFactory.setDelegate(new MockUIDelegate());
    }

    @Test
    public void testDirect()
    {
        ParseResult result = token.parseToken(context, sp, "Source=1");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        finishLoad();
        classFacet.addClass(id, dragon);
        classFacet.setLevel(id, dragon, 1);
        domainInputFacet.add(id, domain, new ClassSource(dragon, 0));
        pc.setDirty(true);
        HashMapToList<CDOMList<Spell>, Integer> map = availableSpellFacet.getSpellLevelInfo(id, sp);
        assertTrue(map.containsListFor(domain.get(ObjectKey.DOMAIN_SPELLLIST)));
        assertEquals(1, map.getListFor(domain.get(ObjectKey.DOMAIN_SPELLLIST)).size());
        assertEquals(1, map.getListFor(domain.get(ObjectKey.DOMAIN_SPELLLIST)).get(0).intValue());
    }

    @Override
    public CDOMToken<?> getToken()
    {
        return token;
    }
}
