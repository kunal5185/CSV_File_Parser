package com.example.csvfileparser.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.csvfileparser.model.CsvParser
import com.example.csvfileparser.viewModel.MainViewModel
import com.example.csvfileparser.viewModel.MainViewModelFactory
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.getValue

/**
 * MainActivity is the View component, using Jetpack Compose.
 */
class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, MainViewModelFactory(CsvParser()))[MainViewModel::class.java]

        // Set the Compose content
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // Pass the context's assets and file name to the Composable
                    CsvParserScreen(
                        viewModel = viewModel,
                        onParseAssetClick = {
                            viewModel.parseCsvFromAssets(
                                assets,
                                "sample_device_report.csv"
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CsvParserScreen(viewModel: MainViewModel, onParseAssetClick: () -> Unit) {
    // Observe LiveData from the ViewModel
    val jsonOutput by viewModel.jsonOutput.observeAsState("Parsed JSON will appear here...")
    val errorMessage by viewModel.errorMessage.observeAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = onParseAssetClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Parse Sample CSV from Assets")
        }

        // Display error message if present
        errorMessage?.let { error ->
            Text(
                text = "Error: $error",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Display JSON output
        Text(
            text = jsonOutput,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Fills remaining space
                .background(Color(0xFFF0F0F0)) // Light gray background
                .padding(8.dp)
                .verticalScroll(rememberScrollState()) // Enable scrolling for long text
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val dummyViewModel = MainViewModel(CsvParser())
    CsvParserScreen(viewModel = dummyViewModel, onParseAssetClick = {})
}
