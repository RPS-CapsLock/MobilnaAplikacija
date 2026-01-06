package com.example.projektaplikacija.pdf

import android.content.Context
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper

fun readPdfFromAssets(context: Context, fileName: String): String {
    context.assets.open(fileName).use { input ->
        PDDocument.load(input).use { doc ->
            return PDFTextStripper().getText(doc)
        }
    }
}

