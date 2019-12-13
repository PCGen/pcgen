/**
 * CharacterSheetInfoTab.java Copyright James Dempsey, 2010
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 **/
package pcgen.gui2.tabs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import pcgen.core.GameMode;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.EquipmentSetFacade;
import pcgen.facade.core.TempBonusFacade;
import pcgen.facade.util.ListFacades;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;
import pcgen.gui2.csheet.CharacterSheetPanel;
import pcgen.gui2.filter.Filter;
import pcgen.gui2.filter.FilteredListFacadeTableModel;
import pcgen.gui2.tools.FlippingSplitPane;
import pcgen.gui2.util.DisplayAwareTab;
import pcgen.gui2.util.table.TableUtils;
import pcgen.gui3.GuiAssertions;
import pcgen.gui3.GuiUtility;
import pcgen.system.ConfigurationSettings;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;
import pcgen.util.enumeration.Tab;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

/**
 * The Class {@code CharacterSheetInfoTab} is a placeholder for the
 * character sheet tab.
 */
public class CharacterSheetInfoTab extends FlippingSplitPane implements CharacterInfoTab, DisplayAwareTab
{
	private final TabTitle tabTitle = new TabTitle(Tab.CHARACTERSHEET);
	private final CharacterSheetPanel csheet;
	private final ComboBox<File> sheetBox;
	private final JTable equipSetTable;
	private final JTable tempBonusTable;
	private final JTable tempBonusRowTable;
	private final JTable equipSetRowTable;


	/**
	 * Create a new instance of CharacterSheetInfoTab
	 */
	@SuppressWarnings("serial")
	public CharacterSheetInfoTab()
	{
		this.csheet = new CharacterSheetPanel();
		this.sheetBox = new ComboBox<>();
		this.equipSetTable = TableUtils.createDefaultTable();
		this.equipSetRowTable = TableUtils.createDefaultTable();
		this.tempBonusTable = TableUtils.createDefaultTable();
		this.tempBonusRowTable = TableUtils.createDefaultTable();
		setOneTouchExpandable(true);

		JPanel panel = new JPanel(new BorderLayout());
		Box box = Box.createHorizontalBox();
		box.add(new JLabel(LanguageBundle.getString("in_character_sheet_label"))); //$NON-NLS-1$

		// todo: make this into a proper component
		sheetBox.setConverter(new StringConverter<>()
		{
			@Override
			public String toString(final File file)
			{
				if (file == null)
				{
					return "";
				}
				return file.getName();
			}

			@Override
			public File fromString(final String input)
			{
				if (input == null)
				{
					return null;
				}
				return new File(input);
			}
		});

		box.add(GuiUtility.wrapParentAsJFXPanel(sheetBox));
		panel.add(box, BorderLayout.NORTH);
		FlippingSplitPane subPane = new FlippingSplitPane();
		subPane.setOrientation(VERTICAL_SPLIT);
		equipSetTable.getTableHeader().setReorderingAllowed(false);
		JScrollPane pane = TableUtils.createRadioBoxSelectionPane(equipSetTable, equipSetRowTable);
		pane.setPreferredSize(new Dimension(200, 100));
		subPane.setTopComponent(pane);

		tempBonusTable.getTableHeader().setReorderingAllowed(false);
		pane = TableUtils.createCheckBoxSelectionPane(tempBonusTable, tempBonusRowTable);
		pane.setPreferredSize(new Dimension(200, 100));
		subPane.setBottomComponent(pane);
		panel.add(subPane, BorderLayout.CENTER);
		panel.setPreferredSize(panel.getMinimumSize());
		setLeftComponent(panel);

		csheet.setPreferredSize(new Dimension(600, 300));
		setRightComponent(csheet);

	}

	@Override
	public ModelMap createModels(CharacterFacade character)
	{
		ModelMap models = new ModelMap();
		models.put(BoxHandler.class, new BoxHandler(character));
		models.put(CSheetHandler.class, new CSheetHandler(character));
		models.put(TempBonusTableModel.class, new TempBonusTableModel(character));
		models.put(EquipSetTableModel.class, new EquipSetTableModel(character));
		return models;
	}

