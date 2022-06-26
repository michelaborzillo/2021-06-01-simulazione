package it.polito.tdp.genes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.genes.db.GenesDao;

public class Model {
	
	GenesDao dao;
	Map<String, Genes> idMap;
	Graph<Genes, DefaultWeightedEdge> grafo;
	
	public Model () {
		dao= new GenesDao();
		idMap= new HashMap<>();
	}
	
	public void creaGrafo() {
		this.grafo= new SimpleWeightedGraph<Genes, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		dao.getAllGenes(idMap);
		List<Genes> geni= dao.getVertici(idMap);
		
		Graphs.addAllVertices(this.grafo, geni);
		for (Adiacenze a: dao.getCoppieGeni(idMap, geni)) {
			Graphs.addEdgeWithVertices(this.grafo, a.getG1(), a.getG2(), this.pesoGrafo(a.getG1(), a.getG2()));
		}
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
		
	}

	public int nArchi() {
		return this.grafo.edgeSet().size();
	
	}
	
	public double pesoGrafo (Genes g1, Genes g2) {
		return dao.pesoStessoCromosoma(g1, g2);
	}

	public Set<Genes> getGeni() {
	return this.grafo.vertexSet();
	}
	
	
	public List<GeniAdiacenti> getAdiacenze(Genes g) {
		List<GeniAdiacenti> vicini= new ArrayList<GeniAdiacenti>();
		List<Genes> viciniId= Graphs.neighborListOf(this.grafo, g);
		for (Genes v: viciniId) {
			vicini.add(new GeniAdiacenti(v, this.pesoGrafo(v,g)));
			
		}
		Collections.sort(vicini);
	
		return vicini;
		
	}
}
