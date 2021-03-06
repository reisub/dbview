package com.infinum.dbinspector.ui.databases

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.infinum.dbinspector.domain.UseCases
import com.infinum.dbinspector.domain.database.models.DatabaseDescriptor
import com.infinum.dbinspector.domain.shared.models.parameters.DatabaseParameters
import com.infinum.dbinspector.ui.shared.base.BaseViewModel

internal class DatabaseViewModel(
    private val getDatabases: UseCases.GetDatabases,
    private val importDatabases: UseCases.ImportDatabases,
    private val removeDatabase: UseCases.RemoveDatabase,
    private val copyDatabase: UseCases.CopyDatabase
) : BaseViewModel() {

    val databases: MutableLiveData<List<DatabaseDescriptor>> = MutableLiveData()

    fun browse(context: Context, query: String? = null) {
        launch {
            databases.value = io {
                getDatabases(
                    DatabaseParameters.Get(
                        context = context,
                        argument = query
                    )
                )
            }
        }
    }

    fun import(context: Context, uris: List<Uri>) =
        launch {
            importDatabases(
                DatabaseParameters.Import(
                    context = context,
                    importUris = uris
                )
            )
            browse(context)
        }

    fun remove(context: Context, database: DatabaseDescriptor) =
        launch {
            val result = io {
                removeDatabase(
                    DatabaseParameters.Command(
                        context = context,
                        databaseDescriptor = database
                    )
                )
            }
            if (result.isNotEmpty()) {
                browse(context)
            }
        }

    fun copy(context: Context, database: DatabaseDescriptor) =
        launch {
            val ok = io {
                copyDatabase(
                    DatabaseParameters.Command(
                        context = context,
                        databaseDescriptor = database
                    )
                )
            }
            if (ok.isNotEmpty()) {
                browse(context)
            }
        }
}
