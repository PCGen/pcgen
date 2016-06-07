/*
 * DescActor.java
 * Copyright 2016 Connor Petty <cpmeister@users.sourceforge.net>
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
 * 
 * Created on Jun 6, 2016, 11:58:14 PM
 */
package pcgen.output.actor;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.output.base.OutputActor;
import pcgen.output.model.InfoModel;

/**
 * An DescActor is designed to act as a shortcut for the 'info.desc'
 * in freemarker.
 */
public class DescActor implements OutputActor<CDOMObject>
{
	/**
	 * @see pcgen.output.base.OutputActor#process(pcgen.cdom.enumeration.CharID,
	 *      java.lang.Object)
	 */
	@Override
	public TemplateModel process(CharID id, CDOMObject d)
		throws TemplateModelException
	{
		return new InfoModel(id, d).get("desc");
	}
}