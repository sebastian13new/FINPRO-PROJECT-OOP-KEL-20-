package com.emberkeep.emberkeep.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InventoryTest {

    @Test
    void tidakBisaMemakaiKunciKalauKosong() {
        Inventory inventory = new Inventory();
        assertFalse(inventory.useKey());
    }

    @Test
    void kunciBisaDitambahDanDipakai() {
        Inventory inventory = new Inventory();
        inventory.addKey();

        assertTrue(inventory.useKey());
        assertEquals(0, inventory.getKeys());
    }

    @Test
    void pedangDidapatSetelahGiveSword() {
        Inventory inventory = new Inventory();
        assertFalse(inventory.hasSword());

        inventory.giveSword();

        assertTrue(inventory.hasSword());
    }
}
