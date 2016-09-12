/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
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

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * SimpleObjectWrapper is an advanced form of wrapper serving the Freemarker
 * Template Engine.
 */
@FunctionalInterface
public interface SimpleObjectWrapper
{
	/**
	 * Wrap the given object into a TemplateModel.
	 * 
	 * @param obj
	 *            The object to be wrapped to a TemplateModel
	 * @return a TemplateModel that wraps the given object
	 * @throws TemplateModelException
	 *             If this Wrapper does not support wrapping the given object
	 */
	TemplateModel wrap(Object obj) throws TemplateModelException;
}
