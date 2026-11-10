package com.example.healthtracker

import android.app.Application
import com.example.healthtracker.data.VitaLogDatabase
import com.example.healthtracker.data.repository.VitaLogRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class VitaLogApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { VitaLogDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { VitaLogRepository(database.vitaLogDao()) }
}
