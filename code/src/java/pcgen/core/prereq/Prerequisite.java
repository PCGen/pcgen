/*
 * Prerequisite.java Copyright 2003 (C) Frugal <frugal@purplewombat.co.uk>
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
 */
package pcgen.core.prereq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import pcgen.cdom.base.Constants;
import pcgen.system.LanguageBundle;

/**
 * The Class {@code Prerequisite} is the storage format for all
 * prerequisites. It is populated by a parser, written out by a writer
 * and tested by a Tester class. Each kind of prerequisite will have 
 * one of each of these three classes that is responsible for managing
 * its lifecycle. 
 */
public class Prerequisite implements Cloneable
{

	private static final String PERCENT_CHOICE_PATTERN = Pattern.quote(Constants.LST_PERCENT_CHOICE);
	/** Kind to be used for a clear prerequisite request. */
	public static final String APPLY_KIND = "APPLY";

	private String kind;
	private String key = null;
	private String subKey = null;
	private List<Prerequisite> prerequisites;
	private PrerequisiteOperator operator = PrerequisiteOperator.GTEQ;
	private String operand = "1"; //$NON-NLS-1$
	/** Indicates that the total of skill ranks, class levels etc should be
	 * added together when checking for a value.
	 */
	private boolean totalValues;

	/** Is a character required to test this prereq against?. */
	private boolean characterRequired = true;

	/** Indicates that the number of qualifying objects should be tallied when checking for a value. */
	private boolean countMultiples;
	private boolean overrideQualify = false;

	/** Used for abilities only - the category to restrict matches to. */
	private String categoryName;

	/**
	 * @return Returns the totalValues.
	 */
	public final boolean isTotalValues()
	{
		return totalValues;
	}

	/**
	 * Sets the totalValues attribute.
	 * @param val The value to set TotalValues to.
	 */
	public final void setTotalValues(final boolean val)
	{
		this.totalValues = val;
	}

	/**
	 * Sets the countMultiples attribute.
	 * @param val
	 *            The value to set countMultiples to.
	 */
	public void setCountMultiples(final boolean val)
	{
		this.countMultiples = val;
	}

	/**
	 * @return Returns the countMultiples.
	 */
	public boolean isCountMultiples()
	{
		return countMultiples;
	}

	/**
	 * Set the key.
	 * @param val the Key to set.
	 */
	public void setKey(final String val)
	{
		this.key = val;
	}

	/**
	 * Get the key.
	 * @return the prerequisite's key.
	 */
	public String getKey()
	{
		return key;
	}

	/**
	 * Set the kind attribute.
	 * @param val
	 *            The value to set kind to.
	 */
	public void setKind(final String val)
	{
		this.kind = val;
	}

	/**
	 * @return Returns the kind.
	 */
	public String getKind()
	{
		return kind;
	}

	/**
	 * Set the operand attribute.
	 * @param val
	 *            The value to set the operand to.
	 */
	public void setOperand(final String val)
	{
		this.operand = val;
	}

	/**
	 * @return Returns the operand.
	 */
	public String getOperand()
	{
		return operand;
	}

