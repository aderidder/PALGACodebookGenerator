/*
 * Copyright 2017 NKI/AvL; VUmc 2018/2019/2020
 *
 * This file is part of PALGA Protocol Codebook Generator.
 *
 * PALGA Protocol Codebook Generator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PALGA Protocol Codebook Generator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PALGA Protocol Codebook Generator. If not, see <http://www.gnu.org/licenses/>
 *
 */

package palgacodebookgenerator;

import palgacodebookgenerator.gui.MainWindow;
import javafx.application.Application;
import javafx.stage.Stage;


/**
 * main for protocol parser
 */
public class PALGACodebookGenerator extends Application {
    public static void main(String ... args) {
        launch(args);
    }

    /**
     * Create the main GUI window
     *
     * @param primaryStage the primary stage
     */
    @Override
    public void start(Stage primaryStage) {
        MainWindow mainWindow = new MainWindow();
        mainWindow.createMainWindow(primaryStage);
    }

}

