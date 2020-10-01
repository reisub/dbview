package im.dino.dbinspector.domain.pragma.schema

import im.dino.dbinspector.data.models.Row
import im.dino.dbinspector.domain.shared.AbstractSchemaOperation

internal class IndexesOperation(
    private val name: String
) : AbstractSchemaOperation<List<Row>>() {

    override fun query(): String = String.format(FORMAT_PRAGMA_INDEXES, name)
}