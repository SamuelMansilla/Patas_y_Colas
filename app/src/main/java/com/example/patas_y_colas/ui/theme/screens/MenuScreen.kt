package com.example.patas_y_colas.ui.screens

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.widget.DatePicker
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.patas_y_colas.PetApplication
import com.example.patas_y_colas.model.Pet
import com.example.patas_y_colas.viewmodel.MenuViewModel
import com.example.patas_y_colas.viewmodel.MenuViewModelFactory
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar
import java.util.Date
import java.util.Objects

// --- Paleta de Colores (sin cambios) ---
private val DarkBackground = Color(0xFF1A1A1A)
private val CardBackground = Color(0xFF2C2C2C)
private val OrangeAccent = Color(0xFFFF9800)
private val DestructiveRed = Color(0xFFE53935)
private val HeaderBlue = Color(0xFF6A79E2)
private val TextWhite = Color.White
private val TextGray = Color(0xFFBDBDBD)

// --- HeaderSection (sin cambios) ---
@Composable
fun HeaderSection(pets: List<Pet>, onPetSelected: (Pet) -> Unit, onAddPetClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
            .background(HeaderBlue)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Bienvenido", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = TextWhite)
                Icon(imageVector = Icons.Default.Settings, contentDescription = "Ajustes", tint = TextWhite.copy(alpha = 0.8f), modifier = Modifier.size(28.dp).clickable { /* TODO */ })
            }
            Text(text = "Gestiona a tus mascotas", style = MaterialTheme.typography.bodyLarge, color = TextWhite.copy(alpha = 0.8f), modifier = Modifier.padding(top = 4.dp, bottom = 24.dp))
            PetSelector(pets = pets, onPetSelected = onPetSelected, onAddPetClicked = onAddPetClicked)
        }
    }
}

