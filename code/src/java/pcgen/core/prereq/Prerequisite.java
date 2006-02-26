/*
 * InfoInventory.java Copyright 2003 (C) Frugal <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on September 16, 2002, 3:30 PM
 *
 * Current Ver: $Revision: 1.28 $ Last Editor: $Author: binkley $ Last Edited: $Date: 2005/10/18 20:23:37 $
 *
 */
package pcgen.core.prereq;

import pcgen.core.utils.CoreUtility;
import pcgen.util.PropertyFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author frugal@purplewombat.co.uk
 *
 */
public class Prerequisite implements Cloneable
{
	/** Kind to be used for a clear prerequisite request. */
	public static final String CLEAR_KIND = "clear";

	private String kind;
	private String key = null;
	private String subKey = null;
	private List prerequisites = new ArrayList();
	private PrerequisiteOperator operator = PrerequisiteOperator.GTEQ;
	private String operand = "1"; //$NON-NLS-1$
	/** Indicates that the total of skill ranks, class levels etc should be
	 * added together when checking for a value.
	 */
	private boolean totalValues;
	/** Indicates that the number of qualifying objects should be tallied
	 * when checking for a value.
	 */
	private boolean countMultiples;
	private boolean overrideQualify = false;
	private int levelQualifier = -1; // used for classes only - classlevel at which this prereq is checked

	public Prerequisite()
	{
	    // Empty Constructor
	}

	public Prerequisite(final Prerequisite that)
	{
		this.kind = that.kind;
		this.key = that.key;
		this.subKey = that.subKey;
		this.operator = that.operator;
		this.operand = that.operand;
		this.countMultiples = that.countMultiples;
		this.levelQualifier = that.levelQualifier;
		if (that.prerequisites != null)
		{
			for (Iterator itr = that.prerequisites.iterator(); itr.hasNext();)
			{
				final Prerequisite foo = (Prerequisite) itr.next();
				this.addPrerequisite(new Prerequisite(foo));
			}
		}
	}

	/**
	 * @return Returns the totalValues.
	 */
	public final boolean isTotalValues()
	{
		return totalValues;
	}
	/**
	 * @param totalValues The totalValues to set.
	 */
	public final void setTotalValues(final boolean totalValues)
	{
		this.totalValues = totalValues;
	}
	/**
	 * @param countMultiples
	 *            The countMultiples to set.
	 */
	public void setCountMultiples(final boolean countMultiples)
	{
		this.countMultiples = countMultiples;
	}

	/**
	 * @return Returns the countMultiples.
	 */
	public boolean isCountMultiples()
	{
		return countMultiples;
	}

	public void setKey(final String key)
	{
		this.key = key;
	}

	public String getKey()
	{
		return key;
	}

	/**
	 * @param kind
	 *            The kind to set.
	 */
	public void setKind(final String kind)
	{
		this.kind = kind;
	}

	/**
	 * @return Returns the kind.
	 */
	public String getKind()
	{
		return kind;
	}

	/**
	 * @param operand
	 *            The operand to set.
	 */
	public void setOperand(final String operand)
	{
		this.operand = operand;
	}

	/**
	 * @return Returns the operand.
	 */
	public String getOperand()
	{
		return operand;
	}

	/**
	 * @param operator
	 *            The operator to set.
	 * @throws PrerequisiteException
	 */
	public void setOperator(final String operator) throws PrerequisiteException
	{
		this.operator = PrerequisiteOperator.getOperatorByName(operator);
	}

	/**
	 * @param operator The operator to set.
	 */
	public void setOperator(final PrerequisiteOperator operator)
	{
		this.operator = operator;
	}

	/**
	 * @return Returns the operator.
	 */
	public PrerequisiteOperator getOperator()
	{
		return operator;
	}

	public void setPrerequisites(final List prerequisites)
	{
		this.prerequisites = prerequisites;
	}

	public void addPrerequisite(final Prerequisite prereq)
	{
		prerequisites.add(prereq);
	}

	public List getPrerequisites()
	{
		return prerequisites;
	}

	/**
	 * @param subKey
	 *            The subKey to set.
	 */
	public void setSubKey(final String subKey)
	{
		this.subKey = subKey;
	}

