package com.acuity.visualisations.data.provider;

import com.acuity.visualisations.data.Data;
import lombok.NonNull;

public interface DataProvider {

    boolean match(@NonNull String location);

    Data get(@NonNull String location);

}
