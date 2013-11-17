package net.citizensnpcs.api.npc;

import net.citizensnpcs.api.util.DataKey;
import net.citizensnpcs.api.util.Messaging;
import net.citizensnpcs.api.util.Storage;

import org.bukkit.entity.EntityType;

public class SimpleNPCDataStore implements NPCDataStore {
    private final Storage root;

    private SimpleNPCDataStore(Storage saves) {
        root = saves;
    }

    @Override
    public void clearData(NPC npc) {
        root.getKey("npc").removeKey(Integer.toString(npc.getId()));
    }

    @Override
    public int createUniqueNPCId(NPCRegistry registry) {
        DataKey key = root.getKey("");
        int newId = key.getInt("last-created-npc-id", -1);
        if (newId == -1 || registry.getById(newId + 1) != null) {
            int maxId = Integer.MIN_VALUE;
            for (NPC npc : registry) {
                if (npc.getId() > maxId) {
                    maxId = npc.getId();
                }
            }
            newId = maxId == Integer.MIN_VALUE ? 0 : maxId + 1;
        } else {
            newId++;
        }
        key.setInt("last-created-npc-id", newId);
        return newId;
    }

    @Override
    public void loadInto(NPCRegistry registry) {
        for (DataKey key : root.getKey("npc").getIntegerSubKeys()) {
            int id = Integer.parseInt(key.name());
            if (!key.keyExists("name")) {
                Messaging.logTr(LOAD_NAME_NOT_FOUND, id);
                continue;
            }
            String unparsedEntityType = key.getString("traits.type", "PLAYER");
            NPC npc = registry.createNPC(id, key.getString("name"));
            npc.load(key);
        }
    }

    @Override
    public void saveToDisk() {
        new Thread() {
            @Override
            public void run() {
                root.save();
            }
        }.start();
    }

    @Override
    public void saveToDiskImmediate() {
        root.save();
    }

    @Override
    public void store(NPC npc) {
        npc.save(root.getKey("npc." + npc.getId()));
    }

    @Override
    public void storeAll(NPCRegistry registry) {
        for (NPC npc : registry)
            store(npc);
    }

    public static NPCDataStore create(Storage storage) {
        return new SimpleNPCDataStore(storage);
    }

    private static final String LOAD_NAME_NOT_FOUND = "citizens.notifications.npc-name-not-found";
}
