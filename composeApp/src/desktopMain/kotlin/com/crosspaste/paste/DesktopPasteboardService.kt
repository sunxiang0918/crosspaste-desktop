package com.crosspaste.paste

import com.crosspaste.app.DesktopAppWindowManager
import com.crosspaste.config.ConfigManager
import com.crosspaste.platform.getPlatform
import com.crosspaste.realm.paste.PasteRealm

fun getDesktopPasteboardService(
    appWindowManager: DesktopAppWindowManager,
    pasteRealm: PasteRealm,
    configManager: ConfigManager,
    currentPaste: CurrentPaste,
    pasteConsumer: TransferableConsumer,
    pasteProducer: TransferableProducer,
): AbstractPasteboardService {
    val currentPlatform = getPlatform()
    return if (currentPlatform.isMacos()) {
        MacosPasteboardService(appWindowManager, pasteRealm, configManager, currentPaste, pasteConsumer, pasteProducer)
    } else if (currentPlatform.isWindows()) {
        WindowsPasteboardService(appWindowManager, pasteRealm, configManager, currentPaste, pasteConsumer, pasteProducer)
    } else if (currentPlatform.isLinux()) {
        LinuxPasteboardService(appWindowManager, pasteRealm, configManager, currentPaste, pasteConsumer, pasteProducer)
    } else {
        throw Exception("Unsupported platform: ${currentPlatform.name}")
    }
}
