package clashsoft.brewingapi.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import clashsoft.brewingapi.BrewingAPI;
import clashsoft.brewingapi.potion.type.PotionType;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

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
		if ("give".equals(args[0]))
		{
			this.givePotion(sender, Arrays.copyOfRange(args, 2, args.length));
		}
	}
	
	public void givePotion(ICommandSender sender, String[] args)
	{
		int id = this.getPotionID(args[0]);
		int duration = 600;
		int amplifier = 0;
		boolean splash = false;
		
		if (id < 0 || id >= Potion.potionTypes.length || Potion.potionTypes[id] == null)
		{
			throw new NumberInvalidException("commands.effect.notFound", new Object[] { Integer.valueOf(id) });
		}
		
		if (args.length >= 2)
		{
			duration = parseIntBounded(sender, args[1], 0, 1000000) * 20;
		}
		if (Potion.potionTypes[id].isInstant())
		{
			duration = 1;
		}
		if (args.length >= 3)
		{
			amplifier = parseIntBounded(sender, args[2], 1, 256) - 1;
		}
		if (args.length >= 4)
		{
			splash = Boolean.parseBoolean(args[3]);
		}
		
		PotionEffect potioneffect = new PotionEffect(id, duration, amplifier);
		ItemStack stack = new ItemStack(BrewingAPI.potion2, 1, splash ? 2 : 1);
		PotionType.getFromEffect(potioneffect).apply(stack);
		
		EntityItem entityitem = ((EntityPlayerMP) sender).dropPlayerItemWithRandomChoice(stack, false);
		entityitem.delayBeforeCanPickup = 0;
		String name = sender.getCommandSenderName();
		
		if (splash)
		{
			notifyAdmins(sender, "commands.potion.give.splash.success", new Object[] { new ChatComponentText(potioneffect.getEffectName()), Integer.valueOf(id), Integer.valueOf(duration), Integer.valueOf(amplifier), name });
		}
		else
		{
			notifyAdmins(sender, "commands.potion.give.success", new Object[] { new ChatComponentText(potioneffect.getEffectName()), Integer.valueOf(id), Integer.valueOf(duration), Integer.valueOf(amplifier), name });
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
			return getListOfStringsMatchingLastWord(args, "give");
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
