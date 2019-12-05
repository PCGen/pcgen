/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
package plugin.lsttokens.gamemode.migrate;

import java.util.regex.Pattern;

import pcgen.cdom.base.Constants;
import pcgen.core.system.MigrationRule;
import pcgen.core.system.MigrationRule.ObjectType;
import pcgen.persistence.lst.MigrationLstToken;
import pcgen.util.Logging;

import org.apache.commons.lang3.StringUtils;

/**
 * The Class {@code NewKeyToken} parses the NEWKEY token in migration.lst
 * game mode files. The NEWKEY token defines the key that should be used instead
 * of an old key when loading an older character.
 */
public class NewKeyToken implements MigrationLstToken
{
    private Pattern invalidKeyPattern = Pattern.compile(".*[,|\\||\\\\|:|;|\\.|%|\\*|=|\\[|\\]].*");
    private Pattern invalidSourceKeyPattern = Pattern.compile(".*[\\||\\\\|;|%|\\*|=|\\[|\\]].*");

    @Override
    public String getTokenName()
    {
        return "NEWKEY";
    }

    @Override
    public boolean parse(MigrationRule migrationRule, String value, String gameModeName)
    {
        if (StringUtils.isBlank(value))
        {
            Logging.log(Logging.LST_ERROR, "Invalid empty " + getTokenName() + " value.");
            return false;
        }
        Pattern validationPattern =
                migrationRule.getObjectType() == ObjectType.SOURCE ? invalidSourceKeyPattern : invalidKeyPattern;
        if (validationPattern.matcher(value).matches())
        {
            Logging.log(Logging.LST_ERROR,
                    "Invalid characters in value '" + value + "' of " + getTokenName() + Constants.COLON + value);
            return false;
        }
        migrationRule.setNewKey(value);
        return true;
    }

}
