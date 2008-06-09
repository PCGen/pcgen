/*
 * PrerequisiteOperator.java
 *
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 19-Dec-2003
 *
 * Current Ver: $Revision$
 *
 * Last Editor: $Author$
 *
 * Last Edited: $Date$
 *
 */
package pcgen.core.prereq;
import pcgen.core.utils.CoreUtility;
import pcgen.util.PropertyFactory;


/**
 * @author wardc
 *
 */
public class PrerequisiteOperator
{
	private static final int ComparatorGTEQ = 0;
	private static final int ComparatorGT   = 1;
	private static final int ComparatorEQ   = 2;
	private static final int ComparatorNEQ  = 3;
	private static final int ComparatorLT   = 4;
	private static final int ComparatorLTEQ = 5;

	private static final String validTypes[] = {
		"gteq",		//$NON-NLS-1$
		"gt",		//$NON-NLS-1$
		"eq",		//$NON-NLS-1$
		"neq",		//$NON-NLS-1$
		"lt",		//$NON-NLS-1$
		"lteq"		//$NON-NLS-1$
	}; 

	private static final String altValidTypes[] = {
		">=",		//$NON-NLS-1$
		">",		//$NON-NLS-1$
		"=",		//$NON-NLS-1$
		"!=",		//$NON-NLS-1$
		"<",		//$NON-NLS-1$
		"<="		//$NON-NLS-1$
	}; 


	public static final PrerequisiteOperator GTEQ = new PrerequisiteOperator(ComparatorGTEQ);
	public static final PrerequisiteOperator GT   = new PrerequisiteOperator(ComparatorGT);
	public static final PrerequisiteOperator EQ   = new PrerequisiteOperator(ComparatorEQ);
	public static final PrerequisiteOperator NEQ  = new PrerequisiteOperator(ComparatorNEQ);
	public static final PrerequisiteOperator LT   = new PrerequisiteOperator(ComparatorLT);
	public static final PrerequisiteOperator LTEQ = new PrerequisiteOperator(ComparatorLTEQ);

	
	private int value = ComparatorGTEQ;

//	private PrerequisiteOperator(final String operator) throws PrerequisiteException
//	{
//		value = getComparisonType(operator);
//	}

	private PrerequisiteOperator(final int operator)
	{
		value = operator;
	}


	public PrerequisiteOperator invert()
	{
		switch(value)
		{
			case ComparatorGT:
				return LTEQ;
				
			case ComparatorEQ:
				return NEQ;
				
			case ComparatorNEQ:
				return EQ;
				
			case ComparatorLT:
				return GTEQ;
				
			case ComparatorLTEQ:
				return GT;
				
			default:
				return LT;
		}
	}


	@Override
	public String toString()
	{
		return validTypes[value];
	}


	public String toDisplayString()
	{
		return PropertyFactory.getString("PrerequisiteOperator.display." + toString());
	}


	public int compare(final int leftHandOp, final int rightHandOp)
	{
		return (int) compare((float) leftHandOp, (float)rightHandOp);
	}


	public float compare(final float leftHandOp, final float rightHandOp)
	{
		boolean passes = false;

		switch(value)
		{
			case ComparatorEQ:
				passes = CoreUtility.doublesEqual(leftHandOp, rightHandOp);
				break;

			case ComparatorLT:
				passes = (leftHandOp < rightHandOp);
				break;

			case ComparatorLTEQ:
				passes = (leftHandOp <= rightHandOp);
				break;

			case ComparatorGT:
				passes = (leftHandOp > rightHandOp);
				break;

			case ComparatorNEQ:
				passes = !CoreUtility.doublesEqual(leftHandOp, rightHandOp);
				break;

			default:
				passes = (leftHandOp >= rightHandOp);
				break;
		}

		if (passes)
		{
			if (CoreUtility.doublesEqual(leftHandOp, 0))
			{
				return 1;
			}
			return leftHandOp;
		}
		return 0;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj)
	{
		if (obj instanceof PrerequisiteOperator)
		{
			return value == ((PrerequisiteOperator) obj).value;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return super.hashCode();
	}


	public static PrerequisiteOperator getOperatorByName(final String operatorName) throws PrerequisiteException
	{
		for (int i = 0; i < validTypes.length; ++i)
		{
			if (validTypes[i].equalsIgnoreCase(operatorName) || altValidTypes[i].equals(operatorName))
			{
				switch(i)
				{
					case ComparatorGT:
						return GT;
						
					case ComparatorEQ:
						return EQ;
						
					case ComparatorNEQ:
						return NEQ;
						
					case ComparatorLT:
						return LT;
						
					case ComparatorLTEQ:
						return LTEQ;
						
					default:
						return GTEQ;
				}
			}
		}

		throw new PrerequisiteException(PropertyFactory.getFormattedString("PrerequisiteOperator.error.invalid_operator", operatorName)); //$NON-NLS-1$
	}

}
