package ru.merkii.rduels.gui.invui.animation;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.animation.impl.AbstractAnimation;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.gui.SlotElement.ItemSlotElement;
import xyz.xenondevs.invui.item.Item;

/**
 * An animation that shows the slots row by row,
 * from bottom to top.
 */
public class CurtainAnimation extends AbstractAnimation {

    private final Item curtainReplacement;
    private final Gui gui;
    private final Sound sound;
    private final SlotElement[] slotElements;
    private int reverseRow;
    private boolean curtainLifted;

    /**
     * Creates a new {@link CurtainAnimation}.
     *
     * @param tickDelay The delay between each slot being shown.
     * @param sound     Whether a sound should be played when the slot is shown.
     */
    public CurtainAnimation(int tickDelay, Gui gui, Sound sound, Item curtainReplacement) {
        super(tickDelay);

        this.gui = gui;
        this.sound = sound;
        this.curtainReplacement = curtainReplacement;

        this.slotElements = gui.getSlotElements();
        this.reverseRow = gui.getHeight() - 1;

        addShowHandler((frame, index) -> playSound());
    }

    @Override
    protected void handleFrame(int frame) {
        boolean showedSomething = false;

        while (!showedSomething && reverseRow >= 0) {
            for (int x = 0; x < getWidth(); x++) {
                int index = convToIndex(x, reverseRow);

                if (!curtainLifted && getSlots().contains(index)) {
                    gui.setSlotElement(index, new ItemSlotElement(curtainReplacement));
                    playSound();
                    continue;
                }

                showedSomething = true;
                show(index);
            }

            reverseRow--;
        }

        if (reverseRow < 0) {
            if (!curtainLifted) {
                curtainLifted = true;
                reverseRow = getHeight() - 1;
                return;
            }
            finish();
        }
    }

    private void playSound() {
        for (Player viewer : getCurrentViewers())
            viewer.playSound(viewer.getLocation(), sound, 1, 1);
    }

    private boolean isCurtain(SlotElement slot) {
        ItemStack item = item(slot);
        if (item == null)
            return false;
        Material slotMaterial = item.getType();
        ItemStack curtainItem = curtainReplacement.getItemProvider().get();
        return curtainItem.getType() == slotMaterial;
    }

    private ItemStack item(SlotElement slot) {
        return slot.getItemStack(null);
    }

    public SlotElement slot(int index) {
        return slotElements[index];
    }

}
