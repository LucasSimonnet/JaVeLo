package ch.epfl.javelo;

/**
 * Permet de convertir des nombres entre la représentation Q28.4 et d'autres représentations
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 */

public final class Q28_4 {

    private static final int DECIMAL_PART_LENGTH = 4;

    private Q28_4 () {}

    /**
     * Convertit un nombre entier à sa représentation en Q28.4
     *
     * @param i
     *          Entier quelconque
     *
     * @return la valeur Q28.4 correspondante
     */

    public static int ofInt (int i) {
        return i << DECIMAL_PART_LENGTH ;
    }

    /**
     * Convertit un nombre Q28.4 en nombre décimale (en double)
     *
     * @param q28_4
     *          Valeur d'un nombre en Q28.4 quelconque
     *
     * @return la valeur décimale correspondante (en double)
     */

    public static double asDouble (int q28_4) {
        return Math.scalb( (double)q28_4 , -DECIMAL_PART_LENGTH ) ;
    }

    /**
     * Convertit un nombre Q28.4 en nombre décimale (en float)
     *
     * @param q28_4
     *          Valeur d'un nombre en Q28.4 quelconque
     *
     * @return la valeur décimale correspondante (en float)
     */

    public static float asFloat (int q28_4) {
        return Math.scalb( q28_4 , -DECIMAL_PART_LENGTH ) ;
    }
}
