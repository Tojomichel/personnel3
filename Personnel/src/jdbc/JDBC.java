package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import personnel.GestionPersonnel;
import personnel.Ligue; // Ajout de l'import pour Credentials
import personnel.Passerelle;
import personnel.SauvegardeImpossible;
import personnel.Employe;

public class JDBC implements Passerelle {

    Connection connection;

    public JDBC() {
        try {
            Class.forName(Credentials.getDriverClassName());
            connection = DriverManager.getConnection(Credentials.getUrl(), Credentials.getUser(), Credentials.getPassword());
        } catch (ClassNotFoundException e) {
            System.out.println("Pilote JDBC non installé.");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public GestionPersonnel getGestionPersonnel() {
        GestionPersonnel gestionPersonnel = new GestionPersonnel();
        try {
            String requete = "select * from ligue";
            Statement instruction = connection.createStatement();
            ResultSet ligues = instruction.executeQuery(requete);
            while (ligues.next()) {
                gestionPersonnel.addLigue(ligues.getInt(1), ligues.getString(2));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return gestionPersonnel;
    }

    @Override
    public void sauvegarderGestionPersonnel(GestionPersonnel gestionPersonnel) throws SauvegardeImpossible {
        close();
    }

    public void close() throws SauvegardeImpossible {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new SauvegardeImpossible(e);
        }
    }

    @Override
    public int insert(Ligue ligue) throws SauvegardeImpossible {
        try {
            PreparedStatement instruction;
            instruction = connection.prepareStatement("insert into ligue (nomLigue) values(?)", Statement.RETURN_GENERATED_KEYS);
            instruction.setString(1, ligue.getNom());
            instruction.executeUpdate();
            ResultSet id = instruction.getGeneratedKeys();
            id.next();
            return id.getInt(1);
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new SauvegardeImpossible(exception);
        }
    }

    @Override
    public int insert(Employe employe) throws SauvegardeImpossible {
        try {
            PreparedStatement instruction = connection.prepareStatement(
                    "INSERT INTO utilisateur (nomUtil, prenomUtil, mailUtil, passwordUtil, date_arrivee, date_depart, numLigue, admin) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            instruction.setString(1, employe.getNom());
            instruction.setString(2, employe.getPrenom());
            instruction.setString(3, employe.getMail());
            instruction.setString(4, employe.getPassword());
            instruction.setDate(5, java.sql.Date.valueOf(employe.getDateArrivee()));
            instruction.setDate(6, java.sql.Date.valueOf(employe.getDateDepart()));
            instruction.setInt(7, employe.getLigue().getIdLigue());
            instruction.setBoolean(8, employe.estAdmin(employe.getLigue()));
            instruction.executeUpdate();
            ResultSet id = instruction.getGeneratedKeys();
            id.next();
            return id.getInt(1);
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new SauvegardeImpossible(exception);
        }
    }
}
