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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import pcgen.base.lang.StringUtil;
import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.TreeMapToList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.content.DamageReduction;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;

/**
 * DamageReductionFacet is a Facet that tracks the DamageReduction objects that
 * have been granted to a Player Character.
 * 
 */
public class DamageReductionFacet extends AbstractSourcedListFacet<CharID, DamageReduction>
		implements DataFacetChangeListener<CharID, CDOMObject>
{

	private static final Pattern OR_PATTERN = Pattern.compile(" [oO][rR] ");
	private static final Pattern AND_PATTERN = Pattern.compile(" [aA][nN][dD] ");

	private PrerequisiteFacet prerequisiteFacet;

	private FormulaResolvingFacet formulaResolvingFacet;

	private BonusCheckingFacet bonusCheckingFacet;

	private CDOMObjectConsolidationFacet consolidationFacet;

	/**
	 * Extracts DamageReduction objects from CDOMObjects granted to a Player
	 * Character. The DamageReduction objects are granted to the Player
	 * Character.
	 * 
	 * Triggered when one of the Facets to which DamageReductionFacet listens
	 * fires a DataFacetChangeEvent to indicate a CDOMObject was added to a
	 * Player Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 */
	@Override
	public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
	{
		CDOMObject cdo = dfce.getCDOMObject();
		List<DamageReduction> drs = cdo.getListFor(ListKey.DAMAGE_REDUCTION);
		if (drs != null)
		{
			addAll(dfce.getCharID(), drs, cdo);
		}
	}

	/**
	 * Extracts DamageReduction objects from CDOMObjects removed from a Player
	 * Character. The DamageReduction objects are removed from to the Player
	 * Character.
	 * 
	 * Triggered when one of the Facets to which DamageReductionFacet listens
	 * fires a DataFacetChangeEvent to indicate a CDOMObject was removed from a
	 * Player Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 */
	@Override
	public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
	{
		removeAll(dfce.getCharID(), dfce.getCDOMObject());
	}

	private CaseInsensitiveMap<Integer> getDRMap(CharID id, Map<DamageReduction, Set<Object>> componentMap)
	{
		CaseInsensitiveMap<Integer> andMap = new CaseInsensitiveMap<>();
		if (componentMap == null || componentMap.isEmpty())
		{
			return andMap;
		}
		CaseInsensitiveMap<Integer> orMap = new CaseInsensitiveMap<>();
		for (Map.Entry<DamageReduction, Set<Object>> me : componentMap.entrySet())
		{
			DamageReduction dr = me.getKey();
			for (Object source : me.getValue())
			{
				if (prerequisiteFacet.qualifies(id, dr, source))
				{
					String sourceString = (source instanceof CDOMObject) ? ((CDOMObject) source).getQualifiedKey() : "";
					int rawDrValue = formulaResolvingFacet.resolve(id, dr.getReduction(), sourceString).intValue();
					String bypass = dr.getBypass();
					if (OR_PATTERN.matcher(bypass).find())
					{
						Integer current = orMap.get(bypass);
						if ((current == null) || (current < rawDrValue))
						{
							orMap.put(dr.getBypass(), rawDrValue);
						}
					}
					else
					{
						/*
						 * TODO Shouldn't this expansion be done in the DR
						 * token? (since it's static?)
						 */
						String[] splits = AND_PATTERN.split(bypass);
						if (splits.length == 1)
						{
							Integer current = andMap.get(dr.getBypass());
							if ((current == null) || (current < rawDrValue))
							{
								andMap.put(dr.getBypass(), rawDrValue);
							}
						}
						else
						{
							for (String split : splits)
							{
								Integer current = andMap.get(split);
								if ((current == null) || (current < rawDrValue))
								{
									andMap.put(split, rawDrValue);
								}
							}
						}
					}
				}
			}
		}

		// For each 'or'
		// Case 1: A greater or equal DR for any value in the OR
		// e.g. 10/good + 5/magic or good = 10/good
		// Case 2: A smaller DR for any value in the OR
		// e.g. 10/magic or good + 5/good = 10/magic or good; 5/good
		// e.g. 10/magic or good or lawful + 5/good = 10/good; 5/magic or good
		for (Map.Entry<Object, Integer> me : orMap.entrySet())
		{
			String origBypass = me.getKey().toString();
			Integer reduction = me.getValue();
			String[] orValues = OR_PATTERN.split(origBypass);
			boolean shouldAdd = true;
			for (String orValue : orValues)
			{
				// See if we already have a value for this type from the 'and'
				// processing.
				Integer andDR = andMap.get(orValue);
				if (andDR != null && andDR >= reduction)
				{
					shouldAdd = false;
					break;
				}
			}
			if (shouldAdd)
			{
				andMap.put(origBypass, reduction);
			}
		}
		return andMap;
	}

	/**
	 * Returns the Damage Reduction String for the Player Character identified
	 * by the given CharID.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character for which the
	 *            Damage Reduction should be returned.
	 * @return the Damage Reduction String for the Player Character identified
	 *         by the given CharID
	 */
	public String getDRString(CharID id)
	{
		return getDRString(id, getCachedMap(id));
	}

	/*
	 * Weird exposure for TemplateModifier (don't like this)
	 * 
	 * TODO This really needs to be in the output layer, not in the facets
	 */
	public String getDRString(CharID id, Map<DamageReduction, Set<Object>> cachedMap)
	{
		CaseInsensitiveMap<Integer> map = getDRMap(id, cachedMap);
		TreeMapToList<Integer, String> hml = new TreeMapToList<>();
		for (Map.Entry<Object, Integer> me : map.entrySet())
		{
			String key = me.getKey().toString();
			int value = me.getValue();
			value += (int) bonusCheckingFacet.getBonus(id, "DR", key);
			hml.addToListFor(value, key);
		}
		for (Integer reduction : hml.getKeySet())
		{
			if (hml.sizeOfListFor(reduction) > 1)
			{
				Set<String> set = new TreeSet<>();
				for (String s : hml.getListFor(reduction))
				{
					if (!OR_PATTERN.matcher(s).find())
					{
						hml.removeFromListFor(reduction, s);
						set.add(s);
					}
				}
				hml.addToListFor(reduction, StringUtil.join(set, " and "));
			}
		}

		StringBuilder sb = new StringBuilder(40);
		boolean needSeparator = false;
		for (Integer reduction : hml.getKeySet())
		{
			Set<String> set = new TreeSet<>();
			for (String s : hml.getListFor(reduction))
			{
				set.add(reduction + "/" + s);
			}
			if (needSeparator)
			{
				sb.insert(0, "; ");
			}
			needSeparator = true;
			sb.insert(0, StringUtil.join(set, "; "));
		}
		return sb.toString();
	}

	/**
	 * Gets the Damage Reduction value for the given Damage Reduction key and
	 * Player Character identified by the given CharID.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character for which the
	 *            Damage Reduction for the given key will be returned
	 * @param key
	 *            The key identifying which Damage Reduction value will be
	 *            returned
	 * @return The Damage Reduction value for the given Damage Reduction key and
	 *         Player Character identified by the given CharID
	 */
	public Integer getDR(CharID id, String key)
	{
		return getNonBonusDR(id, key) + (int) bonusCheckingFacet.getBonus(id, "DR", key);
	}

	/**
	 * Gets the Damage Reduction value, ignoring all bonuses, for the given
	 * Damage Reduction key and Player Character identified by the given CharID.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character for which the
	 *            Damage Reduction for the given key will be returned
	 * @param key
	 *            The key identifying which Damage Reduction value will be
	 *            returned
	 * @return The Damage Reduction value, ignoring all bonuses, for the given
	 *         Damage Reduction key and Player Character identified by the given
	 *         CharID
	 */
	private int getNonBonusDR(CharID id, String key)
	{
		Integer drValue = getDRMap(id, getCachedMap(id)).get(key);
		return (drValue == null) ? 0 : drValue;
	}

	public void setPrerequisiteFacet(PrerequisiteFacet prerequisiteFacet)
	{
		this.prerequisiteFacet = prerequisiteFacet;
	}

	public void setFormulaResolvingFacet(FormulaResolvingFacet formulaResolvingFacet)
	{
		this.formulaResolvingFacet = formulaResolvingFacet;
	}

	public void setBonusCheckingFacet(BonusCheckingFacet bonusCheckingFacet)
	{
		this.bonusCheckingFacet = bonusCheckingFacet;
	}

	public void setConsolidationFacet(CDOMObjectConsolidationFacet consolidationFacet)
	{
		this.consolidationFacet = consolidationFacet;
	}

	/**
	 * Initializes the connections for DamageReductionFacet to other facets.
	 * 
	 * This method is automatically called by the Spring framework during
	 * initialization of the DamageReductionFacet.
	 */
	public void init()
	{
		consolidationFacet.addDataFacetChangeListener(this);
	}
}
