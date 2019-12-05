/*
 * LevelToken.java
 * Copyright 2003 (C) Devon Jones <soulcatcher@evilsoft.org>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.exporttokens;

import java.util.StringTokenizer;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

/**
 * LEVEL token
 */
public class LevelToken extends Token
{
    /**
     * Token name
     */
    public static final String TOKENNAME = "LEVEL";

    @Override
    public String getTokenName()
    {
        return TOKENNAME;
    }

    @Override
    public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
    {
        String retString = "";
        StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
        aTok.nextToken();

        int level = 1;

        if (aTok.hasMoreTokens())
        {
            level = Integer.parseInt(aTok.nextToken());
        }

        if (level < 1 || level > pc.getDisplay().getLevelInfoSize())
        {
            //TODO Error?
            return "";
        }
        PCLevelInfo pcl = pc.getDisplay().getLevelInfo(level - 1);

        if (aTok.hasMoreTokens())
        {
            String tokName = aTok.nextToken();
            if (tokName.equals("CLASSNAME"))
            {
                retString = pcl.getClassKeyName();
            }
            if (tokName.equals("CLASSLEVEL"))
            {
                retString = Integer.toString(pcl.getClassLevel());
            }
            if (tokName.equals("FEATLIST"))
            {
                //TODO This is likely a bug...
                retString = "";
            }
            if (tokName.equals("HP"))
            {
                retString = getLevelHP(pc, pcl);
            }
            if (tokName.equals("SKILLPOINTS"))
            {
                retString = Integer.toString(pcl.getSkillPointsGained(pc));
            }
        }
        return retString;
    }

    /**
     * Get the HP for the level
     *
     * @param pc
     * @param pcl
     * @return the HP for the level
     */
    public static String getLevelHP(PlayerCharacter pc, PCLevelInfo pcl)
    {
        String classKeyName = pcl.getClassKeyName();
        PCClass aClass = pc.getClassKeyed(classKeyName);
        if (aClass == null)
        {
            aClass = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(PCClass.class,
                    classKeyName);
            if (aClass != null)
            {
                CDOMSingleRef<PCClass> exc = aClass.get(ObjectKey.EX_CLASS);
                aClass = pc.getClassKeyed(exc.get().getKeyName());
            }
        }
        if (aClass != null)
        {
            PCClassLevel classLevel = pc.getDisplay().getActiveClassLevel(aClass, pcl.getClassLevel() - 1);
            Integer hp = pc.getDisplay().getHP(classLevel);
            return hp == null ? "0" : hp.toString();
        }
        return "";
    }
}