	/**
	 * Sets an operator attribute from the name of the operator.
	 * @param operator
	 *            The name of the operator to set in the object.
	 * @throws PrerequisiteException throws an exception if it can't locate the operator.
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

	public void addPrerequisite(final Prerequisite prereq)
	{
		if (prerequisites == null)
		{
			prerequisites = new ArrayList<>();
		}
		prerequisites.add(prereq);
	}

	public List<Prerequisite> getPrerequisites()
	{
		if (prerequisites == null)
		{
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(prerequisites);
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

	@Override
	public String toString()
	{
		final StringBuilder buf = new StringBuilder(250);

		buf.append("<"); //$NON-NLS-1$
		buf.append(LanguageBundle.getString("Prerequisite.prereq_tag")); //$NON-NLS-1$
		buf.append(" "); //$NON-NLS-1$

		if (kind != null)
		{
			buf.append(LanguageBundle.getString("Prerequisite.kind")); //$NON-NLS-1$
			buf.append("=\""); //$NON-NLS-1$
			buf.append(kind);
			buf.append("\" "); //$NON-NLS-1$
		}

		if (countMultiples)
		{
			buf.append(LanguageBundle.getString("Prerequisite.count-multiples")); //$NON-NLS-1$
		}

		if (totalValues)
		{
			buf.append(LanguageBundle.getString("Prerequisite.total-values")); //$NON-NLS-1$
		}

		if (categoryName != null)
		{
			buf.append(LanguageBundle.getString("Prerequisite.category")); //$NON-NLS-1$
			buf.append("=\""); //$NON-NLS-1$
			buf.append(categoryName);
			buf.append("\" "); //$NON-NLS-1$
		}

		if (key != null)
		{
			buf.append(LanguageBundle.getString("Prerequisite.key")); //$NON-NLS-1$
			buf.append("=\""); //$NON-NLS-1$
			buf.append(key);
			buf.append("\" "); //$NON-NLS-1$
		}

		if ((subKey != null) && !subKey.equals("")) //$NON-NLS-1$
		{
			buf.append(LanguageBundle.getString("Prerequisite.sub-key")); //$NON-NLS-1$
			buf.append("=\""); //$NON-NLS-1$
			buf.append(subKey);
			buf.append("\" "); //$NON-NLS-1$
		}

		buf.append(LanguageBundle.getString("Prerequisite.operator")); //$NON-NLS-1$
		buf.append("=\""); //$NON-NLS-1$
		buf.append(operator);
		buf.append("\" "); //$NON-NLS-1$

		if (operand != null)
		{
			buf.append(LanguageBundle.getString("Prerequisite.operand")); //$NON-NLS-1$
			buf.append("=\""); //$NON-NLS-1$
			buf.append(operand);
			buf.append("\" "); //$NON-NLS-1$
		}

		if (isOverrideQualify())
		{
			buf.append(LanguageBundle.getString("Prerequisite.override-qualify")); //$NON-NLS-1$
		}

		buf.append(">\n"); //$NON-NLS-1$

		if (prerequisites != null)
		{
			for (Prerequisite prereq : prerequisites)
			{
				buf.append(prereq);
			}
		}

		buf.append("</"); //$NON-NLS-1$
		buf.append(LanguageBundle.getString("Prerequisite.prereq_tag")); //$NON-NLS-1$
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
	 * Setter for the overrideQualify field.
	 * @param override
	 *            Whether to override the qualifications.
	 */
	public void setOverrideQualify(final boolean override)
	{
		this.overrideQualify = override;
	}

	@Override
	public Prerequisite clone() throws CloneNotSupportedException
	{
		final Prerequisite copy = (Prerequisite) super.clone();

		if (prerequisites != null)
		{
			copy.prerequisites = new ArrayList<>();
			for (Prerequisite subreq : prerequisites)
			{
				copy.prerequisites.add(subreq.clone());
			}
		}

		return copy;
	}

	/**
	 * Retrieve the description of the prerequisite. This can either be
	 * in long form 'skill TUMBLE gteq 5' or in short form 'TUMBLE'.
	 * 
	 * @param shortForm True if the abbreviated form should be used.
	 * 
	 * @return The description of the prerequisite
	 */
	public String getDescription(final boolean shortForm)
	{
		final StringBuilder buf = new StringBuilder(250);

		if (categoryName != null && !shortForm)
		{
			buf.append("of category ");
			buf.append(categoryName);
			buf.append(":");
			buf.append(' ');

		}

		if (kind != null && !shortForm)
		{
			buf.append(kind);
			buf.append(' ');
		}

		if (key != null)
		{
			buf.append(key);
			if (!shortForm)
			{
				buf.append(' ');
			}
		}

		if ((subKey != null) && !subKey.equals("")) //$NON-NLS-1$
		{
			buf.append('(');
			buf.append(subKey);
			buf.append(')');
			if (!shortForm)
			{
				buf.append(' ');
			}
		}

		if (!shortForm)
		{
			buf.append(operator);
			buf.append(' ');
		}

		if (operand != null && !shortForm)
		{
			buf.append(operand);
		}

		if (prerequisites != null && !prerequisites.isEmpty() && !shortForm)
		{
			buf.append(" ("); //$NON-NLS-1$
			for (Prerequisite subreq : prerequisites)
			{
				buf.append(subreq.getDescription(false));
			}
			buf.append(')');
		}

		return buf.toString();
	}

