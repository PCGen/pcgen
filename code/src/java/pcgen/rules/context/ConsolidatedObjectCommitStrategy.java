/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.rules.context;

import java.net.URI;
import java.util.Set;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.core.prereq.Prerequisite;

public class ConsolidatedObjectCommitStrategy implements ObjectCommitStrategy
{
	private URI sourceURI;

	private URI extractURI;

	public URI getExtractURI()
	{
		return extractURI;
	}

	public void setExtractURI(URI extractURI)
	{
		this.extractURI = extractURI;
	}

	public URI getSourceURI()
	{
		return sourceURI;
	}

	public void setSourceURI(URI sourceURI)
	{
		this.sourceURI = sourceURI;
	}

	public String getString(CDOMObject cdo, StringKey sk)
	{
		return cdo.get(sk);
	}

	public Integer getInteger(CDOMObject cdo, IntegerKey ik)
	{
		return cdo.get(ik);
	}

	public Formula getFormula(CDOMObject cdo, FormulaKey fk)
	{
		return cdo.get(fk);
	}

	public Formula getVariable(CDOMObject obj, VariableKey key)
	{
		return obj.get(key);
	}

	public Set<VariableKey> getVariableKeys(CDOMObject obj)
	{
		return obj.getVariableKeys();
	}

	public <T> T getObject(CDOMObject cdo, ObjectKey<T> ik)
	{
		return cdo.get(ik);
	}

	public <T> Changes<T> getListChanges(CDOMObject cdo, ListKey<T> lk)
	{
		return new CollectionChanges<T>(cdo.getListFor(lk), null, false);
	}

	public void put(CDOMObject cdo, StringKey sk, String s)
	{
		if (s == null)
		{
			cdo.remove(sk);
		}
		else if (s.startsWith(Constants.LST_DOT_CLEAR))
		{
			throw new IllegalArgumentException("Cannot set a value to " + s);
		}
		else
		{
			cdo.put(sk, s);
		}
	}

	public <T> void put(CDOMObject cdo, ObjectKey<T> sk, T s)
	{
		cdo.put(sk, s);
	}

	public void put(CDOMObject cdo, IntegerKey ik, Integer i)
	{
		cdo.put(ik, i);
	}

	public void put(CDOMObject cdo, FormulaKey fk, Formula f)
	{
		cdo.put(fk, f);
	}

	public void put(CDOMObject obj, VariableKey vk, Formula f)
	{
		obj.put(vk, f);
	}

	public boolean containsListFor(CDOMObject cdo, ListKey<?> key)
	{
		return cdo.containsListFor(key);
	}

	public <T> void addToList(CDOMObject cdo, ListKey<T> key, T value)
	{
		cdo.addToListFor(key, value);
	}

	public void removeList(CDOMObject cdo, ListKey<?> lk)
	{
		cdo.removeListFor(lk);
	}

	public <T> void removeFromList(CDOMObject cdo, ListKey<T> lk, T val)
	{
		cdo.removeFromListFor(lk, val);
	}

	public void put(ConcretePrereqObject cpo, Prerequisite p)
	{
		cpo.addPrerequisite(p);
	}

	public Changes<Prerequisite> getPrerequisiteChanges(ConcretePrereqObject obj)
	{
		return new CollectionChanges<Prerequisite>(obj.getPrerequisiteList(),
				null, false);
	}
}
