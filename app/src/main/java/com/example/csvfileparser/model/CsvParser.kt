package com.example.csvfileparser.model

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/**
 * CsvParser class responsible for parsing the custom CSV-like file format.
 */
class CsvParser {
    /**
     * Parses the input stream containing the CSV-like data.
     * @param inputStream The InputStream to read the CSV data from.
     * @return A DeviceReport object containing the parsed header, records, and trailer.
     * @throws IllegalArgumentException if the file format is invalid or data is missing.
     */
    fun parse(inputStream: InputStream): DeviceReport {
        val reader = BufferedReader(InputStreamReader(inputStream))
        var header: HeaderData? = null
        val records = mutableListOf<RecordData>()
        var trailer: TrailerData? = null
        var recordCount = 0

        reader.useLines { lines ->
            lines.forEachIndexed { index, line ->
                val trimmedLine = line.trim()
                if (trimmedLine.isEmpty()) return@forEachIndexed

                when (trimmedLine.firstOrNull()) {
                    'H' -> {
                        if (header != null) {
                            throw IllegalArgumentException("Multiple header lines found at line ${index + 1}.")
                        }
                        header = parseHeaderLine(trimmedLine, index + 1)
                    }

                    'R' -> {
                        val record = parseRecordLine(trimmedLine, index + 1)
                        records.add(record)
                        recordCount++
                    }

                    'T' -> {
                        if (trailer != null) {
                            throw IllegalArgumentException("Multiple trailer lines found at line ${index + 1}.")
                        }
                        trailer = parseTrailerLine(trimmedLine, index + 1)
                    }

                    else -> {
                        throw IllegalArgumentException("Unknown record type at line ${index + 1}: '${trimmedLine.firstOrNull()}'. Line content: $trimmedLine")
                    }
                }
            }
        }

        if (header == null) {
            throw IllegalArgumentException("Missing header line in the CSV file.")
        }
        if (trailer == null) {
            throw IllegalArgumentException("Missing trailer line in the CSV file.")
        }

        if (trailer.count != recordCount) {
            throw IllegalArgumentException("Record count mismatch. Expected ${trailer.count}, but found $recordCount records.")
        }

        return DeviceReport(header, records, trailer)
    }

    private fun parseHeaderLine(line: String, lineNumber: Int): HeaderData {
        val parts = line.split('|')
        if (parts.size != 2 || parts[0] != "H") {
            throw IllegalArgumentException("Malformed header line at line $lineNumber: $line. Expected 'H|ServerID'.")
        }
        return HeaderData(recordType = parts[0], serverID = parts[1])
    }

    private fun parseRecordLine(line: String, lineNumber: Int): RecordData {
        val parts = line.split('|')
        if (parts.size != 5 || parts[0] != "R") {
            throw IllegalArgumentException("Malformed record line at line $lineNumber: $line. Expected 'R|IMEI1|IMEI2|SerialNo|DeviceName'.")
        }
        return RecordData(
            recordType = parts[0],
            imei1 = parts[1],
            imei2 = parts[2],
            serialNo = parts[3],
            deviceName = parts[4]
        )
    }

    private fun parseTrailerLine(line: String, lineNumber: Int): TrailerData {
        val parts = line.split('|')
        if (parts.size != 2 || parts[0] != "T") {
            throw IllegalArgumentException("Malformed trailer line at line $lineNumber: $line. Expected 'T|Count'.")
        }
        val count = parts[1].toIntOrNull()
            ?: throw IllegalArgumentException("Invalid count in trailer line at line $lineNumber: ${parts[1]}. Expected an integer.")
        return TrailerData(recordType = parts[0], count = count)
    }
}
