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
import com.acuity.visualisations.transform.rule.ValueRule;

public abstract class AbstractParser<E> {

    private Mapper mapper;
    private ParserRule parserRule;

    public AbstractParser() {
    }

    public AbstractParser(Mapper mapper) {
        this.mapper = mapper;
    }

    public AbstractParser(ParserRule parserRule) {
        this.parserRule = parserRule;
    }

    public AbstractParser(Mapper mapper, ParserRule parserRule) {
        this.parserRule = parserRule;
        this.mapper = mapper;
    }

    public E parse(String input) throws InvalidDataFormatException {
        String mapped = applyMapper(getMapper(), input);
        return convert(mapped);
    }

    protected final String applyMapper(Mapper mapper, String input) {
        if (mapper == null || mapper.getValue() == null || mapper.getValue().isEmpty()) {
            return input;
        }
        for (ValueRule rule : mapper.getValue()) {
            if (rule.getFrom().equals(input)) {
                return rule.getTo();
            }
        }
        return input;
    }

    protected abstract E convert(String input) throws InvalidDataFormatException;

    public Mapper getMapper() {
        return mapper;
    }

    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }

    public ParserRule getParserRule() {
        return parserRule;
    }

    public void setParserRule(ParserRule parserRule) {
        this.parserRule = parserRule;
    }

}
