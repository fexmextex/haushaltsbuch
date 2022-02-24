package Haushaltsbuch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBAccess {

	Connection connection;
	private Object[][] data;

	Double ktoStand = 0.0;
	double kontoraus;
	
	int rows = 0;
	Double lastKontoValue = 0.0;
	
	public DBAccess() throws SQLException {
		this.connection = establishConnection();
		selectAll();
	}
	
	public double getLastKontoValue() {
		return this.lastKontoValue;
	}

	public Object[][] getData() {
		return this.data;
	}
	
	public double getKontoraus() {
		return this.kontoraus;
	}
	
	public int getRows() {
		return this.rows;
	}
	
	public void addOneRow() {
		this.rows++;
	}
	
	public void removeOneRow() {
		this.rows--;
	}

	public Connection establishConnection() throws SQLException {
		String dbPath = "jdbc:sqlite:haushaltsbuch.db";

		return DriverManager.getConnection(dbPath);
	}

	public void selectAll() throws SQLException {
		data = new String[rowCounter()][5];
		String sql = "SELECT * FROM buch";
		Statement statement = this.connection.createStatement();
		ResultSet result = statement.executeQuery(sql);

		int z = 0;
		while (result.next()) {
			ktoStand = result.getDouble("kontostand");
			data[z][0] = ktoStand+"";
			
			Double betrag = result.getDouble("betrag");
			data[z][1] = betrag + "";
			
			data[z][2] = result.getString("kategorie");
			
			data[z][3] = result.getString("grund");
			
			data[z][4] = result.getString("datum");

			System.out.println(
					(data[z][0] + " | " + data[z][1] + " | " + data[z][2] + " | " + data[z][3] + " | " + data[z][4])); // test
			z++;
		}
		kontoraus = ktoStand;
		System.out.println(kontoraus);
		
		this.lastKontoValue = ktoStand;
		

	}
	
	private int rowCounter() throws SQLException {
		String sql = "SELECT count(*) FROM buch";
		Statement statement = this.connection.createStatement();
		ResultSet result = statement.executeQuery(sql);
		result.next();
		
		return this.rows = result.getInt(1);
	}

	public void closeConnection() throws SQLException {
		this.connection.close();
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
