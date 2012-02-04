package pcgen.gui2.tabs.models;

import javax.swing.ComboBoxModel;
import pcgen.core.facade.ReferenceFacade;
import pcgen.core.facade.event.ReferenceEvent;
import pcgen.core.facade.event.ReferenceListener;
import pcgen.gui2.util.FacadeListModel;

public abstract class CharacterComboBoxModel<E> extends FacadeListModel<E> implements ComboBoxModel, ReferenceListener<E>
{

	private ReferenceFacade<E> reference = null;
	protected Object selectedItem = null;

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
			setSelectedItem0(reference.getReference());
		}
	}

	public Object getSelectedItem()
	{
		return selectedItem;
	}

	private void setSelectedItem0(Object item)
	{
		selectedItem = item;
		fireContentsChanged(this, -1, -1);
	}

	public void referenceChanged(ReferenceEvent<E> e)
	{
		setSelectedItem0(e.getNewReference());
	}
}
