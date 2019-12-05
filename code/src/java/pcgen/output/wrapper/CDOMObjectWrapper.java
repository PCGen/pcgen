/*
 * Copyright 2014-15 (C) Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.facet.CDOMWrapperInfoFacet;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.output.base.OutputActor;
import pcgen.output.base.PCGenObjectWrapper;
import pcgen.output.model.CDOMObjectModel;

import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;

/**
 * A CategoryWrapper is an ObjectWrapper capable of producing a TemplateModel
 * for CDOMObject objects.
 */
public final class CDOMObjectWrapper implements PCGenObjectWrapper
{

    private static final CDOMWrapperInfoFacet WRAPPER_FACET = FacetLibrary.getFacet(CDOMWrapperInfoFacet.class);

    /**
     * Loads a new OutputActor into the CDOMObjectWrapperInfo for the given
     * class.
     * <p>
     * If the CDOMObjectWrapperInfo already has an OutputActor for the given
     * name, then this method will not add the given OutputActor and will return
     * false.
     *
     * @param cl   The Class for which the given OutputActor will be loaded
     * @param name The name of the interpolation to be used for the given
     *             OutputActor
     * @param oa   The OutputActor to be loaded into the CDOMObjectWrapperInfo
     * @return true if the given name and OutputActor were successfully loaded;
     * false otherwise
     */
    public static <T extends CDOMObject> boolean load(DataSetID id, Class<T> cl, String name,
            OutputActor<CDOMObject> oa)
    {
        return WRAPPER_FACET.set(id, cl, name, oa);
    }

    @Override
    public TemplateHashModel wrap(CharID id, Object o) throws TemplateModelException
    {
        if (o instanceof CDOMObject)
        {
            return new CDOMObjectModel(id, (CDOMObject) o);
        }
        throw new TemplateModelException("Object was not a CDOMObject");
    }
}
