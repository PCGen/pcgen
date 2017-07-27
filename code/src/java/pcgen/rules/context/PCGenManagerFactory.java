/*
 * Copyright 2017 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.rules.context;

import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.cdom.formula.ManagerKey;

public class PCGenManagerFactory implements ManagerFactory
{
	private final LoadContext context;

	public PCGenManagerFactory(LoadContext context)
	{
		this.context = context;
	}

	@Override
	public FormulaSemantics generateFormulaSemantics(FormulaManager manager,
		LegalScope legalScope, Class<?> assertedFormat)
	{
		FormulaSemantics semantics = ManagerFactory.super.generateFormulaSemantics(
			manager, legalScope, assertedFormat);
		return semantics.getWith(ManagerKey.CONTEXT, context);
	}

	@Override
	public EvaluationManager generateEvaluationManager(FormulaManager formulaManager,
		Class<?> assertedFormat)
	{
		EvaluationManager evalManager = ManagerFactory.super.generateEvaluationManager(
			formulaManager, assertedFormat);
		return evalManager.getWith(ManagerKey.CONTEXT, context);
	}

}