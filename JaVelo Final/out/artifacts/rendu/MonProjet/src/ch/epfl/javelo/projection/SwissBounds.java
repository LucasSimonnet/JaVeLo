package ch.epfl.javelo.projection;

/**
 * Contient des constantes et méthodes liées aux limites de la Suisse.
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 */

public final class SwissBounds {
    /**
     *  Plus petite coordonnée E de Suisse en mètres
     */
    public final static double MIN_E = 2_485_000;

    /**
     * la plus grande coordonnée E de Suisse en mètres
     */
    public final static double MAX_E = 2_834_000;

    /**
     * la plus petite coordonnée N de Suisse en mètres
     */
    public final static double MIN_N = 1_075_000;

    /**
     * la plus grande coordonnée N de Suisse en mètres
     */
    public final static double MAX_N = 1_296_000;

    /**
     * la largeur de la Suisse en mètres
     */
    public final static double WIDTH = MAX_E - MIN_E;

    /**
     * la hauteur de la Suisse en mètres
     */
    public final static double HEIGHT = MAX_N - MIN_N;

    private SwissBounds() {}

    /**
     * Permet de tester qu'un point se trouve dans les delimitations Suisses
     *
     * @param e
     *          Coordonnée E (Est) du point
     * @param n
     *          Coordonnée N (Nord) du point
     * @return true si les coordonnées en paramètre sont dans les délimitations Suisses, false sinon
     */
    public static boolean containsEN (double e, double n){
        return (e <= MAX_E) && (e >= MIN_E) && (n <= MAX_N) && (n >= MIN_N);
    }
}
