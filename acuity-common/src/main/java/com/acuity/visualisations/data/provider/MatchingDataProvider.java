package com.acuity.visualisations.data.provider;

import com.acuity.visualisations.data.Data;
import java.util.List;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
public class MatchingDataProvider implements DataProvider {

    @Autowired
    private List<DataProvider> providers;

    @Override
    public boolean match(@NonNull String location) {
        return true;
    }

    @Override
    @SneakyThrows
    public Data get(@NonNull String location) {
        return providers.stream()
                .filter(p -> p.match(location))
                .findFirst()
                .map(p -> p.get(location))
                .orElseThrow(UnsupportedOperationException::new);
    }
}
