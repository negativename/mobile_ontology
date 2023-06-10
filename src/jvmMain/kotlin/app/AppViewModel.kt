package app

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.apache.jena.ontology.Individual
import org.apache.jena.ontology.OntClass
import org.apache.jena.ontology.OntModel
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdfconnection.RDFConnection
import org.apache.jena.riot.Lang
import org.apache.jena.riot.RDFDataMgr
import org.apache.jena.riot.RDFParser
import org.apache.jena.vocabulary.RDF
import org.apache.jena.vocabulary.RDFS
import util.ONTOLOGY_PATH
import util.PREFIX_GROUP
import util.QuerySPARQL
import java.io.FileOutputStream


class AppViewModel : ViewModel() {

    private val appState: MutableStateFlow<AppState> = MutableStateFlow(AppState())
    val uiAppState: StateFlow<AppState> get() = appState

    private val model: OntModel = ModelFactory.createOntologyModel()
    private val queries = QuerySPARQL()

    fun loadModel() {
        viewModelScope.launch(Dispatchers.IO) {
            model.read(ONTOLOGY_PATH, null, "RDF/XML")
            val map = model.nsPrefixMap
            var prefix = ""
            var uri = ""
            model.nsPrefixMap.forEach() {
                prefix = it.key
                uri = it.value
            }
            appState.compareAndSet(
                appState.value,
                appState.value.copy(
                    ontologyIsLoaded = true,
                    prefix = prefix,
                    ontUri = uri,
                    currentIndividual = null,
                    currentClass = null
                )
            )
        }
    }

    fun changeScreen() {
        appState.update {
            it.copy(
                currentScreen = if (it.currentScreen == Route.SparqlScreen)
                    Route.DefaultScreen else Route.SparqlScreen
            )
        }
    }

    fun downloadServer() {
        val inputFileName = ONTOLOGY_PATH
        val `in` = RDFDataMgr.open(inputFileName)
                ?: throw IllegalArgumentException("File: $inputFileName not found")


        RDFParser.source(`in`)
                .forceLang(Lang.RDFXML)
                .parse(model)

        var conn = RDFConnection.connect("http://127.0.0.1:3030/ds")
        conn.load(model);
    }

    fun saveModel() {
        model.write(FileOutputStream(ONTOLOGY_PATH), "RDF/XML")
    }

    fun getAllClasses() {
        viewModelScope.launch(Dispatchers.IO) {
            val classes = model.listClasses()
            appState.compareAndSet(appState.value, appState.value.copy(listClasses = classes.toList()))
        }
    }

    fun createClass() {
        model.createClass(appState.value.ontUri + "Test_Class")
    }

    fun deleteClass() {
        val ontClass = model.getOntClass(appState.value.ontUri + "Test_Class")
        model.removeAll(ontClass, null, null)
        model.removeAll(null, null, ontClass)
        model.remove(ontClass, RDF.type, RDFS.Class)
        appState.compareAndSet(appState.value, appState.value.copy(currentIndividual = null))
    }

    fun getAllIndividuals() {
        viewModelScope.launch(Dispatchers.IO) {
            appState.compareAndSet(
                appState.value,
                appState.value.copy(listIndividual = model.listIndividuals().toList())
            )
        }
    }

    fun createIndividual() {
        val individual = model.createResource(appState.value.ontUri + "test_individual")
        model.add(individual, RDF.type, appState.value.currentClass)
    }

    fun deleteIndividual(individual: Individual) {
        model.removeAll(individual, null, null)
        model.removeAll(null, null, individual)
        appState.compareAndSet(appState.value, appState.value.copy(currentIndividual = null))
    }


    fun executeQuery() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = queries.execute(
                    model = model,
                    queryString = appState.value.queryString,
                    prefix = "$PREFIX_GROUP PREFIX ${appState.value.prefix}: <${appState.value.ontUri}>"
                )
                appState.compareAndSet(appState.value, appState.value.copy(queryResult = result))
            } catch (e: Exception) {
                appState.compareAndSet(appState.value, appState.value.copy(queryResult = "${e.message}"))
            }
        }
    }

    //FOR UI
    fun enterClass(ontClass: OntClass) {
        appState.compareAndSet(appState.value, appState.value.copy(currentClass = ontClass))
    }

    fun enterIndividual(individual: Individual) {
        appState.compareAndSet(appState.value, appState.value.copy(currentIndividual = individual))
    }

    fun onQueryChange(s: String) {
        appState.compareAndSet(
            appState.value,
            appState.value.copy(
                queryString = s,
                queryResult = ""
            )
        )
    }
}