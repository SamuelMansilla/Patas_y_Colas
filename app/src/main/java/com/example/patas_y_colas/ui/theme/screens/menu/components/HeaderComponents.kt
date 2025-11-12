package com.example.patas_y_colas.ui.theme.screens.menu.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.patas_y_colas.R
import com.example.patas_y_colas.model.Pet
import com.example.patas_y_colas.ui.theme.PetTextDark
import com.example.patas_y_colas.ui.theme.PetTextLight

@Composable
fun HeaderSection(
    pets: List<Pet>,
    selectedPet: Pet?,
    onPetSelected: (Pet) -> Unit,
    onAddPetClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 24.dp, end = 24.dp)
    ) {
        Text(
            text = "Tus Mascotas",
            style = MaterialTheme.typography.headlineMedium,
            color = PetTextDark,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                AddPetButton(onClick = onAddPetClicked)
            }
            items(pets) { pet ->
                PetAvatar(
                    pet = pet,
                    isSelected = pet.id == selectedPet?.id,
                    onClick = { onPetSelected(pet) }
                )
            }
        }
    }
}

@Composable
fun PetAvatar(
    pet: Pet,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) colorResource(id = R.color.naranja) else Color.Transparent

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(id = R.drawable.gato), // Imagen de placeholder
            contentDescription = pet.name,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .border(4.dp, borderColor, CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(pet.name, color = if (isSelected) PetTextDark else PetTextLight, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun AddPetButton(onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(colorResource(id = R.color.naranja).copy(alpha = 0.1f))
                .border(
                    2.dp,
                    colorResource(id = R.color.naranja),
                    RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Añadir mascota",
                tint = colorResource(id = R.color.naranja),
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("Añadir", color = PetTextLight, fontSize = 14.sp)
    }
}