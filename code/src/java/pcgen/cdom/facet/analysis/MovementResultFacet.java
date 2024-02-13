/*
 * Copyright (c) 2010-9 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.facet.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import pcgen.base.util.NamedValue;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.MovementType;
import pcgen.cdom.facet.BonusCheckingFacet;
import pcgen.cdom.facet.EquipmentFacet;
import pcgen.cdom.facet.FormulaResolvingFacet;
import pcgen.cdom.facet.base.AbstractStorageFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.core.*;
import pcgen.core.utils.CoreUtility;
import pcgen.util.enumeration.Load;

/**
 * MovementResultFacet stores the resulting movement of a Player Character. Note
 * that this does not store the Movement objects granted by CDOMObjects; rather
 * this is storing the resulting values post aggregation of those Movement
 * objects.
 *
 */
public class MovementResultFacet extends AbstractStorageFacet<CharID>
		implements DataFacetChangeListener<CharID, CDOMObject>
{
	private MovementFacet movementFacet;
	private MoveCloneFacet moveCloneFacet;
	private BaseMovementFacet baseMovementFacet;
	private RaceFacet raceFacet;
	private TemplateFacet templateFacet;
	private EquipmentFacet equipmentFacet;
	private BonusCheckingFacet bonusCheckingFacet;
	private UnencumberedArmorFacet unencumberedArmorFacet;
	private UnencumberedLoadFacet unencumberedLoadFacet;
	private FormulaResolvingFacet formulaResolvingFacet;
	private LoadFacet loadFacet;

	/**
	 * Returns the movement value of the given type for the Player Character
	 * identified by the given CharID. All appropriate BONUSes are added to the
	 * movement before the result is returned.
	 *
	 * @param id
	 *            The CharID identifying the Player Character for which the
	 *            movement value of the given type to be returned
	 * @param moveType
	 *            The MovementType to be returned
	 * @return The movement value of the given type for the Player Character
	 *         identified by the given CharID
	 */
	public double movementOfType(CharID id, MovementType moveType)
	{
		MovementCacheInfo mci = getInfo(id);
		if (mci == null)
		{
			return 0.0;
		}
		return mci.movementOfType(id, moveType);
	}

	/**
	 * Returns the type-safe MovementCacheInfo for this MoneyFacet and the given
	 * CharID. Will return a new, empty MovementCacheInfo if no Money
	 * information has been set for the given CharID. Will not return null.
	 *
	 * Note that this method SHOULD NOT be public. The MovementCacheInfo object
	 * is owned by MoneyFacet, and since it can be modified, a reference to that
	 * object should not be exposed to any object other than MoneyFacet.
	 *
	 * @param id
	 *            The CharID for which the MovementCacheInfo should be returned
	 * @return The MovementCacheInfo for the Player Character represented by the
	 *         given CharID.
	 */
	private MovementCacheInfo getConstructingInfo(CharID id)
	{
		MovementCacheInfo rci = getInfo(id);
		if (rci == null)
		{
			rci = new MovementCacheInfo();
			setCache(id, rci);
		}
		return rci;
	}

	/**
	 * Returns the type-safe MovementCacheInfo for this MoneyFacet and the given
	 * CharID. May return null if no Movement information has been set for the
	 * given CharID.
	 *
	 * Note that this method SHOULD NOT be public. The MovementCacheInfo object
	 * is owned by MoneyFacet, and since it can be modified, a reference to that
	 * object should not be exposed to any object other than MoneyFacet.
	 *
	 * @param id
	 *            The CharID for which the MovementCacheInfo should be returned
	 * @return The MovementCacheInfo for the Player Character represented by the
	 *         given CharID; null if no Movement information has been set for
	 *         the Player Character.
	 */
	private MovementCacheInfo getInfo(CharID id)
	{
		return (MovementCacheInfo) getCache(id);
	}

	/**
	 * Data structure that stores the actual movement values for a Player
	 * Character.
	 */
	public class MovementCacheInfo
	{
		private final Map<MovementType, Double> moveRates = new LinkedHashMap<>();

		private int countMovementTypes()
		{
			return moveRates.size();
		}

		/**
		 * recalculate all the move rates and modifiers
		 */
		private void adjustMoveRates(CharID id)
		{
			Race race = raceFacet.get(id);
			if (race == null)
			{
				return;
			}

			Set<SimpleMovement> mms = baseMovementFacet.getSet(id);
			if (mms == null || mms.isEmpty())
			{
				return;
			}

			for (SimpleMovement movement : movementFacet.getSet(id))
			{
				Double current = moveRates.get(movement.getMovementType());
				double thisValue = movement.getMovement();
				if ((current == null) || (thisValue > current))
				{
					moveRates.put(movement.getMovementType(), thisValue);
				}
			}
			Set<MoveClone> clones = moveCloneFacet.getSet(id);
			if (clones != null)
			{
				for (MoveClone moveClone : clones)
				{
					Double baseMove = moveRates.get(moveClone.getBaseType());
					moveRates.put(moveClone.getCloneType(),
						moveClone.apply(baseMove));
				}
			}

			// Need to create movement entries if there is a BONUS:MOVEADD
			// associated with that type of movement
			for (String moveType : bonusCheckingFacet.getExpandedBonusInfo(id, "MOVEADD"))
			{
				if (moveType.startsWith("TYPE"))
				{
					moveType = moveType.substring(5);
				}

				if (!moveType.equalsIgnoreCase("ALL"))
				{
					String clean = CoreUtility.capitalizeFirstLetter(moveType);
					moveRates.putIfAbsent(MovementType.getConstant(clean), 0.0);
				}
			}
		}

		/**
		 * get the base MOVE: plus any bonuses from BONUS:MOVE additions takes
		 * into account Armor restrictions to movement and load carried
		 *
		 * @param id
		 * @return movement
		 */
		public double movementOfType(CharID id, MovementType moveType)
		{
			// get base movement
			Double moveInFeet = moveRates.get(moveType);
			if (moveInFeet == null)
			{
				return 0.0;
			}

			// First get the MOVEADD bonus
			String movementAsType = "TYPE." + moveType.toString().toUpperCase();
			moveInFeet += bonusCheckingFacet.getBonus(id, "MOVEADD", movementAsType);

			// also check for special case of TYPE=ALL
			moveInFeet += bonusCheckingFacet.getBonus(id, "MOVEADD", "TYPE.ALL");

			double calcMove = moveInFeet;

			// Now we get the BONUS:MOVEMULT multipliers
			double moveMult =
					bonusCheckingFacet.getBonus(id, "MOVEMULT", movementAsType);

			// also check for special case of TYPE=ALL
			moveMult += bonusCheckingFacet.getBonus(id, "MOVEMULT", "TYPE.ALL");

			if (moveMult > 0)
			{
				calcMove = (int) (calcMove * moveMult);
			}

			double postMove = calcMove;

			// now add on any POSTMOVE bonuses
			postMove +=
					bonusCheckingFacet.getBonus(id, "POSTMOVEADD", movementAsType);

			// also check for special case of TYPE=ALL
			postMove += bonusCheckingFacet.getBonus(id, "POSTMOVEADD", "TYPE.ALL");

			// because POSTMOVE is magical movement which should not be
			// multiplied by magical items, etc, we now see which is larger,
			// (baseMove + postMove) or (baseMove * moveMultiplier)
			// and keep the larger one, discarding the other
			moveInFeet = Math.max(calcMove, postMove);

			// get a list of all equipped Armor
			Load armorLoad = Load.LIGHT;

			// Ignore armor weight if the house rule has disabled it
			if (Globals.checkRule(RuleConstants.SYS_LDPACSK))
			{
				for (Equipment eq : equipmentFacet.getSet(id))
				{
					if (!eq.typeStringContains("Armor") || !eq.isEquipped() || eq.isShield())
					{
						continue;
					}
					if (eq.isHeavy() && !unencumberedArmorFacet.ignoreLoad(id, Load.HEAVY))
					{
						armorLoad = armorLoad.max(Load.HEAVY);
					}
					else if (eq.isMedium() && !unencumberedArmorFacet.ignoreLoad(id, Load.MEDIUM))
					{
						armorLoad = armorLoad.max(Load.MEDIUM);
					}
				}
			}

			double armorMove = armorLoad.calcEncumberedMove(moveInFeet);

			Load pcLoad = Load.LIGHT;
			if (Globals.checkRule(RuleConstants.SYS_LDPACSK))
			{
				pcLoad = loadFacet.getLoadType(id);
			}
			double loadMove = calcEncumberedMove(id, pcLoad, moveInFeet);

			// It is possible to have a PC that is not encumbered by Armor
			// But is encumbered by Weight carried (and visa-versa)
			// So do two calcs and take the slowest
			moveInFeet = Math.min(armorMove, loadMove);

			return moveInFeet;
		}

		public List<NamedValue> getMovementValues(CharID id)
		{
			Set<NamedValue> set = new TreeSet<>(new MoveSorter());
			for (MovementType moveType : moveRates.keySet())
			{
				String moveName = moveType.toString();
				set.add(new NamedValue(moveName, movementOfType(id, moveType)));
			}
			return new ArrayList<>(set);
		}

		/**
		 * Returns the base movement value of the given type for the Player
		 * Character. No BONUSes are added to the movement before it is
		 * returned.
		 *
		 * @param moveType
		 *            The movement type to be returned
		 * @return The movement value of the given type for the Player Character
		 */
		public double getMovementOfType(MovementType moveType)
		{
			return moveRates.getOrDefault(moveType, 0.0);
		}

		/**
		 * Returns the base movement value of the given type for the Player
		 * Character, when the Player Character is under the given Load. No
		 * BONUSes are added to the movement before it is returned.
		 *
		 * @param moveType
		 *            The movement type to be returned
		 * @param load
		 *            The Load to be used to calculate the base movement of the
		 *            Player Character
		 * @return The movement value of the given type for the Player Character
		 */
		public int getBaseMovement(MovementType moveType, Load load)
		{
			//TODO Deal with Load?!?
			return moveRates.get(moveType).intValue();
		}

		/**
		 * Returns true if the Player Character has a movement value of the
		 * given type.
		 *
		 * @param moveType
		 *            The movement type to be tested to see if the Player
		 *            Character has a movement value of this type
		 * @return true if the Player Character has a movement value of the
		 *         given type; false otherwise
		 */
		public boolean hasMovement(MovementType moveType)
		{
			return moveRates.containsKey(moveType);
		}

		/**
		 * Works for dnd according to the method noted in the faq. (NOTE: The
		 * table in the dnd faq is wrong for speeds 80 and 90) Not as sure it
		 * works for all other d20 games.
		 *
		 * @param load
		 * @param unencumberedMove
		 *            the unencumbered move value
		 * @return encumbered move as an integer
		 */
		public double calcEncumberedMove(CharID id, Load load, double unencumberedMove)
		{
			double encumberedMove;

			//
			// Can we ignore any encumberance for this type? If we can, then
			// there's
			// no
			// need to do any more calculations.
			//
			if (unencumberedLoadFacet.ignoreLoad(id, load))
			{
				encumberedMove = unencumberedMove;
			}
			else
			{
				String formula = SettingsHandler.getGameAsProperty().get().getLoadInfo().getLoadMoveFormula(load.toString());
				if (!formula.isEmpty())
				{
					formula = formula.replaceAll(Pattern.quote("$$MOVE$$"),
						Double.toString(Math.floor(unencumberedMove)));
					return formulaResolvingFacet.resolve(id, FormulaFactory.getFormulaFor(formula), "").doubleValue();
				}

				return load.calcEncumberedMove(unencumberedMove);
			}

			return encumberedMove;
		}

		@Override
		public int hashCode()
		{
			return moveRates.hashCode();
		}

		@Override
		public boolean equals(Object o)
		{
			if (o == this)
			{
				return true;
			}
			if (o instanceof MovementCacheInfo ci)
			{
				return Objects.equals(moveRates, ci.moveRates);
			}
			return false;
		}
	}

	/**
	 * Returns the number of movement types for the Player Character identified
	 * by the given CharID.
	 *
	 * @param id
	 *            The CharID identifying the Player Character for which the
	 *            number of movement types is to be returned
	 * @return The number of movement types for the Player Character identified
	 *         by the given CharID
	 */
	public int countMovementTypes(CharID id)
	{
		MovementCacheInfo mci = getInfo(id);
		if (mci == null)
		{
			return 0;
		}
		return mci.countMovementTypes();
	}

	/**
	 * Recalculates all movement values for the Player Character identified by
	 * the given CharID.
	 *
	 * @param id
	 *            The CharID for which all of the movement values is to be
	 *            recalculated
	 */
	public void reset(CharID id)
	{
		getConstructingInfo(id).adjustMoveRates(id);
	}

	/**
	 * Returns a non-null List of the movement values for the Player Character
	 * represented by the given CharID.
	 *
	 * This method is value-semantic in that ownership of the returned List is
	 * transferred to the class calling this method. Modification of the
	 * returned List will not modify this MovementResultFacet and modification
	 * of this MovementResultFacet will not modify the returned List.
	 * Modifications to the returned List will also not modify any future or
	 * previous objects returned by this (or other) methods on
	 * MovementResultFacet. If you wish to modify the information stored in this
	 * MovementResultFacet, you must add Movement objects to the Player
	 * Character and call reset(CharID).
	 *
	 * @param id
	 *            The CharID identifying the Player Character for which the
	 *            movement values should be returned
	 * @return A non-null List of the movement values for the Player Character
	 *         represented by the given CharID.
	 */
	public List<NamedValue> getMovementValues(CharID id)
	{
		MovementCacheInfo mci = getInfo(id);
		if (mci == null)
		{
			return Collections.emptyList();
		}
		return mci.getMovementValues(id);
	}

	/**
	 * Returns the base movement value of the given type for the Player
	 * Character identified by the given CharID. No BONUSes are added to the
	 * movement before it is returned.
	 *
	 * @param id
	 *            The CharID identifying the Player Character for which the
	 *            movement value of the given type to be returned
	 * @param moveType
	 *            The movement type to be returned
	 * @return The movement value of the given type for the Player Character
	 *         identified by the given CharID
	 */
	public double getMovementOfType(CharID id, MovementType moveType)
	{
		MovementCacheInfo mci = getInfo(id);
		if (mci == null)
		{
			return 0.0d;
		}
		return mci.getMovementOfType(moveType);
	}

	/**
	 * Returns the base movement value of the given type for the Player
	 * Character identified by the given CharID, when the Player Character is
	 * under the given Load. No BONUSes are added to the movement before it is
	 * returned.
	 *
	 * @param id
	 *            The CharID identifying the Player Character for which the
	 *            movement value of the given type to be returned
	 * @param moveType
	 *            The movement type to be returned
	 * @param load
	 *            The Load to be used to calculate the base movement of the
	 *            Player Character
	 * @return The movement value of the given type for the Player Character
	 *         identified by the given CharID
	 */
	public int getBaseMovement(CharID id, MovementType moveType, Load load)
	{
		MovementCacheInfo mci = getInfo(id);
		if (mci == null)
		{
			return 0;
		}
		return mci.getBaseMovement(moveType, load);
	}

	/**
	 * Returns true if the Player Character identified by the given CharID has a
	 * movement value of the given type.
	 *
	 * @param id
	 *            The CharID identifying the Player Character which will be
	 *            tested to see if it contains a movement of the given type
	 * @param moveType
	 *            The movement type to be tested to see if the Player Character
	 *            has a movement value of this type
	 * @return true if the Player Character identified by the given CharID has a
	 *         movement value of the given type; false otherwise
	 */
	public boolean hasMovement(CharID id, MovementType moveType)
	{
		MovementCacheInfo mci = getInfo(id);
		if (mci == null)
		{
			return false;
		}
		return mci.hasMovement(moveType);
	}

	/**
	 * Triggers a full recalculation of Player Character movement when a
	 * CDOMObject is added to a Player Character.
	 *
	 * Triggered when one of the Facets to which MovementResultFacet listens
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
		reset(dfce.getCharID());
	}

	/**
	 * Triggers a full recalculation of Player Character movement when a
	 * CDOMObject is added to a Player Character.
	 *
	 * Triggered when one of the Facets to which MovementResultFacet listens
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
		reset(dfce.getCharID());
	}

	public void setMovementFacet(MovementFacet movementFacet)
	{
		this.movementFacet = movementFacet;
	}

	public void setMoveCloneFacet(MoveCloneFacet moveCloneFacet)
	{
		this.moveCloneFacet = moveCloneFacet;
	}

	public void setBaseMovementFacet(BaseMovementFacet baseMovementFacet)
	{
		this.baseMovementFacet = baseMovementFacet;
	}

	public void setRaceFacet(RaceFacet raceFacet)
	{
		this.raceFacet = raceFacet;
	}

	public void setTemplateFacet(TemplateFacet templateFacet)
	{
		this.templateFacet = templateFacet;
	}

	public void setEquipmentFacet(EquipmentFacet equipmentFacet)
	{
		this.equipmentFacet = equipmentFacet;
	}

	public void setBonusCheckingFacet(BonusCheckingFacet bonusCheckingFacet)
	{
		this.bonusCheckingFacet = bonusCheckingFacet;
	}

	public void setUnencumberedArmorFacet(UnencumberedArmorFacet unencumberedArmorFacet)
	{
		this.unencumberedArmorFacet = unencumberedArmorFacet;
	}

	public void setUnencumberedLoadFacet(UnencumberedLoadFacet unencumberedLoadFacet)
	{
		this.unencumberedLoadFacet = unencumberedLoadFacet;
	}

	public void setFormulaResolvingFacet(FormulaResolvingFacet formulaResolvingFacet)
	{
		this.formulaResolvingFacet = formulaResolvingFacet;
	}

	public void setLoadFacet(LoadFacet loadFacet)
	{
		this.loadFacet = loadFacet;
	}

	/**
	 * Initializes the connections for MovementResultFacet to other facets.
	 *
	 * This method is automatically called by the Spring framework during
	 * initialization of the MovementResultFacet.
	 */
	public void init()
	{
		raceFacet.addDataFacetChangeListener(2000, this);
		templateFacet.addDataFacetChangeListener(2000, this);
	}

	/**
	 * Copies the contents of the MovementResultFacet from one Player Character
	 * to another Player Character, based on the given CharIDs representing
	 * those Player Characters.
	 *
	 * This is a method in MovementResultFacet in order to avoid exposing the
	 * mutable Map object to other classes. This should not be inlined, as the
	 * Map is internal information to MovementResultFacet and should not be
	 * exposed to other classes.
	 *
	 * Note also the copy is a one-time event and no references are maintained
	 * between the Player Characters represented by the given CharIDs (meaning
	 * once this copy takes place, any change to the MovementResultFacet of one
	 * Player Character will only impact the Player Character where the
	 * MovementResultFacet was changed).
	 *
	 * @param source
	 *            The CharID representing the Player Character from which the
	 *            information should be copied
	 * @param copy
	 *            The CharID representing the Player Character to which the
	 *            information should be copied
	 */
	@Override
	public void copyContents(CharID source, CharID copy)
	{
		MovementCacheInfo mci = getInfo(source);
		if (mci != null)
		{
			MovementCacheInfo copymci = getConstructingInfo(copy);
			copymci.moveRates.putAll(mci.moveRates);
		}
	}

	private class MoveSorter implements Comparator<NamedValue>
	{

		@Override
		public int compare(NamedValue o1, NamedValue o2)
		{
			if (o1.getName().equals("Walk"))
			{
				return -1;
			}
			if (o2.getName().equals("Walk"))
			{
				return 1;
			}
			return o1.getName().compareTo(o2.getName());
		}

	}

}
