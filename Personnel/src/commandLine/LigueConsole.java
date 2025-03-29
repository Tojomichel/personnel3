package commandLine;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static commandLineMenus.rendering.examples.util.InOut.getString;

import java.util.ArrayList;

import commandLineMenus.List;
import commandLineMenus.Menu;
import commandLineMenus.Option;

import personnel.*;

public class LigueConsole 
{
	private GestionPersonnel gestionPersonnel;
	private EmployeConsole employeConsole;

	public LigueConsole(GestionPersonnel gestionPersonnel, EmployeConsole employeConsole)
	{
		this.gestionPersonnel = gestionPersonnel;
		this.employeConsole = employeConsole;
	}

	Menu menuLigues()
	{
		Menu menu = new Menu("Gérer les ligues", "l");
		menu.add(afficherLigues());
		menu.add(ajouterLigue());
		menu.add(selectionnerLigue());
		menu.addBack("q");
		return menu;
	}

	private Option afficherLigues()
	{
		return new Option("Afficher les ligues", "l", () -> {System.out.println(gestionPersonnel.getLigues());});
	}

	private Option afficher(final Ligue ligue)
	{
		return new Option("Afficher la ligue", "l", 
				() -> 
				{
					System.out.println(ligue);
					System.out.println("administrée par " + ligue.getAdministrateur());
				}
		);
	}
	private Option afficherEmployes(final Ligue ligue)
	{
		return new Option("Afficher les employes", "l", () -> {System.out.println(ligue.getEmployes());});
	}

	private Option ajouterLigue()
	{
		return new Option("Ajouter une ligue", "a", () -> 
		{
			try
			{
				gestionPersonnel.addLigue(getString("nom : "));
			}
			catch(SauvegardeImpossible exception)
			{
				System.err.println("Impossible de sauvegarder cette ligue");
			}
		});
	}
	
	private Menu editerLigue(Ligue ligue)
	{
		Menu menu = new Menu("Editer " + ligue.getNom());
		menu.add(afficher(ligue));
		menu.add(gererEmployes(ligue));
		menu.add(changerAdministrateur(ligue)); // Ajout de l'option pour changer l'administrateur
		menu.add(changerNom(ligue));
		menu.add(supprimer(ligue));
		menu.addBack("q");
		return menu;
	}

	private Option changerNom(final Ligue ligue)
	{
		return new Option("Renommer", "r", 
				() -> {ligue.setNom(getString("Nouveau nom : "));});
	}

	private List<Ligue> selectionnerLigue()
	{
		return new List<Ligue>("Sélectionner une ligue", "e", 
				() -> new ArrayList<>(gestionPersonnel.getLigues()),
				(element) -> editerLigue(element)
				);
	}
	
	private Option ajouterEmploye(final Ligue ligue)
	{
		return new Option("ajouter un employé", "a",
				() -> 
				{
					String nom = getString("nom : "); 
					String prenom = getString("prenom : ");
					String mail = getString("mail : "); 
					String password = getString("password : ");

					// demande de la date d'arrivée
					LocalDate dateArrivee = null;
					while (dateArrivee == null){
						try {
							String dateArriveeStr = getString("date d'arrivée au format aaaa-mm-jj: ");
							dateArrivee = LocalDate.parse(dateArriveeStr);
						} catch (DateTimeParseException e){
							System.out.println("Format invalide");
						}
					}

					// demande de la date de départ
					LocalDate dateDepart = null;
					while (dateDepart == null){
						try {
							String dateDepartStr = getString("date de départ au format aaaa-mm-jj: ");
							dateDepart = LocalDate.parse(dateDepartStr);
						} catch (DateTimeParseException e) {
							System.out.println("Format invalide");
						}
					}

					// vérification de la cohérence des dates
					try {
						if (dateDepart.isBefore(dateArrivee)){
							throw new DateIncoherente ("la date de départ doit être après la date d'arrivée");
						}

						ligue.addEmploye(nom, prenom, mail, password, dateArrivee, dateDepart);
					} catch (DateIncoherente e){
						System.out.println("Attention! Erreur: " + e.getMessage());
					}
				}
		);
	}
	
	private Menu gererEmployes(Ligue ligue)
	{
		Menu menu = new Menu("Gérer les employés de " + ligue.getNom(), "e");
		menu.add(afficherEmployes(ligue));
		menu.add(ajouterEmploye(ligue));
		menu.add(selectionnerEmploye(ligue)); // Nouveau menu pour sélectionner un employé
		menu.addBack("q");
		return menu;
	}

	private List<Employe> selectionnerEmploye(final Ligue ligue)
	{
		return new List<>("Sélectionner un employé", "s", 
				() -> new ArrayList<>(ligue.getEmployes()),
				this::menuActionsEmploye // Appelle un menu pour les actions sur l'employé
		);
	}

	private Menu menuActionsEmploye(Employe employe)
	{
		Menu menu = new Menu("Actions pour " + employe.getNom());
		menu.add(modifierEmploye(employe)); // Déplacement de l'option "Modifier un employé"
		menu.add(supprimerEmploye(employe)); // Déplacement de l'option "Supprimer un employé"
		menu.addBack("q");
		return menu;
	}

	private Option modifierEmploye(final Employe employe)
	{
		return new Option("Modifier l'employé", "e", 
				() -> employeConsole.editerEmploye(employe));
	}

	private Option supprimerEmploye(final Employe employe)
	{
		return new Option("Supprimer l'employé", "d", () -> employe.remove());
	}
	
	private List<Employe> changerAdministrateur(final Ligue ligue)
	{
		return new List<>("Changer l'administrateur", "a",
			() -> new ArrayList<>(ligue.getEmployes()),
			(index, employe) -> {
				ligue.setAdministrateur(employe);
				System.out.println("Nouvel administrateur : " + employe.getNom());
			}
		);
	}		

	private Option supprimer(Ligue ligue)
	{
		return new Option("Supprimer", "d", () -> {ligue.remove();});
	}
	
}
