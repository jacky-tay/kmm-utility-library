package kmm.jacky.utilitylibrary.models.column

import kmm.jacky.utilitylibrary.enums.Alignment
import kmm.jacky.utilitylibrary.enums.CellSize
import kmm.jacky.utilitylibrary.models.wrapper.DividerWrapper

class Cell(
    input: Any,
    var span: Int = 1,
) : Column {
    var index = 0

    var content: String = when (input) {
        is String -> input
        is DividerWrapper -> "" // TODO
        else -> input.toString()
    }
    override var definition: Column.Definition = Column.Definition()

    constructor(
        input: Any,
        alignment: Alignment = Alignment.Start,
        span: Int = 1,
        size: CellSize = CellSize.EquallySpacing()
    ) : this(input, span) {
        definition = Column.Definition(alignment, size)
    }

    fun setIndex(index: Int): Cell = this.apply {
        this.index = index
    }

    fun span(span: Int): Cell = this.apply {
        this.span = span
    }

    fun align(alignment: Alignment): Cell = this.apply {
        definition.alignment = alignment
    }
}
