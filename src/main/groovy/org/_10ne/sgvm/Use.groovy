package org._10ne.sgvm
/**
 * @author Noam Y. Tenne.
 */
class Use {

    private static Set<String> KNOWN_CANDIDATES = ['gaiden', 'gradle', 'grails', 'griffon', 'groovy', 'groovyserv',
            'lazybones', 'springboot', 'vertx'] as Set

    def methodMissing(String name, def args) {
        if (!KNOWN_CANDIDATES.contains(name)) {
            throw new IllegalArgumentException("$name is not a valid candidate.");
        }

        def candidate = new Candidate(name: name, options: new Options(args))
        candidate.get()
    }
}
