package io.github.alyphen.ld32;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

import static java.lang.Math.round;
import static java.lang.Math.min;

public class MessageQueue {

    private Array<Message> messages;
    private Message message;
    private float timeDelay;
    private float messageIndex;

    public MessageQueue() {
        messages = new Array<>();
        messages.ordered = true;
    }

    public void queueMessage(Message message) {
        messages.add(message);
    }

    public void tick(float delta) {
        if (message == null && messages.size != 0) {
            message = messages.get(0);
            timeDelay = messages.get(0).getDelay();
            if (message instanceof MessageQueueCallback) ((MessageQueueCallback) message).callback();
            messages.removeIndex(0);
        }
        if (message != null) {
            if (messageIndex >= message.getText().length()) {
                timeDelay -= delta;
            } else {
                messageIndex += delta * 20;
            }
            if (timeDelay <= 0) {
                message = null;
                messageIndex = 0;
            }
        }
    }

    public String getMessage() {
        return message != null ? message.getText().substring(0, min(round(messageIndex), message.getText().length())) + (messageIndex < message.getText().length() ? "_" : "") : null;
    }

    public Color getMessageColour() {
        return message != null ? message.getColour() : null;
    }
}
