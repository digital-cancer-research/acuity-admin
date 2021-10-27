package com.acuity.visualisations.data.provider;

import com.acuity.visualisations.data.Data;
import com.acuity.visualisations.data.EmptyStubData;
import lombok.NonNull;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(4)
public class StubDataProvider implements DataProvider {

    @Override
    public boolean match(@NonNull String location) {
        //this provider is used if no other suitable providers found
        return true;
    }

    @Override
    public Data get(@NonNull String location) {
        return EmptyStubData.getInstance();
    }

}
