/*
 * PortraitInfoPane.java
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
 * Created on Jul 9, 2011, 3:34:09 PM
 */
package pcgen.gui2.tabs.bio;

import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.event.ReferenceEvent;
import pcgen.core.facade.event.ReferenceListener;
import pcgen.gui.utils.Utility;
import pcgen.gui2.tabs.CharacterInfoTab;
import pcgen.gui2.tabs.TabTitle;
import pcgen.gui2.tools.Icons;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class PortraitInfoPane extends JScrollPane implements CharacterInfoTab
{

	private static final TabTitle tabTitle = new TabTitle(LanguageBundle.getString("in_portrait"));
	private final PortraitPane portraitPane;
	private final ThumbnailPane tnPane;
	private final JButton loadButton;
	private final JButton clearButton;
	private final JButton purchaseButton;
	private JFileChooser chooser = null;

	public PortraitInfoPane()
	{
		super();
		this.tnPane = new ThumbnailPane();
		this.portraitPane = new PortraitPane();
		this.loadButton = new JButton();
		this.clearButton = new JButton();
		this.purchaseButton = new JButton();
		initComponents();
	}

	private void initComponents()
	{
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
		Box box = Box.createVerticalBox();
		{
			Box hbox = Box.createHorizontalBox();
			hbox.add(Box.createHorizontalGlue());
			hbox.add(new JLabel(LanguageBundle.getString("in_largePortrait")));
			hbox.add(Box.createHorizontalGlue());
			box.add(hbox);
		}
		box.add(Box.createVerticalStrut(10));
		box.add(portraitPane);
		box.add(Box.createVerticalStrut(10));
		{
			Box hbox = Box.createHorizontalBox();
			hbox.add(loadButton);
			hbox.add(Box.createHorizontalStrut(10));
			hbox.add(clearButton);
			box.add(hbox);
		}
		box.add(Box.createVerticalStrut(5));
		{
			Box hbox = Box.createHorizontalBox();
			hbox.add(purchaseButton);
			box.add(hbox);
		}
		panel.add(box);

		box = Box.createVerticalBox();
		box.add(Box.createVerticalGlue());
		{
			Box hbox = Box.createHorizontalBox();
			hbox.add(Box.createHorizontalGlue());
			hbox.add(new JLabel(LanguageBundle.getString("in_thumbnailPortrait")));
			hbox.add(Box.createHorizontalGlue());
			box.add(hbox);
		}
		box.add(Box.createVerticalStrut(10));
		{
			Box hbox = Box.createHorizontalBox();
			hbox.add(Box.createHorizontalGlue());
			hbox.add(tnPane);
			hbox.add(Box.createHorizontalGlue());
			box.add(hbox);
		}
		box.add(Box.createVerticalGlue());
		panel.add(box);
		this.setViewportView(panel);
	}

	private BufferedImage createDefaultPortrait()
	{
		ImageIcon defaultPortrait = Icons.DefaultPortrait.getImageIcon();
		BufferedImage bufImage = new BufferedImage(defaultPortrait.getIconWidth(),
												   defaultPortrait.getIconHeight(),
												   BufferedImage.TYPE_INT_ARGB);
		defaultPortrait.paintIcon(this, bufImage.createGraphics(), 0, 0);
		return bufImage;
	}

	public Hashtable<Object, Object> createModels(CharacterFacade character)
	{
		Hashtable<Object, Object> state = new Hashtable<Object, Object>();
		state.put(PortraitHandler.class, new PortraitHandler(character));
		state.put(LoadAction.class, new LoadAction(character));
		state.put(ClearAction.class, new ClearAction(character));
		state.put(PurchaseAction.class, new PurchaseAction(character));
		return state;
	}

	public void restoreModels(Hashtable<?, ?> state)
	{
		loadButton.setAction((Action) state.get(LoadAction.class));
		clearButton.setAction((Action) state.get(ClearAction.class));
		purchaseButton.setAction((Action) state.get(PurchaseAction.class));
		((PortraitHandler) state.get(PortraitHandler.class)).install();
	}

	public void storeModels(Hashtable<Object, Object> state)
	{
		((PortraitHandler) state.get(PortraitHandler.class)).uninstall();
	}

	public TabTitle getTabTitle()
	{
		return tabTitle;
	}

	private class LoadAction extends AbstractAction implements PropertyChangeListener
	{

		private final CharacterFacade character;
		private ImagePreviewer previewer = new ImagePreviewer();

		public LoadAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_loadPortrait"));
			this.character = character;
		}

		public void actionPerformed(ActionEvent e)
		{
			if (chooser == null)
			{
				chooser = new JFileChooser();
			}
			chooser.setAccessory(previewer);
			chooser.addPropertyChangeListener(this);
			int ret = chooser.showOpenDialog(PortraitInfoPane.this);
			chooser.removePropertyChangeListener(this);
			BufferedImage image = previewer.getImage();
			if (ret == JFileChooser.APPROVE_OPTION && image != null)
			{
				character.setPortrait(chooser.getSelectedFile());
			}
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			previewer.setImage(chooser.getSelectedFile());
		}

	}

	private class ClearAction extends AbstractAction
	{

		private final CharacterFacade character;

		public ClearAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_clearPortrait"));
			this.character = character;
		}

		public void actionPerformed(ActionEvent e)
		{
			character.setPortrait(null);
		}

	}

	private class PurchaseAction extends AbstractAction
	{

		private final CharacterFacade character;

		public PurchaseAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_buyPortrait"));
			this.character = character;
			putValue(SHORT_DESCRIPTION, LanguageBundle.getString("in_buyPortraitTipString"));
		}

		public void actionPerformed(ActionEvent e)
		{
			Utility.viewInBrowser("https://www.e-junkie.com/ecom/gb.php?cl=154598&c=ib&aff=154875");
		}

	}

	private class PortraitHandler implements ReferenceListener<Object>
	{

		private CharacterFacade character;
		private PortraitPane.MouseHandler mouseHandler;

		public PortraitHandler(CharacterFacade character)
		{
			this.character = character;
			this.mouseHandler = portraitPane.createMouseHandler(character);
		}

		public void install()
		{
			File file = character.getPortraitRef().getReference();
			setPortrait(file);
			Rectangle cropRect = character.getThumbnailCropRef().getReference();
			if (cropRect == null)
			{
				cropRect = new Rectangle(0, 0, 100, 100);
			}
			portraitPane.setCropRectangle(cropRect);
			tnPane.setCropRectangle(cropRect);

			character.getPortraitRef().addReferenceListener(this);
			character.getThumbnailCropRef().addReferenceListener(this);
			portraitPane.addMouseListener(mouseHandler);
			portraitPane.addMouseMotionListener(mouseHandler);
			portraitPane.addMouseWheelListener(mouseHandler);
		}

		public void uninstall()
		{
			character.getPortraitRef().removeReferenceListener(this);
			character.getThumbnailCropRef().removeReferenceListener(this);
			portraitPane.removeMouseListener(mouseHandler);
			portraitPane.removeMouseMotionListener(mouseHandler);
			portraitPane.removeMouseWheelListener(mouseHandler);
		}

		private void setPortrait(File file)
		{
			BufferedImage image = null;
			try
			{
				if (file != null)
				{
					image = ImageIO.read(file);
				}
			}
			catch (IOException ex)
			{
				Logging.errorPrint("Could not load image", ex);
			}
			if (image == null)
			{
				image = createDefaultPortrait();
			}
			portraitPane.setPortraitImage(image);
			portraitPane.revalidate();
			tnPane.setPortraitImage(image);
			tnPane.revalidate();
		}

		public void referenceChanged(ReferenceEvent<Object> e)
		{
			Object obj = e.getNewReference();
			if (obj == null || obj instanceof File)
			{
				setPortrait((File) obj);
				character.setThumbnailCrop(new Rectangle(1, 1, 100, 100));
			}
			else if (obj instanceof Rectangle)
			{
				Rectangle rect = (Rectangle) obj;
				portraitPane.setCropRectangle(rect);
				tnPane.setCropRectangle(rect);
			}
		}

	}

}
