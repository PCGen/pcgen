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
package pcgen.gui2.tabs.bio;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pcgen.cdom.base.Constants;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;
import pcgen.gui2.tabs.CharacterInfoTab;
import pcgen.gui2.tabs.TabTitle;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.Utility;
import pcgen.gui3.GuiUtility;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;
import pcgen.util.Logging;

import javafx.stage.FileChooser;

@SuppressWarnings("serial")
public class PortraitInfoPane extends JScrollPane implements CharacterInfoTab
{

	private static final TabTitle TAB_TITLE = new TabTitle("in_portrait", null);
	private static final int MAX_PORTRAIT_HEIGHT = 400;
	private final PortraitPane portraitPane;
	private final ThumbnailPane tnPane;
	private final JButton loadButton;
	private final JButton clearButton;
	private final JSlider zoomSlider;

	public PortraitInfoPane()
	{
		super();
		this.tnPane = new ThumbnailPane();
		this.portraitPane = new PortraitPane();
		this.loadButton = new JButton();
		this.clearButton = new JButton();
		this.zoomSlider = new JSlider(SwingConstants.VERTICAL);
		initComponents();
	}

	private void initComponents()
	{
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		Utility.buildConstraints(gbc, 0, 0, 2, 1, 0, 0);
		panel.add(new JLabel(LanguageBundle.getString("in_largePortrait")), gbc);
		Utility.buildConstraints(gbc, 0, 1, 2, 1, 0, 0);
		panel.add(portraitPane, gbc);
		Utility.buildConstraints(gbc, 0, 2, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.EAST);
		panel.add(loadButton, gbc);
		Utility.buildConstraints(gbc, 1, 2, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
		panel.add(clearButton, gbc);

		Utility.buildConstraints(gbc, 2, 1, 1, 1, 0, 0, GridBagConstraints.VERTICAL, GridBagConstraints.CENTER);
		zoomSlider.setInverted(true);
		zoomSlider.setPreferredSize(new Dimension(20, 10));
		panel.add(zoomSlider, gbc);

		Utility.buildConstraints(gbc, 3, 0, 1, 1, 0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);
		panel.add(new JLabel(LanguageBundle.getString("in_thumbnailPortrait")), gbc);

		Utility.buildConstraints(gbc, 3, 1, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.NORTH);
		panel.add(tnPane, gbc);

		this.setViewportView(panel);
	}

	private BufferedImage createDefaultPortrait()
	{
		ImageIcon defaultPortrait = Icons.DefaultPortrait.getImageIcon();
		BufferedImage bufImage = new BufferedImage(defaultPortrait.getIconWidth(), defaultPortrait.getIconHeight(),
			BufferedImage.TYPE_INT_ARGB);
		defaultPortrait.paintIcon(this, bufImage.createGraphics(), 0, 0);
		return bufImage;
	}

	@Override
	public ModelMap createModels(CharacterFacade character)
	{
		ModelMap models = new ModelMap();
		models.put(PortraitHandler.class, new PortraitHandler(character));
		models.put(LoadAction.class, new LoadAction(character));
		models.put(ClearAction.class, new ClearAction(character));
		return models;
	}

	@Override
	public void restoreModels(ModelMap models)
	{
		loadButton.setAction(models.get(LoadAction.class));
		clearButton.setAction(models.get(ClearAction.class));
		models.get(PortraitHandler.class).install();
	}

	@Override
	public void storeModels(ModelMap models)
	{
		models.get(PortraitHandler.class).uninstall();
	}

	@Override
	public TabTitle getTabTitle()
	{
		return TAB_TITLE;
	}

	private static final class LoadAction extends AbstractAction
	{

		private final CharacterFacade character;

		private LoadAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_loadPortrait"));
			this.character = character;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialDirectory(new File(PCGenSettings.getPortraitsDir()));
			fileChooser.setTitle(LanguageBundle.getString("in_loadPortrait"));

			// TODO: set extension filter - list of supported images

			File file = GuiUtility.runOnJavaFXThreadNow(() ->
					fileChooser.showOpenDialog(null));
			if (file != null)
			{
				character.setPortrait(file);
			}
		}
	}

	private static final class ClearAction extends AbstractAction
	{

		private final CharacterFacade character;

