package com.spiritlight.mythicdrops;

public enum ItemType {
    INGREDIENT, ITEM, MYTHIC, UNKNOWN;

    public static class Type {
        public static ItemType getType(String string) {
            if(Main.mythic.contains(string)) return MYTHIC;
            if(Main.itemList.contains(string)) return ITEM;
            if(Main.ingredientList.contains(string)) return INGREDIENT;
            return UNKNOWN;
        }
    }
}


