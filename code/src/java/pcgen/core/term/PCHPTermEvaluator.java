/*
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
 */
package pcgen.core.term;

import pcgen.core.PlayerCharacter;

/**
 * The Class {@code PCHPTermEvaluator} is responsible for
 * providing the internal variable HP, which has the value of
 * the character's maximum total hit points. 
 * 
 * 
 */
public class PCHPTermEvaluator extends BasePCTermEvaluator implements TermEvaluator
{

	/**
	 * Instantiates a new pCHP term evaluator.
	 * 
	 * @param originalText the original text
	 */
	public PCHPTermEvaluator(String originalText)
	{
		this.originalText = originalText;
	}

	@Override
	public Float resolve(PlayerCharacter pc)
	{
		return (float) pc.hitPoints();
	}

	@Override
	public boolean isSourceDependant()
	{
		return false;
	}

	public boolean isStatic()
	{
		return false;
	}
}
