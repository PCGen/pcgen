/*
 * Copyright 2013 Connor Petty <cpmeister@users.sourceforge.net>
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
 */
package pcgen.gui2.tools;

import java.beans.PropertyChangeEvent;
import javax.swing.table.TableColumn;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.util.table.DefaultDynamicTableColumnModel;
import pcgen.system.PropertyContext;

/**
 * This is a type of DynamicTableColumnModel which links each of the added columns
 * to a PropertyContext to allow for a persistence of column properties across
 * program executions.
 */
public class PrefTableColumnModel extends DefaultDynamicTableColumnModel
{

	private final PropertyContext colWidthCtx;
	private final PropertyContext colVisibleCtx;

	public PrefTableColumnModel(String prefKey, int offset)
	{
		super(offset);
		PropertyContext baseContext = UIPropertyContext.createContext("tablePrefs"); //$NON-NLS-1$
		PropertyContext viewPrefsContext = baseContext.createChildContext(prefKey);
		colWidthCtx = viewPrefsContext.createChildContext("width"); //$NON-NLS-1$
		colVisibleCtx = viewPrefsContext.createChildContext("visible"); //$NON-NLS-1$
	}

	private static String normalisePrefsKey(String origKey)
	{
		return origKey.replaceAll("[^\\w\\.]", "_"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Adds a new column to this column model along with the default state of the column
	 * when no preferences are found for it. These preferences are stored using each column's identifier
	 * {@code TableColumn.getIdentifier} as the preference key that column. If no identifier is set
	 * the columns header value is used instead.
	 * <br> Note: For the case of always visible columns, i.e. the first 
	 * {@code offset} number of columns added to the model, the default visibility parameter does nothing.
	 * @param column the column to add
	 * @param defaultVisibility the visibility of this column in the absence of an existing preference
	 * @param defaultWidth the width of the column in the absence of an existing preference
	 */
	public void addColumn(TableColumn column, boolean defaultVisibility, int defaultWidth)
	{
		String prefsKey = normalisePrefsKey(column.getIdentifier().toString());
		int width = colWidthCtx.initInt(prefsKey, defaultWidth);
		boolean visibility = colVisibleCtx.getBoolean(prefsKey, defaultVisibility);
		
		column.setPreferredWidth(width);
		addColumn(column);
		setVisible(column, visibility);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		super.propertyChange(evt);
		String name = evt.getPropertyName();
		if ("width".equals(name))
		{
			TableColumn col = (TableColumn) evt.getSource();
			String colKey = col.getIdentifier().toString();
			colWidthCtx.setInt(normalisePrefsKey(colKey), (Integer) evt.getNewValue());
		}
	}

	@Override
	public void setVisible(TableColumn column, boolean visible)
	{
		String colKey = column.getIdentifier().toString();
		colVisibleCtx.setBoolean(normalisePrefsKey(colKey), visible);
		super.setVisible(column, visible);
	}
}
