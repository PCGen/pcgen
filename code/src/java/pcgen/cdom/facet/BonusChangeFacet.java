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
import pcgen.cdom.facet.base.AbstractStorageFacet;

/**
 * BonusChangeFacet tracks changes to Bonus values on a PlayerCharacter and
 * allows other classes to listen to changes in Bonuses on a Player Character.
 */
public class BonusChangeFacet extends AbstractStorageFacet<CharID>
{
    /**
     * The BonusChangeSupport object that manages the listeners that receive
     * BonusChangeEvents from this BonusChangeFacet.
     */
    private final BonusChangeSupport support = new BonusChangeSupport();

    private BonusCheckingFacet bonusCheckingFacet;

    /**
     * Performs a check against the previously known values of the bonuses for
     * the Player Character identified by the given CharID. If any Bonus values
     * have changed, then this will throw a BonusChangeEvent to any
     * BonusChangeListener objects which have subscribed to receive updates for
     * the Bonus name and Bonus type where the value changed.
     *
     * @param id The CharID identifying the Player Character for which the
     *           check for changes in bonus values should be performed
     */
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

    /**
     * Returns a DoubleKeyMap of Bonus values for this BonusChangeFacet and the
     * PlayerCharacter represented by the given CharID. Will create a new empty
     * DoubleKeyMap for the PlayerCharacter represented by the given CharID if
     * no information has been set in this BonusChangeFacet for the given
     * CharID. Will not return null.
     * <p>
     * Note that this method SHOULD NOT be public. The DoubleKeyMap object is
     * owned by BonusChangeFacet, and since it can be modified, a reference to
     * that DoubleKeyMap should not be exposed to any object other than
     * BonusChangeFacet.
     *
     * @param id The CharID for which the DoubleKeyMap of bonus values should
     *           be returned
     * @return The DoubleKeyMap of Bonus values for the Player Character
     * represented by the given CharID
     */
    private DoubleKeyMap<String, String, Double> getConstructingInfo(CharID id)
    {
        DoubleKeyMap<String, String, Double> map = getInfo(id);
        if (map == null)
        {
            map = new DoubleKeyMap<>();
            setCache(id, map);
        }
        return map;
    }

    /**
     * Returns a DoubleKeyMap of Bonus values for this BonusChangeFacet and the
     * PlayerCharacter represented by the given CharID. May return null if no
     * information has been set in this BonusChangeFacet for the given CharID.
     * <p>
     * Note that this method SHOULD NOT be public. The DoubleKeyMap object is
     * owned by BonusChangeFacet, and since it can be modified, a reference to
     * that DoubleKeyMap should not be exposed to any object other than
     * BonusChangeFacet.
     *
     * @param id The CharID for which the DoubleKeyMap of bonus values should
     *           be returned
     * @return The DoubleKeyMap of Bonus values for the Player Character
     * represented by the given CharID
     */
    private DoubleKeyMap<String, String, Double> getInfo(CharID id)
    {
        return (DoubleKeyMap<String, String, Double>) getCache(id);
    }

    /**
     * BonusChangeListener is the interface that must be implemented by a class
     * for it to receive BonusChangeEvents from the BonusChangeFacet when a
     * Bonus value has changed for a Player Character.
     */
    @FunctionalInterface
    public interface BonusChangeListener
    {

        /**
         * Method called when a Bonus value has changed on a Player Character.
         * The BonusChangeEvent contains the relevant details of the Bonus value
         * change.
         *
         * @param bce The BonusChangeEvent containing the details of the Bonus
         *            value change for a Player Character
         */
        void bonusChange(BonusChangeEvent bce);

    }

    /**
     * BonusChangeEvent is an event sent to a BonusChangeListener when a Bonus
     * value changes on a Player Character.
     */
    public static class BonusChangeEvent
    {

        /**
         * The CharID identifying the Player Character on which the Bonus value
         * change took place.
         */
        private final CharID charID;

        /**
         * The Bonus type indicating which Bonus value changed on the Player
         * Character.
         */
        private final String bonusType;

        /**
         * The Bonus name indicating which Bonus value changed on the Player
         * Character.
         */
        private final String bonusName;

        /**
         * The previous value of the Bonus value
         */
        private final Number oldVal;

        /**
         * The new value of the Bonus value
         */
        private final Number newVal;

        /**
         * Constructs a new BonusChangeEvent indicating a Bonus value change
         * took place on the Player Character identified by the given CharId.
         * The Bonus name, type, old value, and new value are provided.
         *
         * @param id       The CharID indicating the Player Character on which the
         *                 Bonus value change took place
         * @param type     The Bonus type for the Bonus value that changed
         * @param name     The Bonus name for the Bonus value that changed
         * @param oldValue The previous value of the Bonus value
         * @param newValue The new value of the Bonus value
         */
        public BonusChangeEvent(CharID id, String type, String name, Number oldValue, Number newValue)
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
     * BonusChangeSupport is a support class that provides the actual structure
     * for adding and removing listeners to a class that can provide updates for
     * changes to Bonus values on a Player Character.
     */
    public static class BonusChangeSupport
    {
        private DoubleKeyMapToList<String, String, BonusChangeListener> listeners = new DoubleKeyMapToList<>();

