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
 * @version $Revision: 1.4 $
 */
public class AbstractCellEditor implements CellEditor
{
	private EventListenerList listenerList = new EventListenerList();

	public final Object getCellEditorValue()
	{
		return null;
	}

	public boolean isCellEditable(EventObject e)
	{
		return true;
	}

	public final void addCellEditorListener(CellEditorListener l)
	{
		listenerList.add(CellEditorListener.class, l);
	}

	public final void cancelCellEditing()
	{
	    // TODO This method currently does nothing?
	}

	public final void removeCellEditorListener(CellEditorListener l)
	{
		listenerList.remove(CellEditorListener.class, l);
	}

	public final boolean shouldSelectCell(EventObject anEvent)
	{
		return false;
	}

	public final boolean stopCellEditing()
	{
		return true;
	}
}
