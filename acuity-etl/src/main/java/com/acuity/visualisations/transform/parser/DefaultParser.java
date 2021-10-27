package com.acuity.visualisations.transform.parser;

import com.acuity.visualisations.transform.rule.Mapper;
import com.acuity.visualisations.transform.rule.ParserRule;

public class DefaultParser extends AbstractParser<String> {

    public DefaultParser() {
        super();
    }

    public DefaultParser(Mapper mapper) {
        super(mapper);
    }

    public DefaultParser(ParserRule parserRule) {
        super(parserRule);
    }

    public DefaultParser(Mapper mapper, ParserRule parserRule) {
        super(mapper, parserRule);
    }

    @Override
    protected String convert(String input) {
        return input;
    }

}
