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
 * Factory to create the appropriate nodes
 */
public class NodeFactory {
    /**
     * checks the data and decides which type of node will be created
     * @param data    the data for the node
     * @return a new node of the appropriate type
     */
    public static Node createNode(String data){
        if (data.contains("form_part = 1")) {
            return new FormNode(data);
        } else if (data.contains("ntype = \"call\"")) {
            return new CallNode(data);
        } else if (data.contains("ntype = \"rule\"")) {
            return new RuleNode(data);
        } else if (data.contains("ntype = \"router\"")) {
            return new RouterNode(data);
        } else if (data.contains("ntype = \"process\"")) {
            return new ProcessNode(data);
        }
        return new NoTypeNode(data);
    }
}
