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

import pcgen.core.system.MigrationRule;
import pcgen.persistence.lst.MigrationLstToken;
import pcgen.persistence.lst.VersionAwareToken;
import pcgen.util.Logging;

import org.apache.commons.lang3.StringUtils;

/**
 * The Class {@code MaxDevVerToken} parses the MAXDEVVER token in migration.lst
 * game mode files. The MAXDEVVER token specifies the non-production PCGen version when
 * the rules object was last coded in the old format.
 */
public class MaxDevVerToken extends VersionAwareToken implements MigrationLstToken
{

    @Override
    public boolean parse(MigrationRule migrationRule, String value, String gameModeName)
    {
        if (StringUtils.isBlank(value))
        {
            Logging.log(Logging.LST_ERROR, "Invalid empty " + getTokenName() + " value.");
            return false;
        }
        if (!validateVersionNumber(value))
        {
            return false;
        }
        migrationRule.setMaxDevVer(value);
        return true;
    }

    @Override
    public String getTokenName()
    {
        return "MAXDEVVER";
    }

}
