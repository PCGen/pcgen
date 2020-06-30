/*
 * PJEP.java
 * Copyright 2003 (C) Greg Bingleman <byngl@hotmail.com>
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
package pcgen.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import pcgen.core.PlayerCharacter;
import pcgen.core.VariableProcessor;
import pcgen.persistence.lst.LstUtils;
import pcgen.system.PluginLoader;

import org.nfunk.jep.ASTFunNode;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * {@code PJEP}
 *
 *
 * Provides a common interface setup for Singular Systems' Java Mathematical Expression Parser.
 *
 * Provides the following functions:
 *   ceil, floor, getvar, var, max, min, if, roll, cl, charbonusto
 *
 * Provides the following variables:
 *   FALSE, TRUE
 */
public final class PJEP extends JEP
{
	private Object parent;
	private String variableSource;
	private static List<Class<PCGenCommand>> commandList = new ArrayList<>();
	private List<PCGenCommand> localCommandList = new ArrayList<>();

	public static void addCommand(Class<PCGenCommand> clazz)
	{
		commandList.add(clazz);
	}

	public static PluginLoader getJepPluginLoader()
	{
		return new PluginLoader()
		{

			@Override
			public void loadPlugin(Class clazz)
            {
				addCommand(clazz);
			}

			@Override
			public Class[] getPluginClasses()
			{
				return new Class[]{PCGenCommand.class};
			}
		};
	}

	public PJEP()
	{
		setAllowUndeclared(true);
		addStandardFunctions();

		for (Class<PCGenCommand> aClass : commandList)
		{
			try
			{
				PCGenCommand com = aClass.newInstance();
				localCommandList.add(com);
				addFunction(com.getFunctionName().toLowerCase(), com);
				addFunction(com.getFunctionName().toUpperCase(), com);
			}
			catch (InstantiationException | IllegalAccessException e)
			{
				Logging.errorPrint(e.getLocalizedMessage(), e);
			}
		}

		addFunction("cl", new ClassLevel());

		addVariable("TRUE", 1);
		addVariable("FALSE", 0);
	}

	@Override
	public Node parseExpression(String expression_in)
	{
		if (updateVariables())
		{
			initSymTab();
		}

		return super.parseExpression(expression_in);
	}

	/**
	 * Identify if the results of the calculation will be cachable.
	 *
	 * @return True if the result would be cachable, false otherwise.
	 */
	public boolean isResultCachable()
	{

		return isResultCachable(getTopNode());
	}

	/**
	 * Identify if results from this node (and its children) are all cachable.
	 *
	 * @param node The node to be checked.
	 * @return True if the result would be cachable, false otherwise.
	 */
	public boolean isResultCachable(Node node)
	{
		if (node instanceof ASTFunNode)
		{
			ASTFunNode funcNode = (ASTFunNode) node;
			if (funcNode.getPFMC() instanceof PCGenCommand)
			{
				PCGenCommand cmd = (PCGenCommand) funcNode.getPFMC();
				if (!cmd.getCachable())
				{
					return false;
				}
			}
		}
		for (int i = 0; i < node.jjtGetNumChildren(); i++)
		{
			if (!isResultCachable(node.jjtGetChild(i)))
			{
				return false;
			}
		}

		return true;
	}

	private boolean updateVariables()
	{
		boolean updated = true;
		if (localCommandList != null)
		{
			for (PCGenCommand com : localCommandList)
			{
				updated = updated && !com.updateVariables(this);
			}
		}
		return updated;
	}

	/**
	 * @deprecated
	 * eg. cl("Fighter")
	 * eg. cl("Fighter", 21)
	 * eg. cl()
	 */
	@Deprecated
	private final class ClassLevel extends PostfixMathCommand
	{
		private ClassLevel()
		{
			numberOfParameters = -1;
		}

		/**
		 * Runs classlevel on the inStack. The parameter is popped
		 * off the {@code inStack}, and the variable's value is
		 * pushed back to the top of {@code inStack}.
		 * 
		 * @param inStack The stack to process
		 * 
		 * @throws ParseException
		 */
		//		@SuppressWarnings("unchecked") //Uses JEP, which doesn't use generics
		@Override
		public void run(Stack inStack) throws ParseException
		{
			LstUtils.deprecationWarning("Jep function cl deprecated, use classlevel instead");

			// check the stack
			checkStack(inStack);

			// get the parameter from the stack

			int paramCount = curNumberOfParameters;

			// If there are no parameters and this is used in a CLASS file,
			// then use the class name

			if (paramCount == 0)
			{
				String src = getVariableSource();
				if (src.startsWith("CLASS:"))
				{
					src = src.substring(6);
					inStack.push(src);
					++paramCount;
				}
			}

			//
			// have to do this in reverse order...this is a stack afterall
			//
			Object param1;
			Object param2 = null;
			if (paramCount == 1)
			{
				param1 = inStack.pop();
			}
			else if (paramCount == 2)
			{
				param2 = inStack.pop();
				param1 = inStack.pop();

				if (param2 instanceof Integer)
				{
					// Nothing to do, it's already an Integer
				}
				else if (param2 instanceof Double)
				{
					param2 = ((Double) param2).intValue();
				}
				else
				{
					throw new ParseException("Invalid parameter type");
				}
			}
			else
			{
				throw new ParseException("Invalid parameter count");
			}

			if (param1 instanceof String)
			{
				PlayerCharacter aPC = null;
				if (parent instanceof VariableProcessor)
				{
					aPC = ((VariableProcessor) parent).getPc();
				}
				else if (parent instanceof PlayerCharacter)
				{
					aPC = (PlayerCharacter) parent;
				}
				if (aPC == null)
				{
					throw new ParseException("Invalid parent (no PC): " + parent.getClass().getName());
				}

				// ";BEFORELEVEL="
				String cl = (String) param1;
				if (param2 != null)
				{
					cl += ";BEFORELEVEL=" + param2.toString();
				}

				inStack.push(new Double(aPC.getClassLevelString(cl, false)));
			}
			else
			{
				throw new ParseException("Invalid parameter type");
			}
		}
	}

	/**
	 * @deprecated
	 * @return Returns the variableSource.
	 */
	@Deprecated
	private String getVariableSource()
	{
		return variableSource;
	}

	/**
	 * @param variableSource The variableSource to set.
	 */
	protected void setVariableSource(String variableSource)
	{
		this.variableSource = variableSource;
		for (PCGenCommand com : localCommandList)
		{
			com.setVariableSource(variableSource);
		}
	}

	/**
	 * @param parent The parent to set.
	 */
	public void setParent(Object parent)
	{
		this.parent = parent;
		for (PCGenCommand com : localCommandList)
		{
			com.setParent(parent);
		}
	}

	public static void clear()
	{
		commandList.clear();
	}
}
