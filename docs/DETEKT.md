# Detekt Static Analysis Setup

This document provides a complete guide for using Detekt static analysis in this project.

## Overview

Detekt is a static code analysis tool for Kotlin that helps maintain code quality by detecting code smells, complexity issues, and potential bugs.

**Configuration:**
- Version: 1.23.7
- Configuration File: `config/detekt/detekt.yml`
- Baseline File: `config/detekt/baseline.xml`
- Strictness: Lenient (essential rules only)

## Project Structure

```
CleanAndroidCompose/
├── config/
│   └── detekt/
│       ├── detekt.yml          # Main configuration file
│       └── baseline.xml        # Baseline for existing violations (generated)
├── build.gradle.kts            # Root build file with Detekt configuration
├── gradle/
│   └── libs.versions.toml      # Version catalog with Detekt plugin
└── app/
    └── build.gradle.kts        # Module build file (Detekt auto-applied)
```

## Gradle Commands

### Run Detekt Analysis

**Analyze all modules:**
```bash
./gradlew detektAll
```

**Analyze specific module:**
```bash
./gradlew :app:detekt
```

**Run with build (includes tests and Detekt):**
```bash
./gradlew check
```

### Generate Baseline

The baseline file captures existing violations so you can focus on new issues:

**Generate baseline for all modules:**
```bash
./gradlew detektBaseline
```

**Generate baseline for specific module:**
```bash
./gradlew :app:detektBaseline
```

**Important:** After generating the baseline, commit `config/detekt/baseline.xml` to version control.

### View Reports

After running Detekt, HTML reports are generated at:
```
build/reports/detekt/detekt.html
```

Open this file in your browser to see detailed findings with descriptions and code snippets.

## Configuration Details

### Enabled Rule Sets

The current configuration enables these essential rule sets:

1. **Complexity** - Detects overly complex code
   - CyclomaticComplexMethod (threshold: 20)
   - LongMethod (threshold: 100 lines)
   - LongParameterList (threshold: 8 parameters)
   - TooManyFunctions (threshold: 20 per file)
   - NestedBlockDepth (threshold: 5 levels)

2. **Coroutines** - Kotlin coroutines best practices
   - GlobalCoroutineUsage
   - InjectDispatcher
   - SleepInsteadOfDelay
   - SuspendFunWithFlowReturnType

3. **Empty Blocks** - Detects suspicious empty code blocks
   - EmptyCatchBlock (with allowed exceptions)
   - EmptyFunctionBlock
   - EmptyIfBlock, EmptyWhenBlock, etc.

4. **Exceptions** - Exception handling best practices
   - PrintStackTrace
   - SwallowedException
   - TooGenericExceptionCaught
   - ThrowingExceptionsWithoutMessageOrCause

5. **Naming** - Kotlin naming conventions
   - ClassNaming, FunctionNaming, VariableNaming
   - BooleanPropertyNaming (must start with is/has/are/should/can)
   - PackageNaming

6. **Performance** - Performance optimizations
   - ArrayPrimitive
   - ForEachOnRange
   - UnnecessaryTemporaryInstantiation

7. **Potential Bugs** - Common bug patterns
   - EqualsWithHashCodeExist
   - UnreachableCode
   - UnsafeCallOnNullableType
   - InvalidRange

8. **Style** - Code style guidelines
   - MaxLineLength (120 characters)
   - NewLineAtEndOfFile
   - ModifierOrder
   - ForbiddenVoid

### Customizing Configuration

To modify Detekt rules, edit `config/detekt/detekt.yml`:

**Change threshold values:**
```yaml
complexity:
  LongMethod:
    active: true
    threshold: 80  # Reduce from 100 to 80 lines
```

**Enable/disable rules:**
```yaml
style:
  MagicNumber:
    active: true  # Enable magic number detection
```

**Add custom patterns:**
```yaml
naming:
  FunctionNaming:
    active: true
    functionPattern: '[a-z][a-zA-Z0-9]*'
```

## CI/CD Integration

Detekt is automatically integrated with the standard Gradle `check` task.

### GitHub Actions Example

