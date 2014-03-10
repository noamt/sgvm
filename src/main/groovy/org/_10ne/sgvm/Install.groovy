package org._10ne.sgvm
/**
 * @author Noam Y. Tenne.
 */
class Install {

    Context context
    Candidates candidates = new Candidates()

    def methodMissing(String name, def args) {
        def options = new Options(args)
        candidates.install(context, name, options)
    }
}
