package im.dino.dbinspector.domain.schema.trigger

import im.dino.dbinspector.data.models.Row
import im.dino.dbinspector.domain.shared.AbstractSchemaOperation

internal class TriggerContentOperation(
    private val name: String
) : AbstractSchemaOperation<List<Row>>() {

    override fun query(): String = String.format(FORMAT_CONTENT_TRIGGER, name)
}