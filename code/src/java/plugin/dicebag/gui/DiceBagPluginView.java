/*
 * Copyright 2003 (C) Ross M. Lodge
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
package plugin.dicebag.gui;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import pcgen.core.SettingsHandler;
import plugin.dicebag.DiceBagPlugin;

/**
 *
 * <p>The view class for the DiceBag plugin.  Should manage and initialize
 * all GUI components.  Should delegate all user actions to the controller class.</p>
 */
public class DiceBagPluginView implements Observer
{
	/** The model */
	private DiceBagPluginModel m_model;

	/** Listener for internal frame events */
	private InternalFrameAdapter listener = new ChildListener();

	/** The desktop pane */
	private JDesktopPane theDesktop = null;

	/** Coords for a new bag */
	private int newX = 0;
	private int newY = 0;

	/**
	 * <p>Default (and only) constructor.  Initializes the components.</p>
	 *
	 * @param o The observable object.
	 */
	public DiceBagPluginView(DiceBagPluginModel o)
	{
		super();
		o.addObserver(this);
		m_model = o;
		initComponents();
	}

	/**
	 * <p>Returns the root component, the one that will
	 * be placed in the main tab pane.</p>
	 *
	 * @return The main or root component for this view.
	 */
	public Component getMainComponent()
	{
		return theDesktop;
	}

	/**
	 * <p>Handles the close all message; requests save for all bags.</p>
	 */
	public void closeAll()
	{
		Component[] frames = theDesktop.getComponents();
		StringBuilder files = new StringBuilder();

		for (int i = 0; i < frames.length; i++)
		{
			if (frames[i] instanceof DiceBagView)
			{
				DiceBagModel bag = ((DiceBagView) frames[i]).getBag();
				askSaveBag(bag, JOptionPane.YES_NO_OPTION);

				if (!bag.isChanged() && !bag.isBagEmpty())
				{
					files.append(bag.getFilePath() + '|');
				}
			}
		}

		SettingsHandler.setGMGenOption(DiceBagPlugin.LOG_NAME + "closeFiles", files.toString());
	}

	/**
	 * <p>Handler for frame activation -- manages the currently active bag
	 * information.</p>
	 *
	 * @param e The event that fired this handler.
	 */
	public void internalFrameActivated(InternalFrameEvent e)
	{
		if ((e.getInternalFrame() != null) && e.getInternalFrame() instanceof DiceBagView)
		{
			m_model.setActiveBag(((DiceBagView) e.getInternalFrame()).getBag());
		}
	}

	/**
	 * <p>Handles closing events -- calls the model {@code closeDiceBag()}
	 * code.</p>
	 *
	 * @param e The event that fired this handler.
	 */
	public void internalFrameClosed(InternalFrameEvent e)
	{
		if ((e.getInternalFrame() != null) && e.getInternalFrame() instanceof DiceBagView)
		{
			m_model.closeDiceBag(((DiceBagView) e.getInternalFrame()).getBag());
		}
	}

	/**
	 * <p>Handles the frame closing event; alows the user to choose
	 * whether or not to save or cancel, and vetoes the close if cancel.</p>
	 *
	 * @param e The event which fired this handler.
	 */
	public void internalFrameClosing(InternalFrameEvent e)
	{
		if ((e.getInternalFrame() != null) && e.getInternalFrame() instanceof DiceBagView)
		{
			final int answer =
					askSaveBag(((DiceBagView) e.getInternalFrame()).getBag(), JOptionPane.YES_NO_CANCEL_OPTION);

			if (answer == JOptionPane.CANCEL_OPTION)
			{
				e.getInternalFrame().setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			}
			else if ((answer == JOptionPane.NO_OPTION) || (answer == JOptionPane.YES_OPTION))
			{
				e.getInternalFrame().setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			}
		}
	}

	@Override
	public void update(Observable o, Object arg)
	{
		if ((o != null) && o instanceof DiceBagPluginModel && (arg != null) && arg instanceof DiceBagMessage)
		{
			DiceBagMessage msg = (DiceBagMessage) arg;

			switch (msg.getType())
			{
				case DiceBagMessage.ALL_DICE_BAGS_REMOVED:
					allDiceBagsRemoved();

					break;

				case DiceBagMessage.DICE_BAG_ADDED:
					diceBagAdded(msg.getDiceBag());

					break;

				case DiceBagMessage.DICE_BAG_REMOVED:
					diceBagRemoved(msg.getDiceBag());

					break;

				case DiceBagMessage.DICE_BAG_SAVED:
					diceBagSaved(msg.getDiceBag());

					break;

				case DiceBagMessage.MODEL_INITIALIZED:
					modelInitialized();

					break;

				default:
					break;
			}
		}
	}

