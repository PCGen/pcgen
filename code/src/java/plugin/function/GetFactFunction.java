/*
 * Copyright 2016-7 (C) Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.Function;
import pcgen.base.formula.base.LegalScope;
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
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.content.fact.FactDefinition;
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.formula.ManagerKey;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.util.StringPClassUtil;

public class GetFactFunction implements Function
{

	private static final Class<CDOMObject> CDOMOBJECT_CLASS = CDOMObject.class;

	@Override
	public String getFunctionName()
	{
		return "getFact";
	}

	@Override
	public Boolean isStatic(StaticVisitor visitor, Node[] args)
	{
		if (args.length == 3)
		{
			return (Boolean) args[1].jjtAccept(visitor, null);
		}
		return Boolean.TRUE;
	}

	@Override
	public FormatManager<?> allowArgs(SemanticsVisitor visitor, Node[] args,
		FormulaSemantics semantics)
	{
		if (args.length == 3)
		{
			return allowThreeArguments(visitor, semantics, args);
		}
		else
		{
			semantics.setInvalid("Function " + getFunctionName()
				+ " received incorrect # of arguments, expected: 2-3 got "
				+ args.length + " " + Arrays.asList(args));
			return null;
		}
	}

	private FormatManager<?> allowThreeArguments(SemanticsVisitor visitor,
		FormulaSemantics semantics, Node[] args)
	{
		//Turn scope node into a scope name
		Node scopeNode = args[0];
		if (!(scopeNode instanceof ASTQuotString))
		{
			semantics.setInvalid("Parse Error: Invalid Scope Node: "
				+ scopeNode.getClass().getName()
				+ " found in location requiring a"
				+ " Static String (class cannot be evaluated)");
			return null;
		}
		ASTQuotString qs = (ASTQuotString) scopeNode;
		String legalScopeName = qs.getText();

		if (args[1] instanceof ASTQuotString)
		{
			//Direct,  no dependencies, skip jjtAccept on args[1]
			return allowFromScopeName(visitor, semantics, legalScopeName, args[2]);
		}
		else if (args[1] instanceof ASTPCGenSingleWord)
		{
			//Variable
			FormatManager<?> objClass = (FormatManager<?>) args[1].jjtAccept(visitor,
				semantics.getWith(FormulaSemantics.ASSERTED, null));
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
		FormulaSemantics semantics, String legalScopeName, Node factNode)
	{
		FormulaManager fm = semantics.get(FormulaSemantics.FMANAGER);
		ScopeInstanceFactory siFactory = fm.getScopeInstanceFactory();
		LegalScope legalScope = siFactory.getScope(legalScopeName);
		if (legalScope == null)
		{
			semantics.setInvalid("Parse Error: Invalid Scope Name: "
				+ legalScopeName + " is not a valid scope name");
			return null;
		}
		Class<? extends Loadable> objectFormat =
				StringPClassUtil.getClassFor(legalScopeName);
		if (!CDOMOBJECT_CLASS.isAssignableFrom(objectFormat))
		{
			//This is basically an internal error! but catch it anyway for now
			semantics.setInvalid("Parse Error: Invalid Scope Name: "
				+ legalScopeName + " is not capable of holding a Fact");
			return null;
		}
		if (!(factNode instanceof ASTQuotString))
		{
			semantics.setInvalid("Parse Error: Invalid Fact Node: "
				+ factNode.getClass().getName()
				+ " found in location requiring a"
				+ " Static String (class cannot be evaluated)");
			return null;
		}
		ASTQuotString qs = (ASTQuotString) factNode;
		String factName = qs.getText();
		AbstractReferenceContext refContext =
				semantics.get(ManagerKey.CONTEXT).getReferenceContext();
		FactDefinition<?, ?> factDef =
				refContext.silentlyGetConstructedCDOMObject(
					FactDefinition.class, legalScopeName + "." + factName);
		if (factDef == null)
		{
			semantics.setInvalid("Parse Error: Invalid Fact: " + factName
				+ " is not a valid FACT name");
			return null;
		}
		Class<?> usable = factDef.getUsableLocation();
		if (!usable.isAssignableFrom(objectFormat))
		{
			semantics.setInvalid("Parse Error: Invalid Fact: "
				+ factDef.getDisplayName() + " works on " + usable
				+ " but formula asserted it was in " + objectFormat);
			return null;
		}
		return factDef.getFormatManager();
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
			if (vs == null)
			{
				System.err.println("Object " + result + " of type "
					+ objClass.getSimpleName() + " was not found");
			}
		}
		else if (result instanceof VarScoped)
		{
			vs = (VarScoped) result;
		}
		else
		{
			throw new IllegalStateException("result must be String or VarScoped");
		}
		//TODO This cast is reckless :(
		return evaluateFromObject(visitor, (CDOMObject) vs, args[2], manager);
	}

	private Object evaluateFromObject(EvaluateVisitor visitor,
		CDOMObject object, Node node, EvaluationManager manager)
	{
		String factName = (String) node.jjtAccept(visitor, manager);
		FactKey<Object> fk = FactKey.valueOf(factName);
		return object.getResolved(fk);
	}

	@Override
	public void getDependencies(DependencyVisitor visitor,
		DependencyManager fdm, Node[] args)
	{
		int argCount = args.length;
		if (argCount == 2)
		{
			args[0].jjtAccept(visitor,
				fdm.getWith(DependencyManager.ASSERTED, null));
		}
		else if (argCount == 3)
		{
			args[1].jjtAccept(visitor,
				fdm.getWith(DependencyManager.ASSERTED, FormatUtilities.STRING_MANAGER));
		}
	}
}
