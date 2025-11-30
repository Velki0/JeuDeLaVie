package fr.baptistegerardin.jeudelavie.exceptions;

/**
 * Exception au sujet du modèle si le fichier renseigné n'est pas conforme à la lecture.
 * Celle-ci est jetée si les informations contenues dans le fichier ne sont pas suffisantes ou contiennent des valeurs impossibles.
 * @author Velki0
 * @version 1.0
 */
public class ModeleRenseigneNonConforme extends Exception {

    /**
     * Constructeur de l'exception si l'utilisateur fourni un fichier erroné.
     * Celle-ci est jetée si les informations contenues dans le fichier ne sont pas suffisantes ou contiennent des valeurs impossibles.
     * @param message Message délivré à l'utilisateur pour un fichier non conforme.
     */
    public ModeleRenseigneNonConforme(String message) {

        super(message);

    }

}
