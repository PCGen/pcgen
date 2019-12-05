/*
 *
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 *
 *
 *
 */
package pcgen.persistence.lst.prereq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pcgen.base.lang.UnreachableError;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.system.PluginLoader;
import pcgen.util.Logging;

public final class PreParserFactory implements PluginLoader
{
    private static PreParserFactory instance = null;
    private Map<String, PrerequisiteParserInterface> parserLookup = new HashMap<>();

    private PreParserFactory() throws PersistenceLayerException
    {
        register(new PreMultParser());
    }

    @Override
    public void loadPlugin(Class<?> clazz) throws Exception
    {
        register((PrerequisiteParserInterface) clazz.newInstance());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class[] getPluginClasses()
    {
        return new Class[]{PrerequisiteParserInterface.class};
    }

    /**
     * Gets the single instance of PreParserFactory.
     *
     * @return Returns the instance.
     * @throws PersistenceLayerException the persistence layer exception
     */
    public static PreParserFactory getInstance() throws PersistenceLayerException
    {
        if (instance == null)
        {
            instance = new PreParserFactory();
        }

        return instance;
    }

    private PrerequisiteParserInterface getParser(String kind)
    {
        return parserLookup.get(kind.toLowerCase());
    }

    public void register(PrerequisiteParserInterface testClass) throws PersistenceLayerException
    {
        String[] kindsHandled = testClass.kindsHandled();

        for (String kind : kindsHandled)
        {
            Object test = parserLookup.get(kind.toLowerCase());

            if (test != null)
            {
                throw new PersistenceLayerException(
                        "Error registering '" + testClass.getClass().getName() + "' as test '" + kind
                                + "'. The test is already registered to '" + test.getClass().getName() + "'");
            }

            parserLookup.put(kind.toLowerCase(), testClass);
        }
    }

    public static List<Prerequisite> parse(final List<String> preStrings)
    {
        final List<Prerequisite> ret = new ArrayList<>(preStrings.size());
        for (String prestr : preStrings)
        {
            try
            {
                final PreParserFactory factory = PreParserFactory.getInstance();
                final Prerequisite prereq = factory.parse(prestr);
                ret.add(prereq);
            } catch (PersistenceLayerException ple)
            {
                Logging.errorPrint(ple.getMessage(), ple);
                //The message is now produced at a lower level, and thus has to be localized there.
                //Logging.errorPrintLocalised(PropertyFactory.getString("PrereqHandler.Unable_to_parse"), object); //$NON-NLS-1$
            }
        }
        return ret;
    }

    public Prerequisite parse(String prereqStr) throws PersistenceLayerException
    {

        if ((prereqStr == null) || (prereqStr.length() <= 0))
        {
            throw new PersistenceLayerException("Null or empty PRE string");
        }

        int index = prereqStr.indexOf(':');
        if (index < 0)
        {
            throw new PersistenceLayerException("'" + prereqStr + "'" + " is a badly formatted prereq.");
        }

        String kind = prereqStr.substring(0, index);
        String formula = prereqStr.substring(index + 1);

        boolean overrideQualify = false;
        if (formula.startsWith("Q:"))
        {
            formula = formula.substring(2);
            overrideQualify = true;
        }

        boolean invertResult = false;
        if (kind.startsWith("!"))
        {
            invertResult = true;
            kind = kind.substring(1);
        }
        kind = kind.substring(3);
        PrerequisiteParserInterface parser = getParser(kind);
        if (parser == null)
        {
            throw new PersistenceLayerException("Can not determine which parser to use for " + "'" + prereqStr + "'");
        }
        try
        {
            Prerequisite prereq = parser.parse(kind, formula, invertResult, overrideQualify);
            //sanity check to make sure we have not got a top level element that
            // is a PREMULT with only 1 element.
            while (prereq.getKind() == null && prereq.getPrerequisiteCount() == 1
                    && prereq.getOperator().equals(PrerequisiteOperator.GTEQ) && prereq.getOperand().equals("1"))
            {
                Prerequisite sub = prereq.getPrerequisites().get(0);
                sub.setOriginalCheckmult(prereq.isOriginalCheckMult());
                prereq = sub;
            }
            return prereq;
        } catch (Throwable t)
        {
            throw new PersistenceLayerException("Can not parse '" + prereqStr + "': " + t.getMessage(), t);
        }
    }

    /**
     * Identify if the token passed in defines a prerequisite.
     *
     * @param token The token to be checked.
     * @return True if the string is a prereq string.
     */
    public static boolean isPreReqString(String token)
    {
        return (token.startsWith("PRE") || token.startsWith("!PRE")) && (token.indexOf(':') > 0);
    }

    public static void clear()
    {
        if (instance != null)
        {
            instance.parserLookup.clear();
            try
            {
                instance.register(new PreMultParser());
            } catch (PersistenceLayerException e)
            {
                throw new UnreachableError("Should be impossible", e);
            }
        }
    }
}