	/**
	 * <p>Handles the all dice bags removed message.</p>
	 */
	private void allDiceBagsRemoved()
	{
		Component[] frames = theDesktop.getComponents();

		for (int i = 0; i < frames.length; i++)
		{
			if (frames[i] instanceof DiceBagView)
			{
				((DiceBagView) frames[i]).hide();
			}
		}
	}

	/**
	 * <p>Displays an option dialog with the specified {@code option}
	 * value and either saves the dice bag or not based on the response.
	 * If the cancel option is chosen or the user aborts the save dialog,
	 * {@code JOptionPane.CANCEL_OPTION} is returned instead
	 * of yes or no.  If the bag has not been changed since creation or
	 * loading, {@code JOptionPane.NO_OPTION} is returned.</p>
	 *
	 * @param bag The bag that needs saving.
	 * @param option One of the JOptionPane constants (like {@code YES_NO_OPTION}
	 * for display in the option pane.
	 * @return The selection option
	 */
	private int askSaveBag(DiceBagModel bag, int option)
	{
		int returnValue;

		if (bag.isChanged())
		{
			returnValue = JOptionPane.showConfirmDialog(getMainComponent(),
				"Do you want to save your changes to dicebag " + bag.getName() + '?', "Save?", option);

			if (returnValue == JOptionPane.YES_OPTION)
			{
				if ((bag.getFilePath() != null) && (!bag.getFilePath().isEmpty()))
				{
					m_model.saveDiceBag(bag);
				}
				else
				{
					final File saveFile = DiceBagPluginController.chooseSaveFile(bag);

					if (saveFile != null)
					{
						m_model.saveDiceBag(bag, saveFile);
					}
					else
					{
						//Use cancel here because the user chose to abort the save dialog
						returnValue = JOptionPane.CANCEL_OPTION;
					}
				}
			}
		}
		else
		{
			returnValue = JOptionPane.NO_OPTION;
		}

		return returnValue;
	}

	/**
	 * <p>Handles the dice bag added message; instantiates a new
	 * internal frame.</p>
	 *
	 * @param model
	 */
	private void diceBagAdded(DiceBagModel model)
	{
		DiceBagView view = new DiceBagView(model);
		view.addInternalFrameListener(listener);
		theDesktop.add(view);
		view.setLocation(newX, newY);
		newX += 20;
		newY += 20;

		if (!theDesktop.getBounds().contains(newX + 40, newY + 40))
		{
			newX = 0;
			newY = 0;
		}

		view.setVisible(true);
	}

	/**
	 * <p>Handles the dice bag removed message; removes
	 * the frame if its open.</p>
	 *
	 * @param model
	 */
	private void diceBagRemoved(DiceBagModel model)
	{
		Component[] frames = theDesktop.getComponents();

		for (int i = 0; i < frames.length; i++)
		{
			if (frames[i] instanceof DiceBagView)
			{
				if (((DiceBagView) frames[i]).getBag() == model)
				{
					((DiceBagView) frames[i]).hide();
				}
			}
		}
	}

	/**
	 * <p>Does nothing.</p>
	 *
	 * @param model
	 */
	private void diceBagSaved(DiceBagModel model)
	{
		// Do nothing . . .
	}

	/**
	 * <p>Initializes all the components of the view.</p>
	 */
	private void initComponents()
	{
		theDesktop = new JDesktopPane();
		theDesktop.setBackground(Color.LIGHT_GRAY);
	}

	/**
	 * <p>Does nothing.</p>
	 */
	private void modelInitialized()
	{
		// Do nothing . . .
	}

	/**
	 * <p>Listener for events on the internal frame children of this view.</p>
	 */
	private class ChildListener extends InternalFrameAdapter
	{
		@Override
		public void internalFrameActivated(InternalFrameEvent e)
		{
			DiceBagPluginView.this.internalFrameActivated(e);
		}

		@Override
		public void internalFrameClosed(InternalFrameEvent e)
		{
			DiceBagPluginView.this.internalFrameClosed(e);
		}

		@Override
		public void internalFrameClosing(InternalFrameEvent e)
		{
			DiceBagPluginView.this.internalFrameClosing(e);
		}
	}
}
