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
import pcgen.output.base.PCGenObjectWrapper;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * SimpleWrapperDelegate is a wrapper around ObjectWrapper.SIMPLE_WRAPPER to
 * allow it to implement the PCGenObjectWrapper interface.
 */
public class SimpleWrapperDelegate implements PCGenObjectWrapper
{

	/**
	 * @see pcgen.output.base.PCGenObjectWrapper#wrap(pcgen.cdom.enumeration.CharID,
	 *      java.lang.Object)
	 */
	@Override
	public TemplateModel wrap(CharID id, Object obj)
		throws TemplateModelException
	{
		return ObjectWrapper.SIMPLE_WRAPPER.wrap(obj);
	}

}
