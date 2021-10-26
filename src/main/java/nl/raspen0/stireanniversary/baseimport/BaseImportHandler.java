package nl.raspen0.stireanniversary.baseimport;

import nl.raspen0.stireanniversary.Base;
import nl.raspen0.stireanniversary.StireAnniversary;

import java.util.Set;

public abstract class BaseImportHandler {

    protected final StireAnniversary plugin;

    BaseImportHandler(StireAnniversary plugin){
        this.plugin = plugin;
    }

    public abstract Set<Base> getBases(String worldName, int anniversaryWorldID);
}
