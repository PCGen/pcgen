/*
 * Copyright (c) Thomas Parker, 2014.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.facet.base.AbstractStorageFacet;
import pcgen.core.Globals;
import pcgen.core.Skill;

/**
 * SkillRankChangeFacet tracks changes to Bonus SKILLRANK values on a
 * PlayerCharacter and allows other classes to listen to such changes on a
 * Player Character.
 */
public class BonusSkillRankChangeFacet extends AbstractStorageFacet<CharID>
{
    /**
     * The SkillRankChangeSupport object that manages the listeners that receive
     * SkillRankChangeEvents from this SkillRankChangeFacet.
     */
    private final SkillRankChangeSupport support = new SkillRankChangeSupport();

    private BonusCheckingFacet bonusCheckingFacet;

    /**
     * Performs a check against the previously known values of the bonuses for
     * the Player Character identified by the given CharID. If any Bonus values
     * have changed, then this will throw a SkillRankChangeEvent to any
     * SkillRankChangeListener objects which have subscribed to receive updates
     * when values change.
     *
     * @param id The CharID identifying the Player Character for which the
     *           check for changes in bonus skillrank values should be
     *           performed
     */
    public void reset(CharID id)
    {
        Map<Skill, Double> map = getConstructingInfo(id);
        for (Skill s : Globals.getContext().getReferenceContext().getConstructedCDOMObjects(Skill.class))
        {
            double newValue = bonusCheckingFacet.getBonus(id, "SKILLRANK", s.getKeyName());

            for (Type singleType : s.getTrueTypeList(false))
            {
                newValue += bonusCheckingFacet.getBonus(id, "SKILLRANK", "TYPE." + singleType);
            }

            Double oldValue = map.get(s);
            if ((oldValue == null) || (newValue != oldValue))
            {
                map.put(s, newValue);
                support.fireSkillRankChange(id, s, oldValue, newValue);
            }
        }
    }

    /**
     * Returns a HashMap of SkillRank Bonus values for this SkillRankChangeFacet
     * and the PlayerCharacter represented by the given CharID. Will create a
     * new empty HashMap for the PlayerCharacter represented by the given CharID
     * if no information has been set in this SkillRankChangeFacet for the given
     * CharID. Will not return null.
     * <p>
     * Note that this method SHOULD NOT be public. The HashMap object is owned
     * by SkillRankChangeFacet, and since it can be modified, a reference to
     * that HashMap should not be exposed to any object other than
     * SkillRankChangeFacet.
     *
     * @param id The CharID for which the HashMap of bonus values should be
     *           returned
     * @return The HashMap of SkillRank Bonus values for the Player Character
     * represented by the given CharID
     */
    private HashMap<Skill, Double> getConstructingInfo(CharID id)
    {
        HashMap<Skill, Double> map = getInfo(id);
        if (map == null)
        {
            map = new HashMap<>();
            setCache(id, map);
        }
        return map;
    }

    /**
     * Returns a HashMap of Bonus values for this SkillRankChangeFacet and the
     * PlayerCharacter represented by the given CharID. May return null if no
     * information has been set in this SkillRankChangeFacet for the given
     * CharID.
     * <p>
     * Note that this method SHOULD NOT be public. The HashMap object is owned
     * by SkillRankChangeFacet, and since it can be modified, a reference to
     * that HashMap should not be exposed to any object other than
     * SkillRankChangeFacet.
     *
     * @param id The CharID for which the HashMap of bonus values should be
     *           returned
     * @return The HashMap of SkillRank Bonus values for the Player Character
     * represented by the given CharID
     */
    private HashMap<Skill, Double> getInfo(CharID id)
    {
        return (HashMap<Skill, Double>) getCache(id);
    }

    /**
     * SkillRankChangeListener is the interface that must be implemented by a
     * class for it to receive SkillRankChangeEvents from the
     * SkillRankChangeFacet when a SkillRank Bonus value has changed for a
     * Player Character.
     */
    @FunctionalInterface
    public interface SkillRankChangeListener
    {

        /**
         * Method called when a SkillRank Bonus value has changed on a Player
         * Character. The SkillRankChangeEvent contains the relevant details of
         * the SkillRank Bonus value change.
         *
         * @param srce The SkillRankChangeEvent containing the details of the
         *             SkillRank Bonus value change for a Player Character
         */
        void bonusChange(SkillRankChangeEvent srce);

    }

    /**
     * SkillRankChangeEvent is an event sent to a SkillRankChangeListener when a
     * SkillRank Bonus value changes on a Player Character.
     */
    public static class SkillRankChangeEvent
    {

        /**
         * The CharID identifying the Player Character on which the Bonus value
         * change took place.
         */
        private final CharID charID;

        /**
         * The Skill for which the SkillRank Bonus value changed on the Player
         * Character.
         */
        private final Skill skill;

        /**
         * The previous value of the Bonus value
         */
        private final Number oldVal;

        /**
         * The new value of the Bonus value
         */
        private final Number newVal;

        /**
         * Constructs a new SkillRankChangeEvent indicating a Bonus value change
         * took place on the Player Character identified by the given CharId.
         * The Bonus name, type, old value, and new value are provided.
         *
         * @param id       The CharID indicating the Player Character on which the
         *                 Bonus value change took place
         * @param sk       The Skill for the SkillRank Bonus value that changed
         * @param oldValue The previous value of the Bonus value
         * @param newValue The new value of the Bonus value
         */
        public SkillRankChangeEvent(CharID id, Skill sk, Number oldValue, Number newValue)
        {
            charID = id;
            skill = sk;
            oldVal = oldValue;
            newVal = newValue;
        }

