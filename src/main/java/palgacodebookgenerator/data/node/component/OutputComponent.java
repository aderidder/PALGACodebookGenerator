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

import palgacodebookgenerator.utils.ParseUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Output part of a Node
 * an output component can contain multiple output values, as one Node can lead
 * to multiple targets
 */
public class OutputComponent implements Component{
    private static final Pattern idPattern = ParseUtils.getStringPattern("id");
    private static final Pattern targetPattern = ParseUtils.getIntPattern("target");
    private static final Pattern can_stopPattern = ParseUtils.getIntPattern("can_stop");

    private List<OutputValues> outputValues = new ArrayList<>();

    /**
     * constructor
     */
    public OutputComponent(){

    }

    // Output looks something like this
    //	outputs = {
    //		{
    //			id = "true",
    //			target = 5140
    //		},
    //		{
    //			id = "false",
    //			target = 11000
    //		}
    //	}

    /**
     * add data to this component
     * @param data    the data to parse
     */
    @Override
    public void addComponentData(String data){
        // split the data as it can contain multiple outputs
        String [] splitData = data.split("}");
        for(String item:splitData){
            if(!item.trim().equalsIgnoreCase("")) {
                // fetch the id, target and can_stop from the data
                String id = ParseUtils.getValue(item, idPattern);
                String target = ParseUtils.getValue(item, targetPattern);
                String can_stop = ParseUtils.getValue(item, can_stopPattern);
                // if a value exists for one of them, create an outputvalues object and store it
                if(!id.equalsIgnoreCase("") || !target.equalsIgnoreCase("") || !can_stop.equalsIgnoreCase("")) {
                    OutputValues values = new OutputValues(id, target, can_stop);
                    outputValues.add(values);
                }
            }
        }
    }

    /**
     * returns a list with all the output targets
     * @return a list with all the output targets
     */
    public List<String> getOutputValues(){
        return outputValues.stream().map(OutputValues::getTarget).collect(Collectors.toList());
    }

    /**
     * return a list of "id" values (which is a bad name as it has values such as "true" or "false"),
     * when the id parameter matches the output target (which should have been the id...)
     * @param id    an identifier for a node, which will be matched to the "target" variable
     * @return list with criteria that need to be met to get to the target such as true or false
     */
    public List<String> getMatchedId(String id){
        List<String> matches = new ArrayList<>();
        for(OutputValues outputValue:outputValues){
            if(outputValue.getTarget().equalsIgnoreCase(id)){
                matches.add(outputValue.getId());
            }
        }
        return matches;
    }

    /**
     * class for output values
     */
    private class OutputValues {
        private String id="";
        private String target="";
        private String can_stop="";

        /**
         * constructor
         * @param id          id, which is not really an id but more of a criterion (e.g. true, false, ...)
         * @param target      the target id, a number whence to go if the criterion is met
         * @param can_stop    whether this node is an end node
         */
        OutputValues(String id, String target, String can_stop){
            this.id = id;
            this.target = target;
            this.can_stop  = can_stop;
        }

        /**
         * returns the "id" of this output
         * @return the "id" of this output
         */
        String getId() {
            return id;
        }

        /**
         * returns the target of this output
         * @return the target of this output
         */
        String getTarget() {
            return target;
        }

        /**
         * returns the can_stop value of this output
         * @return the can_stop value of this output
         */
        public String getCan_stop() {
            return can_stop;
        }
    }
}
