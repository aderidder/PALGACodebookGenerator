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

package palgacodebookgenerator.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.ListSelectionView;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import palgacodebookgenerator.utils.ParseUtils;
import palgacodebookgenerator.utils.SQLiteUtils;

import java.io.File;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * class for the GUI Wizard
 */
class ProtocolWizard {
    private static final Logger logger = LogManager.getLogger(ProtocolWizard.class.getName());
    private static final Pattern tablePrefixPattern = ParseUtils.getStringPattern("conclusion_net");


    private static final int wizardWidth = 600;
    private static final int wizardHeight = 300;
    private final RunParameters runParameters = new RunParameters();
    private boolean canRun = false;

    /**
     * create the wizard
     * parameters from the previous run are used to set some values, such as the filenames
     * @param oldParameters    the parameters used in the previous run
     * @return true/false, whether the wizard was successfully completed
     */
    boolean startWizard(RunParameters oldParameters) {

        canRun = false;
        // create the pages
        WizardPane filesPage = createFilesPage(oldParameters);
        WizardPane protocolPage = createProtocolPage();
        WizardPane netListPage = createNetListPage();
        WizardPane summaryPage = createSummaryPage();

        // add them to the wizard
        Wizard wizard = new Wizard();
        wizard.setFlow(new Wizard.LinearFlow(filesPage, protocolPage, netListPage, summaryPage));

        // show wizard and wait for response
        wizard.showAndWait().ifPresent(result -> {
            if (result == ButtonType.FINISH) {
                canRun = true;
            }
        });
        return canRun;
    }

    /**
     * returns the runparameters which were created during the wizard
     * @return the runparameters
     */
    RunParameters getRunParameters(){
        return runParameters;
    }

    /**
     * retrieve the value of something from the wizard settings map
     * @param wizardSettings    map with the wizardsettings
     * @param setting           the setting for which we want the value
     * @return the string value of the setting
     */
    private static String getStringSetting(Map<String, Object> wizardSettings, String setting){
        if(wizardSettings.containsKey(setting)){
            return (String) wizardSettings.get((setting));
        }
        return "";
    }

    /**
     * set the initial browsing directory to the previous directory if possible
     * @param textField    textfield may already contain a directory or file
     * @return the File to which to set the initial directory
     */
    private File getInitialDirectory(TextField textField) {
        String curContent = textField.getText();
        if (!curContent.equalsIgnoreCase("")) {
            File file = new File(curContent);
            if (file.isDirectory()) {
                return file;
            } else {
                return file.getParentFile();
            }
        }
        return null;
    }