	/**
	 * @return the categoryName
	 */
	public String getCategoryName()
	{
		return categoryName;
	}

	/**
	 * @param categoryName the categoryName to set
	 */
	public void setCategoryName(String categoryName)
	{
		this.categoryName = categoryName;
	}

	@Override
	public int hashCode()
	{
		return (kind == null ? -1 : kind.hashCode()) ^ (key == null ? 0 : key.hashCode());
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if (!(o instanceof Prerequisite other))
		{
			return false;
		}
		if (kind == null)
		{
			if (other.kind != null)
			{
				return false;
			}
		}
		if (key == null)
		{
			if (other.key != null)
			{
				return false;
			}
		}
		if (subKey == null)
		{
			if (other.subKey != null)
			{
				return false;
			}
		}
		if (categoryName == null)
		{
			if (other.categoryName != null)
			{
				return false;
			}
		}
		boolean iHave = prerequisites != null && !prerequisites.isEmpty();
		boolean otherHas = other.prerequisites != null && !other.prerequisites.isEmpty();
		if (iHave)
		{
			if (!otherHas)
			{
				return false;
			}
			List<Prerequisite> otherPRL = other.prerequisites;
			if (otherPRL.size() != prerequisites.size())
			{
				return false;
			}
			ArrayList<Prerequisite> removed = new ArrayList<>(prerequisites);
			removed.removeAll(otherPRL);
			if (!removed.isEmpty())
			{
				return false;
			}
		}
		else if (otherHas)
		{
			return false;
		}
		return countMultiples == other.countMultiples && overrideQualify == other.overrideQualify
			&& operator == other.operator && (kind == null || kind.equals(other.kind))
			&& (key == null || key.equals(other.key)) && (subKey == null || subKey.equals(other.subKey))
			&& operand.equals(other.operand) && (categoryName == null || categoryName.equals(other.categoryName));
	}

	/**
	 * Checks if a character is required to test this prerequisite.
	 * 
	 * @return true, if a character required
	 */
	public boolean isCharacterRequired()
	{
		return characterRequired;
	}

	/**
	 * Sets whether a character is required.
	 * 
	 * @param characterRequired is a character required
	 */
	public void setCharacterRequired(boolean characterRequired)
	{
		this.characterRequired = characterRequired;
	}

	private boolean nativeCheckMult = false;

	public void setOriginalCheckmult(boolean b)
	{
		nativeCheckMult = b;
	}

	public boolean isOriginalCheckMult()
	{
		return nativeCheckMult;
	}

	public Prerequisite specify(String assoc) throws CloneNotSupportedException
	{
		final Prerequisite copy = (Prerequisite) super.clone();
		//PREMULT has no key or operand
		if (copy.key != null)
		{
			copy.key = copy.key.replaceAll(PERCENT_CHOICE_PATTERN, assoc);
		}
		if (copy.operand != null)
		{
			copy.operand = copy.operand.replaceAll(PERCENT_CHOICE_PATTERN, assoc);
		}

		if (prerequisites != null)
		{
			copy.prerequisites = new ArrayList<>();
			for (Prerequisite subreq : prerequisites)
			{
				copy.prerequisites.add(subreq.specify(assoc));
			}
		}
		return copy;
	}

	public int getPrerequisiteCount()
	{
		return prerequisites == null ? 0 : prerequisites.size();
	}

	public void removePrerequisite(Prerequisite p)
	{
		if (prerequisites != null)
		{
			prerequisites.remove(p);
		}
	}
}
