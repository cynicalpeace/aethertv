package com.aethertv.ui.guide

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.aethertv.domain.model.EpgProgram
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val CHANNEL_WIDTH = 160.dp
private val ROW_HEIGHT = 72.dp
private val HOUR_WIDTH = 240.dp // Width for 1 hour of content
private val TIME_HEADER_HEIGHT = 40.dp

private val accentColor = Color(0xFF00B4D8)
private val focusColor = Color(0xFF00B4D8)
private val liveColor = Color(0xFFE63946)
private val cardBg = Color(0xFF1A1A1A)
private val cardBgFocused = Color(0xFF2A3A4A)

@Composable
fun GuideScreen(
    onNavigateToPlayer: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: GuideViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }

    // Refresh current time every minute
    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000)
            viewModel.refreshCurrentTime()
        }
    }

    LaunchedEffect(Unit) {
        delay(300)
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.key) {
                        Key.DirectionUp -> {
                            viewModel.moveFocus(-1, 0)
                            true
                        }
                        Key.DirectionDown -> {
                            viewModel.moveFocus(1, 0)
                            true
                        }
                        Key.DirectionLeft -> {
                            viewModel.moveFocus(0, -1)
                            true
                        }
                        Key.DirectionRight -> {
                            viewModel.moveFocus(0, 1)
                            true
                        }
                        Key.Back, Key.Escape -> {
                            onBack()
                            true
                        }
                        else -> false
                    }
                } else false
            }
            .focusRequester(focusRequester)
            .focusable()
    ) {
        if (uiState.isLoading) {
            LoadingState()
        } else if (!uiState.hasEpgData) {
            NoEpgState()
        } else {
            GuideContent(
                uiState = uiState,
                onProgramClick = { channel, program ->
                    onNavigateToPlayer(channel.channel.infohash)
                },
                onProgramFocus = { program ->
                    viewModel.selectProgram(program)
                },
            )
        }

        // Selected program details panel
        uiState.selectedProgram?.let { program ->
            ProgramDetailPanel(
                program = program,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "📺",
                fontSize = 48.sp,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading TV Guide...",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
            )
        }
    }
}

@Composable
private fun NoEpgState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(48.dp),
        ) {
            Text(
                text = "📋",
                fontSize = 64.sp,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "No EPG Data",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Add an XMLTV source in Settings to see the TV Guide",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
            )
        }
    }
}

@Composable
private fun GuideContent(
    uiState: GuideUiState,
    onProgramClick: (GuideChannel, EpgProgram) -> Unit,
    onProgramFocus: (EpgProgram) -> Unit,
) {
    val channelListState = rememberLazyListState()
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    // Generate time slots (30-min increments)
    val timeSlots = remember(uiState.timelineStart, uiState.timelineEnd) {
        generateTimeSlots(uiState.timelineStart, uiState.timelineEnd)
    }

    // Calculate current time position
    val currentTimeOffset = remember(uiState.currentTime, uiState.timelineStart) {
        val elapsed = uiState.currentTime - uiState.timelineStart
        val totalDuration = uiState.timelineEnd - uiState.timelineStart
        (elapsed.toFloat() / totalDuration) * (HOUR_WIDTH.value * 4) // 4 hours shown
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Time header row
        Row(modifier = Modifier.fillMaxWidth()) {
            // Channel column header
            Box(
                modifier = Modifier
                    .width(CHANNEL_WIDTH)
                    .height(TIME_HEADER_HEIGHT)
                    .background(Color(0xFF1A1A1A))
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text = "Channels",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray,
                )
            }

            // Time slots
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(TIME_HEADER_HEIGHT)
                    .background(Color(0xFF1A1A1A)),
            ) {
                LazyRow(
                    contentPadding = PaddingValues(end = 48.dp),
                ) {
                    items(timeSlots.size) { index ->
                        Box(
                            modifier = Modifier
                                .width(HOUR_WIDTH / 2) // 30-min slots
                                .height(TIME_HEADER_HEIGHT),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            Text(
                                text = timeFormatter.format(Date(timeSlots[index])),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                modifier = Modifier.padding(start = 8.dp),
                            )
                        }
                    }
                }

                // Current time indicator (red line in header)
                Box(
                    modifier = Modifier
                        .offset(x = (CHANNEL_WIDTH.value + currentTimeOffset).dp - CHANNEL_WIDTH)
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(liveColor),
                )
            }
        }

        // Channel rows with programs
        Row(modifier = Modifier.weight(1f)) {
            // Channel list (left column)
            LazyColumn(
                state = channelListState,
                modifier = Modifier.width(CHANNEL_WIDTH),
            ) {
                itemsIndexed(uiState.channels) { index, guideChannel ->
                    ChannelRow(
                        channel = guideChannel,
                        isFocused = index == uiState.focusedChannelIndex,
                    )
                }
            }

            // Programs grid (right scrollable area)
            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    state = channelListState,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    itemsIndexed(uiState.channels) { channelIndex, guideChannel ->
                        ProgramRow(
                            channel = guideChannel,
                            timelineStart = uiState.timelineStart,
                            timelineEnd = uiState.timelineEnd,
                            isChannelFocused = channelIndex == uiState.focusedChannelIndex,
                            focusedProgramIndex = if (channelIndex == uiState.focusedChannelIndex) {
                                uiState.focusedProgramIndex
                            } else -1,
                            currentTime = uiState.currentTime,
                            onProgramClick = { program -> onProgramClick(guideChannel, program) },
                            onProgramFocus = onProgramFocus,
                        )
                    }
                }

                // Current time vertical line
                Box(
                    modifier = Modifier
                        .offset(x = currentTimeOffset.dp)
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(liveColor),
                )
            }
        }
    }
}

