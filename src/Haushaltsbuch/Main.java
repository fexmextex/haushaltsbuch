/**
 * delete akualisiert den kontostand nicht!
 * initial kontostand wird nur aus der tabelle gel�scht, aber nicht aus der Datenbank
 * datumsformat ist schlecht!
 */


package Haushaltsbuch;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;

@SuppressWarnings("serial")
public class Main extends JFrame implements ActionListener{	
	private DBAccess dbAccess;
	
	private JPanel contentPane;
	
	private JButton btnKontoAendern;
	private JLabel lblKontoWert;
	private JTextField txtKonto;
	private JButton btnEinAus;
	private JTextField txtSumme;
	private JComboBox<String> comboBoxKategorie;
	private JTextField txtGrund;
	private JDateChooser dateChooser;
	private JButton btnBuchung;
	private JButton btnDelete;
	
	private JTable table;
	static DefaultTableModel dtm;
	private int selectedIdx = -1;
	private Object[] toDelete;
	

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					Main frame = new Main();
					frame.getContentPane().setPreferredSize(new Dimension(700, 475));
					frame.pack();
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Main() throws SQLException {			
		dbAccess = new DBAccess();
		
		this.setTitle("Haushaltsbuch");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setContentPane(contentPane);
		this.contentPane.setLayout(null);
		
		JLabel lblKontostand = new JLabel("Kontostand");
		lblKontostand.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblKontostand.setBounds(230, 20, 87, 16);
		contentPane.add(lblKontostand);
		
		lblKontoWert = new JLabel(dbAccess.getKontoraus() + " �");
		lblKontoWert.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblKontoWert.setBounds(320, 20, 133, 16);
		contentPane.add(lblKontoWert);
		
		btnKontoAendern = new JButton("Kontostand \u00E4ndern");
		btnKontoAendern.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnKontoAendern.setBounds(27, 12, 176, 32);
		btnKontoAendern.addActionListener(this);	
		contentPane.add(btnKontoAendern);
		
		txtKonto = new JTextField();
		txtKonto.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtKonto.setBounds(27, 61, 176, 32);
		contentPane.add(txtKonto);
		txtKonto.setColumns(10);
		
		JLabel lblSumme = new JLabel("Summe:");
		lblSumme.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblSumme.setBounds(27, 140, 151, 39);
		contentPane.add(lblSumme);
		
		txtSumme = new JTextField();
		txtSumme.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtSumme.setColumns(10);
		txtSumme.setBounds(27, 170, 176, 32);
		contentPane.add(txtSumme);
		
		JLabel lblkategorie = new JLabel("Kategorie:");
		lblkategorie.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblkategorie.setBounds(27, 203, 151, 39);
		contentPane.add(lblkategorie);
		
		String[] kategorie = {"Gehalt", "Miete", "Lebensmittel", "Auto", "Computer", "Freizeit", "Haushalt"};
		comboBoxKategorie = new JComboBox<>(kategorie);
		comboBoxKategorie.setFont(new Font("Tahoma", Font.PLAIN, 14));
		comboBoxKategorie.setBounds(27, 235, 176, 32);
		comboBoxKategorie.addActionListener(this);		
		contentPane.add(comboBoxKategorie);
		
		JLabel lbldatum = new JLabel("Datum");
		lbldatum.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lbldatum.setBounds(27, 330, 151, 39);
		contentPane.add(lbldatum);
		
		btnBuchung = new JButton("Buchung");
		btnBuchung.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnBuchung.addActionListener(this);
		btnBuchung.setBounds(26, 410, 177, 35);
		contentPane.add(btnBuchung);		
		
		makeTable();		
		
		JLabel lblGrund = new JLabel("Grund:");
		lblGrund.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblGrund.setBounds(27, 270, 151, 39);
		contentPane.add(lblGrund);
		
		txtGrund = new JTextField();
		txtGrund.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtGrund.setColumns(10);
		txtGrund.setBounds(27, 300, 176, 32);
		contentPane.add(txtGrund);
		
		dateChooser = new JDateChooser();
		dateChooser.setBounds(27, 361, 176, 34);
		contentPane.add(dateChooser);
		
		
		btnEinAus = new JButton("Auszahlung");
		btnEinAus.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnEinAus.setBounds(27, 110, 176, 32);
		btnEinAus.setForeground(Color.WHITE);
		btnEinAus.setBackground(Color.RED);
		btnEinAus.addActionListener(this);	
		contentPane.add(btnEinAus);
		
		btnDelete = new JButton("Delete");
		btnDelete.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnDelete.setBounds(498, 410, 177, 35);
		btnDelete.addActionListener(this);
		contentPane.add(btnDelete);
	}	
	
	
	public void makeTable() {		
		String[] columNames = {"Kontostand", "Betrag", "Kategorie", "Grund", "Datum"};

		if(dbAccess.getData() != null) {
			dtm = new DefaultTableModel(dbAccess.getData(), columNames);
		} else {
			Object[] tmpo = {0};
			Object[][] tmpo2 = {tmpo};
			dtm = new DefaultTableModel(tmpo2, columNames);
		}		
		
		table = new JTable(dtm);
				
		ListSelectionModel lsm = table.getSelectionModel();
		lsm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);		
		lsm.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectedIdx = table.getSelectedRow();
			}
		});

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(225, 53, 450, 343);
		scrollPane.setBorder(BorderFactory.createSoftBevelBorder(BevelBorder.LOWERED));
		scrollPane.setAutoscrolls(true);		
		contentPane.add(scrollPane);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.btnKontoAendern) {	
			lblKontoWert.setText(txtKonto.getText() + " �");
			try {
				dbAccess.insert(Integer.parseInt(txtKonto.getText()));
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}			
		}
		
		if(e.getSource() == this.btnEinAus) {
			if(btnEinAus.getText().equals("Auszahlung")) {
				btnEinAus.setText("Einzahlung");
				btnEinAus.setBackground(Color.GREEN);
			} else {
				btnEinAus.setText("Auszahlung");
				btnEinAus.setBackground(Color.RED);				
			}
		}
		
		if(e.getSource() == this.btnBuchung) {
			
			String kategorie = comboBoxKategorie.getSelectedItem().toString();
			String grund = txtGrund.getText();
			Date date = dateChooser.getDate();
			
			Double betrag = Double.parseDouble(txtSumme.getText());
			if(betrag < 0) {
				betrag = betrag * (-1);
			}
			
			if(btnEinAus.getText().equals("Auszahlung")) {
				betrag = betrag * (-1);
			}			
			
			try {				
				dbAccess.insert(betrag, kategorie, grund, date);	
				lblKontoWert.setText(dbAccess.getLastKontoValue() + " �");
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		
		if(e.getSource() == this.btnDelete) {			
			System.out.println(selectedIdx);
			toDelete = new Object[5];
			toDelete[0] = table.getValueAt(selectedIdx, 0);
			toDelete[1] = table.getValueAt(selectedIdx, 1);
			toDelete[2] = table.getValueAt(selectedIdx, 2);
			toDelete[3] = table.getValueAt(selectedIdx, 3);
			toDelete[4] = table.getValueAt(selectedIdx, 4);

			if(selectedIdx > -1) {
				try {
					dbAccess.delete(toDelete[0], toDelete[1], toDelete[2], toDelete[3], toDelete[4]);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			dtm.removeRow(selectedIdx);			
		}		
	}
}
