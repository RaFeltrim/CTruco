package com.correacarini.impl.trucomachinebot;

import com.bueno.spi.model.CardToPlay;
import com.bueno.spi.model.GameIntel;
import com.bueno.spi.model.TrucoCard;
import com.bueno.spi.service.BotServiceProvider;

import java.util.ArrayList;
import java.util.List;

import static com.bueno.spi.model.CardRank.*;
import static com.bueno.spi.model.GameIntel.RoundResult.LOST;
import static com.bueno.spi.model.GameIntel.RoundResult.WON;

public class TrucoMachineBot implements BotServiceProvider {
    @Override
    public boolean getMaoDeOnzeResponse(GameIntel intel) {
        TrucoCard vira = intel.getVira();
        List<TrucoCard> cards = intel.getCards();
        List<TrucoCard> manilhas = cards.stream().filter(card -> card.isManilha(vira)).toList();
        List<TrucoCard> strongCards = cards.stream().filter(card -> card.getRank().compareTo(KING) > 0 || card.isManilha(vira))
                .toList();
        if(intel.getOpponentScore()==11){
            return true;
        }
        if(strongCards.size() == 3 && manilhas.size() > 0 && intel.getOpponentScore() <=7){
            return true;
        }
        if (strongCards.size() == 3 && intel.getOpponentScore() >=8) {
            return true;
        }
        return false;
    }

    @Override
    public boolean decideIfRaises(GameIntel intel) {
        if(intel.getScore() == 11 || intel.getOpponentScore() == 11) return false;

        if(hasZapAndManilha(intel)) return true;

        if(intel.getScore() - intel.getOpponentScore() > 3) {
            if(hasManilhaAndThree(intel))  return true;
            if(has3Threes(intel)) return true;
            if(!intel.getRoundResults().isEmpty() && intel.getRoundResults().get(0).equals(WON)){
                if(hasManilhaAndTwo(intel)) return true;
                if(hasDiamondAndKing(intel)) return true;
            }
            if(!intel.getRoundResults().isEmpty() && intel.getRoundResults().get(0).equals(LOST)){
                if(getNumberOfManilhas(intel) == 2) return true;
            }
        }

        if(intel.getRoundResults().size() == 2){
            if(intel.getOpponentCard().isPresent()){
                if(intel.getCards().get(0).compareValueTo(intel.getOpponentCard().get(), intel.getVira()) > 0){
                    return true;
                }
            }
            if(intel.getCards().get(0).isZap(intel.getVira())) return true;
        }

        if(!intel.getRoundResults().isEmpty() && intel.getRoundResults().get(0).equals(WON)){
            if(hasZap(intel) || hasHearts(intel)) return true;
        }
        return false;
    }

    @Override
    public CardToPlay chooseCard(GameIntel intel) {
        List<TrucoCard> cards = new ArrayList<>(intel.getCards());
        TrucoCard vira  = intel.getVira();

        TrucoCard greatestCard = getGreatestCard(cards, vira);

        if(!intel.getRoundResults().isEmpty() && intel.getRoundResults().get(0).equals(WON)){
            if(hasZap(intel)) return CardToPlay.discard(getLowestCard(intel.getCards(), vira));
        }

        if(intel.getOpponentCard().isPresent()){
            TrucoCard minimalGreaterCard = getMinimalGreaterCard(cards, vira, intel.getOpponentCard().get());
            if(minimalGreaterCard == null) {
                if (greatestCard.relativeValue(vira) == intel.getOpponentCard().get().relativeValue((vira))){
                    return CardToPlay.of(greatestCard);
                }

                TrucoCard lowestCard = getLowestCard(cards, vira);
                return CardToPlay.of(lowestCard);
            }
            return CardToPlay.of(minimalGreaterCard);
        }

        return CardToPlay.of(greatestCard);
    }

    @Override
    public int getRaiseResponse(GameIntel intel) {
        if(hasZapCopas(intel)) return 1;

        if (hasZapAndManilha(intel)){
            int round = intel.getRoundResults().size();

            // Se for a primeira rodada eu só aceito
            if (round == 0) return 0;
        }
        return -1;
    }

    @Override
    public String getName() {
        return "Truco Machine";
    }

