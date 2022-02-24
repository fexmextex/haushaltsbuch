/**
 * Das ist die Datenbankzugriffsklasse.
 * bei Konstruktoraufruf:
 * 	- verbindet Sie sich mit der Datenbank "haushaltsbuch.db"
 *  	(welche sich im selben Ordner wie das Jar-File befinden muss)
 * 	- danach ließt Sie die Datenbank und speichert die Daten im "data"-Array
 * 
 * Sie bietet einige Öffentliche Methoden, welche die Datenbankkommunikation
 * 	ermöglichen.
 */

package Haushaltsbuch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBAccess {

	final private Connection connection;
	private Object[][] data;
	
	private int rowAmount = 0;
	private Double lastKontoValue = 0.0;
	
	public DBAccess() throws SQLException {
		this.connection = establishConnection();
		selectAll();
	}
	
	public Object[][] getData() {
		return this.data;
	}
	
	public double getLastKontoValue() {
		return this.lastKontoValue;
	}
	
	public int getRowAmount() {
		return this.rowAmount;
	}
	
	public void addOneToRowAmount() {
		this.rowAmount++;
	}
	
	public void removeOneFromRowAmount() {
		this.rowAmount--;
	}

	public Connection establishConnection() throws SQLException {
		String dbPath = "jdbc:sqlite:assets/haushaltsbuch.db";
		return DriverManager.getConnection(dbPath);
	}

	public void selectAll() throws SQLException {
		data = new String[rowCounter()][5];
		
		String sql = "SELECT * FROM buch";
		Statement statement = this.connection.createStatement();
		ResultSet result = statement.executeQuery(sql);
		Double ktoStand = 0.0;
		
		int z = 0;
		while (result.next()) {
			ktoStand = result.getDouble("kontostand");
			data[z][0] = ktoStand+"";
			
			Double betrag = result.getDouble("betrag");
			data[z][1] = betrag + "";
			
			data[z][2] = result.getString("kategorie");
			
			data[z][3] = result.getString("grund");
			
			data[z][4] = result.getString("datum");
			z++;
		}
		
		this.lastKontoValue = ktoStand;
	}
	
	private int rowCounter() throws SQLException {
		String sql = "SELECT count(*) FROM buch";
		Statement statement = this.connection.createStatement();
		ResultSet result = statement.executeQuery(sql);
		result.next();
		
		return this.rowAmount = result.getInt(1);
	}
	
	public void insertKontostand(final Double kontostand, final String date) throws SQLException {
		String sql = "INSERT INTO buch(kontostand,betrag,kategorie,grund,datum) VALUES ('" + kontostand + "','','-','-','" + date + "')";
		Statement statement = this.connection.createStatement();
		statement.executeUpdate(sql);
				
		Object[] tmp = {kontostand, 0.0, "-", "-", date};
		Main.dtm.addRow(tmp);
		
		this.lastKontoValue = kontostand;
	}

	public void insert(final Double betrag, final String kategorie, final String grund, final String date) throws SQLException {
		Double kontostand = this.lastKontoValue + betrag;	
		
		String sql = "INSERT INTO buch(kontostand,betrag,kategorie,grund,datum) VALUES ('" + kontostand + "','" + betrag + "','"
				+ kategorie + "','" + grund + "','" + date.toString() + "')";
		Statement statement = this.connection.createStatement();
		statement.executeUpdate(sql);

		Object[] tmp = {kontostand, betrag, kategorie, grund, date};
		Main.dtm.addRow(tmp);
		
		this.lastKontoValue = kontostand;
	}
	
	public void delete(final Object kontostand, final Object betrag, final Object kategorie, final Object grund, final Object date) throws SQLException {
		this.lastKontoValue -= Double.parseDouble(betrag.toString()); // hier
		String sqlBetrag = "";		
		if(!betrag.toString().equals("0.0")) {
			sqlBetrag = " AND betrag = " + betrag;			
		}		

		String sql = "DELETE FROM buch WHERE kontostand = " + kontostand + //
				sqlBetrag + //
				" AND kategorie = '" + kategorie + //
				"' AND grund = '" + grund + //
				"' AND datum = '" + date + "'";
		Statement statement = this.connection.createStatement();
		statement.executeUpdate(sql);
	}
	
	public void deleteEverything() throws SQLException {
		String sql = "DELETE FROM buch";
		Statement statement = this.connection.createStatement();
		statement.executeUpdate(sql);
		this.lastKontoValue = 0.0;
	}

	public void kevinDerSchlawiner(final Object kontostand, final Object betrag, final Object kategorie,
			final Object grund, final Object date) throws SQLException {
		Double doubleBetrag = Double.parseDouble(betrag.toString());
		
		String sql = "INSERT INTO buch(kontostand,betrag,kategorie,grund,datum) VALUES ('" + kontostand + "','" + betrag + "','"
				+ kategorie + "','" + grund + "','" + date.toString() + "')";
		
		Statement statement = this.connection.createStatement();
		statement.executeUpdate(sql);		
		
		this.lastKontoValue += doubleBetrag;
	}

}
