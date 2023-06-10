package ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.AppState
import org.apache.jena.ontology.Individual
import org.apache.jena.ontology.OntClass

@Composable
fun DefaultScreen(
    state: AppState,
    onClassClick: (OntClass)->Unit,
    onCreateIndividualClick: ()->Unit,
    onIndividualClick: (Individual)->Unit,
    onDeleteIndividualClick: ()->Unit,
    ){
    Row() {
        LazyColumn {
            items(state.listClasses) {
                if (it.localName != null) {
                    OntClassCard(
                        localName = it.localName,
                        isClicked = if (state.currentClass != null) state.currentClass == it else false,
                        onClick = {onClassClick(it)}
                    )
                }
            }
        }
        if (state.currentClass != null) {
            val scrollState = rememberScrollState()
            Card {
                Column(modifier = Modifier.horizontalScroll(scrollState)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Название класса: ${state.currentClass!!.localName}")
                        Button(onClick = onCreateIndividualClick) {
                            Text("Создать тестового индивида")
                        }
                    }
                    Text("Свойства")
                    LazyColumn {
                        items(state.getCurrentOntClassProperty()) {
                            Text("${it.first}: ${it.second}")
                        }
                    }
                }
            }
        }
        LazyColumn {
            items(state.listIndividual) {
                if (it.localName != null) {
                    OntPoropertyCard(it.localName, onClick = {onIndividualClick(it)})
                }
            }
        }
        if (state.currentIndividual != null) {
            val scrollState = rememberScrollState()
            Column(modifier = Modifier.horizontalScroll(scrollState)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Название класса: ${state.currentIndividual!!.localName}")
                    Button(onClick = onDeleteIndividualClick) {
                        Text("Удалить индивидуума")
                    }
                }
                Text("Свойства")
                LazyColumn {
                    items(state.getCurrentIndividualsProperty()) {
                        Text("${it.first}: ${it.second}")
                    }
                }
            }
        }
    }
}