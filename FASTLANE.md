# Fastlane Integration Guide for Android

This guide provides comprehensive instructions for integrating Fastlane into your modern Android application built with Kotlin and Gradle.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Project Initialization](#project-initialization)
3. [Core Configuration Files](#core-configuration-files)
4. [Google Play Console Integration](#google-play-console-integration)
5. [Firebase App Distribution Integration](#firebase-app-distribution-integration)
6. [Available Lanes](#available-lanes)
7. [App Signing Setup](#app-signing-setup)
8. [Best Practices](#best-practices)
9. [Troubleshooting](#troubleshooting)

---

## Prerequisites

### 1. Install Ruby

Fastlane requires Ruby 2.6 or newer. Check your Ruby version:

```bash
ruby --version
```

**macOS:**
Ruby comes pre-installed on macOS. However, for better version management, consider using `rbenv` or `rvm`:

```bash
# Using Homebrew
brew install rbenv ruby-build

# Add rbenv to bash so that it loads every time you open a terminal
echo 'if which rbenv > /dev/null; then eval "$(rbenv init -)"; fi' >> ~/.zshrc
source ~/.zshrc

# Install Ruby 3.2.0 (or latest stable version)
rbenv install 3.2.0
rbenv global 3.2.0
```

**Linux (Ubuntu/Debian):**

```bash
sudo apt-get update
sudo apt-get install ruby-full build-essential
```

### 2. Install Bundler

Bundler manages Ruby gem dependencies:

```bash
gem install bundler
```

### 3. Verify Installations

```bash
ruby --version   # Should show 2.6 or higher
bundler --version
```

---

## Project Initialization

### Step 1: Navigate to Your Project Directory

```bash
cd /path/to/CleanAndroidCompose
```

### Step 2: Install Dependencies

The `Gemfile` has already been created in your project root. Install the dependencies:

```bash
# Install dependencies from Gemfile
bundle install

# This creates Gemfile.lock which locks dependency versions
```

### Step 3: Initialize Fastlane (Optional)

If you want to regenerate or customize the Fastlane setup:

```bash
bundle exec fastlane init
```

Select option **4** (Manual setup) when prompted, as we've already created the configuration files.

### Step 4: Verify Installation

Check that Fastlane is properly installed:

```bash
bundle exec fastlane --version
```

List all available lanes:

```bash
bundle exec fastlane lanes
```

---

## Core Configuration Files

### 1. Gemfile

Location: `./Gemfile`

```ruby
source "https://rubygems.org"

gem "fastlane", "~> 2.219.0"
```

**Purpose:** Declares Ruby dependencies and locks Fastlane version for consistency across team members.

**Usage:**
- `bundle install` - Install dependencies
- `bundle update` - Update dependencies
- `bundle exec fastlane <lane>` - Run Fastlane commands through Bundler

### 2. Appfile

Location: `./fastlane/Appfile`

```ruby
# Package name of your Android application
package_name("com.adiputrastwn.cleanandroidcompose")

# Path to the JSON key file for Google Play Console authentication
json_key_file(ENV["GOOGLE_PLAY_JSON_KEY_PATH"]) if ENV["GOOGLE_PLAY_JSON_KEY_PATH"]
```

**Purpose:** Contains app-specific configuration used across all lanes.

**Key Parameters:**
- `package_name` - Your app's package identifier (must match `applicationId` in `app/build.gradle.kts`)
- `json_key_file` - Path to Google Play service account JSON key (loaded from environment variable)

### 3. Fastfile

Location: `./fastlane/Fastfile`

Contains all automation lanes (workflows). See [Available Lanes](#available-lanes) section for details.

### 4. Environment Variables

Location: `./fastlane/.env.default` (template) and `./fastlane/.env` (your actual values)

**Setup:**

```bash
# Copy the template
cp fastlane/.env.default fastlane/.env

# Edit and add your actual values
# IMPORTANT: Never commit .env to version control!
```

**Key Variables:**

```bash
# Google Play Console credentials
GOOGLE_PLAY_JSON_KEY_PATH="/path/to/google-play-service-account.json"

# App signing (if configured)
KEYSTORE_PATH="/path/to/release.jks"
KEYSTORE_PASSWORD="your_keystore_password"
KEY_ALIAS="your_key_alias"
KEY_PASSWORD="your_key_password"
```

---

## Google Play Console Integration

### Step 1: Create a Google Cloud Project

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project (or select existing one)
3. Note the project ID

### Step 2: Enable Google Play Android Developer API

1. In Google Cloud Console, navigate to **APIs & Services** → **Library**
2. Search for "Google Play Android Developer API"
3. Click **Enable**

### Step 3: Create a Service Account

1. Navigate to **IAM & Admin** → **Service Accounts**
2. Click **Create Service Account**
3. Fill in the details:
   - **Name:** `fastlane-deployer` (or any descriptive name)
   - **Description:** "Service account for Fastlane automated deployments"
4. Click **Create and Continue**
5. Skip the optional permissions step (click **Continue**)
6. Click **Done**

### Step 4: Create and Download JSON Key

1. Click on the newly created service account
2. Go to the **Keys** tab
3. Click **Add Key** → **Create new key**
4. Select **JSON** format
5. Click **Create**
6. Save the downloaded JSON file securely (e.g., `google-play-service-account.json`)

**IMPORTANT:** Store this file securely and NEVER commit it to version control!

### Step 5: Grant Access in Google Play Console

1. Go to [Google Play Console](https://play.google.com/console)
2. Select your app (or create a new app)
3. Navigate to **Setup** → **API access**
4. Link your Google Cloud Project if not already linked
5. Under **Service accounts**, find your newly created service account
6. Click **Grant access**
7. Configure permissions:
   - **App permissions:** Select your app
   - **Account permissions:** Grant these permissions:
     - View app information and download bulk reports (read only)
     - Create, edit, and delete draft apps
     - Release apps to testing tracks
     - Release apps to production
     - Manage testing tracks and edit tester lists
8. Click **Invite user** → **Send invite**

### Step 6: Configure Fastlane

Add the path to your JSON key file in `fastlane/.env`:

```bash
GOOGLE_PLAY_JSON_KEY_PATH="/Users/yourname/secrets/google-play-service-account.json"
```

### Step 7: Verify Setup

Test the connection:

```bash
bundle exec fastlane run validate_play_store_json_key json_key:/path/to/google-play-service-account.json
```

---

## Firebase App Distribution Integration

Firebase App Distribution is a great alternative to Google Play Console for distributing builds to testers. It's faster to set up and provides an excellent testing workflow.

### Step 1: Create a Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click **Add project** (or select an existing project)
3. Follow the setup wizard
4. Once created, select your project

### Step 2: Add Your Android App to Firebase

1. In Firebase Console, click the **Android icon** to add an Android app
2. Fill in the registration form:
   - **Package name:** `com.adiputrastwn.cleanandroidcompose`
   - **App nickname:** (optional) e.g., "Clean Android Compose"
   - **Debug signing certificate SHA-1:** (optional, for now)
3. Click **Register app**
4. Download `google-services.json` (you can skip this if not using other Firebase features)
5. Click **Continue** and **Finish**

### Step 3: Get Your Firebase App ID

1. In Firebase Console, go to **Project Settings** (gear icon)
2. Scroll down to **Your apps** section
3. Find your Android app and copy the **App ID**
   - Format: `1:123456789:android:abcdef123456`
4. Save this - you'll need it for the `.env` file

### Step 4: Enable App Distribution

1. In Firebase Console sidebar, navigate to **App Distribution**
2. Click **Get started** if prompted
3. Accept the terms

### Step 5: Create Tester Groups

1. In **App Distribution**, go to **Testers & Groups** tab
2. Click **Add group**
3. Create groups for different testing purposes:
   - `qa-team` - Your QA engineers
   - `developers` - Development team
   - `beta-testers` - External beta testers
4. Add testers by email to each group

### Step 6: Create a Service Account for Fastlane

1. In Firebase Console, go to **Project Settings** → **Service Accounts**
2. Click **Generate new private key**
3. Confirm by clicking **Generate key**
4. A JSON file will download - save it securely (e.g., `firebase-service-account.json`)

**IMPORTANT:** Store this file securely and NEVER commit it to version control!

### Step 7: Configure Fastlane

Add the following to `fastlane/.env`:

```bash
# Firebase App ID (from Step 3)
FIREBASE_APP_ID="1:123456789:android:abcdef123456"

# Path to Firebase service account JSON
FIREBASE_SERVICE_CREDENTIALS_FILE="/Users/yourname/secrets/firebase-service-account.json"

# Tester groups (comma-separated)
FIREBASE_GROUPS="qa-team,developers"

# Individual testers (optional, comma-separated emails)
FIREBASE_TESTERS="tester1@example.com,tester2@example.com"

# Default release notes
FIREBASE_RELEASE_NOTES="New build from Fastlane"
```

### Step 8: Install the Firebase Plugin

The plugin is already added to your `Gemfile`. Install it:

```bash
bundle install
```

### Step 9: Verify Setup

Test the Firebase distribution:

```bash
# Distribute a debug build to testers
bundle exec fastlane firebase_debug release_notes:"Testing Firebase integration"
```

### Firebase App Distribution vs Google Play Console

| Feature | Firebase App Distribution | Google Play Console |
|---------|--------------------------|---------------------|
| **Setup Speed** | Fast (minutes) | Slower (requires app creation, review) |
| **Distribution Speed** | Instant | Can take hours for processing |
| **Tester Limit** | Unlimited | Limited by track |
| **Release Notes** | Supports markdown | Plain text |
| **Best For** | Internal testing, QA, beta | Official releases, production |
| **Cost** | Free | Free (with Play Console account) |
| **Installation** | Direct APK/AAB install | Via Play Store app |

### Recommended Workflow

1. **Development/QA Testing:** Use Firebase App Distribution
   - Fast distribution
   - Easy tester management
   - Instant feedback loop

2. **Beta Testing:** Use Google Play Beta track
   - Larger audience
   - Production-like environment
   - Phased rollout capabilities

3. **Production:** Use Google Play Production track
   - Official release
   - Maximum reach
   - Store listing and discovery

---

## Available Lanes

All lanes are defined in `fastlane/Fastfile`. Run any lane with:

```bash
bundle exec fastlane <lane_name>
```

### Code Quality Lanes

#### `lint`
Runs Android lint checks on the debug variant.

```bash
bundle exec fastlane lint
```

**What it does:**
- Executes `./gradlew lintDebug`
- Generates lint report at `app/build/reports/lint-results-debug.html`

#### `detekt`
Runs Detekt static code analysis.

```bash
bundle exec fastlane detekt
```

**What it does:**
- Executes `./gradlew detektAll`
- Uses your existing Detekt configuration

#### `code_quality`
Runs both lint and detekt checks.

```bash
bundle exec fastlane code_quality
```

### Testing Lanes

#### `test`
Runs unit tests for the debug variant.

```bash
bundle exec fastlane test
```

**What it does:**
- Executes `./gradlew testDebugUnitTest`
- Runs all unit tests in `app/src/test/`

### Build Lanes

#### `build_aab`
Builds a release Android App Bundle (AAB).

```bash
# Basic build
bundle exec fastlane build_aab

# With version increment
bundle exec fastlane build_aab increment_version:true
```

**What it does:**
- Cleans the project
- Executes `./gradlew bundleRelease`
- Optionally increments version code
- Output: `app/build/outputs/bundle/release/app-release.aab`

#### `build_apk`
Builds a release APK.

```bash
# Basic build
bundle exec fastlane build_apk

# With version increment
bundle exec fastlane build_apk increment_version:true
```

**What it does:**
- Cleans the project
- Executes `./gradlew assembleRelease`
- Optionally increments version code
- Output: `app/build/outputs/apk/release/app-release.apk`

### Deployment Lanes

#### `deploy_internal`
Deploys AAB to Google Play Console's internal testing track.

```bash
# Basic deployment
bundle exec fastlane deploy_internal

# With version increment
bundle exec fastlane deploy_internal increment_version:true
```

**What it does:**
1. Builds a release AAB
2. Uploads to Internal Testing track
3. Sets release status to "completed"
4. Makes the app immediately available to internal testers

**Prerequisites:**
- Google Play Console service account configured
- App must have been manually published at least once
- `GOOGLE_PLAY_JSON_KEY_PATH` set in `fastlane/.env`

#### `deploy_alpha`
Deploys AAB to Alpha track.

```bash
bundle exec fastlane deploy_alpha
```

#### `deploy_beta`
Deploys AAB to Beta track.

```bash
bundle exec fastlane deploy_beta
```

#### `deploy_production`
Deploys AAB to Production track.

```bash
bundle exec fastlane deploy_production
```

**What it does:**
1. Runs all code quality checks
2. Runs all tests
3. Builds release AAB
4. Uploads to Production track

**⚠️ Warning:** Use with caution! This deploys to production.

#### `promote`
Promotes a release from one track to another.

```bash
# Promote from internal to beta
bundle exec fastlane promote from_track:internal to_track:beta

# Promote from beta to production
bundle exec fastlane promote from_track:beta to_track:production
```

### CI/CD Lanes

#### `ci`
Complete CI pipeline for continuous integration.

```bash
bundle exec fastlane ci
```

**What it does:**
1. Runs code quality checks (lint + detekt)
2. Runs all tests
3. Builds both APK and AAB

**Use case:** Perfect for CI/CD pipelines (GitHub Actions, Jenkins, etc.)

### Firebase App Distribution Lanes

#### `firebase_debug`
Distribute debug APK to Firebase App Distribution.

```bash
# Basic distribution
bundle exec fastlane firebase_debug

# With custom release notes
bundle exec fastlane firebase_debug release_notes:"Fixed login bug"
```

**What it does:**
1. Builds debug APK
2. Uploads to Firebase App Distribution
3. Notifies testers via email

**Prerequisites:**
- Firebase project configured
- `FIREBASE_APP_ID` and `FIREBASE_SERVICE_CREDENTIALS_FILE` set in `fastlane/.env`
- Tester groups created in Firebase Console

**Best for:** Quick QA testing with debug symbols

#### `firebase_release`
Distribute release APK to Firebase App Distribution.

```bash
# Basic distribution
bundle exec fastlane firebase_release

# With version increment and custom notes
bundle exec fastlane firebase_release increment_version:true release_notes:"Version 1.2.0 - New features"
```

**What it does:**
1. Builds release APK (with optional version increment)
2. Uploads to Firebase App Distribution
3. Notifies testers

**Best for:** Beta testing with release builds

#### `firebase_aab`
Distribute release AAB to Firebase App Distribution.

```bash
# Basic distribution
bundle exec fastlane firebase_aab

# With version increment
bundle exec fastlane firebase_aab increment_version:true
```

**What it does:**
1. Builds release AAB (with optional version increment)
2. Uploads to Firebase App Distribution
3. Notifies testers

**Note:** AAB requires Android 5.0+ and is the preferred format for Google Play

#### `firebase_qa`
Distribute to Firebase with full QA checks.

```bash
# Run all checks before distribution
bundle exec fastlane firebase_qa

# With custom options
bundle exec fastlane firebase_qa increment_version:true release_notes:"RC 1.0.0"
```

**What it does:**
1. Runs code quality checks (lint + detekt)
2. Runs all unit tests
3. Builds release APK
4. Distributes to Firebase

**Best for:** Pre-release QA validation before submitting to Play Store

---

## App Signing Setup

### Option 1: Using Gradle Properties (Recommended)

Create a `keystore.properties` file in your project root:

```properties
storeFile=/path/to/release.jks
storePassword=your_store_password
keyAlias=your_key_alias
keyPassword=your_key_password
```

Update `app/build.gradle.kts`:

```kotlin
// Load keystore properties
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    // ... other config

    signingConfigs {
        create("release") {
            storeFile = keystoreProperties["storeFile"]?.let { file(it) }
            storePassword = keystoreProperties["storePassword"] as String?
            keyAlias = keystoreProperties["keyAlias"] as String?
            keyPassword = keystoreProperties["keyPassword"] as String?
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

Add to `.gitignore`:

```
keystore.properties
```

### Option 2: Using Environment Variables

Set environment variables in `fastlane/.env`:

```bash
KEYSTORE_PATH="/path/to/release.jks"
KEYSTORE_PASSWORD="your_password"
KEY_ALIAS="your_alias"
KEY_PASSWORD="your_key_password"
```

Uncomment the signing properties in `fastlane/Fastfile`:

```ruby
properties: {
  "android.injected.signing.store.file" => ENV["KEYSTORE_PATH"],
  "android.injected.signing.store.password" => ENV["KEYSTORE_PASSWORD"],
  "android.injected.signing.key.alias" => ENV["KEY_ALIAS"],
  "android.injected.signing.key.password" => ENV["KEY_PASSWORD"],
}
```

### Creating a Release Keystore

If you don't have a keystore yet:

```bash
keytool -genkey -v -keystore release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-key-alias
```

Follow the prompts and **save the passwords securely!**

---

## Best Practices

### 1. Environment Management

**DO:**
- Use `fastlane/.env` for sensitive information
- Copy `.env.default` to `.env` for each environment (dev, staging, production)
- Create environment-specific files: `.env.production`, `.env.staging`

**DON'T:**
- Commit `.env` files to version control
- Hardcode credentials in Fastfile or Appfile
- Share credentials via email or chat

### 2. Version Control

**DO:**
- Commit `Gemfile`, `Gemfile.lock`, `Fastfile`, `Appfile`, `.env.default`
- Add comprehensive `.gitignore` entries for Fastlane

**DON'T:**
- Commit `.env` files
- Commit service account JSON keys
- Commit keystore files
- Commit Fastlane reports or screenshots

### 3. Security

**DO:**
- Store keystore files and service account keys in secure locations
- Use secret management tools (e.g., 1Password, AWS Secrets Manager) for teams
- Rotate service account keys periodically
- Use least-privilege access for service accounts

**DON'T:**
- Store credentials in code
- Share keystore files via unsecured channels
- Use the same keystore for debug and release builds

### 4. CI/CD Integration

**GitHub Actions Example:**

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
    - uses: actions/checkout@v3

    - name: Set up Ruby
      uses: ruby/setup-ruby@v1
      with:
        ruby-version: '3.2'
        bundler-cache: true

    - name: Set up JDK 11
      uses: actions/setup-java@v3
      with:
        java-version: '11'
        distribution: 'temurin'
        cache: gradle

    - name: Create service account JSON
      run: echo '${{ secrets.GOOGLE_PLAY_SERVICE_ACCOUNT_JSON }}' > service-account.json

    - name: Run CI lane
      env:
        GOOGLE_PLAY_JSON_KEY_PATH: ./service-account.json
      run: bundle exec fastlane ci

    - name: Deploy to Internal Testing
      if: github.ref == 'refs/heads/main'
      env:
        GOOGLE_PLAY_JSON_KEY_PATH: ./service-account.json
      run: bundle exec fastlane deploy_internal
```

### 5. Team Workflow

**Recommended workflow:**

1. **Development:**
   - Run `bundle exec fastlane lint` before committing
   - Run `bundle exec fastlane test` before creating PRs

2. **Code Review:**
   - CI pipeline runs `bundle exec fastlane ci` automatically

3. **Staging:**
   - Merge to `develop` branch triggers `deploy_internal`

4. **Production:**
   - Merge to `main` branch triggers `deploy_beta`
   - Manual promotion from beta to production after QA approval

### 6. Versioning Strategy

**Option A: Manual Versioning**
Update version in `app/build.gradle.kts` manually before deployment.

**Option B: Automatic Versioning**
Use the `increment_version:true` parameter:

```bash
bundle exec fastlane build_aab increment_version:true
```

**Option C: Advanced Versioning**
Install versioning plugin:

```ruby
# Add to Gemfile
gem "fastlane-plugin-versioning_android"
```

Use in Fastfile:

```ruby
android_set_version_name(version_name: "2.0.0")
android_set_version_code(version_code: 20)
```

### 7. Monitoring and Notifications

**Slack Notifications:**

Add to your lanes:

```ruby
slack(
  message: "✅ Successfully deployed to Internal Testing!",
  slack_url: ENV["SLACK_URL"],
  channel: "#mobile-builds",
  success: true
)
```

Set in `.env`:

```bash
SLACK_URL="https://hooks.slack.com/services/YOUR/WEBHOOK/URL"
```

---

## Troubleshooting

### Common Issues

#### 1. "Bundle command not found"

**Solution:**
```bash
gem install bundler
```

#### 2. "Google Play API not enabled"

**Solution:**
- Go to Google Cloud Console
- Enable "Google Play Android Developer API"

#### 3. "Service account doesn't have permission"

**Solution:**
- Check Google Play Console → API Access
- Ensure service account has correct permissions
- Wait 24 hours after granting permissions (API propagation)

#### 4. "Version code already exists"

**Solution:**
- Increment version code in `app/build.gradle.kts`
- Or use `increment_version:true` parameter

#### 5. "Keystore not found"

**Solution:**
- Verify `KEYSTORE_PATH` in `fastlane/.env`
- Ensure path is absolute, not relative
- Check file permissions

#### 6. "Ruby version too old"

**Solution:**
```bash
# Install rbenv and latest Ruby
brew install rbenv
rbenv install 3.2.0
rbenv global 3.2.0
```

#### 7. Fastlane crashes on macOS

**Solution:**
```bash
# Reinstall Ruby dependencies
bundle install --clean
bundle exec fastlane --version
```

### Getting Help

- **Fastlane Docs:** https://docs.fastlane.tools/
- **Fastlane Actions:** https://docs.fastlane.tools/actions/
- **Community:** https://github.com/fastlane/fastlane/discussions

### Debugging

Enable verbose output:

```bash
bundle exec fastlane <lane_name> --verbose
```

Check Fastlane logs:

```bash
cat fastlane/report.xml
```

---

## Quick Reference

### Essential Commands

```bash
# Install dependencies
bundle install

# List available lanes
bundle exec fastlane lanes

# Run lint
bundle exec fastlane lint

# Run tests
bundle exec fastlane test

# Build AAB
bundle exec fastlane build_aab

# Deploy to internal testing
bundle exec fastlane deploy_internal

# Full CI pipeline
bundle exec fastlane ci
```

### File Structure

```
CleanAndroidCompose/
├── Gemfile                          # Ruby dependencies
├── Gemfile.lock                     # Locked dependency versions
├── fastlane/
│   ├── Appfile                      # App configuration
│   ├── Fastfile                     # Lane definitions
│   ├── .env.default                 # Environment variable template
│   └── .env                         # Your actual credentials (gitignored)
├── app/
│   └── build.gradle.kts             # App build configuration
└── .gitignore                       # Git ignore rules
```

---

## Next Steps

1. **Set up Google Play Console integration** (see [Google Play Console Integration](#google-play-console-integration))
2. **Configure app signing** (see [App Signing Setup](#app-signing-setup))
3. **Run your first lane:** `bundle exec fastlane test`
4. **Integrate with CI/CD** (see [Best Practices](#best-practices))
5. **Customize lanes** to fit your workflow

For questions or issues, consult the [Troubleshooting](#troubleshooting) section or visit the [Fastlane documentation](https://docs.fastlane.tools/).

---

**Happy automating! 🚀**