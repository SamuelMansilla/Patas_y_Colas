package com.example.patas_y_colas.ui.screens.menu.components

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.widget.DatePicker
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.patas_y_colas.model.Pet
import com.example.patas_y_colas.ui.theme.*
import java.io.File
import java.io.FileOutputStream
import java.util.*

@Composable
fun PetForm(pet: Pet?, onSave: (Pet) -> Unit, onDelete: (Pet) -> Unit) {
    var name by remember(pet) { mutableStateOf(pet?.name ?: "") }
    var breed by remember(pet) { mutableStateOf(pet?.breed ?: "") }
    var age by remember(pet) { mutableStateOf(pet?.age ?: "") }
    var weight by remember(pet) { mutableStateOf(pet?.weight?.filter { it.isDigit() } ?: "") }
    var vaccines by remember(pet) { mutableStateOf(pet?.vaccines ?: "") }
    var nextVaccines by remember(pet) { mutableStateOf(pet?.nextVaccines ?: "") }
    var imageUri by remember(pet) { mutableStateOf(pet?.imageUri) }

    val initialSpecies = pet?.species
    var speciesOption by remember(pet) { mutableStateOf(if (initialSpecies != null && initialSpecies in listOf("Perro", "Gato")) initialSpecies else "Otro") }
    var otherSpecies by remember(pet) { mutableStateOf(if (initialSpecies != null && initialSpecies !in listOf("Perro", "Gato")) initialSpecies else "") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var breedError by remember { mutableStateOf<String?>(null) }
    var ageError by remember { mutableStateOf<String?>(null) }
    var weightError by remember { mutableStateOf<String?>(null) }
    var otherSpeciesError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                val newUri = copyUriToInternalStorage(context, it)
                imageUri = newUri?.toString()
            }
        }
    )
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success -> if (success) { imageUri = tempImageUri?.toString() } }
    )
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if(isGranted) {
                val uri = context.createImageUri()
                tempImageUri = uri
                cameraLauncher.launch(uri)
            }
        }
    )
    var showImageSourceDialog by remember { mutableStateOf(false) }

    fun validate(): Boolean {
        nameError = if (name.isBlank() || name.any { it.isDigit() }) "Nombre inválido (solo letras)" else null
        breedError = if (breed.any { it.isDigit() }) "Raza inválida (solo letras)" else null
        ageError = if (age.isBlank() || !age.all { it.isDigit() }) "Edad inválida (solo números)" else null
        weightError = if (weight.isBlank() || !weight.all { it.isDigit() }) "Peso inválido (solo números)" else null
        otherSpeciesError = if (speciesOption == "Otro" && otherSpecies.isBlank()) "Especifique la especie" else null
        return nameError == null && breedError == null && ageError == null && weightError == null && otherSpeciesError == null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = if (pet == null) "Nueva Mascota" else "Editar a ${pet.name}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally), color = TextWhite)
            PetTextField(label = "Nombre", value = name, onValueChange = { name = it }, error = nameError)
            Column {
                Text("Especie", color = TextGray, modifier = Modifier.padding(start = 16.dp, bottom = 8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    listOf("Perro", "Gato", "Otro").forEach { option ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = speciesOption == option, onClick = { speciesOption = option }, colors = RadioButtonDefaults.colors(selectedColor = OrangeAccent, unselectedColor = TextGray))
                            Text(option, color = TextWhite)
                        }
                    }
                }
            }
            AnimatedVisibility(visible = speciesOption == "Otro") {
                PetTextField(label = "Especie (Otro)", value = otherSpecies, onValueChange = { otherSpecies = it }, error = otherSpeciesError)
            }
            PetTextField(label = "Raza", value = breed, onValueChange = { breed = it }, error = breedError)
            PetTextField(label = "Edad", value = age, onValueChange = { age = it }, error = ageError, keyboardType = KeyboardType.Number)
            PetTextField(label = "Peso", value = weight, onValueChange = { weight = it }, error = weightError, keyboardType = KeyboardType.Number, suffix = "Kg")
            PetTextField(label = "Vacunas", value = vaccines, onValueChange = { vaccines = it })

            val mContext = LocalContext.current
            val mCalendar = Calendar.getInstance()
            val mDatePickerDialog = DatePickerDialog(mContext, { _: DatePicker, year: Int, month: Int, dayOfMonth: Int -> nextVaccines = "$dayOfMonth/${month + 1}/$year" }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH))
            Box(modifier = Modifier.clickable { mDatePickerDialog.show() }) {
                OutlinedTextField(
                    value = nextVaccines,
                    onValueChange = { },
                    enabled = false,
                    label = { Text("Próximas Vacunas") },
                    trailingIcon = { Icon(Icons.Default.CalendarToday, "Seleccionar fecha", tint = TextGray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = TextWhite,
                        disabledBorderColor = TextGray,
                        disabledLabelColor = TextGray,
                        disabledTrailingIconColor = TextGray
                    )
                )
            }

            Button(onClick = { showImageSourceDialog = true }, modifier = Modifier
                .fillMaxWidth()
                .height(50.dp), shape = CircleShape, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = OrangeAccent), elevation = null, border = BorderStroke(1.dp, OrangeAccent)) { Text("Seleccionar Foto") }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (pet != null) {
                    Button(onClick = { onDelete(pet) }, modifier = Modifier
                        .weight(1f)
                        .height(50.dp), shape = CircleShape, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = DestructiveRed), border = BorderStroke(1.dp, DestructiveRed)) { Text(text = "Eliminar") }
                }
                Button(
                    onClick = {
                        if (validate()) {
                            val finalSpecies = if (speciesOption == "Otro") otherSpecies else speciesOption
                            val petToSave = Pet(id = pet?.id ?: 0, name = name, species = finalSpecies, breed = breed, age = age, weight = "$weight Kg", vaccines = vaccines, nextVaccines = nextVaccines, imageUri = imageUri)
                            onSave(petToSave)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent)
                ) {
                    Text(text = "Guardar", fontSize = 16.sp, color = DarkBackground, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if(showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Seleccionar Imagen") },
            text = { Text("¿Desde dónde quieres obtener la imagen?") },
            confirmButton = { Button(onClick = { showImageSourceDialog = false; cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }) { Text("Cámara") } },
            dismissButton = { Button(onClick = { showImageSourceDialog = false; imagePickerLauncher.launch("image/*") }) { Text("Galería") } }
        )
    }
}

@Composable
private fun PetTextField(label: String, value: String, onValueChange: (String) -> Unit, error: String? = null, keyboardType: KeyboardType = KeyboardType.Text, suffix: String? = null) {
    Column {
        TextField(
            value = value, onValueChange = onValueChange, label = { Text(label) }, isError = error != null,
            modifier = Modifier.fillMaxWidth(), singleLine = true, shape = CircleShape,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Next),
            trailingIcon = { if(suffix != null) Text(suffix, color = TextGray) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = DarkBackground.copy(alpha = 0.5f), unfocusedContainerColor = DarkBackground.copy(alpha = 0.5f),
                focusedTextColor = TextWhite, unfocusedTextColor = TextWhite, cursorColor = OrangeAccent,
                focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, disabledIndicatorColor = Color.Transparent, errorIndicatorColor = Color.Transparent,
                focusedLabelColor = OrangeAccent, unfocusedLabelColor = TextGray, errorLabelColor = DestructiveRed
            )
        )
        if (error != null) {
            Text(text = error, color = DestructiveRed, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
        }
    }
}

private fun copyUriToInternalStorage(context: Context, sourceUri: Uri): Uri? {
    return try {
        val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return null
        val file = File(context.filesDir, "images/pet_image_${System.currentTimeMillis()}.jpg")
        file.parentFile?.mkdirs()
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        Uri.fromFile(file)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun Context.createImageUri(): Uri {
    val file = File(cacheDir, "images/pet_image_${System.currentTimeMillis()}.jpg")
    file.parentFile?.mkdirs()
    return FileProvider.getUriForFile(
        Objects.requireNonNull(this),
        "com.example.patas_y_colas.provider", file
    )
}

