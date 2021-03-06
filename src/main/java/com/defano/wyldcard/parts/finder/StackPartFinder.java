package com.defano.wyldcard.parts.finder;

import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.specifiers.*;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.parts.PartException;
import com.defano.wyldcard.parts.bkgnd.BackgroundModel;
import com.defano.wyldcard.parts.card.CardLayerPartModel;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Optional;

/**
 * Provides methods for finding parts that exist within a specific stack.
 *
 * Limitations:
 *
 * This finder ignores the "stack" portion of any {@link CompositePartSpecifier} assuming the caller has already
 * identified the specified stack and directed the {@link #findPart(ExecutionContext, PartSpecifier)} call to the
 * correct {@link StackPartFinder}.
 *
 * This finder does not handle requests for {@link WindowSpecifier} or {@link PartMessageSpecifier} types.
 */
public interface StackPartFinder extends OrderedPartFinder {

    /**
     * Gets the stack model in which parts should be found.
     *
     * @return The stack model to search for parts.
     */
    StackModel getStackModel();

    /**
     * Finds any kind of part contained within this stack: Buttons and fields (card or background layer), cards and
     * backgrounds.
     *
     * @param context The execution context.
     * @param ps A {@link PartSpecifier} object describing the part to find.
     * @return The model of the requested part.
     * @throws PartException Thrown if the part cannot be located.
     */
    @Override
    default PartModel findPart(ExecutionContext context, PartSpecifier ps) throws PartException {
        if (ps instanceof CompositePartSpecifier) {
            return findCompositePart(context, (CompositePartSpecifier) ps);
        } else if (ps instanceof PartPositionSpecifier) {
            return findPartByPosition((PartPositionSpecifier) ps);
        } else if (ps.isCardPartSpecifier()) {
            return context.getCurrentCard().getCardModel().findPart(context, ps);
        } else if (ps.isBackgroundPartSpecifier()) {
            return context.getCurrentCard().getCardModel().getBackgroundModel().findPart(context, ps);
        } else {
            return OrderedPartFinder.super.findPart(context, ps);
        }
    }

    /**
     * Finds any kind of part contained in this stack within a given list of parts. Note that the list of parts is
     * ignored when providing a {@link CompositePartSpecifier} or a {@link PartPositionSpecifier}.
     *
     *
     * @param context The execution context.
     * @param ps The part specifier representing the part to fetch
     * @param parts The list of parts to search
     * @return The model of the requested part.
     * @throws PartException Thrown if the part cannot be located.
     */
    default PartModel findPart(ExecutionContext context, PartSpecifier ps, List<PartModel> parts) throws PartException {
        if (ps instanceof CompositePartSpecifier) {
            return findCompositePart(context, (CompositePartSpecifier) ps);
        } else if (ps instanceof PartPositionSpecifier) {
            return findPartByPosition((PartPositionSpecifier) ps);
        } else {
            return OrderedPartFinder.super.findPart(context, ps, parts);
        }
    }

    /**
     * Finds a part that's part of another part (for example, 'the second card of the first bg', 'card button 3 of card
     * 4 of background 2').
     *
     *
     * @param context The execution context.
     * @param ps The composite part specifier.
     * @return The found part.
     * @throws PartException Thrown if the requested part cannot be found.
     */
    default PartModel findCompositePart(ExecutionContext context, CompositePartSpecifier ps) throws PartException {
        try {
            PartModel foundPart;
            PartSpecifier owningPartSpecifier = ps.getOwningPartExp().evaluateAsSpecifier(context);

            // Special case: This finder assumes the stack portion of the specifier expression refers to the current
            // stack, but does not verify that's true; it simply ignores the "owning stack" and finds the part itself
            if (owningPartSpecifier.getType() == PartType.STACK) {
                return findPart(context, ps.getPart());
            }

            // Recursively find the card or background containing the requested part
            PartModel owningPart = findPart(context, owningPartSpecifier);

            // Looking for a background button or field on a remote card or background
            if (ps.isBackgroundPartSpecifier()) {
                if (owningPart instanceof CardModel) {
                    foundPart = findPart(context, ps.getPart(), ((CardModel) owningPart).getBackgroundModel().getPartsInDisplayOrder(context, ps.getOwner()));
                } else {
                    foundPart = findPart(context, ps.getPart(), ((BackgroundModel) owningPart).getPartsInDisplayOrder(context, ps.getOwner()));
                }
            }

            // Looking for button or field on a remote card
            else if (ps.isCardPartSpecifier()) {
                foundPart = findPart(context, ps.getPart(), ((CardModel) owningPart).getPartsInDisplayOrder(context));
            }

            // Looking for a card in a remote background
            else {
                foundPart = findPart(context, ps.getPart(), ((LayeredPartFinder) owningPart).getPartsInDisplayOrder(context));
            }

            // Special case: Field needs to be evaluated in the context of the requested card
            if (foundPart instanceof CardLayerPartModel) {
                ((CardLayerPartModel) foundPart).setCurrentCardId(owningPart.getId(context));
            }

            return foundPart;

        } catch (HtException e) {
            throw new PartException(e);
        }
    }

