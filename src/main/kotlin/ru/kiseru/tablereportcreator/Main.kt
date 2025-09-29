package ru.kiseru.tablereportcreator

fun createReport(func: ReportCreator.() -> Unit): String {
    val reportCreator = ReportCreator()
    reportCreator.func()
    return reportCreator.createReport()
}

class ReportCreator {

    private val stringBuilder = StringBuilder()

    private var isTitleSet = false
    private var isTablesCreated = false

    fun setTitle(title: String) {
        if (isTitleSet) {
            throw IllegalStateException("Report title is already set")
        }

        if (isTablesCreated) {
            throw IllegalStateException("Cannot set report title after creating tables")
        }

        isTitleSet = true
        stringBuilder.appendLine(" $title")
    }

    fun createTable(vararg columnSizes: Int, func: ReportTableCreator.() -> Unit) {
        isTablesCreated = true

        if (isTitleSet) {
            stringBuilder.appendLine()
        }

        val reportTableCreator = ReportTableCreator(stringBuilder, *columnSizes)
        reportTableCreator.func()
        reportTableCreator.writeDownLine()
    }

    fun createReport(): String =
        stringBuilder.toString()
}

class ReportTableCreator(
    private val stringBuilder: StringBuilder,
    private vararg val columnSizes: Int,
) {

    private val columnsCount = columnSizes.size

    private var isTitleSet = false
    private var isHeaderSet = false
    private var isDataSet = false

    fun setTitle(title: String) {
        if (isTitleSet) {
            throw IllegalStateException("Table title is already set")
        }

        if (isHeaderSet) {
            throw IllegalStateException("Table headers is already set")
        }

        if (isDataSet) {
            throw IllegalStateException("Table data is already set")
        }

        isTitleSet = true
        stringBuilder.appendLine(" $title")
    }

    fun setHeaders(vararg headers: String) {
        if (isHeaderSet) {
            throw IllegalStateException("Table headers is already set")
        }

        if (isDataSet) {
            throw IllegalStateException("Table data is already set")
        }

        isHeaderSet = true
        writeUpLine()
        writeHeaderRow(*headers)
    }

    fun data(vararg data: List<String>) {
        if (isDataSet) {
            throw IllegalStateException("Table data is already set")
        }

        if (isHeaderSet) {
            writeMiddleLine()
        } else {
            writeUpLine()
        }

        isDataSet = true
        data.forEach(::writeRow)
    }

    private fun writeRow(data: List<String>) {
        if (data.size != columnsCount) {
            throw IllegalArgumentException("The table have $columnsCount columns.")
        }

        stringBuilder.append("│")
        columnSizes.zip(data)
            .forEach { (columnSize, columnValue) ->
                if (columnSize - 2 < columnValue.length) {
                    stringBuilder.append(" ${columnValue.substring(0, columnSize - 2)}")
                } else {
                    stringBuilder.append(" ${columnValue.padEnd(columnSize - 2)}")
                }
                stringBuilder.append(" │")
            }
        stringBuilder.appendLine()
    }

    private fun writeHeaderRow(vararg data: String) {
        if (data.size != columnsCount) {
            throw IllegalArgumentException("The table have $columnsCount columns.")
        }

        stringBuilder.append("│")
        columnSizes.zip(data)
            .forEach { (columnSize, columnValue) ->
                if (columnSize - 2 < columnValue.length) {
                    stringBuilder.append(" ${columnValue.substring(0, columnSize - 2)} │")
                } else {
                    val gapSize = columnSize - columnValue.length
                    val gap = generateSequence { " " }
                        .take(gapSize / 2)
                        .joinToString(separator = "")
                    stringBuilder.append("$gap$columnValue$gap")
                    if (gapSize % 2 != 0) {
                        stringBuilder.append(" ")
                    }
                    stringBuilder.append("│")
                }
            }
        stringBuilder.appendLine()
    }

    private fun writeUpLine() {
        writeLine("┌", "┬", "┐")
    }

    private fun writeMiddleLine() {
        writeLine("├", "┼", "┤")
    }

    fun writeDownLine() {
        if (isHeaderSet || isDataSet) {
            writeLine("└", "┴", "┘")
        }
    }

    private fun writeLine(startSymbol: String, middleSymbol: String, endSymbol: String) {
        stringBuilder.append(startSymbol)
        columnSizes.forEachIndexed { index, size ->
            if (index > 0) {
                stringBuilder.append(middleSymbol)
            }
            val line = generateSequence { "─" }
                .take(size)
                .joinToString(separator = "")
            stringBuilder.append(line)
        }
        stringBuilder.appendLine(endSymbol)
    }
}





