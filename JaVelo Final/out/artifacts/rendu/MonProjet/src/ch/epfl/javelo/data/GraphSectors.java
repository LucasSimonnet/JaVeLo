package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente le tableau contenant les 16384 secteurs de JaVelo
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 *
 *
 * @param buffer
 *          la mémoire tampon contenant la valeur des attributs de la totalité des secteurs
 */
public record GraphSectors(ByteBuffer buffer) {

    private static final byte OFFSET_INT = 0;
    private static final byte OFFSET_SHORT = OFFSET_INT+4;
    private static final byte SECTOR_INTS = OFFSET_SHORT+2;
    private static final int SECTORS_IN_HEIGHT = 128;
    private static final int SECTORS_IN_WIDTH = 128;
    private static final double SECTORS_WIDTH = SwissBounds.WIDTH/SECTORS_IN_WIDTH ;
    private static final double SECTORS_HEIGHT = SwissBounds.HEIGHT/SECTORS_IN_HEIGHT ;

    /**
     * @param startNodeId
     *          Identité du premier noeud du secteur
     * @param endNodeId
     *          Identité du noeud après le dernier noeud du secteur
     */
    public record Sector(int startNodeId, int endNodeId) { }

    private int sectorHeightIndex(double positionN) {
        double maxIndex = SECTORS_IN_HEIGHT-1;
        return (int) Math2.clamp(0,
                Math.floor( (positionN - SwissBounds.MIN_N) / SECTORS_HEIGHT),
                maxIndex);
    }

    private int sectorWidthIndex(double positionE) {
        double maxIndex = SECTORS_IN_WIDTH-1;
        return (int) Math2.clamp(0,
                Math.floor( (positionE - SwissBounds.MIN_E) / SECTORS_WIDTH),
                maxIndex);
    }

    /**
     * Permet de déterminer l'ensemble des secteurs appartenant à une zone donnée
     *
     * @param center
     *          Centre de la zone dont les coordonnées sont dans le systèmes suisses
     * @param distance
     *          Distance entre le centre et l'extrémité de la zone
     *
     * @return la liste de tous les secteurs intersectant la zone de centre Center
     */
    public List<Sector> sectorsInArea(PointCh center, double distance) {
        List <Sector> sectorsIn = new ArrayList<>() ;

        int firstSectorHeightIndex = sectorHeightIndex(center.n() - distance);
        int firstSectorWidthIndex = sectorWidthIndex(center.e() - distance);
        int lastSectorHeightIndex = sectorHeightIndex(center.n() + distance);
        int lastSectorWidthIndex = sectorWidthIndex(center.e() + distance);

        for (int indexHeight = firstSectorHeightIndex; indexHeight<=lastSectorHeightIndex; ++indexHeight) {
            for (int indexWidth = firstSectorWidthIndex; indexWidth<=lastSectorWidthIndex; ++indexWidth) {
                int indexSector = ( indexHeight*SECTORS_IN_WIDTH + indexWidth ) * SECTOR_INTS;
                int firstNode = buffer.getInt(indexSector);
                int nodeNumber = Short.toUnsignedInt( buffer.getShort (indexSector + OFFSET_SHORT ) );
                sectorsIn.add(new Sector( firstNode, firstNode + nodeNumber )  );
            }
        }
        return sectorsIn;
    }

}
