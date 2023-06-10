package util

import org.apache.jena.riot.protobuf.wire.PB_RDF.RDF_PrefixDecl

const val ONTOLOGY_PATH = "C:\\Users\\vlad\\Desktop\\ucheba\\SemanticApp\\SemanticApp\\cache\\MobileOntology.rdf"
private const val PREFIX_RDFS = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
private const val PREFIX_RDF = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
const val PREFIX_GROUP = "$PREFIX_RDF\n$PREFIX_RDFS\n"