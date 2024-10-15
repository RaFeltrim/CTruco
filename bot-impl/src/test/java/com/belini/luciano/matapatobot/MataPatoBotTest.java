package com.belini.luciano.matapatobot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import com.bueno.spi.model.CardRank;
import com.bueno.spi.model.CardSuit;
import com.bueno.spi.model.GameIntel;
import com.bueno.spi.model.TrucoCard;


import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MataPatoBotTest {

    MataPatoBot mataPatoBot;

    @BeforeEach
    public void createPatoBot() {
        mataPatoBot = new MataPatoBot();
    }

    @Nested
    @DisplayName("First Round Tests")
    class FirstRoundTests {

        @Test
        @DisplayName("Should return true if opponent plays first")
        void shouldReturnTrueForOpponentPlayFirst() {
            TrucoCard opponentCard = TrucoCard.of(CardRank.ACE, CardSuit.SPADES);
            assertThat(mataPatoBot.checkFirstPlay(Optional.of(opponentCard))).isTrue();
        }

        @Test
        @DisplayName("Should return false if we play first")
        void shouldReturnFalseIfWePlayFirst() {
            assertThat(mataPatoBot.checkFirstPlay(Optional.empty())).isFalse();
        }

        @Test
        @DisplayName("Should return Round 1 if bot has three cards")
        public void shouldReturnRound1IfBotHasThreeCards() {
            GameIntel intel = mock(GameIntel.class);
            TrucoCard card1 = TrucoCard.of(CardRank.KING, CardSuit.HEARTS);
            TrucoCard card2 = TrucoCard.of(CardRank.THREE, CardSuit.SPADES);
            TrucoCard card3 = TrucoCard.of(CardRank.ACE, CardSuit.CLUBS);
            when(intel.getCards()).thenReturn(Arrays.asList(card1, card2, card3));
            String round = String.valueOf(mataPatoBot.RoundCheck(intel));
            assertThat(round).isEqualTo("Round 1");
        }
    }

    @Nested
    @DisplayName("Second Round Tests")
    class SecondRoundTests {

        @Test
        @DisplayName("Should return Round 2 if bot has two cards")
        public void shouldReturnRound2IfBotHasTwoCards() {
            GameIntel intel = mock(GameIntel.class);
            TrucoCard card1 = TrucoCard.of(CardRank.KING, CardSuit.HEARTS);
            TrucoCard card2 = TrucoCard.of(CardRank.THREE, CardSuit.SPADES);
            when(intel.getCards()).thenReturn(Arrays.asList(card1, card2));
            String round = String.valueOf(mataPatoBot.RoundCheck(intel));
            assertThat(round).isEqualTo("Round 2");
        }
    }

    @Nested
    @DisplayName("Last Round Tests")
    class LastRoundTests {

        @Test
        @DisplayName("Should return Round 3 if bot has one card")
        public void shouldReturnRound3IfBotHasOneCard() {
            GameIntel intel = mock(GameIntel.class);
            TrucoCard card1 = TrucoCard.of(CardRank.ACE, CardSuit.CLUBS);
            when(intel.getCards()).thenReturn(Arrays.asList(card1));
            String round = String.valueOf(mataPatoBot.RoundCheck(intel));
            assertThat(round).isEqualTo("Round 3");
        }

        @Test
        @DisplayName("Should return 'No cards' if the bot has no cards in hand")
        public void shouldReturnNoCardsIfBotHasNoCards() {
            GameIntel intel = mock(GameIntel.class);
            when(intel.getCards()).thenReturn(Arrays.asList());
            String round = String.valueOf(mataPatoBot.RoundCheck(intel));
            assertThat(round).isEqualTo("No cards");
        }

        @Test
        @DisplayName("Play a strong card, excluding top 3")
        public void shouldPlayStrongCard() {
            GameIntel intel = mock(GameIntel.class);
            TrucoCard card1 = TrucoCard.of(CardRank.THREE, CardSuit.DIAMONDS);
            TrucoCard card2 = TrucoCard.of(CardRank.ACE, CardSuit.SPADES);
            TrucoCard card3 = TrucoCard.of(CardRank.KING, CardSuit.SPADES);
            TrucoCard vira = TrucoCard.of(CardRank.JACK, CardSuit.SPADES);
            TrucoCard opponentCard = TrucoCard.of(CardRank.TWO, CardSuit.HEARTS);
            TrucoCard expected = TrucoCard.of(CardRank.THREE, CardSuit.DIAMONDS);

            when(intel.getCards()).thenReturn(Arrays.asList(card1, card2, card3));
            when(intel.getVira()).thenReturn(vira);
            when(intel.getOpponentCard()).thenReturn(Optional.ofNullable(opponentCard));

            assertThat(mataPatoBot.shouldPlayStrongCard(intel)).isEqualTo(expected);
        }

        @Test
        @DisplayName("Play a top 3 card against manilha")
        public void killTheManilha() {
            GameIntel intel = mock(GameIntel.class);
            TrucoCard card1 = TrucoCard.of(CardRank.THREE, CardSuit.DIAMONDS);
            TrucoCard card2 = TrucoCard.of(CardRank.SEVEN, CardSuit.SPADES);
            TrucoCard card3 = TrucoCard.of(CardRank.FIVE, CardSuit.HEARTS);
            TrucoCard vira = TrucoCard.of(CardRank.FOUR, CardSuit.HEARTS);
            TrucoCard opponentCard = TrucoCard.of(CardRank.FIVE, CardSuit.DIAMONDS);
            TrucoCard expected = TrucoCard.of(CardRank.FIVE, CardSuit.HEARTS);

            when(intel.getCards()).thenReturn(Arrays.asList(card1, card2, card3));
            when(intel.getVira()).thenReturn(vira);
            when(intel.getOpponentCard()).thenReturn(Optional.ofNullable(opponentCard));

            assertThat(mataPatoBot.shouldPlayStrongCard(intel)).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("Raise Tests")
    class RaiseTests {

        @Test
        @DisplayName("Should raise if manilha on last round")
        public void shouldRaiseIfManilhaOnLastRound() {
            GameIntel intel = mock(GameIntel.class);
            TrucoCard card1 = TrucoCard.of(CardRank.ACE, CardSuit.HEARTS);
            TrucoCard vira = TrucoCard.of(CardRank.KING, CardSuit.SPADES);

            when(intel.getCards()).thenReturn(Arrays.asList(card1));
            when(intel.getVira()).thenReturn(vira);

            assertTrue(mataPatoBot.verifyGreatHand(intel));
        }
    }
}