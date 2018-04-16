/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cardmatchingfx;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author Sedrick
 */
public final class CardTile extends ImageView
{

    private final String title;
    private final String suit;
    private final String face;
    private boolean showing;
    private final Image backImage;
    private final Image frontImage;
    private boolean isMatched;

    public CardTile(String suit, String face, Image backImage, Image frontImage)
    {
        this.face = face;
        this.suit = suit;
        this.title = face + suit;
        this.backImage = backImage;
        this.frontImage = frontImage;
        this.setImage(backImage);
        showing = false;
        isMatched = false;
    }

    public CardTile(String suit, String face, Image backImage, Image frontImage, double fitWidth, double fitHeight)
    {
        this.face = face;
        this.suit = suit;
        this.title = face + suit;
        this.backImage = backImage;
        this.frontImage = frontImage;
        this.setImage(backImage);
        showing = false;
        isMatched = false;
        this.setFitHeight(fitHeight);
        this.setFitWidth(fitWidth);
    }

    public String getTitle()
    {
        return title;
    }

    public String getSuit()
    {
        return this.suit;
    }

    public String getFace()
    {
        return this.face;
    }

    public void showBackImage()
    {
        this.setImage(backImage);
        showing = false;
    }

    public void showFrontImage()
    {
        this.setImage(frontImage);
        showing = true;
    }

    public boolean isShowing()
    {
        return showing;
    }

    public void setMatched(boolean control)
    {
        this.isMatched = control;
    }

    public boolean isMatched()
    {
        return isMatched;
    }
}
