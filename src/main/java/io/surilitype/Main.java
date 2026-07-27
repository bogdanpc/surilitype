package io.surilitype;

import io.surilitype.typing.Config;
import io.surilitype.view.App;

class Main {

    static void main() throws Exception {
        new App(Config.load()).run();
    }
}
