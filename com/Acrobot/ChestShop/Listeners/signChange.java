package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Language;
import com.Acrobot.ChestShop.Config.MaxPrice;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.Economy.Economy;
import com.Acrobot.ChestShop.Items.Items;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Protection.Security;
import com.Acrobot.ChestShop.Signs.restrictedSign;
import com.Acrobot.ChestShop.Utils.WorldGuard.uWorldGuard;
import com.Acrobot.ChestShop.Utils.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public class signChange implements Listener {

    @EventHandler
    public static void onSignChange(SignChangeEvent event) {
        Block signBlock = event.getBlock();
        String[] line = event.getLines();

        boolean isAlmostReady = uSign.isValidPreparedSign(line);

        Player player = event.getPlayer();
        ItemStack stock = Items.getItemStack(line[3]);
        Material mat = stock == null ? null : stock.getType();

        boolean playerIsAdmin = Permission.has(player, Permission.ADMIN);

        if (isAlmostReady) {
            if (mat == null) {
                player.sendMessage(Config.getLocal(Language.INCORRECT_ITEM_ID));
                dropSign(event);
                return;
            }
        } else {
            if (restrictedSign.isRestricted(line)) {
                if (!restrictedSign.hasPermission(player, line)) {
                    player.sendMessage(Config.getLocal(Language.ACCESS_DENIED));
                    dropSign(event);
                    return;
                }
                Block secondSign = signBlock.getRelative(BlockFace.DOWN);
                if (!playerIsAdmin && (!uSign.isSign(secondSign) || !uSign.isValid((Sign) secondSign.getState())
                        || !uSign.canAccess(player, (Sign) secondSign))) dropSign(event);
            }
            return;
        }

        if (formatFirstLine(line[0], player)) event.setLine(0, uLongName.stripName(player.getName()));

        String thirdLine = formatThirdLine(line[2]);
        if (thirdLine == null) {
            dropSign(event);
            player.sendMessage(Config.getLocal(Language.YOU_CANNOT_CREATE_SHOP));
            return;
        }
        event.setLine(2, thirdLine);
        event.setLine(3, formatFourthLine(line[3], stock));

        Chest chest = uBlock.findChest(signBlock);

        boolean isAdminShop = uSign.isAdminShop(event.getLine(0));
        if (!isAdminShop) {
            if (chest == null) {
                player.sendMessage(Config.getLocal(Language.NO_CHEST_DETECTED));
                dropSign(event);
                return;
            } else if (!playerIsAdmin) {
                if (!Security.canPlaceSign(player, (Sign) signBlock.getState())) {
                    player.sendMessage(Config.getLocal(Language.CANNOT_CREATE_SHOP_HERE));
                    dropSign(event);
                    return;
                }

                Block chestBlock = chest.getBlock();
                boolean canBuildTowny = uSign.towny == null || uTowny.canBuild(player, signBlock.getLocation(), chest.getLocation());
                boolean canBuildWorldGuard = uWorldGuard.wg == null || uWorldGuard.canBuildShopHere(signBlock.getLocation());
                boolean bothActive = (uSign.towny != null && Config.getBoolean(Property.TOWNY_INTEGRATION))
                        && (uWorldGuard.wg != null && Config.getBoolean(Property.WORLDGUARD_INTEGRATION));

                if (((!canBuildTowny || !canBuildWorldGuard) && !bothActive) || (bothActive && !canBuildTowny && !canBuildWorldGuard)) {
                    player.sendMessage(Config.getLocal(Language.CANNOT_CREATE_SHOP_HERE));
                    dropSign(event);
                    return;
                }

                boolean canAccess = !Security.isProtected(chestBlock) || Security.canAccess(player, chestBlock);
                if (!canAccess) {
                    player.sendMessage(Config.getLocal(Language.CANNOT_ACCESS_THE_CHEST));
                    dropSign(event);
                    return;
                }
            }
        }

        float buyPrice = uSign.buyPrice(thirdLine);
        float sellPrice = uSign.sellPrice(thirdLine);

        if (!playerIsAdmin && (!canCreateShop(player, mat.getId(), buyPrice != -1, sellPrice != -1) || !MaxPrice.canCreate(buyPrice, sellPrice, mat))) {
            player.sendMessage(Config.getLocal(Language.YOU_CANNOT_CREATE_SHOP));
            dropSign(event);
            return;
        }

        float shopCreationPrice = Config.getFloat(Property.SHOP_CREATION_PRICE);
        boolean paid = shopCreationPrice != 0 && !isAdminShop && !Permission.has(player, Permission.NOFEE);
        if (paid) {
            if (!Economy.hasEnough(player.getName(), shopCreationPrice)) {
                player.sendMessage(Config.getLocal(Language.NOT_ENOUGH_MONEY));
                dropSign(event);
                return;
            }

            Economy.subtract(player.getName(), shopCreationPrice);
        }

        if (Config.getBoolean(Property.PROTECT_SIGN_WITH_LWC)) {
            if (!Security.protect(player.getName(), signBlock)) player.sendMessage(Config.getLocal(Language.NOT_ENOUGH_PROTECTIONS));
        }
        if (Config.getBoolean(Property.PROTECT_CHEST_WITH_LWC) && chest != null && Security.protect(player.getName(), chest.getBlock())) {
            player.sendMessage(Config.getLocal(Language.PROTECTED_SHOP));
        }

        uLongName.saveName(player.getName());
        player.sendMessage(Config.getLocal(Language.SHOP_CREATED) + (paid ? " - " + Economy.formatBalance(shopCreationPrice) : ""));

        uHeroes.addHeroExp(player);
    }

    private static boolean canCreateShop(Player player, int ID, boolean buy, boolean sell) {
        if (Permission.has(player, Permission.SHOP_CREATION_ID + Integer.toString(ID))) return true;

        if (buy && !Permission.has(player, Permission.SHOP_CREATION_BUY)) return false;
        if (sell && !Permission.has(player, Permission.SHOP_CREATION_SELL)) return false;

        return true;
    }

    private static String formatThirdLine(String thirdLine) {
        thirdLine = thirdLine.toUpperCase();
        String[] split = thirdLine.split(":");
        if (uNumber.isFloat(split[0])) thirdLine = "B " + thirdLine;
        if (split.length == 2 && uNumber.isFloat(split[1])) thirdLine = thirdLine + " S";
        if (thirdLine.length() > 15) thirdLine = thirdLine.replace(" ", "");


        return (thirdLine.length() > 15 ? null : thirdLine);
    }

    private static String formatFourthLine(String fourthLine, ItemStack is) {
        int index = (fourthLine.indexOf(':') != -1 ? fourthLine.indexOf(':') : 9999);
        if (fourthLine.indexOf('-') < index && fourthLine.indexOf('-') != -1) index = fourthLine.indexOf('-');

        StringBuilder toReturn = new StringBuilder(3);
        String matName = fourthLine.split(":|-")[0];
        matName = matName.trim();
        if (uNumber.isInteger(matName)) matName = Items.getName(is, false);
        int iPos = 15 - (fourthLine.length() - index);
        if (index != 9999 && matName.length() > iPos) matName = matName.substring(0, iPos);
        if (Items.getItemStack(matName).getType() == is.getType()) toReturn.append(matName);
        else toReturn.append(is.getTypeId());

        if (index != -1 && index != 9999) toReturn.append(fourthLine.substring(index));
        return uSign.capitalizeFirst(toReturn.toString(), ' ');
    }

    private static boolean formatFirstLine(String line1, Player player) {
        return line1.isEmpty() ||
                (!line1.equals(uLongName.stripName(player.getName()))
                        && !Permission.has(player, Permission.ADMIN)
                        && !Permission.otherName(player, line1));
    }

    private static void dropSign(SignChangeEvent event) {
        event.setCancelled(true);

        Block block = event.getBlock();
        block.setType(Material.AIR);
        block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.SIGN, 1));
    }
}
