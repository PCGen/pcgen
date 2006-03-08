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
 *
 * Created on January 28, 2003, 11:18 PM
 *
 * @(#) $Id: PJEP.java,v 1.34 2005/12/20 17:53:11 byngl Exp $
 */
package pcgen.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.nfunk.jep.JEP;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import pcgen.core.PlayerCharacter;
import pcgen.core.VariableProcessor;
import pcgen.persistence.lst.LstUtils;

/**
 * <code>PJEP</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.34 $
 *
 * Provides a common interface setup for Singular Systems' Java Mathematical Expression Parser.
 *
 * Provides the following functions:
 *   ceil, floor, getvar, var, max, min, if, roll, cl, charbonusto
 *
 * Provides the following variables:
 *   FALSE, TRUE
 *
 */
public final class PJEP extends JEP
{
	private Object parent;
	private String variableSource;
	private static List commandList = new ArrayList();
	private List localCommandList = new ArrayList();
	private static int n = 0;

	public static void addCommand(Class clazz) {
		commandList.add(clazz);
	}
	
	public PJEP()
	{
		setAllowUndeclared(true);
		addStandardFunctions();
		
		for(int i = 0; i < commandList.size(); i++) {
			try {
				Class clazz = (Class)commandList.get(i);
				PCGenCommand com = (PCGenCommand) clazz.newInstance();
				localCommandList.add(com);
				addFunction(com.getFunctionName().toLowerCase(), com);
				addFunction(com.getFunctionName().toUpperCase(), com);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		addFunction("cl", new ClassLevel());

		addVariable("TRUE", 1);
		addVariable("FALSE", 0);
		System.out.println(n++);
	}

	public void parseExpression(String expression_in)
	{
		if (updateVariables())
		{
        	initSymTab();
		}

		super.parseExpression(expression_in);
	}

	private boolean updateVariables()
	{
		boolean updated = true;
		if(localCommandList != null)
		{
			for(int i = 0; i < localCommandList.size(); i++)
			{
				PCGenCommand com = (PCGenCommand)localCommandList.get(i);
				updated = updated && !com.updateVariables(this);
			}
		}
		return updated;
	}

	/**
	 * @deprecated
	 */
	//
	// eg. cl("Fighter")
	// eg. cl("Fighter", 21)
	// eg. cl()
	//
	private final class ClassLevel extends PostfixMathCommand
	{
		private ClassLevel()
		{
			numberOfParameters = -1;
		}

		/**
		 * Runs classlevel on the inStack. The parameter is popped
		 * off the <code>inStack</code>, and the variable's value is
		 * pushed back to the top of <code>inStack</code>.
		 * @param inStack
		 * @throws ParseException
		 */
		public void run(Stack inStack) throws ParseException
		{
			LstUtils.deprecationWarning("Jep function cl deprecated, use classlvl instead");
			
			// check the stack
			checkStack(inStack);

			// get the parameter from the stack
			Object param1;
			Object param2 = null;

			int paramCount = curNumberOfParameters;

			//
			// If there are no parameters and this is used in a CLASS file, then use the
			// class name
			//
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
					// TODO Do Nothing?
				}
				else if (param2 instanceof Double)
				{
					param2 = new Integer(((Double) param2).intValue());
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

			Object result = null;

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
				String cl = (String)param1;
				if (param2 != null)
				{
					cl += ";BEFORELEVEL=" + param2.toString();
				}

				result = new Double(aPC.getClassLevelString(cl, false));

				inStack.push(result);
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
	protected String getVariableSource()
	{
		return variableSource;
	}

	/**
	 * @param variableSource The variableSource to set.
	 */
	protected void setVariableSource(String variableSource)
	{
		this.variableSource = variableSource;
		for(int i = 0; i < localCommandList.size(); i++)
		{
			PCGenCommand com = (PCGenCommand)localCommandList.get(i);
			com.setVariableSource(variableSource);
		}
	}

	/**
	 * @param parent The parent to set.
	 */
	public void setParent(Object parent)
	{
		this.parent = parent;
		for(int i = 0; i < localCommandList.size(); i++)
		{
			PCGenCommand com = (PCGenCommand)localCommandList.get(i);
			com.setParent(parent);
		}
	}
}
