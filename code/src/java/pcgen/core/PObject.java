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
 */
package pcgen.core;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.QualifyingObject;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.HiddenTypeFacet;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.core.utils.KeyedListContainer;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.system.PCGenSettings;

/**
 * {@code PObject}<br>
 * This is the base class for several objects in the PCGen database.
 */
public class PObject extends CDOMObject
		implements Cloneable, Serializable, Comparable<Object>, KeyedListContainer, QualifyingObject
{

	private HiddenTypeFacet hiddenTypeFacet = FacetLibrary.getFacet(HiddenTypeFacet.class);

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
	 */
	@Override
	public int compareTo(final Object obj)
	{
		if (obj != null)
		{
			//this should throw a ClassCastException for non-PObjects, like the Comparable interface calls for
			return this.getKeyName().compareToIgnoreCase(((PObject) obj).getKeyName());
		}
		return 1;
	}

	@Override
	public boolean equals(final Object obj)
	{
		return obj instanceof PObject && getKeyName().equalsIgnoreCase(((PObject) obj).getKeyName());
	}

	//Temporarily commented out since unit tests are badly behaved, see COD#E-1895
	//	@Override
	//	public int hashCode()
	//	{
	//		return getKeyName().hashCode();
	//	}

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
				DataSetID id = Globals.getContext().getDataSetID();
				if (hiddenTypeFacet.contains(id, myClass, it.next()))
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
	 */
	@Override
	public boolean isType(final String aType)
	{
		final String myType;

		if (aType.isEmpty())
		{
			return false;
		}
		else if (aType.charAt(0) == '!')
		{
			myType = aType.substring(1).toUpperCase();
		}
		else if (aType.startsWith("TYPE=") || aType.startsWith("TYPE.")) //$NON-NLS-1$ //$NON-NLS-2$
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
		if (PCGenSettings.OPTIONS_CONTEXT.getBoolean(PCGenSettings.OPTION_SHOW_OUTPUT_NAME_FOR_OTHER_ITEMS, false))
		{
			return getOutputName();
		}

		return getDisplayName();
	}

	/**
	 * @return true if the name of this item is Product Identity (i.e owned by the publisher)
	 */
	public boolean isNamePI()
	{
		return getSafe(ObjectKey.NAME_PI);
	}

	/**
	 * Get the PCC text with the saved name
	 * @return the PCC text with the saved name
	 */
	public String getPCCText()
	{
		StringJoiner txt = new StringJoiner("\t");
		txt.add(getDisplayName());
		Globals.getContext().unparse(this).forEach(txt::add);
		txt.add(PrerequisiteWriter.prereqsToString(this));
		return txt.toString();
	}

	public String getSource()
	{
		return SourceFormat.getFormattedString(this, Globals.getSourceDisplay(), true);
	}

	public String getSourceForNodeDisplay()
	{
		return SourceFormat.getFormattedString(this, SourceFormat.LONG, false);
	}

}
