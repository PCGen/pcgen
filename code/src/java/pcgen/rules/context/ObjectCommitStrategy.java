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
import pcgen.base.util.Indirect;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.enumeration.FactSetKey;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.core.prereq.Prerequisite;

public interface ObjectCommitStrategy
{

	void put(CDOMObject cdo, StringKey sk, String s);

	void remove(CDOMObject cdo, StringKey sk);

	<T> void put(CDOMObject cdo, ObjectKey<T> sk, T s);

	void remove(CDOMObject cdo, ObjectKey<?> sk);

	<T> void put(CDOMObject cdo, FactKey<T> sk, Indirect<T> s);

	void remove(CDOMObject cdo, FactKey<?> sk);

	void put(CDOMObject cdo, IntegerKey ik, Integer i);

	void remove(CDOMObject cdo, IntegerKey ik);

	void put(CDOMObject cdo, FormulaKey fk, Formula f);

	void put(CDOMObject obj, VariableKey vk, Formula f);

	<T> void addToList(CDOMObject cdo, ListKey<T> key, T value);

	void removeList(CDOMObject cdo, ListKey<?> lk);

	<T> void removeFromList(CDOMObject cdo, ListKey<T> lk, T val);

	<T> void addToSet(CDOMObject cdo, FactSetKey<T> key, Indirect<T> value);

	void removeSet(CDOMObject cdo, FactSetKey<?> lk);

	<T> void removeFromSet(CDOMObject cdo, FactSetKey<T> lk, Indirect<T> val);

	Integer getInteger(CDOMObject cdo, IntegerKey ik);

	Formula getFormula(CDOMObject cdo, FormulaKey fk);

	Formula getVariable(CDOMObject obj, VariableKey key);

	Set<VariableKey> getVariableKeys(CDOMObject obj);

	<T> T getObject(CDOMObject cdo, ObjectKey<T> ik);

	<T> Indirect<T> getFact(CDOMObject cdo, FactKey<T> ik);

	<T> Changes<T> getListChanges(CDOMObject cdo, ListKey<T> lk);

	<T> Changes<Indirect<T>> getSetChanges(CDOMObject cdo, FactSetKey<T> lk);

	<K, V> void put(CDOMObject cdo, MapKey<K, V> mk, K key, V value);

	<K, V> void remove(CDOMObject cdo, MapKey<K, V> mk, K key);

	<K, V> MapChanges<K, V> getMapChanges(CDOMObject cdo, MapKey<K, V> mk);

	void setExtractURI(URI extractURI);

	void setSourceURI(URI sourceURI);

	void put(ConcretePrereqObject cpo, Prerequisite p);

	Changes<Prerequisite> getPrerequisiteChanges(ConcretePrereqObject obj);

	String getString(CDOMObject cdo, StringKey sk);

	boolean containsListFor(CDOMObject obj, ListKey<?> lk);

	boolean containsSetFor(CDOMObject obj, FactSetKey<?> lk);

	<T> void removePatternFromList(CDOMObject cdo, ListKey<T> lk, String pattern);

	void clearPrerequisiteList(ConcretePrereqObject cpo);

	<T> PatternChanges<T> getListPatternChanges(CDOMObject cdo, ListKey<T> lk);

	boolean wasRemoved(CDOMObject cdo, ObjectKey<?> ok);

	boolean wasRemoved(CDOMObject cdo, FactKey<?> ok);

	boolean wasRemoved(CDOMObject cdo, FactSetKey<?> ok);

	boolean wasRemoved(CDOMObject cdo, IntegerKey ik);

	boolean wasRemoved(CDOMObject cdo, StringKey sk);
}
