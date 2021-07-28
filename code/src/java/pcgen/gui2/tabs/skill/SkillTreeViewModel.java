/*
 * Copyright 2010 Connor Petty <cpmeister@users.sourceforge.net>
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
package pcgen.gui2.tabs.skill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Skill;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.CharacterLevelFacade;
import pcgen.facade.core.CharacterLevelsFacade;
import pcgen.facade.core.CharacterLevelsFacade.CharacterLevelEvent;
import pcgen.facade.core.CharacterLevelsFacade.SkillBonusListener;
import pcgen.facade.core.CharacterLevelsFacade.SkillBreakdown;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.ListFacade;
import pcgen.gui2.filter.FilteredTreeViewTable;
import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.DataViewColumn;
import pcgen.gui2.util.treeview.DefaultDataViewColumn;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewModel;
import pcgen.gui2.util.treeview.TreeViewPath;
import pcgen.system.LanguageBundle;

import org.apache.commons.lang3.StringUtils;

public class SkillTreeViewModel
		implements TreeViewModel<Skill>, DataView<Skill>, SkillBonusListener, ListSelectionListener
{

	private static final List<? extends DataViewColumn> COLUMNS =
			Arrays.asList(new DefaultDataViewColumn("in_iskTotal", Integer.class, true),
				new DefaultDataViewColumn("in_iskModifier", Integer.class, true),
				new DefaultDataViewColumn("in_skillRanks", Float.class, true, true),
				new DefaultDataViewColumn("in_classString", String.class, true),
				new DefaultDataViewColumn("in_skillSkillCost", String.class,
					SkillCost.CLASS.getCost() != SkillCost.CROSS_CLASS.getCost()),
				new DefaultDataViewColumn("in_descrip", String.class), //$NON-NLS-1$
				new DefaultDataViewColumn("in_source", String.class));
	private final DefaultListFacade<TreeView<Skill>> treeviews;
	private final CharacterFacade character;
	private final CharacterLevelsFacade levels;
	private final ListSelectionModel selectionModel;
	private FilteredTreeViewTable<CharacterFacade, Skill> table;

	public SkillTreeViewModel(CharacterFacade character, ListSelectionModel selectionModel)
	{
		this.character = character;
		this.levels = character.getCharacterLevelsFacade();
		this.selectionModel = selectionModel;

		List<? extends TreeView<Skill>> views = Arrays.asList(SkillTreeView.NAME, SkillTreeView.TYPE_NAME,
			SkillTreeView.KEYSTAT_NAME, SkillTreeView.KEYSTAT_TYPE_NAME, COST_NAME, COST_TYPE_NAME);
		treeviews = new DefaultListFacade<>(views);
	}

	public void install(FilteredTreeViewTable<CharacterFacade, Skill> ftvt)
	{
		this.table = ftvt;
		ftvt.setTreeViewModel(this);
		levels.addSkillBonusListener(this);
		selectionModel.addListSelectionListener(this);
	}

	public void uninstall()
	{
		table = null;
		levels.removeSkillBonusListener(this);
		selectionModel.removeListSelectionListener(this);
	}

	@Override
	public ListFacade<? extends TreeView<Skill>> getTreeViews()
	{
		return treeviews;
	}

	@Override
	public int getDefaultTreeViewIndex()
	{
		return 1;
	}

	@Override
	public DataView<Skill> getDataView()
	{
		return this;
	}

	@Override
	public ListFacade<Skill> getDataModel()
	{
		return character.getDataSet().getSkills();
	}

	@Override
	public String getPrefsKey()
	{
		return "SkillTreeAvail"; //$NON-NLS-1$
	}

	@Override
	public Object getData(Skill obj, int column)
	{
		if (selectionModel.isSelectionEmpty())
		{
			switch (column)
			{
				case 0:
				case 1:
				case 4:
					return 0;
				case 2:
					return 0.0;
				case 3:
				case 5:
					return null;
				case 6:
					return obj.getSource();
				default:
					return null;
			}
		}
		int index = selectionModel.getMinSelectionIndex();
		CharacterLevelFacade level = levels.getElementAt(index);
		SkillBreakdown skillBreakdown = levels.getSkillBreakdown(level, obj);
		switch (column)
		{
			case 0:
				return skillBreakdown.total;
			case 1:
				return skillBreakdown.modifier;
			case 2:
				return skillBreakdown.ranks;
			case 3:
				return levels.getSkillCost(level, obj) == SkillCost.CLASS
					? LanguageBundle.getString("in_yes") //$NON-NLS-1$
					: LanguageBundle.getString("in_no"); //$NON-NLS-1$
			case 4:
				return levels.getSkillCost(level, obj).getCost();
			case 5:
				return character.getInfoFactory().getDescription(obj);
			case 6:
				return obj.getSource();
			default:
				return null;
		}
	}

	@Override
	public void setData(Object value, Skill element, int column)
	{
	}

	@Override
	public List<? extends DataViewColumn> getDataColumns()
	{
		return COLUMNS;
	}

	@Override
	public void skillBonusChanged(CharacterLevelEvent e)
	{
		table.refreshModelData();
	}

	/**
	 * Create a TreeViewPath for the skill and paths but trimming off the final 
	 * path if it is empty. 
	 * @param pobj The skill
	 * @param path The paths under which the skills should be shown.
	 * @return The TreeViewPath.
	 */
	protected static TreeViewPath<Skill> createTreeViewPath(Skill pobj, Object... path)
	{
		Object[] displayPath;
		if (path.length > 0 && StringUtils.isEmpty(String.valueOf(path[path.length - 1])))
		{
			displayPath = Arrays.copyOf(path, path.length - 1);
		}
		else
		{
			displayPath = path;
		}
		if (displayPath.length == 0)
		{
			return new TreeViewPath<>(pobj);
		}
		return new TreeViewPath<>(pobj, displayPath);
	}

	private enum SkillTreeView implements TreeView<Skill>
	{

		NAME("in_Name"), //$NON-NLS-1$
		TYPE_NAME("in_typeName"), //$NON-NLS-1$
		KEYSTAT_NAME("in_keyStatName"), //$NON-NLS-1$
		KEYSTAT_TYPE_NAME("in_keyStatTypeName"); //$NON-NLS-1$
		private final String name;

		private SkillTreeView(String nameKey)
		{
			this.name = LanguageBundle.getString(nameKey);
		}

		@Override
		public String getViewName()
		{
			return name;
		}

		@Override
		@SuppressWarnings("unchecked")
		public List<TreeViewPath<Skill>> getPaths(Skill pobj)
		{
			TreeViewPath<Skill> path;
			switch (this)
			{
				case NAME:
					path = new TreeViewPath<>(pobj);
					break;
				case TYPE_NAME:
					path = createTreeViewPath(pobj, getDisplayType(pobj));
					break;
				case KEYSTAT_NAME:
					path = new TreeViewPath<>(pobj, pobj.getKeyStatAbb());
					break;
				case KEYSTAT_TYPE_NAME:
					path = createTreeViewPath(pobj, pobj.getKeyStatAbb(), getDisplayType(pobj));
					break;
				default:
					throw new InternalError();
			}
			return Collections.singletonList(path);
		}

	}

	private final TreeView<Skill> COST_NAME = new TreeView<>()
	{

		@Override
		public String getViewName()
		{
			return LanguageBundle.getString("in_skillCost_Name"); //$NON-NLS-1$
		}

		@Override
		@SuppressWarnings("unchecked")
		public List<TreeViewPath<Skill>> getPaths(Skill pobj)
		{
			List<Object> path = new ArrayList<>();
			int index = selectionModel.getMinSelectionIndex();
			if (index >= 0)
			{
				CharacterLevelFacade level = levels.getElementAt(index);
				path.add(levels.getSkillCost(level, pobj));
			}
			return Collections.singletonList(createTreeViewPath(pobj, path.toArray()));
		}

	};
	private final TreeView<Skill> COST_TYPE_NAME = new TreeView<>()
	{

		@Override
		public String getViewName()
		{
			return LanguageBundle.getString("in_skillCost_Type_Name"); //$NON-NLS-1$
		}

		@Override
		@SuppressWarnings("unchecked")
		public List<TreeViewPath<Skill>> getPaths(Skill pobj)
		{
			List<Object> path = new ArrayList<>();
			int index = selectionModel.getMinSelectionIndex();
			if (index >= 0)
			{
				CharacterLevelFacade level = levels.getElementAt(index);
				path.add(levels.getSkillCost(level, pobj));
			}
			path.add(getDisplayType(pobj));
			return Collections.singletonList(createTreeViewPath(pobj, path.toArray()));
		}

	};

	private static Object getDisplayType(Skill pobj)
	{
		List<Type> trueTypeList = pobj.getTrueTypeList(true);
		return StringUtil.join(trueTypeList, ".");
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0)
	{
		if (arg0.getValueIsAdjusting())
		{
			return;
		}

		if (table.getSelectedTreeView() == COST_NAME || table.getSelectedTreeView() == COST_TYPE_NAME)
		{
			table.setTreeViewModel(this);
		}
		else
		{
			table.refreshModelData();
		}
	}
}
