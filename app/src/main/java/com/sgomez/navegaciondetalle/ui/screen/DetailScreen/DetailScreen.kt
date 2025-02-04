package com.sgomez.navegaciondetalle.ui.screen.DetailScreen

import android.icu.text.CaseMap.Title
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import com.sgomez.navegaciondetalle.model.MediaItem
import com.sgomez.navegaciondetalle.data.repositories.RemoteConectecition
import com.sgomez.navegaciondetalle.data.repositories.model.Result
import com.sgomez.navegaciondetalle.data.repositories.model.toMediaItem

import kotlinx.coroutines.launch

@Composable
fun DetailScreen(name: String) {
    // Estado para almacenar el resultado del Digimon
    val digimonState = remember { mutableStateOf<Result?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Realizar la llamada a la API en un entorno asincrónico
    LaunchedEffect(name) {
        coroutineScope.launch {
            try {
                val digimon = RemoteConectecition.service.getDigimon(name).get(0)
                val result = Result(digimon.name, digimon.img, digimon.level)
                digimonState.value = result
            } catch (e: Exception) {
                Log.e("DetailScreen", "Error fetching Digimon: ${e.message}")
            }
        }
    }

    // Mostrar el contenido cuando los datos estén listos
    digimonState.value?.let { result ->
        val mediaItem = result.toMediaItem()
        if (mediaItem != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    ImagenFull(mediaItem)
                    TitleName(mediaItem)
                    TitleLevel(mediaItem)
                }

            }

        }
    } ?: run {
        // Mostrar un indicador de carga o mensaje de error mientras se cargan los datos
        CircularProgressIndicator()
    }
}

@Composable
fun ImagenFull(item: MediaItem, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(600.dp)
            .padding(bottom = 16.dp)
    ) {

        Image(
            painter = rememberAsyncImagePainter(
                model = item.img,
                imageLoader = ImageLoader.Builder(context).crossfade(true).build()
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )

    }
}

@Composable
fun TitleName(item: MediaItem) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.Cyan)
            .padding(16.dp)
    ) {
        Text(
            text = "Nombre: "+item.name,
            style = MaterialTheme.typography.labelLarge,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun TitleLevel(item: MediaItem) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.Green)
            .padding(16.dp)
    ) {
        Text(
            text = "Nivel: " + item.level,
            style = MaterialTheme.typography.labelLarge,
            overflow = TextOverflow.Ellipsis,
        )
    }
}