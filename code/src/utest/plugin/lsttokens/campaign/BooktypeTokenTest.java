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
package plugin.lsttokens.campaign;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTypeSafeListTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class BooktypeTokenTest extends AbstractTypeSafeListTestCase<Campaign, String>
{

    static BooktypeToken token = new BooktypeToken();
    static CDOMTokenLoader<Campaign> loader = new CDOMTokenLoader<>();

    @Override
    public Class<Campaign> getCDOMClass()
    {
        return Campaign.class;
    }

    @Override
    public CDOMLoader<Campaign> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<Campaign> getToken()
    {
        return token;
    }

    @Override
    public String getConstant(String string)
    {
        return string;
    }

    @Override
    public char getJoinCharacter()
    {
        return '|';
    }

    @Override
    public ListKey getListKey()
    {
        return ListKey.BOOK_TYPE;
    }

    @Override
    public boolean isClearDotLegal()
    {
        return false;
    }

    @Override
    public boolean isClearLegal()
    {
        return false;
    }

    @Override
    protected boolean requiresPreconstruction()
    {
        return false;
    }

    @Override
    public void testReplacementInputs()
    {
        //Override because BookType performs a .CLEAR
    }

    @Override
    public void testReplacementInputsTwo()
    {
        //Override because BookType performs a .CLEAR
    }

    @Override
    public void testValidInputMultList()
    {
        //Override because BookType performs a .CLEAR
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        //Override because BookType performs a .CLEAR
        return ConsolidationRule.OVERWRITE;
    }
}

























