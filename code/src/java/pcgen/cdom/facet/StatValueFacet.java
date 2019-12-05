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

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VariableID;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractScopeFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.formula.MonitorableVariableStore;
import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
import pcgen.core.Globals;
import pcgen.core.PCStat;
import pcgen.output.channel.ChannelUtilities;

/**
 * StatValueFacet stores the values of PCStat objects (such as Strength is 18) for a
 * Player Character.
 */
public class StatValueFacet extends AbstractScopeFacet<CharID, PCStat, Number>
{

    /**
     * The global LoadContextFacet used to get VariableIDs
     */
    private final LoadContextFacet loadContextFacet = FacetLibrary.getFacet(LoadContextFacet.class);

    private static final ScopeFacet SCOPE_FACET = FacetLibrary.getFacet(ScopeFacet.class);
    private static final VariableStoreFacet RESULT_FACET = FacetLibrary.getFacet(VariableStoreFacet.class);

    /**
     * Gets the stat value for the PlayerCharacter identified by the given CharID and the
     * given source PCStat.
     *
     * @param id   The CharID identifying the PlayerCharacter for which the stat value get
     *             is being performed.
     * @param stat The source PCStat for which the value get is being performed.
     * @return The value for the PlayerCharacter (identified by the given CharID) and the
     * given source PCStat
     */
    public Number get(CharID id, PCStat stat)
    {
        Objects.requireNonNull(stat, "Object for getting stat value may not be null");
        String channelName = getStatChannelName();
        if (channelName == null)
        {
            Map<PCStat, Number> map = getCachedMap(id);
            if (map != null)
            {
                return map.get(stat);
            }
            return null;
        } else
        {
            VariableID<Number> varID = getVarID(id, stat, channelName);
            MonitorableVariableStore varStore = RESULT_FACET.get(id);
            return varStore.get(varID);
        }
    }

    /**
     * Set the given value for the given PCStat in this StatValueFacet for the
     * PlayerCharacter represented by the given CharID
     *
     * @param id    The CharID representing the PlayerCharacter for which the given stat
     *              value should be set
     * @param stat  The PCStat for which the value will be set
     * @param value The value for the given PCStat
     */
    public void set(CharID id, PCStat stat, Number value)
    {
        Objects.requireNonNull(stat, "Object to add may not be null");
        Objects.requireNonNull(value, "Association may not be null");
        String channelName = getStatChannelName();
        Number old;
        if (channelName == null)
        {
            old = getConstructingCachedMap(id).put(stat, value);
        } else
        {
            VariableID<Number> varID = getVarID(id, stat, channelName);
            MonitorableVariableStore varStore = RESULT_FACET.get(id);
            old = varStore.get(varID);
            varStore.put(varID, value);
        }
        if (old != null)
        {
            fireScopeFacetChangeEvent(id, stat, old, DataFacetChangeEvent.DATA_REMOVED);
        }
        fireScopeFacetChangeEvent(id, stat, value, DataFacetChangeEvent.DATA_ADDED);
    }

    /**
     * Returns the type-safe Map for this StatValueFacet and the given CharID. May return
     * null if no information has been set in this StatValueFacet for the given CharID.
     * <p>
     * Note that this method SHOULD NOT be public. The Map is owned by StatValueFacet, and
     * since it can be modified, a reference to that object should not be exposed to any
     * object other than StatValueFacet.
     *
     * @param id The CharID for which the Map should be returned
     * @return The Map for the item represented by the given CharID; null if no
     * information has been set in this StatValueFacet for the item.
     */
    private Map<PCStat, Number> getCachedMap(CharID id)
    {
        return (Map<PCStat, Number>) getCache(id);
    }

    /**
     * Returns a type-safe Map for this StatValueFacet and the given CharID. Will return a
     * new, empty Map if no information has been set in this StatValueFacet for the given
     * CharID. Will not return null.
     * <p>
     * Note that this method SHOULD NOT be public. The Map object is owned by
     * StatValueFacet, and since it can be modified, a reference to that object should not
     * be exposed to any object other than StatValueFacet.
     *
     * @param id The CharID for which the Map should be returned
     * @return The Map for the item represented by the given CharID.
     */
    private Map<PCStat, Number> getConstructingCachedMap(CharID id)
    {
        Map<PCStat, Number> componentMap = getCachedMap(id);
        if (componentMap == null)
        {
            componentMap = new IdentityHashMap<>();
            setCache(id, componentMap);
        }
        return componentMap;
    }

    /**
     * Copies the contents of the StatValueFacet from one PlayerCharacter to another
     * PlayerCharacter, based on the given CharIDs representing those PlayerCharacters.
     * <p>
     * This is a method in StatValueFacet in order to avoid exposing the mutable Map
     * object to other classes. This should not be inlined, as the Map is internal
     * information to StatValueFacet and should not be exposed to other classes.
     * <p>
     * Note also the copy is a one-time event and no references are maintained between the
     * PlayerCharacters represented by the given CharIDs (meaning once this copy takes
     * place, any change to the StatValueFacet of one PlayerCharacter will only impact the
     * PlayerCharacter where the StatValueFacet was changed).
     *
     * @param source      The CharID representing the PlayerCharacter from which the information
     *                    should be copied
     * @param destination The CharID representing the PlayerCharacter to which the information
     *                    should be copied
     */
    @Override
    public void copyContents(CharID source, CharID destination)
    {
        String channelName = getStatChannelName();
        if (channelName == null)
        {
            Map<PCStat, Number> sourceMap = getCachedMap(source);
            if (sourceMap != null)
            {
                getConstructingCachedMap(destination).putAll(sourceMap);
            }
        }
    }

    private String getStatChannelName()
    {
        return ControlUtilities.getControlToken(Globals.getContext(), CControl.STATINPUT);
    }

    private VariableID<Number> getVarID(CharID id, PCStat stat, String channelName)
    {
        String varName = ChannelUtilities.createVarName(channelName);
        ScopeInstanceFactory instFactory = SCOPE_FACET.get(id);
        Optional<String> localScopeName = stat.getLocalScopeName();
        ScopeInstance scopeInst = instFactory.get(localScopeName.get(), Optional.of(stat));
        try
        {
            VariableID<Number> varID = (VariableID<Number>) loadContextFacet.get(id.getDatasetID()).get()
                    .getVariableContext().getVariableID(scopeInst, varName);
            return varID;
        } catch (NullPointerException e)
        {
            throw new IllegalArgumentException("Attempt to get channel " + channelName
                    + " for a STAT was unsuccessful. Was a CHANNEL defined in the Variable file?", e);
        }
    }

}
