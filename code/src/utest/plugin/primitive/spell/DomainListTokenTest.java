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
package plugin.primitive.spell;

import java.net.URISyntaxException;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.Race;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import plugin.lsttokens.ChooseLst;
import plugin.lsttokens.choose.SpellsToken;
import plugin.lsttokens.testsupport.AbstractPrimitiveTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.BeforeEach;

public class DomainListTokenTest extends
        AbstractPrimitiveTokenTestCase<CDOMObject, DomainSpellList>
{
    static ChooseLst token = new ChooseLst();
    static SpellsToken subtoken = new SpellsToken();
    static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<>();

    private static final DomainListToken DOMAINLIST_TOKEN = new DomainListToken();

    public DomainListTokenTest()
    {
        super("DOMAINLIST", "SampleList");
    }

    @BeforeEach
    @Override
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        TokenRegistration.register(DOMAINLIST_TOKEN);
        primaryContext.getReferenceContext().constructNowIfNecessary(Spell.class, "Placeholder");
        secondaryContext.getReferenceContext().constructNowIfNecessary(Spell.class, "Placeholder");
    }

    @Override
    public CDOMSecondaryToken<?> getSubToken()
    {
        return subtoken;
    }

    @Override
    public Class<DomainSpellList> getTargetClass()
    {
        return DomainSpellList.class;
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

    public void testPrimitiveIllegalNullTarget()
    {
        doPrimitiveIllegalTarget(null);
    }

    public void testPrimitiveIllegalNoTarget()
    {
        doPrimitiveIllegalTarget("");
    }

    public void testPrimitiveIllegalBadArgs()
    {
        doPrimitiveIllegalTarget("Foo[Hi]");
    }

    public void testPrimitiveIllegalBadKnownEquals()
    {
        doPrimitiveIllegalTarget("Foo[KNOWN=]");
    }

    public void testPrimitiveIllegalBadKnownEqualsBad()
    {
        doPrimitiveIllegalTarget("Foo[KNOWN=Bad]");
    }

    public void testPrimitiveIllegalBadLevelMax()
    {
        doPrimitiveIllegalTarget("Foo[LEVELMAX]");
    }

    public void testPrimitiveIllegalBadLevelMaxEquals()
    {
        doPrimitiveIllegalTarget("Foo[LEVELMAX=]");
    }

    public void testPrimitiveIllegalBadLevelMaxEqualsBad()
    {
        doPrimitiveIllegalTarget("Foo[LEVELMAX=3-]");
    }

    public void testPrimitiveIllegalBadLevelMin()
    {
        doPrimitiveIllegalTarget("Foo[LEVELMIN]");
    }

    public void testPrimitiveIllegalBadLevelMinEquals()
    {
        doPrimitiveIllegalTarget("Foo[LEVELMIN=]");
    }

    public void testPrimitiveIllegalBadLevelMinEqualsBad()
    {
        doPrimitiveIllegalTarget("Foo[LEVELMIN=3+]");
    }
}
