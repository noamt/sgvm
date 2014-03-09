package org._10ne.sgvm
/**
 * @author Noam Y. Tenne.
 */
class Use {

    Context context
    Candidates candidates = new Candidates()

    def methodMissing(String name, def args) {
        def options = new Options(args)
        candidates.use(context, name, options)
    }
}
