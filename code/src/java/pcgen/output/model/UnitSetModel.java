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

import pcgen.core.UnitSet;
import pcgen.output.base.SimpleWrapperLibrary;

import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

/**
 * An UnitSetModel wraps a UnitSet and serves as a TemplateHashModel for that
 * UnitSet.
 */
public class UnitSetModel implements TemplateHashModel, TemplateScalarModel
{

	/**
	 * The underlying UnitSet.
	 */
	private final UnitSet unitSet;

	/**
	 * Constructs a new UnitSetModel from the given UnitSet.
	 * 
	 * @param set
	 *            The underlying UnitSet
	 */
	public UnitSetModel(UnitSet set)
	{
		Objects.requireNonNull(set, "UnitSet may not be null");
		this.unitSet = set;
	}

	@Override
	public TemplateModel get(String key) throws TemplateModelException
	{
		String unit;
		if ("height".equals(key))
		{
			unit = unitSet.getHeightUnit();
		}
		else if ("distance".equals(key))
		{
			unit = unitSet.getDistanceUnit();
		}
		else if ("weight".equals(key))
		{
			unit = unitSet.getWeightUnit();
		}
		else
		{
			throw new TemplateModelException("UnitSet did not have output of type " + key);
		}
		return SimpleWrapperLibrary.wrap(unit);
	}

	@Override
	public boolean isEmpty()
	{
		return false;
	}

	@Override
	public String getAsString()
	{
		return unitSet.getDisplayName();
	}
}
