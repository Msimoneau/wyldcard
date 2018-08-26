package com.defano.wyldcard.icons;

import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 * A {@link ButtonIcon} backed by a user-generated image.
 */
public class UserIcon implements ButtonIcon {

    private final String name;
    private final BufferedImage imageData;

    public UserIcon(String name, BufferedImage imageData) {
        this.name = name;
        this.imageData = imageData;
    }

    @Override
    public int getId() {
        int nameHash = name.hashCode();

        if (nameHash == Integer.MIN_VALUE) {
            return 0;
        } else {
            return Math.abs(nameHash);
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public AlphaImageIcon getImage() {
        return new AlphaImageIcon(new ImageIcon(imageData), 1.0f);
    }

}