	/**
	 * @return Returns the subKey.
	 */
	public String getSubKey()
	{
		return subKey;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		final StringBuffer buf = new StringBuffer();

		buf.append("<"); //$NON-NLS-1$
		buf.append(PropertyFactory.getString("Prerequisite.prereq_tag")); //$NON-NLS-1$
		buf.append(" "); //$NON-NLS-1$

		if (kind != null)
		{
			buf.append(PropertyFactory.getString("Prerequisite.kind")); //$NON-NLS-1$
			buf.append("=\""); //$NON-NLS-1$
			buf.append(kind);
			buf.append("\" "); //$NON-NLS-1$
		}

		if (countMultiples)
		{
			buf.append(PropertyFactory.getString("Prerequisite.count-multiples")); //$NON-NLS-1$
		}

		if (totalValues)
		{
			buf.append(PropertyFactory.getString("Prerequisite.total-values")); //$NON-NLS-1$
		}

		if (key != null)
		{
			buf.append(PropertyFactory.getString("Prerequisite.key")); //$NON-NLS-1$
			buf.append("=\""); //$NON-NLS-1$
			buf.append(key);
			buf.append("\" "); //$NON-NLS-1$
		}

		if ((subKey != null) && !subKey.equals("")) //$NON-NLS-1$
		{
			buf.append(PropertyFactory.getString("Prerequisite.sub-key")); //$NON-NLS-1$
			buf.append("=\""); //$NON-NLS-1$
			buf.append(subKey);
			buf.append("\" "); //$NON-NLS-1$
		}

		buf.append(PropertyFactory.getString("Prerequisite.operator")); //$NON-NLS-1$
		buf.append("=\""); //$NON-NLS-1$
		buf.append(operator);
		buf.append("\" "); //$NON-NLS-1$

		if (operand != null)
		{
			buf.append(PropertyFactory.getString("Prerequisite.operand")); //$NON-NLS-1$
			buf.append("=\""); //$NON-NLS-1$
			buf.append(operand);
			buf.append("\" "); //$NON-NLS-1$
		}

		buf.append(">\n"); //$NON-NLS-1$

		if (prerequisites.size() > 0)
		{
			for (Iterator iter = prerequisites.iterator(); iter.hasNext();)
			{
				final Prerequisite element = (Prerequisite) iter.next();
				buf.append(element.toString());
			}
		}

		buf.append("</"); //$NON-NLS-1$
		buf.append(PropertyFactory.getString("Prerequisite.prereq_tag")); //$NON-NLS-1$
		buf.append(">\n"); //$NON-NLS-1$

		return buf.toString();
	}

	/**
	 * @return Returns the overrideQualify.
	 */
	public boolean isOverrideQualify()
	{
		return overrideQualify;
	}

	/**
	 * @param overrideQualify
	 *            The overrideQualify to set.
	 */
	public void setOverrideQualify(final boolean overrideQualify)
	{
		this.overrideQualify = overrideQualify;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#clone()
	 */
	public Object clone() throws CloneNotSupportedException
	{
		final Prerequisite copy = (Prerequisite) super.clone();

		if (prerequisites != null)
		{
			copy.prerequisites = new ArrayList();
			for (Iterator iter = prerequisites.iterator(); iter.hasNext();)
			{
				final Prerequisite subreq = (Prerequisite) iter.next();
				copy.prerequisites.add(subreq.clone());
			}
		}

		// All of these aer either immutable, or primitive, neither of which need cloning
		//		private String kind;
		//		private String key=null;
		//		private String subKey=null;
		//		private PrerequisiteOperator operator= new PrerequisiteOperator(PrerequisiteOperator.GTEQ);
		//		private String operand="1"; //$NON-NLS-1$
		//		private boolean countMultiples;
		//		private boolean overrideQualify=false;

		return copy;
	}

	/**
	 * This method will expand the given token in the "value" of this Prerequisite, it will also expand the token in
	 * any sub Prerequisites
	 *
	 * @param token
	 *            The string representing the token to be replaces (i.e. "%CHOICE")
	 * @param tokenValue
	 *            The String representing the new value to be used (i.e. "+2")
	 */
	public void expandToken(final String token, final String tokenValue)
	{
		key = CoreUtility.replaceAll(key, token, tokenValue);
		operand = CoreUtility.replaceAll(operand, token, tokenValue);

		if (prerequisites != null)
		{
			for (Iterator iter = prerequisites.iterator(); iter.hasNext();)
			{
				final Prerequisite subreq = (Prerequisite) iter.next();
				subreq.expandToken(token, tokenValue);
			}
		}
	}

	/**
	 * Retrieve the description of the prerequisite. This can either be
	 * in long form 'skill TUMBLE gteq 5' or in short form 'TUMBLE'.
	 *
	 * @param shortForm True if the abbreviated form should be used.
	 * @return The description of the prerequisite
	 */
	public String getDescription(final boolean shortForm)
	{
		final StringBuffer buf = new StringBuffer();

		if (levelQualifier > 0 && !shortForm)
		{
			buf.append("at level ");
			buf.append(levelQualifier+":");
			buf.append(' '); //$NON-NLS-1$

		}

		if (kind != null && !shortForm)
		{
			buf.append(kind);
			buf.append(' '); //$NON-NLS-1$
		}

		if (key != null)
		{
			buf.append(key);
			if (!shortForm)
			{
				buf.append(' '); //$NON-NLS-1$
			}
		}

		if ((subKey != null) && !subKey.equals("")) //$NON-NLS-1$
		{
			buf.append('('); //$NON-NLS-1$
			buf.append(subKey);
			buf.append(')'); //$NON-NLS-1$
			if (!shortForm)
			{
				buf.append(' '); //$NON-NLS-1$
			}
		}

		if (!shortForm)
		{
			buf.append(operator);
			buf.append(' '); //$NON-NLS-1$
		}

		if (operand != null && !shortForm)
		{
			buf.append(operand);
		}

		if (prerequisites.size() > 0 && !shortForm)
		{
			buf.append(" ("); //$NON-NLS-1$
			for (Iterator iter = prerequisites.iterator(); iter.hasNext();)
			{
				final Prerequisite element = (Prerequisite) iter.next();
				buf.append(element.getDescription(shortForm));
			}
			buf.append(')'); //$NON-NLS-1$
		}

		return buf.toString();
	}

	public int getLevelQualifier()
	{
		return levelQualifier;
	}

	public void setLevelQualifier(final int qualifier)
	{
		levelQualifier = qualifier;
	}

}
