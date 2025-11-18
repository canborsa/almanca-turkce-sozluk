package com.assemcorp.cuttingapp.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.assemcorp.cuttingapp.R
import com.assemcorp.cuttingapp.data.Part
import com.assemcorp.cuttingapp.logic.generatePdf
import com.assemcorp.cuttingapp.viewmodel.CuttingViewModel
import java.util.Locale

@Composable
fun AssemcorpApp(cuttingViewModel: CuttingViewModel = viewModel()) {
    // This is a simple way to force the app to recompose when the language changes.
    // A more robust solution would involve a custom LocalProvider.
    key(cuttingViewModel.currentLanguage) {
        // Set the locale for the app
        val locale = Locale(cuttingViewModel.currentLanguage)
        Locale.setDefault(locale)
        val resources = androidx.compose.ui.platform.LocalContext.current.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)

        MainScreen(cuttingViewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(cuttingViewModel: CuttingViewModel) {
    var customerName by remember { mutableStateOf("") }
    var partWidth by remember { mutableStateOf("") }
    var partHeight by remember { mutableStateOf("") }
    var partQuantity by remember { mutableStateOf("1") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Language and Unit Selection
            Row {
                // Language
                Text("Language:")
                RadioButton(selected = cuttingViewModel.currentLanguage == "tr", onClick = { cuttingViewModel.setLanguage("tr") })
                Text("TR")
                RadioButton(selected = cuttingViewModel.currentLanguage == "en", onClick = { cuttingViewModel.setLanguage("en") })
                Text("EN")
                Spacer(modifier = Modifier.width(16.dp))
                // Unit
                Text("Unit:")
                RadioButton(selected = cuttingViewModel.currentUnit == "cm", onClick = { cuttingViewModel.setUnit("cm") })
                Text("cm")
                RadioButton(selected = cuttingViewModel.currentUnit == "in", onClick = { cuttingViewModel.setUnit("in") })
                Text("in")
            }

            OutlinedTextField(
                value = customerName,
                onValueChange = { customerName = it },
                label = { Text(stringResource(id = R.string.customer_name)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(id = R.string.material_selection), style = MaterialTheme.typography.titleMedium)
            Column {
                cuttingViewModel.materials.forEach { material ->
                    Row {
                        RadioButton(
                            selected = cuttingViewModel.selectedMaterial == material,
                            onClick = { cuttingViewModel.selectedMaterial = material }
                        )
                        Text(
                            text = if (material.isCustom) stringResource(id = R.string.free_size) else material.name,
                            modifier = Modifier.padding(start = 4.dp, end = 16.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(visible = cuttingViewModel.selectedMaterial.isCustom) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        value = cuttingViewModel.customWidth,
                        onValueChange = { cuttingViewModel.customWidth = it },
                        label = { Text(stringResource(id = R.string.roll_width)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = cuttingViewModel.customHeight,
                        onValueChange = { cuttingViewModel.customHeight = it },
                        label = { Text(stringResource(id = R.string.roll_height)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            cuttingViewModel.errorMessage?.let {
                Text(
                    text = stringResource(id = R.string.invalid_roll_dimensions),
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(id = R.string.parts_to_cut), style = MaterialTheme.typography.titleMedium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedTextField(
                    value = partWidth,
                    onValueChange = { partWidth = it },
                    label = { Text("${stringResource(id = R.string.width)} (${cuttingViewModel.currentUnit})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = partHeight,
                    onValueChange = { partHeight = it },
                    label = { Text("${stringResource(id = R.string.height)} (${cuttingViewModel.currentUnit})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = partQuantity,
                    onValueChange = { partQuantity = it },
                    label = { Text(stringResource(id = R.string.quantity)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val width = partWidth.toDoubleOrNull()
                    val height = partHeight.toDoubleOrNull()
                    val quantity = partQuantity.toIntOrNull()
                    if (width != null && height != null && quantity != null) {
                        cuttingViewModel.addPart(width, height, quantity)
                        partWidth = ""
                        partHeight = ""
                        partQuantity = "1"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.add_part))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(id = R.string.added_parts), style = MaterialTheme.typography.titleMedium)
            cuttingViewModel.parts.forEach { part ->
                PartItem(part = part, unit = cuttingViewModel.currentUnit, onRemove = { cuttingViewModel.removePart(part) })
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    cuttingViewModel.calculateCuttingPlan()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.calculate))
            }

            cuttingViewModel.calculationResult?.let { results ->
                Spacer(modifier = Modifier.height(16.dp))
                ResultsSummary(results = results)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val customWidth = if (cuttingViewModel.selectedMaterial.isCustom) cuttingViewModel.customWidth.toDoubleOrNull() else null
                        val customHeight = if (cuttingViewModel.selectedMaterial.isCustom) cuttingViewModel.customHeight.toDoubleOrNull() else null
                        val file = generatePdf(context, results, customerName, cuttingViewModel.selectedMaterial, customWidth, customHeight)
                        Toast.makeText(context, context.getString(R.string.pdf_saved, file.absolutePath), Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(id = R.string.save_as_pdf))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(id = R.string.cutting_plan), style = MaterialTheme.typography.titleMedium)
                results.forEach { rollResult ->
                    val rollWidth = if (cuttingViewModel.selectedMaterial.isCustom) cuttingViewModel.customWidth.toDoubleOrNull() ?: 0.0 else cuttingViewModel.selectedMaterial.width
                    val rollHeight = if (cuttingViewModel.selectedMaterial.isCustom) cuttingViewModel.customHeight.toDoubleOrNull() ?: 0.0 else cuttingViewModel.selectedMaterial.height
                    Text("Rulo #${rollResult.rollNumber}", style = MaterialTheme.typography.titleSmall)
                    RollDrawing(rollResult = rollResult, rollWidth = rollWidth, rollHeight = rollHeight)
                }
            }
        }
    }
}

@Composable
fun PartItem(part: Part, unit: String, onRemove: () -> Unit) {
    val width = if (unit == "in") part.width / 2.54 else part.width
    val height = if (unit == "in") part.height / 2.54 else part.height
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(String.format("%.2f %s x %.2f %s - %d adet", width, unit, height, unit, part.quantity))
        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Delete, contentDescription = "Remove Part")
        }
    }
}
