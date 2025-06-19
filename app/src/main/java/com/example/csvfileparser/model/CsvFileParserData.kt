package com.example.csvfileparser.model

import kotlinx.serialization.Serializable

// Data class for the Header line
@Serializable
data class HeaderData(
    val recordType: String, // Should be "H"
    val serverID: String
)

// Data class for a Record line
@Serializable
data class RecordData(
    val recordType: String, // Should be "R"
    val imei1: String,
    val imei2: String,
    val serialNo: String,
    val deviceName: String
)


// Data class for the Trailer line
@Serializable
data class TrailerData(
    val recordType: String, // Should be "T"
    val count: Int
)

// Main data class to hold the parsed report
@Serializable
data class DeviceReport(
    val header: HeaderData,
    val deviceDetails: List<RecordData>,
    val trailer: TrailerData
)
