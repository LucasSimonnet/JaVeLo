package ch.epfl.javelo;

/**
 * Offre des méthodes statiques permettant d'effectuer certains calculs mathématiques
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 */

public final class Math2 {

    private Math2 () {}

    /**
     * Calcule la partie entière par excès entre deux nombres réels quelconque x et y
     * @param x
     *      Tout nombre entier quelconque
     *
     * @param y
     *      Tout nombre entier quelconque
     *
     * @return la partie entière par excès de la division de x par y,
     */
    public static int ceilDiv (int x, int y){
        Preconditions.checkArgument( x >= 0 && y > 0);
        return (x+y-1) / y;
    }

    /**
     * Permet de determiner la coordonnée d'un point y se trouvant sur une droite dont deux points sont connus
     *
     * @param y0
     *     Coordonnée Y du point d'abscisse 0
     * @param y1
     *     Coordonnée Y du point d'abscisse 1
     * @param x
     *      Nombre réel quelconque
     * @return la coordonnée y du point se trouvant sur la droite passant par (0,y0) et (1,y1) et
     * de coordonnée x donnée
     */
    public static double interpolate (double y0, double y1, double x){
        return Math.fma((y1-y0), x, y0);
    }

    /**
     * Limite la valeur v à l'intervalle allant de min à max
     *
     * @param min
     *      Valeur minimale que peut prendre v
     * @param v
     *      Tout nombre entier quelconque
     * @param max
     *      Valeur maximale que peut prendre v
     *
     * @throws IllegalArgumentException
     *                              si min est strictement supérieur à max
     *
     * @return min si v est inférieur à min, max si v est supérieur à max,
     * v si le nombre est compris entre min et max
     */
    public static int clamp (int min, int v, int max){
        Preconditions.checkArgument(min <= max);
        if ( v < min){
            return min;
        } else
            return Math.min(v, max);
    }

    /**
     * Limite la valeur v à l'intervalle allant de min à max
     *
     * @param min
     *      Valeur minimale que peut prendre v
     * @param v
     *      Tout nombre réel quelconque
     * @param max
     *      Valeur maximale que peut prendre v
     *
     * @throws IllegalArgumentException
     *                              si min est strictement supérieur à max
     *
     * @return min si v est inférieur à min, max si v est supérieur à max,
     * v si le nombre est compris entre min et max
     */
    public static double clamp (double min, double v, double max){
        Preconditions.checkArgument(min <= max);
        if ( v < min){
            return min;
        } else
            return Math.min(v, max);
    }

    /**
     * Calcule l'arcsinus hyperbolique d'un nombre
     *
     * @param x
     *      nombre réel quelconque
     *
     * @return la valeur de l'arcsinus hyperbolique du nombre x
     */
    public static double asinh(double x) {
        return Math.log(x + Math.sqrt( 1 + x*x ) );
    }

    /**
     * Calcule le produit scalaire de deux vecteurs
     *
     * @param uX
     *      Composante X du vecteur u
     * @param uY
     *      Composante Y du vecteur u
     * @param vX
     *      Composante X du vecteur v
     * @param vY
     *      Composante Y du vecteur v
     *
     * @return la valeur du produit scalaire du vecteur u avec le vecteur v
     */
    public static double dotProduct (double uX, double uY, double vX, double vY) {
        return Math.fma( uX, vX, uY*vY );
    }

    /**
     * Calcule la norme au carré d'un vecteur
     *
     * @param uX
     *      Composante X du vecteur u
     * @param uY
     *      Composante Y du vecteur u
     *
     * @return la longueur au carré du vecteur u
     */
    public static double squaredNorm (double uX, double uY) {
        return dotProduct( uX, uY, uX, uY );
    }

    /**
     * Calcule la norme d'un vecteur
     *
     * @param uX
     *        Composante X du vecteur u
     * @param uY
     *        Composante Y du vecteur u
     *
     * @return la longueur du vecteur u
     */
    public static double norm (double uX, double uY) {
        return Math.sqrt( squaredNorm( uX, uY ) ) ;
    }

    /**
     *
     * Calcule la projection orthogonale d'un vecteur AP (point A de coordonnées (aX,aY)
     * et point P de coordonnées (pX,pY)  ) sur le vecteur AB (point B de coordonnées (bX,bY) )
     *
     * @param aX
     *      Coordonnée X du point A
     * @param aY
     *      Coordonnée Y du point A
     * @param bX
     *      Coordonnée X du point B
     * @param bY
     *      Coordonnée Y du point B
     * @param pX
     *      Coordonnée X du point P
     * @param pY
     *      Coordonnée Y du point P
     *
     * @return la longueur de la projection orthogonale du vecteur AP sur le vecteur AB
     */
    public static double projectionLength (double aX, double aY, double bX, double bY, double pX, double pY) {
        double xAB = bX-aX;
        double yAB = bY-aY;
        double xAP = pX-aX;
        double yAP = pY-aY;
        return  dotProduct( xAP,yAP, xAB , yAB ) /  norm(xAB,yAB)  ;
    }
}