@Composable
private fun ChannelRow(
    channel: GuideChannel,
    isFocused: Boolean,
) {
    Row(
        modifier = Modifier
            .width(CHANNEL_WIDTH)
            .height(ROW_HEIGHT)
            .background(if (isFocused) Color(0xFF252525) else Color(0xFF151515))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        channel.channel.iconUrl?.let { url ->
            AsyncImage(
                model = url,
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Fit,
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Text(
            text = channel.channel.name,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isFocused) Color.White else Color.LightGray,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ProgramRow(
    channel: GuideChannel,
    timelineStart: Long,
    timelineEnd: Long,
    isChannelFocused: Boolean,
    focusedProgramIndex: Int,
    currentTime: Long,
    onProgramClick: (EpgProgram) -> Unit,
    onProgramFocus: (EpgProgram) -> Unit,
) {
    val totalDuration = timelineEnd - timelineStart
    val totalWidth = HOUR_WIDTH.value * 4 // 4 hours

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(ROW_HEIGHT)
            .background(if (isChannelFocused) Color(0xFF1A1A1A) else Color(0xFF121212)),
        contentPadding = PaddingValues(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        if (channel.programs.isEmpty()) {
            item {
                // No program info
                Box(
                    modifier = Modifier
                        .width(totalWidth.dp)
                        .fillMaxHeight()
                        .background(cardBg, RoundedCornerShape(4.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        text = "No program info",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                    )
                }
            }
        } else {
            itemsIndexed(channel.programs) { index, program ->
                val programStart = maxOf(program.startTime, timelineStart)
                val programEnd = minOf(program.endTime, timelineEnd)
                val duration = programEnd - programStart
                val widthFraction = duration.toFloat() / totalDuration
                val programWidth = (totalWidth * widthFraction).dp

                val isLive = currentTime in program.startTime until program.endTime
                val isFocused = isChannelFocused && index == focusedProgramIndex

                ProgramCard(
                    program = program,
                    width = programWidth,
                    isLive = isLive,
                    isFocused = isFocused,
                    currentTime = currentTime,
                    onClick = { onProgramClick(program) },
                    onFocus = { onProgramFocus(program) },
                )
            }
        }
    }
}

@Composable
private fun ProgramCard(
    program: EpgProgram,
    width: androidx.compose.ui.unit.Dp,
    isLive: Boolean,
    isFocused: Boolean,
    currentTime: Long,
    onClick: () -> Unit,
    onFocus: () -> Unit,
) {
    var focused by remember { mutableStateOf(false) }

    LaunchedEffect(focused) {
        if (focused) onFocus()
    }

    val bgColor = when {
        isFocused -> cardBgFocused
        isLive -> Color(0xFF2A2020)
        else -> cardBg
    }

    val borderColor = when {
        isFocused -> focusColor
        isLive -> liveColor
        else -> Color.Transparent
    }

    // Progress for live programs
    val progress = if (isLive) {
        val elapsed = currentTime - program.startTime
        val duration = program.endTime - program.startTime
        (elapsed.toFloat() / duration).coerceIn(0f, 1f)
    } else 0f

    Box(
        modifier = Modifier
            .width(width.coerceAtLeast(80.dp))
            .fillMaxHeight()
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .border(
                width = if (isFocused || isLive) 2.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(4.dp),
            )
            .onFocusChanged { focused = it.isFocused }
            .focusable()
            .padding(8.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = program.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = if (isLive) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )

                if (isLive) {
                    Box(
                        modifier = Modifier
                            .background(liveColor, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = "LIVE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                    }
                }
            }

            // Progress bar for live
            if (isLive) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(Color(0xFF333333), RoundedCornerShape(2.dp)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(liveColor, accentColor),
                                ),
                                RoundedCornerShape(2.dp),
                            ),
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgramDetailPanel(
    program: EpgProgram,
    modifier: Modifier = Modifier,
) {
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color(0xEE000000)),
                ),
            )
            .padding(top = 24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp, vertical = 16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = program.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )

                program.category?.let { cat ->
                    Box(
                        modifier = Modifier
                            .background(accentColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = cat,
                            style = MaterialTheme.typography.labelMedium,
                            color = accentColor,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${timeFormatter.format(Date(program.startTime))} - ${timeFormatter.format(Date(program.endTime))}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
            )

            program.description?.let { desc ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private fun generateTimeSlots(start: Long, end: Long): List<Long> {
    val slots = mutableListOf<Long>()
    var current = start - (start % (30 * 60 * 1000)) // Round to 30-min
    while (current < end) {
        slots.add(current)
        current += 30 * 60 * 1000 // 30 minutes
    }
    return slots
}
