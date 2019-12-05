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

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.content.LevelCommandFactory;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.facet.analysis.LevelFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.model.ClassFacet;
import pcgen.cdom.facet.model.ClassFacet.ClassInfo;
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.pclevelinfo.PCLevelInfo;

/**
 * {@code MonsterClassFacet} is a Facet that tracks the {@literal Classes & Levels} that a Player
 * Character was granted through MONSTERCLASS
 */
public class MonsterClassFacet implements DataFacetChangeListener<CharID, CDOMObject>
{

    private LevelFacet levelFacet;

    private ClassFacet classFacet;

    private FormulaResolvingFacet formulaResolvingFacet;

    private LevelInfoFacet levelInfoFacet;

    private RaceFacet raceFacet;

    private final PlayerCharacterTrackingFacet trackingFacet =
            FacetLibrary.getFacet(PlayerCharacterTrackingFacet.class);

    /**
     * Adds monster classes to the Player Character when a CDOMObject which
     * grants monster classes is added to the Player Character.
     * <p>
     * Triggered when one of the Facets to which MonsterClassFacet listens fires
     * a DataFacetChangeEvent to indicate a CDOMObject was added to a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        CharID id = dfce.getCharID();
        CDOMObject cdo = dfce.getCDOMObject();

        // Get existing classes
        ClassInfo ci = classFacet.removeAllClasses(id);

        //
        // Remove all saved monster level information
        //
        for (int i = levelInfoFacet.getCount(id) - 1;i >= 0;--i)
        {
            PCLevelInfo pli = levelInfoFacet.get(id, i);
            final String classKeyName = pli.getClassKeyName();
            final PCClass aClass = Globals.getContext().getReferenceContext()
                    .silentlyGetConstructedCDOMObject(PCClass.class, classKeyName);

            if (aClass.isMonster())
            {
                levelInfoFacet.remove(id, pli);
            }
        }

        PlayerCharacter pc = trackingFacet.getPC(id);
        final List<PCLevelInfo> existingLevelInfo = new ArrayList<>(levelInfoFacet.getSet(id));
        levelInfoFacet.removeAll(id);
        // Make sure monster classes are added first
        if (!pc.isImporting())
        {
            LevelCommandFactory lcf = cdo.get(ObjectKey.MONSTER_CLASS);
            if (lcf != null)
            {
                int levelCount = formulaResolvingFacet.resolve(id, lcf.getLevelCount(), "").intValue();
                pc.incrementClassLevel(levelCount, lcf.getPCClass(), true);
            }
        }
        levelInfoFacet.addAll(id, existingLevelInfo);

        //
        // If user has chosen a class before choosing a race,
        // we need to tweak the number of skill points and feats
        //
        if (!pc.isImporting() && ci != null && !ci.isEmpty())
        {
            int totalLevels = levelFacet.getTotalLevels(id);

            for (PCClass pcClass : ci.getClassSet())
            {
                //
                // Don't add monster classes back in. This will possibly
                // mess up feats earned by level
                // ?Possibly convert to monster class if not null?
                //
                if (!pcClass.isMonster())
                {
                    classFacet.addClass(id, pcClass);
                    int cLevels = ci.getLevel(pcClass);
                    classFacet.setLevel(id, pcClass, cLevels);

                    pc.setSkillPool(pcClass, 0);

                    int cMod = 0;

                    for (int j = 0;j < cLevels;++j)
                    {
                        cMod += pc.recalcSkillPointMod(pcClass, ++totalLevels);
                    }

                    pc.setSkillPool(pcClass, cMod);
                }
            }
        }
    }

    /**
     * Removes monster classes from the Player Character when the CDOMObject
     * which granted the monster classes is removed from the Player Character.
     * <p>
     * Triggered when one of the Facets to which MonsterClassFacet listens fires
     * a DataFacetChangeEvent to indicate a CDOMObject was removed from a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        CDOMObject cdo = dfce.getCDOMObject();
        LevelCommandFactory lcf = cdo.get(ObjectKey.MONSTER_CLASS);
        if (lcf != null)
        {
            CharID id = dfce.getCharID();
            int levelCount = formulaResolvingFacet.resolve(id, lcf.getLevelCount(), "").intValue();
            PlayerCharacter pc = trackingFacet.getPC(id);
            pc.incrementClassLevel(-levelCount, lcf.getPCClass(), true);
        }
    }

    public void setLevelFacet(LevelFacet levelFacet)
    {
        this.levelFacet = levelFacet;
    }

    public void setClassFacet(ClassFacet classFacet)
    {
        this.classFacet = classFacet;
    }

    public void setFormulaResolvingFacet(FormulaResolvingFacet formulaResolvingFacet)
    {
        this.formulaResolvingFacet = formulaResolvingFacet;
    }

    public void setLevelInfoFacet(LevelInfoFacet levelInfoFacet)
    {
        this.levelInfoFacet = levelInfoFacet;
    }

    public void setRaceFacet(RaceFacet raceFacet)
    {
        this.raceFacet = raceFacet;
    }

    /**
     * Initializes the connections for MonsterClassFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the MonsterClassFacet.
     */
    public void init()
    {
        raceFacet.addDataFacetChangeListener(this);
    }
}