        /**
         * Adds a new BonusChangeListener to receive BonusChangeEventas from the
         * change source. The given BonusChangeListener is subscribed to Bonus
         * value changes for the given Bonus type and Bonus name.
         * <p>
         * Note that the BonusChangeListeners are a list, meaning a given
         * BonusChangeListener can be added more than once at a given priority,
         * and if that occurs, it must be removed an equivalent number of times
         * in order to no longer receive events from this BonusChangeSupport.
         *
         * @param listener The BonusChangeListener to receive BonusChangeEvents from
         *                 this BonusChangeSupport
         * @param type     The Bonus type for the Bonus value changes for which the
         *                 given listener will be added to the list of listeners
         * @param name     The Bonus name for the Bonus value changes for which the
         *                 given listener will be added to the list of listeners
         */
        public synchronized void addBonusChangeListener(BonusChangeListener listener, String type, String name)
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

        /**
         * Removes a BonusChangeListener so that it will no longer receive
         * BonusChangeEvents from the source DataFacet. This will remove the
         * data facet change listener from receiving events for the given Bonus
         * type and Bonus name.
         * <p>
         * Note that if the given BonusChangeListener has been registered under
         * a different Bonus type and Bonus name, it will continue to receive
         * events for those Bonus value changes.
         *
         * @param listener The BonusChangeListener to be removed
         * @param type     The Bonus type for the Bonus value changes for which the
         *                 given listener will be removed from the list of listeners
         * @param name     The Bonus name for the Bonus value changes for which the
         *                 given listener will be removed from the list of listeners
         */
        public synchronized void removeBonusChangeListener(BonusChangeListener listener, String type, String name)
        {
            listeners.removeFromListFor(type, name, listener);
        }

        public synchronized BonusChangeListener[] getBonusChangeListeners(String type, String name)
        {
            List<BonusChangeListener> listFor = listeners.getListFor(type, name);
            return (listFor.toArray(new BonusChangeListener[0]));
        }

        /**
         * Sends a BonusChangeEvent to the BonusChangeListeners that are
         * receiving BonusChangeEvents from the change source.
         *
         * @param id       The CharID identifying the Player Character to which the
         *                 BonusChangeEvent relates.
         * @param type     The Bonus type for the Bonus value that changed
         * @param name     The Bonus name for the Bonus value that changed
         * @param oldValue The previous value of the Bonus value
         * @param newValue The new value of the Bonus value
         */
        public void fireBonusChange(CharID id, String type, String name, Number oldValue, Number newValue)
        {
            BonusChangeEvent bce = new BonusChangeEvent(id, type, name, oldValue, newValue);

            List<BonusChangeListener> localListeners = listeners.getListFor(type, name);
            if (localListeners != null)
            {
                for (BonusChangeListener target : localListeners)
                {
                    target.bonusChange(bce);
                }
            }
        }
    }

    /**
     * Adds a new BonusChangeListener to receive BonusChangeEvents from
     * BonusChangeFacet. The given BonusChangeListener subscribed to changes for
     * the given Bonus type and Bonus name.
     * <p>
     * Note that the BonusChangeListeners are a list, meaning a given
     * BonusChangeListener can be added more than once for a given Bonus type
     * and Bonus name, and if that occurs, it must be removed an equivalent
     * number of times in order to no longer receive events from this
     * BonusChangeFacet.
     *
     * @param listener The BonusChangeListener to receive BonusChangeEvents from this
     *                 BonusChangeFacet
     * @param type     The Bonus type for the Bonus value changes for which the given
     *                 listener will be added to the list of listeners
     * @param name     The Bonus name for the Bonus value changes for which the given
     *                 listener will be added to the list of listeners
     */
    public void addBonusChangeListener(BonusChangeListener listener, String type, String name)
    {
        support.addBonusChangeListener(listener, type, name);
    }

    /**
     * Removes a BonusChangeListener so that it will no longer receive
     * BonusChangeEvents from BonusChangeFacet. This will remove the data facet
     * change listener from the list of listeners only for the given Bonus type
     * and Bonus name.
     * <p>
     * Note that if the given BonusChangeListener has been registered under a
     * different Bonus type and Bonus name, it will continue to receive events
     * for those Bonus value changes.
     *
     * @param listener The BonusChangeListener to be removed
     * @param type     The Bonus type for the Bonus value changes for which the given
     *                 listener will be removed from the list of listeners
     * @param name     The Bonus name for the Bonus value changes for which the given
     *                 listener will be removed from the list of listeners
     */
    public void removeBonusChangeListener(BonusChangeListener listener, String type, String name)
    {
        support.removeBonusChangeListener(listener, type, name);
    }

    public void setBonusCheckingFacet(BonusCheckingFacet bonusCheckingFacet)
    {
        this.bonusCheckingFacet = bonusCheckingFacet;
    }

    /**
     * Copies the contents of the BonusChangeFacet from one Player Character to
     * another Player Character, based on the given CharIDs representing those
     * Player Characters.
     * <p>
     * This is a method in BonusChangeFacet in order to avoid exposing the
     * mutable DoubleKeyMap object to other classes. This should not be inlined,
     * as the DoubleKeyMap is internal information to AbstractListFacet and
     * should not be exposed to other classes.
     * <p>
     * Note also the copy is a one-time event and no references are maintained
     * between the Player Characters represented by the given CharIDs (meaning
     * once this copy takes place, any change to the BonusChangeFacet of one
     * Player Character will only impact the Player Character where the
     * BonusChangeFacet was changed).
     *
     * @param source The CharID representing the Player Character from which the
     *               information should be copied
     * @param copy   The CharID representing the Player Character to which the
     *               information should be copied
     */
    @Override
    public void copyContents(CharID source, CharID copy)
    {
        DoubleKeyMap<String, String, Double> map = getInfo(source);
        if (map != null)
        {
            getConstructingInfo(copy).putAll(map);
        }
    }

}
