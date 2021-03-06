/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.reccomplex.worldgen.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Random;

/**
 * Created by lukas on 25.05.14.
 */
public interface WeightedItemCollection
{
    ItemStack getRandomItemStack(Random random);

    String getDescriptor();
}
