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

import java.util.List;

/**
 * Node interface
 * Implemented by DefaultNode
 */
public interface Node {
    /**
     * returns the id of a node
     * @return the id of a node
     */
    String getId();

    /**
     * returns whether the node is a start node
     * @return whether the node is a start node
     */
    boolean isStartNode();

    /**
     * sets the node as connected
     */
    void setConnected();

    /**
     * returns whether the node is connected
     * @return true/false
     */
    boolean isConnected();

    /**
     * returns a list of the output values which determines the targets of the current node
     * @return a list of output values
     */
    List<String> getOutputTargets();

    /**
     * adds a node as a parent node
     * @param node    the parent node
     */
    void addPreviousRuleNode(Node node);

    /**
     * returns whether the node is a codebook node
     * @return true/false
     */
    boolean isCodebookNode();

    /**
     * returns whether the node is one that can contribute rules
     * @return true/false
     */
    boolean isRuleContributorNode();

    /**
     * returns the node type
     * @return the node type
     */
    NodeTypeEnum getNodeType();

}
