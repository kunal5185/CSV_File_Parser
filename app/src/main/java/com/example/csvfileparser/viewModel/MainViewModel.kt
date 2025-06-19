package com.example.csvfileparser.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.csvfileparser.model.CsvParser
import com.example.csvfileparser.model.DeviceReport
import kotlinx.serialization.json.Json
import java.io.InputStream

/**
 * MainViewModel handles the business logic and provides data to the UI.
 */
class MainViewModel(private val csvParser: CsvParser) : ViewModel() {

    // LiveData to expose the parsed JSON string to the UI
    val jsonOutput = MutableLiveData<String>()

    // LiveData to expose error messages to the UI
    val errorMessage = MutableLiveData<String?>()

    /**
     * Parses a CSV file from the application's assets folder and updates LiveData.
     * @param assets The AssetManager to open the asset file.
     * @param fileName The name of the CSV file in the assets folder.
     */
    fun parseCsvFromAssets(assets: android.content.res.AssetManager, fileName: String) {
        try {
            val inputStream: InputStream = assets.open(fileName)
            val deviceReport = csvParser.parse(inputStream)
            inputStream.close()

            val json = Json { prettyPrint = true }
            val jsonString =
                json.encodeToString(serializer = DeviceReport.serializer(), value = deviceReport)
            jsonOutput.postValue(jsonString) // Update LiveData
            errorMessage.postValue(null) // Clear any previous errors
        } catch (e: Exception) {
            errorMessage.postValue("Error parsing asset file '$fileName': ${e.message}")
            jsonOutput.postValue("Error: ${e.message}") // Display error in main output as well
        }
    }
}

/**
 * Custom ViewModelFactory to provide dependencies to the ViewModel.
 */
class MainViewModelFactory(private val csvParser: CsvParser) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(csvParser) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
