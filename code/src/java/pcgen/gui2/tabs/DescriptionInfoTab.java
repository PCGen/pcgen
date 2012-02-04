/**
 * DescriptionInfoTab.java
 * Copyright James Dempsey, 2010
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
 * Created on 29/09/2010 7:16:42 PM
 *
 * $Id: DescriptionInfoTab.java 13208 2010-09-29 12:59:43Z jdempsey $
 */
package pcgen.gui2.tabs;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Hashtable;
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
import pcgen.core.facade.CharacterFacade;
import pcgen.gui2.tabs.bio.BiographyInfoPane;
import pcgen.gui2.tabs.bio.CampaignHistoryInfoPane;
import pcgen.gui2.tabs.bio.PortraitInfoPane;
import pcgen.gui2.tools.FlippingSplitPane;

/**
 * The Class <code>DescriptionInfoTab</code> is a placeholder for the yet to
 * be implemented description tab.
 *
 * <br/>
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2010-09-29 05:59:43 -0700 (Wed, 29 Sep 2010) $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 13208 $
 */
public class DescriptionInfoTab extends FlippingSplitPane implements CharacterInfoTab
{

	private final TabTitle tabTitle = new TabTitle("Description");
	private final PortraitInfoPane portraitPane;
	private final BiographyInfoPane bioPane;
	private final CampaignHistoryInfoPane histPane;
	private final JList pageList;
	private final JButton addButton;
	private final JPanel pagePanel;

	public DescriptionInfoTab()
	{
		this.portraitPane = new PortraitInfoPane();
		this.bioPane = new BiographyInfoPane();
		this.histPane = new CampaignHistoryInfoPane();
		this.pageList = new JList();
		this.addButton = new JButton();
		this.pagePanel = new JPanel();
		initComponents();
	}

	private void initComponents()
	{
		addButton.setText("Add custom page");
		addButton.setAlignmentX((float) 0.5);

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
		addPage(portraitPane);
		addPage(bioPane);
		addPage(histPane);
		// TODO: add the rest of the infoPanes
		setRightComponent(pagePanel);
		setResizeWeight(0);
	}

	private <T extends Component & CharacterInfoTab> void addPage(T page)
	{
		pagePanel.add(page, page.getTabTitle().getValue(TabTitle.TITLE));
	}

	public Hashtable<Object, Object> createModels(CharacterFacade character)
	{
		Hashtable<Object, Object> state = new Hashtable<Object, Object>();
		DefaultListModel listModel = new DefaultListModel();

		PageItem firstPage = new PageItem(character, "Portrait", portraitPane);
		listModel.addElement(firstPage);
		listModel.addElement(new PageItem(character, "Biography", bioPane));
		listModel.addElement(new PageItem(character, "Campaign History", histPane));
		//TODO: add additional pages if needed
		state.put(ListModel.class, listModel);

		ListSelectionModel model = new DefaultListSelectionModel();
		model.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		model.setSelectionInterval(0, 0);
		state.put(ListSelectionModel.class, model);
		state.put(PageHandler.class, new PageHandler(model, firstPage));
		return state;
	}

	public void restoreModels(Hashtable<?, ?> state)
	{
		pageList.setModel((ListModel) state.get(ListModel.class));
		pageList.setSelectionModel((ListSelectionModel) state.get(ListSelectionModel.class));
		((PageHandler) state.get(PageHandler.class)).install();
	}

	public void storeModels(Hashtable<Object, Object> state)
	{
		pageList.setSelectionModel(new DefaultListSelectionModel());
		((PageHandler) state.get(PageHandler.class)).uninstall();
	}

	public TabTitle getTabTitle()
	{
		return tabTitle;
	}

	private static class PageItem
	{

		private String name;
		private String id;
		private CharacterInfoTab page;
		private Hashtable data;

		public PageItem(CharacterFacade character, String name, CharacterInfoTab page)
		{
			this.name = name;
			this.id = (String) page.getTabTitle().getValue(TabTitle.TITLE);
			this.page = page;
			this.data = page.createModels(character);
		}

		public String toString()
		{
			return name;
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

		public void valueChanged(ListSelectionEvent e)
		{
			if (e.getValueIsAdjusting())
			{
				return;
			}
			currentPage.storeModels();
			PageItem item = (PageItem) pageList.getSelectedValue();
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

}
