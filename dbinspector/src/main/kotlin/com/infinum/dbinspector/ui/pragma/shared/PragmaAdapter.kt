package com.infinum.dbinspector.ui.pragma.shared

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.infinum.dbinspector.databinding.DbinspectorItemCellBinding
import com.infinum.dbinspector.domain.shared.models.Cell
import com.infinum.dbinspector.ui.shared.diffutils.CellDiffUtil

internal class PragmaAdapter(
    private val headersCount: Int,
) : PagingDataAdapter<Cell, PragmaViewHolder>(CellDiffUtil()) {

    init {
        stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PragmaViewHolder =
        PragmaViewHolder(
            DbinspectorItemCellBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: PragmaViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, position / headersCount)
    }

    override fun onViewRecycled(holder: PragmaViewHolder) =
        with(holder) {
            unbind()
            super.onViewRecycled(this)
        }
}
