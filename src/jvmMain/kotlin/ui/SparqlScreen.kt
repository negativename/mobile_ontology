package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

@Composable
fun SparqlScreen(value: String, result: String, onValueChange: (String) -> Unit, onButtonClick: () -> Unit) {
    Row {
        Column(modifier = Modifier.weight(1f).padding(24.dp)) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text("SELECT...") },
                trailingIcon = {
                    IconButton(onClick = onButtonClick) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null)
                    }
                }
            )
        }
        LazyColumn(modifier = Modifier.weight(1f).padding(24.dp)) {
            item {
                Text(text = "Результат: ")
            }
            item {
                val scroll = rememberScrollState()
                SelectionContainer {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color.Black,
                                shape = MaterialTheme.shapes.medium
                            )
                            .horizontalScroll(state = scroll)
                    ) {
                        Text(
                            modifier = Modifier.padding(4.dp),
                            text = result,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                        )
                    }
                }
            }
        }
    }
}