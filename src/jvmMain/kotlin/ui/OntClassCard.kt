package ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OntClassCard(localName: String, isClicked: Boolean, onClick: () -> Unit) {
    Card(onClick = onClick, enabled = !isClicked) {
        Column(modifier = Modifier.width(400.dp)) {
            Text(text = localName, style = MaterialTheme.typography.h5, overflow = TextOverflow.Ellipsis)
        }
    }
}