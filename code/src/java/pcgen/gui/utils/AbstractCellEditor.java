/**
 *
 * WHAT IS THE LICENSE OF THIS FILE???
 *
 * Bryan, you included this file, could you please update this section?
 *
 *
 *
 */
package pcgen.gui.utils;

import javax.swing.CellEditor;
import javax.swing.event.CellEditorListener;
import javax.swing.event.EventListenerList;
import java.util.EventObject;

/**
 * <code>AbstractCellEditor</code>.
 *
 * @author ???
 * @version $Revision$
 */
public class AbstractCellEditor implements CellEditor
{
	private EventListenerList listenerList = new EventListenerList();

	/**
	 * Get cell editor value, in this case null
	 * @return null
	 */
	public final Object getCellEditorValue()
	{
		return null;
	}

	/**
	 * Returns true if cell is editable, in this case always return true
	 * 
	 * @param e
	 * @return true
	 */
	public boolean isCellEditable(EventObject e)
	{
		return true;
	}

	/**
	 * Add a listener to the cell editor 
	 * @param l
	 */
	public final void addCellEditorListener(CellEditorListener l)
	{
		listenerList.add(CellEditorListener.class, l);
	}

	/**
	 * Cancel the cell editing, this method curently does nothing 
	 */
	public final void cancelCellEditing()
	{
		// TODO This method currently does nothing?
	}

	/**
	 * Remove the listener from the cell editor
	 * 
	 * @param l
	 */
	public final void removeCellEditorListener(CellEditorListener l)
	{
		listenerList.remove(CellEditorListener.class, l);
	}

	/**
	 * returns true if the event should select the cell, in this case always false
	 * 
	 * @param anEvent
	 * @return false
	 */
	public final boolean shouldSelectCell(EventObject anEvent)
	{
		return false;
	}

	/**
	 * Returns true if you should stop cell editing, in this case always true
	 * 
	 * @return true
	 */
	public final boolean stopCellEditing()
	{
		return true;
	}
}
