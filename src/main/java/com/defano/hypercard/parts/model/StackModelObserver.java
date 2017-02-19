/*
 * StackModelObserver
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.model;

import com.defano.hypercard.parts.CardPart;

public interface StackModelObserver {
    void onCardClosing(CardPart oldCard);
    void onCardOpening(CardPart newCard);
    void onCardOpened(CardPart newCard);
}
