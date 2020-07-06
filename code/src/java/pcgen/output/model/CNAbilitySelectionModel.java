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
package pcgen.output.model;

import java.util.Objects;

import pcgen.cdom.base.Category;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.ObjectWrapperFacet;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.core.Ability;

import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * A CNAbilitySelectionModel is a TemplateHashModel that wraps a
 * CNAbilitySelection object
 */
public class CNAbilitySelectionModel implements TemplateHashModel
{
	private static final ObjectWrapperFacet WRAPPER_FACET = FacetLibrary.getFacet(ObjectWrapperFacet.class);

	/**
	 * The underlying CNAbilitySelection for this CNAbilitySelectionModel
	 */
	private final CNAbilitySelection cnas;

	private final CharID id;

	/**
	 * Constructs a new CNAbilitySelectionModel with the given
	 * CNAbilitySelection as the underlying information.
	 * 
	 * @param cnas
	 *            The CNAbilitySelection that underlies this
	 *            CNAbilitySelectionModel
	 */
	public CNAbilitySelectionModel(CharID id, CNAbilitySelection cnas)
	{
		Objects.requireNonNull(id, "CharID cannot be null");
		Objects.requireNonNull(cnas, "CNAbilitySelection cannot be null");
		this.id = id;
		this.cnas = cnas;
	}

	/**
	 * Acts as a hash for producing the contents of this model.
	 * 
	 * Four items are supported: category, nature, ability and selection.
	 */
	@Override
	public TemplateModel get(String key) throws TemplateModelException
	{
		Object towrap;
		if ("pool".equals(key))
		{
			towrap = cnas.getCNAbility().getAbilityCategory();
		}
		else if ("category".equals(key))
		{
			Category<Ability> cat = cnas.getCNAbility().getAbilityCategory();
			Category<Ability> parent = cat.getParentCategory();
			towrap = (parent == null) ? cat : parent;
		}
		else if ("nature".equals(key))
		{
			towrap = cnas.getCNAbility().getNature();
		}
		else if ("ability".equals(key))
		{
			towrap = cnas.getCNAbility().getAbility();
		}
		else if ("selection".equals(key))
		{
			towrap = cnas.getSelection();
		}
		else
		{
			throw new TemplateModelException("CNAbilitySelection did not have output of type " + key);
		}
		return WRAPPER_FACET.wrap(id, towrap);
	}

	@Override
	public boolean isEmpty()
	{
		return false;
	}
}
