/*
 * Copyright 2015-16 (C) Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.base.math.OrderedPair;
import pcgen.output.base.SimpleObjectWrapper;
import pcgen.output.model.OrderedPairModel;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * An OrderedPairWrapper is an ObjectWrapper capable of producing a
 * TemplateModel for OrderedPair objects.
 */
public class OrderedPairWrapper implements SimpleObjectWrapper
{
    @Override
    public TemplateModel wrap(Object obj) throws TemplateModelException
    {
        if (obj instanceof OrderedPair)
        {
            return new OrderedPairModel((OrderedPair) obj);
        }
        throw new TemplateModelException("Object was not an OrderedPair");
    }
}
