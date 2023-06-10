package app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.apache.jena.ontology.Individual
import org.apache.jena.ontology.OntClass
import ui.DefaultScreen
import ui.SparqlScreen

sealed class Route {
    object SparqlScreen : Route()
    object DefaultScreen : Route()
}

data class AppState(
    val listClasses: List<OntClass> = emptyList(),
    val listIndividual: List<Individual> = emptyList(),
    val currentClass: OntClass? = null,
    val currentIndividual: Individual? = null,
    val ontologyIsLoaded: Boolean = false,
    val prefix: String = "",
    val ontUri: String = "",
    val queryString: String = "",
    val queryResult: String = "",
    val currentScreen: Route = Route.SparqlScreen
) {

    fun getCurrentOntClassProperty(): List<Pair<String, String>> {
        val propMap: MutableMap<String, String> = mutableMapOf()
        currentClass?.listProperties()?.forEachRemaining {
            propMap["${it.predicate.localName} "] =
                if (it.`object`.isLiteral) it.literal.value.toString() else "${it.resource.localName} "
        }
        return propMap.toList()
    }

    fun getCurrentIndividualsProperty(): List<Pair<String, String>> {
        val propMap: MutableMap<String, String> = mutableMapOf()
        currentIndividual?.listProperties()?.forEachRemaining {
            propMap["${it.predicate.localName} "] =
                if (it.`object`.isLiteral) it.literal.value.toString() else "${it.resource.localName} "
        }
        return propMap.toList()
    }

}


@Composable
fun App(viewModel: AppViewModel) {
    val state by viewModel.uiAppState.collectAsState()
    Column(modifier = Modifier.fillMaxSize().background(Color.LightGray)) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .background(MaterialTheme.colors.primary),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = viewModel::loadModel,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.onPrimary)
            ) {
                Text("Загрузить онтологию")
            }

            TextButton(
                onClick = viewModel::getAllClasses,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.onPrimary),
                enabled = state.ontologyIsLoaded
            ) {
                Text("Загрузить классы")
            }

            TextButton(
                onClick = viewModel::getAllIndividuals,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.onPrimary),
                enabled = state.ontologyIsLoaded
            ) {
                Text("Загрузить индивидуумов")
            }
            TextButton(
                onClick = viewModel::saveModel,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.onPrimary),
                enabled = state.ontologyIsLoaded
            ) {
                Text("Сохранить Модель")
            }
            TextButton(
                onClick = viewModel::createClass,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.onPrimary),
                enabled = state.ontologyIsLoaded
            ) {
                Text("Создать Класс")
            }
            TextButton(
                onClick = viewModel::deleteClass,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.onPrimary),
                enabled = state.ontologyIsLoaded
            ) {
                Text("Удалить Класс")
            }
            TextButton(
                onClick = viewModel::changeScreen,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.onPrimary),
                enabled = state.ontologyIsLoaded
            ) {
                Text("Другой экран")
            }
            TextButton(
                    onClick = viewModel::downloadServer,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.onPrimary),
                    enabled = state.ontologyIsLoaded
            ) {
                Text("Загрузить на сервер")
            }
        }
        if (state.ontologyIsLoaded) {
            Text(text = "${state.prefix} ${state.ontUri}", modifier = Modifier.padding(vertical = 8.dp))
            Divider(Modifier.fillMaxWidth(), color = MaterialTheme.colors.primary)
        }
        AnimatedVisibility(state.ontologyIsLoaded) {
            when (state.currentScreen) {
                is Route.DefaultScreen -> {
                    DefaultScreen(
                        state = state,
                        onClassClick = viewModel::enterClass,
                        onCreateIndividualClick = viewModel::createIndividual,
                        onIndividualClick = viewModel::enterIndividual,
                        onDeleteIndividualClick = { viewModel.deleteIndividual(state.currentIndividual!!) },
                    )
                }
                is Route.SparqlScreen -> {
                    SparqlScreen(
                        value = state.queryString,
                        onValueChange = viewModel::onQueryChange,
                        onButtonClick = viewModel::executeQuery,
                        result = state.queryResult
                    )

                }
            }
        }


    }
}
