/*
 * Copyright 2019 Thomas Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 * 
 */
package pcgen.gui2.tabs.models;

import java.beans.PropertyChangeListener;
import java.util.Objects;

import javax.swing.JFormattedTextField;

import pcgen.facade.util.WriteableReferenceFacade;
import pcgen.facade.util.event.ReferenceListener;
import pcgen.gui2.util.ManagedField;

/**
 * A ChannelHandler is a ManagedField that keeps a Channel (in the form of a
 * WriteableReferenceFacade) in sync with a JFormattedTextField presented to the user. A
 * change in either one results in that change being "forwarded" to the other.
 *
 * @param <T>
 *            The class of object being handled by this ChannelHandler.
 */
public class ChannelHandler<T> implements ManagedField
{

	/**
	 * The Text Field displaying the channel content to the user.
	 */
	private JFormattedTextField textField;

	/**
	 * The Underlying ReferenceFacade containing the information on the PC.
	 */
	private WriteableReferenceFacade<T> underlyingRef;

	/**
	 * This listener listens for alterations to the underlying ReferenceFacade, and
	 * updates the Text Field as needed.
	 */
	private ReferenceListener<T> referenceListener =
			event -> textField.setValue(event.getNewReference());

	/**
	 * This listener listens for alterations to the Text Field, and updates the underlying
	 * ReferenceFacade as needed.
	 */
	@SuppressWarnings("unchecked")
	private PropertyChangeListener fieldListener =
			event -> underlyingRef.set(((T) textField.getValue()));

	/**
	 * Construct a new ChannelHandler that manages the given Text Field and
	 * WriteableReferenceFacade.
	 * 
	 * @param field
	 *            The Text Field displaying the channel content to the user
	 * @param ref
	 *            The Underlying ReferenceFacade containing the information on the PC
	 */
	public ChannelHandler(JFormattedTextField field,
		WriteableReferenceFacade<T> ref)
	{
		this.textField = Objects.requireNonNull(field);
		this.underlyingRef = Objects.requireNonNull(ref);
	}

	@Override
	public JFormattedTextField getTextField()
	{
		return textField;
	}

	/**
	 * Attach the handler to the screen field. e.g. When the character is made active.
	 */
	@Override
	public void install()
	{
		textField.setValue(underlyingRef.get());
		textField.addPropertyChangeListener(fieldListener);
		underlyingRef.addReferenceListener(referenceListener);
	}

	/**
	 * Detach the handler from the on screen field. e.g. when the character is no longer
	 * being displayed.
	 */
	@Override
	public void uninstall()
	{
		textField.removePropertyChangeListener(fieldListener);
		underlyingRef.removeReferenceListener(referenceListener);
	}
}
