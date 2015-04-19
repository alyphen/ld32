package io.github.alyphen.ld32;

import com.badlogic.gdx.graphics.Color;

public class Message {

    private String text;
    private Color colour;
    private float delay;

    public Message(String text, Color colour, float delay) {
        this.text = text;
        this.colour = colour;
        this.delay = delay;
    }

    public String getText() {
        return text;
    }

    public Color getColour() {
        return colour;
    }

    public float getDelay() {
        return delay;
    }

}
