/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
import java.util.ListIterator;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.LevelCommandFactory;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.gui2.UIPropertyContext;

/**
 * AddLevelFacet performs the addition of levels to a Player Character that are
 * defined by the ADDLEVEL token.
 */
public class AddLevelFacet implements DataFacetChangeListener<CharID, PCTemplate>
{

    private final PlayerCharacterTrackingFacet trackingFacet =
            FacetLibrary.getFacet(PlayerCharacterTrackingFacet.class);

    private TemplateFacet templateFacet;

    /**
     * Drives the necessary results of an ADDLEVEL: token to apply the results
     * to a Player Character.
     * <p>
     * Triggered when one of the Facets to which AddLevelFacet listens fires a
     * DataFacetChangeEvent to indicate a CDOMObject was added to a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, PCTemplate> dfce)
    {
        PCTemplate template = dfce.getCDOMObject();
        CharID id = dfce.getCharID();
        PlayerCharacter pc = trackingFacet.getPC(id);

        // If we are importing these levels will have been saved with the
        // character so don't apply them again.
        if (!pc.isImporting())
        {
            for (LevelCommandFactory lcf : template.getSafeListFor(ListKey.ADD_LEVEL))
            {
                add(lcf.getLevelCount(), lcf.getPCClass(), pc);
            }
        }
    }

    /**
     * Drives the necessary removal of the results of an ADDLEVEL: token to
     * remove the added levels from a Player Character because the object
     * granting the ADDLEVEL: was removed from the Player Character.
     * <p>
     * Triggered when one of the Facets to which AddLevelFacet listens fires a
     * DataFacetChangeEvent to indicate a CDOMObject was added to a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, PCTemplate> dfce)
    {
        PCTemplate template = dfce.getCDOMObject();
        CharID id = dfce.getCharID();
        PlayerCharacter pc = trackingFacet.getPC(id);

        List<LevelCommandFactory> lcfList = template.getSafeListFor(ListKey.ADD_LEVEL);
        for (ListIterator<LevelCommandFactory> it = lcfList.listIterator(lcfList.size());it.hasPrevious();)
        {
            LevelCommandFactory lcf = it.previous();
            remove(lcf.getLevelCount(), lcf.getPCClass(), pc);
        }
    }

    /**
     * Adds levels of the given PCClass to the given PlayerCharacter.
     * <p>
     * The number of levels added is defined by the level formula in this
     * LevelCommandFactory, and the PCClass is defined by the CDOMReference
     * provided when this LevelCommandFactory was constructed.
     *
     * @param levels A Formula indicating the number of levels of the given PCClass
     *               to be added to the given PlayerCharacter.
     * @param cl     The PCClass for which the levels as defined by the given
     *               Formula will be added to the given PlayerCharacter.
     * @param pc     The PlayerCharacter to which the levels of the given PCClass
     *               will be added.
     * @throws NullPointerException if the given PlayerCharacter is null
     */
    private void add(Formula levels, PCClass cl, PlayerCharacter pc)
    {
        apply(pc, cl, levels.resolve(pc, "").intValue());
    }

    /**
     * Removes levels as defined by the given Formula of the given PCClass from
     * the given PlayerCharacter.
     *
     * @param levels A Formula indicating the number of levels of the given PCClass
     *               to be removed from the given PlayerCharacter.
     * @param cl     The PCClass for which the levels as defined by the given
     *               Formula will be removed from the given PlayerCharacter.
     * @param pc     The PlayerCharacter from which the levels of the PCClass in
     *               this LevelCommandFactory will be removed.
     * @throws NullPointerException if the given PlayerCharacter is null
     */
    public void remove(Formula levels, PCClass cl, PlayerCharacter pc)
    {
        apply(pc, cl, -levels.resolve(pc, "").intValue());
    }

    /**
     * Applies a change in the given number of levels of the given PCClass to
     * the given PlayerCharacter. If the number of levels is greater than zero,
     * then levels are added to the given PlayerCharacter, if less than zero,
     * levels are removed from the given PlayerCharacter.
     *
     * @param pc      The PlayerCharacter for the levels of the PCClass will be
     *                added or removed
     * @param pcClass The PCClass for which the levels as defined by the given
     *                Formula will be added to or removed from the given
     *                PlayerCharacter
     * @param levels  The number of levels to apply to the PlayerCharacter
     * @throws NullPointerException if the given PlayerCharacter is null
     */
    private void apply(PlayerCharacter pc, PCClass pcClass, int levels)
    {
        boolean tempShowHP = SettingsHandler.getShowHPDialogAtLevelUp();
        SettingsHandler.setShowHPDialogAtLevelUp(false);
        int tempChoicePref = UIPropertyContext.getSingleChoiceAction();
        UIPropertyContext.setSingleChoiceAction(Constants.CHOOSER_SINGLE_CHOICE_METHOD_SELECT_EXIT);

        pc.incrementClassLevel(levels, pcClass, true, true);

        UIPropertyContext.setSingleChoiceAction(tempChoicePref);
        SettingsHandler.setShowHPDialogAtLevelUp(tempShowHP);
    }

    public void setTemplateFacet(TemplateFacet templateFacet)
    {
        this.templateFacet = templateFacet;
    }

    /**
     * Initializes the connections for AddLevelFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the AddLevelFacet.
     */
    public void init()
    {
        templateFacet.addDataFacetChangeListener(this);
    }
}
