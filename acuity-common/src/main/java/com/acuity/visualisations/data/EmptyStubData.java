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

package com.acuity.visualisations.data;

import java.io.InputStream;

public final class EmptyStubData implements Data {

    private static EmptyStubData instance;

    private EmptyStubData() {
    }

    /**
     * This class represents empty data. Returned in cases of missing files or exceptions
     */
    public static EmptyStubData getInstance() {
        if (instance == null) {
            instance = new EmptyStubData();
        }
        return instance;
    }

    @Override
    public InputStream stream() {
        throw new UnsupportedOperationException("Stub Data could not be read");
    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public long timestamp() {
        return 0;
    }

    @Override
    public long length() {
        return 0;
    }


}
