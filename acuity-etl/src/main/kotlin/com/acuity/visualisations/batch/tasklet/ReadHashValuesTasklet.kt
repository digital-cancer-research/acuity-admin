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

package com.acuity.visualisations.batch.tasklet

import com.acuity.visualisations.batch.holders.HoldersAware
import com.acuity.visualisations.dal.EntityManager
import com.acuity.visualisations.model.output.entities.*
import com.acuity.visualisations.service.IExecutionProfiler
import com.acuity.visualisations.util.ReflectionUtil
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Scope("step")
open class ReadHashValuesTasklet : HoldersAware(), Tasklet {

    @Autowired
    lateinit private var entityManager: EntityManager

    @Autowired
    lateinit private var executionProfiler: IExecutionProfiler

    override fun initHolders() {
        hashValuesHolder
    }

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus {
        executionProfiler.startOperation(jobExecutionId, "readHashes")

        when {
            entityManager.studyExists(studyName) -> configurationUtil.entityNames.forEach { putHashes(it) }
            else -> configurationUtil.entityNames.forEach { cleanHashes(it) }
        }

        executionProfiler.stopOperation(jobExecutionId, "readHashes")

        return RepeatStatus.FINISHED
    }

    private fun cleanHashes(entityName: String) {
        executionProfiler.apply {
            startOperation(jobExecutionId, "cleanHashes")
            startOperation(jobExecutionId, "cleanHashes-$entityName")
        }

        debug("No data in DB, cleaning hashes for $entityName")
        hashValuesHolder.cleanHashes(ReflectionUtil.getEntityClass(entityName))

        executionProfiler.apply {
            stopOperation(jobExecutionId, "cleanHashes-$entityName")
            stopOperation(jobExecutionId, "cleanHashes")
        }
    }

    private val entitiesToHashExplicitly = setOf(
            AE::class.java,
            AeActionTaken::class.java,
            Drug::class.java,
            AeSeverity::class.java,
            MedDosingSchedule::class.java
    )

    private fun putHashes(entityName: String) {
        val entityClass = ReflectionUtil.getEntityClass(entityName)

        if (!hashValuesHolder.hashValuesLoaded(entityClass) || entityClass in entitiesToHashExplicitly) {
            executionProfiler.apply {
                startOperation(jobExecutionId, "putHashesForEntity")
                startOperation(jobExecutionId, "putHashesForEntity-$entityName")
            }

            debug("Cleaning hashes for $entityName")
            hashValuesHolder.cleanHashes(entityClass)

            debug("Reading hashes for $entityName")
            putHashesForEntity(entityClass)

            executionProfiler.apply {
                stopOperation(jobExecutionId, "putHashesForEntity-$entityName")
                stopOperation(jobExecutionId, "putHashesForEntity")
            }
        }
    }

    private fun putHashesForEntity(entityClass: Class<*>) =
            entityManager.findHash(entityClass, hashValuesHolder.studyGuid, hashValuesHolder.getHashValues(entityClass))

}
