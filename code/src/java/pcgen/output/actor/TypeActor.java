/*
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
 */
package pcgen.output.actor;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.output.base.OutputActor;
import pcgen.output.model.CollectionModel;

import freemarker.template.TemplateModel;

/**
 * A TypeActor is designed to process an interpolation and convert the
 types of a CDOMObject into a TemplateModel.
 * 
 * Note that the actual name of the interpolation is stored externally to this
 * Actor (in CDOMObjectWrapperInfo to be precise)
 */
public class TypeActor implements OutputActor<CDOMObject>
{
	@Override
	public TemplateModel process(CharID id, CDOMObject d) {
		final List<Type> types = d.getSafeListFor(ListKey.TYPE);
		List<String> strings = new ArrayList<>(types.size());
		for (Type type : types)
		{
			strings.add(type.toString());
		}
		return new CollectionModel(id, strings);
	}
}
