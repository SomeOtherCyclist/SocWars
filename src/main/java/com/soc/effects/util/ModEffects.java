package com.soc.effects.util;

import com.soc.effects.AntiGravity;
import com.soc.effects.Armour;
import com.soc.effects.Flight;

public class ModEffects {
    public static void initialise() {
        Armour.initialise();
        Flight.initialise();
        AntiGravity.initialise();
    }
}
