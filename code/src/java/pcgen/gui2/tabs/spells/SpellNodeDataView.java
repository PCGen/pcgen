package pcgen.gui2.tabs.spells;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

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

	public SpellNodeDataView(boolean initiallyVisible, String prefsKey)
	{
		super();
		this.prefsKey = prefsKey;
		columns = Arrays.asList(new DefaultDataViewColumn("School", String.class, initiallyVisible),
								new DefaultDataViewColumn("Subschool", String.class, initiallyVisible),
								new DefaultDataViewColumn("Descriptors", String.class, initiallyVisible),
								new DefaultDataViewColumn("Components", String.class, initiallyVisible),
								new DefaultDataViewColumn("Range", String.class),
								new DefaultDataViewColumn("Duration", String.class),
								new DefaultDataViewColumn("Source", String.class),
								new DefaultDataViewColumn("Cast Time", String.class));
	}

//	@Override
//	public List<?> getData(SuperNode obj)
//	{
//		if (obj instanceof SpellNode)
//		{
//			SpellFacade spell = ((SpellNode) obj).getSpell();
//			if (spell == null)
//			{
//				return Arrays.asList(null, null, null, null, null, null, null);
//			}
//			return Arrays.asList(spell.getSchool(), spell.getSubschool(),
//								 StringUtils.join(spell.getDescriptors(), ", "),
//								 spell.getComponents(), spell.getRange(),
//								 spell.getDuration(), spell.getSource(),
//								 spell.getCastTime());
//		}
//		else
//		{
//			return Arrays.asList(null, null, null, null, null, null, null);
//		}
//	}
	
	
	@Override
	public Object getData(SuperNode obj, int column)
	{
		if(obj instanceof SpellNode){
			SpellFacade spell = ((SpellNode) obj).getSpell();
			if (spell != null)
			{
				switch(column){
					case 0:
						return spell.getSchool();
					case 1:
						return spell.getSubschool();
					case 2:
						return StringUtils.join(spell.getDescriptors(), ", ");
					case 3:
						return spell.getComponents();
					case 4:
						return spell.getRange();
					case 5:
						return spell.getDuration();
					case 6:
						return spell.getSource();
					case 7:
						return spell.getCastTime();
				}
			}
		}
		return null;
	}

	@Override
	public void setData(Object value, SuperNode element, int column)
	{
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
