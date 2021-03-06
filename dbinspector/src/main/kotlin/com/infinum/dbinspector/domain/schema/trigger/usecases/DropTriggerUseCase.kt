package com.infinum.dbinspector.domain.schema.trigger.usecases

import com.infinum.dbinspector.domain.Repositories
import com.infinum.dbinspector.domain.UseCases
import com.infinum.dbinspector.domain.shared.models.Page
import com.infinum.dbinspector.domain.shared.models.parameters.ConnectionParameters
import com.infinum.dbinspector.domain.shared.models.parameters.ContentParameters

internal class DropTriggerUseCase(
    private val connectionRepository: Repositories.Connection,
    private val schemaRepository: Repositories.Schema
) : UseCases.DropTrigger {

    override suspend fun invoke(input: ContentParameters): Page {
        val connection = connectionRepository.open(ConnectionParameters(input.databasePath))
        return schemaRepository.dropByName(input.copy(database = connection.database))
    }
}
