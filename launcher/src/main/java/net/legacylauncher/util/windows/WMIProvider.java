package net.legacylauncher.util.windows;

import net.legacylauncher.util.async.AsyncThread;
import net.legacylauncher.util.shared.WMI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public final class WMIProvider {
    private static final Logger LOGGER = LogManager.getLogger(WMIProvider.class);

    private static final Future<List<String>> AV_LIST = AsyncThread.future(() -> {
        try {
            return WMI.getAVSoftwareList();
        } catch (NoClassDefFoundError noClassDefFoundError) {
            // ignore
            return Collections.emptyList();
        }
    });

    public static List<String> getAvSoftwareList() {
        try {
            return AV_LIST.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.warn("Could not fetch AV list", e);
            return Collections.emptyList();
        }
    }

    private WMIProvider() {
    }
}
