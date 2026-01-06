package com.example.projektaplikacija.export

import android.content.Context
import java.io.File

object TspExporter {

    fun exportAtsp(
        context: Context,
        fileName: String,
        matrix: Array<IntArray>
    ): File {

        val dir = context.getExternalFilesDir(null)
            ?: throw IllegalStateException("External storage not available")

        val file = File(dir, fileName)

        file.printWriter().use { out ->
            out.println("NAME: $fileName")
            out.println("TYPE: ATSP")
            out.println("DIMENSION: ${matrix.size}")
            out.println("EDGE_WEIGHT_TYPE: EXPLICIT")
            out.println("EDGE_WEIGHT_FORMAT: FULL_MATRIX")
            out.println("EDGE_WEIGHT_SECTION")

            matrix.forEach { row ->
                out.println(row.joinToString(" "))
            }

            out.println("EOF")
        }

        return file
    }
}
