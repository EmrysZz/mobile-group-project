package com.example.fr.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fr.data.api.DeviceIdManager
import com.example.fr.data.models.Report
import com.example.fr.ui.viewmodel.ReportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsListScreen(
    navController: NavController,
    viewModel: ReportViewModel = viewModel()
) {
    val context = LocalContext.current
    val deviceIdManager = remember { DeviceIdManager(context) }
    val uiState by viewModel.uiState.collectAsState()

    // Initialize device ID
    LaunchedEffect(Unit) {
        viewModel.setDeviceId(deviceIdManager.getDeviceId())
    }

    // Show error as snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Reports") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("report") }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Report")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.reports.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.ReportOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No reports yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Be the first to report an incident",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.reports) { report ->
                            ReportItemCard(
                                report = report,
                                onUpvote = { viewModel.verifyReport(report.id, true) },
                                onDownvote = { viewModel.verifyReport(report.id, false) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportItemCard(
    report: Report,
    onUpvote: () -> Unit,
    onDownvote: () -> Unit
) {
    val typeColor = when (report.incident_type.lowercase()) {
        "flood" -> Color(0xFF2196F3)
        "road closure" -> Color(0xFFFF9800)
        "rescue needed" -> Color(0xFFF44336)
        "power outage" -> Color(0xFF9C27B0)
        else -> MaterialTheme.colorScheme.primary
    }

    val typeIcon = when (report.incident_type.lowercase()) {
        "flood" -> Icons.Default.Water
        "road closure" -> Icons.Default.Block
        "rescue needed" -> Icons.Default.Sos
        "power outage" -> Icons.Default.PowerOff
        else -> Icons.Default.Warning
    }

    // Use backend vote status
    val hasVoted = report.has_voted
    val userVote = report.user_vote

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        typeIcon,
                        contentDescription = null,
                        tint = typeColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = report.incident_type,
                        style = MaterialTheme.typography.titleMedium,
                        color = typeColor
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = report.verification_count.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (report.verification_count >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = report.description,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = report.user_name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )

                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${String.format("%.2f", report.latitude)}, ${String.format("%.2f", report.longitude)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            report.report_time?.let { time ->
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatReportTime(time),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Vote section
            if (hasVoted) {
                // Show user's vote
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = when (userVote) {
                            "upvote" -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                            "downvote" -> Color(0xFFF44336).copy(alpha = 0.1f)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (userVote) {
                                "upvote" -> Icons.Default.ThumbUp
                                "downvote" -> Icons.Default.ThumbDown
                                else -> Icons.Default.Check
                            },
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = when (userVote) {
                                "upvote" -> Color(0xFF4CAF50)
                                "downvote" -> Color(0xFFF44336)
                                else -> MaterialTheme.colorScheme.outline
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (userVote) {
                                "upvote" -> "You verified this report"
                                "downvote" -> "You marked this as not accurate"
                                else -> "You already voted"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = when (userVote) {
                                "upvote" -> Color(0xFF4CAF50)
                                "downvote" -> Color(0xFFF44336)
                                else -> MaterialTheme.colorScheme.outline
                            }
                        )
                    }
                }
            } else {
                // Show vote buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDownvote,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFF44336)
                        )
                    ) {
                        Icon(
                            Icons.Default.ThumbDown,
                            contentDescription = "Not Accurate",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Not Accurate")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onUpvote,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {
                        Icon(
                            Icons.Default.ThumbUp,
                            contentDescription = "Verify",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Verify")
                    }
                }
            }
        }
    }
}

private fun formatReportTime(dateString: String): String {
    return try {
        val date = dateString.substringBefore("T").replace("-", "/")
        val time = dateString.substringAfter("T").substringBefore(".").take(5)
        "$date $time"
    } catch (e: Exception) {
        dateString
    }
}
