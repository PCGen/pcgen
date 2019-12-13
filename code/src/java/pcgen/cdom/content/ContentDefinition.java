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

import java.util.Objects;

import pcgen.base.util.FormatManager;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.rules.context.LoadContext;
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
public abstract class ContentDefinition<T extends CDOMObject, F> extends UserContent
{

	/**
	 * The display name of this ContentDefinition, which matches the keyword
	 * used in an LST file
	 */
	private String displayName;

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
	 * The value if this is "null" should be interpreted as "HIDDEN", but is
	 * necessary to have as a separate value for establishing whether it was set
	 * by data.
	 */
	private Visibility visibility;

	/**
	 * Sets whether this ContentDefinition is selectable in a CHOOSE or other
	 * item that allows selection via primitives
	 * 
	 * The value if this is "null" should be interpreted as "false", but is
	 * necessary to have as a separate value for establishing whether it was set
	 * by data.
	 */
	private Boolean selectable;

	/**
	 * Sets whether this ContentDefinition is required for the objects as
	 * defined by the usableLocation.
	 * 
	 * The value if this is "null" should be interpreted as "false", but is
	 * necessary to have as a separate value for establishing whether it was set
	 * by data.
	 */
	private Boolean required;

	/**
	 * Sets the Display name for this ContentDefinition
	 * 
	 * @param name
	 *            The DisplayName for this UserContent
	 * @throws IllegalArgumentException
	 *             if the given name is null
	 */
	public void setDisplayName(String name)
	{
		if (name==null) {
			System.out.println("display name should not be null!");
			return;
		}
		displayName = name;
	}

	@Override
	public String getDisplayName()
	{
		return displayName;
	}

	/**
	 * Sets the "usable location" (related to what LST files this
	 * ContentDefinition will be usable within)
	 * 
	 * @param cl
	 *            The Class indicating the "usable location" of this
	 *            ContentDefinition
	 */
	@SuppressWarnings("unchecked")
	public void setUsableLocation(Class<? extends Loadable> cl)
	{
		this.usableLocation = (Class<T>) Objects.requireNonNull(cl);
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
	 * @param fmtManager
	 *            The non-null FormatManager for this ContentDefinition
	 * @return The previous FormatManager for this ContentDefinition.
	 * @throws IllegalArgumentException
	 *             if the given FormatManager is null
	 */
	public FormatManager<?> setFormatManager(FormatManager<F> fmtManager)
	{
		Objects.requireNonNull(fmtManager, "Format Manager cannot be null");
		FormatManager<?> returnValue = formatManager;
		this.formatManager = fmtManager;
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
			throw new UnsupportedOperationException("Global ContentDefinition cannot be required");
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
			activateOutput(context.getDataSetID());
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
	 * 
	 * @param dsID
	 *            The DataSetID for which the output should be activated
	 */
	protected abstract void activateOutput(DataSetID dsID);

	/**
	 * Activates the tokens supporting this ContentDefinition. This is intended
	 * only for internal use by ContentDefinition, as a portion of the
	 * activate() method.
	 * 
	 * This should not be called from an external source
	 */
	protected abstract void activateTokens(LoadContext context);
}
