package com.assemcorp.cuttingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.assemcorp.cuttingapp.ui.AssemcorpApp
import com.assemcorp.cuttingapp.ui.theme.CuttingAppTheme
import com.assemcorp.cuttingapp.viewmodel.CuttingViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cuttingViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(CuttingViewModel::class.java)
        setContent {
            CuttingAppTheme {
                AssemcorpApp(cuttingViewModel)
            }
        }
    }
}
