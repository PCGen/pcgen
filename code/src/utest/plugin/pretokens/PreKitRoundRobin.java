/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.pretokens;

import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreKitParser;
import plugin.pretokens.writer.PreKitWriter;

import org.junit.jupiter.api.BeforeEach;

/**
 * PreKitRoundRobin test the parsing and unparsing of PREKIT tags.
 */
public class PreKitRoundRobin extends AbstractBasicRoundRobin
{
    @BeforeEach
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        TokenRegistration.register(new PreKitParser());
        TokenRegistration.register(new PreKitWriter());
    }

    @Override
    public String getBaseString()
    {
        return "KIT";
    }

    @Override
    public boolean isTypeAllowed()
    {
        return false;
    }

}
