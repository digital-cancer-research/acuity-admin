package com.acuity.visualisations.transform.parser;

import com.acuity.visualisations.exception.InvalidDataFormatException;
import com.acuity.visualisations.transform.rule.Mapper;
import com.acuity.visualisations.transform.rule.ParserRule;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.acuity.visualisations.data.util.Util.isEmpty;

public class IntegerParser extends AbstractParser<Integer> {

    public IntegerParser() {
        super();
    }

    public IntegerParser(Mapper mapper) {
        super(mapper);
    }

    public IntegerParser(ParserRule parserRule) {
        super(parserRule);
    }

    public IntegerParser(Mapper mapper, ParserRule parserRule) {
        super(mapper, parserRule);
    }

    @Override
    public Integer convert(String input) throws InvalidDataFormatException {
        if (isEmpty(input) || "Infinity".equals(input)) {
            return null;
        }

        BigDecimal bigd = null;
        try {
            bigd = new BigDecimal(input);
            return bigd.setScale(0, RoundingMode.HALF_UP).intValueExact();
        } catch (NumberFormatException | ArithmeticException e) {
            throw new InvalidDataFormatException("IntegerParser: Unable to parse the following value: " + input, e);
        }
    }
}
