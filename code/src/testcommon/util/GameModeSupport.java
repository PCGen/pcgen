/*
 * Extracted from GameModeFileLoader.java
 * Copyright 2010 Connor Petty <cpmeister@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package util;

import java.util.Collection;

import pcgen.base.lang.UnreachableError;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.QualifiedObject;
import pcgen.core.character.WieldCategory;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

public class GameModeSupport
{
    public static void addDefaultWieldCategories(LoadContext context) throws PersistenceLayerException
    {
        PreParserFactory prereqParser;

        try
        {
            prereqParser = PreParserFactory.getInstance();
        } catch (final PersistenceLayerException ple)
        {
            Logging.errorPrint("Error Initializing PreParserFactory");
            Logging.errorPrint("  " + ple.getMessage(), ple);
            throw new UnreachableError(ple);
        }

        AbstractReferenceContext refContext = context.getReferenceContext();
        Collection<WieldCategory> categories = refContext.getConstructedCDOMObjects(WieldCategory.class);

        WieldCategory light = null;
        WieldCategory twoHanded = null;
        WieldCategory oneHanded = null;
        WieldCategory tooLarge = null;
        WieldCategory tooSmall = null;

        for (final WieldCategory wc : categories)
        {
            String name = wc.getKeyName();
            if ("Light".equalsIgnoreCase(name))
            {
                light = wc;
            }
            if ("TwoHanded".equalsIgnoreCase(name))
            {
                twoHanded = wc;
            }
            if ("OneHanded".equalsIgnoreCase(name))
            {
                oneHanded = wc;
            }
            if ("TooLarge".equalsIgnoreCase(name))
            {
                tooLarge = wc;
            }
            if ("TooSmall".equalsIgnoreCase(name))
            {
                tooSmall = wc;
            }
        }
        boolean buildLight = false;
        if (light == null)
        {
            light = new WieldCategory();
            light.setName("Light");
            refContext.importObject(light);
            buildLight = true;
        }
        boolean buildTwoHanded = false;
        if (twoHanded == null)
        {
            twoHanded = new WieldCategory();
            twoHanded.setName("TwoHanded");
            refContext.importObject(twoHanded);
            buildTwoHanded = true;
        }
        boolean buildOneHanded = false;
        if (oneHanded == null)
        {
            oneHanded = new WieldCategory();
            oneHanded.setName("OneHanded");
            refContext.importObject(oneHanded);
            buildOneHanded = true;
        }
        boolean buildTooLarge = false;
        if (tooLarge == null)
        {
            tooLarge = new WieldCategory();
            tooLarge.setName("TooLarge");
            refContext.importObject(tooLarge);
            buildTooLarge = true;
        }
        boolean buildTooSmall = false;
        if (tooSmall == null)
        {
            tooSmall = new WieldCategory();
            tooSmall.setName("TooSmall");
            refContext.importObject(tooSmall);
            buildTooSmall = true;
        }

        CDOMDirectSingleRef<WieldCategory> tooSmallRef = CDOMDirectSingleRef.getRef(tooSmall);
        CDOMDirectSingleRef<WieldCategory> lightRef = CDOMDirectSingleRef.getRef(light);
        CDOMDirectSingleRef<WieldCategory> oneHandedRef = CDOMDirectSingleRef.getRef(oneHanded);
        CDOMDirectSingleRef<WieldCategory> twoHandedRef = CDOMDirectSingleRef.getRef(twoHanded);
        CDOMDirectSingleRef<WieldCategory> tooLargeRef = CDOMDirectSingleRef.getRef(tooLarge);
        if (buildLight)
        {
            light.setHandsRequired(1);
            light.setFinessable(true);
            Prerequisite p = prereqParser.parse("PREVARLTEQ:EQUIP.SIZE.INT,PC.SIZE.INT-1");
            QualifiedObject<CDOMSingleRef<WieldCategory>> qo = new QualifiedObject<>(tooSmallRef);
            qo.addPrerequisite(p);
            light.addCategorySwitch(qo);
            p = prereqParser.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT+1");
            qo = new QualifiedObject<>(oneHandedRef);
            qo.addPrerequisite(p);
            light.addCategorySwitch(qo);
            p = prereqParser.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT+2");
            qo = new QualifiedObject<>(twoHandedRef);
            qo.addPrerequisite(p);
            light.addCategorySwitch(qo);
            p = prereqParser.parse("PREVARGTEQ:EQUIP.SIZE.INT,PC.SIZE.INT+3");
            qo = new QualifiedObject<>(tooLargeRef);
            qo.addPrerequisite(p);
            light.addCategorySwitch(qo);
            light.setWieldCategoryStep(1, oneHandedRef);
            light.setWieldCategoryStep(2, twoHandedRef);
        }
        if (buildTwoHanded)
        {
            twoHanded.setFinessable(false);
            twoHanded.setHandsRequired(2);
            Prerequisite p = prereqParser.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT-3");
            QualifiedObject<CDOMSingleRef<WieldCategory>> qo = new QualifiedObject<>(tooSmallRef);
            qo.addPrerequisite(p);
            twoHanded.addCategorySwitch(qo);
            p = prereqParser.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT-2");
            qo = new QualifiedObject<>(lightRef);
            qo.addPrerequisite(p);
            twoHanded.addCategorySwitch(qo);
            p = prereqParser.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT-1");
            qo = new QualifiedObject<>(oneHandedRef);
            qo.addPrerequisite(p);
            twoHanded.addCategorySwitch(qo);
            p = prereqParser.parse("PREVARGTEQ:EQUIP.SIZE.INT,PC.SIZE.INT+1");
            qo = new QualifiedObject<>(tooLargeRef);
            qo.addPrerequisite(p);
            twoHanded.addCategorySwitch(qo);
            twoHanded.setWieldCategoryStep(-2, lightRef);
            twoHanded.setWieldCategoryStep(-1, oneHandedRef);
        }
        if (buildOneHanded)
        {
            oneHanded.setHandsRequired(1);
            oneHanded.setFinessable(false);
            Prerequisite p = prereqParser.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT-2");
            QualifiedObject<CDOMSingleRef<WieldCategory>> qo = new QualifiedObject<>(tooSmallRef);
            qo.addPrerequisite(p);
            oneHanded.addCategorySwitch(qo);
            p = prereqParser.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT-1");
            qo = new QualifiedObject<>(lightRef);
            qo.addPrerequisite(p);
            oneHanded.addCategorySwitch(qo);
            p = prereqParser.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT+1");
            qo = new QualifiedObject<>(twoHandedRef);
            qo.addPrerequisite(p);
            oneHanded.addCategorySwitch(qo);
            p = prereqParser.parse("PREVARGTEQ:EQUIP.SIZE.INT,PC.SIZE.INT+2");
            qo = new QualifiedObject<>(tooLargeRef);
            qo.addPrerequisite(p);
            oneHanded.addCategorySwitch(qo);
            oneHanded.setWieldCategoryStep(-1, lightRef);
            oneHanded.setWieldCategoryStep(1, twoHandedRef);
        }
        if (buildTooLarge)
        {
            tooLarge.setFinessable(false);
            tooLarge.setHandsRequired(999);
            tooLarge.setWieldCategoryStep(-3, lightRef);
            tooLarge.setWieldCategoryStep(-2, oneHandedRef);
            tooLarge.setWieldCategoryStep(-1, twoHandedRef);
            tooLarge.setWieldCategoryStep(0, twoHandedRef);
        }
        if (buildTooSmall)
        {
            tooSmall.setFinessable(false);
            tooSmall.setHandsRequired(2);
            Prerequisite p = prereqParser.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT-3");
            QualifiedObject<CDOMSingleRef<WieldCategory>> qo = new QualifiedObject<>(tooSmallRef);
            qo.addPrerequisite(p);
            tooSmall.addCategorySwitch(qo);
            p = prereqParser.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT-2");
            qo = new QualifiedObject<>(lightRef);
            qo.addPrerequisite(p);
            tooSmall.addCategorySwitch(qo);
            p = prereqParser.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT-1");
            qo = new QualifiedObject<>(oneHandedRef);
            qo.addPrerequisite(p);
            tooSmall.addCategorySwitch(qo);
            p = prereqParser.parse("PREVARGTEQ:EQUIP.SIZE.INT,PC.SIZE.INT+1");
            qo = new QualifiedObject<>(tooLargeRef);
            qo.addPrerequisite(p);
            tooSmall.addCategorySwitch(qo);
            tooSmall.setWieldCategoryStep(-2, lightRef);
            tooSmall.setWieldCategoryStep(-1, oneHandedRef);
        }

    }

}
