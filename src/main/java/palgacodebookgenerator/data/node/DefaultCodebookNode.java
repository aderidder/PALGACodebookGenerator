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

import palgacodebookgenerator.codebook.CodebookItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Defaults for codebooknodes (FormNode and ProcessNode)
 */
abstract class DefaultCodebookNode extends DefaultNode implements CodebookNode {
    /**
     * constructor
     * @param data        data for the node
     * @param nodeType    type of the node
     */
    DefaultCodebookNode(String data, NodeTypeEnum nodeType) {
        super(data, nodeType);
    }

    /**
     * create a list of codebook items for this node
     * @return list of codebook items
     */
    @Override
    public List<CodebookItem> createCodebookItems() {
        // generate codebookitems for the multiparts component
        // this can lead to multiple codebook items
        List<CodebookItem> codebookItemList = multiPartsComponent.createCodebookItems();
        String rules = getRules();

        // for each of the codebook items, set the partial rules, id and node type
        for(CodebookItem codebookItem:codebookItemList){
            codebookItem.setPartialRules(rules);
            codebookItem.setId(id);
            codebookItem.setNtype(nodeType.toString());
        }

        return codebookItemList;
    }

    /**
     * looks at the parents nodes that contribute to the rule
     * @return string representation of the rule
     */
    private String getRules(){
        List<String> rule = new ArrayList<>();
        // for each contributing node, go there to fetch the rule to the current node
        for(RuleContributorNode node: prevRuleNodes){
            rule.add(node.getRules(id));
        }
        return rule.stream().collect(Collectors.joining(" OR "));
    }
}
