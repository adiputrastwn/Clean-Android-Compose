package com.adiputrastwn.cleanandroidcompose.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.adiputrastwn.cleanandroidcompose.ui.theme.CleanAndroidComposeTheme

/**
 * Comprehensive samples demonstrating Coil image loading in Jetpack Compose.
 *
 * This file showcases:
 * 1. Basic image loading with AsyncImage
 * 2. Loading states with SubcomposeAsyncImage
 * 3. Custom transformations (circle crop, rounded corners)
 * 4. Error handling
 * 5. Placeholder images
 * 6. Advanced configurations with ImageRequest
 */

// Sample image URLs for demonstration
private const val SAMPLE_IMAGE_URL = "https://picsum.photos/400/300"
private const val SAMPLE_PORTRAIT_URL = "https://picsum.photos/300/400"
private const val SAMPLE_AVATAR_URL = "https://i.pravatar.cc/300"

/**
 * Main screen showcasing various Coil image loading examples.
 */
@Composable
fun ImageLoadingSamplesScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Coil Image Loading Examples",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Example 1: Basic image loading
        SampleSection(
            title = "1. Basic Image Loading",
            description = "Simple AsyncImage with URL"
        ) {
            BasicImageSample()
        }

        // Example 2: Image with loading state
        SampleSection(
            title = "2. Image with Loading Indicator",
            description = "SubcomposeAsyncImage with CircularProgressIndicator"
        ) {
            ImageWithLoadingStateSample()
        }

        // Example 3: Circle avatar image
        SampleSection(
            title = "3. Circle Avatar",
            description = "Image with CircleShape clip"
        ) {
            CircleAvatarSample()
        }

        // Example 4: Rounded corners image
        SampleSection(
            title = "4. Rounded Corners",
            description = "Image with RoundedCornerShape"
        ) {
            RoundedCornersImageSample()
        }

        // Example 5: Advanced configuration
        SampleSection(
            title = "5. Advanced Configuration",
            description = "ImageRequest with crossfade and content scale"
        ) {
            AdvancedImageSample()
        }
    }
}

/**
 * Example 1: Basic image loading with AsyncImage
 */
@Composable
fun BasicImageSample() {
    AsyncImage(
        model = SAMPLE_IMAGE_URL,
        contentDescription = "Sample image demonstrating basic loading",
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentScale = ContentScale.Crop
    )
}

/**
 * Example 2: Image with loading state using SubcomposeAsyncImage
 */
@Composable
fun ImageWithLoadingStateSample() {
    SubcomposeAsyncImage(
        model = SAMPLE_PORTRAIT_URL,
        contentDescription = "Sample image with loading state",
        loading = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentScale = ContentScale.Crop
    )
}

/**
 * Example 3: Circle avatar image
 */
@Composable
fun CircleAvatarSample() {
    AsyncImage(
        model = SAMPLE_AVATAR_URL,
        contentDescription = "Circle avatar image",
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}

/**
 * Example 4: Image with rounded corners
 */
@Composable
fun RoundedCornersImageSample() {
    AsyncImage(
        model = SAMPLE_IMAGE_URL,
        contentDescription = "Image with rounded corners",
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp)),
        contentScale = ContentScale.Crop
    )
}

/**
 * Example 5: Advanced image configuration with ImageRequest
 */
@Composable
fun AdvancedImageSample() {
    val context = LocalContext.current

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(SAMPLE_IMAGE_URL)
            .crossfade(true)
            .build(),
        contentDescription = "Advanced configured image",
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp)),
        contentScale = ContentScale.Crop
    )
}

/**
 * Helper composable to wrap each sample with a title and description
 */
@Composable
fun SampleSection(
    title: String,
    description: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ImageLoadingSamplesScreenPreview() {
    CleanAndroidComposeTheme {
        ImageLoadingSamplesScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun BasicImageSamplePreview() {
    CleanAndroidComposeTheme {
        SampleSection(
            title = "Basic Image",
            description = "Simple image loading"
        ) {
            BasicImageSample()
        }
    }
}
