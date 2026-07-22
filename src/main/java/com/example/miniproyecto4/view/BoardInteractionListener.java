package com.example.miniproyecto4.view;

import com.example.miniproyecto4.model.Coordinate;

/**
 * Every mouse interaction a board cell can report. Having three methods
 * (instead of a single {@code EventHandler<MouseEvent>}) is what makes
 * {@link BoardInteractionAdapter} worth having: implementers only
 * override the one or two events they actually care about.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public interface BoardInteractionListener
{
    void onCellClicked(Coordinate coordinate);

    void onCellHoverEntered(Coordinate coordinate);

    void onCellHoverExited(Coordinate coordinate);
}
