package ca.ets.tch055_H26.laboratoire4;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Classe principale du laboratoire 4
 * Cas : Gestion des services médicaux
 *
 * Cette classe contient les méthodes à compléter pour le laboratoire.
 * Chaque question de l'énoncé correspond à une méthode.
 *
 * IMPORTANT :
 * - Compléter uniquement les méthodes demandées.
 * - Ne pas modifier la logique de la méthode main().
 * - Respecter le nom du package demandé dans l'énoncé.
 *
 ** @author Ben Abdhellah Amal
 ** @author Pamella Kissok
 * 
 * @equipe : D
 * @author: Lisa Toursal
 * @author: Youssef Tahenti
 * @author:Sophia Sebastianelli
 * @author:Victoria Semichasnov 
 */
public class Laboratoire4Menu {

    public static Statement statmnt = null;

    /* Référence vers l'objet de connexion à la BD */
    public static Connection connexion = null;

    /* Chargement du pilote Oracle */
    static {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Question A : Ouverture de la connexion
     *
     * @param login nom d'utilisateur Oracle
     * @param password mot de passe Oracle
     * @param uri URI JDBC Oracle
     * @return un objet Connection si la connexion réussit, sinon null
     */
    public static Connection connexionBDD(String login, String password, String uri) {

        Connection une_connexion = null;

        try {
        	//Création de la connexion 
        	une_connexion = DriverManager.getConnection(uri, login, password);
        	} catch (SQLException e) {
        		System.out.println("Erreur de connexion à la base de données : " + e.getMessage());
        	}

        return une_connexion;
    }

    /**
     * Option 1 du menu
     * Affiche la liste des services médicaux par ordre alphabétique du code du service.
     *
     * Les données doivent être récupérées à partir de la table Service_Medical.
     */
    public static void listerServicesMedicaux() {
    	
    	// Transformer la requete SQL sous forme de chaine de caracteres
    	String requete = "SELECT code_service, nom_service, tarif_unitaire, statut_service " +
    				   " FROM Service_Medical "+
    				   " ORDER BY code_service ASC";
    	
    	try {
    		//Creation d'un statement pour envoyer et executer la requete SQL
    		Statement stmt = connexion.createStatement();
    		java.sql.ResultSet rs = stmt.executeQuery(requete);
    		
    		//Formatage pour rendre le tableau plus propre
    		System.out.println(String.format("%-10s %-30s %-10s %-20s", "Code", "Nom du service", "Tarif", "Statut"));
            System.out.println("--------------------------------------------------------------------------------");
    		
            //Boucle qui nous permet d'afficher les donnee de la base
    		while (rs.next()) {
    			String code = rs.getString("code_service");
    			String nom = rs.getString("nom_service");
    			float tarif = rs.getFloat("tarif_unitaire");
    			String statut = rs.getString("statut_service");
    			
    		//Formatage du tableau a nouveau	
    			System.out.println(String.format("%-10s %-30s %-10.2f %-20s", code, nom, tarif, statut));
            }
    		
    		rs.close();
    		stmt.close();
    		//Gerer les exceptions si jamais
    	} catch(java.sql.SQLException e) {
    		System.out.println("Erreur lors de la recuperation des informations sur le service" + e.getMessage());
    	}
    	//Pause avant de revenir au menu
    	System.out.println("\nAppuyer sur ENTER pour continuer...");
    	new java.util.Scanner(System.in).nextLine();
    		
    }

    /**
     * Option 2 du menu
     * Ajoute un nouveau service médical dans la base de données.
     *
     * La méthode doit lire les données à partir de la console puis effectuer l'insertion.
     */
    public static void ajouterServiceMedical() {
    		
    	    Scanner scanner = new Scanner(System.in);

    	    try {
    	        System.out.print("Veuillez saisir le code du service : ");
    	        String codeService = scanner.nextLine();

    	        System.out.print("Veuillez saisir le nom du service : ");
    	        String nomService = scanner.nextLine();

    	        System.out.print("Veuillez saisir la description : ");
    	        String description = scanner.nextLine();

    	        System.out.print("Veuillez saisir le tarif unitaire : ");
    	        float tarifUnitaire = Float.parseFloat(scanner.nextLine());

    	        System.out.print("Veuillez saisir le statut du service (Actif / Retire / Suspendu / Temporairement_indisponible) : ");
    	        String statutService = scanner.nextLine();

    	        System.out.print("Veuillez saisir l'identifiant de la catégorie : ");
    	        int idCategorie = Integer.parseInt(scanner.nextLine());

    	        String sql = "INSERT INTO Service_Medical "
    	                   + "(code_service, nom_service, description, tarif_unitaire, statut_service, id_categorie) "
    	                   + "VALUES (?, ?, ?, ?, ?, ?)";

    	        PreparedStatement ps = connexion.prepareStatement(sql);
    	        ps.setString(1, codeService);
    	        ps.setString(2, nomService);
    	        ps.setString(3, description);
    	        ps.setFloat(4, tarifUnitaire);
    	        ps.setString(5, statutService);
    	        ps.setInt(6, idCategorie);

    	        int resultat = ps.executeUpdate();

    	        if (resultat > 0) {
    	            System.out.println("Service médical ajouté avec succès");
    	        } else {
    	            System.out.println("Échec de l'ajout du service médical");
    	        }

    	        ps.close();

    	    } catch (Exception e) {
    	        System.out.println("Erreur lors de l'ajout du service médical : " + e.getMessage());
    	    }

    	    System.out.println("Appuyer sur ENTER pour continuer...");
    	    scanner.nextLine();
    	}
    

    /**
     * Option 3 du menu
     * Affiche une consultation médicale et les services associés.
     *
     * @param numConsultation numéro de la consultation à afficher
     */
    public static void afficherConsultation(int numConsultation) {
    	
    		try {

    		// 1. Vérifier si la consultation existe + infos patient
    		String queryInfo = """
    		SELECT p.nom, p.prenom, p.telephone,
    		c.id_consultation, c.date_heure_consult
    		FROM Consultation c
    		JOIN Patient p ON c.no_patient = p.no_patient
    		WHERE c.id_consultation = ?
    		""";

    		java.sql.PreparedStatement ps1 = connexion.prepareStatement(queryInfo);
    		ps1.setInt(1, numConsultation);
    		java.sql.ResultSet rs1 = ps1.executeQuery();

    		if (!rs1.next()) {
    		System.out.println("Consultation inexistante.");
    		System.out.println("Appuyer sur ENTER pour continuer...");
    	    new java.util.Scanner(System.in).nextLine();
    		return;
    		}

    		System.out.println("Patient : " + rs1.getString("nom") + " " + rs1.getString("prenom"));
    		System.out.println("Téléphone : " + rs1.getString("telephone"));
    		System.out.println("No Consultation : " + rs1.getInt("id_consultation"));
    		System.out.println("Date : " + rs1.getTimestamp("date_heure_consult"));
    		System.out.println("--------------------------------------------------");

    		// 2. Liste des services (IMPORTANT: si la table n'a PAS de quantité → on met 1)
    		String queryServices = """
    		SELECT sm.code_service, sm.nom_service, sm.tarif_unitaire
    		FROM Service_Medical sm
    		JOIN Service_Medical_Consultation smc
    		ON sm.code_service = smc.code_service
    		WHERE smc.id_consultation = ?
    		""";

    		java.sql.PreparedStatement ps2 = connexion.prepareStatement(queryServices);
    		ps2.setInt(1, numConsultation);
    		java.sql.ResultSet rs2 = ps2.executeQuery();

    		double total = 0;

    		System.out.printf("%-10s %-25s %-10s %-10s %-10s\n",
    		"Code", "Nom Service", "Tarif", "Qté", "Total");

    		while (rs2.next()) {
    		String code = rs2.getString("code_service");
    		String nom = rs2.getString("nom_service");
    		double tarif = rs2.getDouble("tarif_unitaire");

    		int qte = 1; 
    		double totalPartiel = tarif * qte;
    		total += totalPartiel;

    		System.out.printf("%-10s %-25s %-10.2f %-10d %-10.2f\n",
    		code, nom, tarif, qte, totalPartiel);
    		}

    		System.out.println("--------------------------------------------------");
    		System.out.println("Total consultation : " + total + " $");

    		System.out.println("Appuyer sur ENTER pour continuer...");
    		new java.util.Scanner(System.in).nextLine();

    		rs1.close();
    		ps1.close();
    		rs2.close();
    		ps2.close();
    		
    		} catch (Exception e) {
    		e.printStackTrace();
    		}
    }

    /**
     * Option 4 du menu
     * Calcule le total des paiements effectués pour une facture.
     *
     * @param numFacture numéro de la facture
     * @param affichage true pour afficher les messages, false sinon
     * @return le montant total des paiements effectués, ou -1 si la facture n'existe pas
     */
    public static float calculerPaiements(int numFacture, boolean affichage) {
        float resultat = -1;

        try {
            // Vérifier si la facture existe
            String sqlFacture = "SELECT no_facture FROM Facture WHERE no_facture = ?";
            PreparedStatement psFacture = connexion.prepareStatement(sqlFacture);
            psFacture.setInt(1, numFacture);

            ResultSet rsFacture = psFacture.executeQuery();

            if (!rsFacture.next()) {
                if (affichage) {
                    System.out.println("La facture n'existe pas.");
                    System.out.println("Appuyer sur ENTER pour continuer...");
                    new Scanner(System.in).nextLine();
                }

                rsFacture.close();
                psFacture.close();
                return -1;
            }

            rsFacture.close();
            psFacture.close();

            // Calculer la somme des paiements effectués pour cette facture
            String sqlPaiements = "SELECT NVL(SUM(montant), 0) AS total_paye "
                                + "FROM Paiement "
                                + "WHERE no_facture = ?";

            PreparedStatement psPaiements = connexion.prepareStatement(sqlPaiements);
            psPaiements.setInt(1, numFacture);

            ResultSet rsPaiements = psPaiements.executeQuery();

            if (rsPaiements.next()) {
                resultat = rsPaiements.getFloat("total_paye");
            } else {
                resultat = 0;
            }

            rsPaiements.close();
            psPaiements.close();

            // Affichage seulement si demandé
            if (affichage) {
                System.out.println("Montant total des paiements pour la facture " 
                        + numFacture + " : " + String.format("%.2f", resultat) + " $");
                System.out.println("Appuyer sur ENTER pour continuer...");
                new Scanner(System.in).nextLine();
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors du calcul des paiements : " + e.getMessage());
        }

        return resultat;

    }

    /**
     * Option 5 du menu
     * Enregistre un nouveau paiement pour une facture.
     *
     * Le paiement peut être effectué :
     * - en espèces
     * - par chèque
     * - par carte de crédit
     *
     * Les informations générales vont dans la table Paiement,
     * puis les informations spécifiques pour :
     * - Paiement par Espece
     * - Paiement par Cheque
     * - Paiement par Carte Credit
     *
     * @param numFacture numéro de la facture
     */
    public static void enregistrerPaiement(int numFacture) {

        Scanner scanner = new Scanner(System.in);

        try {
            // Vérifier si la facture existe
            String sqlFacture = "SELECT total_a_payer FROM Facture WHERE no_facture = ?";
            PreparedStatement psFacture = connexion.prepareStatement(sqlFacture);
            psFacture.setInt(1, numFacture);

            ResultSet rs = psFacture.executeQuery();

            if (!rs.next()) {
                System.out.println("La facture n'existe pas.");
                System.out.println("Appuyer sur ENTER pour continuer...");
                scanner.nextLine();
                rs.close();
                psFacture.close();
                return;
            }

            double totalFacture = rs.getDouble("total_a_payer");
            rs.close();
            psFacture.close();

            // Calculer le montant déjà payé
            double dejaPaye = calculerPaiements(numFacture, false);

            // Saisie du montant 
            System.out.print("Veuillez saisir le montant du paiement : ");
            double montant = Double.parseDouble(scanner.nextLine());

            // Vérification dépassement 
            if (dejaPaye + montant > totalFacture) {
                System.out.println("Erreur : le paiement dépasse le montant total de la facture.");
                System.out.println("Appuyer sur ENTER pour continuer...");
                scanner.nextLine();
                return;
            }

            System.out.print("Type de paiement (ESPECE / CHEQUE / CREDIT) : ");
            String typePaiement = scanner.nextLine().trim().toUpperCase();
            
            // Correction si utilisateur écrit carte 
            if (typePaiement.equals("CARTE")) {
                typePaiement = "CREDIT";
            }
            
            Date datePaiement = new Date(System.currentTimeMillis());

            // mettre des valeurs par défaut (null)
            Integer noCheque = null;
            String institutionBancaire = null;
            String noCarte = null;
            Date dateExpiration = null;
            String typeCarte = null;

            // Selon le type de paiement
            if (typePaiement.equals("CHEQUE")) {
            	
                System.out.print("Numéro du chèque : ");
                noCheque = Integer.parseInt(scanner.nextLine());

                System.out.print("Institution bancaire : ");
                institutionBancaire = scanner.nextLine();

            } else if (typePaiement.equals("CREDIT")) {
                System.out.print("Numéro de carte : ");
                noCarte = scanner.nextLine();

                System.out.print("Date expiration (AAAA-MM-JJ) : ");
                dateExpiration = Date.valueOf(scanner.nextLine());
                
                System.out.print("Type de carte (VISA / MASTERCARD / AMEX) : ");
                typeCarte = scanner.nextLine().trim().toUpperCase();

            } else if (!typePaiement.equals("ESPECE")) {
                System.out.println("Type de paiement invalide.");
                System.out.println("Appuyer sur ENTER pour continuer...");
                scanner.nextLine();
                return;
            }

            // Insertion du paiement
            String sql = "INSERT INTO Paiement "
                    + "(date_paiement, montant, type_paiement, no_cheque, insitution_bancaire, "
                    + "no_carte_credit, date_expiration, type_carte_credit, no_facture) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = connexion.prepareStatement(sql);

            ps.setDate(1, datePaiement);
            ps.setDouble(2, montant);
            ps.setString(3, typePaiement);

            // gestion NULL propre
            if (noCheque != null) ps.setInt(4, noCheque);
            else ps.setNull(4, java.sql.Types.INTEGER);

            if (institutionBancaire != null) ps.setString(5, institutionBancaire);
            else ps.setNull(5, java.sql.Types.VARCHAR);

            if (noCarte != null) ps.setString(6, noCarte);
            else ps.setNull(6, java.sql.Types.VARCHAR);

            if (dateExpiration != null) ps.setDate(7, dateExpiration);
            else ps.setNull(7, java.sql.Types.DATE);
            
            if (typeCarte != null) ps.setString(8, typeCarte);
            else ps.setNull(8, java.sql.Types.VARCHAR);

            ps.setInt(9, numFacture);

            int res = ps.executeUpdate();

            if (res > 0) {
                System.out.println("Paiement enregistré avec succès.");
            } else {
                System.out.println("Échec de l'enregistrement.");
            }

            ps.close();

        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }

        System.out.println("Appuyer sur ENTER pour continuer...");
        scanner.nextLine();
    }

    /**
     * Option 6 du menu
     * Enregistre les évaluations des services médicaux.
     *
     * Les insertions doivent être effectuées par lot.
     *
     * @param listEvaluation tableau d'objets ServiceEvaluationData
     */
    public static void enregistrerEvaluation(ServiceEvaluationData[] listEvaluation) {
        
    	// Vérifie si le tableau est vide ou nul
    	if (listEvaluation == null || listEvaluation.length == 0) {
            System.out.println("Aucune évaluation à enregistrer.");
            System.out.println("Appuyer sur ENTER pour continuer...");
            new Scanner(System.in).nextLine();
            return;
        }

    	// Requête SQL pour insérer une évaluation (sans id car généré automatiquement par trigger)
        String sql = "INSERT INTO Evaluation_Service (no_patient, code_service, note, commentaire) VALUES (?, ?, ?, ?)";

        try {
        	// Désactiver l'auto-commit pour gérer la transaction manuellement
            connexion.setAutoCommit(false);

            // Préparation de la requête SQL
            java.sql.PreparedStatement pstmt = connexion.prepareStatement(sql);

           // Parcours du tableau d'évaluations
            for (ServiceEvaluationData eval : listEvaluation) {

            	// Associer les valeurs de l’objet aux paramètres de la requête
                pstmt.setInt(1, eval.no_patient);
                pstmt.setString(2, eval.code_service);
                pstmt.setInt(3, eval.note);
                pstmt.setString(4, eval.commentaire);

             // Ajout de l'insertion au batch
                pstmt.addBatch();
            }
        
            // Exécution de toutes les insertions en une seule fois 
            int[] resultats = pstmt.executeBatch();
            
           // Validation de la transaction
            connexion.commit();

          // Affichage du nombre d'insertions réussies
            System.out.println(resultats.length + " évaluations enregistrées avec succès.");

          // Fermer le PreparedStatement
            pstmt.close();
            
          // Réactiver l’auto-commit après le traitement
            connexion.setAutoCommit(true);

        } catch (SQLException e) {
            try {
            	// Annulation des insertions en cas d'erreur
                connexion.rollback();
                connexion.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println("Erreur rollback : " + ex.getMessage());
            }

            System.out.println("Erreur lors de l'insertion : " + e.getMessage());
        }

     // Pause pour permettre à l’utilisateur de lire le message
        System.out.println("Appuyer sur ENTER pour continuer...");
        new Scanner(System.in).nextLine();
    }

    /**
     * Question A : Fermeture de la connexion
     *
     * @return true si la fermeture réussit, sinon false
     */
    public static boolean fermetureConnexion() {
        boolean resultat = false;

        try {
            if (connexion != null && !connexion.isClosed()) {
                connexion.close();
                resultat = true;
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la fermeture de la connexion.");
            e.printStackTrace();
        }


        return resultat;
    }

    // ==============================================================================
    // NE PAS MODIFIER LE CODE QUI VA SUIVRE
    // ==============================================================================

    /**
     * Crée et retourne un tableau qui contient 5 évaluations de services médicaux.
     * Chaque évaluation est stockée dans un objet de la classe ServiceEvaluationData.
     *
     * @return un tableau d'objets ServiceEvaluationData
     */
    public static ServiceEvaluationData[] listServiceEvaluationData() {

        ServiceEvaluationData[] list = new ServiceEvaluationData[5];

        list[0] = new ServiceEvaluationData(201, "SM1001", 5, "Excellent service médical");
        list[1] = new ServiceEvaluationData(202, "SM1002", 4, "Service satisfaisant");
        list[2] = new ServiceEvaluationData(203, "SM1003", 3, "Temps d'attente un peu long");
        list[3] = new ServiceEvaluationData(204, "SM1004", 5, "Personnel très professionnel");
        list[4] = new ServiceEvaluationData(205, "SM1005", 4, "Très bon suivi");

        return list;
    }

    /**
     * Affiche un menu pour le choix des opérations.
     */
    public static void afficheMenu() {
        System.out.println("0. Quitter le programme");
        System.out.println("1. Lister les services médicaux");
        System.out.println("2. Ajouter un service médical");
        System.out.println("3. Afficher une consultation");
        System.out.println("4. Afficher le montant payé d'une facture");
        System.out.println("5. Enregistrer un paiement");
        System.out.println("6. Enregistrer les évaluations des services médicaux");
        System.out.println();
        System.out.println("Votre choix...");
    }

    /**
     * La méthode main pour le lancement du programme.
     * Il faut mettre les informations d'accès à la BDD.
     *
     * @param args arguments du programme
     */
    public static void main(String args[]) {

        // Mettre les informations de votre compte sur SGBD Oracle
        String username = "Sophia";
        String motDePasse = "1234";

        String uri = "jdbc:oracle:thin:@localhost:1521:xe";

        // Appel de la méthode pour établir la connexion avec le SGBD
        connexion = connexionBDD(username, motDePasse, uri);

        if (connexion != null) {

            System.out.println("Connexion réussie...");

            // Affichage du menu pour le choix des opérations
            afficheMenu();

            Scanner sc = new Scanner(System.in);
            String choix = sc.nextLine();

            while (!choix.equals("0")) {

                if (choix.equals("1")) {

                    listerServicesMedicaux();

                } else if (choix.equals("2")) {

                    ajouterServiceMedical();

                } else if (choix.equals("3")) {

                    System.out.print("Veuillez saisir le numéro de la consultation : ");
                    sc = new Scanner(System.in);
                    int numConsultation = Integer.parseInt(sc.nextLine().trim());

                    afficherConsultation(numConsultation);

                } else if (choix.equals("4")) {

                    sc = new Scanner(System.in);
                    System.out.print("Veuillez saisir le numéro de la facture : ");
                    int numFacture = Integer.parseInt(sc.nextLine().trim());
                    calculerPaiements(numFacture, true);

                } else if (choix.equals("5")) {

                    System.out.print("Veuillez saisir le numéro de la facture : ");
                    int numFacture = Integer.parseInt(sc.nextLine().trim());
                    sc = new Scanner(System.in);
                    enregistrerPaiement(numFacture);

                } else if (choix.equals("6")) {

                    enregistrerEvaluation(listServiceEvaluationData());
                }

                afficheMenu();
                sc = new Scanner(System.in);
                choix = sc.nextLine();

            } // while

            // Fin de la boucle while - Fermeture de la connexion
            if (fermetureConnexion()) {
                System.out.println("Déconnexion réussie...");
            } else {
                System.out.println("Échec ou erreur lors de la déconnexion...");
            }

        } else {
            System.out.println("Échec de la connexion. Au revoir !");
        }
    } // main()
}

/**
 * Contient les données d'une évaluation d'un service médical.
 */
class ServiceEvaluationData {
    int no_patient;
    String code_service;
    int note;
    String commentaire;

    /**
     * Constructeur
     *
     * @param no_patient numéro du patient
     * @param code_service code du service médical
     * @param note note attribuée au service
     * @param commentaire commentaire du patient
     */
    public ServiceEvaluationData(int no_patient, String code_service, int note, String commentaire) {
        super();
        this.no_patient = no_patient;
        this.code_service = code_service;
        this.note = note;
        this.commentaire = commentaire;
    }
}

