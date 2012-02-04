package pcgen.gui2.tabs.models;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.ComboBoxModel;
import javax.swing.SwingUtilities;

import pcgen.core.facade.ReferenceFacade;
import pcgen.core.facade.event.ReferenceEvent;
import pcgen.core.facade.event.ReferenceListener;
import pcgen.gui2.util.FacadeListModel;

/**
 * The Class <code>DeferredCharacterComboBoxModel</code> is a model for a combo 
 * box that holds off setting the value until focus is lost. This gets around a bug 
 * with the keyboard navigation of JComboBox where each key press selects the 
 * highlighted entry. This model should be used where costly or permanent actions 
 * are taken when an item is selected (e.h Race). 
 * <P>
 * Note: This class needs to be added as a FocusListener of the target JComboBox 
 * for selection to work.  
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 *  
 * @param <E> The type of object being managed, generally a Facade 
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
@SuppressWarnings("serial")
public abstract class DeferredCharacterComboBoxModel<E> extends
		FacadeListModel<E> implements ComboBoxModel, ReferenceListener<E>,
		FocusListener
{

	private ReferenceFacade<E> reference = null;
	protected Object selectedItem = null;

	/**
	 * Set the reference to the selected object that we should listen for external changes to.
	 * @param ref The reference.
	 */
	public void setReference(ReferenceFacade<E> ref)
	{
		if (reference != null)
		{
			reference.removeReferenceListener(this);
		}
		reference = ref;
		if (reference != null)
		{
			reference.addReferenceListener(this);
			setSelectedItem(reference.getReference());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getSelectedItem()
	{
		return selectedItem;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setSelectedItem(Object item)
	{
		selectedItem = item;
		fireContentsChanged(this, -1, -1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void referenceChanged(ReferenceEvent<E> e)
	{
		setSelectedItem(e.getNewReference());
	}
	
	/**
	 * Now that the user has finished updating the combo box, save the value 
	 * they selected. This should be implemented as appropriate for each child 
	 * of this class. 
	 * @param item The item that the user selected.
	 */
	public abstract void commitSelectedItem(Object item);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void focusGained(FocusEvent e)
	{
		// Ignored
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void focusLost(FocusEvent e)
	{
		// Temporary focus lost means something like the drop-down has
		// got focus
		if (e.isTemporary())
		{
			return;
		}

		// Focus was really lost; commit the update but do it after the focus is lost
		final Runnable doUpdate = new Runnable()
		{
			public void run()
			{
				commitSelectedItem(selectedItem);
			}
		};

		SwingUtilities.invokeLater(doUpdate);
	}
}
