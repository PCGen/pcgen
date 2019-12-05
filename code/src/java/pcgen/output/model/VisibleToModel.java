/*
 * Copyright 2017 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.output.model;

import java.util.List;
import java.util.Objects;

import pcgen.util.enumeration.View;
import pcgen.util.enumeration.Visibility;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

/**
 * VisibleToModel is a TemplateMethod for FreeMarker that takes in a view in order to
 * determine whether an item is visible to that View. The View is received from FreeMarker
 * in String format.
 */
public class VisibleToModel implements TemplateMethodModelEx
{

    /**
     * The underlying Visibility, which will be checked against a view.
     */
    private final Visibility visibility;

    /**
     * Constructs a new VisibleToModel with the given underlying Visibility
     *
     * @param v The Visibility for this VisibleToModel
     */
    public VisibleToModel(Visibility v)
    {
        visibility = Objects.requireNonNull(v);
    }

    @Override
    public Object exec(@SuppressWarnings("rawtypes") List arguments) throws TemplateModelException
    {
        if (arguments.size() != 1)
        {
            throw new TemplateModelException("Expected 1 argument but found: " + arguments.size());
        }
        View v = View.getViewFromName(((SimpleScalar) arguments.get(0)).getAsString());
        return visibility.isVisibleTo(v);
    }

}
