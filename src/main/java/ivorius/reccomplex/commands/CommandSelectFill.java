/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.reccomplex.commands;

import ivorius.ivtoolkit.blocks.BlockArea;
import ivorius.ivtoolkit.blocks.BlockCoord;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.entities.StructureEntityInfo;
import ivorius.reccomplex.utils.IBlockState;
import ivorius.reccomplex.utils.BlockStates;
import ivorius.reccomplex.utils.ServerTranslations;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by lukas on 09.06.14.
 */
public class CommandSelectFill extends CommandSelectModify
{
    @Override
    public String getCommandName()
    {
        return RCConfig.commandPrefix + "fill";
    }

    @Override
    public String getCommandUsage(ICommandSender var1)
    {
        return ServerTranslations.usage("commands.selectFill.usage");
    }

    @Override
    public void processCommandSelection(EntityPlayerMP player, StructureEntityInfo structureEntityInfo, BlockCoord point1, BlockCoord point2, String[] args)
    {
        if (args.length >= 1)
        {
            World world = player.getEntityWorld();

            Block dstBlock = getBlockByText(player, args[0]);
            int[] dstMeta = args.length >= 2 ? getMetadatas(args[1]) : new int[]{0};
            List<IBlockState> dst = IntStream.of(dstMeta).mapToObj(i -> BlockStates.fromMetadata(dstBlock, i)).collect(Collectors.toList());

            for (BlockCoord coord : new BlockArea(point1, point2))
            {
                IBlockState state = dst.get(player.getRNG().nextInt(dst.size()));
                world.setBlock(coord.x, coord.y, coord.z, state.getBlock(), BlockStates.getMetadata(state), 3);
            }
        }
        else
        {
            throw ServerTranslations.wrongUsageException("commands.selectFill.usage");
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender commandSender, String[] args)
    {
        if (args.length == 1)
            return getListOfStringsFromIterableMatchingLastWord(args, Block.blockRegistry.getKeys());
        else if (args.length == 2)
        {
            return getListOfStringsMatchingLastWord(args, "0");
        }

        return null;
    }
}
