package com.example.hamster.utils;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeSetup {

    public static void applyTheme(int themeMode) {
        AppCompatDelegate.setDefaultNightMode(themeMode);
    }
}