/*
 * Copyright 2014-15 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.content;

import java.net.URI;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.Loadable;
import pcgen.rules.context.LoadContext;
import pcgen.rules.types.FormatManager;
import pcgen.util.StringPClassUtil;
import pcgen.util.enumeration.View;
import pcgen.util.enumeration.Visibility;

/**
 * A ContentDefinition manages dynamic content for PCGen
 * 
 * @param <T>
 *            The type of object on which this ContentDefinition is legal to use
 *            (in LST files)
 * @param <F>
 *            The type of object used as content in the items managed by this
 *            ContentDefinition
 */
public abstract class ContentDefinition<T extends CDOMObject, F> implements
		Loadable
{

	/**
	 * The unique name of this ContentDefinition.
	 */
	private String name;

	/**
	 * The display name of this ContentDefinition, which matches the keyword
	 * used in an LST file
	 */
	private String displayName;

	/**
	 * The source URI where this ContentDefinition was originally defined
	 */
	private URI sourceURI;

	/**
	 * The usable location for this ContentDefinition
	 */
	private Class<T> usableLocation; //e.g. Deity.class

	/**
	 * The FormatManager which manages the underlying objects used as content in
	 * the items managed by this ContentDefinition
	 */
	private FormatManager<F> formatManager; //e.g. for STRING or ALIGNMENT

	/**
	 * Sets the visibility for this ContentDefinition.
	 * 
	 * The value if this is "null" should be interpreted as "HIDDEN"
	 */
	private Visibility visibility;

	/**
	 * Sets whether this ContentDefinition is selectable in a CHOOSE or other
	 * item that allows selection via primitives
	 * 
	 * The value if this is "null" should be interpreted as "false"
	 */
	private Boolean selectable;

	/**
	 * Sets whether this ContentDefinition is required for the objects as
	 * defined by the usableLocation.
	 * 
	 * The value if this is "null" should be interpreted as "false"
	 */
	private Boolean required;

	/**
	 * A String representing an explanation for
	 */
	private String explanation;

	/**
	 * @see pcgen.cdom.base.Loadable#setName(java.lang.String)
	 */
	@Override
	public void setName(String name)
	{
		if (name == null)
		{
			throw new IllegalArgumentException("Name cannot be null");
		}
		this.name = name;
	}

	/**
	 * @see pcgen.cdom.base.Identified#getKeyName()
	 */
	@Override
	public String getKeyName()
	{
		return name;
	}

	/**
	 * Sets the Display name for this ContentDefinition
	 * 
	 * @param name
	 *            The DisplayName for this ContentDefinition
	 * @throws IllegalArgumentException
	 *             if the given name is null
	 */
	public void setDisplayName(String name)
	{
		if (name == null)
		{
			throw new IllegalArgumentException("Display Name cannot be null");
		}
		displayName = name;
	}

	/**
	 * @see pcgen.cdom.base.Identified#getDisplayName()
	 */
	@Override
	public String getDisplayName()
	{
		return displayName;
	}

	/**
	 * @see pcgen.cdom.base.Loadable#setSourceURI(java.net.URI)
	 */
	@Override
	public void setSourceURI(URI source)
	{
		sourceURI = source;
	}

	/**
	 * @see pcgen.cdom.base.Loadable#getSourceURI()
	 */
	@Override
	public URI getSourceURI()
	{
		return sourceURI;
	}

	/**
	 * Sets the "usable location" (related to what LST files this
	 * ContentDefinition will be usable within)
	 * 
	 * @param cl
	 *            The Class indicating the "usable location" of this
	 *            ContentDefinition
	 */
	public void setUsableLocation(Class<? extends Loadable> cl)
	{
		if (cl == null)
		{
			throw new IllegalArgumentException("Usable Location cannot be null");
		}
		this.usableLocation = (Class<T>) cl;
	}

	/**
	 * Returns the "usable location" of this ContentDefinition (related to what
	 * LST files this ContentDefinition will be usable within)
	 * 
	 * @return The "usable location" of this ContentDefinition
	 */
	public Class<T> getUsableLocation()
	{
		return usableLocation;
	}

	/**
	 * Sets the FormatManager for this ContentDefinition. This is used to
	 * convert to/from the String format used to serialize content for LST
	 * files.
	 * 
	 * @param fmtMgr
	 *            The non-null FormatManager for this ContentDefinition
	 * @return The previous FormatManager for this ContentDefinition.
	 * @throws IllegalArgumentException
	 *             if the given FormatManager is null
	 */
	public FormatManager<?> setFormatManager(FormatManager<F> fmtMgr)
	{
		if (fmtMgr == null)
		{
			throw new IllegalArgumentException("Format Manager cannot be null");
		}
		FormatManager<?> returnValue = formatManager;
		this.formatManager = fmtMgr;
		return returnValue;
	}

	/**
	 * Returns the FormatManager for this ContentDefinition. This is used to
	 * convert to/from the String format used to serialize content for LST
	 * files.
	 * 
	 * @return The FormatManager for this ContentDefinition
	 */
	public FormatManager<F> getFormatManager()
	{
		return formatManager;
	}

	/**
	 * Sets the visibility for this ContentDefinition. The default visibility is
	 * HIDDEN.
	 * 
	 * @param vis
	 *            The visibility for this ContentDefinition
	 */
	public void setVisibility(Visibility vis)
	{
		visibility = vis;
	}

	/**
	 * Returns any specifically defined visibility for this ContentDefinition.
	 * 
	 * Note: This may be null, if the default visibility for ContentDefinition
	 * (HIDDEN) is used and hasn't been explicitly set by the setVisibility
	 * method.
	 * 
	 * @return Any specifically defined visibility for this ContentDefinition
	 */
	public Visibility getVisibility()
	{
		return visibility;
	}

	/**
	 * Sets whether this ContentDefintion is Selectable.
	 * 
	 * @param set
	 *            A boolean value indicating whether this ContentDefinition is
	 *            selectable
	 */
	public void setSelectable(boolean set)
	{
		selectable = set;
	}

	/**
	 * Returns any specifically defined Selectable setting for this
	 * ContentDefinition.
	 * 
	 * Note: This may be null, if the default Selectable setting for
	 * ContentDefinition is used and hasn't been explicitly set by the
	 * setSelectable method.
	 * 
	 * @return Any specifically defined Selectable setting for this
	 *         ContentDefinition
	 */
	public Boolean getSelectable()
	{
		return selectable;
	}

	/**
	 * Sets whether this ContentDefintion is Required.
	 * 
	 * Note: If the "Usable Location" of this ContentDefinition is "GLOBAL"
	 * (meaning CDOMObject.class), then this method will throw an
	 * UnsupportedOperationException
	 * 
	 * @param set
	 *            A boolean value indicating whether this ContentDefinition is
	 *            Required
	 * @throws UnsupportedOperationException
	 *             if the set parameter is true and the "Usable Location" of
	 *             this ContentDefinition is CDOMObject.class
	 */
	public void setRequired(boolean set)
	{
		if (set && CDOMObject.class.equals(usableLocation))
		{
			throw new UnsupportedOperationException(
				"Global ContentDefinition cannot be required");
		}
		required = set;
	}

	/**
	 * Returns any specifically defined Required setting for this
	 * ContentDefinition.
	 * 
	 * Note: This may be null, if the default Required setting for
	 * ContentDefinition is used and hasn't been explicitly set by the
	 * setRequired method.
	 * 
	 * @return Any specifically defined Required setting for this
	 *         ContentDefinition
	 */
	public Boolean getRequired()
	{
		return required;
	}

	/**
	 * Sets the Explanation for this ContentDefinition. This is intended to be a
	 * user understood String; it is not processed by PCGen.
	 * 
	 * @param value
	 *            The Explanation for this ContentDefinition
	 */
	public void setExplanation(String value)
	{
		if (value == null)
		{
			throw new IllegalArgumentException("Explanation may not be null");
		}
		explanation = value;
	}

	/**
	 * Returns the non-null Explanation for this ContentDefinition.
	 * 
	 * @return The non-null Explanation for this ContentDefinition
	 */
	public String getExplanation()
	{
		return explanation;
	}

	/**
	 * @see pcgen.cdom.base.Loadable#getLSTformat()
	 */
	@Override
	public String getLSTformat()
	{
		String loc;
		if (CDOMObject.class.equals(usableLocation))
		{
			loc = "GLOBAL";
		}
		else
		{
			loc = StringPClassUtil.getStringFor(usableLocation);
		}
		return loc + Constants.PIPE + name;
	}

	/**
	 * @see pcgen.cdom.base.Loadable#isInternal()
	 */
	@Override
	@SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
	public boolean isInternal()
	{
		return false;
	}

	/**
	 * @see pcgen.cdom.base.Loadable#isType(java.lang.String)
	 */
	@Override
	@SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
	public boolean isType(String type)
	{
		return false;
	}

	/**
	 * Activates this ContentDefinition by performing the necessary steps to
	 * link it to other subsystems.
	 * 
	 * @param context
	 *            The LoadContext to initialize with tokens, if required
	 */
	public void activate(LoadContext context)
	{
		activateKey();
		Visibility vis = (visibility == null) ? Visibility.HIDDEN : visibility;
		if (vis.isVisibleTo(View.VISIBLE_EXPORT))
		{
			activateOutput();
		}
		activateTokens(context);
	}

	/**
	 * Activates any key owned by this ContentDefinition. This is intended only for
	 * internal use by ContentDefinition, as a portion of the activate() method.
	 * 
	 * This should not be called from an external source
	 */
	protected abstract void activateKey();

	/**
	 * Activates this ContentDefinition for output. This is intended only for
	 * internal use by ContentDefinition, as a portion of the activate() method.
	 * 
	 * This should not be called from an external source
	 */
	protected abstract void activateOutput();

	/**
	 * Activates the tokens supporting this ContentDefinition. This is intended
	 * only for internal use by ContentDefinition, as a portion of the
	 * activate() method.
	 * 
	 * This should not be called from an external source
	 */
	protected abstract void activateTokens(LoadContext context);
}
