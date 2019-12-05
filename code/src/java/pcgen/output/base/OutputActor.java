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
package pcgen.output.base;

import pcgen.cdom.enumeration.CharID;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * An OutputActor is designed to process an interpolation and convert that into
 * a TemplateModel representing the contents of the specific item being
 * requested.
 * <p>
 * Note that the actual name of the interpolation is stored externally to this
 * OutputActor
 *
 * @param <T> The type of object that this OutputActor can process
 */
public interface OutputActor<T>
{
    /**
     * Processes this OutputActor on the given object in order to produce a
     * TemplateModel of a subset of the contents of the object (the subset is
     * defined by other information possessed by the OutputActor).
     *
     * @param id  The CharID for which the TemplateModel of some of the contents
     *            should be processed
     * @param obj The object for which the TemplateModel of some of the contents
     *            should be produced
     * @return A subset of the contents of the object (the subset is defined by
     * other information possessed by the OutputActor)
     * @throws TemplateModelException if turning the contents into a TemplateModel encounters a
     *                                problem
     */
    TemplateModel process(CharID id, T obj) throws TemplateModelException;
}