	@Override
	public void restoreModels(ModelMap models)
	{
		models.get(BoxHandler.class).install();
		models.get(CSheetHandler.class).install();
		EquipSetTableModel equipSetModel = models.get(EquipSetTableModel.class);
		equipSetTable.setModel(equipSetModel);
		equipSetRowTable.setModel(equipSetModel);
		TempBonusTableModel tempBonusModel = models.get(TempBonusTableModel.class);
		tempBonusTable.setModel(tempBonusModel);
		tempBonusRowTable.setModel(tempBonusModel);
	}

	@Override
	public void storeModels(ModelMap models)
	{
		models.get(CSheetHandler.class).uninstall();
	}

	@Override
	public TabTitle getTabTitle()
	{
		return tabTitle;
	}

	@Override
	public void tabSelected()
	{
		// Refresh the character sheet as we have been displayed.
		csheet.refresh();
	}

	private final class BoxHandler
	{

		/**
		 * Prefs key for the character sheet for a game mode.
		 */
		private final CharacterFacade character;
		private final ObservableList<File> sheetBoxItems;

		private BoxHandler(CharacterFacade character)
		{
			GuiAssertions.assertIsNotJavaFXThread();
			if (character==null) {
			    System.out.println("Not expecting a null character in CharacterSheetInfoTab BoxHandler");
            }
			this.character = character;
			// This is the model for ComboBox
			this.sheetBoxItems = FXCollections.observableArrayList();
			GameMode game = character.getDataSet().getGameMode();
			String previewDir = ConfigurationSettings.getPreviewDir();
			File sheetDir = new File(previewDir, game.getCharSheetDir());
			if (sheetDir.exists() && sheetDir.isDirectory())
			{
				File[] files = sheetDir.listFiles(pathname -> pathname.isFile() && !pathname.isHidden());
				if (files == null)
				{
					sheetBoxItems.clear();
				}
				else
				{
					List<File> fileAsList =
							Arrays.stream(files)
		                          .sorted(Comparator.comparing(file -> file.toString()
		                                                                 .toLowerCase(Locale.getDefault())))
		                          .collect(Collectors.toList());
					sheetBoxItems.setAll(fileAsList);
				}


				File file = null;
				String previewSheet = character.getPreviewSheetRef().toString();
				if (previewSheet == null)
				{
					previewSheet = game.getDefaultCharSheet();
				}
				if (previewSheet != null)
				{
					file = new File(sheetDir, previewSheet);
					if (!file.isFile())
					{
						Logging.errorPrint("Invalid Character Sheet: " + file.getAbsolutePath()); //$NON-NLS-1$
					}
				}
				if ((file == null || !file.isFile()) && game.getDefaultCharSheet() != null)
				{
					file = new File(sheetDir, game.getDefaultCharSheet());
				}
				File finalFile = file;
				Platform.runLater(() -> sheetBox.getSelectionModel().select(finalFile));
			}
			else
			{
				sheetBoxItems.clear();
			}
		}

		public void install()
		{
			GuiAssertions.assertIsNotJavaFXThread();
			sheetBox.setOnAction(this::onSelectedCharSheet);

			Platform.runLater(() -> {
				csheet.setCharacterSheet(sheetBox.getSelectionModel().getSelectedItem());
				sheetBox.setItems(sheetBoxItems);
			});
		}

		private void onSelectedCharSheet(final ActionEvent actionEvent)
		{
			GuiAssertions.assertIsJavaFXThread();
			File outputSheet = sheetBox.getSelectionModel().getSelectedItem();
			csheet.setCharacterSheet(outputSheet);
			csheet.refresh();
			if (outputSheet!=null)
    			character.setPreviewSheet(outputSheet.getName());
		}

	}

	private class CSheetHandler implements ListListener<Object>, ReferenceListener<Object>
	{

		private final CharacterFacade character;

		public CSheetHandler(CharacterFacade character)
		{
			this.character = character;
		}

		public void install()
		{
			csheet.setCharacter(character);
		}

		public void uninstall()
		{
		}

		@Override
		public void elementAdded(ListEvent<Object> e)
		{
			csheet.refresh();
		}

		@Override
		public void elementRemoved(ListEvent<Object> e)
		{
			csheet.refresh();
		}

		@Override
		public void elementsChanged(ListEvent<Object> e)
		{
			csheet.refresh();
		}

		@Override
		public void referenceChanged(ReferenceEvent<Object> e)
		{
			csheet.refresh();
		}

