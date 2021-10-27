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
