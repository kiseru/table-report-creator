package ru.kiseru.tablereportcreator

import java.io.OutputStream
import java.io.Writer

fun createReport(outputStream: OutputStream, func: ReportCreator.() -> Unit) {
    val writer = outputStream.bufferedWriter()
    createReport(writer, func)
    writer.flush()
}

fun createReport(writer: Writer, func: ReportCreator.() -> Unit) {
    val reportCreator = ReportCreator(writer)
    reportCreator.func()
}

class ReportCreator(private val writer: Writer) {

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
        writer.write(" $title\n")
    }

    fun createTable(vararg columnSizes: Int, func: ReportTableCreator.() -> Unit) {
        isTablesCreated = true

        if (isTitleSet) {
            writer.write("\n")
        }

        val reportTableCreator = ReportTableCreator(writer, *columnSizes)
        reportTableCreator.func()
        reportTableCreator.writeDownLine()
    }
}

class ReportTableCreator(
    private val writer: Writer,
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
        writer.write(" $title\n")
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

        writer.write("│")
        columnSizes.zip(data)
            .forEach { (columnSize, columnValue) ->
                if (columnSize - 2 < columnValue.length) {
                    writer.write(" ${columnValue.substring(0, columnSize - 2)}")
                } else {
                    writer.write(" ${columnValue.padEnd(columnSize - 2)}")
                }
                writer.write(" │")
            }
        writer.write("\n")
    }

    private fun writeHeaderRow(vararg data: String) {
        if (data.size != columnsCount) {
            throw IllegalArgumentException("The table have $columnsCount columns.")
        }

        writer.write("│")
        columnSizes.zip(data)
            .forEach { (columnSize, columnValue) ->
                if (columnSize - 2 < columnValue.length) {
                    writer.write(" ${columnValue.substring(0, columnSize - 2)} │")
                } else {
                    val gapSize = columnSize - columnValue.length
                    val gap = generateSequence { " " }
                        .take(gapSize / 2)
                        .joinToString(separator = "")
                    writer.write("$gap$columnValue$gap")
                    if (gapSize % 2 != 0) {
                        writer.write(" ")
                    }
                    writer.write("│")
                }
            }
        writer.write("\n")
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
        writer.write(startSymbol)
        columnSizes.forEachIndexed { index, size ->
            if (index > 0) {
                writer.write(middleSymbol)
            }
            val line = generateSequence { "─" }
                .take(size)
                .joinToString(separator = "")
            writer.write(line)
        }

        writer.write("$endSymbol\n")
    }
}





