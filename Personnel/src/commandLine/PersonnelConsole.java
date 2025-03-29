package commandLine;

import personnel.*;
import commandLineMenus.*;
import static commandLineMenus.rendering.examples.util.InOut.*;

public class PersonnelConsole
{
	private GestionPersonnel gestionPersonnel;
	LigueConsole ligueConsole;
	EmployeConsole employeConsole;
	
	public PersonnelConsole(GestionPersonnel gestionPersonnel)
	{
		this.gestionPersonnel = gestionPersonnel;
		this.employeConsole = new EmployeConsole();
		this.ligueConsole = new LigueConsole(gestionPersonnel, employeConsole);
	}
	
	public void start()
	{
		Menu menu = menuPrincipal();
		menu.add(menuEmployes());
		menu.start();
	}
	
	private Menu menuPrincipal()
	{
		Menu menu = new Menu("Gestion du personnel des ligues");
		menu.add(employeConsole.editerEmploye(gestionPersonnel.getRoot()));
		menu.add(ligueConsole.menuLigues());
		menu.add(menuQuitter());
		return menu;
	}

	private Menu menuEmployes()
	{
		Menu menu = new Menu("Gérer les employés");
		menu.add(selectionnerEmploye());
		menu.addBack("q");
		return menu;
	}

	private List<Employe> selectionnerEmploye()
	{
		return new List<>("Sélectionner un employé", "s",
			() -> new ArrayList<>(gestionPersonnel.getRoot().getLigue().getEmployes()),
			this::menuActionsEmploye
		);
	}

	private Menu menuActionsEmploye(Employe employe)
	{
		Menu menu = new Menu("Actions pour " + employe.getNom());
		menu.add(employeConsole.editerEmploye(employe));
		menu.add(supprimerEmploye(employe));
		menu.addBack("q");
		return menu;
	}

	private Option supprimerEmploye(Employe employe)
	{
		return new Option("Supprimer l'employé", "d", () -> employe.remove());
	}

	private Menu menuQuitter()
	{
		Menu menu = new Menu("Quitter", "q");
		menu.add(quitterEtEnregistrer());
		menu.add(quitterSansEnregistrer());
		menu.addBack("r");
		return menu;
	}
	
	private Option quitterEtEnregistrer()
	{
		return new Option("Quitter et enregistrer", "q", 
				() -> 
				{
					try
					{
						gestionPersonnel.sauvegarder();
						Action.QUIT.optionSelected();
					} 
					catch (SauvegardeImpossible e)
					{
						System.out.println("Impossible d'effectuer la sauvegarde");
					}
				}
			);
	}
	
	private Option quitterSansEnregistrer()
	{
		return new Option("Quitter sans enregistrer", "a", Action.QUIT);
	}
	
	private boolean verifiePassword()
	{
		boolean ok = gestionPersonnel.getRoot().checkPassword(getString("password : "));
		if (!ok)
			System.out.println("Password incorrect.");
		return ok;
	}
	
	public static void main(String[] args)
	{
		PersonnelConsole personnelConsole = 
				new PersonnelConsole(GestionPersonnel.getGestionPersonnel());
		if (personnelConsole.verifiePassword())
			personnelConsole.start();
	}
}
