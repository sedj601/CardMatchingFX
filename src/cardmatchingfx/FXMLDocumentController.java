/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cardmatchingfx;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

/**
 *
 * @author blj0011
 */
public class FXMLDocumentController implements Initializable
{

    @FXML
    StackPane spBoardContainer;
    @FXML
    StackPane paneBlock;
    @FXML
    AnchorPane apMain;
//    @FXML
//    Pane paneShuffle;

    List<CardTile> cards = new ArrayList();
    List<CardTile> cardsBeingMatched = new ArrayList();//Used to keep up with the current two cards that are pressed inorder to deteremine if they are a match
    boolean isGameOver = false;
    final double cardsWidth = 50;
    final double cardsHeight = cardsWidth * 1.4;
    GridPane tempGridPane = new GridPane();

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        // TODO
        loadCards();
        createGridPane();
        addMouseClickHandlerToCards();
    }

    @FXML
    public void handleBtnShuffle(ActionEvent actionEvent)
    {
        tempGridPane.getChildren().clear();
        Collections.shuffle(cards);

        //paneShuffle.getChildren().addAll(cards);
        Shape path = new Circle(100);
        paneBlock.getChildren().add(path);
        path.setFill(Color.BLUE);

        paneBlock.toFront();
        paneBlock.getChildren().addAll(cards);

        List<Transition> transitions = new ArrayList();

        for (int i = 0; i < cards.size(); i++) {
            Transition transition = createPathTransition(path, cards.get(i));
            transition.jumpTo(Duration.seconds(2).multiply(i * 1.0 / cards.size()));
            transitions.add(transition);
        }

        for (int i = 0; i < cards.size(); i++) {
            final int t = i;
            transitions.get(i).setOnFinished((event) -> {
                tempGridPane.add(cards.get(t), t % 9, t / 9);
                if (t == cards.size() - 1) {
                    paneBlock.toBack();
                }
            });
            transitions.get(i).play();
        }

    }

    private PathTransition createPathTransition(Shape shape, Node node)
    {

        final PathTransition transition = new PathTransition(
                Duration.seconds(1),
                shape,
                node
        );

        transition.setAutoReverse(false);
        transition.setCycleCount(5);
        transition.setInterpolator(Interpolator.LINEAR);

        return transition;
    }

    private void loadCards()
    {
//        String[] cardFace = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
//        String[] cardSuits = {"cardSpades", "cardHearts", "cardDiamonds", "cardClubs"};
        String cardBack = "cardBack_";
        String[] cardBackColor = {"green", "red", "blue"};
        String cardBackDesign = "12345";
//
        Image tempCardBack = new Image(getClass().getResourceAsStream("images/cards/" + cardBack + cardBackColor[1] + cardBackDesign.charAt(4) + ".png"));
//        for (String suit : cardSuits) {
//            for (int i = 0; i < cardFace.length / 2; i++) {
//                Image tempImage = new Image(getClass().getResourceAsStream("images/cards/" + suit + cardFace[i] + ".png"));
//                cards.add(new CardTile(suit, cardFace[i], tempCardBack, tempImage, cardsWidth, cardsHeight));
//            }
//        }

        String cardSuits = "01";
        String cardFaces = "012345678";
        for (int z = 0; z < 2; z++) {
            for (int i = 0; i < cardSuits.length(); i++) {
                for (int t = 0; t < cardFaces.length(); t++) {
                    Image tempImage = new Image(getClass().getResourceAsStream("images/blue_border/pieceBlue_border" + cardSuits.charAt(i) + cardFaces.charAt(t) + ".png"));
                    cards.add(new CardTile(Character.toString(cardSuits.charAt(i)) + Character.toString(cardFaces.charAt(t)), Character.toString(cardFaces.charAt(t)), tempCardBack, tempImage, cardsWidth, cardsHeight));
                }
            }
        }

    }

    private void createGridPane()
    {
        tempGridPane.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);

        for (int i = 0; i < cards.size(); i++) {
            tempGridPane.add(cards.get(i), i % 9, i / 9);//Add ImageViews to the GridPane
            System.out.println((i % 9) + " : " + (i / 9));
        }

        spBoardContainer.getChildren().add(tempGridPane);
    }

    private void addMouseClickHandlerToCards()
    {
        cards.forEach((view) -> {
            //Set ImageViews' onMouseClicked handler
            view.setOnMouseClicked(event -> {
                if (!view.isShowing())//If card face value is not showing
                {
                    view.showFrontImage();//show it.
                    cardsBeingMatched.add(view);//Add card being clicked to list so it can be compared against the next card
                    if (cardsBeingMatched.size() == 2)//Once two cards are in the list, see if they are equal.
                    {
                        if (cardsBeingMatched.get(0).getTitle().equals(cardsBeingMatched.get(1).getTitle()))//If cards are equal a match is found
                        {
                            cardsBeingMatched.get(0).setMatched(true);
                            cardsBeingMatched.get(1).setMatched(true);
                            System.out.println("Match");
                            cardsBeingMatched.clear();//clear the list to prepare to compare the next two cards that are clicked
                            checkToSeeIfGameIsOver();//Check to see if game is over after finding a match.
                        }
                        else//If cards are not equal
                        {
                            System.out.println("No match");
                            //wait half a second and flip cards back over
                            paneBlock.toFront();//Use to block mouse events until cards flip back over.
                            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), (event1) -> {
                                System.out.println(".5 seconds need to pass before another mouse click!");
                            }));
                            timeline.setCycleCount(1);
                            timeline.play();
                            timeline.setOnFinished(event1 -> {
                                cardsBeingMatched.get(0).showBackImage();
                                cardsBeingMatched.get(1).showBackImage();
                                cardsBeingMatched.clear();//clear the list to prepare to compare the next two cards that are clicked
                                paneBlock.toBack();
                            });
                        }
                    }
                }
            });
        });
    }

    private void restGame()
    {
        cards.forEach(card -> {
            card.showBackImage();
            card.setMatched(false);
        });
        isGameOver = false;
    }

    private void checkToSeeIfGameIsOver()
    {
        boolean isOver = true;

        for (CardTile card : cards) {
            if (!card.isMatched()) {
                isOver = false;
            }
        }

        if (isOver) {
            ButtonType newGame = new ButtonType("New Game", ButtonBar.ButtonData.OK_DONE);
            ButtonType exit = new ButtonType("Exit", ButtonBar.ButtonData.CANCEL_CLOSE);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Game Over!", newGame, exit);
            alert.setContentText("Good job!");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == newGame) {
                restGame();
            }
            else {
                Platform.exit();
            }
        }
    }
}
