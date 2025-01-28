package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.Arrays;

/**
 * Permet de représenter le profil en long d'un itinéraire simple ou multiple
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 */
public final class ElevationProfileComputer {

    private ElevationProfileComputer() {}
    /**
     *
     * @param route
     *          Itinéraire
     * @param maxStepLength
     *          Espacement maximum entre les echantillons
     * @return le profil en long de l'itinéraire route
     *
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength) {
        Preconditions.checkArgument(maxStepLength > 0);
        int sizeElevationSamples = (int) (Math.ceil(route.length() / maxStepLength) +1);
        float[] elevationSamples = new float[sizeElevationSamples];

        double distanceBetweenSamples = route.length() / ((double)sizeElevationSamples-1);

        for (int index = 0; index < elevationSamples.length; ++index)
            elevationSamples[index] = ((float) route.elevationAt(distanceBetweenSamples * index));

        int indexStart = 0;
        while (indexStart < elevationSamples.length-1 && Float.isNaN(elevationSamples[indexStart++])) ;
        Arrays.fill( elevationSamples,  0,  indexStart-1 ,  elevationSamples[indexStart-1] );
        // indexStart - 1 , car on utilise indexStart++ dans la boucle while donc lorsque la boucle s'arrête
        // l'index est incrémentée de 1 unité de trop

        int indexEnd = elevationSamples.length -1 ;
        while (indexEnd > 0 && Float.isNaN(elevationSamples[indexEnd--])) ;
        Arrays.fill( elevationSamples, indexEnd+1, sizeElevationSamples, elevationSamples[indexEnd+1] );
        // indexEnd + 1 , car on utilise indexEnd-- dans la boucle while donc lorsque la boucle s'arrête
        // l'index est décrémentée de 1 unité de trop

        int indexSample = 0, NaNcount = 0;
        for (float sample : elevationSamples){

            if (Float.isNaN(sample)) {
               if(++NaNcount==elevationSamples.length)
                   return new ElevationProfile(route.length(), new float[elevationSamples.length]);

            }
            if (!Float.isNaN(sample)) {
                if(NaNcount != 0) {
                    double previousAlt = elevationSamples[indexSample-NaNcount-1];
                    double followingAlt = elevationSamples[indexSample];
                    int firstNaNindex = indexSample-NaNcount;

                    for (int indexNaN = 0;indexNaN<NaNcount;++indexNaN) {
                        double x = (1.0/(NaNcount+1) )*( indexNaN+1 );
                        int samples = firstNaNindex+indexNaN;
                        elevationSamples[samples] = (float)Math2.interpolate(previousAlt,followingAlt,x);
                    }
                    NaNcount = 0;
                }
            }
            ++indexSample;
        }
        return new ElevationProfile( route.length(), elevationSamples );
    }

}