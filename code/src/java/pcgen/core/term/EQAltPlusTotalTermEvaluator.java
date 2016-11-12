/*
 * EQAltPlusTermEvaluator.java
 * Copyright 2009 (C) James Dempsey
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
 * Created on 09/01/2009 7:26:13 PM
 *
 * $Id: $
 */
package pcgen.core.term;

import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;

/**
 * The Class {@code EQAltPlusTotalTermEvaluator} is responsible for producing
 * the value of the ALTPLUSTOTAL token for use in equipment and eqmod cost formulas. 
 * 
 * 
 * @author James Dempsey &lt;jdempsey@users.sourceforge.net&gt;
 */
public class EQAltPlusTotalTermEvaluator extends BaseEQTermEvaluator implements TermEvaluator
{
	
	/**
	 * Instantiates a new eQ plus term evaluator.
	 * 
	 * @param expressionString the expression string
	 */
	public EQAltPlusTotalTermEvaluator(String expressionString)
	{
		this.originalText = expressionString;
	}
	
	/* (non-Javadoc)
	 * @see pcgen.core.term.TermEvaluator#resolve(pcgen.core.Equipment, boolean, pcgen.core.PlayerCharacter)
	 */
	@Override
	public Float resolve(
			Equipment eq,
			boolean primary,
			PlayerCharacter pc)
	{
		return convertToFloat(originalText, evaluate(eq, primary, pc));
	}

	/* (non-Javadoc)
	 * @see pcgen.core.term.TermEvaluator#evaluate(pcgen.core.Equipment, boolean, pcgen.core.PlayerCharacter)
	 */
	@Override
	public String evaluate(
			Equipment eq,
			boolean primary,
			PlayerCharacter pc) {
		return Integer.toString(eq.calcPlusForHead(false));
	}

	/* (non-Javadoc)
	 * @see pcgen.core.term.TermEvaluator#isSourceDependant()
	 */
	@Override
	public boolean isSourceDependant()
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.term.TermEvaluator#isStatic()
	 */
	public boolean isStatic()
	{
		return false;
	}
	
}
