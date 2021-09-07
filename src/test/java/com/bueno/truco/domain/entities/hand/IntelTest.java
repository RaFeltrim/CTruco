package com.bueno.truco.domain.entities.hand;

import com.bueno.truco.domain.entities.deck.Card;
import com.bueno.truco.domain.entities.deck.Suit;
import com.bueno.truco.domain.entities.player.Player;
import com.bueno.truco.domain.entities.round.Round;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class IntelTest {

    @Mock
    private Hand hand;
    @InjectMocks
    private Intel sut;
    @Mock
    Player player1;
    @Mock
    Player player2;

    @Test
    @DisplayName("Should get the same vira of the current hand")
    void ShouldGetSameViraOfCurrentHand() {
        Card card = new Card(7, Suit.CLUBS);
        when(hand.getVira()).thenReturn(card);
        assertEquals(card, sut.getVira());
    }

    @Test
    @DisplayName("Should get the same points of the current hand")
    void ShouldGetSamePointsOfCurrentHand() {
        when(hand.getScore()).thenReturn(HandScore.of(3));
        assertEquals(HandScore.of(3), sut.getHandScore());
    }

    @Test
    @DisplayName("Should get the same card to play against of the current hand")
    void shouldGetSameCardToPlayAgainstOfCurrentHand() {
        Card card = new Card(7, Suit.CLUBS);
        when(hand.getCardToPlayAgainst()).thenReturn(Optional.of(card));
        assertEquals(card, sut.getCardToPlayAgainst().orElse(null));
    }

    @Test
    @DisplayName("Should get the same open cards of the current hand")
    void shouldGetSameOpenCardsOfCurrentHand() {
        final List<Card> cards = List.of(new Card(7, Suit.SPADES), new Card(7, Suit.CLUBS));
        when(hand.getOpenCards()).thenReturn(cards);
        assertIterableEquals(cards, sut.getOpenCards());
    }

    @Test
    @DisplayName("Should get the same played rounds of the current hand")
    void shouldGetSamePlayedRoundsOfCurrentHand() {
        final List<Round> roundsPlayed = new ArrayList<>();
        when(hand.getRoundsPlayed()).thenReturn(roundsPlayed);
        assertIterableEquals(roundsPlayed, sut.getRoundsPlayed());
    }

    @Test
    @DisplayName("Should get opponent score")
    void ShouldGetOpponentScore() {
        when(player2.getScore()).thenReturn(6);
        when(hand.getFirstToPlay()).thenReturn(player1);
        when(hand.getLastToPlay()).thenReturn(player2);
        final int opponentScore = sut.getOpponentScore(player1);
        assertEquals(player2.getScore(), opponentScore);
    }

    @Test
    @DisplayName("Should get opponent id")
    void ShouldGetOpponentId() {
        when(player2.getUsername()).thenReturn("Test");
        when(hand.getFirstToPlay()).thenReturn(player1);
        when(hand.getLastToPlay()).thenReturn(player2);
        final String opponentId = sut.getOpponentId(player1);
        assertEquals(player2.getUsername(), opponentId);
    }
}