package kmm.jacky.utilitylibrary.enums

enum class Alignment {
    Start,
    Center,
    End,
    Undefined
}

fun Alignment.update(alignment: Alignment): Alignment = when (this) {
    Alignment.Undefined -> alignment
    else -> this
}
