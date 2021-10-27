/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acuity.visualisations.util;

import com.acuity.visualisations.model.aml.AlgorithmMetadata;
import com.acuity.visualisations.model.aml.qtinterval.QtInput;
import com.acuity.visualisations.model.aml.qtinterval.QtOutput;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class QtJsonUtil {

    private static ObjectMapper mapper = new ObjectMapper();

    private QtJsonUtil() {
    }

    public static String convertInputToJson(List<QtInput> input) {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode inputs = mapper.createObjectNode();
        ObjectNode qtIntervals = mapper.createObjectNode();

        qtIntervals.set("ColumnNames", getArrayColumnJsonNode());
        qtIntervals.set("Values", getArrayDataJsonNode(input));

        inputs.set("QtIntervalAlgorithmInput", qtIntervals);

        root.set("Inputs", inputs);
        root.set("GlobalParameters", mapper.createObjectNode());

        return root.toString();
    }

    private static ArrayNode getArrayDataJsonNode(List<QtInput> input) {
        ArrayNode values = mapper.createArrayNode();
        input.forEach(in -> {
            ArrayNode value = mapper.createArrayNode();
            value.add(in.getStudyId());
            value.add(in.getSubject());
            value.add(in.getEcgId());
            value.add(in.getQtInterval());
            value.add(in.getConmed());
            value.add(in.getDiarrhoea());
            value.add(in.getMagnesium());
            value.add(in.getMagnesiumUnit());
            value.add(in.getPotassium());
            value.add(in.getPotassiumUnit());
            values.add(value);
        });
        return values;
    }

    private static ArrayNode getArrayColumnJsonNode() {
        ArrayNode column = mapper.createArrayNode();
        column.add("studyId");
        column.add("subjectId");
        column.add("ecgId");
        column.add("qtInterval");
        column.add("conmed");
        column.add("diarrhoea");
        column.add("magnesium");
        column.add("unitMg");
        column.add("potassium");
        column.add("unitK");
        return column;
    }

    public static List<QtOutput> extractAlgorithmResult(String result) throws IOException {
        List<QtOutput> output = new ArrayList<>();
        JsonNode node = mapper.readTree(result);
        ArrayNode values = (ArrayNode) node.path("Results").path("QtIntervalAlgorithmOut").path("value").path("Values");
        values.elements().forEachRemaining(value ->
                output.add(new QtOutput(noQuotes(value.get(0).toString()), noQuotes(value.get(1).toString()))));
        return output;
    }

    public static AlgorithmMetadata extractAlgorithmMetadata(String result) throws IOException {
        JsonNode node = mapper.readTree(result);
        ArrayNode values = (ArrayNode) node.path("Results").path("QtIntervalAlgorithmInfo").path("value").path("Values");
        return AlgorithmMetadata.builder()
                .name(noQuotes(values.get(0).get(0).toString()))
                .version(noQuotes(values.get(0).get(1).toString()))
                .build();
    }

    private static  String noQuotes(String text) {
        return text.replace("\"", "");
    }

}


