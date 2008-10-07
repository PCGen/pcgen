/**
 * pcgen.core.term.EvaluatorFactory.java
 * Copyright © 2008 Andrew Wilson <nuance@users.sourceforge.net>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created 03-Oct-2008 17:46:37
 *
 * Current Ver: $Revision:$
 * Last Editor: $Author:$
 * Last Edited: $Date:$
 *
 */

package pcgen.core.term;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;

import pcgen.core.GameMode;
import pcgen.core.SettingsHandler;

public class EvaluatorFactory {

	Pattern internalVarPattern;
	Map<String, TermEvaluatorBuilder> BuilderStore;
	
	private Map<String, TermEvaluator> SrcNeutralEvaluatorStore = 
			new HashMap<String, TermEvaluator>();
	private Map<String, Map<String, TermEvaluator>> SrcDependantEvaluatorStore = 
			new HashMap<String, Map<String, TermEvaluator>>();


	public static EvaluatorFactory PC =
			new EvaluatorFactory(true, TermEvaluatorBuilderPCVar.values());
	
	public static EvaluatorFactory EQ =
			new EvaluatorFactory(false, TermEvaluatorBuilderEQVar.values());

	private EvaluatorFactory (
			boolean addStats,
			final TermEvaluatorBuilder[] termEvaluatorBuilders)
	{
		TermEvaluatorBuilder[] evals = (addStats) ?
				addStatBuilder(termEvaluatorBuilders) :
				termEvaluatorBuilders;

		BuilderStore     = new TreeMap<String, TermEvaluatorBuilder>();
		StringBuilder sb = new StringBuilder("^(");

		boolean add = false;

		for (TermEvaluatorBuilder e : evals) {
			if (add) {
				sb.append("|");
			} else {
				add = true;
			}
			sb.append(e.getTermConstructorPattern());
			
			String[] keys = e.getTermConstructorKeys();
			for (String k : keys) {
				BuilderStore.put(k, e);
			}
		}

		sb.append(")");
		internalVarPattern = Pattern.compile(sb.toString());	
	}
	
	private static TermEvaluatorBuilder[] addStatBuilder(
			TermEvaluatorBuilder[] builderArray)
	{
		int end = builderArray.length;

		TermEvaluatorBuilder[] tempArray = new TermEvaluatorBuilder[end + 1];

		System.arraycopy(builderArray, 0, tempArray, 0, end);

		tempArray[end] = makeStatBuilder();

		return tempArray;
	}

	private static TermEvaluatorBuilder makeStatBuilder()
	{
		GameMode game = SettingsHandler.getGame();
		int num = game.s_ATTRIBSHORT.length;
		String[] s = new String[num];

		StringBuffer pSt = new StringBuffer(num * 4 + 6);

		pSt.append("(?:");
		boolean add1 = false;
		for (int x = 0; x < num; ++x)
		{
			if (add1) {
				pSt.append("|");
			} else {
				add1 = true;
			}
			pSt.append(game.s_ATTRIBSHORT[x]);
			s[x] = game.s_ATTRIBSHORT[x];
		}
		pSt.append(")");

		return new TermEvaluatorBuilderPCStat(pSt.toString(), s, false);
	}

	private TermEvaluator makeTermEvaluator(
			String term,
			String source) {
		
		Matcher mat = internalVarPattern.matcher(term);

		if (mat.find()) {
			String matchedPortion = mat.group(1);
			TermEvaluatorBuilder f = BuilderStore.get(matchedPortion);

			try
			{
				if (f.isEntireTerm() &&
					(term.length() != matchedPortion.length()))
				{
					return null;
				}
				else
				{
					return f.getTermEvaluator(term, source, matchedPortion);
				}
			}
			catch (TermEvaulatorException e)
			{
				e.printStackTrace();
			}
		}
		
		return null;
	}

	public TermEvaluator getTermEvaluator (
			String term,
			String source) {

		Map<String, TermEvaluator> inner = SrcDependantEvaluatorStore.get(term);

		if (inner == null)
		{
			TermEvaluator evaluator = SrcNeutralEvaluatorStore.get(term);
			if (evaluator != null) {
				return evaluator; 
			}
		}
		else
		{
			TermEvaluator evaluator = inner.get(source);
			if (evaluator != null) {
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
			Map<String, TermEvaluator> j = (i == null) ? new HashMap<String, TermEvaluator>() : i;
			j.put(source, evaluator);
			SrcDependantEvaluatorStore.put(term, j);	
		}
		else
		{
			SrcNeutralEvaluatorStore.put(term, evaluator);
		}

		return evaluator;
	}
}
