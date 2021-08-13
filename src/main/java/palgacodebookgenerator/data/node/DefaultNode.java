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

import palgacodebookgenerator.data.node.component.MultiPartsComponent;
import palgacodebookgenerator.data.node.component.OutputComponent;
import palgacodebookgenerator.utils.ParseUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Default Node which attempts to implement as much of the shared node stuff as possible
 */
abstract class DefaultNode implements Node {
    private static final Pattern idPattern = ParseUtils.getIntPattern("id");
    private static final Pattern is_silentPattern = ParseUtils.getIntPattern("is_silent");
    private static final Pattern can_startPattern = ParseUtils.getIntPattern("can_start");

    private boolean isConnected=false;

    private String can_start="";

    List<RuleContributorNode> prevRuleNodes = new ArrayList<>();

    NodeTypeEnum nodeType;
    String id="";
    String is_silent="";

    OutputComponent outputComponent;
    MultiPartsComponent multiPartsComponent;

    /**
     * constructor
     * @param data        the data for this node
     * @param nodeType    the type of node
     */
    DefaultNode(String data, NodeTypeEnum nodeType){
        this.nodeType = nodeType;
        addNodeData(data);
    }

    /**
     * store the standard variables for the nodes
     * @param data    the data for this node
     */
    private void addNodeData(String data) {
        id = ParseUtils.getValue(data, idPattern);
        is_silent = ParseUtils.getValue(data, is_silentPattern);
        can_start = ParseUtils.getValue(data, can_startPattern);

        addOutputComponent(data);
        addMultiPartsComponent(data);
        addNodeSpecificData(data);
    }

    /**
     * allow nodes to also add specific data
     * @param data    the data for this node
     */
    abstract void addNodeSpecificData(String data);

    /**
     * adds the output component to this node
     * @param data    the data for this node
     */
    private void addOutputComponent(String data){
        String outputString = ParseUtils.getElementData(data,"outputs ");
        if(!outputString.equalsIgnoreCase("")){
            outputComponent = new OutputComponent();
            outputComponent.addComponentData(outputString);
        }
    }

    /**
     * adds the multiparts component to this node
     * @param data    the data for this node
     */
    private void addMultiPartsComponent(String data){
        String partsString = ParseUtils.getElementData(data,"parts ");
        if(!partsString.equalsIgnoreCase("")){
            multiPartsComponent = new MultiPartsComponent();
            multiPartsComponent.addComponentData(partsString);
        }
    }

    /**
     * returns the id of a node
     * @return the id of a node
     */
    @Override
    public String getId(){
        return id;
    }

    /**
     * returns whether the node is a start node
     * @return whether the node is a start node
     */
    @Override
    public boolean isStartNode(){
        return can_start.equalsIgnoreCase("1");
    }

    /**
     * returns a list of the output values which determines the targets of the current node
     * @return a list of output values
     */
    @Override
    public List<String> getOutputTargets(){
        return outputComponent.getOutputValues();
    }

    /**
     * sets the node as connected
     */
    @Override
    public void setConnected(){
        isConnected = true;
    }

    /**
     * adds a node as a parent node
     * @param node    the parent node
     */
    @Override
    public void addPreviousRuleNode(Node node){
        prevRuleNodes.add((RuleContributorNode) node);
    }

    /**
     * returns the node type
     * @return the node type
     */
    @Override
    public NodeTypeEnum getNodeType(){
        return nodeType;
    }

    /**
     * returns whether the node is one that can contribute rules
     * @return true/false
     */
    @Override
    public boolean isRuleContributorNode(){
        return nodeType== NodeTypeEnum.RULE || nodeType== NodeTypeEnum.ROUTER;
    }

    /**
     * returns whether the node is connected
     * @return true/false
     */
    @Override
    public boolean isConnected(){
        return isConnected;
    }
}
