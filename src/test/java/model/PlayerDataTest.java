package model;

import com.example.miniproyecto4.model.PlayerData;
import com.example.miniproyecto4.model.ShotResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link PlayerData}: every counter reacts correctly to
 * each possible {@link ShotResult}, and blank names fall back to a
 * default nickname.
 *
 * @author Alejandro Valencia Sandoval
 */
class PlayerDataTest
{
    @Test
    void registerShotMissIncrementsOnlyShotsAndMisses()
    {
        PlayerData data = new PlayerData("Alejo");

        data.registerShot(ShotResult.MISS);

        assertEquals(1, data.getShotsFired());
        assertEquals(1, data.getMisses());
        assertEquals(0, data.getHits());
        assertEquals(0, data.getShipsSunk());
    }

    @Test
    void registerShotHitIncrementsShotsAndHits()
    {
        PlayerData data = new PlayerData("Alejo");

        data.registerShot(ShotResult.HIT);

        assertEquals(1, data.getShotsFired());
        assertEquals(1, data.getHits());
        assertEquals(0, data.getShipsSunk());
    }

    @Test
    void registerShotSunkIncrementsHitsAndShipsSunk()
    {
        PlayerData data = new PlayerData("Alejo");

        data.registerShot(ShotResult.SUNK);

        assertEquals(1, data.getShotsFired());
        assertEquals(1, data.getHits());
        assertEquals(1, data.getShipsSunk());
    }

    @Test
    void multipleShotsAccumulateCorrectly()
    {
        PlayerData data = new PlayerData("Alejo");

        data.registerShot(ShotResult.MISS);
        data.registerShot(ShotResult.HIT);
        data.registerShot(ShotResult.SUNK);
        data.registerShot(ShotResult.MISS);

        assertEquals(4, data.getShotsFired());
        assertEquals(2, data.getMisses());
        assertEquals(2, data.getHits());
        assertEquals(1, data.getShipsSunk());
    }

    @Test
    void blankNameFallsBackToDefault()
    {
        PlayerData data = new PlayerData("   ");
        assertEquals("Jugador", data.getName());
    }
}
