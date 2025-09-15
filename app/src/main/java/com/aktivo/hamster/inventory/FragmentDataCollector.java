package com.aktivo.hamster.inventory;

public interface FragmentDataCollector {
    /**
     * Metode ini akan dipanggil oleh Activity untuk memaksa Fragment
     * mengumpulkan data terbarunya dan memperbarui ViewModel.
     */
    void collectDataForSave();
}