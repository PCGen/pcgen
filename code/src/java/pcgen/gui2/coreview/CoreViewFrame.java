/*
 * Copyright (c) Thomas Parker, 2013.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.gui2.coreview;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import org.apache.commons.lang.StringUtils;

import pcgen.cdom.meta.CorePerspective;
import pcgen.core.facade.AbilityFacade;
import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.CoreViewNodeFacade;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.DelegatingListFacade;
import pcgen.core.facade.util.ListFacade;
import pcgen.gui2.tools.Utility;
import pcgen.gui2.util.JTreeViewTable;
import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.DataViewColumn;
import pcgen.gui2.util.treeview.DefaultDataViewColumn;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewModel;
import pcgen.gui2.util.treeview.TreeViewPath;
import pcgen.util.Logging;

public class CoreViewFrame extends JFrame 
{

	private JPanel panel = new JPanel();
	private JTree tree;
	private final JTreeViewTable<CoreViewNodeFacade> viewTable;

	public CoreViewFrame(Frame frame, CharacterFacade character)
	{
		viewTable = new JTreeViewTable<CoreViewNodeFacade>();
		
		CoreViewTreeViewModel coreViewTreeViewModel = new CoreViewTreeViewModel(character);
		coreViewTreeViewModel.setPerspective(CorePerspective.LANGUAGE);
		viewTable.setTreeViewModel(coreViewTreeViewModel);
		initialize(character);
	}
	
	public void initialize(CharacterFacade character)
	{
		Box box = Box.createVerticalBox();
		Box picklists = Box.createHorizontalBox();
		//Add choosers to picklists here
		box.add(picklists);

		
		JScrollPane pane = new JScrollPane(viewTable);
		pane.setPreferredSize(new Dimension(500, 300));
		box.add(pane);
		setLayout(new BorderLayout());
		getContentPane().add(box, BorderLayout.CENTER);
		setTitle("Core Debug View");
		pack();
		Utility.centerFrame(this, true);
	}


	private static class GrantedTreeView implements TreeView<CoreViewNodeFacade>
	{

		public GrantedTreeView()
		{
		}

		@Override
		public String getViewName()
		{
			return "Object Tree";
		}

		@Override
		public List<TreeViewPath<CoreViewNodeFacade>> getPaths(CoreViewNodeFacade pobj)
		{
			List<List<CoreViewNodeFacade>> abilityPaths = new ArrayList<List<CoreViewNodeFacade>>();
			addPaths(abilityPaths, pobj.getGrantedByNodes(),
					 new ArrayList<CoreViewNodeFacade>());
			Logging.debugPrint("Converted " + pobj.getGrantedByNodes() + " into " + abilityPaths + " for " + pobj);
			if (abilityPaths.isEmpty())
			{
				return Collections.singletonList(new TreeViewPath<CoreViewNodeFacade>(pobj));
			}

			List<TreeViewPath<CoreViewNodeFacade>> paths = new ArrayList<TreeViewPath<CoreViewNodeFacade>>();
			for (List<CoreViewNodeFacade> path : abilityPaths)
			{
				Collections.reverse(path);
				paths.add(new TreeViewPath<CoreViewNodeFacade>(path.toArray(), pobj));
			}
			return paths;
		}

		private void addPaths(List<List<CoreViewNodeFacade>> abilityPaths,
							  List<CoreViewNodeFacade> grantedByNodes,
							  ArrayList<CoreViewNodeFacade> path)
		{
			if (path.size() > 20)
			{
				Logging.errorPrint("Found probable ability prereq cycle ["
					+ StringUtils.join(path, ",") + "] with prereqs ["
					+ StringUtils.join(grantedByNodes, ",") + "]. Skipping.");
				return;
			}
			for (CoreViewNodeFacade node : grantedByNodes)
			{
				@SuppressWarnings("unchecked")
				ArrayList<CoreViewNodeFacade> pathclone = (ArrayList<CoreViewNodeFacade>) path.clone();
				pathclone.add(node);
				List<CoreViewNodeFacade> preAbilities2 = node.getGrantedByNodes();
				// Don't include self references in the path
				preAbilities2.remove(node);
				
				
				if (preAbilities2.isEmpty())
				{
					abilityPaths.add(pathclone);
				}
				else
				{
					addPaths(abilityPaths, preAbilities2, pathclone);
				}
			}
		}

	}


	private static class CoreViewTreeViewModel extends
			DelegatingListFacade<AbilityFacade> implements
			TreeViewModel<CoreViewNodeFacade>, DataView<CoreViewNodeFacade>
	{
		private final CharacterFacade character;
		private CorePerspective corePerspective;
		private DefaultListFacade<CoreViewNodeFacade> coreViewList;
		private final List<? extends DataViewColumn> dataColumns;

		public CoreViewTreeViewModel(CharacterFacade character)
		{
			this.character = character;

			dataColumns =
					Arrays
						.asList(
							new DefaultDataViewColumn("Key", String.class),
							new DefaultDataViewColumn("Node Type", String.class),
							new DefaultDataViewColumn("Source", String.class),
							new DefaultDataViewColumn("Requirements",
								String.class));
		}
		
		/**
		 * @param language
		 */
		public void setPerspective(CorePerspective corePerspective)
		{
			this.corePerspective = corePerspective;
			List<CoreViewNodeFacade> coreViewNodes = character.getCoreViewTree(CorePerspective.LANGUAGE);
			coreViewList = new DefaultListFacade<CoreViewNodeFacade>(coreViewNodes);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListFacade<? extends TreeView<CoreViewNodeFacade>> getTreeViews()
		{
			DefaultListFacade<TreeView<CoreViewNodeFacade>> views =
					new DefaultListFacade<TreeView<CoreViewNodeFacade>>();
			views.addElement(new GrantedTreeView());
			return views;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getDefaultTreeViewIndex()
		{
			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DataView<CoreViewNodeFacade> getDataView()
		{
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListFacade<CoreViewNodeFacade> getDataModel()
		{
			return coreViewList;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public List<?> getData(CoreViewNodeFacade obj)
		{
			return Arrays.asList(obj.getKey(),
				 obj.getNodeType(),
				 obj.getSource(),
				 obj.getRequirements());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public List<? extends DataViewColumn> getDataColumns()
		{
			return dataColumns;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getPrefsKey()
		{
			return "CoreDebugView";
		}
	}
}
