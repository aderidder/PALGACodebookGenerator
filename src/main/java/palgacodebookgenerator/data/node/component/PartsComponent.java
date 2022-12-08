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
import palgacodebookgenerator.utils.ParseUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The parts component class stores the Node's parts information
 */
class PartsComponent implements Component{
    private static final Pattern logPattern = ParseUtils.getStringPattern("log");
    private static final Pattern namePattern = ParseUtils.getStringPattern("_name");
    private static final Pattern pathPattern = ParseUtils.getStringPattern("path");
    private static final Pattern data_typePattern = ParseUtils.getStringPattern("data_type");
    private static final Pattern captionPattern = ParseUtils.getStringPattern("caption");
    private static final Pattern rulePattern = ParseUtils.getStringPattern("rule");

    private static final Pattern elementOperatorPattern = ParseUtils.getStringPattern("elementOperator");
    private static final Pattern operatorPattern = ParseUtils.getStringPattern("operator");
    private static final Pattern testPattern = ParseUtils.getStringPattern("test");
    private static final Pattern referencePattern = ParseUtils.getStringPattern("reference");

    private static final Pattern valuePattern = ParseUtils.getStringPattern("value");

    private static final Pattern messagePattern = ParseUtils.getStringPattern("message");
    private static final Pattern typePattern = ParseUtils.getStringPattern("type");

    private final List<String> optionValues = new ArrayList<>();

    // rule parts decide which node which Node one can travel to
    // e.g. if (value == 3)==true --> goto node x, otherwise goto node y
    private final List<RuleParts> rulePartsValues = new ArrayList<>();

    // validation rules decide e.g. whether the field is mandatory, or whether a value
    // should be between 0 and 10
    private final List<ValidationRules> validationRulesValues = new ArrayList<>();

    private String caption="";
    private String path="";
    private String data_type="";
    private String _name="";
    private String log="";
    private String rule="";

    private boolean isCodebookComponent = false;

    PartsComponent(){
    }

    /**
     * returns whether the parts component is a codebook component
     * @return true/false
     */
    boolean isCodebookComponent(){
        return isCodebookComponent;
    }

    /**
     * sets this component as a codebook component
     */
    void setCodebookComponent(){
        isCodebookComponent = true;
    }

    /**
     * returns whether this component's path variable has a value
     * @return true/false
     */
    boolean hasPath(){
        return !path.equalsIgnoreCase("");
    }

    /**
     * returns whether this component's log variable has a value
     * @return true/false
     */
    boolean hasLog(){
        return !log.equalsIgnoreCase("");
    }

    /**
     * returns a string representation of the partial rule
     * @return a string representation of the partial rule
     */
    String getPartialRule(){
        return rulePartsValues.stream().map(RuleParts::getRule).collect(Collectors.joining(" "));
    }

    /**
     * return the stored rule variable
     * this is generally used by switch nodes, which leads to a parts component
     * with e.g. _name = "switch", default = "default", rule = "Lokalisatie"
     * the rule is then used together with the output id (which is a text, e.g.
     * id = "afstand in centimeter") to create a value which defines when a field
     * is entered (similar to the partial rules).
     * @return rule string
     */
    String getRule(){
        return rule;
    }

    /**
     * create a new codebookitem for this component
     * @return the newly created codebook item
     */
    CodebookItem generateCodebookItem(){
        List<String> validationRules = validationRulesValues.stream().map(t->t.getValidationRule()).collect(Collectors.toList());
        CodebookItem codebookItem = new CodebookItem(path, caption, data_type, _name, optionValues, validationRules);
        codebookItem.setLog(log);
        return codebookItem;
    }

    /**
     * add the data to this component
     * @param data    the data to add
     */
    @Override
    public void addComponentData(String data){
        log = ParseUtils.getValue(data, logPattern);
        _name = ParseUtils.getValue(data, namePattern);
        caption = ParseUtils.getValue(data, captionPattern);
        path = ParseUtils.getValue(data, pathPattern);
        data_type = ParseUtils.getValue(data, data_typePattern);
        rule = ParseUtils.getValue(data, rulePattern);
        addOptions(data);
        addRuleparts(data);
        addValidationRules(data);
    }

