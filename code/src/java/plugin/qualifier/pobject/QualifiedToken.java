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
package plugin.qualifier.pobject;

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
import pcgen.rules.persistence.token.QualifierToken;
import pcgen.util.Logging;

public class QualifiedToken<T extends CDOMObject> implements QualifierToken<T>
{

	private Class<T> refClass;

	private PrimitiveChoiceFilter<T> pcs = null;

	private CDOMGroupRef<T> allRef;

	private boolean negated;

	public String getTokenName()
	{
		return "QUALIFIED";
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
		allRef = sc.getAllReference();
		negated = negate;
		if (value != null)
		{
			pcs = context.getPrimitiveChoiceFilter(sc, value);
			return pcs != null;
		}
		return true;
	}

	public Class<? super T> getChoiceClass()
	{
		if (refClass == null)
		{
			return CDOMObject.class;
		}
		else
		{
			return refClass;
		}
	}

	public Set<T> getSet(PlayerCharacter pc)
	{
		Collection<T> objects = allRef.getContainedObjects();
		Set<T> returnSet = new HashSet<T>();
		if (objects != null)
		{
			for (T po : objects)
			{
				boolean allow = (pcs == null) || pcs.allow(pc, po);
				if (allow && (pc.isQualified(po) ^ negated))
				{
					returnSet.add(po);
				}
			}
		}
		return returnSet;
	}

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
		if (o instanceof QualifiedToken)
		{
			QualifiedToken<?> other = (QualifiedToken<?>) o;
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
		GroupingState gs =
				(pcs == null) ? GroupingState.ANY : pcs.getGroupingState()
					.reduce();
		return negated ? gs.negate() : gs;
	}
}
