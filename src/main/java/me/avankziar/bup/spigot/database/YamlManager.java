package main.java.me.avankziar.bup.spigot.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import main.java.me.avankziar.bup.spigot.database.Language.ISO639_2B;
import main.java.me.avankziar.bup.spigot.permission.BoniMali;

public class YamlManager
{
	private ISO639_2B languageType = ISO639_2B.GER;
	//The default language of your plugin. Mine is german.
	private ISO639_2B defaultLanguageType = ISO639_2B.GER;
	
	//Per Flatfile a linkedhashmap.
	private static LinkedHashMap<String, Language> configSpigotKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, Language> commandsKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, Language> languageKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, Language> bmlanguageKeys = new LinkedHashMap<>();
	/*
	 * Here are mutiplefiles in one "double" map. The first String key is the filename
	 * So all filename muss be predefine. For example in the config.
	 */
	private static LinkedHashMap<String, LinkedHashMap<String, Language>> guisKeys = new LinkedHashMap<>();
	
	public YamlManager()
	{
		initConfig();
		initCommands();
		initBonusMalusLanguage();
		initLanguage();
	}
	
	public ISO639_2B getLanguageType()
	{
		return languageType;
	}

	public void setLanguageType(ISO639_2B languageType)
	{
		this.languageType = languageType;
	}
	
	public ISO639_2B getDefaultLanguageType()
	{
		return defaultLanguageType;
	}
	
	public LinkedHashMap<String, Language> getConfigSpigotKey()
	{
		return configSpigotKeys;
	}
	
	public LinkedHashMap<String, Language> getCommandsKey()
	{
		return commandsKeys;
	}
	
	public LinkedHashMap<String, Language> getLanguageKey()
	{
		return languageKeys;
	}
	
	public LinkedHashMap<String, Language> getBonusMalusLanguageKey()
	{
		return bmlanguageKeys;
	}
	
	public LinkedHashMap<String, LinkedHashMap<String, Language>> getGUIKey()
	{
		return guisKeys;
	}
	
	/*
	 * The main methode to set all paths in the yamls.
	 */
	public void setFileInput(YamlConfiguration yml, LinkedHashMap<String, Language> keyMap, String key, ISO639_2B languageType)
	{
		if(!keyMap.containsKey(key))
		{
			return;
		}
		if(yml.get(key) != null)
		{
			return;
		}
		if(keyMap.get(key).languageValues.get(languageType).length == 1)
		{
			if(keyMap.get(key).languageValues.get(languageType)[0] instanceof String)
			{
				yml.set(key, ((String) keyMap.get(key).languageValues.get(languageType)[0]).replace("\r\n", ""));
			} else
			{
				yml.set(key, keyMap.get(key).languageValues.get(languageType)[0]);
			}
		} else
		{
			List<Object> list = Arrays.asList(keyMap.get(key).languageValues.get(languageType));
			ArrayList<String> stringList = new ArrayList<>();
			if(list instanceof List<?>)
			{
				for(Object o : list)
				{
					if(o instanceof String)
					{
						stringList.add(((String) o).replace("\r\n", ""));
					} else
					{
						stringList.add(o.toString().replace("\r\n", ""));
					}
				}
			}
			yml.set(key, (List<String>) stringList);
		}
	}
	
