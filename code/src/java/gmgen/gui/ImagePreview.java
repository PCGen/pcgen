package gmgen.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.SystemColor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.apache.commons.lang3.StringUtils;

import pcgen.core.PlayerCharacter;
import pcgen.io.PCGFile;
import pcgen.io.PCGIOHandler;
import pcgen.system.LanguageBundle;

/**
 * A panel for previewing graphics in a file chooser.  This includes previewing
 * a character portrait referenced in a PCG file.
 *
 * TODO Support PCG portraits
 */
public final class ImagePreview extends JPanel implements PropertyChangeListener
{
	private static final int SIZE = 200;

	private static final String IN_NOT_AN_IMAGE = LanguageBundle.getString("in_ImagePreview_notAnImage");
	private static final String IN_NO_CHARACTER_PORTRAIT =
			LanguageBundle.getString("in_ImagePreview_noCharacterPortrait");

	private final JFileChooser jfc;

	private PlayerCharacter aPC;
	private Image image;

	/**
	 * Constructor
	 * @param jfc
	 */
	private ImagePreview(final JFileChooser jfc)
	{
		this.jfc = jfc;

		jfc.addPropertyChangeListener(this);
		jfc.setAccessory(this);

		setPreferredSize(new Dimension(SIZE, SIZE));
	}

	/**
	 * Decorates a Swing file chooser with an image previewer.
	 *
	 * @param jfc the file chooser
	 *
	 * @return the file chooser
	 */
	public static JFileChooser decorateWithImagePreview(final JFileChooser jfc)
	{
		new ImagePreview(jfc); // hooks everything up

		return jfc;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent evt)
	{
		try
		{
			updateImage(jfc.getSelectedFile());
		}
		catch (final IOException e)
		{
			e.printStackTrace(); // TODO: ack, suckage!
		}
	}

	/**
	 * Update the image
	 * @param file
	 * @throws IOException
	 */
	private void updateImage(final File file) throws IOException
	{
		if (file == null || !file.exists())
		{
			image = null;
			return;
		}

		if (PCGFile.isPCGenCharacterFile(file))
		{
			aPC = new PlayerCharacter(Collections.emptyList());

			new PCGIOHandler().readForPreview(aPC, file.getAbsolutePath());

			final String portraitPath = aPC.getDisplay().getPortraitPath();

			image = StringUtils.isEmpty(portraitPath) ? null : ImageIO.read(new File(portraitPath));
		}
		else
		{
			aPC = null;
			image = ImageIO.read(file);
		}

		repaint();
	}

	@Override
	protected void paintComponent(final Graphics g)
	{
		g.setColor(UIManager.getColor("Panel.background"));
		g.fillRect(0, 0, getWidth(), getHeight());

		final int textX = ImagePreview.getFontHeightHint(g);
		final int textY = ImagePreview.SIZE - ImagePreview.getFontHeightHint(g);

		if (image != null)
		{
			final int width = image.getWidth(null);
			final int height = image.getHeight(null);
			final int side = Math.max(width, height);
			final double scale = SIZE / (double) side;

			g.drawImage(image, 0, 0, (int) (scale * width), (int) (scale * height), null);

			// Annotate with original dimensions.  Overlay black on white so
			// the values are visible against most possible image backgrounds.
			final String dim = width + " x " + height;

			g.setColor(SystemColor.text);
			g.drawString(dim, textX, textY);
			g.setColor(SystemColor.textText);
			g.drawString(dim, textX - 1, textX - 1);
		}
		else
		{
			g.setColor(UIManager.getColor("Panel.foreground"));
			// TODO: I18N
			g.drawString(aPC == null ? ImagePreview.IN_NOT_AN_IMAGE : ImagePreview.IN_NO_CHARACTER_PORTRAIT, textX, textY);
		}
	}

	private static int getFontHeightHint(final Graphics g)
	{
		return g.getFontMetrics().getHeight();
	}
}
