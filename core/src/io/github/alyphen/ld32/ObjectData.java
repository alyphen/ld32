package io.github.alyphen.ld32;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ObjectData {

    private Animation animation;
    private float stateTime;

    public ObjectData(Animation animation) {
        this.animation = animation;
    }

    public Animation getAnimation() {
        return animation;
    }

    public void tick(float delta) {
        stateTime += delta;
    }

    public TextureRegion getCurrentFrame() {
        return getAnimation().getKeyFrame(stateTime);
    }

}