```yaml
name: Android CI

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Grant execute permission for gradlew
        run: chmod +x gradlew

      - name: Run checks (includes Detekt)
        run: ./gradlew check

      - name: Upload Detekt reports
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: detekt-reports
          path: '**/build/reports/detekt/'
```

### GitLab CI Example

```yaml
stages:
  - verify

detekt:
  stage: verify
  image: openjdk:17-jdk
  script:
    - ./gradlew detektAll
  artifacts:
    when: always
    paths:
      - '**/build/reports/detekt/'
    reports:
      junit: '**/build/reports/detekt/*.xml'
```

## Handling Findings

### When Detekt Fails the Build

If Detekt finds violations, the build will fail with output like:
```
Complexity - 2/10 - 20min debt
    LongMethod - [MainActivity.kt:45] - The function onCreate is too long (150 > 100)
    ComplexMethod - [ViewModel.kt:78] - The function calculate has a cyclomatic complexity of 25 (> 20)
```

### Resolution Strategies

**1. Fix the issue (recommended):**
```kotlin
// Before: Long method
fun processData() {
    // 150 lines of code...
}

// After: Refactored into smaller methods
fun processData() {
    validateInput()
    transformData()
    saveResults()
}

private fun validateInput() { /* ... */ }
private fun transformData() { /* ... */ }
private fun saveResults() { /* ... */ }
```

**2. Suppress specific findings (use sparingly):**
```kotlin
@Suppress("LongMethod")
fun legacyFunction() {
    // Acceptable if refactoring is not feasible
}
```

**3. Update baseline (for existing code):**
```bash
./gradlew detektBaseline
git add config/detekt/baseline.xml
git commit -m "Update Detekt baseline"
```

## Adding Detekt to New Modules

When creating a new module, Detekt is automatically applied via the root `build.gradle.kts` subprojects configuration. No additional setup is required.

If you need module-specific configuration:

```kotlin
// module/build.gradle.kts
detekt {
    config.setFrom(rootProject.files("config/detekt/detekt.yml"))
    baseline = file("$rootDir/config/detekt/baseline.xml")

    // Module-specific overrides
    source.setFrom(files("src/main/kotlin", "src/main/java"))
}
```

## Best Practices

1. **Run Detekt locally before committing:**
   ```bash
   ./gradlew detektAll
   ```

2. **Generate baseline only when necessary:**
   - When first integrating Detekt
   - After major refactoring
   - Not for hiding new violations

3. **Review findings before suppressing:**
   - Understand why the rule exists
   - Consider if the code can be improved
   - Document why suppression is needed

4. **Keep configuration in sync:**
   - Commit `detekt.yml` to version control
   - Review rule changes in team discussions
   - Document team-specific conventions

5. **Monitor technical debt:**
   - Track baseline size over time
   - Aim to reduce violations gradually
   - Make fixing violations part of regular work

## Troubleshooting

### Build fails with "Could not resolve dependencies"

**Solution:** Sync Gradle files and ensure internet connectivity:
```bash
./gradlew --refresh-dependencies detektAll
```

### "Config file not found" error

**Solution:** Ensure the config file exists at the expected location:
```bash
ls -la config/detekt/detekt.yml
```

### Baseline file not being used

**Solution:** Verify baseline path in `build.gradle.kts` and ensure file exists:
```bash
ls -la config/detekt/baseline.xml
```

### Too many false positives

**Solution:** Adjust rule thresholds in `detekt.yml` or disable specific rules:
```yaml
complexity:
  LongMethod:
    threshold: 150  # Increase threshold
```

## Resources

- [Detekt Official Documentation](https://detekt.dev/)
- [Detekt Rule Set Documentation](https://detekt.dev/docs/rules/complexity)
- [Detekt GitHub Repository](https://github.com/detekt/detekt)
- [Kotlin Style Guide](https://kotlinlang.org/docs/coding-conventions.html)

## Support

For issues or questions about this Detekt setup:
1. Check this documentation
2. Review Detekt's official documentation
3. Consult with the team lead or senior developers
4. Open an issue in the project repository
