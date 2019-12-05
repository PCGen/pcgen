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
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.spell.Spell;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractStringTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.Test;

public class TargetAreaTokenTest extends AbstractStringTokenTestCase<Spell>
{

    static TargetareaToken token = new TargetareaToken();
    static CDOMTokenLoader<Spell> loader = new CDOMTokenLoader<>();

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

    @Override
    public StringKey getStringKey()
    {
        return StringKey.TARGET_AREA;
    }

    @Override
    protected boolean isClearLegal()
    {
        return true;
    }

    @Test
    public void testGoodParentheses()
    {
        assertTrue(parse("(first)"));
    }

    @Test
    public void testBadParentheses()
    {
        assertFalse(parse("(first"), "Missing end paren should have been flagged.");
        assertFalse(parse("first)"), "Missing start paren should have been flagged.");
        assertFalse(parse("(fir)st)"), "Missing start paren should have been flagged.");
        assertFalse(parse(")(fir(st)"), "Out of order parens should have been flagged.");
    }

    /*
     * TODO Need to figure out ownership of this responsibility
     */
    // @Test
    // public void testUnparseBadParens() throws PersistenceLayerException
    // {
    // primaryProf.addToListFor(getListKey(), "(first");
    // assertBadUnparse();
    //	}

}
