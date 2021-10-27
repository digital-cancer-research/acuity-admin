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

SELECT     b.ACTIVE_SUBSTANCE, a.ACTIVITY_CODE, a.ACTIVITY_DESCRIPTION AS STUDY, a.A_TYPE_10_DESC AS STUDY_TYPE, a.STUDY_DLVRY_MODEL AS DELIVERY_MODEL,
                      a.INTERNAL_AZPOM_PHASE_DESC AS STUDY_PHASE,
                      a.PROJECT_CODE, a.PROJECT_NM, H.DATE_PLANNED AS FSI_PLN, L.DATE_PLANNED AS DBL_PLN, L.DATE_ACTUAL as DBL_ACT, B.STUDY_STATUS
FROM         dbo.SRC_CURRENT_AZ_STUDY_LIST_AZ_MEDI AS a LEFT OUTER JOIN
                      dbo.PRT_STUDY_DESCRIPTORS AS B ON a.ACTIVITY_CODE = B.STUDY_CODE LEFT OUTER JOIN
                      dbo.IMP_CURRENT_STUDY_MS AS F ON B.TRIAL_NO = F.STUDY_NUMBER AND F.MILESTONE_NUMBER = 2500 LEFT OUTER JOIN
                      dbo.IMP_CURRENT_STUDY_MS AS G ON B.TRIAL_NO = G.STUDY_NUMBER AND G.MILESTONE_NUMBER = 4650 LEFT OUTER JOIN
                      dbo.IMP_CURRENT_STUDY_MS AS H ON B.TRIAL_NO = H.STUDY_NUMBER AND H.MILESTONE_NUMBER = 3200 LEFT OUTER JOIN
                      dbo.IMP_CURRENT_STUDY_MS AS L ON B.TRIAL_NO = L.STUDY_NUMBER AND L.MILESTONE_NUMBER = 4000
WHERE   ( ( (F.DATE_ACTUAL <= GETDATE()) AND (G.DATE_PLANNED >= GETDATE()) )
            OR ((F.DATE_PLANNED >= GETDATE())) )
            AND (LEN(LTRIM(RTRIM(a.ACTIVITY_CODE))) = 11)
            AND (B.STUDY_CODE IS NOT NULL) 
            AND (B.STUDY_TYPE = 'R&D') 
            AND (B.HCS_DATE IS NULL)
            AND (l.DATE_ACTUAL is null or l.DATE_ACTUAL <= GETDATE());
