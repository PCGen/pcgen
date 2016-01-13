package pcgen.gui2.tabs.spells;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import pcgen.facade.core.InfoFactory;

import pcgen.facade.core.SpellFacade;
import pcgen.facade.core.SpellSupportFacade.SpellNode;
import pcgen.facade.core.SpellSupportFacade.SuperNode;
import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.DataViewColumn;
import pcgen.gui2.util.treeview.DefaultDataViewColumn;

class SpellNodeDataView implements DataView<SuperNode>
{

	private final List<? extends DataViewColumn> columns;
	private final String prefsKey;
	private final InfoFactory infoFactory;

	public SpellNodeDataView(boolean initiallyVisible, String prefsKey, InfoFactory infoFactory)
	{
		super();
		this.prefsKey = prefsKey;
		this.infoFactory = infoFactory;
		columns = Arrays.asList(new DefaultDataViewColumn("School", String.class, initiallyVisible),
								new DefaultDataViewColumn("Subschool", String.class, initiallyVisible),
								new DefaultDataViewColumn("Descriptors", String.class, initiallyVisible),
								new DefaultDataViewColumn("Components", String.class, initiallyVisible),
								new DefaultDataViewColumn("in_descrip", String.class, initiallyVisible),
								new DefaultDataViewColumn("Range", String.class),
								new DefaultDataViewColumn("Duration", String.class),
								new DefaultDataViewColumn("Source", String.class),
								new DefaultDataViewColumn("Cast Time", String.class));
	}

	@Override
	public List<?> getData(SuperNode obj)
	{
		if (obj instanceof SpellNode)
		{
			SpellFacade spell = ((SpellNode) obj).getSpell();
			if (spell == null)
			{
				return Arrays.asList(null, null, null, null, null, null, null);
			}
			return Arrays.asList(spell.getSchool(), spell.getSubschool(),
								 StringUtils.join(spell.getDescriptors(), ", "),
								 spell.getComponents(), 
								 infoFactory.getDescription(spell),
								 spell.getRange(),
								 spell.getDuration(), spell.getSource(),
								 spell.getCastTime());
		}
		else
		{
			return Arrays.asList(null, null, null, null, null, null, null);
		}
	}

	@Override
	public List<? extends DataViewColumn> getDataColumns()
	{
		return columns;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPrefsKey()
	{
		return prefsKey;
	}

}
