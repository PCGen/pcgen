/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.gui2.converter;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import pcgen.base.util.DoubleKeyMap;
import pcgen.base.util.DoubleKeyMapToList;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AspectName;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.cdom.enumeration.RaceType;
import pcgen.cdom.enumeration.Region;
import pcgen.cdom.enumeration.SubClassCategory;
import pcgen.cdom.enumeration.SubRace;
import pcgen.cdom.enumeration.SubRegion;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.gui2.converter.event.TokenProcessEvent;
import pcgen.gui2.converter.event.TokenProcessorPlugin;
import pcgen.system.PluginLoader;
import pcgen.util.Logging;

public final class TokenConverter
{

    private static final DoubleKeyMap<Class<?>, String, TokenProcessorPlugin> MAP = new DoubleKeyMap<>();

    private static final DoubleKeyMap<Class<?>, String, Boolean> CACHED = new DoubleKeyMap<>();

    private static final DoubleKeyMapToList<Class<?>, String, TokenProcessorPlugin> TOKEN_CACHE =
            new DoubleKeyMapToList<>();

    private static final DefaultTokenProcessor DEFAULT_PROC = new DefaultTokenProcessor();

    private TokenConverter()
    {
    }

    public static void addToTokenMap(TokenProcessorPlugin tpp)
    {
        TokenProcessorPlugin old = MAP.put(tpp.getProcessedClass(), tpp.getProcessedToken(), tpp);
        if (old != null)
        {
            Logging.errorPrint("More than one Conversion token for " + tpp.getProcessedClass().getSimpleName() + ' '
                    + tpp.getProcessedToken() + " found");
        }
    }

    public static PluginLoader getPluginLoader()
    {
        return new PluginLoader()
        {

            @Override
            public void loadPlugin(Class<?> clazz) throws Exception
            {
                addToTokenMap((TokenProcessorPlugin) clazz.newInstance());
            }

            @Override
            public Class[] getPluginClasses()
            {
                return new Class[]{TokenProcessorPlugin.class};
            }
        };
    }

    public static String process(TokenProcessEvent tpe)
    {
        Class<?> cl = tpe.getPrimary().getClass();
        String key = tpe.getKey();

        ensureCategoryExists(tpe);

        List<TokenProcessorPlugin> tokens = getTokens(cl, key);
        StringBuilder error = new StringBuilder();
        try
        {
            if (tokens != null)
            {
                for (TokenProcessorPlugin converter : tokens)
                {
                    error.append(converter.process(tpe));
                    if (tpe.isConsumed())
                    {
                        break;
                    }
                }
            }
            if (!tpe.isConsumed())
            {
                error.append(DEFAULT_PROC.process(tpe));
            }
        } catch (Exception ex)
        {
            Logging.errorPrint("Parse of " + tpe.getKey() + ':' + tpe.getValue() + " failed", ex);
        }
        return tpe.isConsumed() ? null : error.toString();
    }

    /**
     * If this is an ABILITY token, ensure that we have an ability category
     * in place to use for conversion. This is because the categories can be
     * declared in data which may not be being converted.
     *
     * @param tpe The token event that is being processed.
     */
    private static void ensureCategoryExists(TokenProcessEvent tpe)
    {
        if (!tpe.getKey().equals("ABILITY"))
        {
            return;
        }
        String value = tpe.getValue();
        StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
        String cat = tok.nextToken();
        Category<Ability> category =
                tpe.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(AbilityCategory.class, cat);
        if (category == null)
        {
            //			Logging.log(Logging.INFO, "Found new cat " + cat + " in " + tpe);
            tpe.getContext().getReferenceContext().constructCDOMObject(AbilityCategory.class, cat);
        }
    }

    static class ConverterIterator implements Iterator<TokenProcessorPlugin>
    {

        private Class<?> rootClass;
        private final String tokenKey;
        private TokenProcessorPlugin nextToken = null;
        private boolean needNewToken = true;

        public ConverterIterator(Class<?> cl, String key)
        {
            rootClass = cl;
            tokenKey = key;
        }

        @Override
        public boolean hasNext()
        {
            setNextToken();
            return !needNewToken;
        }

        protected void setNextToken()
        {
            if (needNewToken)
            {
                nextToken = null;
                while (nextToken == null && rootClass != null)
                {
                    nextToken = grabToken(rootClass, tokenKey);
                    rootClass = rootClass.getSuperclass();
                }
                needNewToken = nextToken == null;
            }
        }

        protected TokenProcessorPlugin grabToken(Class<?> cl, String key)
        {
            return MAP.get(cl, key);
        }

        @Override
        public TokenProcessorPlugin next()
        {
            setNextToken();
            if (needNewToken)
            {
                throw new NoSuchElementException();
            }
            needNewToken = true;
            return nextToken;
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("Iterator does not support remove");
        }
    }

    public static List<TokenProcessorPlugin> getTokens(Class<?> cl, String name)
    {
        List<TokenProcessorPlugin> list = TOKEN_CACHE.getListFor(cl, name);
        if (!CACHED.containsKey(cl, name))
        {
            for (Iterator<TokenProcessorPlugin> it = new ConverterIterator(cl, name);it.hasNext();)
            {
                TokenProcessorPlugin token = it.next();
                TOKEN_CACHE.addToListFor(cl, name, token);
            }
            list = TOKEN_CACHE.getListFor(cl, name);
            CACHED.put(cl, name, Boolean.TRUE);
        }
        return list;
    }

    public static void clearConstants()
    {
        AspectName.clearConstants();
        RaceSubType.clearConstants();
        RaceType.clearConstants();
        Region.clearConstants();
        SubClassCategory.clearConstants();
        SubRace.clearConstants();
        SubRegion.clearConstants();
        Type.buildMap();
        VariableKey.clearConstants();
    }

    public static void clear()
    {
        MAP.clear();
        TOKEN_CACHE.clear();
        CACHED.clear();
    }

}
