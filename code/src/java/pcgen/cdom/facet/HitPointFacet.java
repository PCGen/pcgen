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

import pcgen.base.util.RandomUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.HitDie;
import pcgen.cdom.content.Processor;
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
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;

/**
 * HitPointFacet stores information about hit points for a Player Character.
 * Specifically this Facet stores the number of hit points granted to a Player
 * Character for each PCClassLevel possessed by the Player Character.
 * 
 */
public class HitPointFacet extends AbstractAssociationFacet<CharID, PCClassLevel, Integer>
		implements DataFacetChangeListener<CharID, CDOMObject>
{

	private final PlayerCharacterTrackingFacet trackingFacet =
			FacetLibrary.getFacet(PlayerCharacterTrackingFacet.class);

	private ClassFacet classFacet;

	private RaceFacet raceFacet;

	private TemplateFacet templateFacet;

	private LevelFacet levelFacet;

	private BonusCheckingFacet bonusCheckingFacet;

	/**
	 * Roll the hitpoints for a single level.
	 *
	 * @param min the minimum number on the die
	 * @param max the maximum number on the die
	 * @param totalLevel the level the hitpoints are being rolled for (used in maths)
	 * @return the hitpoints for the given level.
	 */
	private static int rollHP(final int min, final int max, final int totalLevel)
	{
		int roll;

		switch (SettingsHandler.getHPRollMethod())
		{
			case Constants.HP_USER_ROLLED -> roll = 1;
			case Constants.HP_AVERAGE -> {
				roll = max - min;

				// (n+1)/2
				// average roll on a die with an  odd # of sides works out exactly
				// average roll on a die with an even # of sides will have an extra 0.5

				if (((totalLevel & 0x01) == 0) && ((roll & 0x01) != 0))
				{
					++roll;
				}
				roll = min + (roll / 2);
			}
			case Constants.HP_AUTO_MAX -> roll = max;
			case Constants.HP_PERCENTAGE -> roll =
					(min - 1) + (int) ((SettingsHandler.getHPPercent() * ((max - min) + 1)) / 100.0);
			case Constants.HP_AVERAGE_ROUNDED_UP -> roll = (int) Math.ceil((min + max) / 2.0);
			case Constants.HP_STANDARD, default -> roll = Math.abs(RandomUtil.getRandomInt((max - min) + 1)) + min;
		}

		return roll;
	}

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
				Processor<HitDie> dieLock = cdo.get(ObjectKey.HITDIE);
				if (dieLock != null)
				{
					for (int level = 1; level <= classFacet.getLevel(id, pcClass); level++)
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
		Processor<HitDie> dieLock = raceFacet.get(id).get(ObjectKey.HITDIE);
		if (dieLock != null)
		{
			currDie = dieLock.applyProcessor(currDie, pcClass);
		}

		// Templates
		for (PCTemplate template : templateFacet.getSet(id))
		{
			if (template != null)
			{
				Processor<HitDie> lock = template.get(ObjectKey.HITDIE);
				if (lock != null)
				{
					currDie = lock.applyProcessor(currDie, pcClass);
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
				Processor<HitDie> lock = cl.get(ObjectKey.HITDIE);
				if (lock != null)
				{
					currDie = lock.applyProcessor(currDie, pcClass);
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
			final int min = 1 + (int) bonusCheckingFacet.getBonus(id, "HD", "MIN")
				+ (int) bonusCheckingFacet.getBonus(id, "HD", "MIN;CLASS." + pcc.getKeyName());
			final int max = getLevelHitDie(id, pcc, level).getDie() + (int) bonusCheckingFacet.getBonus(id, "HD", "MAX")
				+ (int) bonusCheckingFacet.getBonus(id, "HD", "MAX;CLASS." + pcc.getKeyName());

			if (first && maximizeHPatFirstLevel(pcc, level))
			{
				roll = max;
			}
			else
			{
				PlayerCharacter pc = trackingFacet.getPC(id);
				if (!pc.isImporting())
				{
					roll = rollHP(min, max, levelFacet.getTotalLevels(id));
				}
			}

			roll += ((int) bonusCheckingFacet.getBonus(id, "HP", "CURRENTMAXPERLEVEL"));
		}
		PCClassLevel classLevel = classFacet.getClassLevel(id, pcc, level - 1);
		set(id, classLevel, roll);
	}

	private boolean maximizeHPatFirstLevel(PCClass pcc, int level)
	{
		boolean classAllowsMaxHP = !SettingsHandler.isHPMaxAtFirstPCClassLevelOnly() || pcc.isType("PC");
		return (level == 1) && SettingsHandler.isHPMaxAtFirstLevel() && classAllowsMaxHP;
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
