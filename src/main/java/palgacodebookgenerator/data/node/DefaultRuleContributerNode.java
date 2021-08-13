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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Default Node for nodes that contribute rules
 */
abstract public class DefaultRuleContributerNode extends DefaultNode implements RuleContributorNode{
    DefaultRuleContributerNode(String data, NodeTypeEnum nodeType) {
        super(data, nodeType);
    }

    /**
     * returns the rules that lead from this node to the child, merged with the rules of the
     * parents that lead to this node
     * @param childId    the target node
     * @return a string with the rule
     */
    @Override
    public String getRules(String childId) {
        String myRule = getMyRule(childId);
        List<String> parentRules = getParentRules();
        if(parentRules.size()>0) {
            return parentRules.stream().collect(Collectors.joining(" OR ")) + " AND " + myRule;
        }
        return myRule;
    }

    /**
     * get a list of the rules that lead to this node
     * @return list of the rules that lead to this node
     */
    private List<String> getParentRules(){
        List<String> parentRules = new ArrayList<>();
        for(RuleContributorNode node:prevRuleNodes){
            parentRules.add(node.getRules(id));
        }
        return parentRules;
    }

    /**
     * get a string representation of the rule for this node, leading to the child
     * @param childId    target node
     * @return string of the rule
     */
    abstract String getMyRule(String childId);
}
