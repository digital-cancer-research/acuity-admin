package com.acuity.visualisations.transform.parser;

import com.acuity.visualisations.exception.InvalidDataFormatException;
import com.acuity.visualisations.transform.rule.Mapper;
import com.acuity.visualisations.transform.rule.ParserRule;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;

import static com.acuity.visualisations.data.util.Util.isEmpty;

public class LabvalueParser extends AbstractParser<BigDecimal> {

    public LabvalueParser() {
        super();
    }

    public LabvalueParser(Mapper mapper) {
        super(mapper);
    }

    public LabvalueParser(ParserRule parserRule) {
        super(parserRule);
    }

    public LabvalueParser(Mapper mapper, ParserRule parserRule) {
        super(mapper, parserRule);
    }

    @Override
    protected BigDecimal convert(String input) throws InvalidDataFormatException {

        if (isEmpty(input)) {
            return null;
        }

        DecimalFormat dFormat = new DecimalFormat();
        dFormat.setParseBigDecimal(true);

        String skipWhitespaces = input.replaceAll(" ", "");
        try {
            BigDecimal result = (BigDecimal) dFormat.parse(skipWhitespaces);
            result = result.setScale(2, BigDecimal.ROUND_HALF_UP);
            return result;
        } catch (ParseException e) {
            String skipWhitespaces2 = skipWhitespaces.substring(1);
            try {
                BigDecimal result = (BigDecimal) dFormat.parse(skipWhitespaces2);
                result = result.setScale(2, BigDecimal.ROUND_HALF_UP);
                return result;
            } catch (ParseException e1) {
                throw new InvalidDataFormatException("BigDecimal parser: Unable to parse following value: " + input, e);
            }
        }

    }

}
