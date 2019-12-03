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
package pcgen.output.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import pcgen.cdom.base.SetFacet;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.ObjectWrapperFacet;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;

/**
 * A SetFacetModel wraps a SetFacet and serves as a TemplateSequenceModel for
 * that SetFacet (with a given CharID)
 * 
 * @param <T>
 *            The Type of object contained in the SetFacet contained by this
 *            SetFacetModel
 */
public class SetFacetModel<T> implements TemplateSequenceModel, Iterable<T>
{
	//Make sure to use PCGen's Wrappers since we don't know the underlying type
	private static final ObjectWrapperFacet WRAPPER_FACET = FacetLibrary.getFacet(ObjectWrapperFacet.class);

	/**
	 * The underlying CharID used to get items from the underlying SetFacet
	 */
	private final CharID id;

	/**
	 * The underlying SetFacet used to get information about the PlayerCharacter
	 */
	private final SetFacet<CharID, T> facet;

	/**
	 * Constructs a new SetFacetModel from the given CharID and SetFacet
	 * 
	 * @param id
	 *            The underlying CharID used to get items from the given
	 *            SetFacet
	 * @param facet
	 *            The underlying SetFacet used to get information about the
	 *            PlayerCharacter
	 */
	public SetFacetModel(CharID id, SetFacet<CharID, T> facet)
	{
		Objects.requireNonNull(id, "CharID may not be null");
		Objects.requireNonNull(facet, "SetFacet may not be null");
		this.id = id;
		this.facet = facet;
	}

	@Override
	public Iterator<T> iterator()
	{
		return facet.getSet(id).iterator();
	}

	@Override
	public TemplateModel get(int index) throws TemplateModelException
	{
		if (index < 0)
		{
			return null;
		}
		ArrayList<T> list = new ArrayList<>(facet.getSet(id));
		if (index >= list.size())
		{
			return null;
		}
		return WRAPPER_FACET.wrap(id, list.get(index));
	}

	@Override
	public int size() {
		return facet.getCount(id);
	}

}
