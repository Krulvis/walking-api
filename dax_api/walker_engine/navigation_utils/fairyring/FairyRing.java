package dax_api.walker_engine.navigation_utils.fairyring;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.*;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import dax_api.shared.helpers.VarbitHelper.RSVarBit;
import dax_api.walker_engine.WaitFor;
import dax_api.walker_engine.interaction_handling.InteractionHelper;
import dax_api.walker_engine.navigation_utils.fairyring.letters.FirstLetter;
import dax_api.walker_engine.navigation_utils.fairyring.letters.SecondLetter;
import dax_api.walker_engine.navigation_utils.fairyring.letters.ThirdLetter;

import static scripts.dax_api.walker_engine.navigation_utils.fairyring.letters.FirstLetter.*;
import static scripts.dax_api.walker_engine.navigation_utils.fairyring.letters.SecondLetter.*;
import static scripts.dax_api.walker_engine.navigation_utils.fairyring.letters.ThirdLetter.*;

public class FairyRing {

	public static final int
		INTERFACE_MASTER = 398,
		TELEPORT_CHILD = 26,
		ELITE_DIARY_VARBIT = 4538;
	private static final int[]
			DRAMEN_STAFFS = {772,9084};

	private static RSObject[] ring;


	private static RSInterface getTeleportButton() {
		return Interfaces.get(INTERFACE_MASTER, TELEPORT_CHILD);
	}

	public static boolean takeFairyRing(Locations location){

		if(location == null)
			return false;
		if (RSVarBit.get(ELITE_DIARY_VARBIT).getValue() == 0 && Equipment.getCount(DRAMEN_STAFFS) == 0){
			if (!InteractionHelper.click(InteractionHelper.getRSItem(Filters.Items.idEquals(DRAMEN_STAFFS)), "Wield")){
				return false;
			}
		}
		handleSpecialCases();
		if(!hasInterface()){
			if(hasCachedLocation(location)){
				return takeLastDestination(location) && WaitFor.milliseconds(500, 1200) != null;
			} else if(!openFairyRing()){
				return false;
			}
		}
		final RSTile myPos = Player.getPosition();
		return location.turnTo() && pressTeleport() && Timing.waitCondition(() -> myPos.distanceTo(Player.getPosition()) > 20,8000) && WaitFor.milliseconds(500, 1200) != null;
	}

	private static boolean hasInterface(){
		return Interfaces.isInterfaceSubstantiated(INTERFACE_MASTER);
	}

	private static boolean hasCachedLocation(Locations location){
		ring = Objects.findNearest(25,"Fairy ring");
		return ring.length > 0 && Filters.Objects.actionsContains(location.toString()).test(ring[0]);
	}

	private static boolean takeLastDestination(Locations location){
		final RSTile myPos = Player.getPosition();
		return InteractionHelper.click(ring[0],"Last-destination (" + location + ")") &&
				Timing.waitCondition(() -> myPos.distanceTo(Player.getPosition()) > 20,8000);
	}

	private static boolean pressTeleport(){
		RSInterface iface = getTeleportButton();
		return iface != null && iface.click();
	}

	private static boolean openFairyRing(){
		if(ring.length == 0)
			return false;
		return InteractionHelper.click(ring[0],"Configure") &&
				Timing.waitCondition(() -> Interfaces.isInterfaceSubstantiated(INTERFACE_MASTER),10000);
	}

	public enum Locations {
		ABYSSAL_AREA(A, L, R),
		ABYSSAL_NEXUS(D, I, P),
		APE_ATOLL(C, L, R),
		ARCEUUS_LIBRARY(C, I, S),
		ARDOUGNE_ZOO(B, I, S),
		CANIFIS(C, K, S),
		CHASM_OF_FIRE(D, J, R),
		COSMIC_ENTITYS_PLANE(C, K, P),
		DORGESH_KAAN_SOUTHERN_CAVE(A, J, Q),
		DRAYNOR_VILLAGE_ISLAND(C, L, P),
		EDGEVILLE(D, K, R),
		ENCHANTED_VALLEY(B, K, Q),
		FELDIP_HILLS_HUNTER_AREA(A, K, S),
		FISHER_KINGS_REALM(B, J, R),
		GORAKS_PLANE(D, I, R),
		HAUNTED_WOODS(A, L, Q),
		HAZELMERE(C, L, S),
		ISLAND_SOUTHEAST_ARDOUGNE(A, I, R),
		KALPHITE_HIVE(B, I, Q),
		KARAMJA_KARAMBWAN_SPOT(D, K, P),
		LEGENDS_GUILD(B, L, R),
		LIGHTHOUSE(A, L, P),
		MCGRUBOR_WOODS(A, L, S),
		MISCELLANIA(C, I, P),
		MISCELLANIA_PENGUINS(A, J, S),
		MORT_MYRE_ISLAND(B, I, P),
		MORT_MYRE_SWAMP(B, K, R),
		MOUNT_KARUULM(C, I, R),
		MUDSKIPPER_POINT(A, I, Q),
		MYREQUE_HIDEOUT(D, L, S),
		NORTH_OF_NARDAH(D, L, Q),
		PISCATORIS_HUNTER_AREA(A, K, Q),
		POH(D, I, Q),
		POISON_WASTE(D, L, R),
		POLAR_HUNTER_AREA(D, K, S),
		RELLEKKA_SLAYER_CAVE(A, J, R),
		SHILO_VILLAGE(C, K, R),
		SINCLAIR_MANSION(C, J, R),
		SOUTH_CASTLE_WARS(B, K, P),
		TOWER_OF_LIFE(D, J, P),
		TZHAAR(B, L, P),
		WIZARDS_TOWER(D, I, S),
		YANILLE(C, I, Q),
		ZANARIS(B, K, S),
		ZUL_ANDRA(B, J, S);

		FirstLetter first;
		SecondLetter second;
		ThirdLetter third;

		Locations(FirstLetter first, SecondLetter second, ThirdLetter third) {
			this.first = first;
			this.second = second;
			this.third = third;
		}

		public boolean turnTo() {
			return first.turnTo() && WaitFor.milliseconds(400, 800) != null &&
					second.turnTo() && WaitFor.milliseconds(400, 800) != null &&
					third.turnTo() && WaitFor.milliseconds(400, 800) != null;
		}

		@Override
		public String toString() {
			return "" + first + second + third;
		}
	}

	private static final RSTile SINCLAIR_TILE = new RSTile(2705, 3576, 0);

	private static void handleSpecialCases(){
		RSTile myPos = Player.getPosition();
		if(myPos.distanceTo(SINCLAIR_TILE) < 5 && Player.getRSPlayer().isInCombat() &&
			NPCs.find(Filters.NPCs.nameEquals("Wolf").and(n -> n.isInteractingWithMe() && n.getPosition().distanceTo(myPos) <= 2)).length > 0){
			if(myPos.getY() >= 3577){
				Walking.walkTo(SINCLAIR_TILE.translate(-1, 0));
			} else {
				Walking.walkTo(SINCLAIR_TILE.translate(0, 1));
			}
		}
	}
}