	public void initConfig() //INFO:Config
	{
		configSpigotKeys.put("useIFHAdministration"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				true}));
		configSpigotKeys.put("IFHAdministrationPath"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"bup"}));
		
		configSpigotKeys.put("ServerName"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"hub"}));
		
		configSpigotKeys.put("EnableCommands.Base"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				true}));
		
		configSpigotKeys.put("EnableMechanic.BonusMalus"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				true}));
		configSpigotKeys.put("EnableMechanic.VanillaThrowExpBottle"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				true}));
		configSpigotKeys.put("ExpBottle.ExpIntoBottle"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				20}));
		configSpigotKeys.put("ExpBottle.ExpFromBottle"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				20}));
		configSpigotKeys.put("ExpBottle.BottleTerm"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"Bottle",
				"bottle",
				"B",
				"b",
				"Flasche",
				"flasche",
				"F",
				"f"}));
		configSpigotKeys.put("ExpBottle.LevelTerm"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"Level",
				"level",
				"L",
				"l"}));
	}
	
	//INFO:Commands
	public void initCommands()
	{		
		commandsInput("bottleup", "bottleup", "bottleup.cmd.bottleup", 
				"/bottleup [pagenumber]", "/bottleup ", false,
				"&c/bottleup &f| Infoseite für alle Befehle.",
				"&c/bottleup &f| Info page for all commands.",
				"&bBefehlsrecht für &f/bottleup",
				"&bCommandright for &f/bottleup",
				"&eBasisbefehl für das BottleUp Plugin.",
				"&eGroundcommand for the BottleUp Plugin.");
		String path = "bottle";
		commandsInput("bottle", "bottle", "bottle.cmd.bottle", 
				"/bottle ", "/bottle ", false,
				"&c/bottle &f| Infoseite für alle Befehle.",
				"&c/bottle &f| Info page for all commands.",
				"&bBefehlsrecht für &f/bottle",
				"&bCommandright for &f/bottle",
				"&eInfobefehl für Expflaschen.",
				"&eInfocommand for expbottle.");
		String basePermission = "bottle.cmd.";
		argumentInput(path+"_calculate", "openuntillevel", basePermission,
				"/bottle openuntillevel [level]", "/bottle openuntillevel ", false,
				"&c/bottle openuntillevel [Level] &f| Öffnet alle oder eine bestimmte Anzahl, bis zu dem angegeben Level, an Erfahrungsflaschen um deren Exp aufzunehmen.",
				"&c/bottle openuntillevel [level] &f| Open all or a certain number, up to the specified level, of experience bottles to absorb their exp.",
				"&bBefehlsrecht für &f/bottle openuntillevel [Level]",
				"&bCommandright for &f/bottle openuntillevel [level]",
				"&eÖffnet alle oder eine bestimmte Anzahl, bis zu dem angegeben Level, an Erfahrungsflaschen um deren Exp aufzunehmen.",
				"&eOpen all or a certain number, up to the specified level, of experience bottles to absorb their exp.");
		argumentInput(path+"_fill", "fill", basePermission,
				"/bottle fill [Number:Useage(bottle or level etc.)]", "/bottle fill ", false,
				"&c/bottle fill [Zahl:Benutzung(Flasche oder Level etc.)] &f| Füllt die gesamte Spielerexp in Glasflaschen oder ein gewisse Flaschenanzahl. Optional bis zu einem gewissen Level.",
				"&c/bottle fill [Number:Useage(bottle or level etc.)] &f| Fills the entire player exp in glass bottles or a certain number of bottles. Optional up to a certain level.",
				"&bBefehlsrecht für &f/bottle fill [Zahl:Benutzung(Flasche oder Level etc.)]",
				"&bCommandright for &f/bottle fill [Number:Useage(bottle or level etc.)] ",
				"&eFüllt die Spielerexp in Glasflaschen. Optional bis zu einem gewissen Level.",
				"&eFills the player exp in glass bottles. Optional up to a certain level.");
		argumentInput(path+"_use", "use", basePermission,
				"/bottle use [Number:Useage(bottle or level etc.)]", "/bottle open ", false,
				"&c/bottle use [Zahl:Benutzung(Flasche oder Level etc.)] &f| Öffnet und benutzt alle oder die angegebene Anzahl an Erfahrungsflaschen um deren Exp aufzunehmen. Optional bis zu einem gewissen Level.",
				"&c/bottle use [Number:Useage(bottle or level etc.)] &f| Opens and used all or the specified number of experience bottles to pick up their exp. Optional up to a certain level.",
				"&bBefehlsrecht für &f/bottle use [Zahl:Benutzung(Flasche oder Level etc.)]",
				"&bCommandright for &f/bottle usen [Number:Useage(bottle or level etc.)]",
				"&eÖffnet Erfahrungsflaschen, die der Spielerexp hinzugefügt wird. Optional bis zu einem gewissen Level.",
				"&eOpens experience bottles, which is added to the player's exp. Optional up to a certain level.");
		
	}
	
	private void commandsInput(String path, String name, String basePermission, 
			String suggestion, String commandString, boolean putUpCmdPermToBonusMalusSystem,
			String helpInfoGerman, String helpInfoEnglish,
			String dnGerman, String dnEnglish,
			String exGerman, String exEnglish)
	{
		commandsKeys.put(path+".Name"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				name}));
		commandsKeys.put(path+".Permission"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				basePermission}));
		commandsKeys.put(path+".Suggestion"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				suggestion}));
		commandsKeys.put(path+".PutUpCommandPermToBonusMalusSystem"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				putUpCmdPermToBonusMalusSystem}));
		commandsKeys.put(path+".CommandString"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				commandString}));
		commandsKeys.put(path+".HelpInfo"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				helpInfoGerman,
				helpInfoEnglish}));
		commandsKeys.put(path+".Displayname"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				dnGerman,
				dnEnglish}));
		commandsKeys.put(path+".Explanation"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				exGerman,
				exEnglish}));
	}
	
	private void argumentInput(String path, String argument, String basePermission, 
			String suggestion, String commandString, boolean putUpCmdPermToBonusMalusSystem,
			String helpInfoGerman, String helpInfoEnglish,
			String dnGerman, String dnEnglish,
			String exGerman, String exEnglish)
	{
		commandsKeys.put(path+".Argument"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				argument}));
		commandsKeys.put(path+".Permission"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				basePermission+"."+argument}));
		commandsKeys.put(path+".Suggestion"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				suggestion}));
		commandsKeys.put(path+".PutUpCommandPermToBonusMalusSystem"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				putUpCmdPermToBonusMalusSystem}));
		commandsKeys.put(path+".CommandString"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				commandString}));
		commandsKeys.put(path+".HelpInfo"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				helpInfoGerman,
				helpInfoEnglish}));
		commandsKeys.put(path+".Displayname"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				dnGerman,
				dnEnglish}));
		commandsKeys.put(path+".Explanation"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				exGerman,
				exEnglish}));
	}
	
	public void initLanguage() //INFO:Languages
	{
		languageKeys.put("InputIsWrong",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDeine Eingabe ist fehlerhaft! Klicke hier auf den Text, um weitere Infos zu bekommen!",
						"&cYour input is incorrect! Click here on the text to get more information!"}));
		languageKeys.put("NoPermission",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu hast dafür keine Rechte!",
						"&cYou dont not have the rights!"}));
		languageKeys.put("NoPlayerExist",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDer Spieler existiert nicht!",
						"&cThe player does not exist!"}));
		languageKeys.put("NoNumber",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDas Argument &f%value% &cmuss eine ganze Zahl sein.",
						"&cThe argument &f%value% &must be an integer."}));
		languageKeys.put("NoDouble",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDas Argument &f%value% &cmuss eine Gleitpunktzahl sein!",
						"&cThe argument &f%value% &must be a floating point number!"}));
		languageKeys.put("IsNegativ",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDas Argument &f%value% &cmuss eine positive Zahl sein!",
						"&cThe argument &f%value% &must be a positive number!"}));
		languageKeys.put("GeneralHover",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eKlick mich!",
						"&eClick me!"}));
		languageKeys.put("Headline", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e=====&7[&6BottleUp&7]&e=====",
						"&e=====&7[&6BottleUp&7]&e====="}));
		languageKeys.put("Next", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e&nnächste Seite &e==>",
						"&e&nnext page &e==>"}));
		languageKeys.put("Past", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e<== &nvorherige Seite",
						"&e<== &nprevious page"}));
		languageKeys.put("IsTrue", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&a✔",
						"&a✔"}));
		languageKeys.put("IsFalse", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&c✖",
						"&c✖"}));
		languageKeys.put("CmdBottle.BaseInfo", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e=====&7[&6BottleUp&7]&e=====",
						"&eDerzeit besitzt du &f%totalexp% &eExp.",
						"&eBei &f%expinbottle% &eExp die eine Flasche passen, kannst du &f%bottle% &eExpflaschen befüllen.",
						"&f%expfrombottle% &eExp bekommst du pro Flasche.",
						"&e=====&7[&6BottleUp&7]&e=====",
						"&eCurrently you own &f%totalexp% &eExp.",
						"&eWith &f%expinbottle% &eExp that fit a bottle, you can fill &f%bottle% &eExp bottles.",
						"&f%expfrombottle% &eExp you get per bottle."}));
		languageKeys.put("CmdBottle.NothingToCalculate", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDein Spielerlevel ist gleich dem angegeben Level. Da gibt es nichts zu berechnen.",
						"&cYour player level is equal to the specified level. There is nothing to calculate."}));
		languageKeys.put("CmdBottle.CalculateFill", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDeine aktuellen Exp sind &f%playerexp%&e. Um das angegebene Level &f%level% &ezu erreichen, müssen &f%bottleamount% &eFlaschen jeweils &f%expbottle% &eExp gefüllt werden.",
						"&eYour current exp are &f%playerexp%&e. To reach the specified &f%level% &e, &f%bottleamount% &ebottles must be filled each &f%expbottle% &eExp."}));
		languageKeys.put("CmdBottle.CalculateUse", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDeine aktuellen Exp sind &f%playerexp%&e. Um das angegebene Level &f%level% &ezu erreichen, müssen &f%bottleamount% &eFlaschen jeweils &f%expbottle% &eExp benutzt werden.",
						"&eYour current exp are &f%playerexp%&e. To reach the specified &f%level% &e, &f%bottleamount% &ebottles must be &f%expbottle% &eexp used respectively."}));
		languageKeys.put("CmdBottle.WrongInput", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cFalsche Eingabe! Entweder keine Eingabe, nur Zahlen oder nach den Zahlen ein : sowie %bottle%, %level%!",
						"&eWrong input! Either no input, only numbers or after the numbers a : as well as %bottle%, %level%!"}));
		languageKeys.put("CmdBottle.Fill", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDu hast &f%bottleamount% &eExpflaschen befüllt. Dafür hast du &f%removeexp% &eExp aufgewendet. Dir verbleiben noch &f%endexp% &eExp.",
						"&eYou have filled &f%bottleamount% &eExp bottles. You have spent &f%removeexp% &eExp for this. You still have &f%endexp% &eExp left."}));
		languageKeys.put("CmdBottle.CannotFillMoreBottleBecauseLevelIsAlreadyReached", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu kannst keine Flaschen zu abfüllen, denn du hast das gewünschte Level bereits erreicht!",
						"&cYou can not fill bottles, because you have already reached the desired level!"}));
		languageKeys.put("CmdBottle.FillUntillevel", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDu hast &f%bottleamount% &eExpflaschen befüllt bis zum Level &f%level%&e. Dafür hast du &f%removeexp% &eExp aufgewendet.",
						"&eYou have filled &f%bottleamount% &eExp bottles up to the &f%level%&e level. You have spent &f%removeexp% &eExp for this."}));
		languageKeys.put("CmdBottle.Use", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDu hast &f%bottleamount% &eExpflaschen geleert. Dafür hast du &f%addexp% &eExp bekommen. Du hast nun &f%endexp% &eExp.",
						"&eYou have emptied &f%bottleamount% &eExp bottles. For that you got &f%addexp% &eExp. You now have &f%endexp% &eExp."}));
		languageKeys.put("CmdBottle.CannotUseMoreBottleBecauseLevelIsAlreadyReached", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu brauchst keine Flaschen zu öffnen, denn du hast das gewünschte Level bereits erreicht!",
						"&cYou dont need to open any bottles, because you have already reached the desired level!"}));
		languageKeys.put("CmdBottle.UseUntilLevel", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDu hast &f%bottleamount% &eExpflaschen geöffnet bis zum Level &f%level%&e. Dafür wurden &f%addexp% &eExp aufgewendet.",
						"&eYou have opened &f%bottleamount% &eExp bottles up to the &f%level%&e level. For this you spent &f%addexp% &eExp."}));
		
		
	}
	
	public void initBonusMalusLanguage() //INFO:BonusMalusLanguages
	{
		bmlanguageKeys.put(BoniMali.EXP_IN_BOTTLE.toString()+".Displayname",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eMenge an Exp die in eine Flasche passen",
						"&eAmount of Exp that fit in one bottle"}));
		bmlanguageKeys.put(BoniMali.EXP_IN_BOTTLE.toString()+".Explanation",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDefiniert, wieviel Exp in eine",
						"&eFlasche gefüllt werden kann.",
						"&eDefines how much Exp can",
						"&ebe filled into a bottle."}));
		bmlanguageKeys.put(BoniMali.EXP_OUT_BOTTLE.toString()+".Displayname",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eMenge an Exp die aus einer Flasche kommen",
						"&eAmount of exp coming out of one bottle"}));
		bmlanguageKeys.put(BoniMali.EXP_OUT_BOTTLE.toString()+".Explanation",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDefiniert, wieviel Exp aus einer",
						"&eFlasche kommt.",
						"&eDefines how much exp comes",
						"&eout of a bottle."}));
	}
}