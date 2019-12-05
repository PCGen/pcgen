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
package plugin.lsttokens.kit;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.net.URISyntaxException;

import pcgen.core.PCAlignment;
import pcgen.core.kit.KitAlignment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMSubLineLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractKitTokenTestCase;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AlignTokenTest extends AbstractKitTokenTestCase<KitAlignment>
{

    static AlignToken token = new AlignToken();
    static CDOMSubLineLoader<KitAlignment> loader = new CDOMSubLineLoader<>(
            "SPELLS", KitAlignment.class);

    @Override
    @BeforeEach
    public final void setUp() throws PersistenceLayerException,
            URISyntaxException
    {
        super.setUp();
        PCAlignment lg = BuildUtilities.createAlignment("Lawful Good", "LG");
        primaryContext.getReferenceContext().importObject(lg);
        PCAlignment ln = BuildUtilities.createAlignment("Lawful Neutral", "LN");
        primaryContext.getReferenceContext().importObject(ln);
        PCAlignment slg = BuildUtilities.createAlignment("Lawful Good", "LG");
        secondaryContext.getReferenceContext().importObject(slg);
        PCAlignment sln = BuildUtilities.createAlignment("Lawful Neutral", "LN");
        secondaryContext.getReferenceContext().importObject(sln);
    }

    @Override
    public Class<KitAlignment> getCDOMClass()
    {
        return KitAlignment.class;
    }

    @Override
    public CDOMSubLineLoader<KitAlignment> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<KitAlignment> getToken()
    {
        return token;
    }

    @Test
    public void testInvalidInputEmptySpellbook()
    {
        if (parse("NoAlign"))
        {
            assertFalse(primaryContext.getReferenceContext().resolveReferences(null));
        }
    }

    @Test
    public void testRoundRobinSimple() throws PersistenceLayerException
    {
        runRoundRobin("LG");
    }

    @Test
    public void testRoundRobinTwo() throws PersistenceLayerException
    {
        runRoundRobin("LG|LN");
    }

}
