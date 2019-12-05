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
package actor.choose;

import static org.junit.jupiter.api.Assertions.assertEquals;

import pcgen.cdom.base.CategorizedChooser;
import pcgen.core.Ability;
import pcgen.core.Globals;
import pcgen.rules.context.LoadContext;
import plugin.lsttokens.choose.AbilityToken;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The Class {@code AbilityTokenTest} verifies the AbilityToken
 * class is working correctly.
 */
public class AbilityTokenTest
{

    private static final CategorizedChooser<Ability> PCA = new AbilityToken();
    private static final String ITEM_NAME = "ItemName";

    private LoadContext context;

    @BeforeEach
    public void setUp()
    {
        Globals.emptyLists();
        context = Globals.getContext();
        context.getReferenceContext().importObject(BuildUtilities.getFeatCat());
    }

    @AfterEach
    public void tearDown()
    {
        Globals.emptyLists();
        context = null;
    }

    private Ability getObject()
    {
        Ability a = BuildUtilities.getFeatCat().newInstance();
        a.setName(ITEM_NAME);
        context.getReferenceContext().importObject(a);
        return a;
    }

    @Test
    public void testEncodeChoice()
    {
        assertEquals(getExpected(), PCA.encodeChoice(getObject()));
    }

    protected String getExpected()
    {
        return ITEM_NAME;
    }

    @Test
    public void testDecodeChoice()
    {
        assertEquals(getObject(),
                PCA.decodeChoice(context, getExpected(), BuildUtilities.getFeatCat()));
    }

    @Test
    public void testLegacyDecodeChoice()
    {
        assertEquals(getObject(), PCA.decodeChoice(context, "CATEGORY=FEAT|" + ITEM_NAME,
                BuildUtilities.getFeatCat()));
    }

}
