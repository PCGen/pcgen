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

import java.util.Objects;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.PCStringKey;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.ObjectWrapperFacet;
import pcgen.cdom.facet.fact.FactFacet;

import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * An FactFacetModel wraps a FactFacet and serves as a TemplateHashModel for
 * that FactFacet (with a given CharID).
 */
public class FactFacetModel implements TemplateHashModel
{

	private static final ObjectWrapperFacet WRAPPER_FACET = FacetLibrary.getFacet(ObjectWrapperFacet.class);

	/**
	 * The underlying CharID used to get the PlayerCharacter's item from the
	 * underlying FactFacet.
	 */
	private final CharID id;

	/**
	 * The underlying FactFacet used to get information about the
	 * PlayerCharacter.
	 */
	private final FactFacet facet;

	/**
	 * Constructs a new FactFacetModel from the given CharID and FactFacet.
	 * 
	 * @param id
	 *            The underlying CharID used to get the PlayerCharacter's info
	 *            from the given FactFacet
	 * @param facet
	 *            The underlying FactFacet used to get information about the
	 *            PlayerCharacter
	 */
	public FactFacetModel(CharID id, FactFacet facet)
	{
		Objects.requireNonNull(id, "CharID may not be null");
		Objects.requireNonNull(facet, "FactFacet may not be null");
		this.id = id;
		this.facet = facet;
	}

	@Override
	public TemplateModel get(String arg0) throws TemplateModelException
	{
		PCStringKey key = PCStringKey.getStringKey(arg0);
		return WRAPPER_FACET.wrap(id, facet.get(id, key));
	}

	@Override
	public boolean isEmpty()
	{
		return false;
	}
}
