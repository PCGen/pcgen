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
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.BonusSkillRankChangeFacet.SkillRankChangeEvent;
import pcgen.cdom.facet.SkillRankFacet.SkillRankChangeListener;
import pcgen.cdom.facet.base.AbstractStorageFacet;
import pcgen.cdom.facet.event.AssociationChangeEvent;
import pcgen.cdom.facet.event.AssociationChangeListener;
import pcgen.core.Skill;

/**
 * TotalSkillRankFacet stores the total skill rank for Skills (includes user
 * taken ranks and BONUS:SKILLRANK)
 */
public class TotalSkillRankFacet extends AbstractStorageFacet<CharID>
        implements SkillRankChangeListener, pcgen.cdom.facet.BonusSkillRankChangeFacet.SkillRankChangeListener
{
    private static final Double DOUBLE_ZERO = 0.0d;

    private SkillRankFacet skillRankFacet;

    private BonusSkillRankChangeFacet bonusSkillRankChangeFacet;

    /**
     * Set the given association for the given object in this
     * AbstractAssociationFacet for the Player Character represented by the
     * given CharID
     *
     * @param id   The CharID representing the Player Character for which the
     *             given skill rank should be set
     * @param sk   The skill for which the rank will be set
     * @param rank The rank for the given skill
     */
    public void set(CharID id, Skill sk, Double rank)
    {
        Objects.requireNonNull(sk, "Skill cannot be null");
        Objects.requireNonNull(rank, "Rank cannot be null");
        Map<Skill, Double> map = getConstructingInfo(id);
        Double currentRank = map.get(sk);
        boolean isNew = (currentRank == null) || (rank.doubleValue() != currentRank.doubleValue());
        if (isNew)
        {
            map.put(sk, rank);
            if (support != null)
            {
                support.fireAssociationChange(id, sk, currentRank, rank);
            }
        }
    }

    /**
     * Removes the association for the given source object in this
     * AbstractAssociationFacet for the Player Character represented by the
     * given CharID.
     *
     * @param id The CharID representing the Player Character from which the
     *           given item association should be removed
     * @param sk The skill for which the rank should be removed
     */
    public void remove(CharID id, Skill sk)
    {
        Objects.requireNonNull(sk, "Skill cannot be null");
        Map<Skill, Double> map = getInfo(id);
        if (map == null)
        {
            return;
        }
        Double currentRank = map.get(sk);
        if (currentRank == null)
        {
            return;
        }
        if (support != null)
        {
            support.fireAssociationChange(id, sk, currentRank, DOUBLE_ZERO);
        }
    }

    /**
     * Copies the contents of the AbstractScopeFacet from one Player Character
     * to another Player Character, based on the given CharIDs representing
     * those Player Characters.
     * <p>
     * This is a method in AbstractScopeFacet in order to avoid exposing the
     * mutable Map object to other classes. This should not be inlined, as the
     * Map is internal information to AbstractScopeFacet and should not be
     * exposed to other classes.
     * <p>
     * Note also the copy is a one-time event and no references are maintained
     * between the Player Characters represented by the given CharIDs (meaning
     * once this copy takes place, any change to the AbstractScopeFacet of one
     * Player Character will only impact the Player Character where the
     * AbstractScopeFacet was changed).
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

    /**
     * The AssociationChangeSupport object that manages the listeners that
     * receive AssociationChangeEvents from this AssociationChangeFacet.
     */
    private AssociationChangeSupport support;

    /**
     * Gets the association for the Player Character (identified by the given
     * CharID) and the given source object.
     *
     * @param id  The CharID identifying the Player Character for which the
     *            association get is being performed.
     * @param obj The source object for which the association get is being
     *            performed.
     * @return The association for the Player Character (identified by the given
     * CharID) and the given source object
     */
    public Double get(CharID id, Skill obj)
    {
        Objects.requireNonNull(obj, "Object for getting association may not be null");
        Map<Skill, Double> map = getInfo(id);
        if (map != null)
        {
            return map.get(obj);
        }
        return null;
    }

    /**
     * AssociationChangeSupport is a support class that provides the actual
     * structure for adding and removing listeners to a class that can provide
     * updates for changes to Association Bonus values on a Player Character.
     */
    public static class AssociationChangeSupport
    {
        private final Object source;

        public AssociationChangeSupport(Object src)
        {
            source = src;
        }

        private final List<AssociationChangeListener> listeners = new ArrayList<>();

        /**
         * Adds a new AssociationChangeListener to receive
         * AssociationChangeEventas from the change source. The given
         * AssociationChangeListener is subscribed to all Association Bonus
         * value changes.
         * <p>
         * Note that the AssociationChangeListeners are a list, meaning a given
         * AssociationChangeListener can be added more than once, and if that
         * occurs, it must be removed an equivalent number of times in order to
         * no longer receive events from this AssociationChangeSupport.
         *
         * @param listener The AssociationChangeListener to receive
         *                 AssociationChangeEvents from this AssociationChangeSupport
         */
        public synchronized void addAssociationChangeListener(AssociationChangeListener listener)
        {
            listeners.add(listener);
        }

        /**
         * Removes a AssociationChangeListener so that it will no longer receive
         * AssociationChangeEvents from the source DataFacet.
         *
         * @param listener The AssociationChangeListener to be removed
         */
        public synchronized void removeAssociationChangeListener(AssociationChangeListener listener)
        {
            listeners.remove(listener);
        }

        public synchronized AssociationChangeListener[] getAssociationChangeListeners()
        {
            return (listeners.toArray(new AssociationChangeListener[0]));
        }

        /**
         * Sends a AssociationChangeEvent to the AssociationChangeListeners that
         * are receiving AssociationChangeEvents from the change source.
         *
         * @param id       The CharID identifying the Player Character to which the
         *                 AssociationChangeEvent relates.
         * @param skill    The skill for which the Association Bonus value changed
         * @param oldValue The previous value of the Bonus value
         * @param newValue The new value of the Bonus value
         */
        public void fireAssociationChange(CharID id, Skill skill, Number oldValue, Number newValue)
        {
            AssociationChangeEvent bce = new AssociationChangeEvent(id, skill, oldValue, newValue, source);

            for (AssociationChangeListener target : listeners)
            {
                target.bonusChange(bce);
            }
        }
    }

    /**
     * Adds a new AssociationChangeListener to receive AssociationChangeEvents
     * from AssociationChangeFacet. The given AssociationChangeListener
     * subscribed to all Association Bonus changes.
     * <p>
     * Note that the AssociationChangeListeners are a list, meaning a given
     * AssociationChangeListener can be added more than once, and if that
     * occurs, it must be removed an equivalent number of times in order to no
     * longer receive events from this AssociationChangeFacet.
     *
     * @param listener The AssociationChangeListener to receive
     *                 AssociationChangeEvents from this AssociationChangeFacet
     */
    public void addAssociationChangeListener(AssociationChangeListener listener)
    {
        if (support == null)
        {
            support = new AssociationChangeSupport(this);
        }
        support.addAssociationChangeListener(listener);
    }

    /**
     * Removes a AssociationChangeListener so that it will no longer receive
     * AssociationChangeEvents from AssociationChangeFacet.
     *
     * @param listener The AssociationChangeListener to be removed
     */
    public void removeAssociationChangeListener(AssociationChangeListener listener)
    {
        if (support == null)
        {
            support = new AssociationChangeSupport(this);
        }
        support.removeAssociationChangeListener(listener);
    }

    private Map<Skill, Double> getConstructingInfo(CharID id)
    {
        Map<Skill, Double> map = getInfo(id);
        if (map == null)
        {
            map = new IdentityHashMap<>();
            setCache(id, map);
        }
        return map;
    }

    private Map<Skill, Double> getInfo(CharID id)
    {
        return (Map<Skill, Double>) getCache(id);
    }

    @Override
    public void bonusChange(SkillRankChangeEvent srce)
    {
        CharID id = srce.getCharID();
        Skill skill = srce.getSkill();
        double newBonus = srce.getNewVal().doubleValue();
        float rank = skillRankFacet.getRank(id, skill);
        set(id, skill, rank + newBonus);
    }

    @Override
    public void rankChanged(pcgen.cdom.facet.SkillRankFacet.SkillRankChangeEvent srce)
    {
        CharID id = srce.getCharID();
        Skill skill = srce.getSkill();
        float newRank = srce.getNewRank();
        double bonus = bonusSkillRankChangeFacet.getRank(id, skill);
        set(id, skill, newRank + bonus);
    }

    public void init()
    {
        skillRankFacet.addSkillRankChangeListener(this);
        bonusSkillRankChangeFacet.addSkillRankChangeListener(this);
    }

    public void setSkillRankFacet(SkillRankFacet skillRankFacet)
    {
        this.skillRankFacet = skillRankFacet;
    }

    public void setBonusSkillRankChangeFacet(BonusSkillRankChangeFacet bonusSkillRankChangeFacet)
    {
        this.bonusSkillRankChangeFacet = bonusSkillRankChangeFacet;
    }

}
