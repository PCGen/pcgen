/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
 */
package pcgen.rules.persistence.token;

import java.util.Collection;
import java.util.logging.Level;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Converter;
import pcgen.cdom.base.PrimitiveCollection;
import pcgen.cdom.base.PrimitiveFilter;
import pcgen.cdom.converter.AddFilterConverter;
import pcgen.cdom.converter.NegateFilterConverter;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.reference.SelectionCreator;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * An AbstractPCQualifierToken is a QualifierToken that implements the "PC"
 * qualifier for a specific type of target object.
 * 
 * @param <T>
 *            The Type of object on which this AbstractPCQualifierToken operates
 */
public abstract class AbstractPCQualifierToken<T extends CDOMObject> implements QualifierToken<T>, PrimitiveFilter<T>
{

	private Class<T> refClass;

	private PrimitiveCollection<T> pcs = null;

	private boolean wasRestricted = false;

	private boolean negated = false;

	@Override
	public String getTokenName()
	{
		return "PC";
	}

	@Override
	public boolean initialize(LoadContext context, SelectionCreator<T> sc, String condition, String value,
		boolean negate)
	{
		if (condition != null)
		{
			Logging.addParseMessage(Level.SEVERE,
				"Cannot make " + getTokenName() + " into a conditional Qualifier, remove =");
			return false;
		}
		if (sc == null)
		{
			throw new IllegalArgumentException();
		}
		refClass = sc.getReferenceClass();
		negated = negate;
		if (value == null)
		{
			pcs = sc.getAllReference();
		}
		else
		{
			pcs = context.getPrimitiveChoiceFilter(sc, value);
			wasRestricted = true;
		}
		return pcs != null;
	}

	protected abstract Collection<T> getPossessed(PlayerCharacter pc);

	@Override
	public String getLSTformat(boolean useAny)
	{
		StringBuilder sb = new StringBuilder();
		if (negated)
		{
			sb.append('!');
		}
		sb.append(getTokenName());
		if (wasRestricted)
		{
			sb.append('[').append(pcs.getLSTformat(useAny)).append(']');
		}
		return sb.toString();
	}

	@Override
	public int hashCode()
	{
		return pcs == null ? 0 : pcs.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof AbstractPCQualifierToken<?> other)
		{
			if (pcs == null)
			{
				if (other.pcs != null)
				{
					return false;
				}
			}
			else
			{
				if (!pcs.equals(other.pcs))
				{
					return false;
				}
			}
			if (refClass == null)
			{
				if (other.refClass != null)
				{
					return false;
				}
				// Required so PC qualifiers of different classes are not
				// accidentally matched during token library initialization
				return getClass().equals(other.getClass());
			}
			else
			{
				if (!refClass.equals(other.refClass))
				{
					return false;
				}
			}
			return negated == other.negated;
		}
		return false;
	}

	@Override
	public GroupingState getGroupingState()
	{
		GroupingState gs = pcs == null ? GroupingState.ANY : pcs.getGroupingState().reduce();
		return negated ? gs.negate() : gs;
	}

	@Override
	public <R> Collection<? extends R> getCollection(PlayerCharacter pc, Converter<T, R> c)
	{
		Converter<T, R> conv = c;
		conv = negated ? new NegateFilterConverter<>(conv) : conv;
		conv = new AddFilterConverter<>(conv, this);
		return pcs.getCollection(pc, conv);
	}

	@Override
	public boolean allow(PlayerCharacter pc, T po)
	{
		return getPossessed(pc).contains(po);
	}
}
