package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Classe principale de l'application
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 */

public final class JaVelo extends Application {

    private static final int  MIN_WIDTH = 800, MIN_HEIGHT = 600, MIN_LIST_SIZE = 1,
            LAST_INDEX = MIN_LIST_SIZE, MAX_LIST_SIZE = MIN_LIST_SIZE+1;

    public static void main (String[] args) { launch(args); }

    /**
     * Permet de construire l'interface graphique
     *
     * @throws Exception
     *          si une erreur entrée/sortie se produit
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Graph graph = Graph.loadFrom(Path.of("javelo-data"));
        TileManager tileManager = new TileManager(Path.of("osm-cache"),"tile.openstreetmap.org");
        CostFunction cf = new CityBikeCF(graph);
        RouteBean routeBean = new RouteBean(new RouteComputer(graph,cf));

        ErrorManager errorManager = new ErrorManager();
        Consumer<String> consumer = errorManager::displayError;

        AnnotatedMapManager mapManager = new AnnotatedMapManager(graph, tileManager, routeBean, consumer);

        DoubleProperty position = new SimpleDoubleProperty();

        ElevationProfileManager profileManager = new ElevationProfileManager(routeBean.profileProperty(), position);
        Pane profilePane = profileManager.pane();
        SplitPane.setResizableWithParent(profilePane, false);

        SplitPane splitPane = new SplitPane(mapManager.pane());
        splitPane.setOrientation(Orientation.VERTICAL);
        ObservableList<Node> l = splitPane.getItems();

        routeBean
                .routeProperty()
                .addListener((r,oldR,newR) -> {
            if (routeBean.route()==null && l.size()>MIN_LIST_SIZE)
                l.remove(LAST_INDEX);
            else if (l.size()<MAX_LIST_SIZE)
                l.add(profilePane);
        });

        StackPane stackPane = new StackPane(splitPane,errorManager.pane());

        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Fichier");
        MenuItem menuItem = new MenuItem("Exporter GPX");

        menu
                .getItems()
                .add(menuItem);

        menuBar
                .getMenus()
                .add(menu);

        menuItem
                .disableProperty()
                .bind(routeBean.routeProperty().isNull());

        menuItem.setOnAction(e -> {
            try {
                GpxGenerator.writeGpx("javelo.gpx",routeBean.route()
                        , routeBean.profileProperty().get());
            } catch (IOException exception) {
                throw new UncheckedIOException(exception);
            }
        });

        BorderPane mainPane = new BorderPane(stackPane,menuBar,null,null,null);

        position
                .bind(routeBean.highlightedPositionProperty());

        routeBean
                .highlightedPositionProperty()
                .bind(Bindings
                        .when( mapManager.mousePositionOnRouteProperty().greaterThan(0) )
                        .then( mapManager.mousePositionOnRouteProperty() )
                        .otherwise( profileManager.mousePositionOnProfileProperty() ) );

        mainPane
                .getStylesheets()
                .add("map.css");

        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.setTitle("JaVelo");
        primaryStage.show();
    }
}