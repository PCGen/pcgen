/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.choose;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.SubClassCategory;
import pcgen.core.PCClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractQualifiedChooseToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * New chooser plugin, handles PC class.
 */
public class ClassToken extends AbstractQualifiedChooseToken<PCClass>
{

    private static final Class<PCClass> PCCLASS_CLASS = PCClass.class;

    @Override
    public String getTokenName()
    {
        return "CLASS";
    }

    @Override
    public String getParentToken()
    {
        return "CHOOSE";
    }

    @Override
    protected String getDefaultTitle()
    {
        return "PCClass choice";
    }

    @Override
    public PCClass decodeChoice(LoadContext context, String s)
    {
        int dotLoc = s.indexOf('.');
        if (dotLoc == -1)
        {
            // Primitive
            return context.getReferenceContext().silentlyGetConstructedCDOMObject(PCCLASS_CLASS, s);
        }

        // SubClass
        String parent = s.substring(0, dotLoc);
        String subclass = s.substring(dotLoc + 1);
        SubClassCategory scc = SubClassCategory.getConstant(parent);
        return context.getReferenceContext().getManufacturerId(scc).getActiveObject(subclass);
    }

    @Override
    public String encodeChoice(PCClass choice)
    {
        return choice.getFullKey();
    }

    @Override
    protected AssociationListKey<PCClass> getListKey()
    {
        return AssociationListKey.getKeyFor(PCCLASS_CLASS, "CHOOSE*PCCLASS");
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, CDOMObject obj, String value)
    {
        return super.parseTokenWithSeparator(context, context.getReferenceContext().getManufacturer(PCCLASS_CLASS), obj,
                value);
    }

    @Override
    protected String getPersistentFormat()
    {
        /*
         * TODO This is not entirely true, as this is not correct for SubClasses... :/
         */
        return "PCCLASS";
    }
}
