/**
 * Copyright (c) 2008 Andrew Wilson <nuance@users.sourceforge.net>.
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * <p>
 * Created 03-Oct-2008 17:46:37
 */

package pcgen.core.term;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pcgen.core.Globals;
import pcgen.core.PCStat;
import pcgen.util.Logging;

public final class EvaluatorFactory
{

    Pattern internalVarPattern;
    Map<String, TermEvaluatorBuilder> BuilderStore;

    private final Map<String, TermEvaluator> SrcNeutralEvaluatorStore = new HashMap<>();
    private final Map<String, Map<String, TermEvaluator>> SrcDependantEvaluatorStore = new HashMap<>();

    public static final EvaluatorFactory PC = new EvaluatorFactory(true, TermEvaluatorBuilderPCVar.values());

    public static final EvaluatorFactory EQ = new EvaluatorFactory(false, TermEvaluatorBuilderEQVar.values());

    private EvaluatorFactory(boolean addStats, final TermEvaluatorBuilder[] termEvaluatorBuilders)
    {
        TermEvaluatorBuilder[] evals = (addStats) ? addStatBuilder(termEvaluatorBuilders) : termEvaluatorBuilders;

        BuilderStore = new TreeMap<>();
        StringBuilder sb = new StringBuilder("^(");

        boolean add = false;

        for (TermEvaluatorBuilder e : evals)
        {
            if (add)
            {
                sb.append("|");
            } else
            {
                add = true;
            }
            sb.append(e.getTermConstructorPattern());

            String[] keys = e.getTermConstructorKeys();
            for (String k : keys)
            {
                BuilderStore.put(k, e);
            }
        }

        sb.append(")");
        internalVarPattern = Pattern.compile(sb.toString());
    }

    private static TermEvaluatorBuilder[] addStatBuilder(TermEvaluatorBuilder[] builderArray)
    {
        int end = builderArray.length;

        TermEvaluatorBuilder[] tempArray = new TermEvaluatorBuilder[end + 1];

        System.arraycopy(builderArray, 0, tempArray, 0, end);

        tempArray[end] = makeStatBuilder();

        return tempArray;
    }

    private static TermEvaluatorBuilder makeStatBuilder()
    {
        Collection<PCStat> stats = Globals.getContext().getReferenceContext().getConstructedCDOMObjects(PCStat.class);
        List<String> s = new LinkedList<>();
        StringBuilder pSt = new StringBuilder(stats.size() * 4 + 6);

        pSt.append("(?:");
        boolean add1 = false;
        for (PCStat stat : stats)
        {
            if (add1)
            {
                pSt.append("|");
            } else
            {
                add1 = true;
            }
            pSt.append(stat.getKeyName());
            s.add(stat.getKeyName());
        }
        pSt.append(")");

        return new TermEvaluatorBuilderPCStat(pSt.toString(), s.toArray(new String[0]), false);
    }

    private TermEvaluator makeTermEvaluator(String term, String source)
    {

        Matcher mat = internalVarPattern.matcher(term);

        if (mat.find())
        {
            String matchedPortion = mat.group(1);
            TermEvaluatorBuilder f = BuilderStore.get(matchedPortion);

            try
            {
                if (f.isEntireTerm() && (term.length() != matchedPortion.length()))
                {
                    return null;
                } else
                {
                    return f.getTermEvaluator(term, source, matchedPortion);
                }
            } catch (TermEvaulatorException e)
            {
                if (Logging.isDebugMode())
                {
                    Logging.log(Logging.DEBUG, e.toString());
                }
            }
        }

        return null;
    }

    public TermEvaluator getTermEvaluator(String term, String source)
    {

        Map<String, TermEvaluator> inner = SrcDependantEvaluatorStore.get(term);

        if (inner == null)
        {
            TermEvaluator evaluator = SrcNeutralEvaluatorStore.get(term);
            if (evaluator != null)
            {
                return evaluator;
            }
        } else
        {
            TermEvaluator evaluator = inner.get(source);
            if (evaluator != null)
            {
                return evaluator;
            }
        }

        TermEvaluator evaluator = makeTermEvaluator(term, source);

        if (evaluator == null)
        {
            return null;
        }

        if (evaluator.isSourceDependant())
        {
            Map<String, TermEvaluator> i = SrcDependantEvaluatorStore.get(term);
            Map<String, TermEvaluator> j = (i == null) ? new HashMap<>() : i;
            j.put(source, evaluator);
            SrcDependantEvaluatorStore.put(term, j);
        } else
        {
            SrcNeutralEvaluatorStore.put(term, evaluator);
        }

        return evaluator;
    }
}
