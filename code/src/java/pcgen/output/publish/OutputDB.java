/*
 * Copyright (c) Thomas Parker, 2015.
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
package pcgen.output.publish;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.DoubleKeyMap;
import pcgen.cdom.base.ItemFacet;
import pcgen.cdom.base.SetFacet;
import pcgen.cdom.enumeration.CharID;
import pcgen.core.GameMode;
import pcgen.output.base.ModeModelFactory;
import pcgen.output.base.ModelFactory;
import pcgen.output.factory.ItemModelFactory;
import pcgen.output.factory.SetModelFactory;
import pcgen.output.model.BooleanOptionModel;

import freemarker.template.TemplateModel;

/**
 * OutputDB is the OutputDatabase for building the Map to be provided to
 * FreeMarker for output.
 */
public final class OutputDB
{

    private OutputDB()
    {
        //Utility class should not be constructed
    }

    /**
     * The Map of string names to output models (that are dynamic based on a PC)
     */
    private static final DoubleKeyMap<Object, Object, ModelFactory> outModels =
            new DoubleKeyMap<>(CaseInsensitiveMap.class, CaseInsensitiveMap.class);

    /**
     * The map of string names to models for global items (not PC dependent)
     */
    private static final Map<Object, TemplateModel> globalModels = new CaseInsensitiveMap<>();

    /**
     * The Map of string names to output models for the Game Mode
     */
    private static final Map<Object, ModeModelFactory> modeModels = new CaseInsensitiveMap<>();

    /**
     * Registers a new ModelFactory to be used in output
     *
     * @param name         The interpolation for the ModelFactory
     * @param modelFactory The ModelFactory to be used to generate the Models when the
     *                     output Map is built
     */
    public static void registerModelFactory(String name, ModelFactory modelFactory)
    {
        Objects.requireNonNull(modelFactory, "Model Factory may not be null");
        String[] locationElements = name.split("\\.");
        if (locationElements.length == 0)
        {
            throw new IllegalArgumentException("Name may not be null or empty: " + name);
        }
        if (locationElements.length > 2)
        {
            throw new IllegalArgumentException("Name may only contain zero or one period");
        }
        String secondName = (locationElements.length == 1) ? "" : locationElements[1];
        ModelFactory old = outModels.put(locationElements[0], secondName, modelFactory);
        if (old != null)
        {
            throw new UnsupportedOperationException("Cannot have two Output Models using the same name: " + name);
        }
    }

    /**
     * Registers a new ItemFacet with the OutputDatabase using the given name as
     * the interpolation for fetching information from the given ItemFacet.
     *
     * @param name  The name as the interpolation for fetching information from
     *              the given ItemFacet during output
     * @param facet The ItemFacet to be registered with the given name
     */
    public static void register(String name, ItemFacet<CharID, ?> facet)
    {
        registerModelFactory(name, new ItemModelFactory(facet));
    }

    /**
     * Registers a new SetFacet with the OutputDatabase using the given name as
     * the interpolation for fetching information from the given SetFacet.
     *
     * @param name  The name as the interpolation for fetching information from
     *              the given SetFacet during output
     * @param facet The SetFacet to be registered with the given name
     */
    public static void register(String name, SetFacet<CharID, ?> facet)
    {
        registerModelFactory(name, new SetModelFactory(facet));
    }

    /**
     * Builds the PlayerCharacter data model for the given CharID.
     *
     * @param id The CharID for which the data model should be built
     * @return A Map of the data model for the PlayerCharacter identified by the
     * given CharID
     */
    public static Map<String, Object> buildDataModel(CharID id)
    {
        Map<String, Object> input = new HashMap<>();
        for (Object k1 : outModels.getKeySet())
        {
            for (Object k2 : outModels.getSecondaryKeySet(k1))
            {
                ModelFactory modelFactory = outModels.get(k1, k2);
                TemplateModel model = modelFactory.generate(id);
                String k1String = k1.toString();
                if ("".equals(k2.toString()))
                {
                    input.put(k1String, model);
                } else
                {
                    ensureMap(input, k1String);
                    @SuppressWarnings("unchecked")
                    Map<Object, Object> m = (Map<Object, Object>) input.get(k1String);
                    m.put(k2.toString(), model);
                }
            }
        }
        return input;
    }

