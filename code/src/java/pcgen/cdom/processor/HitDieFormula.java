/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.processor;

import pcgen.base.formula.ReferenceFormula;
import pcgen.cdom.content.HitDie;
import pcgen.cdom.content.Processor;

/**
 * A HitDieFormula represents a modified HitDie that changes relative to a
 * ReferenceFormula
 */
public class HitDieFormula implements Processor<HitDie>
{

	/**
	 * The ReferenceFormula used by this HitDieFormula to modify an incoming
	 * HitDie.
	 */
	private final ReferenceFormula<Integer> formula;

	/**
	 * Constructs a new HitDieFormula object with the given ReferenceFormula to
	 * modify HitDie objects.
	 * 
	 * @param refFormula
	 *            A ReferenceFormula to modify a HitDie when this HitDieFormula
	 *            acts on a given HitDie
	 */
	public HitDieFormula(ReferenceFormula<Integer> refFormula)
	{
		formula = refFormula;
	}

	/**
	 * Applies this Processor to the given input HitDie, which modifies the given
	 * input HitDie with the formula provided at construction of the
	 * HitDieFormula.
	 * 
	 * Since HitDieFormula is universal, the given context is ignored.
	 * 
	 * @param origHD
	 *            The input HitDie this HitDieFormula will act upon.
	 * @param context
	 *            The context, ignored by HitDieFormula.
	 * @return The modified HitDie
	 * @throws NullPointerException
	 *             if the given HitDie is null
	 */
	@Override
	public HitDie applyProcessor(HitDie origHD, Object context)
	{
		return new HitDie(formula.resolve(origHD.getDie()));
	}

	/**
	 * Returns a representation of this HitDieFormula, suitable for storing in
	 * an LST file.
	 * 
	 * @return A representation of this HitDieFormula, suitable for storing in
	 *         an LST file.
	 */
	@Override
	public String getLSTformat()
	{
		return '%' + formula.toString();
	}

	/**
	 * The class of object this Processor acts upon (HitDie).
	 * 
	 * @return The class of object this Processor acts upon (HitDie.class)
	 */
	@Override
	public Class<HitDie> getModifiedClass()
	{
		return HitDie.class;
	}

	@Override
	public int hashCode()
	{
		return formula.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof HitDieFormula && ((HitDieFormula) obj).formula.equals(formula);
	}
}
