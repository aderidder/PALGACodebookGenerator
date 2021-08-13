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

package palgacodebookgenerator.data.net;

import palgacodebookgenerator.utils.ParseUtils;

import java.util.regex.Pattern;

/**
 * class for net information: first entry in a Net is special and contains net information
 */
class NetInformation {
    private static final Pattern namePattern = ParseUtils.getStringPattern("name");
    private static final Pattern versionPattern = ParseUtils.getIntPattern("version");
    private static final Pattern stampPattern = ParseUtils.getIntPattern("stamp");

    private String name="";
    private String version="";
    private String stamp="";

    /**
     * constructor
     * @param data    NET data which contains the information we need
     */
    NetInformation(String data){
        addNodeData(data);
    }

    /**
     * parse the add the data to set the values of interest
     * @param data    NET data which contains the information we need
     */
    private void addNodeData(String data) {
        // Apparently this part can also contain a datamodel element, which also has a name and a version
        // Remove it from the String if it's there.
        String datamodel = ParseUtils.getElementData(data, "datamodel");
        if(!datamodel.equalsIgnoreCase("")) {
            data = data.replace(datamodel, "datamodel=\n");
        }
        name = ParseUtils.getValue(data, namePattern);
        version = ParseUtils.getValue(data, versionPattern);
        stamp = ParseUtils.getValue(data, stampPattern);
    }

    /**
     * returns the name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * returns the version of the net
     * @return the version of the net
     */
    public String getVersion() {
        return version;
    }
}
