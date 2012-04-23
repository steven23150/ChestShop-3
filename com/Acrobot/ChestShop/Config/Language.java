package com.Acrobot.ChestShop.Config;

/**
 * @author Acrobot
 */
public enum Language {
    prefix: "&a[Shop] &f"
    iteminfo: "&aInformation sur l'Objet :&f"
    ACCESS_DENIED: "&4Vous n'avez pas la permission !"
    NOT_ENOUGH_MONEY: "Vous n'avez pas de quoi payer !"
    NOT_ENOUGH_MONEY_SHOP: "Le vendeur n'a pas de quoi vous payer !"
    NO_BUYING_HERE: "Vous ne pouvez pas acheter ici !"
    NO_SELLING_HERE: "Vous ne pouvez pas vendre ici !"
    NOT_ENOUGH_SPACE_IN_INVENTORY: "Vous n'avez plus de place dans votre inventaire !"
    NOT_ENOUGH_SPACE_IN_CHEST: "Il n'y a plus de place dans le coffre !"
    NOT_ENOUGH_ITEMS_TO_SELL: "Vous n'avez pas d'objet à vendre !"
    NOT_ENOUGH_STOCK: "Ce Magasin est vide."
    NOT_ENOUGH_STOCK_IN_YOUR_SHOP: "Votre Magasin de %material est vide !"
    YOU_BOUGHT_FROM_SHOP: "Vous avez achète %amount %item à %owner pour %price."
    SOMEBODY_BOUGHT_FROM_YOUR_SHOP: "%buyer vous achète %amount %item pour %price."
    YOU_SOLD_TO_SHOP: "Vous avez vendu %amount %item à %buyer pour %price."
    SOMEBODY_SOLD_TO_YOUR_SHOP: "%seller vous à vendu %amount %item pour %price."
    YOU_CANNOT_CREATE_SHOP: "Vous ne pouvez pas créer ce type de Magasin !"
    NO_CHEST_DETECTED: "Impossible de trouver le coffre !"
    ANOTHER_SHOP_DETECTED: "Le Magasin d'un autre joueur est détecté !"
    CANNOT_ACCESS_THE_CHEST: "Vous n'avez pas la permission d'accéder à ce coffre !"
    PROTECTED_SHOP: "Le Magasin est protégé par LWC !"
    SHOP_CREATED: "Le Magasin est créé !"
    NO_PERMISSION: "Vous n'avez pas la permission !"
    INCORRECT_ITEM_ID: "Vous avez spécifié une ID invalide !"
    SHOP_REFUNDED: "Vous avez ete remboursé de %amount."
    NOT_ENOUGH_PROTECTIONS: "Vous avez atteint la limite de protection !"
    CANNOT_CREATE_SHOP_HERE: "Vous ne pouvez pas créer un Magasin ici !"



    private final String text;

    private Language(String def) {
        text = def;
    }

    public String toString() {
        return text;
    }
}
