module dorkbox.jna {
    exports dorkbox.jna;
    exports dorkbox.jna.rendering;
    exports dorkbox.jna.linux;
    exports dorkbox.jna.linux.structs;
    exports dorkbox.jna.macos;
    exports dorkbox.jna.macos.cocoa;
    exports dorkbox.jna.macos.foundation;
    exports dorkbox.jna.windows;
    exports dorkbox.jna.windows.structs;

    requires transitive dorkbox.updates;
    requires transitive dorkbox.os;

    requires transitive kotlin.stdlib;

    requires static com.sun.jna;
    requires static com.sun.jna.platform;

    requires static org.slf4j;
}
