package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * représente le tableau de toutes les arêtes du graphe JaVelo
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 *
 *
 * @param edgesBuffer
 *          la mémoire tampon contenant la valeur de certains attributs pour la totalité des arêtes du graphe
 * @param profileIds
 *           la mémoire tampon contenant la valeur des attributs restants pour la totalité des arêtes du graphe
 *  @param elevations
 *          la mémoire tampon contenant la totalité des échantillons des profils, compressés ou non
 */
public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {

    private static final int Offset_EdgeId = 0;
    private static final short Offset_length = Offset_EdgeId + 4;
    private static final short Offset_elevationGain = Offset_length + 2;
    private static final short Offset_AttributeId = Offset_elevationGain +2 ;
    private static final int Edges_INTS = Offset_AttributeId + 2;

    /**
     * Permet de déterminer si l'arête d'identité donnée est dans le sens inverse de la voie OSM
     *
     * @param edgeId
     *          Identité d'une arête
     *
     * @return true si l'arête est dans le sens inverse , false sinon
     */
    public boolean isInverted(int edgeId) {
        int inverseBit = edgesBuffer.get( edgeId*Edges_INTS );
        return Bits.extractUnsigned(inverseBit,Integer.SIZE-1,1) == 1;
    }

    /**
     * Permet de déterminer l'identité du nœud destination de l'arête d'identité donnée
     *
     * @param edgeId
     *          Identité d'une arête
     *
     * @return l'identité du nœud destination de l'arête d'identité donnée
     */
    public int targetNodeId(int edgeId) {
        int targetNodeId = edgesBuffer.getInt(edgeId*Edges_INTS);
        if ( isInverted(edgeId) )
            targetNodeId = ~targetNodeId ;
        return Bits.extractUnsigned( targetNodeId,0,Integer.SIZE-1 ) ;
    }

    /**
     * Permet de déterminer la longueur, en mètres, de l'arête d'identité donnée
     *
     * @param edgeId
     *          Identité d'une arête
     *
     * @return la longueur, en mètres, de l'arête d'identité donnée
     */
    public double length(int edgeId) {
        int edgeIndex = edgeId*Edges_INTS + Offset_length ;
        int edgeLength = Short.toUnsignedInt( edgesBuffer.getShort(edgeIndex) );
        return Q28_4.asDouble(edgeLength);
    }

    /**
     * Permet de déterminer le dénivelé positif, en mètres, de l'arête d'identité donnée
     *
     * @param edgeId
     *          Identité d'une arête
     *
     * @return le dénivelé positif, en mètres, de l'arête d'identité donnée
     */
    public double elevationGain(int edgeId) {
        int edgeIndex = edgeId*Edges_INTS + Offset_elevationGain ;
        int edgeElevationGain = Short.toUnsignedInt( edgesBuffer.getShort(edgeIndex) );
        return Q28_4.asDouble(edgeElevationGain);
    }

    /**
     * Permet de déterminer la détention ou non d'un profil
     *
     * @param edgeId
     *          Identité d'une arête
     *
     * @return vrai si l'arête d'identité donnée possède un profil
     */
    public boolean hasProfile(int edgeId) {
        int edgeProfileId = profileIds.get(edgeId);
        return Bits.extractUnsigned( edgeProfileId,Integer.SIZE-2,2 ) != 0;
    }

    /**
     * Permet de déterminer l'identité de l'ensemble d'attributs attaché à l'arête d'identité donnée
     *
     * @param edgeId
     *          Identité d'une arête
     * @return l'identité de l'ensemble d'attributs attaché à l'arête d'identité donnée
     */
    public int attributesIndex(int edgeId) {
        int edgeIndex = edgeId*Edges_INTS + Offset_AttributeId ;
        return Short.toUnsignedInt( edgesBuffer.getShort(edgeIndex) );
    }

    /**
     * Permet de déterminer les échantillons du profil de l'arête d'identité donnée
     *
     * @param edgeId
     *          Identité d'une arête
     *
     * @return le tableau des échantillons du profil de l'arête d'identité donnée,
     * qui est vide si l'arête ne possède pas de profil
     */
    public float[] profileSamples (int edgeId) {

        if ( !hasProfile(edgeId) ) {
            return new float[0];
        }

        int edgeProfileId = profileIds.get(edgeId);

        int type = Bits.extractUnsigned(edgeProfileId, Integer.SIZE-2, 2);
        int firstSample = Bits.extractUnsigned(edgeProfileId, 0, Integer.SIZE-2);

        int edgeLengthIndex = edgeId * Edges_INTS + Offset_length ;
        int edgeLength = Short.toUnsignedInt( edgesBuffer.getShort(edgeLengthIndex) );
        int samplesLength = Math2.ceilDiv( edgeLength, Q28_4.ofInt(2) ) + 1;
        float[] samples = new float[samplesLength];

        if (type == 1) {
            int incrementation = 1, indexSample = 0, indexElevation = 0;
            if (isInverted(edgeId)) {
                indexSample = samplesLength-1;
                incrementation = -1;
            }
            while ( indexSample >= 0 && indexSample < samplesLength ) {
                short alt = elevations.get( firstSample + indexElevation );
                samples[indexSample] = Q28_4.asFloat( Short.toUnsignedInt(alt) );
                indexSample += incrementation;
                ++ indexElevation;
            }
        }
        else {
            float sample;

            if (isInverted(edgeId)) {
                samples[samplesLength-1] = Q28_4.asFloat( Short.toUnsignedInt(elevations.get(firstSample)) );
                sample = samples[samplesLength-1];
            }
            else {
                samples[0] = Q28_4.asFloat( Short.toUnsignedInt(elevations.get(firstSample)) );
                sample = samples[0];
            }

            int profileLength , profilePerShort;

            if (type == 2) {
                profilePerShort = 2;
                profileLength = 8;
            }
            else {
                profilePerShort = profileLength = 4;
            }

            int sampleIndex = 1;
            int shortNumber = Math2.ceilDiv( samplesLength, profilePerShort );
            for ( int shortIndex = 1 ; shortIndex <= shortNumber ; ++shortIndex ) {

                for ( int indexProfile = profilePerShort-1 ; indexProfile>=0 ; --indexProfile ) {
                    if (! (sampleIndex == samplesLength) ) {
                        int altDifference = elevations.get( firstSample + shortIndex );
                        int profileStart = indexProfile * profileLength;
                        sample += Q28_4.asFloat( Bits.extractSigned( altDifference, profileStart, profileLength ) );
                        if (isInverted(edgeId))
                            samples[samplesLength-1 - sampleIndex] = sample;
                        else
                            samples[sampleIndex] = sample;
                        ++sampleIndex;
                    }
                }
            }
        }
        return samples;
    }
}
