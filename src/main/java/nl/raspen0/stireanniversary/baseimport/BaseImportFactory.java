package nl.raspen0.stireanniversary.baseimport;

import nl.raspen0.stireanniversary.BaseType;
import nl.raspen0.stireanniversary.StireAnniversary;

public class BaseImportFactory {

    public static BaseImportHandler getImportHandler(BaseType type, StireAnniversary plugin){
        switch (type){
            case FACTION -> {
                return new FactionsHandler(plugin);
            }
            case TOWN -> {
                return new TownyHandler(plugin);
            }
            case BED -> {
                return new BedHandler(plugin);
            }
            case ESSENTIALS -> {
                return new EssentialsHandler(plugin);
            }
            default -> {
                return null;
            }
        }
    }
}
