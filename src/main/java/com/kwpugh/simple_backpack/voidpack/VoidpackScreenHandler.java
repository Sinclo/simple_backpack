package com.kwpugh.simple_backpack.voidpack;

import com.google.common.collect.Sets;
import com.kwpugh.simple_backpack.Backpack;
import com.kwpugh.simple_backpack.backpack.BackpackItem;
import com.kwpugh.simple_backpack.bundle.SimpleBundleItem;
import com.kwpugh.simple_backpack.enderpack.EnderPackItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import java.util.Set;

public class VoidpackScreenHandler extends GenericContainerScreenHandler
{
    private final ScreenHandlerType<?> type;
    public static final Set<Item> SHULKER_BOXES;

    private final Inventory inventory;
    private final PlayerInventory playerInventory;
    public final int inventoryWidth = 9;
    public final int inventoryHeight = 6;

    static
    {
        SHULKER_BOXES = Sets.newHashSet(Items.SHULKER_BOX, Items.BLACK_SHULKER_BOX, Items.BLUE_SHULKER_BOX,
                Items.BROWN_SHULKER_BOX, Items.CYAN_SHULKER_BOX, Items.GRAY_SHULKER_BOX, Items.GREEN_SHULKER_BOX,
                Items.LIGHT_BLUE_SHULKER_BOX, Items.LIGHT_GRAY_SHULKER_BOX, Items.LIME_SHULKER_BOX,
                Items.MAGENTA_SHULKER_BOX, Items.ORANGE_SHULKER_BOX, Items.PINK_SHULKER_BOX, Items.RED_SHULKER_BOX,
                Items.WHITE_SHULKER_BOX, Items.YELLOW_SHULKER_BOX, Items.PURPLE_SHULKER_BOX);
    }

    public VoidpackScreenHandler(int syncId, PlayerInventory playerInventory)
    {
        this(syncId, playerInventory, new SimpleInventory(54));
    }

    public VoidpackScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory)
    {
        this(Backpack.VOID_PACK_SCREEN_HANDLER, syncId, playerInventory, inventory);
    }

    protected VoidpackScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, Inventory inventory)
    {
        super(ScreenHandlerType.GENERIC_9X6, syncId, playerInventory, inventory, 6);
        this.type = type;
        this.inventory = inventory;
        this.playerInventory = playerInventory;

        checkSize(inventory, inventoryHeight * inventoryWidth);
        inventory.onOpen(playerInventory.player);
        setupSlots(false);
    }

    public void setupSlots(final boolean includeChestInventory)
    {
        int i = (this.inventoryHeight - 4) * 18;

        int n;
        int m;
        for(n = 0; n < this.inventoryHeight; ++n)
        {
            for(m = 0; m < 9; ++m)
            {
                this.addSlot(new VoidpackLockedSlot(inventory, m + n * 9, 8 + m * 18, 18 + n * 18));
            }
        }

        for(n = 0; n < 3; ++n)
        {
            for(m = 0; m < 9; ++m)
            {
                this.addSlot(new VoidpackLockedSlot(playerInventory, m + n * 9 + 9, 8 + m * 18, 103 + n * 18 + i));
            }
        }

        for(n = 0; n < 9; ++n)
        {
            this.addSlot(new VoidpackLockedSlot(playerInventory, n, 8 + n * 18, 161 + i));
        }
    }

    public static class VoidpackLockedSlot extends Slot
    {
        public VoidpackLockedSlot(Inventory inventory, int index, int x, int y)
        {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity)
        {
            return stackMovementIsAllowed(getStack());
        }

        @Override
        public boolean canInsert(ItemStack stack)
        {
            return stackMovementIsAllowed(stack);
        }

        // Prevents items that override canBeNested() from being inserted into backpack
        public boolean stackMovementIsAllowed(ItemStack stack)
        {
            return stack.getItem().canBeNested();
        }
    }

    @Override
    public ScreenHandlerType<?> getType() {
        return type;
    }

    @Override
    public void onSlotClick(int slotId, int clickData, SlotActionType actionType, PlayerEntity playerEntity)
    {
        // Prevents single or shift-click while pack is open
        if (slotId >= 0)  // slotId < 0 are used for networking internals
        {
            ItemStack stack = getSlot(slotId).getStack();

            if (stack.getItem() instanceof BackpackItem ||
                stack.getItem() instanceof VoidpackItem ||
                stack.getItem() instanceof EnderPackItem ||
                stack.getItem() instanceof SimpleBundleItem ||
                SHULKER_BOXES.contains(stack.getItem()))
            {
                // Return to caller with no action
                return;
            }
        }

        super.onSlotClick(slotId, clickData, actionType, playerEntity);
    }

    @Override
    public void close(PlayerEntity player)
    {
        super.close(player);
        this.inventory.onClose(player);
    }
}