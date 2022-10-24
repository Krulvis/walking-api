package dax_api.shared.helpers.magic;

import org.tribot.api2007.*;
import org.tribot.api2007.types.RSItem;
import dax_api.shared.Pair;


public enum Spell {

    VARROCK_TELEPORT    (
	    SpellBook.Type.STANDARD, 25, "Varrock Teleport",    new Pair<>(1, RuneElement.LAW), new Pair<>(3, RuneElement.AIR),     new Pair<>(1, RuneElement.FIRE)),
    LUMBRIDGE_TELEPORT  (
	    SpellBook.Type.STANDARD, 31, "Lumbridge Teleport",  new Pair<>(1, RuneElement.LAW), new Pair<>(3, RuneElement.AIR),     new Pair<>(1, RuneElement.EARTH)),
    FALADOR_TELEPORT    (
	    SpellBook.Type.STANDARD, 37, "Falador Teleport",    new Pair<>(1, RuneElement.LAW), new Pair<>(3, RuneElement.AIR),     new Pair<>(1, RuneElement.WATER)),
    CAMELOT_TELEPORT    (
	    SpellBook.Type.STANDARD, 45, "Camelot Teleport",    new Pair<>(1, RuneElement.LAW), new Pair<>(5, RuneElement.AIR)),
    ARDOUGNE_TELEPORT   (
	    SpellBook.Type.STANDARD, 51, "Ardougne Teleport",   new Pair<>(2, RuneElement.LAW), new Pair<>(2, RuneElement.WATER)),
    KOUREND_TELEPORT	(
	    SpellBook.Type.STANDARD, 69, "Kourend Castle Teleport",new Pair<>(2, RuneElement.LAW), new Pair<>(2, RuneElement.SOUL),new Pair<>(4, RuneElement.WATER), new Pair<>(5, RuneElement.FIRE)),
    TELEPORT_TO_HOUSE   (
        SpellBook.Type.STANDARD, 69, "Kourend Castle Teleport",new Pair<>(1, RuneElement.LAW), new Pair<>(1, RuneElement.AIR), new Pair<>(1, RuneElement.EARTH))
    ;

    private SpellBook.Type spellBookType;
    private int requiredLevel;
    private String spellName;
    private Pair<Integer, RuneElement>[] recipe;

    Spell(SpellBook.Type spellBookType, int level, String spellName, Pair<Integer, RuneElement>... recipe){
        this.spellBookType = spellBookType;
        this.requiredLevel = level;
        this.spellName = spellName;
        this.recipe = recipe;
    }

    public Pair<Integer, RuneElement>[] getRecipe(){
        return recipe;
    }

    public String getSpellName() {
        return spellName;
    }

    public boolean cast() {
        return canUse(Inventory.getAll(), Equipment.getItems()) && Magic.selectSpell(getSpellName());
    }

    public boolean canUse(){
        return canUse(Inventory.getAll(), Equipment.getItems());
    }

    public boolean canUse(RSItem[] inventory, RSItem[] equipment){
        if (SpellBook.getCurrentSpellBook() != spellBookType){
            return false;
        }
        if (requiredLevel > Skills.SKILLS.MAGIC.getCurrentLevel()){
            return false;
        }
        if (this == ARDOUGNE_TELEPORT && Game.getSetting(165) < 30){
            return false;
        }

        for (Pair<Integer, RuneElement> pair : recipe){
            int amountRequiredForSpell = pair.getKey();
            RuneElement runeElement = pair.getValue();
            if (runeElement.getCount(inventory, equipment) < amountRequiredForSpell){
                return false;
            }
        }
        return true;
    }

}
