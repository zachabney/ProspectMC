package net.citizensnpcs.npc;

import java.util.Iterator;

import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.NPCCreateEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCDataStore;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.npc.ai.NPCHolder;
import net.citizensnpcs.util.ByIdArray;
import net.citizensnpcs.util.NMS;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.google.common.base.Preconditions;

public class CitizensNPCRegistry implements NPCRegistry {
    private final ByIdArray<NPC> npcs = new ByIdArray<NPC>();
    private final NPCDataStore saves;

    public CitizensNPCRegistry(NPCDataStore store) {
        saves = store;
    }

    @Override
    public NPC createNPC(int id, String name) {
        Preconditions.checkNotNull(name, "name cannot be null");
        CitizensNPC npc = new CitizensNPC(id, name, this);
        if (npc == null)
            throw new IllegalStateException("Could not create NPC.");
        npcs.put(npc.getId(), npc);
        Bukkit.getPluginManager().callEvent(new NPCCreateEvent(npc));
        return npc;
    }

    @Override
    public NPC createNPC(String name) {
        return createNPC(generateUniqueId(), name);
    }

    @Override
    public void deregister(NPC npc) {
        npcs.remove(npc.getId());
        if (saves != null) {
            saves.clearData(npc);
        }
        npc.despawn(DespawnReason.REMOVAL);
    }

    @Override
    public void deregisterAll() {
        Iterator<NPC> itr = iterator();
        while (itr.hasNext()) {
            NPC npc = itr.next();
            itr.remove();
            npc.despawn(DespawnReason.REMOVAL);
            for (Trait t : npc.getTraits()) {
                t.onRemove();
            }
            if (saves != null) {
                saves.clearData(npc);
            }
        }
    }

    private int generateUniqueId() {
        return saves.createUniqueNPCId(this);
    }

    @Override
    public NPC getById(int id) {
        if (id < 0)
            throw new IllegalArgumentException("invalid id");
        return npcs.get(id);
    }

    @Override
    public NPC getNPC(Entity entity) {
        if (entity == null)
            return null;
        if (entity instanceof NPCHolder)
            return ((NPCHolder) entity).getNPC();
        if (!(entity instanceof LivingEntity))
            return null;
        Object handle = NMS.getHandle((LivingEntity) entity);
        return handle instanceof NPCHolder ? ((NPCHolder) handle).getNPC() : null;
    }

    @Override
    public boolean isNPC(Entity entity) {
        return getNPC(entity) != null;
    }

    @Override
    public Iterator<NPC> iterator() {
        return npcs.iterator();
    }
}