    private TrucoCard getGreatestCard( List<TrucoCard> cards, TrucoCard vira){
        TrucoCard greatestCard = cards.get(0);

        for (TrucoCard card : cards) {
            int comparison = card.compareValueTo(greatestCard, vira);
            if (comparison > 0) {
                greatestCard = card;
            }
        }

        return greatestCard;
    }

    private TrucoCard getMinimalGreaterCard( List<TrucoCard> cards, TrucoCard vira, TrucoCard opponentCard){
        TrucoCard minimalGreaterCard = null;

        for (TrucoCard card : cards) {
            int comparison = card.compareValueTo(opponentCard, vira);
            if (comparison > 0) {
                if (minimalGreaterCard == null || card.compareValueTo(minimalGreaterCard, vira) < 0) {
                    minimalGreaterCard = card;
                }
            }
        }

        return minimalGreaterCard;
    }

    private TrucoCard getLowestCard( List<TrucoCard> cards, TrucoCard vira){
        TrucoCard lowestCard = cards.get(0);

        for (TrucoCard card : cards) {
            int comparison = card.compareValueTo(lowestCard, vira);
            if (comparison < 0) {
                lowestCard = card;
            }
        }

        return lowestCard;
    }

    private boolean hasZapAndManilha(GameIntel intel) {
        boolean zap = false;
        int manilhas = 0;

        for (TrucoCard card : intel.getCards()) {
            if (card.isZap(intel.getVira())) {
                zap = true;
            }
            if (card.isOuros(intel.getVira()) || card.isEspadilha(intel.getVira()) || card.isCopas(intel.getVira())) {
                manilhas += 1;
            }
        }

        return zap && manilhas >= 1;
    }

    private boolean hasManilhaAndThree(GameIntel intel) {
        int manilhas = 0;
        boolean hasThree = false;

        for (TrucoCard card : intel.getCards()) {
            if (card.getRank().equals(THREE)) {
                hasThree = true;
            }
            if (card.isOuros(intel.getVira()) || card.isEspadilha(intel.getVira()) || card.isCopas(intel.getVira()) || card.isZap(intel.getVira())) {
                manilhas += 1;
            }
        }

        return hasThree && manilhas >= 1;
    }

    private boolean hasManilhaAndTwo(GameIntel intel) {
        int manilhas = 0;
        boolean hasTwo = false;

        for (TrucoCard card : intel.getCards()) {
            if (card.getRank().equals(TWO)) {
                hasTwo = true;
            }
            if (card.isOuros(intel.getVira()) || card.isEspadilha(intel.getVira()) || card.isCopas(intel.getVira()) || card.isZap(intel.getVira())) {
                manilhas += 1;
            }
        }
        return hasTwo && manilhas >= 1;
    }

    private boolean hasZap(GameIntel intel) {
        for (TrucoCard card : intel.getCards()) {
            if (card.isZap(intel.getVira())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasHearts(GameIntel intel) {
        for (TrucoCard card : intel.getCards()) {
            if (card.isCopas(intel.getVira())) {
                return true;
            }
        }
        return false;
    }

    private boolean has3Threes(GameIntel intel){
        int threes = 0;
        for (TrucoCard card : intel.getCards()) {
            if (card.getRank().equals(THREE)) {
                threes++;
            }
        }
        return threes == 3;
    }

    private int getNumberOfManilhas(GameIntel intel) {
        int manilhas = 0;
        for (TrucoCard card : intel.getCards()) {
            if (card.isOuros(intel.getVira()) || card.isEspadilha(intel.getVira()) || card.isCopas(intel.getVira()) || card.isZap(intel.getVira())) {
                manilhas += 1;
            }
        }
        return manilhas;
    }
    private boolean hasDiamondAndKing(GameIntel intel) {
        boolean diamond = false;
        boolean king = false;

        for (TrucoCard card : intel.getCards()) {
            if (card.isOuros(intel.getVira())){
                diamond = true;
            }
            if (card.getRank().equals(KING)){
                king = true;
            }
        }
        return diamond && king;
    }

    private boolean hasZapCopas(GameIntel intel){
        boolean zap = false;
        boolean copas = false;
        for (TrucoCard card : intel.getCards()){
            if (card.isZap(intel.getVira())){
                zap = true;
            }

            if (card.isCopas(intel.getVira())){
                copas = true;
            }
        }

        if (zap && copas) return true;
        return false;
    }
}
