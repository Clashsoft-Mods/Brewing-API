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
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.StatCollector;

public class CommandGivePotion extends CommandBase implements ICommand
{
	/**
	 * Return the required permission level for this command.
	 */
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
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return "commands.givepotion.usage";
	}
	
	@Override
	public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
	{
		if (par2ArrayOfStr.length < 2)
		{
			throw new WrongUsageException("commands.givepotion.usage", new Object[0]);
		}
		else
		{
			EntityPlayerMP entityplayermp = getPlayer(par1ICommandSender, par2ArrayOfStr[0]);
			
			{
				int i = this.getPotionID(par2ArrayOfStr[1]);
				int j = 600;
				int k = 30;
				int l = 0;
				boolean m = false;
				
				if (i < 0 || i >= Potion.potionTypes.length || Potion.potionTypes[i] == null)
				{
					throw new NumberInvalidException("commands.effect.notFound", new Object[] { Integer.valueOf(i) });
				}
				
				if (par2ArrayOfStr.length >= 3)
				{
					k = parseIntBounded(par1ICommandSender, par2ArrayOfStr[2], 0, 1000000);
					
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
				
				if (par2ArrayOfStr.length >= 4)
				{
					l = parseIntBounded(par1ICommandSender, par2ArrayOfStr[3], 0, 255);
				}
				
				if (par2ArrayOfStr.length >= 5)
				{
					m = Boolean.parseBoolean(par2ArrayOfStr[4]);
				}
				
				{
					PotionEffect potioneffect = new PotionEffect(i, j, l);
					ItemStack stack = new ItemStack(BrewingAPI.potion2, 1, m ? 2 : 1);
					PotionType b = new PotionType(potioneffect, l, j);
					b.addPotionTypeToItemStack(stack);
					
					EntityItem entityitem = entityplayermp.dropPlayerItem(stack);
					entityitem.delayBeforeCanPickup = 0;
					
					if (m)
						notifyAdmins(par1ICommandSender, "commands.givepotion.success.splash", new Object[] { ChatMessageComponent.createFromTranslationKey(potioneffect.getEffectName()), Integer.valueOf(i), Integer.valueOf(l), entityplayermp.getEntityName(), Integer.valueOf(k) });
					else
						notifyAdmins(par1ICommandSender, "commands.givepotion.success", new Object[] { ChatMessageComponent.createFromTranslationKey(potioneffect.getEffectName()), Integer.valueOf(i), Integer.valueOf(l), entityplayermp.getEntityName(), Integer.valueOf(k) });
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
			for (int j = 0; j < Potion.potionTypes.length; j++)
			{
				if (Potion.potionTypes[j] != null && StatCollector.translateToLocal(Potion.potionTypes[j].getName()).replace(" ", "").equalsIgnoreCase(string))
				{
					i = j;
					break;
				}
			}
		}
		return i;
	}
	
	@Override
	public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2)
	{
		return par2 == 0;
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
	{
		if (par2ArrayOfStr.length == 2)
		{
			String[] potionNames = new String[Potion.potionTypes.length];
			for (int i = 0; i < Potion.potionTypes.length; i++)
			{
				if (Potion.potionTypes[i] != null)
					potionNames[i] = StatCollector.translateToLocal(Potion.potionTypes[i].getName()).replace(" ", "").toLowerCase();
				else
					potionNames[i] = "";
			}
			
			List list = getListOfStringsMatchingLastWord(par2ArrayOfStr, potionNames);
			return list;
		}
		return super.addTabCompletionOptions(par1ICommandSender, par2ArrayOfStr);
	}
}
