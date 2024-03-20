module xyz.niceduckdev {
    requires javafx.controls;
    requires kryonet;

    opens xyz.niceduckdev.cards;
    exports xyz.niceduckdev.cards;

    opens xyz.niceduckdev.network;
    exports xyz.niceduckdev.network;

    opens xyz.niceduckdev.ui;
    exports xyz.niceduckdev.ui;

    opens xyz.niceduckdev.utilities;
    exports xyz.niceduckdev.utilities;

    opens xyz.niceduckdev;
    exports xyz.niceduckdev;
}