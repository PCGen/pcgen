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
package selectionactor;

import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.core.PCTemplate;
import plugin.lsttokens.TemplateLst;

import selectionactor.testsupport.AbstractSelectionActorTest;

public class TemplateLstTest extends AbstractSelectionActorTest<PCTemplate>
{

    private static final ChooseSelectionActor<PCTemplate> CSA = new TemplateLst();

    @Override
    public ChooseSelectionActor<PCTemplate> getActor()
    {
        return CSA;
    }

    @Override
    public Class<PCTemplate> getCDOMClass()
    {
        return PCTemplate.class;
    }

    @Override
    public boolean isGranted()
    {
        return true;
    }

}
