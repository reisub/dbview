package com.infinum.dbinspector.ui.content.shared

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.infinum.dbinspector.R
import com.infinum.dbinspector.databinding.DbinspectorActivityContentBinding
import com.infinum.dbinspector.domain.shared.models.Sort
import com.infinum.dbinspector.extensions.setupAsTable
import com.infinum.dbinspector.ui.Presentation
import com.infinum.dbinspector.ui.content.table.TableViewModel
import com.infinum.dbinspector.ui.content.trigger.TriggerViewModel
import com.infinum.dbinspector.ui.content.view.ViewViewModel
import com.infinum.dbinspector.ui.pragma.PragmaActivity
import com.infinum.dbinspector.ui.shared.base.BaseActivity
import com.infinum.dbinspector.ui.shared.base.lifecycle.LifecycleConnection
import com.infinum.dbinspector.ui.shared.delegates.lifecycleConnection
import com.infinum.dbinspector.ui.shared.delegates.viewBinding
import com.infinum.dbinspector.ui.shared.edgefactories.bounce.BounceEdgeEffectFactory
import com.infinum.dbinspector.ui.shared.headers.HeaderAdapter

internal abstract class ContentActivity : BaseActivity() {

    override val binding by viewBinding(DbinspectorActivityContentBinding::inflate)

    abstract override val viewModel: ContentViewModel

    private val connection: LifecycleConnection by lifecycleConnection()

    @get:StringRes
    abstract val title: Int

    @get:MenuRes
    abstract val menu: Int

    @get:StringRes
    abstract val drop: Int

    private lateinit var contentPreviewFactory: ContentPreviewFactory

    private lateinit var headerAdapter: HeaderAdapter

    private lateinit var contentAdapter: ContentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { finish() }

        contentPreviewFactory = ContentPreviewFactory(this)

        if (connection.hasSchemaData) {
            viewModel.databasePath = connection.databasePath!!
            viewModel.open()
            setupUi(
                connection.databasePath!!,
                connection.databaseName!!,
                connection.schemaName!!
            )

            viewModel.header(connection.schemaName!!) { tableHeaders ->
                headerAdapter = HeaderAdapter(tableHeaders, true) { header ->
                    query(connection.schemaName!!, header.name, header.sort)
                    headerAdapter.updateHeader(header)
                }

                contentAdapter = ContentAdapter(
                    headersCount = tableHeaders.size,
                    onCellClicked = { cell -> contentPreviewFactory.showCell(cell) }
                )

                with(binding) {
                    contentAdapter.addLoadStateListener { loadState ->
                        if (loadState.prepend.endOfPaginationReached) {
                            swipeRefresh.isRefreshing = loadState.refresh !is LoadState.NotLoading
                        }
                    }

                    swipeRefresh.setOnRefreshListener {
                        contentAdapter.refresh()
                    }

                    recyclerView.layoutManager = GridLayoutManager(
                        this@ContentActivity,
                        tableHeaders.size,
                        RecyclerView.VERTICAL,
                        false
                    )
                    recyclerView.adapter = ConcatAdapter(headerAdapter, contentAdapter)
                    recyclerView.edgeEffectFactory = BounceEdgeEffectFactory()
                }

                query(connection.schemaName!!)
            }
        } else {
            showDatabaseParametersError()
        }
    }

    private fun setupUi(databasePath: String, databaseName: String, schemaName: String) {
        with(binding.toolbar) {
            title = getString(this@ContentActivity.title)
            subtitle = listOf(databaseName, schemaName).joinToString(" → ")
            menuInflater.inflate(this@ContentActivity.menu, menu)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.clear -> {
                        drop(schemaName)
                        true
                    }
                    R.id.drop -> {
                        drop(schemaName)
                        true
                    }
                    R.id.pragma -> {
                        pragma(databaseName, databasePath, schemaName)
                        true
                    }
                    R.id.edit -> {
                        contentPreviewFactory.showEdit(databasePath, databaseName)
                        true
                    }
                    else -> false
                }
            }
        }

        binding.recyclerView.setupAsTable()
    }

    private fun pragma(databaseName: String?, databasePath: String?, schemaName: String) {
        startActivity(
            Intent(this, PragmaActivity::class.java)
                .apply {
                    putExtra(Presentation.Constants.Keys.DATABASE_NAME, databaseName)
                    putExtra(Presentation.Constants.Keys.DATABASE_PATH, databasePath)
                    putExtra(Presentation.Constants.Keys.SCHEMA_NAME, schemaName)
                }
        )
    }

    private fun drop(name: String) =
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dbinspector_title_info)
            .setMessage(String.format(getString(drop), name))
            .setPositiveButton(android.R.string.ok) { dialog: DialogInterface, _: Int ->
                viewModel.drop(name) {
                    when (viewModel) {
                        is TableViewModel -> clearTable()
                        is TriggerViewModel -> dropTrigger()
                        is ViewViewModel -> dropView()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            .create()
            .show()

    private fun query(
        name: String,
        orderBy: String? = null,
        sort: Sort = Sort.ASCENDING
    ) =
        viewModel.query(name, orderBy, sort) {
            contentAdapter.submitData(it)
        }

    private fun clearTable() =
        contentAdapter.refresh()

    private fun dropTrigger() {
        setResult(
            Activity.RESULT_OK,
            Intent().apply {
                putExtra(Presentation.Constants.Keys.SHOULD_REFRESH, true)
            }
        )
        finish()
    }

    private fun dropView() {
        setResult(
            Activity.RESULT_OK,
            Intent().apply {
                putExtra(Presentation.Constants.Keys.SHOULD_REFRESH, true)
            }
        )
        finish()
    }
}
