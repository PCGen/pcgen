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
import java.util.function.Supplier;

import pcgen.base.math.OrderedPair;
import pcgen.output.base.SimpleWrapperLibrary;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;

/**
 * A OrderedPairModel wraps a OrderedPair object into a TemplateScalarModel and
 * TemplateSequenceModel (of length 2).
 */
public class OrderedPairModel implements TemplateScalarModel, TemplateSequenceModel, Supplier<OrderedPair>
{

	/**
	 * The underlying OrderedPair object
	 */
	private final OrderedPair point;

	/**
	 * Constructs a new OrderedPairModel with the given underlying OrderedPair
	 * 
	 * @param point The OrderedPair this OrderedPairModel wraps
	 */
	public OrderedPairModel(OrderedPair point)
	{
		Objects.requireNonNull(point, "OrderedPair cannot be null");
		this.point = point;
	}

	@Override
	public String getAsString()
    {
		return point.toString();
	}

	@Override
	public OrderedPair get()
	{
		return point;
	}

	@Override
	public TemplateModel get(int index) throws TemplateModelException
	{
		if (index == 0)
		{
			return SimpleWrapperLibrary.wrap(point.getPreciseX());
		}
		else if (index == 1)
		{
			return SimpleWrapperLibrary.wrap(point.getPreciseY());
		}
		return null;
	}

	@Override
	public int size()
    {
		return 2;
	}

}
