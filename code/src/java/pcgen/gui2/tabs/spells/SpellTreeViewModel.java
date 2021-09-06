/*
 * Copyright 2011 Connor Petty <cpmeister@users.sourceforge.net>
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
package pcgen.gui2.tabs.spells;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import pcgen.facade.core.InfoFactory;
import pcgen.facade.core.SpellSupportFacade.SpellNode;
import pcgen.facade.core.SpellSupportFacade.SuperNode;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.ListFacade;
import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewModel;
import pcgen.gui2.util.treeview.TreeViewPath;
import pcgen.system.LanguageBundle;

public class SpellTreeViewModel implements TreeViewModel<SuperNode>
{

	private final SpellNodeDataView dataView;
	private final ListFacade<? extends SuperNode> spellNodes;
	private final ListFacade<SpellTreeView> treeViews;

	public SpellTreeViewModel(ListFacade<? extends SuperNode> spellNodes, boolean showcolumns, String prefsKey,
		InfoFactory infoFactory)
	{
		this.spellNodes = spellNodes;
		this.treeViews = new DefaultListFacade<>(Arrays.asList(SpellTreeView.values()));
		this.dataView = new SpellNodeDataView(showcolumns, prefsKey, infoFactory);
	}

	@Override
	public ListFacade<? extends TreeView<SuperNode>> getTreeViews()
	{
		return treeViews;
	}

	@Override
	public int getDefaultTreeViewIndex()
	{
		return 0;
	}

	@Override
	public DataView<SuperNode> getDataView()
	{
		return dataView;
	}

	@Override
	public ListFacade<SuperNode> getDataModel()
	{
		return (ListFacade<SuperNode>) spellNodes;
	}

	private enum SpellTreeView implements TreeView<SuperNode>
	{

		CLASS_LEVEL_SPELL("in_spellClassLevelSpell"), //$NON-NLS-1$
		CLASS_LEVEL_SCHOOL_SPELL("in_spellClassLevelSchoolSpell"); //$NON-NLS-1$

		private final String name;

		private SpellTreeView(String name)
		{
			this.name = LanguageBundle.getString(name);
		}

		@Override
		public String getViewName()
		{
			return name;
		}

		@Override
		@SuppressWarnings("unchecked")
		public List<TreeViewPath<SuperNode>> getPaths(SuperNode node)
		{
			TreeViewPath<SuperNode> path;
			if (node instanceof SpellNode pobj)
			{
				LinkedList<Object> pathList = new LinkedList<>();
				switch (this)
				{
					case CLASS_LEVEL_SPELL -> {
						Collections.addAll(pathList, pobj.getRootNode(), pobj.getSpellcastingClass(),
								pobj.getSpellLevel()
						);
						pathList.removeAll(Collections.singleton(null));
						if (pobj.getSpell() == null)
						{
							pathList.removeLast();
						}
						path = new TreeViewPath<>(pobj, pathList.toArray());
					}
					case CLASS_LEVEL_SCHOOL_SPELL -> {
						Collections.addAll(pathList, pobj.getRootNode(), pobj.getSpellcastingClass(),
								pobj.getSpellLevel()
						);
						if (pobj.getSpell() != null)
						{
							pathList.add(pobj.getSpell().getSchool());
						}
						pathList.removeAll(Collections.singleton(null));
						if (pobj.getSpell() == null)
						{
							pathList.removeLast();
						}
						path = new TreeViewPath<>(pobj, pathList.toArray());
					}
					default -> throw new InternalError();
				}

			}
			else
			{
				path = new TreeViewPath<>(node);
			}
			return Collections.singletonList(path);
		}

	}

}
