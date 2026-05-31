/*
 * Copyright 2026 Vest <Vest@users.noreply.github.com>
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
 */
package pcgen.gui3.namegen;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Window;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import pcgen.gui3.GuiAssertions;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

/**
 * Modal random-name dialog. Presented to Swing callers as a synchronous
 * call: construct, {@link #showAndBlock()}, then read back
 * {@link #getChosenName()} / {@link #getGender()}.
 *
 * <p>Hosts the FXML scene inside a Swing {@link JDialog} via
 * {@link JFXPanel}. The Swing dialog gives us proper owner/modality
 * semantics against the rest of the Swing UI; the FX side keeps the
 * existing FXML, controller, and scene graph untouched.
 */
public final class RandomNameDialog
{
	private static final String FXML_RESOURCE = "RandomNamePanel.fxml";

	private final Window owner;
	private final String initialGender;
	private RandomNamePanelController controller;

	public RandomNameDialog(Window owner, String initialGender)
	{
		this.owner = owner;
		this.initialGender = initialGender;
	}

	public RandomNameDialog(String initialGender)
	{
		this(null, initialGender);
	}

	/**
	 * Show the dialog and block until the user closes it. Must be called
	 * from a non-FX thread; the call hops to the EDT if needed because
	 * Swing modal dialogs require it.
	 */
	public void showAndBlock()
	{
		GuiAssertions.assertIsNotJavaFXThread();
		if (!EventQueue.isDispatchThread())
		{
			try
			{
				EventQueue.invokeAndWait(this::showAndBlock);
			}
			catch (InterruptedException _)
			{
				Thread.currentThread().interrupt();
			}
			catch (java.lang.reflect.InvocationTargetException e)
			{
				Logging.errorPrint("failed to show random-name dialog", e);
			}
			return;
		}

		JDialog dialog = new JDialog(owner, LanguageBundle.getString("in_rndNameTitle"),
				Dialog.ModalityType.APPLICATION_MODAL);
		JFXPanel fxPanel = new JFXPanel();
		dialog.setContentPane(fxPanel);

		CountDownLatch sceneReady = new CountDownLatch(1);
		Platform.runLater(() -> {
			try
			{
				FXMLLoader loader = new FXMLLoader();
				URL location = RandomNameDialog.class.getResource(FXML_RESOURCE);
				Objects.requireNonNull(location, FXML_RESOURCE);
				loader.setLocation(location);
				loader.setResources(LanguageBundle.getBundle());
				Scene scene = loader.load();
				controller = loader.getController();
				assert controller != null;
				if (initialGender != null && !initialGender.isEmpty())
				{
					controller.setInitialGender(initialGender);
				}
				controller.setCloseAction(() -> SwingUtilities.invokeLater(dialog::dispose));
				fxPanel.setScene(scene);
				// Read the FXML's preferred size from the scene root and
				// pin both the JFXPanel's preferred size and the dialog's
				// minimum size to it. Without this, JFXPanel reports a
				// preferred size of 0 and pack() collapses the dialog.
				javafx.scene.Parent root = scene.getRoot();
				root.applyCss();
				root.layout();
				int prefW = (int) Math.ceil(root.prefWidth(-1));
				int prefH = (int) Math.ceil(root.prefHeight(prefW));
				SwingUtilities.invokeLater(() -> {
					Dimension pref = new Dimension(prefW, prefH);
					fxPanel.setPreferredSize(pref);
					dialog.pack();
					Dimension packed = dialog.getSize();
					// Lock the height (user can't change it), let the
					// width float above the design width.
					dialog.setMinimumSize(new Dimension(packed.width, packed.height));
					dialog.setMaximumSize(new Dimension(Integer.MAX_VALUE, packed.height));
					dialog.setResizable(true);
					dialog.setLocationRelativeTo(owner);
				});
			}
			catch (IOException e)
			{
				Logging.errorPrint("failed to load random-name dialog FXML", e);
			}
			finally
			{
				sceneReady.countDown();
			}
		});
		try
		{
			sceneReady.await();
		}
		catch (InterruptedException _)
		{
			Thread.currentThread().interrupt();
			return;
		}

		dialog.setVisible(true);
	}

	public String getChosenName()
	{
		return controller == null ? "" : controller.getChosenName();
	}

	public String getGender()
	{
		return controller == null ? "" : controller.getGender();
	}
}
