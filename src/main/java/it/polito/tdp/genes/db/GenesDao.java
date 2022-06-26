package it.polito.tdp.genes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.genes.model.Adiacenze;
import it.polito.tdp.genes.model.Genes;


public class GenesDao {
	
	public List<Genes> getAllGenes(Map<String, Genes> idMap){
		String sql = "SELECT DISTINCT GeneID, Essential, Chromosome FROM Genes";
		List<Genes> result = new ArrayList<Genes>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Genes genes = new Genes(res.getString("GeneID"), 
						res.getString("Essential"), 
						res.getInt("Chromosome"));
				idMap.put(genes.getGeneId(), genes);
				result.add(genes);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public List<Genes> getVertici (Map<String, Genes>idMap){
		String sql="SELECT DISTINCT g.GeneID AS id "
				+ "FROM genes g "
				+ "WHERE g.Essential='Essential'";
		List<Genes> result = new ArrayList<Genes>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Genes genes = idMap.get(res.getString("id"));
				//if (!genes.equals(null))
				result.add(genes);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}

	public double pesoStessoCromosoma (Genes gg1, Genes gg2) {
		String sql="SELECT distinct i.Expression_Corr AS corr,  g1.Chromosome AS g1Crom, g2.Chromosome AS g2Crom "
				+ "FROM genes g1, genes g2, interactions i "
				+ "WHERE (i.GeneID1=g1.GeneID OR i.GeneID1=g2.GeneID) AND (i.GeneID2=g2.GeneID OR i.GeneID2=g1.GeneID) "
				+ "AND g1.GeneID=? AND g2.GeneID=?";
		double peso=0;
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, gg1.getGeneId());
			st.setString(2, gg2.getGeneId());
			ResultSet res = st.executeQuery();
			if (res.next()) {
				int g1=res.getInt("g1Crom");
				int g2= res.getInt("g2Crom");
				if (g1==g2) {
					peso=res.getDouble("corr")*2;
				}
				else 
					peso=res.getDouble("corr");
				
			}
			res.close();
			st.close();
			conn.close();
			return Math.abs(peso);
			
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
		
	}
	
	
	public List<Adiacenze> getCoppieGeni (Map<String, Genes> idMap, List<Genes> vertici) {
		String sql ="SELECT DISTINCT i.Expression_Corr AS num,  g1.GeneID AS id1, g2.GeneID AS id2 "
				+ "FROM genes g1, genes g2, interactions i "
				+ "WHERE i.GeneID1=g1.GeneID AND i.GeneID2=g2.GeneID "
				+ "AND g1.Essential='Essential' AND g2.Essential='Essential' AND i.GeneID1<>i.GeneID2";
		List<Adiacenze> result = new ArrayList<Adiacenze>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				Genes g1= idMap.get(res.getString("id1"));
				Genes g2=idMap.get(res.getString("id2"));
				if (vertici.contains(g1) && vertici.contains(g2))
				result.add(new Adiacenze(g1, g2, res.getDouble("num")));
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	
}
