/**
 * DescriptionInfoTab.java Copyright James Dempsey, 2010
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
 */
package pcgen.gui2.tabs;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pcgen.core.NoteItem;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;
import pcgen.gui2.tabs.bio.BiographyInfoPane;
import pcgen.gui2.tabs.bio.CampaignHistoryInfoPane;
import pcgen.gui2.tabs.bio.NoteInfoPane;
import pcgen.gui2.tabs.bio.PortraitInfoPane;
import pcgen.gui2.tools.FlippingSplitPane;
import pcgen.system.LanguageBundle;
import pcgen.util.enumeration.Tab;

/**
 * The Class {@code DescriptionInfoTab} is a placeholder for the yet to be
 * implemented description tab.
 */
@SuppressWarnings("serial")
public class DescriptionInfoTab extends FlippingSplitPane implements CharacterInfoTab
{

	private final TabTitle tabTitle = new TabTitle(Tab.DESCRIPTION);
	private final PortraitInfoPane portraitPane;
	private final BiographyInfoPane bioPane;
	private final CampaignHistoryInfoPane histPane;
	private final JList pageList;
	private final JButton addButton;
	private final JPanel pagePanel;

	/**
	 * Create a new instance of DescriptionInfoTab
	 */
	public DescriptionInfoTab()
	{
		super(); //$NON-NLS-1$
		this.portraitPane = new PortraitInfoPane();
		this.bioPane = new BiographyInfoPane();
		this.histPane = new CampaignHistoryInfoPane();
		this.pageList = new JList<>();
		this.addButton = new JButton();
		this.pagePanel = new JPanel();
		initComponents();
	}

	private void initComponents()
	{
		addButton.setAlignmentX(0.5f);

		Box box = Box.createVerticalBox();
		box.add(new JScrollPane(pageList));
		box.add(Box.createVerticalStrut(5));
		{
			Box hbox = Box.createHorizontalBox();
			hbox.add(Box.createRigidArea(new Dimension(8, 0)));
			hbox.add(addButton);
			hbox.add(Box.createRigidArea(new Dimension(8, 0)));
			box.add(hbox);
		}
		box.add(Box.createVerticalStrut(4));
		setLeftComponent(box);

		CardLayout pages = new CardLayout();

		pagePanel.setLayout(pages);
		addPage(bioPane);
		addPage(portraitPane);
		addPage(histPane);
		setRightComponent(pagePanel);
		setResizeWeight(0);
	}

	private <T extends Component & CharacterInfoTab> void addPage(T page)
	{
		pagePanel.add(page, page.getTabTitle().getValue(TabTitle.TITLE));
	}

	/**
	 * @param page
	 */
	private <T extends Component & CharacterInfoTab> void removePage(T page)
	{
		pagePanel.remove(page);
	}

	@Override
	public ModelMap createModels(CharacterFacade character)
	{
		ModelMap models = new ModelMap();
		DefaultListModel<PageItem> listModel = new DefaultListModel<>();
		List<NoteInfoPane> notePaneList = new ArrayList<>();

		PageItem firstPage = new PageItem(
			character, LanguageBundle.getString("in_descBiography"), bioPane); //$NON-NLS-1$
		listModel.addElement(firstPage);
		listModel.addElement(new PageItem(
			character, LanguageBundle.getString("in_portrait"), portraitPane)); //$NON-NLS-1$
		listModel.addElement(new PageItem(
			character, LanguageBundle.getString("in_descCampHist"), histPane)); //$NON-NLS-1$

		models.put(ListModel.class, listModel);
		models.put(List.class, notePaneList);
		models.put(NoteListHandler.class, new NoteListHandler(character, listModel, notePaneList));

		ListSelectionModel model = new DefaultListSelectionModel();
		model.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		model.setSelectionInterval(0, 0);
		models.put(ListSelectionModel.class, model);
		models.put(PageHandler.class, new PageHandler(model, firstPage));
		models.put(AddAction.class, new AddAction(character));

		return models;
	}

	@Override
	public void restoreModels(ModelMap models)
	{
		pageList.setModel(models.get(ListModel.class));
		pageList.setSelectionModel(models.get(ListSelectionModel.class));
		models.get(NoteListHandler.class).install();
		models.get(PageHandler.class).install();
		addButton.setAction(models.get(AddAction.class));
	}

	@Override
	public void storeModels(ModelMap models)
	{
		pageList.setSelectionModel(new DefaultListSelectionModel());
		models.get(PageHandler.class).uninstall();
		models.get(NoteListHandler.class).uninstall();
	}

	@Override
	public TabTitle getTabTitle()
	{
		return tabTitle;
	}

	private class NoteListHandler implements ListListener<NoteItem>
	{

		private static final int NUM_NON_NOTE_NODES = 3;
		private final ListFacade<NoteItem> notes;
		private final DefaultListModel<PageItem> listModel;
		private final List<NoteInfoPane> notePaneList;
		private final CharacterFacade character;

		public NoteListHandler(CharacterFacade character, DefaultListModel<PageItem> listModel,
			List<NoteInfoPane> notePaneList)
		{
			this.character = character;
			this.listModel = listModel;
			this.notePaneList = notePaneList;
			this.notes = character.getDescriptionFacade().getNotes();

			for (NoteItem note : notes)
			{
				createNotePane(note, character, listModel, notePaneList, -1);
			}

		}

