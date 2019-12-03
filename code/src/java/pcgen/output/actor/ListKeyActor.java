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
package pcgen.output.actor;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.output.base.OutputActor;
import pcgen.output.model.CollectionModel;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * A ListKeyActor is designed to process an interpolation and convert that into
 * a TemplateModel representing the contents of the specific List being
 * requested by its ListKey
 * 
 * Note that the actual name of the interpolation is stored externally to this
 * Actor (in CDOMObjectWrapperInfo to be precise)
 */
public class ListKeyActor implements OutputActor<CDOMObject>
{
	/**
	 * The ListKey underlying this ListKeyActor (for which the contents will be
	 * returned)
	 */
	private final ListKey<?> lk;

	/**
	 * Constructs a new ListKeyActor with the given ListKey
	 * 
	 * @param lk
	 *            The ListKey underlying this ListKeyActor
	 */
	public ListKeyActor(ListKey<?> lk)
	{
		this.lk = lk;
	}

	@Override
	public TemplateModel process(CharID id, CDOMObject d) {
		//Our own ListModel so that we end up wrapping subcontents on "our terms"
		return new CollectionModel(id, d.getSafeListFor(lk));
	}
}
