**Design Document Android CSV File Parser (MVVM):**


**1. Purpose**
This document describes the design of an Android application developed in Kotlin that parses a custom CSV-like file format and converts its contents into a JSON output. The application is built following the Model-View-ViewModel (MVVM) architectural pattern to ensure separation of concerns, testability, and maintainability.

**2. Architecture**
The application strictly adheres to the Model-View-ViewModel (MVVM) architectural pattern, which promotes a clear separation between the UI (View), the presentation logic (ViewModel), and the business logic/data (Model).
•	Model: Represents the data and business logic. It includes the data structures for the CSV report and the core parsing logic.
•	View: The UI layer responsible for displaying data and capturing user input. It observes changes in the ViewModel.
•	ViewModel: Acts as a bridge between the View and the Model. It prepares data for the View, handles UI-related logic, and communicates with the Model. It does not hold direct references to the View.

**3. Components**
The application consists of the following key components:

**3.1. Model Layer**
•	HeaderData (data class): Represents a single header record from the CSV file.
•	RecordData (data class): Represents a single data record (device detail) from the CSV file.
•	TrailerData (data class): Represents the trailer record, typically containing a total count.
•	DeviceReport (data class): An aggregation of HeaderData, a list of RecordData, and TrailerData, representing the complete parsed report.
•	CsvParser (class): Contains the core logic for reading an InputStream, parsing each line based on its type ('H', 'R', 'T'), validating the format, and constructing a DeviceReport object. It throws IllegalArgumentException for parsing errors or invalid file structure.

**3.2. ViewModel Layer**
•	MainViewModel (class, extends androidx.lifecycle.ViewModel):
o	Holds MutableLiveData<String> instances (jsonOutput and errorMessage) to expose parsed data and error messages to the UI.
o	Contains the parseCsvFromAssets function, which orchestrates the parsing process for an asset file:
o	Takes AssetManager and the asset file name to open the file.
o	Instantiates and uses CsvParser to get the DeviceReport.
o	Uses kotlinx.serialization.json.Json to convert the DeviceReport object into a pretty-printed JSON string.
o	Updates the LiveData objects with the result or any exceptions caught.
o	It takes CsvParser as a dependency in its constructor, promoting testability.

**3.3. View Layer**
•	MainActivity (class, extends androidx.activity.ComponentActivity):
o	The primary UI component, using Jetpack Compose for building the user interface.
o	Initializes the MainViewModel.
o	Uses the setContent block to define the composable UI hierarchy.
o	Passes the AssetManager to the Composable function for accessing asset files.
•	CsvParserScreen (Composable function):
o	A @Composable function that represents the entire UI screen.
o	Takes MainViewModel and a callback onParseAssetClick for the button click.
o	Observes the jsonOutput and errorMessage LiveData from MainViewModel using observeAsState().
o	Renders the UI elements (Button, Text) based on the observed state. The button now triggers the parsing of the sample asset file.

**3.4. Utility**
•	MainViewModelFactory (class, implements ViewModelProvider.Factory):
o	A custom factory required to correctly instantiate MainViewModel because MainViewModel has a constructor that requires a CsvParser instance. This allows MainViewModel to be created by the Android framework while still allowing for dependency injection.


**4. Sequence Diagram**
The following sequence diagram illustrates the flow of control and data when a user interacts with the application to parse the sample CSV file from assets.

@startuml

actor User

participant MainActivity as View

participant MainViewModel as ViewModel

participant CsvParser as Model

participant AssetManager as Assets

participant InputStream as File

User -> View: Taps "Parse Sample CSV from Assets" Button

View -> ViewModel: Calls viewModel.parseCsvFromAssets(assets, "sample_device_report.csv")

ViewModel -> Assets: assets.open("sample_device_report.csv")

Assets -> ViewModel: Returns InputStream (File)

ViewModel -> Model: Calls csvParser.parse(inputStream)

Model -> File: Reads line by line

Model -> Model: Parses Header

Model -> Model: Parses Records (iterative)

Model -> Model: Parses Trailer

Model -> ViewModel: Returns DeviceReport object

ViewModel -> ViewModel: Closes InputStream

ViewModel -> ViewModel: Converts DeviceReport to JSON string (using kotlinx.serialization)

ViewModel -> View: Updates jsonOutput LiveData (postValue)

View -> View: Observes jsonOutput LiveData

View -> View: Updates UI (Composable recomposes)

@enduml






**Demo:**

https://github.com/user-attachments/assets/4f02951a-0f72-47c3-97f0-e293b8a34f50


