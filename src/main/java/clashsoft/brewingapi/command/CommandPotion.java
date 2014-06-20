package clashsoft.brewingapi.command;

import java.util.ArrayList;
import java.util.List;

import clashsoft.brewingapi.BrewingAPI;
import clashsoft.brewingapi.potion.type.IPotionType;
import clashsoft.brewingapi.potion.type.PotionType;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;

public class CommandPotion extends CommandBase
{
	public static List<String>	potionNames;
	
	static
	{
		int len = Potion.potionTypes.length;
		potionNames = new ArrayList(64);
		for (int i = 0; i < len; i++)
		{
			if (Potion.potionTypes[i] != null)
			{
				potionNames.add(Potion.potionTypes[i].getName());
			}
		}
	}
	
	@Override
	public int getRequiredPermissionLevel()
	{
		return 2;
	}
	
	@Override
	public String getCommandName()
	{
		return "potion";
	}
	
	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "commands.potion.usage";
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		if (args.length == 0 || !"give".equals(args[0]) && !"add".equals(args[0]) && !"remove".equals(args[0]))
			throw new CommandNotFoundException("commands.potion.notFound");
		
		int index = 1;
		int id = this.getPotionID(args[index]);
		
		if (id == -1)
		{
			ICommandSender sender1 = sender.getEntityWorld().getPlayerEntityByName(args[index]);
			if (sender1 != null)
				sender = sender1;
			
			index++;
			id = this.getPotionID(args[index]);
		}
		String name = sender.getCommandSenderName();
		EntityPlayer player = getCommandSenderAsPlayer(sender);
		
		int duration = 600;
		int amplifier = 0;
		boolean splash = false;
		
		if (id < 0 || id >= Potion.potionTypes.length || Potion.potionTypes[id] == null)
		{
			throw new NumberInvalidException("commands.potion.effect.notFound", new Object[] { Integer.valueOf(id) });
		}
		
		if (Potion.potionTypes[id].isInstant())
		{
			duration = 1;
		}
		else if (args.length >= index + 1)
		{
			duration = parseIntBounded(sender, args[index + 1], 0, 1000000) * 20;
		}
		
		if (args.length >= index + 2)
		{
			amplifier = parseIntBounded(sender, args[index + 2], 1, 256) - 1;
		}
		if (args.length >= index + 3)
		{
			splash = Boolean.parseBoolean(args[index + 3]);
		}
		
		PotionEffect potioneffect = new PotionEffect(id, duration, amplifier);
		IPotionType potionType = PotionType.getFromEffect(potioneffect);
		
		if ("give".equals(args[0]))
		{
			ItemStack stack = new ItemStack(BrewingAPI.potion2, 1, splash ? 2 : 1);
			potionType.apply(stack);
			
			EntityItem entityitem = player.dropPlayerItemWithRandomChoice(stack, false);
			entityitem.delayBeforeCanPickup = 0;
			
			if (splash)
			{
				notifyAdmins(sender, "commands.potion.give.splash.success", potioneffect.getEffectName(), id, duration, amplifier, name);
			}
			else
			{
				notifyAdmins(sender, "commands.potion.give.success", potioneffect.getEffectName(), id, duration, amplifier, name);
			}
		}
		else if ("add".equals(args[0]))
		{
			ItemStack stack = player.getHeldItem();
			potionType.apply(stack);
			
			notifyAdmins(sender, "command.potion.remove.success", potioneffect.getEffectName(), id, duration, amplifier, name);
		}
		else if ("remove".equals(args[0]))
		{
			ItemStack stack = player.getHeldItem();
			potionType.remove(stack);
			
			notifyAdmins(sender, "command.potion.remove.success", potioneffect.getEffectName(), id, duration, amplifier, name);
		}
	}
	
	public int getPotionID(String string)
	{
		try
		{
			return Integer.parseInt(string);
		}
		catch (Exception ex)
		{
			return potionNames.indexOf(string);
		}
	}
	
	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return index == 1;
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, "give", "add", "remove");
		}
		else if (args.length == 2)
		{
			return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
		}
		else if (args.length == 3)
		{
			return getListOfStringsFromIterableMatchingLastWord(args, potionNames);
		}
		return super.addTabCompletionOptions(sender, args);
	}
	
	@Override
	public int compareTo(Object o)
	{
		return 0;
	}
}
