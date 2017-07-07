/*
 * PCGenActions.java
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
 * Created on Aug 26, 2016, 1:33:18 PM
 */
package pcgen.gui3;

import java.util.HashMap;
import java.util.Map;
import javafx.event.ActionEvent;
import pcgen.gui2.tools.Icons;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class PCGenActions
{

	private PCGenApplication app;

	private final Map<Class<? extends PCGenAction>, PCGenAction> actionMap;

	public PCGenActions(PCGenApplication app)
	{
		this.app = app;
		actionMap = new HashMap<>();
		actionMap.put(NewAction.class, new NewAction());
		actionMap.put(OpenAction.class, new OpenAction());
		actionMap.put(CloseAction.class, new CloseAction());
	}

	public PCGenAction getAction(Class<? extends PCGenAction> actionClass)
	{
		return actionMap.get(actionClass);
	}

//    public void newCharacter(ActionEvent event){
//	public void newCharacter(ActionEvent event)
//	{
//		System.out.println("sadfasdf");
//	}

//    public EventHandler<ActionEvent> newCharacterHandler(){
//        return (ActionEvent event) -> {
//               
//        };
//    }
	public class NewAction extends PCGenAction
	{

		private NewAction()
		{
//			super("mnuFileNew", NEW_COMMAND, "shortcut N", Icons.New16);
			super("mnuFileNew", Icons.New16);
			this.disabled.bind(app.loadedDataSetProperty().isNull());
		}

		@Override
		public void handle(ActionEvent event)
		{
			app.createNewCharacter();
		}

	}

	public class OpenAction extends PCGenAction
	{

		private OpenAction()
		{
//			super("mnuFileOpen", OPEN_COMMAND, "shortcut O", Icons.Open16);
			super("mnuFileOpen", Icons.Open16);
		}

		@Override
		public void handle(ActionEvent e)
		{
			app.showOpenCharacterChooser();
		}

	}

	public class CloseAction extends PCGenAction
	{

		private CloseAction()
		{
//			super("mnuFileClose", CLOSE_COMMAND, "shortcut W", Icons.Close16);
			super("mnuFileClose", Icons.Close16);
			disabled.bind(app.currentCharacterProperty().isNull());
		}

		@Override
		public void handle(ActionEvent e)
		{
			app.closeCharacter(app.currentCharacterProperty().get());
		}

	}

//	private abstract class CharacterAction extends PCGenAction
//	{
//
////		private ReferenceFacade<?> ref;
//
//		public CharacterAction(String prop)
//		{
//			this(prop, null, null);
//		}
//
//		public CharacterAction(String prop, Icons icon)
//		{
//			this(prop, null, icon);
//		}
//
//		public CharacterAction(String prop, String accelerator)
//		{
//			this(prop, accelerator, null);
//		}
//
//		public CharacterAction(String prop, String accelerator, Icons icon)
//		{
//			super(prop, accelerator, icon);
//			disabled.bind(app.currentCharacterProperty().isNull());
//		}
//
////		private class CharacterListener implements ReferenceListener<Object>
////		{
////
////			@Override
////			public void referenceChanged(ReferenceEvent<Object> e)
////			{
////				setEnabled(e.getNewReference() != null);
////			}
////
////		}
//
//	}
}
