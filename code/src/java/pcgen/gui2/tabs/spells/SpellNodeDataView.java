package pcgen.gui2.tabs.spells;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import pcgen.core.facade.SpellFacade;
import pcgen.core.facade.SpellSupportFacade.SpellNode;
import pcgen.core.facade.SpellSupportFacade.SuperNode;
import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.DataViewColumn;
import pcgen.gui2.util.treeview.DefaultDataViewColumn;

class SpellNodeDataView implements DataView<SuperNode>
{

	private final List<? extends DataViewColumn> columns;

	public SpellNodeDataView(boolean initiallyVisible)
	{
		super();
		columns = Arrays.asList(new DefaultDataViewColumn("School", String.class, initiallyVisible),
								new DefaultDataViewColumn("Subschool", String.class, initiallyVisible),
								new DefaultDataViewColumn("Descriptors", String.class, initiallyVisible),
								new DefaultDataViewColumn("Components", String.class, initiallyVisible),
								new DefaultDataViewColumn("Range", String.class),
								new DefaultDataViewColumn("Duration", String.class),
								new DefaultDataViewColumn("Source", String.class));
	}

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
								 spell.getComponents(), spell.getRange(),
								 spell.getDuration(), spell.getSource());
		}
		else
		{
			return Arrays.asList(null, null, null, null, null, null, null);
		}
	}

	public List<? extends DataViewColumn> getDataColumns()
	{
		return columns;
	}

}
