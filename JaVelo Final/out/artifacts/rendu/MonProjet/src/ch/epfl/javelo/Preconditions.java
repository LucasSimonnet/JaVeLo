package ch.epfl.javelo;

import ch.epfl.javelo.gui.TileManager;
import javafx.scene.image.Image;

import java.util.LinkedHashMap;

/**
 * Contient seulement la méthode checkArgument
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 */

public final class Preconditions {

    private Preconditions (){}

    /**
     *  Checke si l'expression prise en paramètre est vraie
     *
     * @param shouldBeTrue
     *                expression qui doit être vrai pour ne pas lancer d'exception
     *
     * @throws IllegalArgumentException
     *                si le paramètre est false
     */
    public static void checkArgument (boolean shouldBeTrue) {
        if (! shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
