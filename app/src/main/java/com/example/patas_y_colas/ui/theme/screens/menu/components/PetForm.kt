package com.example.patas_y_colas.ui.theme.screens.menu.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.patas_y_colas.R
import com.example.patas_y_colas.model.Pet
import com.example.patas_y_colas.model.VaccineRecord

@Composable
fun PetForm(
    pet: Pet?,
    onSave: (Pet) -> Unit,
    onDelete: (Pet) -> Unit
) {
    var name by remember(pet) { mutableStateOf(pet?.name ?: "") }
    var species by remember(pet) { mutableStateOf(pet?.species ?: "") }
    var breed by remember(pet) { mutableStateOf(pet?.breed ?: "") }
    var age by remember(pet) { mutableStateOf(pet?.age ?: "") }
    var weight by remember(pet) { mutableStateOf(pet?.weight ?: "") }

    // --- ¡CAMBIO IMPORTANTE! ---
    // Ahora usamos una lista de 'VaccineRecord' directamente
    var vaccineRecords by remember(pet) { mutableStateOf(pet?.vaccineRecords ?: emptyList()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (pet == null) "Nueva Mascota" else "Editar Mascota",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = species,
                    onValueChange = { species = it },
                    label = { Text("Especie") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = breed,
                    onValueChange = { breed = it },
                    label = { Text("Raza") },
                    modifier = Modifier.weight(1f)
                )
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Edad") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Peso (kg)") },
                    modifier = Modifier.weight(1f)
                )
            }

            // --- Sección de Vacunas (MODIFICADA) ---
            Text(
                "Vacunas",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp)
            )

            vaccineRecords.forEachIndexed { index, vaccine ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = vaccine.vaccineName,
                        onValueChange = { newName ->
                            val newList = vaccineRecords.toMutableList()
                            newList[index] = vaccine.copy(vaccineName = newName)
                            vaccineRecords = newList
                        },
                        label = { Text("Vacuna") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = vaccine.date,
                        onValueChange = { newDate ->
                            val newList = vaccineRecords.toMutableList()
                            newList[index] = vaccine.copy(date = newDate)
                            vaccineRecords = newList
                        },
                        label = { Text("Fecha (d/m/aaaa)") },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        val newList = vaccineRecords.toMutableList()
                        newList.removeAt(index)
                        vaccineRecords = newList
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar vacuna")
                    }
                }
            }

            Button(
                onClick = {
                    vaccineRecords = vaccineRecords + VaccineRecord(vaccineName = "", date = "")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Añadir Vacuna")
            }

            // --- Botones de Acción ---
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        val petToSave = Pet(
                            id = pet?.id ?: 0,
                            name = name,
                            species = species,
                            breed = breed,
                            age = age,
                            weight = weight,
                            imageUri = pet?.imageUri, // Mantenemos la imagen si existe
                            vaccineRecords = vaccineRecords // Usamos la lista actualizada
                        )
                        onSave(petToSave)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.naranja)
                    )
                ) {
                    Text(if (pet == null) "Guardar" else "Actualizar")
                }

                if (pet != null) {
                    Button(
                        onClick = { onDelete(pet) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Eliminar")
                    }
                }
            }
        }
    }
}