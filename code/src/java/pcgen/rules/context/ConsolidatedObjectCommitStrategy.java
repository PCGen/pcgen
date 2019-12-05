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
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

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

public class ConsolidatedObjectCommitStrategy implements ObjectCommitStrategy
{
    private URI sourceURI;

    private URI extractURI;

    public URI getExtractURI()
    {
        return extractURI;
    }

    @Override
    public void setExtractURI(URI extractURI)
    {
        this.extractURI = extractURI;
    }

    public URI getSourceURI()
    {
        return sourceURI;
    }

    @Override
    public void setSourceURI(URI sourceURI)
    {
        this.sourceURI = sourceURI;
    }

    @Override
    public String getString(CDOMObject cdo, StringKey sk)
    {
        return cdo.get(sk);
    }

    @Override
    public Integer getInteger(CDOMObject cdo, IntegerKey ik)
    {
        return cdo.get(ik);
    }

    @Override
    public Formula getFormula(CDOMObject cdo, FormulaKey fk)
    {
        return cdo.get(fk);
    }

    @Override
    public Formula getVariable(CDOMObject obj, VariableKey key)
    {
        return obj.get(key);
    }

    @Override
    public Set<VariableKey> getVariableKeys(CDOMObject obj)
    {
        return obj.getVariableKeys();
    }

    @Override
    public <T> T getObject(CDOMObject cdo, ObjectKey<T> ik)
    {
        return cdo.get(ik);
    }

    @Override
    public <T> Indirect<T> getFact(CDOMObject cdo, FactKey<T> ik)
    {
        return cdo.get(ik);
    }

    @Override
    public <T> Changes<Indirect<T>> getSetChanges(CDOMObject cdo, FactSetKey<T> lk)
    {
        return new CollectionChanges<>(cdo.getSetFor(lk), null, false);
    }

    @Override
    public <T> Changes<T> getListChanges(CDOMObject cdo, ListKey<T> lk)
    {
        return new CollectionChanges<>(cdo.getListFor(lk), null, false);
    }

    @Override
    public void put(CDOMObject cdo, StringKey sk, String s)
    {
        cdo.put(sk, s);
    }

    @Override
    public <T> void put(CDOMObject cdo, ObjectKey<T> sk, T s)
    {
        cdo.put(sk, s);
    }

    @Override
    public void remove(CDOMObject cdo, ObjectKey<?> sk)
    {
        cdo.remove(sk);
    }

    @Override
    public <T> void put(CDOMObject cdo, FactKey<T> sk, Indirect<T> s)
    {
        cdo.put(sk, s);
    }

    @Override
    public void remove(CDOMObject cdo, FactKey<?> sk)
    {
        cdo.remove(sk);
    }

    @Override
    public void put(CDOMObject cdo, IntegerKey ik, Integer i)
    {
        cdo.put(ik, i);
    }

    @Override
    public void put(CDOMObject cdo, FormulaKey fk, Formula f)
    {
        cdo.put(fk, f);
    }

    @Override
    public void put(CDOMObject obj, VariableKey vk, Formula f)
    {
        obj.put(vk, f);
    }

    @Override
    public boolean containsListFor(CDOMObject cdo, ListKey<?> key)
    {
        return cdo.containsListFor(key);
    }

    @Override
    public <T> void addToList(CDOMObject cdo, ListKey<T> key, T value)
    {
        cdo.addToListFor(key, value);
    }

    @Override
    public void removeList(CDOMObject cdo, ListKey<?> lk)
    {
        cdo.removeListFor(lk);
    }

    @Override
    public <T> void removeFromList(CDOMObject cdo, ListKey<T> lk, T val)
    {
        cdo.removeFromListFor(lk, val);
    }

    @Override
    public boolean containsSetFor(CDOMObject cdo, FactSetKey<?> key)
    {
        return cdo.containsSetFor(key);
    }

    @Override
    public <T> void addToSet(CDOMObject cdo, FactSetKey<T> key, Indirect<T> value)
    {
        cdo.addToSetFor(key, value);
    }

    @Override
    public void removeSet(CDOMObject cdo, FactSetKey<?> lk)
    {
        cdo.removeSetFor(lk);
    }

    @Override
    public <T> void removeFromSet(CDOMObject cdo, FactSetKey<T> lk, Indirect<T> val)
    {
        cdo.removeFromSetFor(lk, val);
    }

    @Override
    public <K, V> void put(CDOMObject cdo, MapKey<K, V> mk, K key, V value)
    {
        cdo.addToMapFor(mk, key, value);
    }

    @Override
    public <K, V> void remove(CDOMObject cdo, MapKey<K, V> mk, K key)
    {
        cdo.removeFromMapFor(mk, key);
    }

    @Override
    public void put(ConcretePrereqObject cpo, Prerequisite p)
    {
        cpo.addPrerequisite(p);
    }

    @Override
    public <K, V> MapChanges<K, V> getMapChanges(CDOMObject cdo, MapKey<K, V> mk)
    {
        return new MapChanges<>(cdo.getMapFor(mk), null, false);
    }

    @Override
    public Changes<Prerequisite> getPrerequisiteChanges(ConcretePrereqObject obj)
    {
        return new CollectionChanges<>(obj.getPrerequisiteList(), null, false);
    }

    @Override
    public <T> void removePatternFromList(CDOMObject cdo, ListKey<T> lk, String pattern)
    {
        List<T> list = cdo.getListFor(lk);
        if (list == null || list.isEmpty())
        {
            return;
        }
        Pattern p = Pattern.compile(pattern);
        for (T obj : list)
        {
            if (p.matcher(obj.toString()).find())
            {
                cdo.removeFromListFor(lk, obj);
            }
        }
    }

    @Override
    public void clearPrerequisiteList(ConcretePrereqObject cpo)
    {
        cpo.clearPrerequisiteList();
    }

    @Override
    public <T> PatternChanges<T> getListPatternChanges(CDOMObject cdo, ListKey<T> lk)
    {
        return new PatternChanges<>(cdo.getListFor(lk), null, false);
    }

    @Override
    public boolean wasRemoved(CDOMObject cdo, ObjectKey<?> ok)
    {
        return false;
    }

    @Override
    public boolean wasRemoved(CDOMObject cdo, FactKey<?> ok)
    {
        return false;
    }

    @Override
    public boolean wasRemoved(CDOMObject cdo, FactSetKey<?> ok)
    {
        return false;
    }

    @Override
    public void remove(CDOMObject cdo, StringKey sk)
    {
        cdo.remove(sk);
    }

    @Override
    public boolean wasRemoved(CDOMObject cdo, StringKey sk)
    {
        return false;
    }

    @Override
    public void remove(CDOMObject cdo, IntegerKey ik)
    {
        cdo.remove(ik);
    }

    @Override
    public boolean wasRemoved(CDOMObject cdo, IntegerKey ik)
    {
        return false;
    }

}
