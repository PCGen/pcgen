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

import pcgen.core.AgeSet;

import freemarker.template.TemplateScalarModel;

/**
 * A AgeSetModel is a TemplateHashModel that wraps a AgeSet object
 */
public class AgeSetModel implements TemplateScalarModel
{
	/**
	 * The underlying AgeSet for this AgeSetModel
	 */
	private final AgeSet set;

	/**
	 * Constructs a new AgeSetModel with the given AgeSet as the underlying
	 * information.
	 * 
	 * @param as
	 *            The AgeSet that underlies this AgeSetModel
	 */
	public AgeSetModel(AgeSet as)
	{
		Objects.requireNonNull(as, "AgeSet cannot be null");
		this.set = as;
	}

	@Override
	public String getAsString()
	{
		return set.getKeyName();
	}

}
