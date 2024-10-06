package com.example.app_pos_compose.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app_pos_compose.data.MenuCategorySource.Categories
import com.example.app_pos_compose.data.TableSource

@Preview(showBackground = true)
@Composable
fun MenuUiPreview(){
    MenuUi(viewModel = UiViewModel())
}

@Composable
fun MenuUi(
    viewModel: UiViewModel,
    modifier: Modifier = Modifier
){
    val currentSelectedCategory = 0

    Column(
        modifier = modifier
            .fillMaxSize(),
    ) {
        Text(
            text = "UFPOS",
            modifier = modifier
                .padding(horizontal = 10.dp)
        )

        LazyRow(
            modifier
                .height(50.dp)
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .border(0.dp, color = androidx.compose.ui.graphics.Color.Black),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items(Categories.size) { index ->
                CategoryCard(Categories[index])
            }
        }

        LazyHorizontalGrid(
            rows = GridCells.Fixed(4),
            contentPadding = PaddingValues(10.dp)
        ){

        }
    }
}

@Composable
fun CategoryCard(
    title: String,
){
    Text(
        text = title,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .width(100.dp)

    )
}