    /**
     * Finds a card or background based on its relative position to the current card or background.
     *
     * @param ps The specification of the card or background to find
     * @return The model of the requested part.
     * @throws PartException Thrown if the requested part cannot be found.
     */
    default PartModel findPartByPosition(PartPositionSpecifier ps) throws PartException {

        // Bail if request to find any kind of part other than a card or background
        if (ps.getType() != PartType.BACKGROUND && ps.getType() != PartType.CARD) {
            throw new PartException("Cannot find " + ps.getType().toString().toLowerCase() + " by position.");
        }

        int thisCard = getStackModel().getCurrentCard().getCardIndexInStack();

        try {
            if (ps.getType() == PartType.CARD) {
                switch (ps.getPosition()) {
                    case NEXT:
                        return getStackModel().getCardModel(thisCard + 1);
                    case PREV:
                        return getStackModel().getCardModel(thisCard - 1);
                    case THIS:
                        return getStackModel().getCardModel(thisCard);
                }
            }

            if (ps.getType() == PartType.BACKGROUND) {
                switch (ps.getPosition()) {
                    case NEXT:
                        return getStackModel().getBackground(findNextBackground().getBackgroundId());
                    case PREV:
                        return getStackModel().getBackground(findPrevBackground().getBackgroundId());
                    case THIS:
                        return getStackModel().getCurrentCard().getBackgroundModel();
                }
            }

        } catch (Throwable t) {
            throw new PartException("No such " + ps.getType().name().toLowerCase() + ".");
        }

        throw new PartException("Bug! Unhandled positional part type: " + ps.getType());
    }

    /**
     * Finds the first previous card containing a different background then the current card.
     *
     * @return The first previous card with a different background than the current card.
     * @throws PartException Thrown if no such card can be found.
     */
    default CardModel findPrevBackground() throws PartException {
        int thisCard = getStackModel().getCurrentCard().getCardIndexInStack();
        List<CardModel> prevCards = Lists.reverse(getStackModel().getCardModels().subList(0, thisCard + 1));

        return findNextBackground(prevCards);
    }

    /**
     * Finds the next card containing a different background then the current card.
     *
     * @return The next card with a different background than the current card.
     * @throws PartException Thrown if no such card can be found.
     */
    default CardModel findNextBackground() throws PartException {
        int thisCard = getStackModel().getCurrentCard().getCardIndexInStack();
        int cardCount = getStackModel().getCardCount();
        List<CardModel> nextCards = getStackModel().getCardModels().subList(thisCard, cardCount);

        return findNextBackground(nextCards);
    }

    /**
     * Finds the next card within a list of card models containing a different background than the first card in this
     * list.
     *
     * @param cardList The list of card models to traverse.
     * @return The first card with a different background than the first card.
     * @throws PartException Thrown if no such card can be found.
     */
    default CardModel findNextBackground(List<CardModel> cardList) throws PartException {
        int thisBackground = cardList.get(0).getBackgroundId();

        Optional<CardModel> nextBkgnd = cardList.stream().filter(cardModel -> cardModel.getBackgroundId() != thisBackground).findFirst();
        if (nextBkgnd.isPresent()) {
            return nextBkgnd.get();
        }

        throw new PartException("No such card.");
    }

    /**
     * Finds the card on which a part identified by a given {@link CompositePartSpecifier} lives. When the "owning part"
     * is a background (for example, "button 1 of background 3"), then the first card of that background is returned.
     *
     *
     * @param context The execution context.
     * @param ps A composite part specifier, the owning card of which should be returned.
     * @return The owning card
     * @throws PartException Thrown if no such part can be found.
     */
    default CardModel findOwningCard(ExecutionContext context, CompositePartSpecifier ps) throws PartException {
        BackgroundModel bkgndModel = ps.getOwningPartExp().partFactor(context, BackgroundModel.class);
        if (bkgndModel != null) {
            return bkgndModel.getCardModels(context).get(0);
        }

        CardModel cardModel = ps.getOwningPartExp().partFactor(context, CardModel.class);
        if (cardModel != null) {
            return cardModel;
        }

        throw new PartException("Expected a card or background.");
    }

}
