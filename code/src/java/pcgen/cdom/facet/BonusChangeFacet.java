/*
 * Copyright (c) Thomas Parker, 2009.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.facet;

import java.util.Collection;
import java.util.List;

import pcgen.base.util.DoubleKeyMap;
import pcgen.base.util.DoubleKeyMapToList;
import pcgen.cdom.enumeration.CharID;

public class BonusChangeFacet
{
	private final Class<?> thisClass = getClass();

	private final BonusChangeSupport support = new BonusChangeSupport();

	private BonusCheckingFacet bonusCheckingFacet;

	public void reset(CharID id)
	{
		DoubleKeyMap<String, String, Double> map = getConstructingInfo(id);
		for (String type : support.getBonusTypes())
		{
			for (String name : support.getBonusNames(type))
			{
				Double newValue = bonusCheckingFacet.getBonus(id, type, name);
				Double oldValue = map.get(type, name);
				if (!newValue.equals(oldValue))
				{
					map.put(type, name, newValue);
					support.fireBonusChange(id, type, name, oldValue, newValue);
				}
			}
		}
	}

	private DoubleKeyMap<String, String, Double> getConstructingInfo(CharID id)
	{
		DoubleKeyMap<String, String, Double> map = getInfo(id);
		if (map == null)
		{
			map = new DoubleKeyMap<String, String, Double>();
			FacetCache.set(id, thisClass, map);
		}
		return map;
	}

	private DoubleKeyMap<String, String, Double> getInfo(CharID id)
	{
		return (DoubleKeyMap<String, String, Double>) FacetCache.get(id,
				thisClass);
	}

	public interface BonusChangeListener
	{

		void bonusChange(BonusChangeEvent bce);

	}

	public static class BonusChangeEvent
	{

		private final CharID charID;
		private final String bonusType;
		private final String bonusName;
		private final Object oldVal;
		private final Object newVal;

		public BonusChangeEvent(CharID id, String type, String name,
				Object oldValue, Object newValue)
		{
			charID = id;
			bonusType = type;
			bonusName = name;
			oldVal = oldValue;
			newVal = newValue;
		}

		public CharID getCharID()
		{
			return charID;
		}

		public String getBonusType()
		{
			return bonusType;
		}

		public String getBonusName()
		{
			return bonusName;
		}

		public Object getOldVal()
		{
			return oldVal;
		}

		public Object getNewVal()
		{
			return newVal;
		}

	}

	public static class BonusChangeSupport
	{
		private DoubleKeyMapToList<String, String, BonusChangeListener> listeners = new DoubleKeyMapToList<String, String, BonusChangeListener>();

		public synchronized void addBonusChangeListener(
				BonusChangeListener listener, String type, String name)
		{
			listeners.addToListFor(type, name, listener);
		}

		public Collection<String> getBonusTypes()
		{
			return listeners.getKeySet();
		}

		public Collection<String> getBonusNames(String type)
		{
			return listeners.getSecondaryKeySet(type);
		}

		public synchronized void removeBonusChangeListener(
				BonusChangeListener listener, String type, String name)
		{
			listeners.removeFromListFor(type, name, listener);
		}

		public synchronized BonusChangeListener[] getBonusChangeListeners(
				String type, String name)
		{
			return (listeners.getListFor(type, name)
					.toArray(new BonusChangeListener[0]));
		}

		public void fireBonusChange(CharID id, String type, String name,
				Object oldValue, Object newValue)
		{
			BonusChangeEvent bce = new BonusChangeEvent(id, type, name,
					oldValue, newValue);

			List<BonusChangeListener> localListeners = listeners.getListFor(
					type, name);
			if (localListeners != null)
			{
				for (BonusChangeListener target : localListeners)
				{
					target.bonusChange(bce);
				}
			}
		}
	}

	public void addBonusChangeListener(BonusChangeListener listener,
			String type, String name)
	{
		support.addBonusChangeListener(listener, type, name);
	}

	public BonusChangeListener[] getBonusChangeListeners(String type,
			String name)
	{
		return support.getBonusChangeListeners(type, name);
	}

	public void removeBonusChangeListener(BonusChangeListener listener,
			String type, String name)
	{
		support.removeBonusChangeListener(listener, type, name);
	}

	public void setBonusCheckingFacet(BonusCheckingFacet bonusCheckingFacet)
	{
		this.bonusCheckingFacet = bonusCheckingFacet;
	}
	
	
}
