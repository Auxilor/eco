package com.willfp.eco.internal;

import com.willfp.eco.core.EcoPlugin;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class Plugins {
    public static final Map<String, EcoPlugin> LOADED_ECO_PLUGINS = new HashMap<>();
}
