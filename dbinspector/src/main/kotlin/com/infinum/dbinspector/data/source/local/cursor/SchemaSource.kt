package com.infinum.dbinspector.data.source.local.cursor

import com.infinum.dbinspector.data.Sources
import com.infinum.dbinspector.data.models.local.cursor.output.QueryResult
import com.infinum.dbinspector.data.source.local.cursor.shared.CursorSource
import com.infinum.dbinspector.data.source.memory.pagination.Paginator
import com.infinum.dbinspector.data.models.local.cursor.input.Query
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.suspendCancellableCoroutine

@Suppress("LongParameterList")
internal class SchemaSource(
    private val tablesPaginator: Paginator,
    private val tableByNamePaginator: Paginator,
    private val dropTableContentPaginator: Paginator,
    private val viewsPaginator: Paginator,
    private val viewByNamePaginator: Paginator,
    private val dropViewPaginator: Paginator,
    private val triggersPaginator: Paginator,
    private val triggerByNamePaginator: Paginator,
    private val dropTriggerPaginator: Paginator,
    private val rawPaginator: Paginator,
    private val store: Sources.Local.Store
) : CursorSource(), Sources.Local.Schema {

    // region Tables
    override suspend fun getTables(query: Query): QueryResult =
        store.settings().data.firstOrNull().let {
            suspendCancellableCoroutine { continuation ->
                collectRows(
                    query = query,
                    paginator = tablesPaginator,
                    settings = it,
                    continuation = continuation
                )
            }
        }

    override suspend fun getTableByName(query: Query): QueryResult =
        store.settings().data.firstOrNull().let {
            suspendCancellableCoroutine { continuation ->
                collectRows(
                    query = query,
                    paginator = tableByNamePaginator,
                    settings = it,
                    continuation = continuation
                )
            }
        }

    override suspend fun dropTableContentByName(query: Query): QueryResult =
        store.settings().data.firstOrNull().let {
            suspendCancellableCoroutine { continuation ->
                collectRows(
                    query = query,
                    paginator = dropTableContentPaginator,
                    settings = it,
                    continuation = continuation
                )
            }
        }
    // endregion

    // region Views
    override suspend fun getViews(query: Query): QueryResult =
        store.settings().data.firstOrNull().let {
            suspendCancellableCoroutine { continuation ->
                collectRows(
                    query = query,
                    paginator = viewsPaginator,
                    settings = it,
                    continuation = continuation
                )
            }
        }

    override suspend fun getViewByName(query: Query): QueryResult =
        store.settings().data.firstOrNull().let {
            suspendCancellableCoroutine { continuation ->
                collectRows(
                    query = query,
                    paginator = viewByNamePaginator,
                    settings = it,
                    continuation = continuation
                )
            }
        }

    override suspend fun dropViewByName(query: Query): QueryResult =
        store.settings().data.firstOrNull().let {
            suspendCancellableCoroutine { continuation ->
                collectRows(
                    query = query,
                    paginator = dropViewPaginator,
                    settings = it,
                    continuation = continuation
                )
            }
        }
    // endregion

    // region Triggers
    override suspend fun getTriggers(query: Query): QueryResult =
        store.settings().data.firstOrNull().let {
            suspendCancellableCoroutine { continuation ->
                collectRows(
                    query = query,
                    paginator = triggersPaginator,
                    settings = it,
                    continuation = continuation
                )
            }
        }

    override suspend fun getTriggerByName(query: Query): QueryResult =
        store.settings().data.firstOrNull().let {
            suspendCancellableCoroutine { continuation ->
                collectRows(
                    query = query,
                    paginator = triggerByNamePaginator,
                    settings = it,
                    continuation = continuation
                )
            }
        }

    override suspend fun dropTriggerByName(query: Query): QueryResult =
        store.settings().data.firstOrNull().let {
            suspendCancellableCoroutine { continuation ->
                collectRows(
                    query = query,
                    paginator = dropTriggerPaginator,
                    settings = it,
                    continuation = continuation
                )
            }
        }
    // endregion

    // region Raw
    override suspend fun rawQuery(query: Query): QueryResult =
        store.settings().data.firstOrNull().let {
            suspendCancellableCoroutine { continuation ->
                collectRows(
                    query = query,
                    paginator = rawPaginator,
                    settings = it,
                    continuation = continuation
                )
            }
        }
    // endregion
}
