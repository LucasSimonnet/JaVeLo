package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Permet de gèrer l'affichage de messages d'erreur.
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 */
public final class ErrorManager {
    private final VBox pane;
    private final SequentialTransition transitions;

    private static final double MIN_OPACITY = 0, MAX_OPACITY = 0.8,
            FIRST_TRANSITION_DURATION = 200, SECOND_TRANSITION_DURATION = 500;

    /**
     * Permet de construire un gestionnaire des messages d'erreurs
     */
    public ErrorManager() {
        pane = new VBox();
        pane.getStylesheets().add("error.css");

        SequentialTransition transitions = new SequentialTransition();

        transitions
                .getChildren()
                .add(createTransition(MIN_OPACITY, MAX_OPACITY, FIRST_TRANSITION_DURATION));

        transitions
                .getChildren()
                .add(new PauseTransition());

        transitions
                .getChildren()
                .add(createTransition(MAX_OPACITY, MIN_OPACITY, SECOND_TRANSITION_DURATION));

        transitions.setNode(pane);
        this.transitions = transitions;
    }

    /**
     * Permet de créer une transition
     *
     * @param fromValue
     *      Valeur a laquelle l'animation commence
     * @param toValue
     *      Valeur a laquelle l'animation se termine
     * @param duration
     *      Durée de l'animation
     * @return la transition
     */
    private FadeTransition createTransition(double fromValue, double toValue, double duration){
        FadeTransition transition = new FadeTransition();
        transition.setDuration(new Duration(duration));
        transition.setFromValue(fromValue);
        transition.setToValue(toValue);
        return transition;
    }

    /**
     * @return le panneau sur lequel apparaissent les messages d'erreurs
     */
    public Pane pane() {
        pane.setMouseTransparent(true);
        return pane;
    }

    /**
     * Permet de faire apparaître un message d'erreur à l'écran et produit un son
     *
     * @param message
     *      Message à afficher
     */
    public void displayError(String message) {
        transitions.stop();
        pane
                .getChildren()
                .clear();

        Text text = new Text();
        text.setText(message);

        pane
                .getChildren()
                .add(text);

        java
                .awt
                .Toolkit
                .getDefaultToolkit()
                .beep();

        transitions.play();
    }
}