// --- MenuScreen  ---
@Composable
fun MenuScreen() {
    val application = LocalContext.current.applicationContext as PetApplication
    val viewModel: MenuViewModel = viewModel(factory = MenuViewModelFactory(application.repository))

    val pets by viewModel.allPets.collectAsState()
    var selectedPet by remember { mutableStateOf<Pet?>(null) }
    var isFormVisible by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize(), color = DarkBackground) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HeaderSection(
                pets = pets,
                onPetSelected = { pet ->
                    if (selectedPet == pet && isFormVisible) {
                        isFormVisible = false
                    } else {
                        selectedPet = pet
                        isFormVisible = true
                    }
                },
                onAddPetClicked = {
                    if (selectedPet == null && isFormVisible) {
                        isFormVisible = false
                    } else {
                        selectedPet = null
                        isFormVisible = true
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = isFormVisible,
                enter = expandVertically(expandFrom = Alignment.Top, animationSpec = tween(500)),
                exit = shrinkVertically(shrinkTowards = Alignment.Top, animationSpec = tween(500))
            ) {
                PetForm(
                    pet = selectedPet,
                    onClose = { isFormVisible = false },
                    onSave = { petToSave ->
                        if (petToSave.id == 0) viewModel.insert(petToSave)
                        else viewModel.update(petToSave)
                        isFormVisible = false
                    },
                    onDelete = { petToDelete ->
                        viewModel.delete(petToDelete)
                        isFormVisible = false
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// --- PetSelector ---
@Composable
fun PetSelector(pets: List<Pet>, onPetSelected: (Pet) -> Unit, onAddPetClicked: () -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        item { AddPetCircle(onClick = onAddPetClicked) }
        items(pets) { pet -> PetCircle(pet = pet, onClick = { onPetSelected(pet) }) }
    }
}

// --- AddPetCircle  ---
@Composable
fun AddPetCircle(onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar Mascota", modifier = Modifier.size(40.dp), tint = TextWhite)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Agregar", color = TextWhite, fontWeight = FontWeight.SemiBold)
    }
}

// --- PetCircle  ---
@Composable
fun PetCircle(pet: Pet, onClick: () -> Unit) {
    val icon = when (pet.species.lowercase()) {
        "perro" -> Icons.Filled.Pets
        "gato" -> Icons.Filled.Favorite
        else -> Icons.Filled.Star
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(CardBackground)
                .border(2.5.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (pet.imageUri != null) {
                AsyncImage(
                    model = Uri.parse(pet.imageUri),
                    contentDescription = pet.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(imageVector = icon, contentDescription = pet.name, modifier = Modifier.size(40.dp), tint = OrangeAccent)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = pet.name, color = TextWhite, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
    }
}


// --- PetForm (MODIFICADO con la lógica de copiado de imagen) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetForm(pet: Pet?, onClose: () -> Unit, onSave: (Pet) -> Unit, onDelete: (Pet) -> Unit) {
    var name by remember { mutableStateOf(pet?.name ?: "") }
    var breed by remember { mutableStateOf(pet?.breed ?: "") }
    var age by remember { mutableStateOf(pet?.age ?: "") }
    var weight by remember { mutableStateOf(pet?.weight?.filter { it.isDigit() } ?: "") }
    var vaccines by remember { mutableStateOf(pet?.vaccines ?: "") }
    var nextVaccines by remember { mutableStateOf(pet?.nextVaccines ?: "") }
    var imageUri by remember { mutableStateOf(pet?.imageUri) }

    val initialSpecies = pet?.species
    var speciesOption by remember { mutableStateOf(if (initialSpecies != null && initialSpecies in listOf("Perro", "Gato")) initialSpecies else "Otro") }
    var otherSpecies by remember { mutableStateOf(if (initialSpecies != null && initialSpecies !in listOf("Perro", "Gato")) initialSpecies else "") }

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
                // Copiamos la imagen a nuestro almacenamiento interno y guardamos la nueva URI
                val newUri = copyUriToInternalStorage(context, it)
                imageUri = newUri?.toString()
            }
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                imageUri = tempImageUri?.toString()
            }
        }
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
        // ... (lógica de validación sin cambios)
        return true
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ... (el resto del formulario no cambia)
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
                    colors = TextFieldDefaults.outlinedTextFieldColors(disabledTextColor = TextWhite, disabledBorderColor = TextGray, disabledLabelColor = TextGray, disabledTrailingIconColor = TextGray)
                )
            }

            Button(
                onClick = { showImageSourceDialog = true },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = OrangeAccent),
                elevation = null,
                border = BorderStroke(1.dp, OrangeAccent)
            ) {
                Text("Seleccionar Foto")
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (pet != null) {
                    Button(onClick = { onDelete(pet) }, modifier = Modifier.weight(1f).height(50.dp), shape = CircleShape, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = DestructiveRed), border = BorderStroke(1.dp, DestructiveRed)) {
                        Text(text = "Eliminar")
                    }
                }
                Button(
                    onClick = {
                        if (validate()) {
                            val finalSpecies = if (speciesOption == "Otro") otherSpecies else speciesOption
                            val petToSave = Pet(id = pet?.id ?: 0, name = name, species = finalSpecies, breed = breed, age = age, weight = "$weight Kg", vaccines = vaccines, nextVaccines = nextVaccines, imageUri = imageUri)
                            onSave(petToSave)
                        }
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
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
            confirmButton = {
                Button(onClick = {
                    showImageSourceDialog = false
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }) { Text("Cámara") }
            },
            dismissButton = {
                Button(onClick = {
                    showImageSourceDialog = false
                    imagePickerLauncher.launch("image/*")
                }) { Text("Galería") }
            }
        )
    }
}

// --- PetTextField  ---
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

// --- Función de ayuda para crear la URI de la imagen de la cámara (CORREGIDA) ---
fun Context.createImageUri(): Uri {
    val file = File(cacheDir, "images/pet_image_${System.currentTimeMillis()}.jpg")
    file.parentFile?.mkdirs()
    return FileProvider.getUriForFile(
        Objects.requireNonNull(this),
        "com.example.patas_y_colas.provider", file
    )
}

// --- NUEVA FUNCIÓN DE AYUDA para copiar la imagen de la galería ---
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

