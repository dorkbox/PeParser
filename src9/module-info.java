module dorkbox.pe {
    exports dorkbox.peParser;
    exports dorkbox.peParser.headers;
    exports dorkbox.peParser.misc;
    exports dorkbox.peParser.types;

    requires transitive dorkbox.collections;
    requires transitive dorkbox.byteUtils;
    requires transitive dorkbox.hexUtils;
    requires transitive dorkbox.updates;
    requires transitive dorkbox.utilities;

    requires transitive kotlin.stdlib;
}
