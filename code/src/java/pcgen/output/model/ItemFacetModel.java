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

import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.ItemFacet;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.ObjectWrapperFacet;

import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

/**
 * An ItemFacetModel wraps a ItemFacet and serves as a TemplateHashModel for
 * that ItemFacet (with a given CharID).
 * 
 * @param <T>
 *            The Type of object contained in the ItemFacet contained by this
 *            ItemFacetModel
 */
public class ItemFacetModel<T> implements TemplateHashModel, TemplateScalarModel, Iterable<T>
{
	//Make sure to use PCGen's Wrappers since we don't know the underlying type
	private static final ObjectWrapperFacet WRAPPER_FACET = FacetLibrary.getFacet(ObjectWrapperFacet.class);

	/**
	 * The underlying CharID used to get the PlayerCharacter's item from the
	 * underlying ItemFacet.
	 */
	private final CharID id;

	/**
	 * The underlying ItemFacet used to get information about the
	 * PlayerCharacter.
	 */
	private final ItemFacet<CharID, T> facet;

	/**
	 * A cache of the TemplateHashModel of the underlying object, if any
	 */
	private transient TemplateHashModel cache;

	/**
	 * Whether the cache variable has been set (required because null is legal)
	 */
	private transient boolean cacheSet = false;

	/**
	 * Constructs a new ItemFacetModel from the given CharID and SetFacet.
	 * 
	 * @param id
	 *            The underlying CharID used to get the PlayerCharacter's item
	 *            from the given ItemFacet
	 * @param facet
	 *            The underlying ItemFacet used to get information about the
	 *            PlayerCharacter
	 */
	public ItemFacetModel(CharID id, ItemFacet<CharID, T> facet)
	{
		Objects.requireNonNull(id, "CharID may not be null");
		Objects.requireNonNull(facet, "SetFacet may not be null");
		this.id = id;
		this.facet = facet;
	}

	@Override
	public TemplateModel get(String arg0) throws TemplateModelException
	{
		TemplateHashModel model = getInternalHashModel();
		if (model == null)
		{
			return null;
		}
		return model.get(arg0);
	}

	private TemplateHashModel getInternalHashModel() throws TemplateModelException
	{
		if (!cacheSet)
		{
			T obj = facet.get(id);
			TemplateModel wrapped = WRAPPER_FACET.wrap(id, obj);
			if (wrapped instanceof TemplateHashModel)
			{
				cache = (TemplateHashModel) wrapped;
				cacheSet = true;
			}
		}
		return cache;
	}

	@Override
	public boolean isEmpty() throws TemplateModelException
	{
		return (facet.get(id) == null) || getInternalHashModel().isEmpty();
	}

	@Override
	public String getAsString()
	{
		T obj = facet.get(id);
		if (obj == null)
		{
			return Constants.EMPTY_STRING;
		}
		if (obj instanceof CDOMObject)
		{
			return ((CDOMObject) obj).getDisplayName();
		}
		return obj.toString();
	}

	@Override
	public Iterator<T> iterator()
	{
		return Collections.singleton(facet.get(id)).iterator();
	}

}
