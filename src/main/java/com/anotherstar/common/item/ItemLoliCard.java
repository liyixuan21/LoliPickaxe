package com.anotherstar.common.item;

import java.util.List;

import com.anotherstar.client.creative.CreativeTabLoader;
import com.anotherstar.client.util.LoliCardUtil;
import com.anotherstar.common.LoliPickaxe;
import com.anotherstar.common.gui.LoliGUIHandler;
import com.anotherstar.network.LoliCardPacket;
import com.anotherstar.network.LoliCardPacket.ItemType;
import com.anotherstar.network.NetworkHandler;
import com.google.common.collect.Lists;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemLoliCard extends Item {

	public ItemLoliCard() {
		this.setUnlocalizedName("loliCard");
		this.setCreativeTab(CreativeTabLoader.loliTabs);
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return stack.hasTagCompound() ? 64 : 1;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (world.isRemote) {
			player.openGui(LoliPickaxe.instance, LoliGUIHandler.GUI_LOLI_CARD, world, 0, hand == EnumHand.MAIN_HAND ? 0 : 1, 0);
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		if (world.isRemote && LoliCardUtil.customArtNames != null && LoliCardUtil.customArtNames.length != 0 && (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("picture"))) {
			List<String> accessName = Lists.newArrayList();
			for (String name : LoliCardUtil.customArtNames) {
				if (!name.contains("''")) {
					accessName.add(name);
				}
			}
			if (!accessName.isEmpty()) {
				NetworkHandler.INSTANCE.sendMessageToServer(new LoliCardPacket(itemSlot, ItemType.LOLICARD, accessName.get(world.rand.nextInt(accessName.size()))));
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (isInCreativeTab(tab) && LoliCardUtil.customArtNames != null && LoliCardUtil.customArtNames.length != 0) {
			for (String name : LoliCardUtil.customArtNames) {
				if (!name.contains("''")) {
					ItemStack stack = new ItemStack(this);
					NBTTagCompound nbt = new NBTTagCompound();
					nbt.setString("picture", name);
					stack.setTagCompound(nbt);
					items.add(stack);
				}
			}
		}
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
			if (nbt.hasKey("picture")) {
				tooltip.add(nbt.getString("picture"));
			}
		}
	}

}
