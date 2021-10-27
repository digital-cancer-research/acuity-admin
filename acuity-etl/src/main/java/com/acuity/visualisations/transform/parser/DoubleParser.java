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

package com.acuity.visualisations.transform.parser;

import com.acuity.visualisations.exception.InvalidDataFormatException;
import com.acuity.visualisations.transform.rule.Mapper;
import com.acuity.visualisations.transform.rule.ParserRule;

import static com.acuity.visualisations.data.util.Util.isEmpty;

public class DoubleParser extends AbstractParser<Double> {

    public DoubleParser() {
        super();
    }

    public DoubleParser(Mapper mapper) {
        super(mapper);
    }

    public DoubleParser(ParserRule parserRule) {
        super(parserRule);
    }

    public DoubleParser(Mapper mapper, ParserRule parserRule) {
        super(mapper, parserRule);
    }

    @Override
    protected Double convert(String input) throws InvalidDataFormatException {
        if (isEmpty(input)) {
            return null;
        }

        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            throw new InvalidDataFormatException("Unable to parse following value: " + input, e);
        }
    }

}
