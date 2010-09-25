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
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.PrimitiveChoiceFilter;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.SelectionCreator;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

public abstract class AbstractPCQualifierToken<T extends CDOMObject> implements
		QualifierToken<T>
{

	private Class<T> refClass;

	private PrimitiveChoiceFilter<T> pcs = null;

	private boolean negated = false;
	
	private CDOMGroupRef<T> allReference;
	
	public String getTokenName()
	{
		return "PC";
	}

	public boolean initialize(LoadContext context, SelectionCreator<T> sc,
			String condition, String value, boolean negate)
	{
		if (condition != null)
		{
			Logging.addParseMessage(Level.SEVERE, "Cannot make "
					+ getTokenName()
					+ " into a conditional Qualifier, remove =");
			return false;
		}
		if (sc == null)
		{
			throw new IllegalArgumentException();
		}
		refClass = sc.getReferenceClass();
		negated = negate;
		allReference = sc.getAllReference();
		if (value != null)
		{
			pcs = context.getPrimitiveChoiceFilter(sc, value);
			return pcs != null;
		}
		return true;
	}

	public Set<T> getSet(PlayerCharacter pc)
	{
		Collection<T> possessed = getPossessed(pc);
		Collection<T> objects = negated ? allReference.getContainedObjects() : possessed;
		Set<T> returnSet = new HashSet<T>();
		if (objects != null)
		{
			for (T po : objects)
			{
				boolean allow = pcs == null || pcs.allow(pc, po);
				if (allow && (!negated || !possessed.contains(po)))
				{
					returnSet.add(po);
				}
			}
		}
		return returnSet;
	}

	protected abstract Collection<T> getPossessed(PlayerCharacter pc);

	public String getLSTformat(boolean useAny)
	{
		StringBuilder sb = new StringBuilder();
		if (negated)
		{
			sb.append('!');
		}
		sb.append(getTokenName());
		if (pcs != null)
		{
			sb.append('[').append(pcs.getLSTformat()).append(']');
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
		if (o instanceof AbstractPCQualifierToken)
		{
			AbstractPCQualifierToken<?> other = (AbstractPCQualifierToken<?>) o;
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

	public GroupingState getGroupingState()
	{
		GroupingState gs = pcs == null ? GroupingState.ANY : pcs
				.getGroupingState().reduce();
		return negated ? gs.negate() : gs;
	}
}