		private ClearAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_clearPortrait")); //$NON-NLS-1$
			this.character = character;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			character.setPortrait(null);
		}

	}

	private class PortraitHandler extends MouseAdapter implements ReferenceListener<Object>, ChangeListener
	{

		private final CharacterFacade character;
		private Rectangle cropRect;
		private BufferedImage portrait;
		private float scale;
		private boolean movingRect = false;
		private Point cropOffset = null;

		public PortraitHandler(CharacterFacade character)
		{
			this.character = character;
			cropRect = character.getThumbnailCropRef().get();
			if (cropRect == null)
			{
				cropRect = new Rectangle(0, 0, 100, 100);
			}
		}

		public void install()
		{
			setPortrait(character.getPortraitRef().get());
			portraitPane.setCropRectangle(cropRect);
			tnPane.setCropRectangle(cropRect);

			character.getPortraitRef().addReferenceListener(this);
			character.getThumbnailCropRef().addReferenceListener(this);
			portraitPane.addMouseListener(this);
			portraitPane.addMouseMotionListener(this);
			portraitPane.addMouseWheelListener(this);
			zoomSlider.addChangeListener(this);
		}

		public void uninstall()
		{
			character.getPortraitRef().removeReferenceListener(this);
			character.getThumbnailCropRef().removeReferenceListener(this);
			portraitPane.removeMouseListener(this);
			portraitPane.removeMouseMotionListener(this);
			portraitPane.removeMouseWheelListener(this);
			zoomSlider.removeChangeListener(this);
		}

		private void setPortrait(File file)
		{
			portrait = null;
			try
			{
				if (file != null)
				{
					if (file.exists() && file.canRead())
					{
						portrait = ImageIO.read(file);
					}
					else
					{
						Logging.errorPrint("Unable to read portrait file " //$NON-NLS-1$
							+ file.getAbsolutePath());
					}
				}
			}
			catch (IOException ex)
			{
				Logging.errorPrint("Could not load image", ex); //$NON-NLS-1$
			}
			if (portrait == null)
			{
				portrait = createDefaultPortrait();
			}
			portraitPane.setPortraitImage(portrait);
			scale = Math.min(MAX_PORTRAIT_HEIGHT / (float) portrait.getHeight(), 1);
			int min = Constants.THUMBNAIL_SIZE;
			int max = Math.min(portrait.getHeight(), portrait.getWidth());
			int value = Math.min(cropRect.width, max);
			value = Math.max(value, min);
			zoomSlider.setModel(new DefaultBoundedRangeModel(value, 0, min, max));
			portraitPane.revalidate();
			tnPane.setPortraitImage(portrait);
			tnPane.revalidate();
		}

		@Override
		public void referenceChanged(ReferenceEvent<Object> e)
		{
			Object obj = e.getNewReference();
			if (obj == null || obj instanceof File)
			{
				setPortrait((File) obj);
				Rectangle cropRect = new Rectangle(1, 1, 100, 100);
				Utility.adjustRectToFitImage(portrait, cropRect);
				character.setThumbnailCrop(cropRect);
			}
			else if (obj instanceof Rectangle rect)
			{
				Rectangle cropRect = new Rectangle(rect);
				Utility.adjustRectToFitImage(portrait, cropRect);
				if (!rect.equals(cropRect))
				{
					character.setThumbnailCrop(new Rectangle(cropRect));
				}
				portraitPane.setCropRectangle(rect);
				tnPane.setCropRectangle(rect);
				zoomSlider.setValue(rect.width);
			}
		}

		@Override
		public void mouseDragged(MouseEvent e)
		{
			if (movingRect)
			{
				int x;
				int y;
				if (scale < 1)
				{
					x = (int) (e.getX() / scale - cropOffset.x);
					y = (int) (e.getY() / scale - cropOffset.y);
				}
				else
				{
					x = e.getX() - cropOffset.x;
					y = e.getY() - cropOffset.y;
				}
				x = Math.max(x, 0);
				y = Math.max(y, 0);
				cropRect.setLocation(x, y);
				Utility.adjustRectToFitImage(portrait, cropRect);
				character.setThumbnailCrop(new Rectangle(cropRect));
			}
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			if (e.getButton() != MouseEvent.BUTTON1)
			{
				return;
			}
			Point mousepoint = e.getPoint();
			if (scale < 1)
			{
				mousepoint.x /= scale;
				mousepoint.y /= scale;
			}
			movingRect = cropRect.contains(mousepoint);
			cropOffset = new Point(mousepoint.x - cropRect.x, mousepoint.y - cropRect.y);
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			movingRect = false;
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e)
		{
			if (e.getScrollType() != MouseWheelEvent.WHEEL_UNIT_SCROLL)
			{
				return;
			}
			Point mousepoint = e.getPoint();
			if (scale < 1)
			{
				mousepoint.x /= scale;
				mousepoint.y /= scale;
			}
			if (!cropRect.contains(mousepoint))
			{
				return;
			}
			int units = e.getUnitsToScroll();
			int size = cropRect.width + units;
			size = Math.max(size, 100);
			size = Math.min(size, portrait.getWidth());
			size = Math.min(size, portrait.getHeight());

			cropRect.width = size;
			cropRect.height = size;

			int x = cropRect.x;
			int y = cropRect.y;
			x = Math.max(x, 0);
			y = Math.max(y, 0);
			cropRect.setLocation(x, y);
			Utility.adjustRectToFitImage(portrait, cropRect);
			character.setThumbnailCrop(new Rectangle(cropRect));
		}

		@Override
		public void stateChanged(ChangeEvent e)
		{
			int size = zoomSlider.getValue();
			cropRect.width = size;
			cropRect.height = size;

			int x = cropRect.x;
			int y = cropRect.y;
			x = Math.max(x, 0);
			y = Math.max(y, 0);
			cropRect.setLocation(x, y);
			Utility.adjustRectToFitImage(portrait, cropRect);
			character.setThumbnailCrop(new Rectangle(cropRect));
		}
	}

}
