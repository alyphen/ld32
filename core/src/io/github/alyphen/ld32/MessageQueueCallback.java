package io.github.alyphen.ld32;

import com.badlogic.gdx.graphics.Color;

import static com.badlogic.gdx.graphics.Color.BLACK;

public abstract class MessageQueueCallback extends Message {

    public MessageQueueCallback(float delay) {
        super("", BLACK, delay);
    }

    public MessageQueueCallback(String text, Color colour, float delay) {
        super(text, colour, delay);
    }

    public abstract void callback();

}
