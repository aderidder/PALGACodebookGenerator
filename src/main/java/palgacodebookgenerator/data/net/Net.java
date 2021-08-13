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

import palgacodebookgenerator.codebook.CodebookItem;
import palgacodebookgenerator.data.node.CodebookNode;
import palgacodebookgenerator.data.node.Node;
import palgacodebookgenerator.data.node.NodeFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * class that keeps track of a PALGA NET
 */
public class Net {
    private static final Logger logger = LogManager.getLogger(Net.class.getName());
    private Map<String, Node> nodeMap = new HashMap<>();
    private List<CodebookNode> codebookNodes = new ArrayList<>();
    private Node startNode;

    private NetInformation netInformation;

    /**
     * constructor
     * @param data    the data for the NET
     */
    public Net(String data){
        parseNet(data);
    }

    /**
     * parse a net, transforming it into Nodes
     * @param data    the data for the NET
     */
    private void parseNet(String data){
        // split the data, which gives us an array with a node at each position
        String [] splitString = data.split("\n\n");
        netInformation = new NetInformation(splitString[0]);

        // create a node for each entry
        for(int i=1; i<splitString.length; i++) {
            Node node = NodeFactory.createNode(splitString[i]);
            if(node.isStartNode()){
                startNode = node;
            }

            // store the node in a map
            String key = node.getId();
            if(!nodeMap.containsKey(key)){
                nodeMap.put(key, node);
            }
            else{
                logger.log(Level.INFO, "Net: "+netInformation.getName()+"\tdouble id found: "+key);
            }
        }
        // connect the nodes, which we need to be able to generate rules
        if(startNode!=null) {
            connectNodes(startNode);
        }
    }

    /**
     * find all the nodes that are reachable from the startNode. Others are in a side net and
     * can be ignored (discussed with PALGA)
     * recursive function
     * determine whether the nodes are codebook nodes
     * add parents, which we need to add the rule information to the reports
     * @param node    the current node
     */
    private void connectNodes(Node node){
        node.setConnected();
        // check whether this node is a codebooknode and if it is, add it to the codebooknode list
        if(node.isCodebookNode()){
            codebookNodes.add((CodebookNode) node);
        }

        // find the target nodes of our current node
        List<String> outputTargets = node.getOutputTargets();
        for(String target:outputTargets){
            if(!target.equalsIgnoreCase("")) {
                // if there is a target node, fetch it from the nodemap
                Node targetNode = nodeMap.get(target);
                // if our current node is a node that may contribute Rule information, add it to the targetNode
                if (node.isRuleContributorNode()) {
                    targetNode.addPreviousRuleNode(node);
                }
                // if we haven't visited the target node yet, go there
                if(!targetNode.isConnected()) {
                    connectNodes(targetNode);
                }
            }
        }
    }

    /**
     * add data to the itemMap
     * the map contains the path (variable name) as key and a list of codebook items as values
     * basically it stores the codebook items with the same path names together in a list.
     * @param itemMap    map further filled with paths and their codebook items
     */
    public void addCodebookItems(Map<String, List<CodebookItem>> itemMap){
        // for each codebook node in this net
        for(CodebookNode codebookNode:codebookNodes){
            // get a list which contains codebookitems for this node. Has to be a list, as a Parts entry can contain multiple paths (concept names)
            List<CodebookItem> codebookItemList = codebookNode.createCodebookItems();
            // for each entry in the list find the name and add the entry to the map at that name, which will allow us to merge later if we want
            for(CodebookItem codebookItem:codebookItemList){
                // store the name of the net in the codebookItem
                codebookItem.setNet(netInformation.getName());

                // add the codecookitem to the Map
                String path = codebookItem.getPath();
                if(!itemMap.containsKey(path)){
                    itemMap.put(path, new ArrayList<>());
                }
                List<CodebookItem> tmpList = itemMap.get(path);
                tmpList.add(codebookItem);
            }
        }
    }
}
