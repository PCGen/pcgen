/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.rules.persistence.token;

import java.io.StringWriter;
import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterFactory;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

public class PreCompatibilityToken
        implements CDOMPrimaryToken<ConcretePrereqObject>, CDOMSecondaryToken<ConcretePrereqObject>
{
    private static PrerequisiteWriterFactory factory = PrerequisiteWriterFactory.getInstance();

    private final String tokenRoot;
    private final String tokenName;
    private final PrerequisiteParserInterface token;
    private final boolean invert;

    public PreCompatibilityToken(String s, PrerequisiteParserInterface prereqToken, boolean inv)
    {
        tokenRoot = s.toUpperCase();
        token = prereqToken;
        invert = inv;
        tokenName = (invert ? "!" : "") + "PRE" + tokenRoot;
    }

    @Override
    public Class<ConcretePrereqObject> getTokenClass()
    {
        return ConcretePrereqObject.class;
    }

    @Override
    public ParseResult parseToken(LoadContext context, ConcretePrereqObject obj, String value)
    {
        boolean overrideQualify = false;
        String preValue = value;
        if (value.startsWith("Q:"))
        {
            preValue = value.substring(2);
            overrideQualify = true;
        }
        try
        {
            Prerequisite p = token.parse(tokenRoot, preValue, invert, overrideQualify);
            if (p == null)
            {
                return ParseResult.INTERNAL_ERROR;
            }
            context.getObjectContext().put(obj, p);
            return ParseResult.SUCCESS;
        } catch (PersistenceLayerException e)
        {
            return new ParseResult.Fail(e.getMessage());
        }
    }

    @Override
    public String getTokenName()
    {
        return tokenName;
    }

    public static int compatibilityLevel()
    {
        return 5;
    }

    public static int compatibilityPriority()
    {
        return 0;
    }

    public static int compatibilitySubLevel()
    {
        return 14;
    }

    @Override
    public String getParentToken()
    {
        return "*KITTOKEN";
    }

    @Override
    public String[] unparse(LoadContext context, ConcretePrereqObject obj)
    {
        Changes<Prerequisite> changes = context.getObjectContext().getPrerequisiteChanges(obj);
        if (changes == null || changes.isEmpty())
        {
            return null;
        }
        Set<String> set = new TreeSet<>();
        for (Prerequisite p : changes.getAdded())
        {
            String kind = p.getKind();
            final StringWriter capture = new StringWriter();
            try
            {
                PrerequisiteWriterInterface writer = factory.getWriter(kind);
                writer.write(capture, p);
            } catch (PersistenceLayerException e)
            {
                Logging.errorPrint("Error in Compatibility Token", e);
            }
            String output = capture.toString();
            int colonLoc = output.indexOf(':');
            boolean outInvert = output.startsWith("!");
            if (invert ^ outInvert)
            {
                continue;
            }
            String key = output.substring(0, colonLoc);
            if (tokenName.equalsIgnoreCase(key))
            {
                set.add(output.substring(colonLoc + 1));
            }
        }
        if (set.isEmpty())
        {
            return null;
        }
        return set.toArray(new String[0]);
    }
}
