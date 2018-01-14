/*
 * PCGenApplication.java
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
 * Created on Aug 26, 2016, 1:17:39 PM
 */
package pcgen.gui3;

import java.io.File;

import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.DataSetFacade;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class PCGenApplication extends Application
{

	private Stage primaryStage;
	
	private SimpleObjectProperty<DataSetFacade> loadedDataSet;
	private SimpleObjectProperty<CharacterFacade> currentCharacter;

	public PCGenApplication()
	{
		loadedDataSet = new SimpleObjectProperty<>(this, "loadedDataSet");
		currentCharacter = new SimpleObjectProperty<>(this, "currentCharacter");
	}
	
	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		this.primaryStage = primaryStage;
		primaryStage.setTitle("PCGen");
		VBox box = new VBox();
		
		PCGenActions actions = new PCGenActions(this);
		
		box.getChildren().addAll(new PCGenMenuBar(actions));

		Scene scene = new Scene(box, 400, 350);

		primaryStage.setScene(scene);
		primaryStage.show();
//		primaryStage.t
//		primaryStage.getO
	}

	public ReadOnlyObjectProperty<DataSetFacade> loadedDataSetProperty()
	{
		return loadedDataSet;
	}

	public void createNewCharacter()
	{

	}

	public void showOpenCharacterChooser()
	{
		FileChooser chooser = new FileChooser();
		
		ExtensionFilter filter = new ExtensionFilter("Pcg files only", "pcg");
		chooser.getExtensionFilters().clear();
		chooser.getExtensionFilters().add(filter);
		chooser.setSelectedExtensionFilter(filter);
		chooser.setTitle("Open Character");
		File selectedFile = chooser.showOpenDialog(primaryStage);
		
	}

	public ReadOnlyObjectProperty<CharacterFacade> currentCharacterProperty()
	{
		return currentCharacter;
	}
	
	public void closeCharacter(CharacterFacade character){
		
	}

}
