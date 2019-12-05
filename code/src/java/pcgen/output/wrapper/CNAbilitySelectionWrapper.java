/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.output.wrapper;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.output.base.PCGenObjectWrapper;
import pcgen.output.model.CNAbilitySelectionModel;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * A CNAbilitySelectionWrapper is an ObjectWrapper capable of producing a
 * TemplateModel for CNAbilitySelection objects.
 */
public class CNAbilitySelectionWrapper implements PCGenObjectWrapper
{
    @Override
    public TemplateModel wrap(CharID id, Object o) throws TemplateModelException
    {
        if (o instanceof CNAbilitySelection)
        {
            return new CNAbilitySelectionModel(id, (CNAbilitySelection) o);
        }
        throw new TemplateModelException("Object was not a CNAbilitySelection");
    }
}
