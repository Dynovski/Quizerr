package pl.dynovski.quizerr.helpers

import pl.dynovski.quizerr.firebaseObjects.TestResult

class ResultSection(var name: String, var items: Array<TestResult>) {

    fun shouldShow(): Boolean {
        return items.isNotEmpty()
    }
}