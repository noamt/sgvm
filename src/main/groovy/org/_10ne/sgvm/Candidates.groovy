package org._10ne.sgvm

import java.nio.file.Path

/**
 * @author Noam Y. Tenne.
 */
class Candidates {

    static Set<String> KNOWN_CANDIDATES = ['gaiden', 'gradle', 'grails', 'griffon', 'groovy', 'groovyserv', 'lazybones',
                                           'springboot', 'vertx'] as Set

    Path get(Context context, String name) {
        validateCandidate(name)
        context.candidateDir(name)
    }

    private static void validateCandidate(String name) {
        if (!KNOWN_CANDIDATES.contains(name)) {
            throw new IllegalArgumentException("$name is not a valid candidate.");
        }
    }
}