/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
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
package plugin.function;

import java.util.Arrays;

import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.DynamicDependency;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.Function;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.TrainingStrategy;
import pcgen.base.formula.base.VarScoped;
import pcgen.base.formula.inst.ScopeInstanceFactory;
import pcgen.base.formula.parse.ASTPCGenSingleWord;
import pcgen.base.formula.parse.ASTQuotString;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;
import pcgen.base.util.FormatManager;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.formula.ManagerKey;
import pcgen.util.StringPClassUtil;

public class DropIntoContext implements Function
{

	@Override
	public String getFunctionName()
	{
		return "dropIntoContext";
	}

	@Override
	public Boolean isStatic(StaticVisitor visitor, Node[] args)
	{
		//3-args, but we know first one is static (scope name)
		return (Boolean) args[1].jjtAccept(visitor, null)
			&& (Boolean) args[2].jjtAccept(visitor, null);
	}

	@Override
	public FormatManager<?> allowArgs(SemanticsVisitor visitor, Node[] args,
		FormulaSemantics semantics)
	{
		int argCount = args.length;
		if (argCount == 3)
		{
			return allowThreeArguments(visitor, semantics, args);
		}
		else
		{
			semantics.setInvalid("Function " + getFunctionName()
				+ " received incorrect # of arguments, expected: 3 got " + args.length
				+ ' ' + Arrays.asList(args));
			return null;
		}
	}

	private FormatManager<?> allowThreeArguments(SemanticsVisitor visitor,
		FormulaSemantics semantics, Node[] args)
	{
		Node scopeNode = args[0];
		if (!(scopeNode instanceof ASTQuotString))
		{
			semantics.setInvalid("Parse Error: Invalid Scope Node: "
				+ scopeNode.getClass().getName() + " found in location requiring a"
				+ " Static String (first arg cannot be evaluated)");
			return null;
		}
		ASTQuotString qs = (ASTQuotString) scopeNode;
		String legalScopeName = qs.getText();

		if (args[1] instanceof ASTQuotString)
		{
			//Direct,  no dependencies
			return allowFromScopeName(visitor, semantics, legalScopeName, args[2]);
		}
		else if (args[1] instanceof ASTPCGenSingleWord)
		{
			//Variable
			semantics = semantics.getWith(FormulaSemantics.ASSERTED, null);
			FormatManager<?> objClass =
					(FormatManager<?>) args[1].jjtAccept(visitor, semantics);
			if (!semantics.isValid())
			{
				return null;
			}
			Class<?> managedClass = objClass.getManagedClass();
			if (String.class.isAssignableFrom(managedClass)
				|| VarScoped.class.isAssignableFrom(objClass.getManagedClass()))
			{
				return allowFromScopeName(visitor, semantics, legalScopeName, args[2]);
			}
			else
			{
				semantics.setInvalid("Parse Error: Invalid Object Format: " + objClass
					+ " is not capable of holding variables and is not a key (String)");
				return null;
			}
		}
		else
		{
			//Error
			semantics.setInvalid("Parse Error: Invalid second argument: "
				+ " must be a String or single variable");
			return null;
		}
	}

	private FormatManager<?> allowFromScopeName(SemanticsVisitor visitor,
		FormulaSemantics semantics, String legalScopeName, Node node)
	{
		FormulaManager fm = semantics.get(FormulaSemantics.FMANAGER);
		ScopeInstanceFactory siFactory = fm.getScopeInstanceFactory();
		LegalScope legalScope = siFactory.getScope(legalScopeName);
		if (legalScope == null)
		{
			semantics.setInvalid("Parse Error: Invalid Scope Name: " + legalScopeName
				+ " is not a valid scope name");
			return null;
		}
		//Rest of Equation
		semantics = semantics.getWith(FormulaSemantics.FMANAGER, fm);
		semantics = semantics.getWith(FormulaSemantics.SCOPE, legalScope);
		FormatManager<?> format = (FormatManager<?>) node.jjtAccept(visitor, semantics);
		return format;
	}

	@Override
	public Object evaluate(EvaluateVisitor visitor, Node[] args,
		EvaluationManager manager)
	{
		ASTQuotString qs = (ASTQuotString) args[0];
		String legalScopeName = qs.getText();
		Object result = args[1].jjtAccept(visitor, manager);
		VarScoped vs;
		if (result instanceof String)
		{
			Class<? extends Loadable> objClass = StringPClassUtil.getClassFor(legalScopeName);
			vs = (VarScoped) manager.get(ManagerKey.CONTEXT).getReferenceContext()
				.silentlyGetConstructedCDOMObject(objClass, (String) result);
		}
		else if (result instanceof VarScoped)
		{
			vs = (VarScoped) result;
		}
		else
		{
			throw new IllegalStateException("result must be String or VarScoped");
		}
		return evaluateFromObject(visitor, legalScopeName, vs, args[2], manager);
	}

	private Object evaluateFromObject(EvaluateVisitor visitor, String legalScopeName,
		VarScoped vs, Node node, EvaluationManager manager)
	{
		FormulaManager fm = manager.get(EvaluationManager.FMANAGER);
		ScopeInstanceFactory siFactory = fm.getScopeInstanceFactory();
		ScopeInstance scopeInst = siFactory.get(legalScopeName, vs);
		//Rest of Equation
		return node.jjtAccept(visitor,
			manager.getWith(EvaluationManager.INSTANCE, scopeInst));
	}

	@Override
	public void getDependencies(DependencyVisitor visitor, DependencyManager fdm,
		Node[] args)
	{
		String legalScopeName = ((ASTQuotString) args[0]).getText();

		TrainingStrategy ts = new TrainingStrategy();
		DependencyManager trainer = fdm.getWith(DependencyManager.VARSTRATEGY, ts);
		if (args[1] instanceof ASTQuotString)
		{
			//Direct,  no dependencies
		}
		else if (args[1] instanceof ASTPCGenSingleWord)
		{
			//Variable
			args[1].jjtAccept(visitor, trainer);
		}
		else
		{
			//Error
		}

		DynamicDependency dd = new DynamicDependency(ts.getControlVar(), legalScopeName);
		fdm.get(DependencyManager.DYNAMIC).addDependency(dd);
		DependencyManager dynamic = fdm.getWith(DependencyManager.VARSTRATEGY, dd);
		//Rest of Equation
		args[2].jjtAccept(visitor, dynamic);
	}

}
