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
package pcgen.output.base;

import pcgen.cdom.enumeration.CharID;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * PCGenObjectWrapper is an advanced form of wrapper serving the Freemarker
 * Template Engine.
 * <p>
 * This can wrap an object based on the object and the given CharID. The CharID
 * is used to resolve any character specific information necessary to
 * appropriate wrap an object.
 */
public interface PCGenObjectWrapper
{
    /**
     * Wrap the given object into a TemplateModel, using the given CharID if
     * necessary.
     *
     * @param id  The CharID of the active Player Character, to be used by the
     *            Wrapper if necessary
     * @param obj The object to be wrapped to a TemplateModel
     * @return a TemplateModel that wraps the given object
     * @throws TemplateModelException If this Wrapper does not support wrapping the given object
     */
    TemplateModel wrap(CharID id, Object obj) throws TemplateModelException;
}
