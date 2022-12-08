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

import palgacodebookgenerator.data.Protocol;

import java.io.File;
import java.util.List;

/**
 * class that stores everything required for a run
 */
class RunParameters {
    private String codebookType;

    private String outputDir = "";
    private String workspaceFileName = "";
    private String overwriteFileName = "";

    private boolean storeOptionsInSeparateSheets = false;

    private final Protocol protocol = new Protocol();

    RunParameters() {

    }

    /**
     * returns the protocol table prefix
     * @return the protocol table prefix
     */
    String getProtocolTablePrefix() {
        return protocol.getProtocolTablePrefix();
    }

    /**
     * returns the protocol name
     * @return the protocol name
     */
    String getProtocolName() {
        return protocol.getProtocolName();
    }

    /**
     * sets the protocol name
     * @param protocolTablePrefix    the name of the protocol
     */
    void setProtocolTablePrefix(String protocolTablePrefix) {
        protocolTablePrefix = protocolTablePrefix.substring(0, protocolTablePrefix.lastIndexOf("_")+1);
        protocol.setProtocolTablePrefix(protocolTablePrefix);
    }

    /**
     * get the nets that have been selected in the wizard
     * @return a list with the selected nets
     */
    List<String> getSelectedNets() {
        return protocol.getSelectedNets();
    }

    /**
     * set the selected nets to what has been selected in the wizard
     * @param selectedNets    list of the selected nets
     */
    void setSelectedNets(List<String> selectedNets) {
        protocol.setSelectedNets(selectedNets);
    }

    /**
     * get the complete filename of the database (the name seems to be workspace.db, hence the name of the function)
     * @return the filename
     */
    String getWorkspaceFileName() {
        return workspaceFileName;
    }

    /**
     * set the complete filename of the database (the name seems to be workspace.db, hence the name of the function)
     * @param workspaceFileName name of the workspace file
     */
    void setWorkspaceFileName(String workspaceFileName) {
        this.workspaceFileName = workspaceFileName;
    }

    /**
     * get the name of the overwrites file, which is used to overwrite concept captions
     * @return full name of the file
     */
    String getOverwriteFileName() {
        return overwriteFileName;
    }

    /**
     * set the name of the overwrites file, which is used to overwrite concept captions
     * @param overwriteFileName    full name of the file
     */
    void setOverwriteFileName(String overwriteFileName) {
        this.overwriteFileName = overwriteFileName;
    }

    /**
     * get the output directory
     * @return output directory
     */
    String getOutputDir() {
        return outputDir;
    }

    /**
     * set the output directory
     * @param outputDir    the output directory
     */
    void setOutputDir(String outputDir) {
        if(!outputDir.endsWith(File.separator)){
            outputDir+=File.separator;
        }
        this.outputDir = outputDir;
    }

    /**
     * get the type of codebook which was selected in the wizard. determines columns which will appear in the output
     * @return the selected type of codebook
     */
    String getCodebookType() {
        return codebookType;
    }

    /**
     * set the type of codebook selected in the wizard
     * @param codebookType    the selected type of codebook
     */
    void setCodebookType(String codebookType) {
        this.codebookType = codebookType;
    }

    /**
     * loads extra information about the protocol, such as the version
     */
    void loadProtocolInfo(){
        protocol.loadProtocolInfo();
    }

    /**
     * returns the version of the protocol
     * @return version of the protocol
     */
    String getProtocolVersion(){
        return protocol.getVersion();
    }

    /**
     * set whether the concept options (e.g. male, female) should be written to separate sheets (e.g. gender)
     * @param value    true/false
     */
    void setStoreOptionsInSeparateSheets(Boolean value){
        storeOptionsInSeparateSheets = value;
    }

    /**
     * return whether the concept options (e.g. male, female) should be written to separate sheets (e.g. gender)
     * @return true/false
     */
    boolean getStoreOptionsInSeparateSheets(){
        return storeOptionsInSeparateSheets;
    }

    /**
     * returns the protocol object, which contains the data etc.
     * @return the protocol object
     */
    public Protocol getProtocol() {
        return protocol;
    }
}
