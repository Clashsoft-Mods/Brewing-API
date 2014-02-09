package clashsoft.brewingapi.command;

import java.util.List;

import clashsoft.brewingapi.BrewingAPI;
import clashsoft.brewingapi.brewing.PotionType;

import net.minecraft.command.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;

public class CommandGivePotion extends CommandBase implements ICommand
{
	public static String[]	potionNames;
	
	@Override
	public int getRequiredPermissionLevel()
	{
		return 2;
	}
	
	@Override
	public String getCommandName()
	{
		return "givepotion";
	}
	
	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "commands.givepotion.usage";
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		if (args.length < 2)
		{
			throw new WrongUsageException("commands.givepotion.usage", new Object[0]);
		}
		else
		{
			EntityPlayerMP entityplayermp = getPlayer(sender, args[0]);
			
			{
				int i = this.getPotionID(args[1]);
				int j = 600;
				int k = 30;
				int l = 0;
				boolean m = false;
				
				if (i < 0 || i >= Potion.potionTypes.length || Potion.potionTypes[i] == null)
				{
					throw new NumberInvalidException("commands.effect.notFound", new Object[] { Integer.valueOf(i) });
				}
				
				if (args.length >= 3)
				{
					k = parseIntBounded(sender, args[2], 0, 1000000);
					
					if (Potion.potionTypes[i].isInstant())
					{
						j = k;
					}
					else
					{
						j = k * 20;
					}
				}
				else if (Potion.potionTypes[i].isInstant())
				{
					j = 1;
				}
				
				if (args.length >= 4)
				{
					l = parseIntBounded(sender, args[3], 0, 255);
				}
				
				if (args.length >= 5)
				{
					m = Boolean.parseBoolean(args[4]);
				}
				
				{
					PotionEffect potioneffect = new PotionEffect(i, j, l);
					ItemStack stack = new ItemStack(BrewingAPI.potion2, 1, m ? 2 : 1);
					PotionType b = new PotionType(potioneffect, l, j);
					b.addPotionTypeToItemStack(stack);
					
					EntityItem entityitem = entityplayermp.dropPlayerItemWithRandomChoice(stack, false);
					entityitem.delayBeforeCanPickup = 0;
					String name = entityplayermp.getDisplayName();
					
					if (m)
						notifyAdmins(sender, "commands.givepotion.success.splash", new Object[] {
								new ChatComponentText(potioneffect.getEffectName()),
								Integer.valueOf(i),
								Integer.valueOf(l),
								name,
								Integer.valueOf(k) });
					else
						notifyAdmins(sender, "commands.givepotion.success", new Object[] {
								new ChatComponentText(potioneffect.getEffectName()),
								Integer.valueOf(i),
								Integer.valueOf(l),
								name,
								Integer.valueOf(k) });
				}
			}
		}
	}
	
	public int getPotionID(String string)
	{
		int i = -1;
		try
		{
			i = Integer.parseInt(string);
		}
		catch (Exception ex)
		{
			for (int j = 0; j < potionNames.length; j++)
			{
				if (string.equals(potionNames[j]))
				{
					return j;
				}
			}
		}
		return i;
	}
	
	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return index == 0;
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 2)
		{
			if (potionNames == null)
			{
				int len = Potion.potionTypes.length;
				potionNames = new String[len];
				for (int i = 0; i < len; i++)
				{
					if (Potion.potionTypes[i] != null)
					{
						potionNames[i] = StatCollector.translateToLocal(Potion.potionTypes[i].getName()).replace(" ", "").toLowerCase();
					}
					else
					{
						potionNames[i] = "";
					}
				}
			}
			
			List list = getListOfStringsMatchingLastWord(args, potionNames);
			return list;
		}
		return super.addTabCompletionOptions(sender, args);
	}
	
	@Override
	public int compareTo(Object o)
	{
		return 0;
	}
}
