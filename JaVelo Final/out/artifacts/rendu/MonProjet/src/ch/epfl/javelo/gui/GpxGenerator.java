package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RoutePoint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Représente un générateur d'itinéraire au format GPX
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 */
public abstract class GpxGenerator implements Document {

    private static Document newDocument() {
        try {
            return DocumentBuilderFactory
                    .newDefaultInstance()
                    .newDocumentBuilder()
                    .newDocument();
        } catch (ParserConfigurationException e) {
            throw new Error(e);
        }
    }

    /**
     * Permet de creer le document Gpx selon un itinéraire et son profil
     *
     * @param route
     *          Itinéraire
     * @param profile
     *          Profil de l'itinéraire
     *
     * @return le document Gpx correspondant a l'itinéraire et son profil
     */
    public static Document createGpx (Route route, ElevationProfile profile) {
        Document doc = newDocument();

        Element root = doc
                .createElementNS("http://www.topografix.com/GPX/1/1",
                        "gpx");
        doc.appendChild(root);

        root.setAttributeNS(
                "http://www.w3.org/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                "http://www.topografix.com/GPX/1/1 "
                        + "http://www.topografix.com/GPX/1/1/gpx.xsd");
        root.setAttribute("version", "1.1");
        root.setAttribute("creator", "JaVelo");

        Element metadata = doc.createElement("metadata");
        root.appendChild(metadata);

        Element name = doc.createElement("name");
        metadata.appendChild(name);
        name.setTextContent("Route JaVelo");

        Element rte = doc.createElement("rte");
        root.appendChild(rte);

        for (PointCh point : route.points()) {
            Element rtept = doc.createElement("rtept");
            rtept.setAttribute("lon", String.valueOf( Math.toDegrees( point.lon() ) ) );
            rtept.setAttribute("lat", String.valueOf( Math.toDegrees( point.lat() ) ) );
            rte.appendChild(rtept);

            RoutePoint r = route.pointClosestTo(point);
            Element ele = doc.createElement("ele");
            ele.setTextContent( String.valueOf( profile.elevationAt( r.position() ) ) );
            rtept.appendChild(ele);
        }

        return doc;
    }

    /**
     * Permet d'écrire le document Gpx dans le fichier selon l'itinéraire et son profil
     *
     * @param name
     *          Nom d'un fichier
     * @param route
     *          Itinéraire
     * @param profile
     *          Profil de l'itinéraire
     *
     * @return le document Gpx écrit dans le fichier correspondant a l'itinéraire et son profil
     *
     * @throws IOException
     *          si une erreur entrée/sortie se produit
     */
    public static Document writeGpx (String name, Route route, ElevationProfile profile) throws IOException {
        Document doc = createGpx(route,profile);
        Writer w = new FileWriter(name);

        try {
            Transformer transformer = TransformerFactory
                    .newDefaultInstance()
                    .newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc),
                    new StreamResult(w));
        } catch (TransformerException t) {
            throw new Error(t);
        }

        return doc;
    }
}