    // options look something like
    //			choices = {
    //				{
    //					caption = "leeg&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;conform",
    //					column = "1",
    //					p = "",
    //					rule = "$(temp.IdWork)>1 and '$(temp.MacroConfromSampleInput)'==''",
    //					value = "nee",
    //					weight = ""
    //				},
    //				{
    //					etc
    //				}
    //       }
    /**
     * add the choices / options values
     * @param data    the data which to parse
     */
    private void addOptions(String data){
        data = ParseUtils.getElementData(data,"choices ");
        if(!data.equalsIgnoreCase("")) {
            String[] splitData = data.split("}");
            for (String item : splitData) {
                if (!item.trim().equalsIgnoreCase("")) {
                    String value = ParseUtils.getValue(item, valuePattern);
                    if (!value.equalsIgnoreCase("")) {
                        optionValues.add(value);
                    }
                }
            }
        }
    }

    // Ruleparts look something like:
    // ruleparts =  {
    //                  {
    //                      html = "",
    //                      id = 0,
    //                      leftOrder = {},
    //                      operator = "equals",
    //                      reference = "Typetumor",
    //                      rightOrder = {},
    //                      test = "NET/NEC",
    //                      type = "text"
    //                  },
    //                  {
    //                      etc.
    //                  }
    //              }
    //
    /**
     * add the rule parts
     * @param data    the data which to parse
     */
    private void addRuleparts(String data){
        data = ParseUtils.getElementData(data,"ruleparts ");
        if(!data.equalsIgnoreCase("")) {
            String[] splitData = data.split(",\\s*\\{");
            for (String item : splitData) {
                if (!item.trim().equalsIgnoreCase("")) {
                    String elementOperator = ParseUtils.getValue(item, elementOperatorPattern);
                    String operator = ParseUtils.getValue(item, operatorPattern);
                    String test = ParseUtils.getValue(item, testPattern);
                    String reference = ParseUtils.getValue(item, referencePattern);
                    if (!reference.equalsIgnoreCase("")) {
                        RuleParts ruleParts = new RuleParts(elementOperator, operator, test, reference);
                        rulePartsValues.add(ruleParts);
                    }
                }
            }
        }
    }

    // validation rules look like this
    //			validation_rules = {
    //				{
    //					type = "mandatory"
    //				},
    //				{
    //					message = "Dit mag alleen een numerieke waarde zijn",
    //					type = "numeric"
    //				}
    //			},
    /**
     * add the validation rules
     * @param data    the data which to parse
     */
    private void addValidationRules(String data){
        data = ParseUtils.getElementData(data, "validation_rules ");
        if(!data.equalsIgnoreCase("")) {
            // split the data by looking into separate rules
            // more complicated split, as the validation rules themselves can also contain {}
            String[] splitData = data.split(",\\s*\\{");
            for (String item : splitData) {
                if (!item.trim().equalsIgnoreCase("")) {
                    String type = ParseUtils.getValue(item, typePattern);
                    String messsage = ParseUtils.getValue(item, messagePattern);

                    if (!messsage.equalsIgnoreCase("") || !type.equalsIgnoreCase("")) {
                        ValidationRules validationRules = new ValidationRules(type, messsage);
                        validationRulesValues.add(validationRules);
                    }
                }
            }
        }
    }


    private static class RuleParts{
        private String elementOperator="";
        private String operator="";
        private String test="";
        private String reference="";

        RuleParts(String elementOperator, String caption, String value, String reference){
            this.elementOperator = elementOperator;
            this.operator = caption;
            this.test = value;
            this.reference = reference;
        }

        /**
         * returns a string representation of the rule parts
         * @return a string representation of the rule parts
         */
        public String getRule(){
            return (elementOperator+" "+reference+" "+operator+" "+test).trim();
        }
    }

    private static class ValidationRules{
        private String type="";
        private String message="";

        ValidationRules(String type, String message){
            this.message = message;
            this.type = type;
        }

        /**
         * returns a string representation of the validation rule
         * @return a string representation of the validation rule
         */
        String getValidationRule(){
            if(message.equalsIgnoreCase("") && !type.equalsIgnoreCase("")){
                return type;
            }
            return message;
        }
    }
}
