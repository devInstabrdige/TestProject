import systems.danger.kotlin.*

danger(args) {
    onGitHub {
        val isTrivial = pullRequest.title.contains("#trivial", ignoreCase = true)

        if (!isTrivial) {
            warn("Please ensure this PR has been reviewed.")
        }
    }
}
