// File: app/src/main/java/com/example/hamster/inventory/FragmentDataCollector.java
package com.example.hamster.inventory;

public interface FragmentDataCollector {
    /**
     * Metode ini akan dipanggil oleh Activity untuk memaksa Fragment
     * mengumpulkan data terbarunya dan memperbarui ViewModel.
     */
    void collectDataForSave();
}