        public CharID getCharID()
        {
            return charID;
        }

        public Skill getSkill()
        {
            return skill;
        }

        public Number getOldVal()
        {
            return oldVal;
        }

        public Number getNewVal()
        {
            return newVal;
        }

    }

    /**
     * SkillRankChangeSupport is a support class that provides the actual
     * structure for adding and removing listeners to a class that can provide
     * updates for changes to SkillRank Bonus values on a Player Character.
     */
    public static class SkillRankChangeSupport
    {
        private List<SkillRankChangeListener> listeners = new ArrayList<>();

        /**
         * Adds a new SkillRankChangeListener to receive SkillRankChangeEventas
         * from the change source. The given SkillRankChangeListener is
         * subscribed to all SkillRank Bonus value changes.
         * <p>
         * Note that the SkillRankChangeListeners are a list, meaning a given
         * SkillRankChangeListener can be added more than once, and if that
         * occurs, it must be removed an equivalent number of times in order to
         * no longer receive events from this SkillRankChangeSupport.
         *
         * @param listener The SkillRankChangeListener to receive
         *                 SkillRankChangeEvents from this SkillRankChangeSupport
         */
        public synchronized void addSkillRankChangeListener(SkillRankChangeListener listener)
        {
            listeners.add(listener);
        }

        /**
         * Removes a SkillRankChangeListener so that it will no longer receive
         * SkillRankChangeEvents from the source DataFacet.
         *
         * @param listener The SkillRankChangeListener to be removed
         */
        public synchronized void removeSkillRankChangeListener(SkillRankChangeListener listener)
        {
            listeners.remove(listener);
        }

        public synchronized SkillRankChangeListener[] getSkillRankChangeListeners()
        {
            return (listeners.toArray(new SkillRankChangeListener[0]));
        }

        /**
         * Sends a SkillRankChangeEvent to the SkillRankChangeListeners that are
         * receiving SkillRankChangeEvents from the change source.
         *
         * @param id       The CharID identifying the Player Character to which the
         *                 SkillRankChangeEvent relates.
         * @param skill    The skill for which the SkillRank Bonus value changed
         * @param oldValue The previous value of the Bonus value
         * @param newValue The new value of the Bonus value
         */
        public void fireSkillRankChange(CharID id, Skill skill, Number oldValue, Number newValue)
        {
            SkillRankChangeEvent bce = new SkillRankChangeEvent(id, skill, oldValue, newValue);

            for (SkillRankChangeListener target : listeners)
            {
                target.bonusChange(bce);
            }
        }
    }

    /**
     * Adds a new SkillRankChangeListener to receive SkillRankChangeEvents from
     * SkillRankChangeFacet. The given SkillRankChangeListener subscribed to all
     * SkillRank Bonus changes.
     * <p>
     * Note that the SkillRankChangeListeners are a list, meaning a given
     * SkillRankChangeListener can be added more than once, and if that occurs,
     * it must be removed an equivalent number of times in order to no longer
     * receive events from this SkillRankChangeFacet.
     *
     * @param listener The SkillRankChangeListener to receive SkillRankChangeEvents
     *                 from this SkillRankChangeFacet
     */
    public void addSkillRankChangeListener(SkillRankChangeListener listener)
    {
        support.addSkillRankChangeListener(listener);
    }

    /**
     * Removes a SkillRankChangeListener so that it will no longer receive
     * SkillRankChangeEvents from SkillRankChangeFacet.
     *
     * @param listener The SkillRankChangeListener to be removed
     */
    public void removeSkillRankChangeListener(SkillRankChangeListener listener)
    {
        support.removeSkillRankChangeListener(listener);
    }

    public void setBonusCheckingFacet(BonusCheckingFacet bonusCheckingFacet)
    {
        this.bonusCheckingFacet = bonusCheckingFacet;
    }

    /**
     * Copies the contents of the SkillRankChangeFacet from one Player Character
     * to another Player Character, based on the given CharIDs representing
     * those Player Characters.
     * <p>
     * This is a method in SkillRankChangeFacet in order to avoid exposing the
     * mutable HashMap object to other classes. This should not be inlined, as
     * the HashMap is internal information to SkillRankChangeFacet and should
     * not be exposed to other classes.
     * <p>
     * Note also the copy is a one-time event and no references are maintained
     * between the Player Characters represented by the given CharIDs (meaning
     * once this copy takes place, any change to the SkillRankChangeFacet of one
     * Player Character will only impact the Player Character where the
     * SkillRankChangeFacet was changed).
     *
     * @param source The CharID representing the Player Character from which the
     *               information should be copied
     * @param copy   The CharID representing the Player Character to which the
     *               information should be copied
     */
    @Override
    public void copyContents(CharID source, CharID copy)
    {
        Map<Skill, Double> map = getInfo(source);
        if (map != null)
        {
            getConstructingInfo(copy).putAll(map);
        }
    }

    public double getRank(CharID id, Skill skill)
    {
        Map<Skill, Double> map = getInfo(id);
        if (map != null)
        {
            Double rank = map.get(skill);
            if (rank != null)
            {
                return rank;
            }
        }
        return 0.0;
    }

}
