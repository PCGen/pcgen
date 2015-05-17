/*
 * Copyright (c) Thomas Parker, 2012.
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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.content.HitDie;
import pcgen.cdom.content.Modifier;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.facet.analysis.LevelFacet;
import pcgen.cdom.facet.base.AbstractAssociationFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.model.ClassFacet;
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;

/**
 * HitPointFacet stores information about hit points for a Player Character.
 * Specifically this Facet stores the number of hit points granted to a Player
 * Character for each PCClassLevel possessed by the Player Character.
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class HitPointFacet extends
		AbstractAssociationFacet<PCClassLevel, Integer> implements
		DataFacetChangeListener<CharID, CDOMObject>
{

	private final PlayerCharacterTrackingFacet trackingFacet = FacetLibrary
			.getFacet(PlayerCharacterTrackingFacet.class);
	
	private ClassFacet classFacet;

	private RaceFacet raceFacet;

	private TemplateFacet templateFacet;
	
	private LevelFacet levelFacet;

	private BonusCheckingFacet bonusCheckingFacet;

	/**
	 * Watches for new PCClassLevel objects to be granted to the Player
	 * Character. When called, this then triggers the determination of the Hit
	 * Points for that PCClassLevel.
	 * 
	 * Triggered when one of the Facets to which FollowerOptionFacet listens
	 * fires a DataFacetChangeEvent to indicate a FollowerOption was added to a
	 * Player Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.event.DataFacetChangeListener#dataAdded(pcgen.cdom.facet.event.DataFacetChangeEvent)
	 */
	@Override
	public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
	{
		CharID id = dfce.getCharID();
		CDOMObject cdo = dfce.getCDOMObject();
		PlayerCharacter pc = trackingFacet.getPC(id);
		if (!pc.isImporting())
		{
			boolean first = true;
			for (PCClass pcClass : classFacet.getSet(id))
			{
				//
				// Recalculate HPs in case HD have changed.
				//
				Modifier<HitDie> dieLock = cdo.get(ObjectKey.HITDIE);
				if (dieLock != null)
				{
					for (int level = 1; level <= classFacet.getLevel(id,
						pcClass); level++)
					{
						HitDie baseHD = pcClass.getSafe(ObjectKey.LEVEL_HITDIE);
						if (!baseHD.equals(getLevelHitDie(id, pcClass, level)))
						{
							// If the HD has changed from base reroll
							rollHP(id, pcClass, level, first);
							pc.setDirty(true);
						}
					}
				}
				first = false;
			}
		}
	}

	@Override
	public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
	{
		/*
		 * TODO This probably needs some form of symmetry - when a PCClassLevel
		 * is removed, the number of hit points for that PCClassLevel is
		 * removed.
		 * 
		 * Alternatively, we can define this in such a way that the otherwise
		 * lost information is saved, so that addition and removal of the same
		 * level doesn't trigger new seleciton of hit points - need to define
		 * the best strategy here (and just clearly document the decision)
		 */
	}

	/**
	 * Returns the HitDie for the given PCClass and level in the Player
	 * Character identified by the given CharID.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character for which the
	 *            HitDie of the given PCClass and level will be returned
	 * @param pcClass
	 *            The PCClass for which the HitDie will be returned
	 * @param classLevel
	 *            The level for which the HitDie will be returned
	 * @return The HitDie for the given PCClass and level in the Player
	 *         Character identified by the given CharID
	 */
	public HitDie getLevelHitDie(CharID id, PCClass pcClass, int classLevel)
	{
		// Class Base Hit Die
		HitDie currDie = pcClass.getSafe(ObjectKey.LEVEL_HITDIE);
		Modifier<HitDie> dieLock = raceFacet.get(id).get(ObjectKey.HITDIE);
		if (dieLock != null)
		{
			currDie = dieLock.applyModifier(currDie, pcClass);
		}

		// Templates
		for (PCTemplate template : templateFacet.getSet(id))
		{
			if (template != null)
			{
				Modifier<HitDie> lock = template.get(ObjectKey.HITDIE);
				if (lock != null)
				{
					currDie = lock.applyModifier(currDie, pcClass);
				}
			}
		}

		// Levels
		PCClassLevel cl = classFacet.getClassLevel(id, pcClass, classLevel);
		if (cl != null)
		{
			if (cl.get(ObjectKey.DONTADD_HITDIE) != null)
			{
				currDie = HitDie.ZERO; //null;
			}
			else
			{
				Modifier<HitDie> lock = cl.get(ObjectKey.HITDIE);
				if (lock != null)
				{
					currDie = lock.applyModifier(currDie, pcClass);
				}
			}
		}

		return currDie;
	}

	/**
	 * Rolls the hit points for a given PCClass and level.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character on which the hit
	 *            points are to be rolled
	 * @param pcc
	 *            The PCClass for which the hit points are to be rolled
	 * @param level
	 *            The class level for which the hit points are to be rolled
	 * @param first
	 *            And identifier indicating if this is the Player Character's
	 *            first level.
	 */
	public void rollHP(CharID id, PCClass pcc, int level, boolean first)
	{
		int roll = 0;

		HitDie lvlDie = getLevelHitDie(id, pcc, level);
		if ((lvlDie == null) || (lvlDie.getDie() == 0))
		{
			roll = 0;
		}
		else
		{
			final int min =
					1
						+ (int) bonusCheckingFacet.getBonus(id, "HD", "MIN")
						+ (int) bonusCheckingFacet.getBonus(id, "HD", "MIN;CLASS."
							+ pcc.getKeyName());
			final int max =
					getLevelHitDie(id, pcc, level).getDie()
						+ (int) bonusCheckingFacet.getBonus(id, "HD", "MAX")
						+ (int) bonusCheckingFacet.getBonus(id, "HD", "MAX;CLASS."
							+ pcc.getKeyName());

			if (SettingsHandler.getGame().getHPFormula().length() == 0)
			{
				if (first
					&& level == 1
					&& SettingsHandler.isHPMaxAtFirstLevel()
					&& (!SettingsHandler.isHPMaxAtFirstPCClassLevelOnly() || pcc
						.isType("PC")))
				{
					roll = max;
				}
				else
				{
					PlayerCharacter pc = trackingFacet.getPC(id);
					if (!pc.isImporting())
					{
						roll =
								Globals.rollHP(min, max, pcc.getDisplayName(),
									level, levelFacet.getTotalLevels(id));
					}
				}
			}

			roll += ((int) bonusCheckingFacet.getBonus(id, "HP", "CURRENTMAXPERLEVEL"));
		}
		PCClassLevel classLevel = classFacet.getClassLevel(id, pcc, level - 1);
		set(id, classLevel, Integer.valueOf(roll));
	}

	public void setClassFacet(ClassFacet classFacet)
	{
		this.classFacet = classFacet;
	}

	public void setRaceFacet(RaceFacet raceFacet)
	{
		this.raceFacet = raceFacet;
	}

	public void setTemplateFacet(TemplateFacet templateFacet)
	{
		this.templateFacet = templateFacet;
	}

	public void setLevelFacet(LevelFacet levelFacet)
	{
		this.levelFacet = levelFacet;
	}

	public void setBonusCheckingFacet(BonusCheckingFacet bonusCheckingFacet)
	{
		this.bonusCheckingFacet = bonusCheckingFacet;
	}

	/**
	 * Initializes the connections for HitPointFacet to other facets.
	 * 
	 * This method is automatically called by the Spring framework during
	 * initialization of the HitPointFacet.
	 */
	public void init()
	{
		templateFacet.addDataFacetChangeListener(this);
	}
}
