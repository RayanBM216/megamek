package megamek.server.victory;

import megamek.common.Game;
import megamek.common.options.GameOptions;
import megamek.common.options.OptionsConstants;
import megamek.server.scriptedevent.VictoryTriggeredEvent;
import megamek.server.trigger.Trigger;
import megamek.server.trigger.TriggerSituation;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VictoryHelperTest {

    @Test
    void testCheckForVictory() {
        Game game = mock(Game.class);
        GameOptions options = mock(GameOptions.class);
        when(game.getOptions()).thenReturn(options);
        when(options.booleanOption(OptionsConstants.VICTORY_CHECK_VICTORY)).thenReturn(false);
        when(game.scriptedEvents()).thenReturn(Collections.emptyList());

        VictoryHelper helper = new VictoryHelper(game);
        VictoryResult result = helper.checkForVictory(game, new HashMap<>());

        assertFalse(result.isVictory());
    }

    @Test
    void testCheckVictoryScriptedEvents() {
        Game game = mock(Game.class);
        GameOptions options = mock(GameOptions.class);
        when(game.getOptions()).thenReturn(options);
        when(options.booleanOption(anyString())).thenReturn(false);

        VictoryTriggeredEvent event = mock(VictoryTriggeredEvent.class);
        Trigger trigger = mock(Trigger.class);

        when(event.isGameEnding()).thenReturn(true);
        when(event.trigger()).thenReturn(trigger);
        when(trigger.isTriggered(eq(game), eq(TriggerSituation.ROUND_END))).thenReturn(true);
        when(event.checkVictory(eq(game), any())).thenReturn(new VictoryResult(true));
        when(game.scriptedEvents()).thenReturn(List.of(event));

        VictoryHelper helper = new VictoryHelper(game);
        VictoryResult result = helper.checkVictoryScriptedEvents(game, new HashMap<>());

        assertTrue(result.isVictory());
    }

    @Test
    void testCheckVictoryConditions() {
        Game game = mock(Game.class);
        GameOptions options = mock(GameOptions.class);
        when(game.getOptions()).thenReturn(options);

        // Désactiver toutes les conditions dans les options
        when(options.booleanOption(OptionsConstants.VICTORY_CHECK_VICTORY)).thenReturn(true);
        when(options.booleanOption(OptionsConstants.VICTORY_USE_BV_DESTROYED)).thenReturn(false);
        when(options.booleanOption(OptionsConstants.VICTORY_USE_BV_RATIO)).thenReturn(false);
        when(options.booleanOption(OptionsConstants.VICTORY_USE_KILL_COUNT)).thenReturn(false);
        when(options.booleanOption(OptionsConstants.VICTORY_COMMANDER_KILLED)).thenReturn(false);
        when(options.intOption(OptionsConstants.VICTORY_ACHIEVE_CONDITIONS)).thenReturn(1);
        when(game.gameTimerIsExpired()).thenReturn(false);

        VictoryHelper helper = spy(new VictoryHelper(game));
        doReturn(VictoryResult.noResult()).when(helper).checkVictoryConditions(any(), any());
        VictoryResult result = helper.checkVictoryConditions(game, new HashMap<>());

        assertFalse(result.isVictory(), "Pas de condition remplie, donc pas de victoire");
    }


}
