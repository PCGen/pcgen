/*
 * RuleCheck.java
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
 * Created on Novmeber 06, 2003, 11:59 PM PST
 *
 * Current Ver: $Revision: 1.7 $
 * Last Editor: $Author: karianna $
 * Last Edited: $Date: 2005/09/12 16:02:31 $
 *
 */
package pcgen.core;


/**
 * <code>RuleCheck</code> describes checks that can be turned on or off
 * in the GUI by the users
 *
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * @version $Revision: 1.7 $
 */
public final class RuleCheck
{
	private String desc = "";
	private String excludeKey = "";
	private String key = "";
	private String name = "";
	private String parm = "";
	private String var = "";
	private boolean status = false;

	/**
	 * Default constructor for RuleCheck
	 **/
	public RuleCheck()
	{
	    // Empty Constructor
	}

	/**
	 * @param aString Used to set on/off status
	 **/
	public void setDefault(final String aString)
	{
		status = aString.startsWith("Y") || aString.startsWith("y");
	}

	public boolean getDefault()
	{
		return status;
	}

	/**
	 * @param aString set desc to
	 **/
	public void setDesc(final String aString)
	{
		desc = aString;
	}

	public String getDesc()
	{
		return desc;
	}

	/**
	 * @param aString set exclude to
	 **/
	public void setExclude(final String aString)
	{
		excludeKey = aString;
	}

	public boolean isExclude()
	{
		return (excludeKey.length() > 0);
	}

	public String getExcludeKey()
	{
		return excludeKey;
	}

	/**
	 * Returns the unique key for this Rule
	 * @return key
	 */
	public String getKey()
	{
		return key;
	}

	/**
	 * Sets the Name (and key if not already set)
	 * @param aName set name to
	 */
	public void setName(final String aName)
	{
		name = aName;

		if (key.length() <= 0)
		{
			key = aName;
		}
	}

	public String getName()
	{
		return name;
	}

	/**
	 * @param aString set parm, key and var to
	 **/
	public void setParameter(final String aString)
	{
		parm = aString;
		key = aString;

		if (var.length() <= 0)
		{
			var = aString;
		}
	}

	public String getParameter()
	{
		return parm;
	}

	/**
	 * @param aString set key and var to
	 **/
	public void setVariable(final String aString)
	{
		var = aString;
		key = aString;
	}

	public String getVariable()
	{
		return var;
	}
}
