package pcgen.gui2.tabs.models;

import javax.swing.ComboBoxModel;

import pcgen.facade.util.ReferenceFacade;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;
import pcgen.facade.util.ListFacade;
import pcgen.gui2.util.FacadeListModel;

public abstract class CharacterComboBoxModel<E> extends FacadeListModel<E> implements ComboBoxModel, ReferenceListener<E>
{

	private ReferenceFacade<E> reference = null;
	protected Object selectedItem = null;

	public CharacterComboBoxModel()
	{
	}

	public CharacterComboBoxModel(ListFacade<E> list, ReferenceFacade<E> ref)
	{
		setListFacade(list);
		setReference(ref);
	}

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
			setSelectedItem0(reference.get());
		}
	}

	@Override
	public Object getSelectedItem()
	{
		return selectedItem;
	}

	private void setSelectedItem0(Object item)
	{
		selectedItem = item;
		fireContentsChanged(this, -1, -1);
	}

	@Override
	public void referenceChanged(ReferenceEvent<E> e)
	{
		setSelectedItem0(e.getNewReference());
	}
}
