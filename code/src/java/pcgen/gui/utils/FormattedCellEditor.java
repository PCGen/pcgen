/*
 *  PCGen
 *  Copyright (C) 2003 Ross M. Lodge
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
 * FormattedCellEditor.java
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.gui.utils;

import javax.swing.AbstractCellEditor;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.TableCellEditor;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.NumberFormatter;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventObject;

/**
 * @author Ross M. Lodge
 *         <p>
 *         FormattedCellEditor
 *         </p>
 *
 * <p>
 * A TableCellEditor implementation that uses Java 1.4's JFormattedTextField to
 * provided editors for table cells.
 * </p>
 */
public class FormattedCellEditor extends AbstractCellEditor implements
		TableCellEditor
{

	/** A default format for date values. */
	public static DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat(
			"MM/dd/yyyy");

	/** The component */
	JFormattedTextField m_component = null;

	/**
	 * <p>
	 * An action listener for our component; stops cell editing on an action.
	 * </p>
	 */
	ActionListener m_listener = new ActionListener()
	{

		public void actionPerformed(ActionEvent e)
		{
			stopCellEditing();
		}
	};

	/**
	 * <p>
	 * Allows the caller to pass in a specific <code>JFormattedTextField</code>
	 * instance.
	 * </p>
	 *
	 * @param field
	 *            The field to use as an editor
	 */
	public FormattedCellEditor(JFormattedTextField field)
	{
		super();
		setup(field);
	}

	/**
	 * <p>
	 * Allows the caller to pass an object to be used as an example to generate
	 * a JFormattedTextField.
	 * </p>
	 *
	 * @param defaultObject
	 *            Any object
	 */
	public FormattedCellEditor(Object defaultObject)
	{
		setup(defaultObject.getClass());
	}

	/**
	 * Allows the caller to pass a Class value to be used to generate a
	 * JFormattedTextField
	 *
	 * @param cls
	 */
	public FormattedCellEditor(Class cls)
	{
		setup(cls);
	}

	/**
	 * <p>
	 * Sets up this instance based on the specified class. Several classes are
	 * recognized
	 * <ul>
	 *   <li>Float.class results in a NumberFormatter</li>
	 *   <li>Integer.class results in a NumberFormatter</li>
	 *   <li>Date.class results in a DateFormatter</li>
	 *   <li>Anything else results in a DefaultFormatter</li>
	 * </ul>
	 * <strong>Note that a DefaultFormatter requires an object that has a
	 * constructor with a single string argument to operate correctly
	 * </strong>
	 * </p>
	 *
	 * @param cls
	 *            A Class value to be used as a base for determining the
	 *            JFormattedTextField to use.
	 */
	private void setup(Class cls)
	{
		DefaultFormatter formatter = null;
		if (cls == Float.class)
		{
			formatter = new NumberFormatter(NumberFormat.getInstance());
		} else if (cls == Integer.class)
		{
			formatter = new NumberFormatter(NumberFormat.getIntegerInstance());
		} else if (cls == Date.class)
		{
			formatter = new DateFormatter(DEFAULT_DATE_FORMAT);
		} else
		{
			formatter = new DefaultFormatter();
		}
		formatter.setValueClass(cls);
		JFormattedTextField field = new JFormattedTextField(formatter);
		if (field.getFormatter() instanceof NumberFormatter)
		{
			field.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		setup(field);
	}

	/**
	 *
	 * <p>
	 * This sets up the field for use. It performs additional setup if the
	 * fields formatter is a DefaultFormatter; it also sets up the fields
	 * border to show red when the value is invalid or black when the value is
	 * valid.
	 * </p>
	 *
	 * @param field
	 */
	private void setup(JFormattedTextField field)
	{
		m_component = field;
		JFormattedTextField.AbstractFormatter formatter = m_component
				.getFormatter();
		if (formatter instanceof DefaultFormatter)
		{
			((DefaultFormatter) formatter).setAllowsInvalid(true);
			((DefaultFormatter) formatter).setCommitsOnValidEdit(true);
			((DefaultFormatter) formatter).setOverwriteMode(false);
		}
		m_component.setBorder(BorderFactory.createLineBorder(Color.black));
		m_component.addActionListener(m_listener);
		m_component.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		m_component.addPropertyChangeListener(new PropertyChangeListener()
		{

			Border m_originalBorder = m_component.getBorder();

			public void propertyChange(PropertyChangeEvent evt)
			{
				if (evt.getPropertyName() != null
						&& evt.getPropertyName().equals("editValid"))
				{
					if (evt.getNewValue() != null
							&& evt.getNewValue() instanceof Boolean)
					{
						if (((Boolean) evt.getNewValue()).booleanValue())
						{
							m_component.setBorder(m_originalBorder);
						} else
						{
							m_component.setBorder(BorderFactory
									.createLineBorder(Color.red));
						}
					}
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	public Object getCellEditorValue()
	{
		return m_component.getValue();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable,
	 *      java.lang.Object, boolean, int, int)
	 */
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column)
	{
		m_component.setValue(value);
		m_component.requestFocus();
		m_component.selectAll();
		return m_component;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.swing.table.TableCellEditor#isCellEditable(java.util.EventObject
	 *      anEvent)
	 */
	public boolean isCellEditable(EventObject anEvent)
	{
		if (anEvent instanceof MouseEvent) { return ((MouseEvent) anEvent)
				.getClickCount() >= 2; }
		return true;
	}
}
