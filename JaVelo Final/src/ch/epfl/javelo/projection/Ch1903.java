package ch.epfl.javelo.projection;

/**
 * Contient des matières permettant de convertir des coordonnées suisses en coordonnées WGS 84
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 */

public final class Ch1903 {

    private Ch1903() {}

    /**
     * Convertit des coordonnées WGS 84 en coordonnées suisses
     *
     * @param lon
     *       Longitude du point en coordonnées WGS 84
     * @param lat
     *       Latitude du point en coordonnées WGS 84
     * @return la coordonnée E (est) du point en mètres
     */
    public static double e (double lon, double lat) {
        double l1 = (1e-4) * (3_600 * Math.toDegrees(lon) - 26_782.5);
        double p1 = (1e-4) * (3_600 * Math.toDegrees(lat) - 169_028.66);
        return 2_600_072.37
                + 211_455.93*l1
                - 10_938.51*l1*p1
                - 0.36*l1*p1*p1
                - 44.54*l1*l1*l1;
    }

    /**
     * Convertit des coordonnées WGS 84 en coordonnées suisses
     *
     * @param lon
     *      Longitude du point en coordonnées WGS 84
     * @param lat
     *      Latitude du point en coordonnées WGS 84
     * @return la coordonnée N (nord) du point en mètres
     */
    public static double n (double lon, double lat) {
        double l1 = (1e-4) * (3_600 * Math.toDegrees(lon) - 26_782.5);
        double p1 = (1e-4) * (3_600 * Math.toDegrees(lat) - 169_028.66);
        return 1_200_147.07
                + 308_807.95*p1
                + 3_745.25*l1*l1
                + 76.63*p1*p1
                - 194.56*l1*l1*p1
                + 119.79*p1*p1*p1;
    }

    /**
     * Convertit des coordonnées suisses en coordonnées WGS 84
     *
     * @param e
     *      Coordonnée E (Est) du point
     * @param n
     *      Coordonnée N (Nord) du point
     * @return la longitude dans le système WGS84 du point, donnée en radians
     */
    public static double lon (double e, double n) {
        double x = (1e-6) * (e - 2_600_000);
        double y = (1e-6) * (n - 1_200_000);
        double l0 = 2.6779094
                + 4.728982*x
                + 0.791484*x*y
                + 0.1306*x*y*y
                - 0.0436*x*x*x;
        return Math.toRadians(l0 * 100.0 / 36.0);
    }

    /**
     * Convertit des coordonnées suisses en coordonnées WGS 84
     *
     * @param e
     *      Coordonnée E (Est) du point
     * @param n
     *      Coordonnée N (Nord) du point
     * @return la latitude dans le système WGS84 du point donnée en radians
     */
    public static double lat (double e, double n){
        double x = (1e-6) * (e - 2_600_000);
        double y = (1e-6) * (n - 1_200_000);
        double p0 = 16.9023892
                + 3.238272*y
                - 0.270978*x*x
                - 0.002528*y*y
                - 0.0447*x*x*y
                - 0.0140*y*y*y;
        return Math.toRadians(p0 * 100.0 / 36.0);
    }
}
