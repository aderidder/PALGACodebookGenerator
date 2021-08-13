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

package palgacodebookgenerator.data.node;

/**
 * Node for which no type is specified
 */
public class NoTypeNode extends DefaultNode{
    /**
     * constructor
     * @param data    data string for the node
     */
    NoTypeNode(String data) {
        super(data, NodeTypeEnum.NOTYPE);
    }

    /**
     * node specific data, which cannot be handled by the default nodes
     * @param data    data string for the node
     */
    @Override
    void addNodeSpecificData(String data) {

    }

    /**
     * returns whether this type of node is a codebook node
     * @return false
     */
    @Override
    public boolean isCodebookNode() {
        // this is just a check to see whether this actually occurs... If it does, maybe it could be a codebook node?
        if(multiPartsComponent!=null && multiPartsComponent.validCodebookComponent(nodeType)){
            System.err.println("Not added to the codebook, as it's a NoTypeNode. But does have a path and log is set to 1. Id="+this.getId());
        }
        return false;
    }
}
