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

package palgacodebookgenerator.data;

import palgacodebookgenerator.codebook.CodebookItem;
import palgacodebookgenerator.data.net.Net;
import palgacodebookgenerator.utils.ParseUtils;
import palgacodebookgenerator.utils.SQLiteUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * PALGA Protocol
 */
public class Protocol {
    // List with nets selected in the wizard
    private List<String> selectedNets = new ArrayList<>();
    private String protocolTablePrefix;
    private String protocolName;

    private Map<String, List<CodebookItem>> codebookItemMap = new TreeMap<>();

    private ProtocolInfo protocolInfo;

    public Protocol(){

    }

    /**
     * load additional information about the selected protocol
     */
    public void loadProtocolInfo(){
        protocolInfo = new ProtocolInfo();
    }

    /**
     * returns the version of the selected protocol
     * @return the version of the selected protocol
     */
    public String getVersion(){
        return protocolInfo.getVersion();
    }

    /**
     * returns the small version of the selected protocol
     * @return the small version of the selected protocol
     */
    public String getSmallVersion(){
        return protocolInfo.getSmallVersion();
    }

    /**
     * set the table prefix of the protocol as well as the name
     * @param protocolTablePrefix    the name of the protocol
     */
    public void setProtocolTablePrefix(String protocolTablePrefix){
        this.protocolTablePrefix = protocolTablePrefix;
        protocolName = protocolTablePrefix.substring(0, protocolTablePrefix.length()-1);
    }

    /**
     * returns the name of the protocol
     * @return the name of the protocol
     */
    public String getProtocolTablePrefix(){
        return protocolTablePrefix;
    }

    /**
     * returns a list of the selected nets
     * @return a list of the selected nets
     */
    public List<String> getSelectedNets() {
        return selectedNets;
    }

    /**
     * set the list of selected nets
     * @param selectedNets    the list of the selected nets
     */
    public void setSelectedNets(List<String> selectedNets) {
        this.selectedNets = selectedNets;
    }


    /**
     * returns the codebookItems map. It the map is empty, fills it first
     * @return the codebookItems map
     */
    public Map<String, List<CodebookItem>> getCodebookItems(){
        if (codebookItemMap.size()==0) generateCodebookItems();
        return codebookItemMap;
    }

    /**
     * for each net, adds codebookItems to the codebookItemMap
     */
    private void generateCodebookItems(){
        List<Net> netList = createNetList();
        // for each net add its items to the codebookItem Map
        for(Net net:netList){
            net.addCodebookItems(codebookItemMap);
        }
    }

    /**
     * creates Net objects for each Net selected net and return them as a list
     * @return list with net objects
     */
    private List<Net> createNetList(){
        SQLiteUtils.openDB();
        // List with data of the selected nets
        List<Net> netList = new ArrayList<>();

        // change the list with the selected nets into a string usable by sql and fetch the data from the database
        String selectedNetsString = selectedNets.stream().map(t-> "'"+t+"'").collect(Collectors.joining(","));
        List<String> dbNetList = SQLiteUtils.getLogicNetData(selectedNetsString);

        // create nets for the net-data found in the database and add to the netlist
        for(String netString:dbNetList){
            Net net = new Net(netString);
            netList.add(net);
        }

        SQLiteUtils.closeDB();
        return netList;
    }

    /**
     * returns the name of the protocol
     * @return the name of the protocol
     */
    public String getProtocolName() {
        return protocolName;
    }

    /**
     * additional protocol info
     */
    static class ProtocolInfo {
        private final Pattern versionPattern = ParseUtils.getStringPattern("version");
        private String version;
        private String smallVersion;

        ProtocolInfo(){
            setup();
        }

        /**
         * setup for the ProtocolInfo. Retrieves the data from the database
         * based on the projectName and then extracts the version information from it
         */
        private void setup(){
            SQLiteUtils.openDB();
            String data = SQLiteUtils.doTableSettingsQuery();
            version = ParseUtils.getValue(data, versionPattern);
            setSmallVersion();
            SQLiteUtils.closeDB();
        }

        /**
         * attempts to create a smaller version of the version string. E.g. 23.34235 becomes 23
         */
        private void setSmallVersion(){
            int lastDotIndex = version.lastIndexOf(".");
            if(lastDotIndex>0){
                smallVersion = version.substring(lastDotIndex+1);
            }
            else {
                smallVersion = version;
            }
        }

        /**
         * returns the protocol version
         * @return the protocol version
         */
        public String getVersion() {
            return version;
        }

        /**
         * returns the small version
         * @return the small version
         */
        String getSmallVersion(){
            return smallVersion;
        }
    }
}
