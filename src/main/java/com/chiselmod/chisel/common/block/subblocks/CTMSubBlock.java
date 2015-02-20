package com.chiselmod.chisel.common.block.subblocks;

import com.chiselmod.chisel.client.render.CTMBlockResources;
import com.chiselmod.chisel.common.block.BlockCarvable;
import scala.actors.threadpool.Arrays;

import java.util.List;

/**
 * Implementation of ICTMSubBlock
 *
 * @author minecreatr
 */
public class CTMSubBlock extends SubBlock implements ICTMSubBlock{


    private CTMBlockResources resources;

    public CTMSubBlock(String name, BlockCarvable parent, CTMBlockResources resources){
        super(name, parent);
        this.resources=resources;
    }

    public CTMSubBlock(String name, BlockCarvable parent){
        super(name, parent);

    }

    public CTMBlockResources getResources(){
        return this.resources;
    }

    /**
     * Generated a CTM subBlock from the given
     * @param parent The parent block
     * @param name The Sub block name
     * @param lore The Lore
     * @return The SubBlock
     */
    public static CTMSubBlock generateSubBlock(BlockCarvable parent, String name, List<String> lore){
        return new CTMSubBlock(name, parent, CTMBlockResources.generateBlockResources(parent, name, lore));
    }

    /**
     * Generated a CTM subBlock from the given
     * @param parent The parent block
     * @param name The Sub block name
     * @param lore The Lore
     * @return The SubBlock
     */
    public static CTMSubBlock generateSubBlock(BlockCarvable parent, String name, String... lore){
        List<String> loreList = Arrays.asList(lore);
        return new CTMSubBlock(name, parent, CTMBlockResources.generateBlockResources(parent, name, loreList));
    }


}
