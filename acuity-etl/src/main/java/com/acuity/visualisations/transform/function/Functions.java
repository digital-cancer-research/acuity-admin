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

package com.acuity.visualisations.transform.function;

import java.util.HashMap;
import java.util.Map;

/**
 * Functions registry
 */
public final class Functions {
    private static Map<String, AbstractFunction> aggregators = new HashMap<>();

    static {
        aggregators.put("DateAssembler", new DateAssembler());
        aggregators.put("Concatenate", new Concatenate());
        aggregators.put("DateAssemblerDefaultTime", new DateAssemblerDefaultTime());
        aggregators.put("MaxValue", new MaxValue());
        aggregators.put("ObjectArray", new ObjectArray());
    }

    private Functions() {
    }

    public static AbstractFunction getAggregator(String name) {
        return aggregators.get(name);
    }
}