    private static void ensureMap(Map<String, Object> input, String k1String)
    {
        if (!input.containsKey(k1String))
        {
            input.put(k1String, new HashMap<>());
        }
    }

    /**
     * Builds the "game mode" data model
     *
     * @return Returns a Map containing the "game mode" information
     */
    public static Map<String, Object> buildModeDataModel(GameMode mode)
    {
        Map<String, Object> input = new HashMap<>();
        modeModels.forEach((key, modelFactory) ->
                input.put(key.toString(), modelFactory.generate(mode)));
        return input;
    }

    /**
     * Registers a ModeModelFactory under the given name.
     * <p>
     * Note that only one ModeModelFactory can be registered under a given (case
     * insensitive) name. Additional items registered under the same name will
     * cause an UnsupportedOperationException.
     *
     * @param name    The Name the given ModeModelFactory should be registered under
     *                for use as an interpolation under gamemode. in FreeMarker
     * @param factory The ModeModelFactory to be registered under the given name
     */
    public static void registerMode(String name, ModeModelFactory factory)
    {
        Objects.requireNonNull(factory, "Model Factory may not be null");
        int dotLoc = name.indexOf('.');
        if (dotLoc != -1)
        {
            throw new IllegalArgumentException("Name may not contain a dot: " + name);
        }
        ModeModelFactory old = modeModels.put(name, factory);
        if (old != null)
        {
            throw new UnsupportedOperationException("Cannot have two Mode Models using the same name: " + name);
        }
    }

    /**
     * Returns a specific portion of the PlayerCharacter data model for the
     * given CharID and selection string.
     *
     * @param id   The CharID for which the data model should be built
     * @param keys A String (or array) of keys identifying the portion of the
     *             data model to be built
     * @return An Iterable for the portion of the data model identified by the
     * given Strings and the PlayerCharacter identified by the given
     * CharID
     */
    public static Iterable<?> getIterable(CharID id, String... keys)
    {
        String k1 = keys[0];
        String k2 = (keys.length > 1) ? keys[1] : "";
        ModelFactory modelFactory = outModels.get(k1, k2);
        if (modelFactory == null)
        {
            return null;
        }
        return modelFactory.generate(id);
    }

    /**
     * Returns true if the given interpolation is legal based on the items
     * registered with OutputDB.
     *
     * @param interpolation The interpolation to be checked to see if it is legal
     * @return true if the given interpolation is legal based on the items
     * registered with OutputDB; false otherwise
     */
    public static boolean isLegal(String interpolation)
    {
        return outModels.containsKey(interpolation);
    }

    /**
     * Resets the Output Database, to be used when sources are purged/reloaded
     * or around testing
     */
    public static void reset()
    {
        outModels.clear();
        globalModels.clear();
        modeModels.clear();
    }

    /**
     * Returns a map of the global TemplateModel objects (those that do not
     * depend on a PC)
     * <p>
     * Note that ownership of the returned map is transferred to the calling
     * object, no changes to the returned map will impact OutputDB, nor will
     * changes to OutputDB impact the returned Map.
     *
     * @return a Map of the global TemplateModel objects
     */
    public static Map<Object, TemplateModel> getGlobal()
    {
        Map<Object, TemplateModel> map = new CaseInsensitiveMap<>();
        map.putAll(globalModels);
        return map;
    }

    /**
     * Registers a new Boolean Preference for inclusion in the global Models.
     *
     * @param pref         The preference name, as identified in the preference file
     * @param defaultValue The default value for the preference if it is not defined
     */
    public static void registerBooleanPreference(String pref, boolean defaultValue)
    {
        if ((pref == null) || (pref.isEmpty()))
        {
            throw new IllegalArgumentException("Preference Name may not be null or empty: " + pref);
        }
        addGlobalModel(pref, new BooleanOptionModel(pref, defaultValue));
    }

    /**
     * Directly adds a new TemplateModel as part of the "Global" Models in
     * OutputDB.
     *
     * @param name  The name to be used when the TemplateModel is referred to in
     *              FreeMarker
     * @param model The TemplateModel to be added to the global models
     */
    public static void addGlobalModel(String name, TemplateModel model)
    {
        TemplateModel old = globalModels.put(name, model);
        if (old != null)
        {
            throw new UnsupportedOperationException(
                    "Cannot have two Global Output Models using the same name: " + name);
        }
    }
}
