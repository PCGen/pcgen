package pcgen.gui2.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;

import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.ListFacade;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.util.table.DefaultDynamicTableColumnModel;
import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.DataViewColumn;
import pcgen.gui2.util.treeview.DefaultDataViewColumn;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewModel;
import pcgen.gui2.util.treeview.TreeViewPath;
import pcgen.system.PropertyContext;

import org.junit.jupiter.api.Test;

/** Verifies the Swing table menu retains its actions through model and parent changes. */
class JTreeViewTableMenuTest
{
	@Test
	void menuSwitchesViewsAndColumnsWithoutAccumulatingItems() throws Exception
	{
		SwingUtilities.invokeAndWait(() -> {
			PropertyContext preferences = UIPropertyContext.createContext("tablePrefs")
				.createChildContext("JTreeViewTableMenuTest");
			String oldView = preferences.getProperty("viewIdx");
			try
			{
				preferences.setInt("viewIdx", 0);
				JTreeViewTable<String> table = new JTreeViewTable<>();
				MenuModel model = new MenuModel();
				table.setTreeViewModel(model);
				int menuSize = table.cornerPopupMenu.getComponentCount();
				table.setTreeViewModel(model);
				assertEquals(menuSize, table.cornerPopupMenu.getComponentCount());
				JRadioButtonMenuItem flat = (JRadioButtonMenuItem) table.cornerPopupMenu.getComponent(1);
				JRadioButtonMenuItem grouped = (JRadioButtonMenuItem) table.cornerPopupMenu.getComponent(2);
				grouped.doClick(0);
				assertSame(model.grouped, table.getSelectedTreeView());
				assertTrue(grouped.isSelected());
				assertFalse(flat.isSelected());
				assertEquals(1, preferences.getInt("viewIdx"));

				JCheckBoxMenuItem value = (JCheckBoxMenuItem) table.cornerPopupMenu.getComponent(menuSize - 1);
				assertEquals(2, table.getColumnCount());
				value.doClick(0);
				assertEquals(1, table.getColumnCount());
				value.doClick(0);
				assertEquals(2, table.getColumnCount());

				table.cornerPopupMenu.setTreeViewsEnabled(false);
				assertFalse(table.cornerPopupMenu.getComponent(1).isEnabled());
				assertTrue(table.cornerPopupMenu.getComponent(menuSize - 1).isEnabled());

				JScrollPane pane = new JScrollPane(table);
				for (int i = 0; i < 50; i++)
				{
					table.configureEnclosingScrollPane();
					JButton button = (JButton) pane.getCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER);
					assertSame(table.cornerPopupMenu, button.getComponentPopupMenu());
					table.unconfigureEnclosingScrollPane();
				}
			}
			finally
			{
				preferences.setProperty("viewIdx", oldView);
			}
		});
	}

	@Test
	void dynamicTableMenuTracksColumnChangesAndReplacementModels() throws Exception
	{
		SwingUtilities.invokeAndWait(() -> {
			JDynamicTable table = new JDynamicTable();
			DefaultDynamicTableColumnModel columns = new DefaultDynamicTableColumnModel(0);
			TableColumn first = new TableColumn(0);
			first.setHeaderValue("First");
			columns.addColumn(first);
			table.setColumnModel(columns);
			JScrollPane pane = new JScrollPane(table);
			table.configureEnclosingScrollPane();
			JButton button = (JButton) pane.getCorner(ScrollPaneConstants.UPPER_TRAILING_CORNER);
			JPopupMenu menu = button.getComponentPopupMenu();
			JCheckBoxMenuItem item = (JCheckBoxMenuItem) menu.getComponent(0);
			assertEquals("First", item.getText());
			boolean initiallyVisible = columns.isVisible(first);
			item.doClick(0);
			assertEquals(!initiallyVisible, columns.isVisible(first));
			TableColumn second = new TableColumn(1);
			second.setHeaderValue("Second");
			columns.addColumn(second);
			assertEquals(2, menu.getComponentCount());
			columns.removeColumn(first);
			assertEquals("Second", ((JCheckBoxMenuItem) menu.getComponent(0)).getText());
			table.setColumnModel(new DefaultDynamicTableColumnModel(0));
			assertEquals(0, menu.getComponentCount());
			assertFalse(button.isVisible());
			columns.addColumn(first);
			assertEquals(0, menu.getComponentCount(), "The old column model must be detached");
		});
	}

	@Test
	void buttonOpensItsMenuOnTheSwingThread() throws Exception
	{
		SwingUtilities.invokeAndWait(() -> {
			Component[] invoker = new Component[1];
			JPopupMenu menu = new JPopupMenu()
			{
				@Override
				public void show(Component component, int x, int y)
				{
					assertTrue(SwingUtilities.isEventDispatchThread());
					invoker[0] = component;
				}
			};
			JTableMenuButton button = new JTableMenuButton(menu);
			button.doClick(0);
			assertSame(button, invoker[0]);
		});
	}

	/** Distinct views and one optional column make menu actions observable. */
	private static final class MenuModel implements TreeViewModel<String>, DataView<String>
	{
		private final TreeView<String> flat = new NamedView("Flat");
		private final TreeView<String> grouped = new NamedView("Grouped");
		private final DefaultListFacade<TreeView<String>> views = new DefaultListFacade<>(List.of(flat, grouped));
		private final DefaultListFacade<String> rows = new DefaultListFacade<>(List.of("Example"));

		@Override
		public ListFacade<? extends TreeView<String>> getTreeViews()
		{
			return views;
		}

		@Override
		public int getDefaultTreeViewIndex()
		{
			return 0;
		}

		@Override
		public DataView<String> getDataView()
		{
			return this;
		}

		@Override
		public ListFacade<String> getDataModel()
		{
			return rows;
		}

		@Override
		public Object getData(String element, int column)
		{
			return element;
		}

		@Override
		public void setData(Object value, String element, int column)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public List<? extends DataViewColumn> getDataColumns()
		{
			return List.of(new DefaultDataViewColumn("Value", String.class, true));
		}

		@Override
		public String getPrefsKey()
		{
			return "JTreeViewTableMenuTest";
		}
	}

	/** A tree view with a distinct label. */
	private record NamedView(String name) implements TreeView<String>
	{
		@Override
		public String getViewName()
		{
			return name;
		}

		@Override
		public List<TreeViewPath<String>> getPaths(String element)
		{
			return List.of(new TreeViewPath<>(element));
		}
	}
}
