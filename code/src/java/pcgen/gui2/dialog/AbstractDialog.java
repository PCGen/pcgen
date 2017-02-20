/*
 * Copyright 2012 Vincent Lhote
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
 */
package pcgen.gui2.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.UIManager;

import pcgen.gui2.tools.Utility;
import pcgen.system.LanguageBundle;

/**
 * A dialog with a Ok, a cancel and eventually an apply button.
 *
 *
 */
public abstract class AbstractDialog extends JDialog
{

	private static final long serialVersionUID = -6457261103398090360L;

	// TODO provide a UIManager or L&F derivated value
	protected static final int GAP = 12;

	/** The OK button */
	private JButton okButton;

	/**
	 * @see JDialog#JDialog(Frame, String, boolean)
	 */
	public AbstractDialog(Frame f, String title, boolean modal)
	{
		super(f, title, modal);
		initialize();
	}

	private void initialize()
	{
		okButton = new JButton(LanguageBundle.getString(getOkKey()));
		okButton.setMnemonic(LanguageBundle.getMnemonic(getOkMnKey()));
		okButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				okButtonActionPerformed();
			}
		});

		JButton cancelButton = new JButton(LanguageBundle.getString(getCancelKey()));
		cancelButton.setMnemonic(LanguageBundle.getMnemonic(getCancelMnKey()));
		cancelButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				cancelButtonActionPerformed();
			}
		});

		JButton bApply = null;
		if (includeApplyButton())
		{
			bApply = new JButton(LanguageBundle.getString("in_apply")); //$NON-NLS-1$
			bApply.setMnemonic(LanguageBundle.getMnemonic("in_mn_apply")); //$NON-NLS-1$
			bApply.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent evt)
				{
					applyButtonActionPerformed();
				}
			});
		}

		// initialize button panel
		JPanel buttonPanel = new JPanel();

		buttonPanel.setBorder(UIManager.getBorder("OptionPane.border")); //$NON-NLS-1$
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		// offer identical width for Preferred size if needed
		boolean sameSize = UIManager.getBoolean("OptionPane.sameSizeButtons"); //$NON-NLS-1$
		if (sameSize)
		{
			int max = Math.max(okButton.getPreferredSize().width, cancelButton.getPreferredSize().width);
			if (includeApplyButton())
			{
				max = Math.max(max, bApply.getPreferredSize().width);
				bApply.setPreferredSize(new Dimension(max, bApply.getPreferredSize().height));
			}
			okButton.setPreferredSize(new Dimension(max, okButton.getPreferredSize().height));
			cancelButton.setPreferredSize(new Dimension(max, cancelButton.getPreferredSize().height));
		}
		// add button, respecting OptionPane.isYesLast
		boolean isYesLast = UIManager.getBoolean("OptionPane.isYesLast"); //$NON-NLS-1$
		int padding = UIManager.getInt("OptionPane.buttonPadding"); //$NON-NLS-1$
		if (isYesLast)
		{
			if (includeApplyButton())
			{
				buttonPanel.add(bApply);
				buttonPanel.add(Box.createHorizontalStrut(padding));
			}
			buttonPanel.add(cancelButton);
			buttonPanel.add(Box.createHorizontalStrut(padding));
			buttonPanel.add(okButton);
		}
		else
		{
			buttonPanel.add(okButton);
			buttonPanel.add(Box.createHorizontalStrut(padding));
			if (includeApplyButton())
			{
				buttonPanel.add(bApply);
				buttonPanel.add(Box.createHorizontalStrut(padding));
			}
			buttonPanel.add(cancelButton);
		}

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(getCenter(), BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		if (shouldSetOkAsDefault())
		{
			setOkAsDefault();
		}

		if (shouldInstallEsc())
		{
			Utility.installEscapeCloseOperation(this);
		}
	}

	/**
	 * Set the ok default button as default. Use for dialog that need some field to be entered before allowing ok to be
	 * the default, like a username/password dialog.
	 */
	protected void setOkAsDefault()
	{
		getRootPane().setDefaultButton(okButton);
	}

	/**
	 * {@code true} if the ok button should be set as default during init
	 * @return {@code true} by default
	 */
	protected boolean shouldSetOkAsDefault()
	{
		return true;
	}

	/**
	 * Indicate if Esc should be installed as close window (not cancel) during init
	 * @return {@code true} by default
	 */
	protected boolean shouldInstallEsc()
	{
		return true;
	}

	protected String getCancelMnKey()
	{
		return "in_mn_cancel"; //$NON-NLS-1$
	}

	protected String getCancelKey()
	{
		return "in_cancel"; //$NON-NLS-1$
	}

	protected String getOkMnKey()
	{
		return "in_mn_ok"; //$NON-NLS-1$
	}

	protected String getOkKey()
	{
		return "in_ok"; //$NON-NLS-1$
	}

	protected abstract JComponent getCenter();

	protected boolean includeApplyButton()
	{
		return false;
	}

	/**
	 * Defaults to calling apply and closing.
	 */
	public void okButtonActionPerformed()
	{
		applyButtonActionPerformed();
		close();
	}

	/**
	 * Defaults to closing the window
	 */
	public void cancelButtonActionPerformed()
	{
		close();
	}

	/**
	 * Defaults to hide and dispose.
	 */
	protected void close()
	{
		setVisible(false);
		dispose();
	}

	/**
	 * what to do if the ok button is pressed (beside closing the dialog)
	 */
	protected abstract void applyButtonActionPerformed();

}
