package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

import java.util.List;

/**
 * Permet de gèrer l'affichage et l'interaction avec le profil en long d'un itinéraire.
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 */
public final class ElevationProfileManager {
    private static final int MIN_HORIZONTAL_DISTANCE_PIXEL = 50, MIN_VERTICAL_DISTANCE_PIXEL = 25, POLICE_TEXT = 10;

    private final ReadOnlyObjectProperty <ElevationProfile> profile;
    private final ObjectProperty<Transform> screenToWorld;
    private final ObjectProperty<Transform> worldToScreen;
    private final ObjectProperty<Rectangle2D> rectangle;

    private final SimpleDoubleProperty mousePosition;
    private final ReadOnlyDoubleProperty position;

    private final Insets dim = new Insets(10,10,20,40);
    private final BorderPane pane;
    private final Pane center;
    private final VBox bottom;

    private Line line;
    private Polygon polygon;
    private Path grid;
    private Group textGroup;
    private Text profileStats;

    private final int[] POS_STEPS =
            { 1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000 };
    private final int[] ELE_STEPS =
            { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };

    /**
     * Permet de construire un gestionnaire de profil d'itinéraire
     *
     * @param profile
     *      Propriéte contenant le profil à afficher
     * @param position
     *      Propriéte contenant la position le long du profil à mettre en évidence
     */
    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> profile
            , ReadOnlyDoubleProperty position) {
        this.profile = profile;
        this.position = position;
        worldToScreen = new SimpleObjectProperty<>();
        screenToWorld = new SimpleObjectProperty<>();
        rectangle = new SimpleObjectProperty<>();
        mousePosition = new SimpleDoubleProperty(Double.NaN);

        pane = new BorderPane();
        pane.getStylesheets().add("elevation_profile.css");

        bottom = new VBox();
        bottom.setId("profile_data");

        center = new Pane();
        pane.setCenter(center);
        pane.setBottom(bottom);

        createPane();
        dimRectangle();

        center
                .widthProperty().addListener((o,oldO,newO) -> {
                    dimRectangle();
                    drawProfile();
        });

        center
                .heightProperty()
                .addListener((o,oldO,newO) -> {
                    dimRectangle();
                    drawProfile();
        });

        profile.addListener((o,oldO,newO) -> {
            if (profile.get()!=null) {
                drawProfile();
            }
        });

        pane.setOnMouseMoved(m ->{
            if(rectangle.get().contains(m.getX(),m.getY()))
                mousePosition.set(screenToWorld
                        .get()
                        .transform(m.getX(),0)
                        .getX());
            else
                mousePosition.set(Double.NaN);
        });

        pane.setOnMouseExited(m -> mousePosition.set(Double.NaN));
    }

    /**
     * Permet de dessiner la totalité du profil de l'itinéraire
     */
    private void drawProfile() {
        createTransformProperties();
        drawLine();
        createPolygon();
        createGrid();
    }

    /**
     * Permet de créer le panneau selon les différents éléments graphiques de l'affichage du profil
     */
    private void createPane() {
        List<Node> centerChildren = center.getChildren();

        grid = new Path();
        grid.setId("grid");
        centerChildren.add(grid);

        polygon = new Polygon();
        polygon.setId("profile");
        centerChildren.add(polygon);

        line = new Line();
        centerChildren.add(line);

        textGroup = new Group();
        centerChildren.add(textGroup);

        profileStats = new Text();
        profileStats.setId("profile_data");
        bottom
                .getChildren()
                .add(profileStats);
    }

    /**
     * Permet de créer la totalité de la grille du profil
     */
    private void createGrid() {
        double pLength = profile.get().length();
        grid.getElements().clear();
        textGroup.getChildren().clear();

        createStatistics();

        double dist = screenToWorld.get().deltaTransform(MIN_HORIZONTAL_DISTANCE_PIXEL,0).getX();
        for (int pos_step : POS_STEPS) {
            if (pos_step >= dist) {
                int lineCount = (int)(pLength/pos_step);
                for (int x = 0; x <= lineCount; ++x) {
                    Point2D fromP = worldToScreen.get().transform(pos_step*x,profile.get().minElevation());
                    PathElement moveTo = new MoveTo(fromP.getX(),fromP.getY());
                    createText(fromP, pos_step*x/1000 ,"horizontal");

                    Point2D toP = worldToScreen.get().transform(pos_step*x,profile.get().maxElevation());
                    PathElement lineTo = new LineTo(toP.getX(),toP.getY());

                    grid.getElements().add(moveTo);
                    grid.getElements().add(lineTo);
                }
                break;
            }
        }

        for (int ele_step : ELE_STEPS) {
            Point2D e = worldToScreen.get().deltaTransform(0,0);
            Point2D e1 = worldToScreen.get().deltaTransform(0,ele_step);
            if (e.distance(e1) >= MIN_VERTICAL_DISTANCE_PIXEL) {
                int firstEle = (int)profile.get().minElevation();
                while (firstEle%ele_step != 0){
                    firstEle+=1;
                }
                int lineCount = (int)((profile.get().maxElevation()-firstEle)/ele_step);
                for (int y = 0; y <= lineCount; ++y) {
                    Point2D p = worldToScreen.get().transform(0,ele_step*y+firstEle);
                    PathElement moveTo = new MoveTo(p.getX(),p.getY());
                    createText(p, ele_step*y+firstEle,"vertical");
                    p = worldToScreen.get().transform(pLength,ele_step*y+firstEle);
                    PathElement lineTo = new LineTo(p.getX(),p.getY());
                    grid.getElements().add(moveTo);
                    grid.getElements().add(lineTo);
                }
                break;
            }
        }
    }

    /**
     * Permet de créer la zone de texte contenant les statistiques du profil
     */
    private void createStatistics(){
        ElevationProfile prof = profile.get();
        String statistics = "Longueur : %.1f km" +
                "     Montée : %.0f m" +
                "     Descente : %.0f m" +
                "     Altitude : de %.0f m à %.0f m";

        profileStats.setText(String.format(statistics, prof.length()/1000, prof.totalAscent()
                , prof.totalDescent(), prof.minElevation(), prof.maxElevation()));
    }

    /**
     * Permet de créer les zones de textes des stats de la grille du profil
     *
     * @param p
     *      Point sur le panneau
     * @param text
     *      Valeur des statistiques du profil
     * @param type
     *      Type de la ligne correspondant à la zone de texte
     */
    private void createText(Point2D p, int text, String type) {
        Text etiq = new Text();
        Font f = Font.font("Avenir", POLICE_TEXT);
        etiq.setFont(f);
        if (type.equals("vertical") )
            etiq.setTextOrigin(VPos.CENTER);
        else
            etiq.setTextOrigin(VPos.TOP);

        etiq.setText(String.valueOf(text));
        etiq.setLayoutX((type.equals("vertical")?p.getX()-etiq.prefWidth(0)-2:p.getX()-etiq.prefWidth(0)/2));
        etiq.setLayoutY(p.getY());
        etiq.getStyleClass().addAll("grid_label",type);
        textGroup.getChildren().add(etiq);
    }

    /**
     * Permet de dessiner une ligne représentant la position mise en évidence
     */
    private void drawLine () {
        line.startYProperty().bind(Bindings.select(rectangle, "minY"));
        line.endYProperty().bind(Bindings.select(rectangle, "maxY"));
        line.visibleProperty().bind(position.greaterThanOrEqualTo(0));
        line.layoutXProperty().bind(Bindings.createDoubleBinding(() -> worldToScreen
                        .get()
                        .transform(position.get(), 0)
                        .getX(),
                position
        ));
    }

    /**
     * Permet de stocker les dimensions du rectangle contenant le polygone du profil
     */
    private void dimRectangle() {
        double rWidth = center.getWidth() - dim.getLeft() - dim.getRight();
        double rHeight = center.getHeight() - dim.getBottom() - dim.getTop();
        Rectangle2D r = new Rectangle2D(dim.getLeft(), dim.getTop(), (rWidth>0)?rWidth:0, (rHeight>0)?rHeight:0);
        rectangle.set(r);
    }

    /**
     * Permet de créer les differentes transformations entre le systéme de coordonnées du profil et du panneau
     */
    private void createTransformProperties() {
        Affine screenToWorld = new Affine();
        screenToWorld.prependTranslation(-dim.getLeft(), -dim.getTop());
        Rectangle2D r = rectangle.get();
        ElevationProfile p = profile.get();

        double difProfile = p.maxElevation() - p.minElevation();
        double maxLength = p.length();

        screenToWorld.prependScale( maxLength/r.getWidth(),-difProfile/r.getHeight() );
        screenToWorld.prependTranslation(0,p.maxElevation());
        this.screenToWorld.set(screenToWorld);

        Affine worldToScreen;
        try {
            worldToScreen = screenToWorld.createInverse();
        } catch (NonInvertibleTransformException e) {
            throw new Error(e);
        }
        this.worldToScreen.set(worldToScreen);
    }

    /**
     * Permet de créer le polygon représentant le profil
     */
    private void createPolygon () {
        List<Double> points = polygon.getPoints();
        Rectangle2D r = rectangle.get();
        double minX = r.getMinX();
        double maxX = r.getMaxX();
        double maxY = r.getMaxY();

        points.clear();
        points.add(minX);
        points.add(maxY);

        for (double x = minX ; x<maxX ; ++x){
            points.add(x);
            double xp = profile
                    .get()
                    .elevationAt(
                            screenToWorld
                                    .get()
                                    .transform(x,0)
                                    .getX()
                    );

            points.add(
                    worldToScreen
                            .get()
                            .transform(0,xp)
                            .getY()
            );
        }
        points.add(maxX);
        points.add(maxY);
    }

    /**
     * @return le panneau contenant le dessin du profil
     */
    public Pane pane () {
        return pane;
    }

    /**
     * @return une propriété en lecture seule contenant la position du pointeur de la souris le long du profil
     */
    public ReadOnlyDoubleProperty mousePositionOnProfileProperty () {
        return mousePosition;
    }
}