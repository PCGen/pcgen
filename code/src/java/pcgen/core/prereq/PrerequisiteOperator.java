/*
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
 */
package pcgen.core.prereq;

import pcgen.core.utils.CoreUtility;
import pcgen.system.LanguageBundle;

public enum PrerequisiteOperator
{
    GTEQ
            {
                @Override
                public String getFormulaSyntax()
                {
                    return ">=";
                }

                @Override
                public PrerequisiteOperator invert()
                {
                    return LT;
                }

                @Override
                public boolean booleanCompare(float a, float b)
                {
                    return a >= b;
                }
            },
    GT
            {
                @Override
                public String getFormulaSyntax()
                {
                    return ">";
                }

                @Override
                public PrerequisiteOperator invert()
                {
                    return LTEQ;
                }

                @Override
                public boolean booleanCompare(float a, float b)
                {
                    return a > b;
                }
            },
    EQ
            {
                @Override
                public String getFormulaSyntax()
                {
                    return "=";
                }

                @Override
                public PrerequisiteOperator invert()
                {
                    return NEQ;
                }

                @Override
                public boolean booleanCompare(float a, float b)
                {
                    return CoreUtility.doublesEqual(a, b);
                }
            },
    NEQ
            {
                @Override
                public String getFormulaSyntax()
                {
                    return "!=";
                }

                @Override
                public PrerequisiteOperator invert()
                {
                    return EQ;
                }

                @Override
                public boolean booleanCompare(float a, float b)
                {
                    return !CoreUtility.doublesEqual(a, b);
                }
            },
    LT
            {
                @Override
                public String getFormulaSyntax()
                {
                    return "<";
                }

                @Override
                public PrerequisiteOperator invert()
                {
                    return GTEQ;
                }

                @Override
                public boolean booleanCompare(float a, float b)
                {
                    return a < b;
                }
            },
    LTEQ
            {
                @Override
                public String getFormulaSyntax()
                {
                    return "<=";
                }

                @Override
                public PrerequisiteOperator invert()
                {
                    return GT;
                }

                @Override
                public boolean booleanCompare(float a, float b)
                {
                    return a <= b;
                }
            };

    public abstract String getFormulaSyntax();

    public abstract PrerequisiteOperator invert();

    public String toDisplayString()
    {
        return LanguageBundle.getString("PrerequisiteOperator.display." + toString().toLowerCase());
    }

    public int compare(final int leftHandOp, final int rightHandOp)
    {
        return (int) compare((float) leftHandOp, (float) rightHandOp);
    }

    public float compare(final float leftHandOp, final float rightHandOp)
    {
        boolean passes = booleanCompare(leftHandOp, rightHandOp);
        if (passes)
        {
            if (leftHandOp < 0.0d || CoreUtility.doublesEqual(leftHandOp, 0))
            {
                return 1;
            }
            return leftHandOp;
        }
        return 0;
    }

    public abstract boolean booleanCompare(float leftHandOp, float rightHandOp);

    public static PrerequisiteOperator getOperatorByName(final String operatorName) throws PrerequisiteException
    {
        try
        {
            return valueOf(operatorName.toUpperCase());
        } catch (IllegalArgumentException e)
        {
            /*
             * TODO Should we deprecate this behavior?
             */
            for (PrerequisiteOperator po : values())
            {
                if (po.getFormulaSyntax().equals(operatorName))
                {
                    return po;
                }
            }
        }
        throw new PrerequisiteException(
                LanguageBundle.getFormattedString(
                        "PrerequisiteOperator.error.invalid_operator", operatorName)); //$NON-NLS-1$
    }

}
