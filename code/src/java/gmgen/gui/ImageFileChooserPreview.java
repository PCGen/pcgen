/*
   GNU Lesser General Public License
   ImageFileChooserPreview
   Copyright (C) 2000-2002  Frits Jalvingh & Howard Kistler
   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2.1 of the License, or (at your option) any later version.
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.
   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package gmgen.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

/** Class provides a preview window for the selected image file
 */
public class ImageFileChooserPreview extends JComponent implements PropertyChangeListener
{
	private static final int PREVIEW_WIDTH = 100;
	private static final int PREVIEW_HEIGHT = 100;
	private File imageFile = null;
	private ImageIcon imageThumb = null;

	/** This class requires a file chooser to register with so this class will
	 * be notified when a new file is selected in the browser.
	 * @param parent that this preview window is used in.
	 */
	public ImageFileChooserPreview(JFileChooser parent)
	{
		setPreferredSize(new Dimension(PREVIEW_WIDTH, PREVIEW_HEIGHT));
		parent.addPropertyChangeListener(this);
	}

	/** Loads a new image into the preview window, and scales it if necessary.
	 */
	private void loadImage()
	{
		if (imageFile == null)
		{
			imageThumb = null;

			return;
		}

		imageThumb = new ImageIcon(imageFile.getPath());

		// Check if thumb requires scaling
		if ((imageThumb.getIconHeight() < PREVIEW_HEIGHT) && (imageThumb.getIconWidth() < PREVIEW_WIDTH))
		{
			return;
		}

		int w = PREVIEW_WIDTH;
		int h = PREVIEW_HEIGHT;

		if (imageThumb.getIconHeight() > imageThumb.getIconWidth())
		{
			w = -1;
		}
		else
		{
			h = -1;
		}

		imageThumb = new ImageIcon(imageThumb.getImage().getScaledInstance(w, h, Image.SCALE_DEFAULT));
	}

	/** Paints the icon of the current image, if one's present..
	 * @param g object to use when painting the component.
	 */
	@Override
	public void paintComponent(Graphics g)
	{
		if (imageThumb == null)
		{
			loadImage();
		}

		if (imageThumb == null)
		{
			return;
		}

		int x = (getWidth() - imageThumb.getIconWidth()) / 2;
		int y = (getHeight() - imageThumb.getIconHeight()) / 2;

		if (y < 0)
		{
			y = 0;
		}

		if (x < 5)
		{
			x = 5;
		}

		imageThumb.paintIcon(this, g, x, y);
	}

	/** Callback (event handler) to indicate that a property of the
	 * JFileChooser has changed. If the selected file has changed cause a new
	 * thumbnail to load.
	 * @param evt
	 */
	@Override
	public void propertyChange(final PropertyChangeEvent evt)
	{
		if (evt.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY))
		{
			imageFile = (File) evt.getNewValue();

			if (isShowing())
			{
				loadImage();
				repaint();
			}
		}
	}
}
