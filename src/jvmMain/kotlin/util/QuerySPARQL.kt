package util

import org.apache.jena.ontology.OntModel
import org.apache.jena.query.QueryException
import org.apache.jena.query.QueryExecutionFactory
import org.apache.jena.query.QueryFactory
import org.apache.jena.query.ResultSetFormatter
import org.apache.jena.rdf.model.Model
import org.apache.jena.riot.RDFDataMgr
import org.apache.jena.riot.RDFFormat
import java.io.StringWriter
import javax.swing.text.html.StyleSheet


class QuerySPARQL {

    private val pattern by lazy {
        Regex("^(ASK|CONSTRUCT|DESCRIBE|SELECT)[ \\t\\n\\r]+")
    }

    sealed class Query(private val queryString: String) {
        val query = QueryFactory.create(queryString)!!

        class Ask(queryString: String) : Query(queryString)
        class Select(queryString: String) : Query(queryString)
        class Construct(queryString: String) : Query(queryString)
        class Describe(queryString: String) : Query(queryString)
    }

    suspend fun execute(model: OntModel, queryString: String, prefix: String): String {
        val query = getQuery(queryString, prefix = prefix)
        val exec = QueryExecutionFactory.create(query.query, model)
        try {
            when (query) {
                is Query.Ask -> {
                    val result: Boolean = exec.execAsk()
                    exec.close()
                    return "$result"
                }

                is Query.Construct -> {
                    val result: Model = exec.execConstruct()
                    val resultWriter = StringWriter()
                    RDFDataMgr.write(resultWriter, result, RDFFormat.TURTLE_PRETTY)
                    exec.close()
                    return resultWriter.toString()
                }

                is Query.Describe -> {
                    val result: Model = exec.execDescribe()
                    val resultWriter = StringWriter()
                    RDFDataMgr.write(resultWriter, result, RDFFormat.TURTLE_PRETTY)
                    exec.close()
                    return resultWriter.toString()
                }

                is Query.Select -> {
                    val result = exec.execSelect()
                    val stringRes = ResultSetFormatter.asText(result)
                    exec.close()
                    return stringRes
                }
            }
        } catch (e: Exception) {
            exec.close()
            return "${e.message} ${e.stackTrace}"
        }
    }


    private fun getQuery(queryString: String, prefix: String): Query {
        val token = pattern.find(queryString)
        return when (token?.value?.trim()) {
            "ASK" -> Query.Ask(prefix + queryString)
            "CONSTRUCT" -> Query.Construct(prefix + queryString)
            "SELECT" -> Query.Select(prefix + queryString)
            "DESCRIBE" -> Query.Describe(prefix + queryString)
            else -> throw QueryException("Type query ${token?.value} not found")
        }
    }

}