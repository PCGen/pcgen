/*
 * PCGenAction.java
 * Copyright 2016 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Aug 27, 2016, 8:28:50 PM
 */
package pcgen.gui3;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableStringValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyCombination;
import pcgen.gui2.tools.Icons;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class PCGenAction implements EventHandler<ActionEvent>
{

	protected final SimpleStringProperty text = new SimpleStringProperty(this, "text");

	protected final SimpleObjectProperty<KeyCombination> accelerator = new SimpleObjectProperty<>(this, "accelerator");

	protected final SimpleObjectProperty<Node> graphic = new SimpleObjectProperty<>(this, "graphic");

	protected final SimpleBooleanProperty disabled = new SimpleBooleanProperty(this, "disabled", false);

	public PCGenAction(String text, Node graphic)
	{
		this.text.set(text);
		this.graphic.set(graphic);
	}

	public PCGenAction(String text, Icons icon)
	{
		this.text.set(text);
		this.graphic.set(icon.getImageView());
	}

	public PCGenAction(String text, String accelerator, Icons icon)
	{
		this.text.set(text);
//		this.accelerator.set(accelerator);
		this.graphic.set(icon.getImageView());
	}

	public ObservableStringValue textProperty()
	{
		return text;
	}

	public ObservableObjectValue<KeyCombination> acceleratorProperty()
	{
		return accelerator;
	}

	public ObservableObjectValue<Node> graphicProperty()
	{
		return graphic;
	}

	public ObservableBooleanValue disabledProperty()
	{
		return disabled;
	}

	@Override
	public void handle(ActionEvent event)
	{
	}

}
