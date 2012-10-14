/*
 * SpellTreeViewModel.java
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
 * Created on Sep 23, 2011, 2:02:31 PM
 */
package pcgen.gui2.tabs.spells;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import pcgen.core.facade.SpellSupportFacade.SpellNode;
import pcgen.core.facade.SpellSupportFacade.SuperNode;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.ListFacade;
import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewModel;
import pcgen.gui2.util.treeview.TreeViewPath;
import pcgen.system.LanguageBundle;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class SpellTreeViewModel implements TreeViewModel<SuperNode>
{

	private final SpellNodeDataView dataView;
	private final ListFacade<? extends SuperNode> spellNodes;
	private final ListFacade<SpellTreeView> treeViews;
	private final String prefsKey;

	public SpellTreeViewModel(ListFacade<? extends SuperNode> spellNodes, boolean showcolumns, String prefsKey)
	{
		this.spellNodes = spellNodes;
		this.prefsKey = prefsKey;
		this.treeViews = new DefaultListFacade<SpellTreeView>(Arrays.asList(SpellTreeView.values()));
		this.dataView = new SpellNodeDataView(showcolumns, prefsKey);
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
		
		private String name;

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
			if (node instanceof SpellNode)
			{
				SpellNode pobj = (SpellNode) node;
				LinkedList<Object> pathList = new LinkedList<Object>();
				switch (this)
				{
					case CLASS_LEVEL_SPELL:
						Collections.addAll(pathList, pobj.getRootNode(), pobj.getSpellcastingClass(), pobj.getSpellLevel());
						pathList.removeAll(Collections.singleton(null));
						if (pobj.getSpell() == null)
						{
							pathList.removeLast();
						}
						path = new TreeViewPath<SuperNode>(pobj, pathList.toArray());
						break;
					case CLASS_LEVEL_SCHOOL_SPELL:
						Collections.addAll(pathList, pobj.getRootNode(),
							pobj.getSpellcastingClass(), pobj.getSpellLevel());
						if (pobj.getSpell() != null)
						{
							pathList.add(pobj.getSpell().getSchool());
						}
						pathList.removeAll(Collections.singleton(null));
						if (pobj.getSpell() == null)
						{
							pathList.removeLast();
						}
						path = new TreeViewPath<SuperNode>(pobj, pathList.toArray());
						break;
					default:
						throw new InternalError();
				}

			}
			else
			{
				path = new TreeViewPath<SuperNode>(node);
			}
			return Arrays.asList(path);
		}

	}

}