    /**
     * create a filechooser and set the textfield to the selected value
     * @param textField          the textfield which may contain a previous value and will contain the selected value
     * @param extensionFilter    filters for file extensions
     */
    private void browseFile(TextField textField, FileChooser.ExtensionFilter ... extensionFilter){
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(getInitialDirectory(textField));
            fileChooser.setTitle("Select data file");
            fileChooser.getExtensionFilters().addAll(extensionFilter);
            File selectedFile = fileChooser.showOpenDialog(null);

            if (selectedFile != null) {
                textField.setText(selectedFile.getCanonicalPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * create a directorychooser and set the textfield to the selected value
     * @param textField    the textfield which may contain a previous value and will contain the selected value
     */
    private void browseDir(TextField textField){
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setInitialDirectory(getInitialDirectory(textField));
            directoryChooser.setTitle("Select output directory");
            File selectedDirectory = directoryChooser.showDialog(null);
            if (selectedDirectory != null) {
                textField.setText(selectedDirectory.getCanonicalPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * create a row with a label textfield and file browse button, browsing files
     * @param gridPane          the gridpane to which the items will be added
     * @param id                base id of the new items
     * @param label             label to be added
     * @param oldVal            old value, which will be the initial value
     * @param row               row number
     * @param extensionFilters  filename extension filters
     * @return the textfield which was created for the browse row
     */
    private TextField createBrowseFileRow(GridPane gridPane, String id, String label, String oldVal, int row, FileChooser.ExtensionFilter ... extensionFilters){
        // add label and textfield
        gridPane.add(new Label(label), 0, row);
        TextField textField = createTextField(id, oldVal);
        gridPane.add(textField, 1, row);

        // add browse button
        addBrowseButton(gridPane, id, row, event -> browseFile(textField, extensionFilters));
        return textField;
    }

    /**
     * create a row with a label textfield and file browse button, browsing directories
     * @param gridPane  the gridpane to which the items will be added
     * @param id        base id of the new items
     * @param label     label to be added
     * @param oldVal    old value, which will be the initial value
     * @param row       row number
     * @return the textfield which was created for the browse row
     */
    private TextField createBrowseDirRow(GridPane gridPane, String id, String label, String oldVal, int row){
        // add label and textfield
        gridPane.add(new Label(label), 0, row);
        TextField textField = createTextField(id, oldVal);
        gridPane.add(textField, 1, row);

        // add browse button
        addBrowseButton(gridPane, id, row, event -> browseDir(textField));
        return textField;
    }

    /**
     * create a button with functionality
     * @param gridPane      the gridpane to which the items will be added
     * @param id            base id of the new items
     * @param row           row number
     * @param eventHandler  eventhandler which will be called when the button is clicked
     */
    private void addBrowseButton(GridPane gridPane, String id, int row, EventHandler eventHandler){
        Button browseButton = new Button("Browse");
        browseButton.setId(id+"Button");
        browseButton.setOnAction(eventHandler);
        gridPane.add(browseButton, 2, row);
    }

    /**
     * create the file selection wizard page
     * @param oldParameters previous run parameters
     * @return the wizard page
     */
    private WizardPane createFilesPage(RunParameters oldParameters){
        return new WizardPane() {
            ValidationSupport validationSupport = new ValidationSupport();

            {
                this.getStylesheets().clear();
                this.setPrefWidth(wizardWidth);
                this.setPrefHeight(wizardHeight);
                this.setHeaderText("Please select files");
                createContent();
            }

            /**
             * create the content of this page
             */
            private void createContent(){
                // create file extension filters
                FileChooser.ExtensionFilter allFilesExtensionFilter = new FileChooser.ExtensionFilter("all files", "*.*");
                FileChooser.ExtensionFilter txtFilesExtensionFilter = new FileChooser.ExtensionFilter("txt files", "*.txt");
                FileChooser.ExtensionFilter dbFilesExtensionFilter = new FileChooser.ExtensionFilter("database files", "*.db");

                // create the gridpane and add the textfields, buttons and labels
                int row = 0;
                GridPane gridPane = createGridPane();

                TextField workspaceFileTextField = createBrowseFileRow(gridPane, "workspaceFile", "Workspace file:", oldParameters.getWorkspaceFileName(), row++, dbFilesExtensionFilter, allFilesExtensionFilter);
                TextField outputFileDir = createBrowseDirRow(gridPane, "outputFileDir", "Output directory:", oldParameters.getOutputDir(), row++);
                createBrowseFileRow(gridPane, "overwriteFile", "Name overwrite file:", oldParameters.getOverwriteFileName(), row++, txtFilesExtensionFilter, allFilesExtensionFilter);

                this.setContent(gridPane);

                // add validation
                validationSupport.initInitialDecoration();

                // workaround for bug https://bitbucket.org/controlsfx/controlsfx/issues/539/multiple-dialog-fields-with-validation
                Platform.runLater(() -> {
                    validationSupport.registerValidator(outputFileDir, Validator.createEmptyValidator("Output directory required"));
                    validationSupport.registerValidator(workspaceFileTextField, Validator.createEmptyValidator("Database file required"));
                });
            }

            /**
             * things to do when we enter the page
             * @param wizard    the wizard
             */
            @Override
            public void onEnteringPage(Wizard wizard) {
                wizard.invalidProperty().unbind();
                wizard.invalidProperty().bind(validationSupport.invalidProperty());
            }

            /**
             * things to do when we leave the page
             * @param wizard    the wizard
             */
            @Override
            public void onExitingPage(Wizard wizard){
                // add the selected values to the runparameters
                runParameters.setOverwriteFileName(getStringSetting(wizard.getSettings(), "overwriteFile"));
                runParameters.setOutputDir(getStringSetting(wizard.getSettings(), "outputFileDir"));
                runParameters.setWorkspaceFileName(getStringSetting(wizard.getSettings(), "workspaceFile"));
                SQLiteUtils.setDatabase(runParameters.getWorkspaceFileName());
            }
        };
    }

    /**
     * create the protocol wizard page
     * @return the page
     */
    private WizardPane createProtocolPage(){
        return new WizardPane(){
            private final ComboBox<String> codebookTypeComboBox = createComboBox("codebookType");
            private final CheckBox optionsInSheetsCheckBox = new CheckBox();
            private final Label optionsInSheetsLabel = new Label("Create separate sheets for Options found in protocol: ");

            final ValidationSupport validationSupport = new ValidationSupport();

            {
                this.getStylesheets().clear();
                this.setPrefWidth(wizardWidth);
                this.setPrefHeight(wizardHeight);
                this.setHeaderText("Select Protocol");
                createContent();
            }

            /**
             * create the content of this page
             */
            private void createContent(){
                int row = 0;
                GridPane gridPane = createGridPane();
                codebookTypeComboBoxSetup(gridPane, row);
                optionsInSheetsCheckBoxSetup(gridPane, ++row);

                this.setContent(gridPane);

                // add validation
                validationSupport.initInitialDecoration();
            }

            /**
             * create label etc. for the checkbox
             * @param gridPane    the pane to which we will add the items
             * @param row         row number
             */
            private void optionsInSheetsCheckBoxSetup(GridPane gridPane, int row){
                gridPane.add(optionsInSheetsLabel, 0, row);
                optionsInSheetsCheckBox.setId("optionsInSheetsCheckBox");
                gridPane.add(optionsInSheetsCheckBox, 1, row);
                setOptionsRowVisible(false);
            }

            /**
             * change visibility of the options in sheets label and its checkbox
             * @param visible whether the checkbox and label should be made visible or invisible
             */
            private void setOptionsRowVisible(boolean visible){
                optionsInSheetsLabel.setVisible(visible);
                optionsInSheetsCheckBox.setVisible(visible);
            }

            /**
             * create label etc. for the codebooktype combobox
             * @param gridPane    the pane to which we will add the items
             * @param row         row number
             */
            private void codebookTypeComboBoxSetup(GridPane gridPane, int row){
                codebookTypeComboBox.setItems(FXCollections.observableArrayList("PALGAWEB", "PALGA", "NKI", "DEBUG", "PALGA & NKI"));
                gridPane.add(new Label("Codebook Type: "), 0, row);
                gridPane.add(codebookTypeComboBox, 1, row);
                codebookTypeComboBox.getSelectionModel().select(4);

                // hide the "options in sheets" stuff when PALGA & NKI is selected
                codebookTypeComboBox.setOnAction((event) -> {
                    if(codebookTypeComboBox.getValue().equalsIgnoreCase("PALGA & NKI")){
                        setOptionsRowVisible(false);
                    }
                    else{
                        setOptionsRowVisible(true);
                    }
                });
            }

            /**
             * fetch the protocol's table prefix from the database
             */
            private void setProtocolTablePrefix(){
                SQLiteUtils.openDB();
                String settings = SQLiteUtils.doTableSettingsQuery();
                SQLiteUtils.closeDB();
                runParameters.setProtocolTablePrefix(ParseUtils.getValue(settings, tablePrefixPattern));
            }

            /**
             * things to do when we enter the page
             * @param wizard    the wizard
             */
            @Override
            public void onEnteringPage(Wizard wizard) {
                wizard.invalidProperty().unbind();
                wizard.invalidProperty().bind(validationSupport.invalidProperty());
            }

            /**
             * things to do when we leave the page
             * @param wizard    the wizard
             */
            @Override
            public void onExitingPage(Wizard wizard){
                setProtocolTablePrefix();
                runParameters.setCodebookType(getStringSetting(wizard.getSettings(), "codebookType"));
                runParameters.setStoreOptionsInSeparateSheets((Boolean) wizard.getSettings().get("optionsInSheetsCheckBox"));
            }
        };
    }

    /**
     * create the netlist wizard page
     * @return the page
     */
    private WizardPane createNetListPage(){
        return new WizardPane(){
            private String curWorkspace="-1";
            private final ListSelectionView<String> listSelectionView = new ListSelectionView<>();
            final ValidationSupport validationSupport = new ValidationSupport();

            private TextField textField;

            // init
            {
                this.getStylesheets().clear();
                this.setPrefWidth(wizardWidth);
                this.setPrefHeight(wizardHeight);
                this.setHeaderText("Select Nets (at least 1)");
                createContent();
            }

            /**
             * create the content of this page
             */
            private void createContent(){
                // create gridpane for our items
                GridPane gridPane = createGridPane();

                // add the listselectionview
                gridPane.add(listSelectionView, 0, 0);

                // create label and textfield
                Label label = new Label("Number of nets:");
                textField = createTextField("netCounter", "0");
                addTargetListEventListener();

                // create an hbox to group them together
                HBox hBox = new HBox(label, textField);
                hBox.setPadding(new Insets(0, 0, 0, 15));
                hBox.setSpacing(10);

                // add the hbox
                gridPane.add(hBox, 0, 1);

                // show the gridpane
                this.setContent(gridPane);

                // add the validator
                validationSupport.initInitialDecoration();
                Predicate isNumberPredicate = o -> Integer.valueOf(textField.getText())>0;
                validationSupport.registerValidator(textField, Validator.createPredicateValidator(isNumberPredicate, "Select at least one NET"));
            }

            /**
             * set the net list for the selected protocol
             * this will allow the user to select one or more nets
             */
            private void setNetList(){
                // cleat both lists
                listSelectionView.getSourceItems().clear();
                listSelectionView.getTargetItems().clear();

                // fetch the net names for the protocol
                String protocol = runParameters.getProtocolTablePrefix();
                SQLiteUtils.openDB();
                ObservableList<String> netNames = FXCollections.observableArrayList(SQLiteUtils.getLogicNetNames(protocol));
                SQLiteUtils.closeDB();

                // set the listselectionview to these items
                listSelectionView.getSourceItems().addAll(netNames);
            }

            /**
             * validation for listselectionview is currently not supported, so instead we'll add
             * validation to a textfield which keeps track of the number of selected nets.
             * this should be > 0
             */
            private void addTargetListEventListener(){
                // create a ListChangeListener
                // if we observe a change in some Observable List, find the size of the list and set
                // the textfield to this size
                ListChangeListener listChangeListener = c -> textField.setText(String.valueOf(c.getList().size()));
                // add the listener to our observable targetitems list
                listSelectionView.getTargetItems().addListener(listChangeListener);
            }

            /**
             * check whether the net list has to be refreshed
             */
            private void setNets(){
                String workspace = runParameters.getWorkspaceFileName();
                if(!workspace.equalsIgnoreCase(curWorkspace)){
                    curWorkspace = workspace;
                    setNetList();
                }
            }

            /**
             * things to do when we enter the page
             * @param wizard    the wizard
             */
            @Override
            public void onEnteringPage(Wizard wizard) {
                // set the validator and the nets
                wizard.invalidProperty().unbind();
                wizard.invalidProperty().bind(validationSupport.invalidProperty());
                setNets();
            }

            /**
             * things to do when we leave the page
             * @param wizard    the wizard
             */
            @Override
            public void onExitingPage(Wizard wizard){
                // add the selected nets to the settings
                runParameters.setSelectedNets(listSelectionView.getTargetItems());
            }
        };
    }

    /**
     * create the summary wizard page
     * @return the page
     */
    private WizardPane createSummaryPage(){
        return new WizardPane(){
            {
                this.getStylesheets().clear();
                this.setPrefWidth(wizardWidth);
                this.setPrefHeight(wizardHeight);
                this.setHeaderText("Summary");
            }

            /**
             * generates a string of content
             * @return string representation of the content
             */
            private String generateContentText(){
                String content="Protocol: "+runParameters.getProtocolName()+"\n";
                content += "Version: "+runParameters.getProtocolVersion()+"\n";
                content += "Codebook type: "+runParameters.getCodebookType()+"\n";
                content += "\nNets:\n"+runParameters.getSelectedNets().stream().collect(Collectors.joining("\t"));
                return content;
            }

            /**
             * things to do when we enter the page
             * @param wizard    the wizard
             */
            @Override
            public void onEnteringPage(Wizard wizard) {
                wizard.invalidProperty().unbind();
                runParameters.loadProtocolInfo();
                this.setContentText(generateContentText());

            }
        };
    }

    /**
     * creates grid pane
     * @return grid pane
     */
    private GridPane createGridPane(){
        GridPane pageGrid = new GridPane();
        pageGrid.setVgap(10);
        pageGrid.setHgap(10);

        return pageGrid;
    }

    /**
     * creates standard textfield
     * @param id      id for the textfield
     * @param text    contents for the textfield
     * @return the new textfield
     */
    private TextField createTextField(String id, String text) {
        TextField textField = new TextField();
        textField.setId(id);
        textField.setText(text);
        GridPane.setHgrow(textField, Priority.ALWAYS);
        return textField;
    }

    /**
     * creates standard combobox
     * @param id    id for the textfield
     * @return the new combobox
     */
    private ComboBox<String> createComboBox(String id) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setId(id);
        GridPane.setHgrow(comboBox, Priority.ALWAYS);
        return comboBox;
    }

}