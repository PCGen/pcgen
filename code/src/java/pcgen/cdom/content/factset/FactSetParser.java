/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.content.factset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.FactSetKey;
import pcgen.rules.context.AbstractObjectContext;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * A FactSetParser is a dynamically built subtoken created when a FACTSET: is
 * defined
 *
 * @param <T> The type of object upon which the FactSetParser can be used
 * @param <F> The format of the data stored in the FactSet
 */
public class FactSetParser<T extends CDOMObject, F> extends AbstractTokenWithSeparator<T>
        implements CDOMSecondaryToken<T>
{

    /**
     * The underlying FactSetInfo indicating static information about the Fact
     * for which this FactSetParser can parse the LST information
     */
    private final FactSetInfo<T, F> def;

    /**
     * Constructs a new FactSetParser with the given FactSetInfo.
     *
     * @param fsi The FactSetInfo underlying this FactSetParser
     * @throws IllegalArgumentException if the given FactSetInfo is null
     */
    public FactSetParser(FactSetInfo<T, F> fsi)
    {
        def = Objects.requireNonNull(fsi);
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, T obj, String value)
    {
        FormatManager<F> fmtManager = def.getFormatManager();
        FactSetKey<F> fsk = def.getFactSetKey();
        StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
        boolean firstToken = true;
        AbstractObjectContext objContext = context.getObjectContext();
        while (st.hasMoreTokens())
        {
            String token = st.nextToken();
            if (Constants.LST_DOT_CLEAR_ALL.equals(token))
            {
                if (!firstToken)
                {
                    return new ParseResult.Fail(
                            "Non-sensical situation was " + "encountered while parsing " + getParentToken() + Constants.PIPE
                                    + getTokenName() + ": When used, .CLEARALL must be the first argument");
                }
                objContext.removeSet(obj, fsk);
                firstToken = false;
            }

            Indirect<F> indirect = fmtManager.convertIndirect(token);
            objContext.addToSet(obj, fsk, indirect);
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public Class<T> getTokenClass()
    {
        return def.getUsableLocation();
    }

    @Override
    public String getTokenName()
    {
        return def.getFactSetName();
    }

    @Override
    public String getParentToken()
    {
        return "FACTSET";
    }

    @Override
    public String[] unparse(LoadContext context, T obj)
    {
        FactSetKey<F> fk = def.getFactSetKey();
        Changes<Indirect<F>> changes = context.getObjectContext().getSetChanges(obj, fk);
        Collection<Indirect<F>> removedItems = changes.getRemoved();
        List<String> results = new ArrayList<>(2);
        if (changes.includesGlobalClear())
        {
            results.add(Constants.LST_DOT_CLEAR_ALL);
        }
        if (removedItems != null && !removedItems.isEmpty())
        {
            context.addWriteMessage(getTokenName() + " does not support " + Constants.LST_DOT_CLEAR_DOT);
            return null;
        }
        Collection<Indirect<F>> added = changes.getAdded();
        if (added != null && !added.isEmpty())
        {
            StringBuilder sb = new StringBuilder();
            boolean needsPipe = false;
            for (Indirect<F> indirect : added)
            {
                if (needsPipe)
                {
                    sb.append(Constants.PIPE);
                }
                sb.append(indirect.getUnconverted());
                needsPipe = true;
            }
            results.add(sb.toString());
        }
        return results.toArray(new String[0]);
    }
}
