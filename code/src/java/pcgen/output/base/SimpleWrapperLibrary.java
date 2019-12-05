/*
 * Copyright (c) Thomas Parker, 2016.
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
package pcgen.output.base;

import java.util.ArrayList;
import java.util.Collection;

import pcgen.output.wrapper.AgeSetWrapper;
import pcgen.output.wrapper.BooleanWrapper;
import pcgen.output.wrapper.CategoryWrapper;
import pcgen.output.wrapper.EnumWrapper;
import pcgen.output.wrapper.NumberWrapper;
import pcgen.output.wrapper.OrderedPairWrapper;
import pcgen.output.wrapper.StringWrapper;
import pcgen.output.wrapper.TypeSafeConstantWrapper;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * SimpleWrapperLibrary stores information on simple wrappers used to wrap
 * objects into TemplateModel objects for FreeMarker.
 */
public final class SimpleWrapperLibrary
{

    private static final Collection<SimpleObjectWrapper> WRAPPER_LIST = new ArrayList<>();

    static
    {
        WRAPPER_LIST.add(new StringWrapper());
        WRAPPER_LIST.add(new NumberWrapper());
        WRAPPER_LIST.add(new BooleanWrapper());
        WRAPPER_LIST.add(new TypeSafeConstantWrapper());
        WRAPPER_LIST.add(new CategoryWrapper());
        WRAPPER_LIST.add(new EnumWrapper());
        WRAPPER_LIST.add(new OrderedPairWrapper());
        WRAPPER_LIST.add(new AgeSetWrapper());
    }

    private SimpleWrapperLibrary()
    {
    }

    /**
     * Wraps the given object into a TemplateModel, using the ObjectWrapper
     * objects previously added to this SimpleWrapperLibrary.
     * <p>
     * Each ObjectWrapper which has been added to the SimpleWrapperLibrary is
     * given the chance, in the order they were added to the
     * SimpleWrapperLibrary, to wrap the object into a TemplateModel. The
     * results of the first successful ObjectWrapper are returned.
     *
     * @param toWrap The Object to be wrapped
     * @return The TemplateModel produced by an ObjectWrapper contained in this
     * SimpleWrapperLibrary
     * @throws TemplateModelException if no ObjectWrapper in this SimpleWrapperLibrary can wrap the
     *                                given object into a TemplateModel
     */
    public static TemplateModel wrap(Object toWrap) throws TemplateModelException
    {
        if (toWrap == null)
        {
            return null;
        }
        for (SimpleObjectWrapper ow : WRAPPER_LIST)
        {
            try
            {
                return ow.wrap(toWrap);
            } catch (TemplateModelException ignored)
            {
                //No worries, Try the next one
            }
        }
        String info = toWrap.getClass().getCanonicalName();
        throw new TemplateModelException("Unable to find wrapping for " + info);
    }
}
