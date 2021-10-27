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

package com.acuity.visualisations.util;

public final class JobLauncherConsts {

    public static final String STUDY_KEY = "etl.study";
    public static final String PROJECT_KEY = "etl.project";
    public static final String AML_ENABLED = "aml.enabled";
    public static final String UNIQUE_KEY = "etl.launchingTime";

    public static final String CONF_SOURCE_TYPE = "etl.confSourceType";

    public static final String JOB_NAME = "jobName";

    public static final String AML_GROUP_NAME = "aml";
    public static final String ETL_GROUP_NAME = "quartz-batch";

    public static final String AML_JOB_NAME = "AML_Job";
    public static final String ETL_JOB_NAME = "ACUITY_ETL_Job";

    private JobLauncherConsts() {
    }
}
