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
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.rules.context.AbstractReferenceContext;

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
		AbstractReferenceContext refContext =
				semantics.get(ManagerKey.CONTEXT).getReferenceContext();
		FormatManager<?> formatManager = refContext.getFormatManager(legalScopeName);
		if (formatManager == null)
		{
			semantics.setInvalid("Parse Error: Invalid Format Name: "
				+ legalScopeName + " is not a valid Format");
			return null;
		}
		if (!VarScoped.class.isAssignableFrom(formatManager.getManagedClass()))
		{
			semantics.setInvalid("Parse Error: Invalid Format Name: "
				+ legalScopeName + " is not capable of holding a Variable");
			return null;
		}
		//Rest of Equation
		if (!(formatManager instanceof ReferenceManufacturer))
		{
			semantics.setInvalid("Parse Error: Invalid Format Name: " + legalScopeName
				+ " is not Buildable");
			return null;
		}
		FormulaManager fm = semantics.get(FormulaSemantics.FMANAGER);
		ScopeInstanceFactory siFactory = fm.getScopeInstanceFactory();
		LegalScope legalScope = siFactory.getScope(getScopeNameFor(formatManager));
		semantics = semantics.getWith(FormulaSemantics.SCOPE, legalScope);
		return (FormatManager<?>) node.jjtAccept(visitor, semantics);
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
			AbstractReferenceContext refContext =
					manager.get(ManagerKey.CONTEXT).getReferenceContext();
			FormatManager<?> formatManager = refContext.getFormatManager(legalScopeName);
			vs = (VarScoped) formatManager.convert((String) result);
		}
		else if (result instanceof VarScoped)
		{
			vs = (VarScoped) result;
		}
		else
		{
			throw new IllegalStateException("result must be String or VarScoped");
		}
		return evaluateFromObject(visitor, vs, args[2], manager);
	}

	private Object evaluateFromObject(EvaluateVisitor visitor, 
		VarScoped vs, Node node, EvaluationManager manager)
	{
		FormulaManager fm = manager.get(EvaluationManager.FMANAGER);
		ScopeInstanceFactory siFactory = fm.getScopeInstanceFactory();
		ScopeInstance scopeInst = siFactory.get(vs.getLocalScopeName(), vs);
		//Rest of Equation
		return node.jjtAccept(visitor,
			manager.getWith(EvaluationManager.INSTANCE, scopeInst));
	}

	@Override
	public FormatManager<?> getDependencies(DependencyVisitor visitor, DependencyManager fdm,
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
		AbstractReferenceContext refContext =
				fdm.get(ManagerKey.CONTEXT).getReferenceContext();
		FormatManager<?> formatManager = refContext.getFormatManager(legalScopeName);
		DynamicDependency dd =
				new DynamicDependency(ts.getControlVar(), getScopeNameFor(formatManager));
		fdm.get(DependencyManager.DYNAMIC).addDependency(dd);
		FormulaManager fm = fdm.get(DependencyManager.FMANAGER);
		ScopeInstanceFactory siFactory = fm.getScopeInstanceFactory();
		LegalScope legalScope = siFactory.getScope(getScopeNameFor(formatManager));
		DependencyManager dynamic = fdm.getWith(DependencyManager.VARSTRATEGY, dd);
		dynamic = dynamic.getWith(DependencyManager.SCOPE, legalScope);
		//Rest of Equation
		return (FormatManager<?>) args[2].jjtAccept(visitor, dynamic);
	}

	private <T, V extends Loadable & VarScoped> String getScopeNameFor(
		FormatManager<T> formatManager)
	{
		//We know this from the VarScoped check above and limit on ReferenceManufacturer
		@SuppressWarnings("unchecked")
		ReferenceManufacturer<V> referenceManufacturer =
				(ReferenceManufacturer<V>) formatManager;
		V object = referenceManufacturer.buildObject("Dummy");
		return object.getLocalScopeName();
	}

}
