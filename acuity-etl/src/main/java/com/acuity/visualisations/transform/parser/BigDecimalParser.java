package com.acuity.visualisations.transform.parser;

import com.acuity.visualisations.exception.InvalidDataFormatException;
import com.acuity.visualisations.transform.rule.Mapper;
import com.acuity.visualisations.transform.rule.ParserRule;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;

import static com.acuity.visualisations.data.util.Util.isEmpty;

public class BigDecimalParser extends AbstractParser<BigDecimal> {

    public BigDecimalParser() {
        super();
    }

    public BigDecimalParser(Mapper mapper) {
        super(mapper);
    }

    public BigDecimalParser(ParserRule parserRule) {
        super(parserRule);
    }

    public BigDecimalParser(Mapper mapper, ParserRule parserRule) {
        super(mapper, parserRule);
    }

    @Override
    protected BigDecimal convert(String input) throws InvalidDataFormatException {
        if (isEmpty(input)) {
            return null;
        }

        DecimalFormat dFormat = new DecimalFormat();
        dFormat.setParseBigDecimal(true);

        try {
            String skipWhitespaces = input.replaceAll(" ", "");
            BigDecimal result = (BigDecimal) dFormat.parse(skipWhitespaces);
            result = result.setScale(2, BigDecimal.ROUND_HALF_UP);
            return result;
        } catch (ParseException e) {
            throw new InvalidDataFormatException("BigDecimal parser: Unable to parse following value: " + input, e);
        }

    }

}
