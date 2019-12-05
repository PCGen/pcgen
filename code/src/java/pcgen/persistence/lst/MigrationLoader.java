/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.equipforge.net>
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
 *
 */

package pcgen.persistence.lst;

import java.net.URI;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.base.lang.UnreachableError;
import pcgen.core.SystemCollections;
import pcgen.core.system.MigrationRule;
import pcgen.core.system.MigrationRule.ObjectType;
import pcgen.core.utils.CoreUtility;
import pcgen.persistence.SystemLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

import org.apache.commons.lang3.StringUtils;

/**
 * MigrationLoader is a LstFileLoader that processes the migration.lst file
 */
public class MigrationLoader extends LstLineFileLoader
{
    private String invalidKeyPattern = ".*[,|\\||\\\\|:|;|%|\\*|=|\\[|\\]].*";
    private String invalidSourceKeyPattern = ".*[\\||\\\\|;|%|\\*|=|\\[|\\]].*";

    @Override
    public void parseLine(LoadContext context, String lstLine, URI sourceURI)
    {

        final StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);

        String firstToken = colToken.nextToken().trim();
        final MigrationRule migrationRule = parseFirstToken(firstToken, lstLine, sourceURI);
        if (migrationRule == null)
        {
            return;
        }

        Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(MigrationLstToken.class);
        while (colToken.hasMoreTokens())
        {
            final String colString = colToken.nextToken().trim();

            final int idxColon = colString.indexOf(':');
            String key = "";
            try
            {
                key = colString.substring(0, idxColon);
            } catch (StringIndexOutOfBoundsException e)
            {
                // TODO Handle Exception
            }
            MigrationLstToken token = (MigrationLstToken) tokenMap.get(key);

            if (token != null)
            {
                final String value = colString.substring(idxColon + 1);
                LstUtils.deprecationCheck(token, migrationRule.getOldKey(), sourceURI, value);
                if (!token.parse(migrationRule, value, getGameMode()))
                {
                    Logging.errorPrint("Error parsing migration rule " + migrationRule.getOldKey() + ':' + sourceURI
                            + ':' + colString + "\"");
                    return;
                }
            } else
            {
                Logging.errorPrint(
                        "Unknown token " + key + " in migration rule '" + lstLine + "' in " + sourceURI.toString());
                return;
            }
        }

        // Check for mandatory tokens and interrelationship rules
        boolean errorFound = false;
        if (migrationRule.getMaxVer() == null)
        {
            Logging.errorPrint(
                    "Missing required token MAXVER in migration rule '" + lstLine + "' in " + sourceURI.toString());
            errorFound = true;
        }
        if (migrationRule.getNewKey() == null)
        {
            Logging.errorPrint(
                    "Missing required token NEWKEY in migration rule '" + lstLine + "' in " + sourceURI.toString());
            errorFound = true;
        }
        if (migrationRule.getMinVer() == null && migrationRule.getMinDevVer() != null)
        {
            Logging.errorPrint("MINVER is required when MINDEVVER is used. Migration rule was '" + lstLine + "' in "
                    + sourceURI.toString());
            errorFound = true;
        }
        if (migrationRule.getMaxVer() != null && migrationRule.getMaxDevVer() != null
                && CoreUtility.compareVersions(migrationRule.getMaxVer(), migrationRule.getMaxDevVer()) >= 0)
        {
            Logging.errorPrint(
                    "MAXVER must be before MAXDEVVER. Migration rule was '" + lstLine + "' in " + sourceURI.toString());
            errorFound = true;
        }
        if (migrationRule.getMinVer() != null && migrationRule.getMinDevVer() != null
                && CoreUtility.compareVersions(migrationRule.getMinVer(), migrationRule.getMinDevVer()) >= 0)
        {
            Logging.errorPrint(
                    "MINVER must be before MINDEVVER. Migration rule was '" + lstLine + "' in " + sourceURI.toString());
            errorFound = true;
        }
        if (errorFound)
        {
            // Abandon this rule.
            return;
        }

        SystemCollections.addToMigrationRulesList(migrationRule, gameMode);
    }

    /**
     * Parse the leading token and value to produce a starting MigrationRule object.
     *
     * @param firstToken The first token and value.
     * @param lstLine    The full line it came form, for error reporting purposes only.
     * @param sourceURI  The source path, for error reporting purposes only.
     * @return A new MigrationRule, or null if the token could nto be parsed.
     */
    MigrationRule parseFirstToken(String firstToken, String lstLine, URI sourceURI)
    {
        final int idxColon = firstToken.indexOf(':');
        if (idxColon <= 0 || idxColon == firstToken.length() - 1)
        {
            // Missing colon or missing key or value
            Logging.errorPrint("Illegal migration rule '" + lstLine + "' in " + sourceURI.toString());
            return null;
        }

        String objTypeKey = "";
        try
        {
            objTypeKey = firstToken.substring(0, idxColon);
        } catch (StringIndexOutOfBoundsException e)
        {
            throw new UnreachableError(e);
        }

        MigrationRule.ObjectType objType;
        try
        {
            objType = ObjectType.valueOf(objTypeKey);
        } catch (IllegalArgumentException e)
        {
            Logging.errorPrint("Unknown object type for migration rule '" + lstLine + "' in " + sourceURI.toString());
            return null;
        }

        String key = "";
        try
        {
            key = firstToken.substring(idxColon + 1);
        } catch (StringIndexOutOfBoundsException e)
        {
            throw new UnreachableError(e);
        }

        MigrationRule rule;
        if (objType.isCategorized())
        {
            if (key.endsWith("|"))
            {
                // Extra |
                Logging.errorPrint("Invalid category|key of '" + firstToken + "' of migration rule '" + lstLine
                        + "' in " + sourceURI.toString());
                return null;
            }
            String[] keyParts = key.split("\\|");
            if (keyParts.length < 2)
            {
                // No | so missing a category
                Logging.errorPrint("Missing category in '" + firstToken + "' of migration rule '" + lstLine + "' in "
                        + sourceURI.toString());
                return null;
            }
            if (keyParts.length > 2)
            {
                // Extra |
                Logging.errorPrint("Invalid category|key of '" + firstToken + "' of migration rule '" + lstLine
                        + "' in " + sourceURI.toString());
                return null;
            }
            if (StringUtils.isBlank(keyParts[0]) || keyParts[0].matches(invalidKeyPattern))
            {
                Logging.errorPrint("Invalid category of '" + keyParts[0] + "' of migration rule '" + lstLine + "' in "
                        + sourceURI.toString());
                return null;
            }
            if (StringUtils.isBlank(keyParts[1]) || keyParts[1].matches(invalidKeyPattern))
            {
                Logging.errorPrint("Invalid key of '" + keyParts[1] + "' of migration rule '" + lstLine + "' in "
                        + sourceURI.toString());
                return null;
            }

            rule = new MigrationRule(objType, keyParts[0], keyParts[1]);
        } else
        {
            String invalidKeyPat = objType == ObjectType.SOURCE ? invalidSourceKeyPattern : invalidKeyPattern;
            if (StringUtils.isBlank(key) || key.matches(invalidKeyPat))
            {
                Logging.errorPrint(
                        "Invalid key of '" + key + "' of migration rule '" + lstLine + "' in " + sourceURI.toString());
                return null;
            }
            rule = new MigrationRule(objType, key);
        }

        return rule;
    }

}
