/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on April 21, 2001, 2:15 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.QualifyingObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.core.utils.KeyedListContainer;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;

/**
 * <code>PObject</code><br>
 * This is the base class for several objects in the PCGen database.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
/**
 * @author Joe.Frazier
 *
 */
public class PObject extends CDOMObject implements Cloneable, Serializable, Comparable<Object>,
	KeyedListContainer, QualifyingObject
{
	/** Standard serialVersionUID for Serializable objects */
	private static final long serialVersionUID = 1;

	private final Class<?> myClass = getClass();
	
	/* ************
	 * Methods
	 * ************/

	/**
	 * if a class implements the Cloneable interface then it should have a
	 * public" 'clone ()' method It should be declared to throw
	 * CloneNotSupportedException', but subclasses do not need the "throws"
	 * declaration unless their 'clone ()' method will throw the exception
	 * Thus subclasses can decide to not support 'Cloneable' by implementing
	 * the 'clone ()' method to throw 'CloneNotSupportedException'
	 * If this rule were ignored and the parent did not have the "throws"
	 * declaration then subclasses that should not be cloned would be forced
	 * to implement a trivial 'clone ()' to satisfy inheritance
	 * final" classes implementing 'Cloneable' should not be declared to
	 * throw 'CloneNotSupportedException" because their implementation of
	 * clone ()' should be a fully functional method that will not
	 * throw the exception.
	 * @return cloned object
	 * @throws CloneNotSupportedException
	 */
	@Override
	public PObject clone() throws CloneNotSupportedException
	{
		return (PObject) super.clone();
	}

	/**
	 * Compares the keys of the object.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(final Object obj)
	{
		if (obj != null)
		{
			//this should throw a ClassCastException for non-PObjects, like the Comparable interface calls for
			return this.getKeyName().compareToIgnoreCase(((PObject) obj).getKeyName());
		}
		return 1;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals( final Object obj )
	{
		if ( obj == null )
		{
			return false;
		}
		final String thisKey;
		final String otherKey;
		if ( obj instanceof PObject )
		{
			thisKey = getKeyName();
			otherKey = ((PObject)obj).getKeyName();
		}
		else
		{
			thisKey = toString();
			otherKey = obj.toString();
		}
		return thisKey.equalsIgnoreCase( otherKey );
	}

	/**
	 * Set the name (sets keyname also)
	 * @param aString
	 */
	@Override
	public void setName(final String aString)
	{
		if (!aString.endsWith(".MOD"))
		{
			super.setName(aString);
			put(StringKey.KEY_NAME, aString);
		}
	}

	///////////////////////////////////////////////////////////////////////
	// Accessor(s) and Mutator(s)
	///////////////////////////////////////////////////////////////////////

	public final String getOutputName()
	{
		return OutputNameFormatting.getOutputName(this);
	}

	/**
	 * Get the type of PObject
	 * 
	 * @return the type of PObject
	 */
	public String getType()
	{
		return StringUtil.join(getTrueTypeList(false), ".");
	}

	public final List<Type> getTrueTypeList(final boolean visibleOnly)
	{
		final List<Type> ret = getSafeListFor(ListKey.TYPE);
		if (visibleOnly)
		{
			for (Iterator<Type> it = ret.iterator(); it.hasNext();)
			{
				if (SettingsHandler.getGame().isTypeHidden(myClass, it.next().toString()))
				{
					it.remove();
				}
			}
		}
		return Collections.unmodifiableList(ret);
	}
	
	/**
	 * If aType begins with an &#34; (Exclamation Mark) the &#34; will be
	 * removed before checking the type.
	 *
	 * @param aType
	 * @return Whether the item is of this type
	 * 
	 * Note:  This method is overridden in Equipment.java
	 */
	@Override
	public boolean isType(final String aType)
	{
		final String myType;

		if (aType.length() == 0)
		{
			return false;
		}
		else if (aType.charAt(0) == '!')
		{
			myType = aType.substring(1).toUpperCase();
		}
		else if (aType.startsWith("TYPE=") || aType.startsWith("TYPE."))	//$NON-NLS-1$ //$NON-NLS-2$
		{
			myType = aType.substring(5).toUpperCase();
		}
		else
		{
			myType = aType.toUpperCase();
		}
		
		//
		// Must match all listed types in order to qualify
		//
		StringTokenizer tok = new StringTokenizer(myType, ".");
		while (tok.hasMoreTokens())
		{
			if (!containsInList(ListKey.TYPE, Type.getConstant(tok.nextToken())))
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString()
	{
		return getDisplayName();
	}

	/**
	 * Get the PCC text with the saved name
	 * @return the PCC text with the saved name
	 */
	public String getPCCText()
	{
		final StringBuffer txt = new StringBuffer(200);
		txt.append(getDisplayName());
		txt.append("\t");
		txt.append(StringUtil.joinToStringBuffer(Globals.getContext().unparse(
				this), "\t"));
		txt.append("\t");
		txt.append(PrerequisiteWriter.prereqsToString(this));
		return txt.toString();
	}

	public int numberInList(PlayerCharacter pc, final String aType)
	{
		return 0;
	}

	public String getVariableSource()
	{
		return "POBJECT|" + this.getKeyName();
	}
}
