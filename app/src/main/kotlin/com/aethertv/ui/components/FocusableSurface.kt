package com.aethertv.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import androidx.tv.material3.Text

/**
 * A properly focusable Surface for TV that shows visual focus feedback.
 * Use this instead of raw Surface for all interactive elements.
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun FocusableSurface(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
    containerColor: Color = Color(0xFF2A2A2A),
    focusedColor: Color = Color(0xFF444444),
    shape: Shape = RoundedCornerShape(8.dp),
    showBorder: Boolean = true,
    borderColor: Color = Color.White,
    content: @Composable () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    
    val currentColor = if (isFocused) focusedColor else containerColor
    
    Surface(
        onClick = onClick,
        modifier = modifier
            .then(
                if (focusRequester != null) Modifier.focusRequester(focusRequester) 
                else Modifier
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable(),
        shape = ClickableSurfaceDefaults.shape(shape = shape),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = currentColor,
            focusedContainerColor = focusedColor
        ),
        border = if (isFocused && showBorder) {
            ClickableSurfaceDefaults.border(
                focusedBorder = Border(
                    border = BorderStroke(2.dp, borderColor)
                )
            )
        } else {
            ClickableSurfaceDefaults.border()
        }
    ) {
        content()
    }
}

/**
 * A styled button with proper focus handling for TV.
 */
@Composable
fun TvButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
    primary: Boolean = false,
    fontSize: TextUnit = 14.sp,
    horizontalPadding: Dp = 16.dp,
    verticalPadding: Dp = 10.dp
) {
    val baseColor = if (primary) Color(0xFF0077B6) else Color(0xFF2A2A2A)
    val focusedColor = if (primary) Color(0xFF00B4D8) else Color(0xFF444444)
    
    FocusableSurface(
        onClick = onClick,
        modifier = modifier,
        focusRequester = focusRequester,
        containerColor = baseColor,
        focusedColor = focusedColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = fontSize,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = horizontalPadding, vertical = verticalPadding)
        )
    }
}

/**
 * A small compact button for inline actions (like ON/OFF toggles).
 */
@Composable
fun TvCompactButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color(0xFF333333),
    focusedColor: Color = Color(0xFF555555),
    textColor: Color = Color.White
) {
    FocusableSurface(
        onClick = onClick,
        modifier = modifier,
        containerColor = containerColor,
        focusedColor = focusedColor,
        shape = RoundedCornerShape(4.dp),
        showBorder = true
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

/**
 * A chip/tag style selectable button.
 */
@Composable
fun TvChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    selectedColor: Color = Color(0xFF00B4D8),
    unselectedColor: Color = Color(0xFF2A2A2A),
    focusedUnselectedColor: Color = Color(0xFF444444)
) {
    val containerColor = if (isSelected) selectedColor else unselectedColor
    val focusedColor = if (isSelected) selectedColor else focusedUnselectedColor
    
    FocusableSurface(
        onClick = onClick,
        modifier = modifier,
        containerColor = containerColor,
        focusedColor = focusedColor,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
