package ch.epfl.javelo.gui;

import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;

/**
 * Represente un gestionnaire de tuiles OSM
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 */


public final class TileManager {
    private static final float LOAD_FACTOR = 0.75f;
    private static final int INITIAL_CAPACITY = 100, MIN_ZOOM = 0, MIN_X = 0, MIN_Y = 0;

    /**
     * Puissance à laquelle il faut élever 2 pour calculer le nombre de tuiles par coté de la carte
     * selon le niveau de zoom
     */
    private static final int TWO = 2;

    private final String tileName;
    private final Path path;

    public final LinkedHashMap <TileId,Image> cacheMemory =
            new LinkedHashMap<>(INITIAL_CAPACITY,LOAD_FACTOR,true);

    /**
     * @param path
     *          Chemin d'accès au répertoire contenant le cache disque
     *
     * @param tileName
     *          le nom du serveur de tuile
     */
    public TileManager(Path path, String tileName) {
        this.tileName = tileName;
        this.path = path;
    }

    /**
     * Permet de retourner l'image correspondant à l'identité tileId
     *
     * @param tileId
     *          Identité d'une tuile
     *
     * @return l'image de l'identité de la tuile passée en argument
     *
     * @throws IOException
     *          si une erreur entrée/sortie se produit
     */
    public Image imageForTileAt(TileId tileId) throws IOException {

        if ( cacheMemory.containsKey(tileId) )
            return cacheMemory.get(tileId);

        Path directory = Path.of(path +"/"+tileId.zoomLevel+"/"+tileId.X);

        Path newFile = directory.resolve(tileId.Y+".png");
        if( Files.exists(newFile) ){
            isFull();
            Image im;
            try (InputStream i = new FileInputStream( newFile.toString() ) ) {
                im = new Image(i);
                cacheMemory.put(tileId, im);
            }
            return im;
        }

        Files.createDirectories(directory);
        URL u = new URL("https://"+tileName+"/"+tileId.zoomLevel+"/"+tileId.X+"/"+tileId.Y+".png");
        URLConnection c = u.openConnection();
        c.setRequestProperty("User-Agent", "JaVelo");
        try ( InputStream i = c.getInputStream() ;
              OutputStream o = new FileOutputStream( newFile.toString() ) ) {
                i.transferTo(o);
        }
        Image im;

        try ( InputStream i = new FileInputStream( newFile.toString() ) ) {
            im = new Image(i);
            isFull();
            cacheMemory.put( tileId, im);
        }

        return im;
    }

    /**
     * Représente l'identité d'une tuile OSM
     *
     * @param zoomLevel
     *          le niveau de zoom de la tuile
     *
     * @param X
     *          l'index X de la tuile
     *
     * @param Y
     *          l'index Y de la tuile
     *
     */
    public record TileId(int zoomLevel, int X, int Y) {


        /**
         * Permet de vérifier qu'une tuile est valide
         *
         * @param zoomLevel
         *          zoom
         * @param X
         *          coordonnées x de la tuile
         * @param Y
         *          coordonnées y de la tuile
         *
         * @return true si la tuile est valide, false sinon
         */
        public static boolean isValid (int zoomLevel, double X, double Y) {

            double max = Math.pow(TWO,zoomLevel) - 1 ;

            return zoomLevel >= MIN_ZOOM
                    && X >= MIN_X
                    && X <= max
                    && Y >= MIN_Y
                    && Y <= max;
        }

    }

    /**
     * Permet de savoir si le cache mémoire est rempli
     */
    private void isFull() {
        if (cacheMemory.size() >= INITIAL_CAPACITY) {
            for (TileId LRU : cacheMemory.keySet()) {
                cacheMemory.remove(LRU);
                break;
            }
        }
    }
}

