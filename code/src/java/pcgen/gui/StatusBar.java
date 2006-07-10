package pcgen.gui;

import pcgen.core.SettingsHandler;

import javax.swing.*;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Stack;

/**
 * <code>StatusBar</code> is an application status bar with a message on the
 * left and a status area on the right.
 *
 * @author <a href="mailto:binkley@alumnir.rice.edu">B. K. Oxley (binkley)</a>
 * @version $Id$
 * @since Oct 22, 2005 4:33:27 PM
 */
public class StatusBar
		extends JPanel
{
	private static final int STATUSBAR_HEIGHT = 17;
	private static final int MB = 1024 * 1024;

	private final JLabel messageArea = new StatusBarArea(SwingConstants.LEFT, 8,
		true);
	private final Stack<String> oldMessages = new Stack<String>();
	private final JLabel memoryArea = new MemoryArea();
	private final JLabel statusArea = new StatusBarArea(SwingConstants.CENTER, 3,
			false);

	private boolean showMemoryArea;

	/**
	 * Constructs a new, default <code>StatusBar</code> and shows memory
	 * use if {@link SettingsHandler#isShowMemoryArea()}.
	 */
	public StatusBar()
	{
		setLayout(new SpringLayout());

		add(messageArea);
		add(statusArea);

		// Call this *after* adding the other components -- it assumes
		// the status bar is already in a sane state.
		setShowMemoryArea(SettingsHandler.isShowMemoryArea());

		updateLayout();
	}

	/**
	 * Gets the message area text.
	 *
	 * @return the message area text
	 */
	public String getMessageAreaText()
	{
		return messageArea.getText();
	}

	/**
	 * Sets the message area text and saves the previous message.  Repeated
	 * calls stack up the previous messages.  Nest calls to
	 * <code>setMessageAreaText</code> and
	 * <code>restoreMessageAreaText</code> in pairs.
	 *
	 * @param message the new message area text
	 *
	 * @see #restoreMessageAreaText()
	 */
	public void setMessageAreaText(final String message)
	{
		oldMessages.push(getMessageAreaText());
		setMessageAreaTextWithoutSaving(message);
	}

	/**
	 * Sets the message area text discarding the previous message.
	 *
	 * @param message
	 */
	public void setMessageAreaTextWithoutSaving(final String message)
	{
		String actualMsg = message;
		if (message == null || message.length() == 0)
		{
			actualMsg = "                                               ";
		}
		messageArea.setText(actualMsg);
		// Show the message as a tooltip so that when the message is
		// longer than the status bar, user can hover over it to see
		// the parts chopped off.
		messageArea.setToolTipText(actualMsg.trim());
		revalidate();
	}

	/**
	 * Restores the previous message area text.  Repeated calls restore
	 * older messages.  Nest calls to <code>setMessageAreaText</code> and
	 * <code>restoreMessageAreaText</code> in pairs.
	 *
	 * @see #setMessageAreaText(String)
	 */
	public void restoreMessageAreaText()
	{
		setMessageAreaTextWithoutSaving((String) oldMessages.pop());
	}

	/**
	 * Adds or removes the memory area from the status bar.
	 *
	 * @param showMemoryArea <code>true</code> to add the memory area
	 */
	public void setShowMemoryArea(final boolean showMemoryArea)
	{
		if (showMemoryArea)
		{
			if (!this.showMemoryArea)
			{
				remove(memoryArea);
				remove(statusArea);
				add(memoryArea);
				add(statusArea);
				updateLayout();
			}
		}
		else if (this.showMemoryArea)
		{
			remove(memoryArea);
			updateLayout();
		}

		this.showMemoryArea = showMemoryArea;
	}

	/**
	 * Sets the status area message.
	 *
	 * @param status the status area message
	 */
	public void setStatus(final String status)
	{
		statusArea.setText(status);
	}

	/**
	 * Clears the status area.
	 */
	public void clearStatus()
	{
		statusArea.setText(" ");
	}

	private static Dimension getAreaDimension(final int widthExtent)
	{
		return new Dimension(STATUSBAR_HEIGHT * widthExtent,
				STATUSBAR_HEIGHT);
	}

	private void updateLayout()
	{
		// 1 x N layout with no padding or offset.
		SpringUtilities.makeCompactGrid(
				this, 1, getComponentCount(), 0, 0, 0, 0);
	}

	private class StatusBarArea
			extends JLabel
	{
		private StatusBarArea(final int textPosition,
				final int widthExtent, final boolean resizing)
		{
			super(" ", textPosition);

			setBorder(BorderFactory.createLoweredBevelBorder());

			final Dimension dim = getAreaDimension(widthExtent);
			setMinimumSize(dim);
			setPreferredSize(dim);

			if (!resizing)
			{
				setMaximumSize(dim);
			}

			setText(" ");
		}
	}

	private class MemoryArea
			extends StatusBarArea
	{
		private MemoryArea()
		{
			super(SwingConstants.CENTER, 3, false);

			setToolTipText(
					"<html>Used v. total memory in MB.<br>Click to release memory.");

			addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent e)
				{
					// TODO: I18N
					setMessageAreaText("Releasing memory...");
					// Force a repaint; GC goes too fast for Swing to update
					// before it finishes -- even repaint() is too slow.
					paint(getGraphics());
					final Cursor oldCursor = getCursor();
					setCursor(Cursor.getPredefinedCursor(
							Cursor.WAIT_CURSOR));
					System.gc();
					setCursor(oldCursor);
					restoreMessageAreaText();
				}
			});

			new Timer(1000, new ActionListener()
			{
				public void actionPerformed(final ActionEvent e)
				{
					final Runtime runtime
						= Runtime.getRuntime();
					final long total
						= runtime.totalMemory();
					final long current
						= total - runtime.freeMemory();

					// Because div floors the result, add
					// one to get 64MB to look like 64MB and
					// not 63MB.
					setText((current / MB + 1)
						+ "/" + (total / MB + 1));
				}
			}).start();
		}
	}
}