		private NoteInfoPane createNotePane(NoteItem note, CharacterFacade character,
			DefaultListModel<PageItem> listModel, List<NoteInfoPane> notePaneList, int pos)
		{
			NoteInfoPane notePane = new NoteInfoPane(note);
			PageItem pageItem = new PageItem(character, note, notePane);
			if (pos >= 0 && pos < notePaneList.size())
			{
				// List model also has the portrait etc tabs, so we have to skip over those.
				listModel.insertElementAt(pageItem, pos + NUM_NON_NOTE_NODES);
				notePaneList.add(pos, notePane);
			}
			else
			{
				listModel.addElement(pageItem);
				notePaneList.add(notePane);
			}
			return notePane;
		}

		public void install()
		{
			notes.addListListener(this);

			for (NoteInfoPane noteInfoPane : notePaneList)
			{
				addPage(noteInfoPane);
			}
		}

		public void uninstall()
		{
			notes.removeListListener(this);

			for (NoteInfoPane noteInfoPane : notePaneList)
			{
				removePage(noteInfoPane);
			}
		}

		@Override
		public void elementAdded(ListEvent<NoteItem> e)
		{
			NoteItem note = e.getElement();
			NoteInfoPane notePane = createNotePane(note, character, listModel, notePaneList, e.getIndex());
			addPage(notePane);
		}

		@Override
		public void elementRemoved(ListEvent<NoteItem> e)
		{
			NoteItem note = e.getElement();
			if (note == null)
			{
				return;
			}

			removeNote(note);
			// Select the next node
			int index = e.getIndex() + NUM_NON_NOTE_NODES;
			if (index >= pageList.getModel().getSize())
			{
				index = pageList.getModel().getSize() - 1;
			}
			pageList.setSelectedIndex(index);
		}

		private void removeNote(NoteItem note)
		{
			for (Iterator<NoteInfoPane> iterator = notePaneList.iterator(); iterator.hasNext();)
			{
				NoteInfoPane pane = iterator.next();
				if (pane.getNote().equals(note))
				{
					iterator.remove();
					break;
				}
			}
			IntStream.range(0, listModel.getSize())
			         .mapToObj(listModel::elementAt)
			         .filter(item -> note == item.note)
			         .findFirst()
			         .ifPresent(listModel::removeElement);
		}

		@Override
		public void elementsChanged(ListEvent<NoteItem> e)
		{
			notePaneList.forEach(listModel::removeElement);
			notePaneList.clear();
			for (NoteItem note : notes)
			{
				createNotePane(note, character, listModel, notePaneList, -1);
			}
		}

		@Override
		public void elementModified(ListEvent<NoteItem> e)
		{
			NoteItem note = e.getElement();
			if (note == null)
			{
				return;
			}

			int noteIndex = e.getIndex();
			listModel.set(noteIndex, listModel.getElementAt(noteIndex));
		}
	}

	private static class PageItem
	{

		private final NoteItem note;
		private final String name;
		private final String id;
		private final CharacterInfoTab page;
		private final ModelMap data;

		/**
		 * Create a new instance of PageItem to represent a Note.
		 *
		 * @param character The character being displayed.
		 * @param note      The note being represented.
		 * @param page      The page to display the note.
		 */
		public PageItem(CharacterFacade character, NoteItem note, CharacterInfoTab page)
		{
			this.note = note;
			this.name = ""; //$NON-NLS-1$
			this.id = (String) page.getTabTitle().getValue(TabTitle.TITLE);
			this.page = page;
			this.data = page.createModels(character);
		}

		/**
		 * Create a new instance of PageItem to represent a pre-defined panel.
		 *
		 * @param character The character being displayed.
		 * @param name      The name of the page.
		 * @param page      The pre-defined page..
		 */
		public PageItem(CharacterFacade character, String name, CharacterInfoTab page)
		{
			this.note = null;
			this.name = name;
			this.id = (String) page.getTabTitle().getValue(TabTitle.TITLE);
			this.page = page;
			this.data = page.createModels(character);
		}

		@Override
		public String toString()
		{
			return note == null ? name : note.getName();
		}

		public void storeModels()
		{
			page.storeModels(data);
		}

		public void restoreModels()
		{
			page.restoreModels(data);
		}

	}

	private class PageHandler implements ListSelectionListener
	{

		private final ListSelectionModel selectionModel;
		private PageItem currentPage;

		public PageHandler(ListSelectionModel selectionModel, PageItem currentPage)
		{
			this.selectionModel = selectionModel;
			this.currentPage = currentPage;
		}

		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			if (e.getValueIsAdjusting())
			{
				return;
			}
			PageItem item = (PageItem) pageList.getSelectedValue();
			if (item == null)
			{
				return;
			}
			currentPage.storeModels();
			currentPage = item;
			currentPage.restoreModels();
			CardLayout pages = (CardLayout) pagePanel.getLayout();
			pages.show(pagePanel, currentPage.id);
		}

		public void install()
		{
			selectionModel.addListSelectionListener(this);
			currentPage.restoreModels();
			CardLayout pages = (CardLayout) pagePanel.getLayout();
			pages.show(pagePanel, currentPage.id);
		}

		public void uninstall()
		{
			selectionModel.removeListSelectionListener(this);
			currentPage.storeModels();
		}

	}

	/**
	 * The Class {@code AddAction} acts on a user pressing the Add Note
	 * button.
	 */
	private static class AddAction extends AbstractAction
	{

		private final CharacterFacade character;

		public AddAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_descAddPage")); //$NON-NLS-1$
			this.character = character;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			character.getDescriptionFacade().addNewNote();
		}

	}

}
