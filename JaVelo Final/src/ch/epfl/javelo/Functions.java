package ch.epfl.javelo;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.DoubleUnaryOperator;

/**
 * Permet de créer des objets représentant des fonctions mathématiques des réels vers les réels
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 */

public final class Functions {

    /**
     * Représente une fonction constante
     *
     * @param y
     *      Valeur constante de la fonction
     */
    private static final record Constant(double y) implements DoubleUnaryOperator {

        /**
         * Permet d'appliquer la fonction constante à n'importe quel point d'abscisse x
         *
         * @param x
         *          Abscisse d'un point quelconque de la fonction
         *
         * @return la valeur de l'ordonnée constante de la fonction
         */
        @Override
        public double applyAsDouble(double x) {
            return this.y ;
        }
    }

    /**
     * Permet de créer une fonction constante
     *
     * @param y
     *          Ordonnée constante de la fonction
     *
     * @return la fonction constante de valeur y
     */
    public static DoubleUnaryOperator constant(double y) {
        return new Constant(y) ;
    }

    /**
     * Représente une fonction obtenue par interpolation linéaire entre des échantillons d'ordonnées connues
     *
     * @param samples
     *          Contient les valeurs des ordonnées de tous les échantillons
     * @param pas
     *          Écart régulier entre chaque échantillon en partant de l'abscisse 0 jusqu'à xMax
     * @param xMax
     *          Abscisse maximale de la fonction (celle du dernier échantillon du tableau samples)
     */
    private static final record Sampled(float[] samples, double pas, double xMax) implements DoubleUnaryOperator {

        /**
         * Calcule l'ordonnée d'un point donnée sur la fonction par interpolation linéaire
         *
         * @param x
         *          Abscisse x d'un point quelconque
         *
         * @return la valeur de l'ordonnée du point d'abscisse x sur la fonction
         */
       @Override
        public double applyAsDouble(double x) {
           if (x <= 0) {
               return samples[0];
           }
           if (x >= xMax) {
               return samples[samples.length - 1];
           }
            int index = (int)Math.floor( x/pas );
            float previousY = samples[index];
            float nextY = samples[index + 1];
            x = ( x - pas*index ) / pas;
            return Math2.interpolate( previousY, nextY, x );
        }
    }

    /**
     * Permet de créer une fonction à partir des échantillons de points du tableau samples
     *
     * @param samples
     *          Contient les valeurs des ordonnées de tous les échantillons
     * @param xMax
     *          Abscisse maximale de la fonction (celle du dernier échantillon du tableau samples)
     *
     * @return la fonction contenant tous les échantillons espacés régulièrement et d'abscisse maximale xMax
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax) {
        Preconditions.checkArgument(samples.length >= 2 && xMax > 0 );
        double pas = xMax / (samples.length - 1);
        return new Sampled( Arrays.copyOf(samples,samples.length) , pas, xMax );
    }

}
