package org._10ne.sgvm

import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Noam Y. Tenne.
 */
class Gvm {

    static Path GVM_HOME_DIR

    static {
        GVM_HOME_DIR = Paths.get(System.properties['user.home'], '.gvm')
    }

    public static Use use() {
        new Use()
    }
}
