package com.example.patas_y_colas.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.patas_y_colas.PetApplication
import com.example.patas_y_colas.model.Pet
import com.example.patas_y_colas.ui.screens.menu.components.HeaderSection
import com.example.patas_y_colas.ui.screens.menu.components.PetForm
import com.example.patas_y_colas.ui.screens.menu.components.ReminderSection
import com.example.patas_y_colas.ui.theme.*
import com.example.patas_y_colas.ui.utils.rememberWindowSizeClass
import com.example.patas_y_colas.viewmodel.MenuViewModel
import com.example.patas_y_colas.viewmodel.MenuViewModelFactory

@Composable
fun MenuScreen() {
    val application = LocalContext.current.applicationContext as PetApplication
    val viewModel: MenuViewModel = viewModel(factory = MenuViewModelFactory(application.repository))
    val pets by viewModel.allPets.collectAsState()

    var selectedPet by remember { mutableStateOf<Pet?>(null) }
    var isFormVisible by remember { mutableStateOf(false) }

    val windowSizeClass = rememberWindowSizeClass()

    if (windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact) {
        isFormVisible = true
        if (selectedPet == null && pets.isNotEmpty()) {
            selectedPet = pets.first()
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = PetBackground) { // Color de fondo principal
        when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact -> {
                MenuScreenCompact(
                    pets = pets,
                    selectedPet = selectedPet,
                    isFormVisible = isFormVisible,
                    onPetSelected = { pet ->
                        if (selectedPet == pet) isFormVisible = !isFormVisible
                        else { selectedPet = pet; isFormVisible = true }
                    },
                    onAddPetClicked = { selectedPet = null; isFormVisible = true },
                    onFormAction = { action ->
                        when(action) {
                            is FormAction.Save -> if (action.pet.id == 0) viewModel.insert(action.pet) else viewModel.update(action.pet)
                            is FormAction.Delete -> viewModel.delete(action.pet)
                        }
                        isFormVisible = false
                    }
                )
            }
            else -> {
                MenuScreenExpanded(
                    pets = pets,
                    selectedPet = selectedPet,
                    onPetSelected = { pet -> selectedPet = pet },
                    onAddPetClicked = { selectedPet = null },
                    onFormAction = { action ->
                        when(action) {
                            is FormAction.Save -> if (action.pet.id == 0) viewModel.insert(action.pet) else viewModel.update(action.pet)
                            is FormAction.Delete -> viewModel.delete(action.pet)
                        }
                        selectedPet = null
                    }
                )
            }
        }
    }
}

@Composable
fun MenuScreenCompact(
    pets: List<Pet>,
    selectedPet: Pet?,
    isFormVisible: Boolean,
    onPetSelected: (Pet) -> Unit,
    onAddPetClicked: () -> Unit,
    onFormAction: (FormAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HeaderSection(pets = pets, onPetSelected = onPetSelected, onAddPetClicked = onAddPetClicked)
        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(
            visible = isFormVisible,
            enter = expandVertically(expandFrom = Alignment.Top, animationSpec = tween(500)),
            exit = shrinkVertically(shrinkTowards = Alignment.Top, animationSpec = tween(500))
        ) {
            PetForm(
                pet = selectedPet,
                onSave = { onFormAction(FormAction.Save(it)) },
                onDelete = { onFormAction(FormAction.Delete(it)) }
            )
        }

        AnimatedVisibility(
            visible = !isFormVisible,
            enter = fadeIn(animationSpec = tween(500)),
            exit = fadeOut(animationSpec = tween(500))
        ) {
            ReminderSection(pets = pets)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun MenuScreenExpanded(
    pets: List<Pet>,
    selectedPet: Pet?,
    onPetSelected: (Pet) -> Unit,
    onAddPetClicked: () -> Unit,
    onFormAction: (FormAction) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderSection(pets = pets, onPetSelected = onPetSelected, onAddPetClicked = onAddPetClicked)
            Spacer(modifier = Modifier.height(24.dp))
            ReminderSection(pets = pets)
        }
        Column(
            modifier = Modifier
                .weight(1.5f)
                .verticalScroll(rememberScrollState())
                .padding(top = 24.dp, bottom = 16.dp, end = 24.dp, start = 8.dp)
        ) {
            PetForm(
                pet = selectedPet,
                onSave = { onFormAction(FormAction.Save(it)) },
                onDelete = { onFormAction(FormAction.Delete(it)) }
            )
        }
    }
}

sealed class FormAction {
    data class Save(val pet: Pet) : FormAction()
    data class Delete(val pet: Pet) : FormAction()
}

