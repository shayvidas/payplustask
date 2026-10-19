# Code Standards & Best Practices

## 📝 Documentation Standards

### **KDoc Comments**
All public classes, functions, and properties must have KDoc comments.

```kotlin
/**
 * Repository interface defining data operations for billing entries.
 * 
 * This abstraction allows the presentation layer to be independent of the data source,
 * following the Repository pattern and enabling easier testing.
 */
interface BillingRepository {
    
    /**
     * Fetches all billing entry headers.
     * 
     * @return Result containing list of billing headers or error
     */
    suspend fun getBillingHeaders(): Result<List<BillingEntryHeader>>
}
```

### **Inline Comments**
Use inline comments for complex logic:

```kotlin
// Upload button - only visible for MasterCard transactions
if (entry.cardType.equals(Constants.CARD_MASTERCARD, ignoreCase = true)) {
    // Button implementation
}
```

## 🎨 Naming Conventions

### **Classes**
- **PascalCase**: `BillingListViewModel`
- **Descriptive**: Name reflects purpose
- **Suffixes**: ViewModel, Repository, Service, State

### **Functions**
- **camelCase**: `loadBillingEntries()`
- **Verb-based**: Action name
- **Descriptive**: Clear intent

### **Variables**
- **camelCase**: `billingId`, `apiService`
- **Meaningful**: No abbreviations unless common
- **Constants**: `UPPER_SNAKE_CASE`

### **Composables**
- **PascalCase**: `BillingListScreen()`
- **Noun-based**: What they represent
- **Descriptive**: Clear component purpose

## 🏗️ Architecture Rules

### **MVVM Separation**

#### **View (Composable)**
- ✅ Renders UI based on state
- ✅ Emits user events to ViewModel
- ❌ No business logic
- ❌ No direct repository access

#### **ViewModel**
- ✅ Manages UI state
- ✅ Handles business logic
- ✅ Communicates with repository
- ❌ No Android framework dependencies (except AndroidViewModel when needed)
- ❌ No direct UI references

#### **Repository**
- ✅ Data operations
- ✅ Error handling
- ✅ Data source abstraction
- ❌ No UI logic
- ❌ No business rules

### **State Management**

#### **Use StateFlow**
```kotlin
private val _state = MutableStateFlow<BillingListState>(Loading)
val state: StateFlow<BillingListState> = _state.asStateFlow()
```

#### **Immutable State**
```kotlin
sealed class BillingListState {
    object Loading : BillingListState()
    data class Success(val data: List<BillingEntry>) : BillingListState()
}
```

#### **Single Source of Truth**
- ViewModel holds state
- UI observes and reacts
- Never duplicate state

## 🧪 Testing Standards

### **Unit Tests**
- Test all ViewModels
- Test repository implementations
- Mock dependencies
- Cover edge cases

### **Test Naming**
```kotlin
@Test
fun `loadBillingEntries updates state to Success when API call succeeds`()
```

## 🔒 Security Practices

### **Network Security**
- Use HTTPS in production
- Cleartext only for local development
- Define network security config

### **Data Handling**
- Don't log sensitive data
- Mask card numbers
- Validate all inputs

### **API Keys**
- Never hardcode API keys
- Use BuildConfig or secrets
- Add to .gitignore

## 🎯 Kotlin Best Practices

### **Use Kotlin Features**

#### **Data Classes**
```kotlin
data class BillingEntry(
    val id: Long,
    val price: Double
)
```

#### **Sealed Classes for State**
```kotlin
sealed class BillingState {
    object Loading : BillingState()
    data class Success(val data: List<T>) : BillingState()
    data class Error(val message: String) : BillingState()
}
```

#### **Extension Functions**
```kotlin
fun String.formatCurrency(): String {
    return "$${this}"
}
```

#### **Scope Functions**
```kotlin
billingEntry?.let { entry ->
    displayDetails(entry)
}
```

### **Null Safety**
- Avoid `!!` operator
- Use `?.` safe call
- Provide default values with `?:`
- Use `let`, `run`, `apply` appropriately

### **Coroutines**
```kotlin
viewModelScope.launch {
    // Async work
}
```

## 🎨 Compose Best Practices

### **Composable Functions**

#### **Naming**
- PascalCase
- Noun-based
- Descriptive

#### **Parameters**
```kotlin
@Composable
fun BillingListItem(
    entry: BillingEntryHeader,  // Required data
    onClick: () -> Unit,        // Actions
    modifier: Modifier = Modifier  // Modifier last with default
)
```

#### **State Hoisting**
```kotlin
// ❌ Bad - state in composable
@Composable
fun Counter() {
    var count by remember { mutableStateOf(0) }
    Button(onClick = { count++ }) {
        Text("Count: $count")
    }
}

// ✅ Good - hoisted state
@Composable
fun Counter(
    count: Int,
    onIncrement: () -> Unit
) {
    Button(onClick = onIncrement) {
        Text("Count: $count")
    }
}
```

#### **Side Effects**
```kotlin
// Use LaunchedEffect for one-time effects
LaunchedEffect(key1 = billingId) {
    viewModel.loadDetails(billingId)
}

// Use DisposableEffect for cleanup
DisposableEffect(Unit) {
    onDispose {
        // Cleanup
    }
}
```

### **Performance**

#### **Stable Parameters**
```kotlin
@Composable
fun BillingList(
    entries: List<BillingEntry>,  // Stable
    onItemClick: (Long) -> Unit   // Stable
)
```

#### **Keys in Lists**
```kotlin
LazyColumn {
    items(
        items = entries,
        key = { it.id }  // Important for performance
    ) { entry ->
        BillingListItem(entry)
    }
}
```

#### **Remember Expensive Calculations**
```kotlin
val formattedDate = remember(timestamp) {
    DateFormatter.formatTimestamp(timestamp)
}
```

## 📦 Dependency Injection

### **Hilt Annotations**

#### **Application**
```kotlin
@HiltAndroidApp
class BillingApplication : Application()
```

#### **Activity**
```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity()
```

#### **ViewModel**
```kotlin
@HiltViewModel
class BillingListViewModel @Inject constructor(
    private val repository: BillingRepository
) : ViewModel()
```

#### **Modules**
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        // Implementation
    }
}
```

## 🔍 Code Review Checklist

### **Before Committing**
- ✅ All code documented
- ✅ No hardcoded strings (use strings.xml)
- ✅ No magic numbers (use constants)
- ✅ Proper error handling
- ✅ Tests written and passing
- ✅ No compiler warnings
- ✅ Code formatted (Ctrl+Alt+L)
- ✅ No unused imports

### **Architecture**
- ✅ MVVM separation maintained
- ✅ Proper layer communication
- ✅ Single responsibility principle
- ✅ Dependencies injected
- ✅ No circular dependencies

### **UI**
- ✅ Composables are reusable
- ✅ State properly hoisted
- ✅ Modifiers used correctly
- ✅ Accessibility considered
- ✅ Responsive to different screen sizes

### **Performance**
- ✅ No unnecessary recompositions
- ✅ Keys used in lists
- ✅ Expensive operations remembered
- ✅ Images optimized
- ✅ No memory leaks

## 📚 Resources

- [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- [Android Kotlin Style Guide](https://developer.android.com/kotlin/style-guide)
- [Jetpack Compose Guidelines](https://developer.android.com/jetpack/compose/mental-model)
- [Effective Kotlin](https://kt.academy/book/effectivekotlin)

---

**Remember:** Clean code is not just about making it work, but making it maintainable, testable, and understandable for future developers (including yourself!).
