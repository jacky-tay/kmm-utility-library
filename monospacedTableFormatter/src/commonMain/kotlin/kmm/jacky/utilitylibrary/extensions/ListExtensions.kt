package kmm.jacky.utilitylibrary.extensions

import kmm.jacky.utilitylibrary.enums.CellSize
import kmm.jacky.utilitylibrary.models.column.Cell
import kmm.jacky.utilitylibrary.models.column.Column
import kmm.jacky.utilitylibrary.models.row.BaseRow
import kmm.jacky.utilitylibrary.models.row.IRow
import kotlin.math.floor
import kotlin.math.max

inline fun <T> List<T>.firstIndexFrom(from: Int, predicate: (T) -> Boolean): Int {
    val start = max(0, from)
    for (i in start until size) {
        if (predicate(this[i]))
            return i
    }
    return -1
}

internal fun List<*>.spacingWidth(): Int = if (size > 4) 2 else 3

// spacing count should be size - 1 multiply by spacing width
internal fun List<*>.totalSpacingWidth(): Int = (size - 1) * spacingWidth()

internal fun List<Column.Definition>.isValid(width: Int): Boolean {
    var sum = totalSpacingWidth()
    for (definition in this) {
        val size = definition.size
        sum += when (size) {
            is CellSize.FixedWidth -> size.width
            is CellSize.Percentage -> ((width - totalSpacingWidth()) * size.factor).toInt()
            else -> 1 // all column should have minimum width of 1
        }
    }
    return sum <= width
}

internal fun List<Column.Reference>.updatePosition(): List<Column.Reference> {
    val spacer = spacingWidth()
    for (i in 1 until size) {
        this[i].start = this[i - 1].end + spacer
    }
    return this
}

internal fun List<Cell>.insertColumnDefinition(definitions: List<Column.Definition>) {
    if (definitions.size == size) {
        definitions.forEachIndexed { i, def ->
            this[i].index = i
            this[i].definition = def
        }
    } else if (fold(0) { sum, cell -> sum + cell.span } == definitions.size) {
        var i = 0
        for (cell in this) {
            cell.definition.alignment = cell.definition.alignment.update(definitions[i].alignment)
            if (cell.span == 1) {
                cell.definition.size = definitions[i].size
            }
            i += cell.span // i should always be in bound
        }
    } else if (this.size == 1) {
        this[0].index = 0
        this[0].span = definitions.size
    }
}

internal fun List<Column.Definition>.buildColumnReferencesFromRows(
    rows: List<BaseRow>,
    width: Int
): List<Column.Reference> {
    val availableWidth = width - totalSpacingWidth()
    var remainingWidth: Double = availableWidth.toDouble()
    val references = map { Column.Reference() }
    val equallySpacingSet = mutableMapOf<Int, Int>()

    for (i in indices) {
        val size = this[i].size
        when (size) {
            is CellSize.FixedWidth -> size.width
            is CellSize.Percentage -> floor(availableWidth * size.factor).toInt()
            is CellSize.ShrinkToFit -> rows.maxOf { it.getColumnWidthAt(i, size) }
            is CellSize.ExpandToFit -> equallySpacingSet.put(i, 1).run { null }
            is CellSize.EquallySpacing -> equallySpacingSet.put(i, size.weight).run { null }
            // set undefined to fill max
            is CellSize.Undefined -> equallySpacingSet.put(i, 1).run { null }
        }?.also { cellWidth ->
            references[i].len = cellWidth
            remainingWidth -= cellWidth
        }
    }
    val totalWeight = equallySpacingSet.values.sum()
    val weightWidth = floor(
        remainingWidth / max(1, totalWeight) // take max 1, in case totalWeight is zero
    ).toInt()

    equallySpacingSet.forEach {
        references[it.key].len = weightWidth * it.value
    }

    return references.updatePosition()
}

internal fun List<IRow>.buildColumnReferencesFromDefinitions(
    definitions: List<Column.Definition>,
    width: Int
): List<Column.Reference> {
    return definitions.buildColumnReferencesFromRows(filterIsInstance<BaseRow>(), width)
}
