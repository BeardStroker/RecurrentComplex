/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.reccomplex.gui.editinventorygen;

import ivorius.reccomplex.gui.InventoryWatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lukas on 27.05.14.
 */
public class InventoryGenericInvGen implements IInventory
{
    public List<WeightedRandomChestContent> chestContents;
    private List<ItemStack> cachedItemStacks = new ArrayList<>();

    private List<InventoryWatcher> watchers = new ArrayList<>();

    public InventoryGenericInvGen(List<WeightedRandomChestContent> chestContents)
    {
        this.chestContents = chestContents;

        buildCachedStacks();
    }

    public void addWatcher(InventoryWatcher watcher)
    {
        watchers.add(watcher);
    }

    public void removeWatcher(InventoryWatcher watcher)
    {
        watchers.remove(watcher);
    }

    public List<InventoryWatcher> watchers()
    {
        return Collections.unmodifiableList(watchers);
    }

    @Override
    public int getSizeInventory()
    {
        return cachedItemStacks.size();
    }

    @Override
    public ItemStack getStackInSlot(int var1)
    {
        return var1 < cachedItemStacks.size() ? cachedItemStacks.get(var1) : null;
    }

    @Override
    public ItemStack decrStackSize(int var1, int var2)
    {
        int stackIndex = var1 / 2;

        if (stackIndex < chestContents.size())
        {
            WeightedRandomChestContent chestContent = chestContents.get(stackIndex);

            if (var1 % 2 == 0)
            {
                chestContent.theMinimumChanceToGenerateItem -= var2;
            }
            else
            {
                chestContent.theMaximumChanceToGenerateItem -= var2;
            }

            validateMinMax(chestContent);

//            if (chestContent.theMinimumChanceToGenerateItem <= 0 || chestContent.theMaximumChanceToGenerateItem <= 0)
//                chestContents.remove(stackIndex);

            ItemStack returnStack = cachedItemStacks.get(var1).splitStack(var2);
            markDirtyFromInventoryGenerator();
            return returnStack;
        }
        else
        {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int var1)
    {
        return null;
    }

    @Override
    public void setInventorySlotContents(int var1, ItemStack var2)
    {
        int stackIndex = var1 / 2;

        if (stackIndex < chestContents.size())
        {
            if (var2 != null)
            {
                WeightedRandomChestContent chestContent = chestContents.get(stackIndex);
                chestContent.theItemId = var2;

                if (var1 % 2 == 0)
                {
                    chestContent.theMinimumChanceToGenerateItem = var2.stackSize;
                }
                else
                {
                    chestContent.theMaximumChanceToGenerateItem = var2.stackSize;
                }

                validateMinMax(chestContent);
            }
            else
            {
                chestContents.remove(stackIndex);
            }
        }
        else
        {
            if (var2 != null)
            {
                int min = var1 % 2 == 0 ? var2.stackSize : 1;
                int max = var1 % 2 == 1 ? var2.stackSize : var2.getMaxStackSize();

                WeightedRandomChestContent weightedRandomChestContent = new WeightedRandomChestContent(var2, min, max, 100);
                chestContents.add(weightedRandomChestContent);
            }
        }

        markDirtyFromInventoryGenerator();
    }

    private static void validateMinMax(WeightedRandomChestContent chestContent)
    {
        if (chestContent.theMaximumChanceToGenerateItem < chestContent.theMinimumChanceToGenerateItem)
        {
            int tmp = chestContent.theMaximumChanceToGenerateItem;
            chestContent.theMaximumChanceToGenerateItem = chestContent.theMinimumChanceToGenerateItem;
            chestContent.theMinimumChanceToGenerateItem = tmp;
        }
    }

    @Override
    public String getInventoryName()
    {
        return null;
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return false;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public void markDirty()
    {
        calculateMinMax();
        buildCachedStacks();

        for (InventoryWatcher watcher : watchers)
        {
            watcher.inventoryChanged(this);
        }
    }

    public void markDirtyFromInventoryGenerator()
    {
        buildCachedStacks();

        for (InventoryWatcher watcher : watchers)
        {
            watcher.inventoryChanged(this);
        }
    }

    private void calculateMinMax()
    {
        for (int i = 0; i < cachedItemStacks.size(); i++)
        {
            ItemStack stack = cachedItemStacks.get(i);
            int stackIndex = i / 2;

            if (stackIndex < chestContents.size())
            {
                WeightedRandomChestContent chestContent = chestContents.get(stackIndex);

                if (i % 2 == 0)
                {
                    chestContent.theMinimumChanceToGenerateItem = stack.stackSize;
                }
                else
                {
                    chestContent.theMaximumChanceToGenerateItem = stack.stackSize;
                }
            }
        }

        chestContents.forEach(InventoryGenericInvGen::validateMinMax);
    }

    private void buildCachedStacks()
    {
        cachedItemStacks.clear();
        for (WeightedRandomChestContent chestContent : chestContents)
        {
            ItemStack stackLow = chestContent.theItemId.copy();
            stackLow.stackSize = chestContent.theMinimumChanceToGenerateItem;
            ItemStack stackHigh = chestContent.theItemId.copy();
            stackHigh.stackSize = chestContent.theMaximumChanceToGenerateItem;

            cachedItemStacks.add(stackLow);
            cachedItemStacks.add(stackHigh);
        }
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer var1)
    {
        return true;
    }

    @Override
    public void openInventory()
    {

    }

    @Override
    public void closeInventory()
    {

    }

    @Override
    public boolean isItemValidForSlot(int var1, ItemStack var2)
    {
        return true;
    }
}
