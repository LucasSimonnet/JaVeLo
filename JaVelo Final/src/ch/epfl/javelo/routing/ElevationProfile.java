package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.function.DoubleUnaryOperator;

/**
 * Permet de représenter le profil en long d'un itinéraire simple ou multiple
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 */
public final class ElevationProfile {
    private final float[] elevationSamples;
    private final DoubleUnaryOperator profile;
    private final double altMax, altMin, length;
    private double totalAscent, totalDescent;


    /**
     *
     * @param length
     *          Longueur d'un  itinéraire
     * @param elevationSamples
     *          Echantillons d'altitude répartis uniformément le long de l'itinéraire
     */
    public ElevationProfile(double length, float[]elevationSamples) {
        Preconditions.checkArgument(length>0 || elevationSamples.length>=2);
        this.length = length;
        this.elevationSamples = Arrays.copyOf(elevationSamples, elevationSamples.length);
        this.profile = Functions.sampled(elevationSamples, length);

        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        for (float alt : elevationSamples)
            s.accept(alt);
        altMin = s.getMin();
        altMax = s.getMax();

        for (int indexSamples = 1 ; indexSamples<elevationSamples.length ; ++indexSamples) {
            double currentElevation = elevationSamples[indexSamples];
            double previousElevation = elevationSamples[indexSamples-1];
            double difference = currentElevation - previousElevation;
            if (difference<0)
                totalDescent-=difference;
            if (difference>0)
                totalAscent += difference;
        }
    }

    /**
     * Permet de déterminer la longueur du profil, en mètres
     *
     * @return  la longueur du profil, en mètres
     */
    public double length() {
        return length;
    }

    /**
     * Permet de déterminer l'altitude minimum du profil, en mètres
     *
     * @return l'altitude minimum du profil, en mètres
     */
    public double minElevation () {
        return altMin;
    }

    /**
     * Permet de déterminer l'altitude maximum du profil, en mètres
     *
     * @return l'altitude maximum du profil, en mètres,
     */
    public double maxElevation () {
        return altMax;
    }

    /**
     * Calcule le denivelé positif en mètres du profil
     *
     * @return le dénivelé positif total du profil, en mètres
     */
    public double totalAscent() {
        return totalAscent;

    }



    /**
     * Calcule le denivelé négatif en mètres du profil
     *
     * @return le dénivelé négatif total du profil, en mètres
     */
    public double totalDescent() {
        return totalDescent;
        }



    /**
     * Permet de déterminer l'altitude du profil donnée en paramètre selon sa position
     *
     * @param position
     *          Position du profil donnée
     *
     * @return l'altitude du profil à la position donnée,  le premier échantillon est retourné lorsque
     * la position est négative, le dernier lorsqu'elle est supérieure à la longueur
     */
    public double elevationAt(double position) {
        return profile.applyAsDouble(position);
    }

}
