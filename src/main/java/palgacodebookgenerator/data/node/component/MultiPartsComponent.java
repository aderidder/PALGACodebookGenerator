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

package palgacodebookgenerator.data.node.component;

import palgacodebookgenerator.codebook.CodebookItem;
import palgacodebookgenerator.data.node.NodeTypeEnum;
import palgacodebookgenerator.utils.ParseUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * The parts entry in a Node sometimes contains multiple entries, hence we need a MultiPartsComponent
 */
public class MultiPartsComponent implements Component {
    private List<PartsComponent> partsComponentList = new ArrayList<>();

    /**
     * add data to this component
     * @param data    contains the data
     */
    @Override
    public void addComponentData(String data) {
        // get a List with an entry for each part
        List<String> splitData = ParseUtils.partsSplitter(data);

        // for each entry, create a PartsComponent
        for (String item : splitData) {
            if (!item.trim().equalsIgnoreCase("")) {
                PartsComponent partsComponent = new PartsComponent();
                partsComponent.addComponentData(item);
                partsComponentList.add(partsComponent);
            }
        }
    }

    /**
     * check whether the multiparts component meets the requirements to be a codebook item
     * @param nodeType    the type of node
     * @return true/false
     */
    public boolean validCodebookComponent(NodeTypeEnum nodeType){
        boolean codebookComponent = false;
        // NOTYPE and PROCESS nodes require a log and a path
        if(nodeType == NodeTypeEnum.NOTYPE || nodeType == NodeTypeEnum.PROCESS){
            for (PartsComponent partsComponent:partsComponentList){
                if(partsComponent.hasLog() && partsComponent.hasPath()){
                    partsComponent.setCodebookComponent();
                    codebookComponent = true;
                }
            }
        }
        // FORM nodes require a path
        else if(nodeType == NodeTypeEnum.FORM){
            for (PartsComponent partsComponent:partsComponentList){
                if(partsComponent.hasPath()){
                    partsComponent.setCodebookComponent();
                    codebookComponent = true;
                }
            }
        }
        return codebookComponent;
    }

    /**
     * create codebookItems for this multipart component
     * multipart components can consist of multiple part components; each such part component
     * may be a codebookcomponent.
     * @return a list of the generated codebook items
     */
    public List<CodebookItem> createCodebookItems(){
        List<CodebookItem> codebookItemList = new ArrayList<>();
        for(PartsComponent partsComponent:partsComponentList){
            // each partscomponent that is a codebook component becomes a codebook item
            if(partsComponent.isCodebookComponent()){
                CodebookItem codebookItem = partsComponent.generateCodebookItem();
                codebookItemList.add(codebookItem);
            }
        }
        return codebookItemList;
    }

    /**
     * gets a list of the partialrules for each part component of the multipart component
     * @return list of partial rules
     */
    public List<String> getPartialRules(){
        List<String> rules = new ArrayList<>();
        for(PartsComponent partsComponent:partsComponentList){
            String rule = partsComponent.getPartialRule();
            if(!rule.equalsIgnoreCase("")){
                rules.add(rule);
            }
        }
        return rules;
    }

    /**
     * gets a list of the rules for each part component of the multipart component
     * @return list of rules
     */
    public List<String> getRules(){
        List<String> rules = new ArrayList<>();
        for(PartsComponent partsComponent:partsComponentList){
            String rule = partsComponent.getRule();
            if(!rule.equalsIgnoreCase("")){
                rules.add(rule);
            }
        }
        return rules;
    }
}