		@Override
		public void elementModified(ListEvent<Object> e)
		{
			csheet.refresh();
		}

	}

	private class TempBonusTableModel extends FilteredListFacadeTableModel<TempBonusFacade>
			implements Filter<CharacterFacade, TempBonusFacade>
	{

		/**
		 * Version for serialisation.
		 */
		private static final long serialVersionUID = -2157540968522498242L;

		private final ListListener<TempBonusFacade> listener = new ListListener<>()
		{

			@Override
			public void elementAdded(ListEvent<TempBonusFacade> e)
			{
				int index = ListFacades.wrap(sortedList).indexOf(e.getElement());
				TempBonusTableModel.this.fireTableCellUpdated(index, -1);
			}

			@Override
			public void elementRemoved(ListEvent<TempBonusFacade> e)
			{
				int index = ListFacades.wrap(sortedList).indexOf(e.getElement());
				TempBonusTableModel.this.fireTableCellUpdated(index, -1);
			}

			@Override
			public void elementsChanged(ListEvent<TempBonusFacade> e)
			{
				TempBonusTableModel.this.fireTableRowsUpdated(0, sortedList.getSize() - 1);
			}

			@Override
			public void elementModified(ListEvent<TempBonusFacade> e)
			{
			}

		};

		public TempBonusTableModel(CharacterFacade character)
		{
			super(character);
			setDelegate(character.getTempBonuses());
			setFilter(this);
			character.getTempBonuses().addListListener(listener);
		}

		@Override
		public Class<?> getColumnClass(int columnIndex)
		{
			if (columnIndex == -1)
			{
				return Boolean.class;
			}
			return super.getColumnClass(columnIndex);
		}

		@Override
		protected Object getValueAt(TempBonusFacade element, int column)
		{
			switch (column)
			{
				case -1:
					return element.isActive();
				case 0:
					return element;
				default:
					return null;
			}
		}

		@Override
		public int getColumnCount()
		{
			return 1;
		}

		@Override
		public String getColumnName(int column)
		{
			if (column == 0)
			{
				return LanguageBundle.getString("in_InfoTempMod"); //$NON-NLS-1$
			}
			return null;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			TempBonusFacade bonus = sortedList.getElementAt(rowIndex);
			character.setTempBonusActive(bonus, aValue == Boolean.TRUE);
			csheet.refresh();
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return columnIndex < 0;
		}

		@Override
		public boolean accept(CharacterFacade context, TempBonusFacade element)
		{
			return true;
		}

	}

	private class EquipSetTableModel extends FilteredListFacadeTableModel<EquipmentSetFacade>
			implements ReferenceListener<EquipmentSetFacade>, Filter<CharacterFacade, EquipmentSetFacade>
	{

		/**
		 * Version for serialisation.
		 */
		private static final long serialVersionUID = 5028006226606996671L;

		public EquipSetTableModel(CharacterFacade character)
		{
			super(character);
			character.getEquipmentSetRef().addReferenceListener(this);
			setDelegate(character.getEquipmentSets());
			setFilter(this);
		}

		@Override
		public Class<?> getColumnClass(int columnIndex)
		{
			if (columnIndex == -1)
			{
				return Boolean.class;
			}
			return super.getColumnClass(columnIndex);
		}

		@Override
		protected Object getValueAt(EquipmentSetFacade element, int column)
		{
			switch (column)
			{
				case -1:
					return character.getEquipmentSetRef().get() == element;
				case 0:
					return element;
				default:
					return null;
			}
		}

		@Override
		public int getColumnCount()
		{
			return 1;
		}

		@Override
		public String getColumnName(int column)
		{
			if (column == 0)
			{
				return LanguageBundle.getString("in_csEquipSets"); //$NON-NLS-1$
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return columnIndex < 0;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			EquipmentSetFacade eqset = sortedList.getElementAt(rowIndex);
			character.setEquipmentSet(eqset);
			csheet.refresh();
		}

		@Override
		public void referenceChanged(ReferenceEvent<EquipmentSetFacade> e)
		{
			fireTableRowsUpdated(0, character.getEquipmentSets().getSize() - 1);
		}

		@Override
		public boolean accept(CharacterFacade context, EquipmentSetFacade element)
		{
			return true;
		}

	}

}
