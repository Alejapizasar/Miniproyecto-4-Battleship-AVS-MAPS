package com.example.miniproyecto4.view;

import com.example.miniproyecto4.model.Coordinate;

/**
 * No-op default implementation of {@link BoardInteractionListener}, in
 * the classic AWT-style "Adapter" spirit ({@code MouseAdapter} etc.):
 * concrete listeners extend this and override only the callbacks they
 * need instead of implementing all three methods every time.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public abstract class BoardInteractionAdapter implements BoardInteractionListener
{
    @Override
    public void onCellClicked(Coordinate coordinate)
    {
        // Default: do nothing. Override where a click matters.
    }

    @Override
    public void onCellHoverEntered(Coordinate coordinate)
    {
        // Default: do nothing. Override where hover feedback matters.
    }

    @Override
    public void onCellHoverExited(Coordinate coordinate)
    {
        // Default: do nothing.
    }
}
