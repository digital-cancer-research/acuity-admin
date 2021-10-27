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
