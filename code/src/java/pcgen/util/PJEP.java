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

import org.nfunk.jep.JEP;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;
import pcgen.core.*;

import java.util.Stack;

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

	public PJEP()
	{
		setAllowUndeclared(true);
		addStandardFunctions();

		addFunction("ceil", new Ceil());
		addFunction("floor", new Floor());
		addFunction("max", new Max());
		addFunction("min", new Min());
		addFunction("getvar", new GetVar());
		addFunction("var", new GetVar());
		addFunction("if", new If());
		addFunction("roll", new Roll());
		addFunction("cl", new ClassLevel());
		addFunction("charbonusto", new CharBonusTo());
		addFunction("skillinfo", new SkillInfo());

		addFunction("CEIL", new Ceil());
		addFunction("FLOOR", new Floor());
		addFunction("MAX", new Max());
		addFunction("MIN", new Min());
		addFunction("GETVAR", new GetVar());
		addFunction("VAR", new GetVar());
		addFunction("IF", new If());
		addFunction("ROLL", new Roll());
//		addFunction("CL", new ClassLevel());
		addFunction("CHARBONUSTO", new CharBonusTo());
		addFunction("SKILLINFO", new SkillInfo());

		addVariable("TRUE", 1);
		addVariable("FALSE", 0);
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
		boolean bUpdated = updateCL();
		return bUpdated;
	}

	private boolean updateCL()
	{
		boolean bUpdated = false;
		if (removeVariable("CL") != null)
		{
			bUpdated = true;
		}

		String src = getVariableSource();
		if ((src == null) || !src.startsWith("CLASS:"))
		{
			return bUpdated;
		}
		src = src.substring(6);

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
			return bUpdated;
		}


		Double result = new Double(aPC.getClassLevelString(src, false));
		addVariable("CL", result.doubleValue());

		return true;
	}

	//
	// eg. ceil(12.6) --> 13
	//
	private static final class Ceil extends PostfixMathCommand
	{
		/**
		 * Constructor
		 */
		private Ceil()
		{
			numberOfParameters = 1;
		}

		/**
		 * Runs ceil on the inStack. The parameter is popped
		 * off the <code>inStack</code>, and the ceiling of it's value is
		 * pushed back to the top of <code>inStack</code>.
		 * @param inStack
		 * @throws ParseException
		 */
		public void run(Stack inStack) throws ParseException
		{
			// check the stack
			checkStack(inStack);

			// get the parameter from the stack
			Object param = inStack.pop();

			// check whether the argument is of the right type
			if (param instanceof Double)
			{
				// calculate the result
				double r = Math.ceil(((Double) param).doubleValue());

				// push the result on the inStack
				inStack.push(new Double(r));
			}
			else
			{
				throw new ParseException("Invalid parameter type");
			}
		}
	}

	//
	// eg. floor(12.6) --> 12
	//
	private static final class Floor extends PostfixMathCommand
	{
		/**
		 * Constructor
		 */
		private Floor()
		{
			numberOfParameters = 1;
		}

		/**
		 * Runs floor on the inStack. The parameter is popped
		 * off the <code>inStack</code>, and the floor of its value is
		 * pushed back to the top of <code>inStack</code>.
		 * @param inStack
		 * @throws ParseException
		 */
		public void run(Stack inStack) throws ParseException
		{
			// check the stack
			checkStack(inStack);

			// get the parameter from the stack
			Object param = inStack.pop();

			// check whether the argument is of the right type
			if (param instanceof Double)
			{
				// calculate the result
				double r = Math.floor(((Double) param).doubleValue());

				// push the result on the inStack
				inStack.push(new Double(r));
			}
			else
			{
				throw new ParseException("Invalid parameter type");
			}
		}
	}


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


	//
	// eg. charbonusto("PCLEVEL", "Wizard");
	// eg. charbonusto("Wizard");
	//
	private final class CharBonusTo extends PostfixMathCommand
	{
		private CharBonusTo()
		{
			numberOfParameters = -1;
		}

		/**
		 * Runs charbonusto on the inStack. The parameter is popped
		 * off the <code>inStack</code>, and the variable's value is
		 * pushed back to the top of <code>inStack</code>.
		 * @param inStack
		 * @throws ParseException
		 */
		public void run(Stack inStack) throws ParseException
		{
			// check the stack
			checkStack(inStack);

			// get the parameter from the stack
			Object param1;
			Object param2;

			//
			// have to do this in reverse order...this is a stack afterall
			//
			if (curNumberOfParameters == 1)
			{
				param2 = inStack.pop();
				param1 = "PCLEVEL";
			}
			else if (curNumberOfParameters == 2)
			{
				param2 = inStack.pop();
				param1 = inStack.pop();
			}
			else
			{
				throw new ParseException("Invalid parameter count");
			}

			Object result = null;

			if ((param1 instanceof String) && (param2 instanceof String))
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

				result = new Double(aPC.getTotalBonusTo((String)param1, (String)param2));

				inStack.push(result);
			}
			else
			{
				throw new ParseException("Invalid parameter type");
			}
		}

	}


	//
	// eg. skill("rank", "Swim")
	// eg. skill("total", "Swim")
	// eg. skill("modifier", "Swim")
	// eg. skill("totalrank", "Swim")
	//
	private final class SkillInfo extends PostfixMathCommand
	{
		/**
		 * Constructor
		 */
		private SkillInfo()
		{
			numberOfParameters = 2;
		}

		/**
		 * Runs skill on the inStack. The parameter is popped
		 * off the <code>inStack</code>, and the variable's value is
		 * pushed back to the top of <code>inStack</code>.
		 * @param inStack
		 * @throws ParseException
		 */
		public void run(Stack inStack) throws ParseException
		{
			// check the stack
			checkStack(inStack);

			// get the parameters from the stack
			//
			// have to do this in reverse order...this is a stack afterall
			//
			Object param2 = inStack.pop();
			Object param1 = inStack.pop();

			if ((param1 instanceof String) && (param2 instanceof String))
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

	    		final Skill aSkill = aPC.getSkillNamed((String)param2);

				Object result = null;
	    		if (aSkill != null)
	    		{
					if (((String) param1).equalsIgnoreCase("modifier"))
					{
						result = new Double(aSkill.modifier(aPC).intValue());		// aSkill.modifier() returns Integer
					}
					else if (((String) param1).equalsIgnoreCase("rank"))
					{
						result = new Double(aSkill.getRank().doubleValue());		// aSkill.getRank() returns Float
					}
					else if (((String) param1).equalsIgnoreCase("total"))
					{
						result = new Double(aSkill.getTotalRank(aPC).intValue() + aSkill.modifier(aPC).intValue());
					}
					else if (((String) param1).equalsIgnoreCase("totalrank"))
					{
						result = new Double(aSkill.getTotalRank(aPC).doubleValue());	// aSkill.getTotalRank() returns Float
					}
				}
				else
				{
					result = new Double(0);
				}

				inStack.push(result);
			}
			else
			{
				throw new ParseException("Invalid parameter type");
			}
		}
	}

	//
	// eg. roll("10+d10")
	//
	private final class Roll extends PostfixMathCommand
	{
		/**
		 * Constructor
		 */
		private Roll()
		{
			numberOfParameters = 1;
		}

		/**
		 * Runs getvar on the inStack. The parameter is popped
		 * off the <code>inStack</code>, and the variable's value is
		 * pushed back to the top of <code>inStack</code>.
		 * @param inStack
		 * @throws ParseException
		 */
		public void run(Stack inStack) throws ParseException
		{
			// check the stack
			checkStack(inStack);

			// get the parameter from the stack
			Object param1;

			//
			// have to do this in reverse order...this is a stack afterall
			//
			param1 = inStack.pop();

			Object result = null;

			if (param1 instanceof String)
			{
				result = new Integer(pcgen.core.RollingMethods.roll((String)param1));

				inStack.push(result);
			}
			else
			{
				throw new ParseException("Invalid parameter type");
			}
		}
	}

	//
	// eg. getvar("CL=Fighter")
	//
	private final class GetVar extends PostfixMathCommand
	{
		/**
		 * Constructor
		 */
		private GetVar()
		{
			numberOfParameters = -1; // allow variable # of parameters
		}

		/**
		 * Runs getvar on the inStack. The parameter is popped
		 * off the <code>inStack</code>, and the variable's value is
		 * pushed back to the top of <code>inStack</code>.
		 * @param inStack
		 * @throws ParseException
		 */
		public void run(Stack inStack) throws ParseException
		{
			// check the stack
			checkStack(inStack);

			// get the parameter from the stack
			Object param1;
			Object param2 = null;

			//
			// have to do this in reverse order...this is a stack afterall
			//
			if (curNumberOfParameters == 1)
			{
				param1 = inStack.pop();
			}
			else if (curNumberOfParameters == 2)
			{
				param2 = inStack.pop();
				param1 = inStack.pop();

				if (!(param2 instanceof Double))
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
				if (parent instanceof PlayerCharacter)
				{
					PlayerCharacter character = (PlayerCharacter)parent;
					result = getVariableForCharacter(character, param1);
				}
				else if (parent instanceof Equipment)
				{
					boolean bPrimary = true;

					if (param2 != null)
					{
						bPrimary = (((Double) param2).intValue() != 0);
					}

					result = ((Equipment) parent).getVariableValue((String) param1, "", bPrimary, null);
				}
				else if (parent instanceof VariableProcessorPC)
				{
					VariableProcessorPC vpc = (VariableProcessorPC) parent;
					// check to see if this is just a variable
					result = vpc.lookupVariable((String)param1, getVariableSource(), null);
					if (result == null)
					{
						result = vpc.getVariableValue(null, (String)param1, getVariableSource(), 0);
					}
				}
				else if (parent instanceof VariableProcessorEq)
				{
					VariableProcessorEq veq = (VariableProcessorEq) parent;
					result = veq.getVariableValue(null, (String)param1, getVariableSource(), 0);
				}
				else if (parent == null)
				{
					Logging.errorPrint("Ignored request for var " + String.valueOf(param1) + " with no parent.");
//					PlayerCharacter aPC = Globals.getCurrentPC();
//
//					if (aPC != null)
//					{
//						result = getVariableForCharacter(aPC, param1);
//					}
				}

				if (result == null)
				{
					throw new ParseException("Error retreiving variable:" + param1);
				}

				inStack.push(result);
			}
			else
			{
				throw new ParseException("Invalid parameter type");
			}
		}

		protected Object getVariableForCharacter(PlayerCharacter character, Object param1)
		{
			//System.out.println("getVariableForCharacter(" + param1 + ", " + getVariableSource() + ")");
			Object result = character.getVariableValue((String) param1, getVariableSource());
			return result;
		}
	}

	//
	// eg. max(12.6, 20) --> 20
	//
	private static final class Max extends PostfixMathCommand
	{

		/**
		 * <p>
		 * Initializes the number of parameters to = -1, indicating a variable
		 * number of parameters.
		 * </p>
		 *
		 */
		public Max()
		{
			super();
			numberOfParameters = -1;
		}

		/**
		 * <p>
		 * Calculates the maximum of the parameters on the stack, all of which
		 * are assumed to be of type double.
		 * </p>
		 *
		 * @param stack
		 *            Stack of incoming arguments.
		 * @throws ParseException
		 */
		public void run(Stack stack) throws ParseException
		{
			// Check if stack is null
			if (null == stack)
			{
				throw new ParseException("Stack argument null");
			}

			Object param = null;
			double result = 0;
			boolean first = true;
			int i = 0;

			// repeat summation for each one of the current parameters
			while (i < curNumberOfParameters)
			{
				// get the parameter from the stack
				param = stack.pop();
				if (param instanceof Number)
				{
					// calculate the result
					if (first || ((Number) param).doubleValue() > result)
					{
						result = ((Number) param).doubleValue();
					}
				}
				else
				{
					throw new ParseException("Invalid parameter type");
				}
				first = false;

				i++;
			}

			// push the result on the inStack
			stack.push(new Double(result));
		}

	}

	//
	// eg. min(12.6, 20) --> 12.6
	//
	private static final class Min extends PostfixMathCommand
	{

		/**
		 * <p>
		 * Initializes the number of parameters to = -1, indicating a variable
		 * number of parameters.
		 * </p>
		 *
		 */
		public Min()
		{
			super();
			numberOfParameters = -1;
		}

		/**
		 * <p>
		 * Calculates the minimum of the parameters on the stack, all of which
		 * are assumed to be of type double.
		 * </p>
		 *
		 * @param stack
		 *            Stack of incoming arguments.
		 * @throws ParseException
		 */
		public void run(Stack stack) throws ParseException
		{
			// Check if stack is null
			if (null == stack)
			{
				throw new ParseException("Stack argument null");
			}

			Object param = null;
			double result = 0;
			boolean first = true;
			int i = 0;

			// repeat summation for each one of the current parameters
			while (i < curNumberOfParameters)
			{
				// get the parameter from the stack
				param = stack.pop();
				if (param instanceof Number)
				{
					// calculate the result
					if (first || ((Number) param).doubleValue() < result)
					{
						result = ((Number) param).doubleValue();
					}
				}
				else
				{
					throw new ParseException("Invalid parameter type");
				}
				first = false;

				i++;
			}

			// push the result on the inStack
			stack.push(new Double(result));
		}
	}

	/**
	 * <p>
	 * If class; extends PostfixMathCommand. This class accepts three agruments.
	 * The first is a number interpreted as a boolean. The other two may be any
	 * supported classes. If the first argument != 0, the second argument is
	 * returned. Otherwise, the third argument is returned.
	 * </p>
	 * <p>
	 * So, given if(x,y,z), y is returned if x != 0, and z is returned
	 * otherise.
	 * </p>
	 *
	 * @author Ross M. Lodge
	 *
	 */
	private static final class If extends PostfixMathCommand
	{
		/**
		 * <p>
		 * Initializes the number of parameters to = 3, indicating three number
		 * of parameters.
		 * </p>
		 *
		 */
		public If()
		{
			super();
			numberOfParameters = 3;
		}

		/**
		 * <p>
		 * Evaluates the three parameters. The first may be a subclass of
		 * Number, or a Boolean. The second and third can be any supported type.
		 * If the first argument is true, the second argument is returned;
		 * otherwise, the third argument is returned.
		 * </p>
		 *
		 * @param stack
		 *            Stack of incoming arguments.
		 * @throws ParseException
		 */
		public void run(Stack stack) throws ParseException
		{
			// Check if stack is null
			if (null == stack)
			{
				throw new ParseException("Stack argument null");
			}

			Object result = null;

			boolean condition = false;

			Object param3 = stack.pop();
			Object param2 = stack.pop();
			Object param1 = stack.pop();

			if (param1 instanceof Number)
			{
				condition = (((Number) param1).doubleValue() != 0d);
			}
			else if (param1 instanceof Boolean)
			{
				condition = ((Boolean) param1).booleanValue();
			}
			else
			{
				throw new ParseException(
						"Invalid parameter type for Parameter 1");
			}

			if (condition)
			{
				result = param2;
			}
			else
			{
				result = param3;
			}

			// push the result on the inStack
			stack.push(result);
		}

	}

	/**
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
	}

	/**
	 * @return Returns the parent.
	 */
	public Object getParent()
	{
		return parent;
	}
	/**
	 * @param parent The parent to set.
	 */
	public void setParent(Object parent)
	{
		this.parent = parent;
	}
}
